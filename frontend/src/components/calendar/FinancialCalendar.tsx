import { useState } from "react";
import { Transaction, Category } from "../../types";
import { Card } from "../ui/card";

interface DayData {
  date: Date;
  total: number;
  transactions: Transaction[];
}

interface FinancialCalendarProps {
  transactions: Transaction[];
  categories: Category[];
}

export function FinancialCalendar({
  transactions,
  categories,
}: FinancialCalendarProps) {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [hoveredDay, setHoveredDay] = useState<DayData | null>(null);
  const [tooltipPosition, setTooltipPosition] = useState({ x: 0, y: 0 });

  // Функция для получения цвета и эмодзи в зависимости от суммы расходов
  const getStyleForAmount = (
    amount: number
  ): { gradient: string; emoji: string; border: string } => {
    if (amount === 0)
      return {
        gradient: "bg-gradient-to-br from-emerald-100 to-emerald-200",
        emoji: "", //"😊",
        border: "border-emerald-300",
      };
    if (amount <= 500)
      return {
        gradient: "bg-gradient-to-br from-green-100 to-green-200",
        emoji: "", //"🙂",
        border: "border-green-300",
      };
    if (amount <= 2000)
      return {
        gradient: "bg-gradient-to-br from-amber-100 to-amber-200",
        emoji: "", //"😐",
        border: "border-amber-300",
      };
    if (amount <= 5000)
      return {
        gradient: "bg-gradient-to-br from-orange-200 to-orange-300",
        emoji: "", //"😟",
        border: "border-orange-400",
      };
    return {
      gradient: "bg-gradient-to-br from-red-200 to-red-300",
      emoji: "", //"😱",
      border: "border-red-400",
    };
  };

  // Получение данных по дням месяца
  const getMonthData = (): DayData[] => {
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    const days: DayData[] = [];

    for (let date = 1; date <= lastDay.getDate(); date++) {
      const currentDate = new Date(year, month, date);

      // Фильтруем транзакции за этот день
      const dayTransactions = transactions.filter((t) => {
        const tDate = new Date(t.date); // Ensure date is a Date object
        return (
          t.type === "expense" &&
          tDate.getDate() === date &&
          tDate.getMonth() === month &&
          tDate.getFullYear() === year
        );
      });

      const total = dayTransactions.reduce((sum, t) => sum + t.amount, 0);

      days.push({
        date: currentDate,
        total,
        transactions: dayTransactions,
      });
    }

    return days;
  };

  const monthData = getMonthData();

  // Функция для изменения месяца
  const changeMonth = (delta: number) => {
    const newMonth = new Date(currentMonth);
    newMonth.setMonth(newMonth.getMonth() + delta);
    setCurrentMonth(newMonth);
  };

  // Получение имени месяца
  const monthNames = [
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь",
  ];

  const monthName = monthNames[currentMonth.getMonth()];
  const year = currentMonth.getFullYear();

  // Определяем первый день недели месяца (0 = воскресенье, 1 = понедельник, ...)
  const firstDayOfWeek = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth(),
    1
  ).getDay();
  const adjustedFirstDay = firstDayOfWeek === 0 ? 6 : firstDayOfWeek - 1; // Преобразуем так, чтобы понедельник был 0

  const handleMouseEnter = (day: DayData, event: React.MouseEvent) => {
    setHoveredDay(day);
    setTooltipPosition({ x: event.clientX, y: event.clientY });
  };

  const handleMouseLeave = () => {
    setHoveredDay(null);
  };

  // Получить категорию по ID
  const getCategoryById = (categoryId: string) => {
    return categories.find((c) => c.id === categoryId);
  };

  return (
    <div className="space-y-6">
      {/* Заголовок с навигацией */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
            {monthName} {year}
          </h2>
          <p className="text-sm text-muted-foreground mt-1">
            Календарь финансового самочувствия
          </p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => changeMonth(-1)}
            className="p-3 rounded-xl hover:bg-blue-50 transition-all hover:scale-105 active:scale-95 border border-gray-200"
            aria-label="Предыдущий месяц"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="m15 18-6-6 6-6" />
            </svg>
          </button>
          <button
            onClick={() => setCurrentMonth(new Date())}
            className="px-5 py-3 rounded-xl hover:bg-blue-50 transition-all hover:scale-105 active:scale-95 border border-gray-200"
          >
            Сегодня
          </button>
          <button
            onClick={() => changeMonth(1)}
            className="p-3 rounded-xl hover:bg-blue-50 transition-all hover:scale-105 active:scale-95 border border-gray-200"
            aria-label="Следующий месяц"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="m9 18 6-6-6-6" />
            </svg>
          </button>
        </div>
      </div>

      {/* Календарь */}
      <Card className="p-8 bg-gradient-to-br from-white to-blue-50/30 shadow-lg">
        {/* Дни недели */}
        <div className="grid grid-cols-7 gap-3 mb-4">
          {["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"].map((day, index) => (
            <div
              key={day}
              className={`text-center py-3 rounded-lg ${
                index >= 5 ? "bg-purple-50" : "bg-blue-50"
              }`}
            >
              <span
                className={index >= 5 ? "text-purple-700" : "text-blue-700"}
              >
                {day}
              </span>
            </div>
          ))}
        </div>

        {/* Дни месяца */}
        <div className="grid grid-cols-7 gap-3">
          {/* Пустые ячейки для выравнивания первого дня */}
          {Array.from({ length: adjustedFirstDay }).map((_, index) => (
            <div key={`empty-${index}`} className="aspect-square" />
          ))}

          {/* Дни месяца */}
          {monthData.map((day) => {
            const isToday =
              day.date.toDateString() === new Date().toDateString();
            const style = getStyleForAmount(day.total);
            const isWeekend =
              day.date.getDay() === 0 || day.date.getDay() === 6;

            return (
              <div
                key={day.date.toISOString()}
                className={`aspect-square rounded-2xl ${
                  style.gradient
                } border-2 ${style.border} 
                  flex flex-col items-center justify-center p-3 cursor-pointer 
                  transition-all duration-300 hover:scale-110 hover:shadow-xl hover:z-10
                  ${
                    isToday
                      ? "ring-4 ring-blue-500 ring-offset-2 shadow-lg"
                      : "shadow-md"
                  }
                  ${isWeekend ? "opacity-90" : ""}
                  relative overflow-hidden group`}
                onMouseEnter={(e) => handleMouseEnter(day, e)}
                onMouseLeave={handleMouseLeave}
              >
                {/* Декоративный элемент для текущего дня */}
                {isToday && (
                  <div className="absolute top-1 right-1 w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
                )}

                {/* Эмодзи настроения */}
                <div className="text-2xl mb-1 transition-transform group-hover:scale-125">
                  {style.emoji}
                </div>

                {/* Номер дня */}
                <span className="text-sm">{day.date.getDate()}</span>

                {/* Сумма расходов */}
                {day.total > 0 && (
                  <span className="text-xs mt-1 px-2 py-0.5 bg-white/80 rounded-full backdrop-blur-sm">
                    {Math.round(day.total).toLocaleString("ru-RU")} ₽
                  </span>
                )}
              </div>
            );
          })}
        </div>

        {/* Легенда */}
        <div className="mt-8 pt-6 border-t border-gray-200">
          <p className="text-sm text-muted-foreground mb-4">
            Настроение по тратам:
          </p>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4 text-sm">
            <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-emerald-100 to-emerald-200 border border-emerald-300">
              <span className="text-2xl">😊</span>
              <div>
                <div>Отлично</div>
                <div className="text-xs text-muted-foreground">0 ₽</div>
              </div>
            </div>
            <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-green-100 to-green-200 border border-green-300">
              <span className="text-2xl">🙂</span>
              <div>
                <div>Хорошо</div>
                <div className="text-xs text-muted-foreground">до 500 ₽</div>
              </div>
            </div>
            <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-amber-100 to-amber-200 border border-amber-300">
              <span className="text-2xl">😐</span>
              <div>
                <div>Нормально</div>
                <div className="text-xs text-muted-foreground">500–2000 ₽</div>
              </div>
            </div>
            <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-orange-200 to-orange-300 border border-orange-400">
              <span className="text-2xl">😟</span>
              <div>
                <div>Много</div>
                <div className="text-xs text-muted-foreground">2000–5000 ₽</div>
              </div>
            </div>
            <div className="flex items-center gap-3 p-3 rounded-xl bg-gradient-to-br from-red-200 to-red-300 border border-red-400">
              <span className="text-2xl">😱</span>
              <div>
                <div>Очень много</div>
                <div className="text-xs text-muted-foreground">
                  свыше 5000 ₽
                </div>
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* Тултип */}
      {hoveredDay && hoveredDay.transactions.length > 0 && (
        <div
          className="fixed z-50 bg-white rounded-2xl shadow-2xl border-2 border-gray-200 p-5 max-w-sm pointer-events-none backdrop-blur-xl bg-white/95 animate-in fade-in duration-200"
          style={{
            left: `${tooltipPosition.x + 10}px`,
            top: `${tooltipPosition.y + 10}px`,
          }}
        >
          <div className="mb-3 pb-3 border-b border-gray-200">
            <p className="text-sm">
              {hoveredDay.date.toLocaleDateString("ru-RU", {
                day: "numeric",
                month: "long",
              })}
            </p>
            <p className="text-lg mt-1">
              Всего:{" "}
              <span className="text-red-600">
                {hoveredDay.total.toLocaleString("ru-RU")} ₽
              </span>
            </p>
          </div>
          <div className="space-y-2">
            {hoveredDay.transactions.map((transaction) => {
              const category = getCategoryById(transaction.categoryId);
              return (
                <div
                  key={transaction.id}
                  className="flex items-center justify-between gap-3 p-2 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <span className="flex items-center gap-2 text-sm">
                    <span className="text-lg">{category?.icon}</span>
                    <span>{category?.name}</span>
                  </span>
                  <span className="text-sm text-red-600">
                    {transaction.amount.toLocaleString("ru-RU")} ₽
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
