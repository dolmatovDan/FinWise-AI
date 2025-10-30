import pandas as pd
from common import prepare_components_series, fit_and_forecast, read_json_stdin, write_json_stdout

def main():
    req = read_json_stdin()
    tx = req.get("transactions", [])
    if not tx:
        write_json_stdout({"error": "transactions is empty"})
        return
    df = pd.DataFrame(tx)

    gran = (req.get("granularity") or "month").lower()
    freq = "A-DEC" if gran.startswith("y") else "M"
    steps = int(req.get("steps") or (1 if freq == "A-DEC" else 1))
    method = (req.get("model") or "auto").lower()

    inc, exp, _ = prepare_components_series(df, freq=freq)
    inc_fc = fit_and_forecast(inc, steps, freq, method=method)
    exp_fc = fit_and_forecast(exp, steps, freq, method=method)

    out = {
        "period_end": [d.strftime("%Y-%m-%d") for d in inc_fc.index.to_pydatetime().tolist()],
        "income_forecast": [float(x) for x in inc_fc.values.tolist()],
        "expense_forecast": [float(x) for x in exp_fc.values.tolist()],
    }
    write_json_stdout(out)

if __name__ == "__main__":
    main()
