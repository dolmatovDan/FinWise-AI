// frontend/src/components/transactions/TransactionsPage.tsx

import { useState } from "react";
import { Transaction, Category, TransactionType } from "../../types";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { TransactionList } from "./TransactionList";
import { TransactionForm } from "./TransactionForm";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../ui/tabs";

interface TransactionsPageProps {
  transactions: Transaction[];
  categories: Category[];
  onAddTransaction: (transaction: Omit<Transaction, "id" | "userId">) => void;
  onDeleteTransaction: (id: string) => void;
}

export function TransactionsPage({
  transactions,
  categories,
  onAddTransaction,
  onDeleteTransaction,
}: TransactionsPageProps) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [filterType, setFilterType] = useState<"all" | "income" | "expense">(
    "all"
  );

  const handleFormSuccess = () => {
    setIsDialogOpen(false);
  };

  const sortedTransactions = [...transactions].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  const filteredTransactions =
    filterType === "all"
      ? sortedTransactions
      : sortedTransactions.filter((t) => t.type === filterType);

  const totalIncome = transactions
    .filter((t) => t.type === "income")
    .reduce((sum, t) => sum + t.amount, 0);

  const totalExpense = transactions
    .filter((t) => t.type === "expense")
    .reduce((sum, t) => sum + t.amount, 0);

  const formatCurrency = (value: number): string => {
    return new Intl.NumberFormat("ru-RU", {
      style: "currency",
      currency: "RUB",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(value);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Все транзакции</h2>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button>
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
                className="mr-2"
              >
                <path d="M5 12h14" />
                <path d="M12 5v14" />
              </svg>
              Добавить операцию
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Новая операция</DialogTitle>
            </DialogHeader>
            <TransactionForm
              categories={categories}
              onSuccess={handleFormSuccess}
              onAddTransaction={onAddTransaction} // ← передаём коллбэк
            />
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle className="text-sm">Всего транзакций</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{transactions.length}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-sm">Доходы</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl text-green-600">
              {formatCurrency(totalIncome)}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-sm">Расходы</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl text-red-600">
              {formatCurrency(totalExpense)}
            </div>
          </CardContent>
        </Card>
      </div>

      <Tabs
        value={filterType}
        onValueChange={(value: string) => {
          if (value === "all" || value === "income" || value === "expense") {
            setFilterType(value);
          }
        }}
      >
        <TabsList>
          <TabsTrigger value="all">Все</TabsTrigger>
          <TabsTrigger value="income">Доходы</TabsTrigger>
          <TabsTrigger value="expense">Расходы</TabsTrigger>
        </TabsList>
        <TabsContent value={filterType} className="mt-6">
          <TransactionList
            transactions={filteredTransactions}
            categories={categories}
            onDelete={onDeleteTransaction}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}
