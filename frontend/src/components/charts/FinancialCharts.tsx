// frontend/src/components/charts/FinancialCharts.tsx

import { Transaction, Category } from '../../types';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';

interface FinancialChartsProps {
  transactions: Transaction[];
  categories: Category[];
}

/**
 * Визуализация финансовых данных:
 * - Доходы и расходы по месяцам (с годом)
 * - Расходы по категориям с цветовой индикацией
 */
export function FinancialCharts({ transactions, categories }: FinancialChartsProps) {
  // 🔁 Преобразуем категории в Map для O(1) поиска
  const categoryMap = new Map(categories.map(cat => [cat.id, cat]));

  // 📅 Группировка транзакций по месяцам (с учётом года)
  const monthlyMap = new Map<
    string, // monthKey = "2024-11"
    { monthKey: string; monthLabel: string; income: number; expense: number }
  >();

  transactions.forEach(tx => {
    const date = new Date(tx.date);
    // Защита от некорректных дат
    if (isNaN(date.getTime())) return;

    const monthKey = date.toISOString().slice(0, 7); // "2024-11"
    const monthLabel = date.toLocaleDateString('ru-RU', {
      year: '2-digit',
      month: 'short',
    }); // "ноя 24"

    if (!monthlyMap.has(monthKey)) {
      monthlyMap.set(monthKey, {
        monthKey,
        monthLabel,
        income: 0,
        expense: 0,
      });
    }

    const entry = monthlyMap.get(monthKey)!;
    if (tx.type === 'income') {
      entry.income += tx.amount;
    } else if (tx.type === 'expense') {
      entry.expense += tx.amount;
    }
  });

  // Сортируем по дате (от старых к новым)
  const monthlyData = Array.from(monthlyMap.values()).sort(
    (a, b) => new Date(a.monthKey).getTime() - new Date(b.monthKey).getTime()
  );

  // 💸 Агрегация расходов по категориям
  const expenseMap = new Map<string, { id: string; name: string; color: string; value: number }>();

  transactions
    .filter(t => t.type === 'expense')
    .forEach(tx => {
      const category = categoryMap.get(tx.categoryId);
      if (!category) return;

      const key = category.id;
      if (expenseMap.has(key)) {
        expenseMap.get(key)!.value += tx.amount;
      } else {
        expenseMap.set(key, {
          id: category.id,
          name: category.name,
          color: category.color,
          value: tx.amount,
        });
      }
    });

  // Сортируем по убыванию суммы
  const expensesByCategory = Array.from(expenseMap.values()).sort((a, b) => b.value - a.value);

  // 💰 Форматирование валюты
  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  // 📏 Максимальное значение для масштабирования баров
  const maxValue = monthlyData.length
    ? Math.max(...monthlyData.map(d => Math.max(d.income, d.expense)))
    : 1;

  return (
    <div className="grid gap-6 md:grid-cols-2">
      {/* 📊 Доходы и расходы по месяцам */}
      <Card>
        <CardHeader>
          <CardTitle>Доходы и расходы по месяцам</CardTitle>
        </CardHeader>
        <CardContent>
          {monthlyData.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              Нет данных для отображения
            </div>
          ) : (
            <div className="space-y-4">
              {monthlyData.map(data => (
                <div key={data.monthKey} className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span>{data.monthLabel}</span>
                    <span className="text-muted-foreground">
                      {formatCurrency(data.income)} / {formatCurrency(data.expense)}
                    </span>
                  </div>
                  <div className="flex gap-2">
                    <div
                      className="h-8 bg-green-500 rounded"
                      style={{ width: `${(data.income / maxValue) * 100}%` }}
                    />
                    <div
                      className="h-8 bg-red-500 rounded"
                      style={{ width: `${(data.expense / maxValue) * 100}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* 🥧 Расходы по категориям */}
      <Card>
        <CardHeader>
          <CardTitle>Расходы по категориям</CardTitle>
        </CardHeader>
        <CardContent>
          {expensesByCategory.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              Нет расходов для отображения
            </div>
          ) : (
            <div className="space-y-3">
              {expensesByCategory.map(item => {
                const total = expensesByCategory.reduce((sum, i) => sum + i.value, 0);
                const percentage = total > 0 ? (item.value / total) * 100 : 0;

                return (
                  <div key={item.id} className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span>{item.name}</span>
                      <span className="text-muted-foreground">
                        {formatCurrency(item.value)} ({percentage.toFixed(0)}%)
                      </span>
                    </div>
                    <div className="w-full bg-muted rounded-full h-2">
                      <div
                        className="h-2 rounded-full transition-all"
                        style={{
                          width: `${percentage}%`,
                          backgroundColor: item.color,
                        }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}