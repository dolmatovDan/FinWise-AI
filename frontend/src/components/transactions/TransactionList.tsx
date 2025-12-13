// frontend/src/components/transactions/TransactionList.tsx

import { Transaction, Category, TransactionType } from "../../types";
import { Card } from "../ui/card";
import { Button } from "../ui/button";

interface TransactionListProps {
  transactions: Transaction[];
  categories: Category[];
  onDelete?: (id: string) => void;
}

export function TransactionList({
  transactions,
  categories,
  onDelete,
}: TransactionListProps) {
  const getCategoryById = (id: string) => {
    return categories.find((c) => c.id === id);
  };

  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat("ru-RU", {
      style: "currency",
      currency: "RUB",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="space-y-3">
      {transactions.map((transaction) => {
        // Помни: transaction.categoryId временно хранит имя категории
        const category = getCategoryById(transaction.categoryId);
        const isIncome = transaction.type === "income";

        return (
          <Card key={transaction.id} className="p-4">
            <div className="flex items-start justify-between gap-4">
              <div className="flex items-start gap-3 flex-1">
                <div
                  className="w-10 h-10 rounded-lg flex items-center justify-center"
                  style={{
                    backgroundColor: category?.color
                      ? `${category.color}20`
                      : "#e5e7eb",
                  }}
                >
                  <span className="text-lg">{category?.icon || "❓"}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2">
                    <h4 className="truncate">{transaction.description}</h4>
                    {isIncome ? (
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        className="text-green-600 flex-shrink-0"
                      >
                        <circle cx="12" cy="12" r="10" />
                        <path d="m16 12-4-4-4 4" />
                        <path d="M12 16V8" />
                      </svg>
                    ) : (
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        className="text-red-600 flex-shrink-0"
                      >
                        <circle cx="12" cy="12" r="10" />
                        <path d="M12 8v8" />
                        <path d="m8 12 4 4 4-4" />
                      </svg>
                    )}
                  </div>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-muted-foreground text-sm">
                      {category?.name || transaction.categoryId}
                    </span>
                    <span className="text-muted-foreground text-sm">•</span>
                    <span className="text-muted-foreground text-sm">
                      {transaction.date.toLocaleDateString("ru-RU")}
                    </span>
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <span
                  className={`${isIncome ? "text-green-600" : "text-red-600"}`}
                >
                  {isIncome ? "+" : "-"} {formatCurrency(transaction.amount)}
                </span>
                {onDelete && (
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => onDelete(transaction.id)}
                    className="h-8 w-8"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    >
                      <path d="M3 6h18" />
                      <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" />
                      <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" />
                    </svg>
                  </Button>
                )}
              </div>
            </div>
          </Card>
        );
      })}
      {transactions.length === 0 && (
        <div className="text-center py-12 text-muted-foreground">
          Нет транзакций для отображения
        </div>
      )}
    </div>
  );
}
