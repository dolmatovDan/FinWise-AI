import { useState } from "react";
import { Transaction, Category } from "../../types";
import { FinancialCalendar } from "../calendar/FinancialCalendar";
import { CalendarStats } from "../calendar/CalendarStats";

interface FinancialWellbeingPageProps {
  transactions: Transaction[];
  categories: Category[];
}

export function FinancialWellbeingPage({
  transactions,
  categories,
}: FinancialWellbeingPageProps) {
  const [currentMonth] = useState(new Date());

  return (
    <div className="space-y-8">
      {/* Заголовок */}
      <div>
        <h1 className="text-3xl mb-2">Финансовое самочувствие</h1>
        <p className="text-muted-foreground">
          Визуализация ваших финансовых привычек и паттернов трат
        </p>
      </div>

      {/* Календарь */}
      <FinancialCalendar transactions={transactions} categories={categories} />

      {/* Статистика */}
      <CalendarStats
        transactions={transactions}
        categories={categories}
        currentMonth={currentMonth}
      />
    </div>
  );
}
