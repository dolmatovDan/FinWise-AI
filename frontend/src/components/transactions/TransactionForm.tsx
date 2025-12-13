// frontend/src/components/transactions/TransactionForm.tsx

import { useState } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Textarea } from "../ui/textarea";
import { Category, Transaction, TransactionType } from "../../types";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Calendar } from "../ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";

interface TransactionFormProps {
  categories: Category[];
  onSuccess?: () => void;
  onAddTransaction: (transaction: Omit<Transaction, "id" | "userId">) => void;
}

export function TransactionForm({
  categories,
  onSuccess,
  onAddTransaction, // ← не забудь деструктуризировать!
}: TransactionFormProps) {
  const [type, setType] = useState<TransactionType>("expense");
  const [amount, setAmount] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [description, setDescription] = useState("");
  const [date, setDate] = useState<Date>(new Date());
  const [isSubmitting, setIsSubmitting] = useState(false);

  const filteredCategories = categories.filter(
    (c) => c.type === type && c.isActive
  );

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const numAmount = parseFloat(amount);
    const selectedCategory = categories.find((c) => c.id === categoryId);

    if (
      isNaN(numAmount) ||
      numAmount <= 0 ||
      !selectedCategory ||
      !description.trim()
    ) {
      return;
    }

    setIsSubmitting(true);
    try {
      const newTransaction: Omit<Transaction, "id" | "userId"> = {
        amount: numAmount,
        type,
        categoryId: categoryId,
        description: description.trim(),
        date,
      };

      await onAddTransaction(newTransaction);
      onSuccess?.();
    } catch (err) {
      console.error("Ошибка создания транзакции:", err);
      alert("Не удалось создать транзакцию");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="space-y-2">
          <Label>Тип операции</Label>
          <Select
            value={type}
            onValueChange={(value: string) => setType(value as TransactionType)}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="expense">Расход</SelectItem>
              <SelectItem value="income">Доход</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-2">
          <Label htmlFor="amount">Сумма</Label>
          <Input
            id="amount"
            type="number"
            placeholder="0"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            min="0"
            step="0.01"
            required
          />
        </div>
      </div>

      <div className="space-y-2">
        <Label>Категория</Label>
        <Select value={categoryId} onValueChange={setCategoryId} required>
          <SelectTrigger>
            <SelectValue placeholder="Выберите категорию" />
          </SelectTrigger>
          <SelectContent>
            {filteredCategories.map((category) => (
              <SelectItem key={category.id} value={category.id}>
                <span className="flex items-center gap-2">
                  <span>{category.icon}</span>
                  <span>{category.name}</span>
                </span>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="space-y-2">
        <Label>Дата</Label>
        <Popover>
          <PopoverTrigger asChild>
            <Button variant="outline" className="w-full justify-start">
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
                <path d="M8 2v4" />
                <path d="M16 2v4" />
                <rect width="18" height="18" x="3" y="4" rx="2" />
                <path d="M3 10h18" />
              </svg>
              {date.toLocaleDateString("ru-RU")}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0">
            <Calendar
              mode="single"
              selected={date}
              onSelect={(newDate: Date | undefined) =>
                newDate && setDate(newDate)
              }
            />
          </PopoverContent>
        </Popover>
      </div>

      <div className="space-y-2">
        <Label htmlFor="description">Описание</Label>
        <Textarea
          id="description"
          placeholder="Добавьте описание транзакции..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />
      </div>

      <Button type="submit" className="w-full" disabled={isSubmitting}>
        {isSubmitting ? "Создание..." : "Добавить операцию"}
      </Button>
    </form>
  );
}
