import re
import json
import numpy as np
import pandas as pd
from typing import Optional, Tuple
from statsmodels.tsa.holtwinters import ExponentialSmoothing, Holt

try:
    from prophet import Prophet  # pip install prophet cmdstanpy
    _HAS_PROPHET = True
except Exception:
    _HAS_PROPHET = False

_KEEP = re.compile(r"[^А-Яа-яЁё0-9 ,.!?:;()«»\"'–—\-•\n]")

def clean_ru(text: str) -> str:
    text = _KEEP.sub(" ", text or "")
    return re.sub(r"\s+", " ", text).strip()

def normalize_columns(df: pd.DataFrame) -> pd.DataFrame:
    work = df.copy()
    for col in list(work.columns):
        lc = col.lower()
        if lc in ("date", "дата"):
            work.rename(columns={col: "date"}, inplace=True)
        elif lc in ("amount", "сумма"):
            work.rename(columns={col: "amount"}, inplace=True)
        elif lc in ("category", "категория"):
            work.rename(columns={col: "category"}, inplace=True)
        elif lc in ("type", "тип"):
            work.rename(columns={col: "type"}, inplace=True)
    required = {"date", "amount", "type"}
    missing = required - set(map(str, work.columns))
    if missing:
        raise ValueError(f"Отсутствуют колонки: {', '.join(sorted(missing))}")
    work["date"] = pd.to_datetime(work["date"], errors="coerce")
    work = work.dropna(subset=["date"])
    work["amount"] = pd.to_numeric(work["amount"], errors="coerce").fillna(0.0)
    if "category" not in work.columns:
        work["category"] = "Без категории"
    return work

def is_expense(t: str) -> bool:
    t = str(t).strip().lower()
    return t in {"expense", "расход", "расходы", "-", "e", "exp"}

def is_income(t: str) -> bool:
    t = str(t).strip().lower()
    return t in {"income", "доход", "+", "i", "inc"}

def prepare_components_series(df: pd.DataFrame, freq: str="M") -> Tuple[pd.Series, pd.Series, pd.Series]:
    if df is None or df.empty:
        raise ValueError("Пустая таблица транзакций.")
    work = normalize_columns(df)
    work["is_expense"] = work["type"].apply(is_expense)
    work["is_income"] = work["type"].apply(is_income)

    inc = work.loc[work["is_income"]].set_index("date")["amount"].resample(freq).sum().sort_index()
    exp = work.loc[work["is_expense"]].set_index("date")["amount"].abs().mul(-1).resample(freq).sum().sort_index()

    if not inc.empty or not exp.empty:
        start = min([x.index.min() for x in [inc, exp] if not x.empty])
        end   = max([x.index.max() for x in [inc, exp] if not x.empty])
        full_idx = pd.date_range(start, end, freq=freq)
        inc = inc.reindex(full_idx, fill_value=0.0)
        exp = exp.reindex(full_idx, fill_value=0.0)
    net = inc + exp
    inc.index.name = exp.index.name = net.index.name = "period_end"
    return inc, exp, net

def fit_and_forecast(history: pd.Series, steps: int, freq: str, method: str = "auto") -> pd.Series:
    """
    method — если помесячно и точек >= 18, пробуем Prophet; иначе Holt/Holt-Winters, 
                  для годовой частоты Prophet при точках >= 5 
                  (надо точнее посчитать точек сколько)
    """
    if len(history) < 3:
        last = float(history.iloc[-1]) if len(history) else 0.0
        start = (history.index[-1] if len(history) else pd.Timestamp.today().normalize()) + \
                pd.tseries.frequencies.to_offset(freq)
        idx = pd.date_range(start, periods=steps, freq=freq)
        return pd.Series([last] * steps, index=idx, name="forecast")

    use_prophet = False
    if method == "prophet":
        use_prophet = True
    elif method == "auto":
        if freq.startswith("A"):                 
            use_prophet = _HAS_PROPHET and (len(history) >= 5)
        else:                                       
            use_prophet = _HAS_PROPHET and (len(history) >= 18)

    if use_prophet:
        try:
            pfreq = "Y" if freq.startswith("A") else "M"
            dfp = history.reset_index()
            dfp.columns = ["ds", "y"]

            m = Prophet(
                yearly_seasonality=(pfreq == "M"),
                weekly_seasonality=False,
                daily_seasonality=False,
                seasonality_mode="additive",
            )
            m.fit(dfp)
            future = m.make_future_dataframe(periods=steps, freq=pfreq)
            fcst = m.predict(future).tail(steps)
            yhat = pd.Series(fcst["yhat"].values, index=pd.DatetimeIndex(fcst["ds"]), name="forecast")

            # приводим к концу периода
            if pfreq == "M":
                yhat.index = yhat.index.to_period("M").to_timestamp(how="end")
            else:
                yhat.index = yhat.index.to_period("Y").to_timestamp(how="end")

            if yhat.index.freq is None:
                yhat.index = pd.date_range(yhat.index[0], periods=len(yhat), freq=("A-DEC" if pfreq == "Y" else "M"))
            return yhat
        except Exception:
            # мягкий откат на Holt
            pass

    try:
        if freq.startswith("A"):
            model = Holt(history, initialization_method="estimated")
        else:
            if len(history) >= 24:
                model = ExponentialSmoothing(
                    history, trend="add", seasonal="add", seasonal_periods=12,
                    initialization_method="estimated"
                )
            else:
                model = Holt(history, initialization_method="estimated")
        fit = model.fit(optimized=True)
        fc = fit.forecast(steps)
        if not isinstance(fc.index, pd.DatetimeIndex) or len(fc.index) != steps:
            start = history.index[-1] + pd.tseries.frequencies.to_offset(freq)
            idx = pd.date_range(start, periods=steps, freq=freq)
            fc = pd.Series(np.asarray(fc), index=idx, name="forecast")
        return fc
    except Exception:
        tail = min(6, len(history))
        baseline = float(history.tail(tail).mean()) if tail else 0.0
        start = history.index[-1] + pd.tseries.frequencies.to_offset(freq)
        idx = pd.date_range(start, periods=steps, freq=freq)
        return pd.Series([baseline] * steps, index=idx, name="forecast")

def current_month_snapshot(df: pd.DataFrame) -> dict:
    if df is None or df.empty:
        return {}
    w = normalize_columns(df)
    w["is_income"] = w["type"].apply(is_income)
    w["is_expense"] = w["type"].apply(is_expense)
    lastp = w["date"].dt.to_period("M").max()
    cur = w[w["date"].dt.to_period("M") == lastp].copy()
    if cur.empty:
        return {}
    income_total  = float(cur.loc[cur["is_income"], "amount"].sum())
    expense_total = -float(cur.loc[cur["is_expense"], "amount"].abs().sum())
    net = income_total + expense_total
    exp_df = cur.loc[cur["is_expense"], ["category","amount"]].copy()
    exp_df["amount"] = -exp_df["amount"].abs()
    top = exp_df.groupby("category")["amount"].sum().sort_values().head(5)
    return {
        "month": str(lastp),
        "income_total": income_total,
        "expense_total": expense_total,
        "net": net,
        "top_expense_categories": [(str(k), float(v)) for k,v in top.items()]
    }

def read_json_stdin() -> dict:
    import sys
    raw = sys.stdin.read()
    return json.loads(raw or "{}")

def write_json_stdout(obj) -> None:
    import sys
    sys.stdout.write(json.dumps(obj, ensure_ascii=False))
    sys.stdout.flush()
