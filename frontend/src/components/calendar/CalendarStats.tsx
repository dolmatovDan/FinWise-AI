import { Transaction, Category } from "../../types";
import { Card } from "../ui/card";

interface CalendarStatsProps {
  transactions: Transaction[];
  categories: Category[];
  currentMonth: Date;
}

export function CalendarStats({
  transactions,
  categories,
  currentMonth,
}: CalendarStatsProps) {
  // Фильтруем транзакции за текущий месяц
  const monthTransactions = transactions.filter((t) => {
    const tDate = new Date(t.date); // Ensure date is a Date object
    return (
      tDate.getMonth() === currentMonth.getMonth() &&
      tDate.getFullYear() === currentMonth.getFullYear()
    );
  });

  const expenses = monthTransactions.filter((t) => t.type === "expense");
  const incomes = monthTransactions.filter((t) => t.type === "income");

  // Вычисляем статистику
  const totalExpenses = expenses.reduce((sum, t) => sum + t.amount, 0);
  const totalIncome = incomes.reduce((sum, t) => sum + t.amount, 0);

  // Средний расход в день
  const daysInMonth = new Date(
    currentMonth.getFullYear(),
    currentMonth.getMonth() + 1,
    0
  ).getDate();
  const averagePerDay = totalExpenses / daysInMonth;

  // Дни без трат
  const uniqueExpenseDays = new Set(
    expenses.map((t) => new Date(t.date).getDate())
  );
  const daysWithoutExpenses = daysInMonth - uniqueExpenseDays.size;

  // Самый дорогой день
  const expensesByDay = expenses.reduce((acc, t) => {
    const day = new Date(t.date).getDate();
    acc[day] = (acc[day] || 0) + t.amount;
    return acc;
  }, {} as Record<number, number>);

  const mostExpensiveDay = Object.entries(expensesByDay).reduce(
    (max, [day, amount]) => {
      return amount > max.amount ? { day: Number(day), amount } : max;
    },
    { day: 0, amount: 0 }
  );

  // Рекорд бережливости (максимальное количество дней подряд без трат)
  const daysArray = Array.from({ length: daysInMonth }, (_, i) => i + 1);
  let maxStreak = 0;
  let currentStreak = 0;

  daysArray.forEach((day) => {
    if (!uniqueExpenseDays.has(day)) {
      currentStreak++;
      maxStreak = Math.max(maxStreak, currentStreak);
    } else {
      currentStreak = 0;
    }
  });

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

  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-2xl bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
          Итоги за {monthNames[currentMonth.getMonth()].toLowerCase()}{" "}
          {currentMonth.getFullYear()}
        </h3>
        <p className="text-sm text-muted-foreground mt-1">
          Анализ ваших финансовых привычек
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Средний расход в день */}
        <Card className="p-5 bg-gradient-to-br from-blue-50 to-white border-2 border-blue-100 hover:shadow-lg transition-all">
          <div className="flex items-start gap-3">
            <div className="p-3 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-md">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-white"
              >
                <line x1="12" x2="12" y1="2" y2="22" />
                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
              </svg>
            </div>
            <div className="flex-1">
              <p className="text-sm text-muted-foreground mb-1">
                Средний расход в день
              </p>
              <p className="text-2xl">
                {Math.round(averagePerDay).toLocaleString("ru-RU")} ₽
              </p>
            </div>
          </div>
        </Card>

        {/* Дней без трат */}
        <Card className="p-5 bg-gradient-to-br from-green-50 to-white border-2 border-green-100 hover:shadow-lg transition-all">
          <div className="flex items-start gap-3">
            <div className="p-3 bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-md">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-white"
              >
                <path d="M8 2v4" />
                <path d="M16 2v4" />
                <rect width="18" height="18" x="3" y="4" rx="2" />
                <path d="M3 10h18" />
                <path d="m9 16 2 2 4-4" />
              </svg>
            </div>
            <div className="flex-1">
              <p className="text-sm text-muted-foreground mb-1">
                Дней без трат
              </p>
              <p className="text-2xl">
                {daysWithoutExpenses}{" "}
                {daysWithoutExpenses === 1 ? "день" : "дней"}
              </p>
              <p className="text-xs text-green-600 mt-1">
                😊 Отличный результат!
              </p>
            </div>
          </div>
        </Card>

        {/* Рекорд бережливости */}
        <Card className="p-5 bg-gradient-to-br from-purple-50 to-white border-2 border-purple-100 hover:shadow-lg transition-all">
          <div className="flex items-start gap-3">
            <div className="p-3 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-md">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-white"
              >
                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6" />
                <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18" />
                <path d="M4 22h16" />
                <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22" />
                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22" />
                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z" />
              </svg>
            </div>
            <div className="flex-1">
              <p className="text-sm text-muted-foreground mb-1">
                Рекорд бережливости
              </p>
              <p className="text-2xl">
                {maxStreak} {maxStreak === 1 ? "день" : "дней"} подряд
              </p>
              <p className="text-xs text-purple-600 mt-1">🏆 Так держать!</p>
            </div>
          </div>
        </Card>

        {/* Самый дорогой день */}
        <Card className="p-5 bg-gradient-to-br from-red-50 to-white border-2 border-red-100 hover:shadow-lg transition-all">
          <div className="flex items-start gap-3">
            <div className="p-3 bg-gradient-to-br from-red-500 to-red-600 rounded-xl shadow-md">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-white"
              >
                <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z" />
                <path d="M3 6h18" />
                <path d="M16 10a4 4 0 0 1-8 0" />
              </svg>
            </div>
            <div className="flex-1">
              <p className="text-sm text-muted-foreground mb-1">
                Самый дорогой день
              </p>
              {mostExpensiveDay.day > 0 ? (
                <>
                  <p className="text-2xl">
                    {mostExpensiveDay.day}{" "}
                    {monthNames[currentMonth.getMonth()]
                      .slice(0, 3)
                      .toLowerCase()}
                  </p>
                  <p className="text-sm text-red-600 mt-1">
                    {Math.round(mostExpensiveDay.amount).toLocaleString(
                      "ru-RU"
                    )}{" "}
                    ₽
                  </p>
                </>
              ) : (
                <p className="text-2xl">—</p>
              )}
            </div>
          </div>
        </Card>
      </div>

      {/* Дополнительная информация */}
      <Card className="p-8 bg-gradient-to-br from-slate-50 to-white border-2 border-gray-200 shadow-lg">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="text-center p-6 rounded-2xl bg-gradient-to-br from-green-100 to-green-50 border-2 border-green-200">
            <p className="text-sm text-muted-foreground mb-3">Всего доходов</p>
            <div className="flex items-center justify-center gap-2">
              <span className="text-3xl">💰</span>
              <p className="text-3xl text-green-600">
                +{totalIncome.toLocaleString("ru-RU")} ₽
              </p>
            </div>
          </div>
          <div className="text-center p-6 rounded-2xl bg-gradient-to-br from-red-100 to-red-50 border-2 border-red-200">
            <p className="text-sm text-muted-foreground mb-3">Всего расходов</p>
            <div className="flex items-center justify-center gap-2">
              <span className="text-3xl">💸</span>
              <p className="text-3xl text-red-600">
                -{totalExpenses.toLocaleString("ru-RU")} ₽
              </p>
            </div>
          </div>
          <div className="text-center p-6 rounded-2xl bg-gradient-to-br from-blue-100 to-blue-50 border-2 border-blue-200">
            <p className="text-sm text-muted-foreground mb-3">Баланс месяца</p>
            <div className="flex items-center justify-center gap-2">
              <span className="text-3xl">
                {totalIncome - totalExpenses >= 0 ? "✨" : "📉"}
              </span>
              <p
                className={`text-3xl ${
                  totalIncome - totalExpenses >= 0
                    ? "text-green-600"
                    : "text-red-600"
                }`}
              >
                {totalIncome - totalExpenses >= 0 ? "+" : ""}
                {(totalIncome - totalExpenses).toLocaleString("ru-RU")} ₽
              </p>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}
