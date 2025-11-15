// frontend/src/components/categories/CategoryManager.tsx

import { useState } from "react";
import { Category, TransactionType } from "../../types";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Switch } from "../ui/switch";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { Badge } from "../ui/badge";

// Палитра для выбора
const EMOJI_OPTIONS = [
  "💼",
  "💻",
  "📈",
  "🛒",
  "🚗",
  "🎮",
  "⚕️",
  "📚",
  "🏠",
  "👕",
  "✈️",
  "🍕",
  "☕",
  "🎬",
  "💡",
  "🎨",
  "🏋️",
  "🎵",
];
const COLOR_OPTIONS = [
  "#10b981",
  "#06b6d4",
  "#8b5cf6",
  "#f59e0b",
  "#ef4444",
  "#ec4899",
  "#14b8a6",
  "#3b82f6",
  "#6366f1",
  "#a855f7",
];

interface CategoryManagerProps {
  categories: Category[];
  onAddCategory: (category: Omit<Category, "id">) => void;
  onToggleCategory: (id: string, isActive: boolean) => void;
  onDeleteCategory: (id: string) => void;
}

export function CategoryManager({
  categories,
  onAddCategory,
  onToggleCategory,
  onDeleteCategory,
}: CategoryManagerProps) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [name, setName] = useState("");
  const [type, setType] = useState<TransactionType>("expense");
  const [icon, setIcon] = useState(EMOJI_OPTIONS[0]);
  const [color, setColor] = useState(COLOR_OPTIONS[0]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onAddCategory({ name, type, icon, color, isActive: true });
    // Сброс формы
    setName("");
    setType("expense");
    setIcon(EMOJI_OPTIONS[0]);
    setColor(COLOR_OPTIONS[0]);
    setIsDialogOpen(false);
  };

  const incomeCategories = categories.filter((c) => c.type === "income");
  const expenseCategories = categories.filter((c) => c.type === "expense");

  // Вспомогательная функция для отображения фона иконки
  const getIconBgStyle = (categoryColor: string) => ({
    backgroundColor: `${categoryColor}20`, // добавляем прозрачность (20 = 12% opacity)
  });

  return (
    <div className="space-y-6">
      {/* Заголовок и кнопка добавления */}
      <div className="flex justify-between items-center">
        <h3 className="text-xl font-semibold">Управление категориями</h3>
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
              Добавить категорию
            </Button>
          </DialogTrigger>

          <DialogContent>
            <DialogHeader>
              <DialogTitle>Новая категория</DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="cat-name">Название</Label>
                <Input
                  id="cat-name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Название категории"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label>Тип</Label>
                <Select
                  value={type}
                  onValueChange={(value: string) =>
                    setType(value as TransactionType)
                  }
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
                <Label>Иконка</Label>
                <div className="grid grid-cols-9 gap-2">
                  {EMOJI_OPTIONS.map((emoji) => (
                    <button
                      key={emoji}
                      type="button"
                      onClick={() => setIcon(emoji)}
                      className={`w-10 h-10 rounded-lg flex items-center justify-center transition-all ${
                        icon === emoji
                          ? "bg-primary text-primary-foreground"
                          : "bg-muted hover:bg-muted/80"
                      }`}
                    >
                      {emoji}
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-2">
                <Label>Цвет</Label>
                <div className="grid grid-cols-10 gap-2">
                  {COLOR_OPTIONS.map((colorOption) => (
                    <button
                      key={colorOption}
                      type="button"
                      onClick={() => setColor(colorOption)}
                      className={`w-10 h-10 rounded-lg ${
                        color === colorOption
                          ? "ring-2 ring-offset-2 ring-primary"
                          : ""
                      }`}
                      style={{ backgroundColor: colorOption }}
                    />
                  ))}
                </div>
              </div>

              <Button type="submit" className="w-full">
                Создать категорию
              </Button>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      {/* Списки категорий */}
      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Доходы</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {incomeCategories.length === 0 ? (
              <p className="text-muted-foreground text-center py-4">
                Нет категорий доходов
              </p>
            ) : (
              incomeCategories.map((category) => (
                <CategoryItem
                  key={category.id}
                  category={category}
                  onToggle={onToggleCategory}
                  onDelete={onDeleteCategory}
                />
              ))
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Расходы</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {expenseCategories.length === 0 ? (
              <p className="text-muted-foreground text-center py-4">
                Нет категорий расходов
              </p>
            ) : (
              expenseCategories.map((category) => (
                <CategoryItem
                  key={category.id}
                  category={category}
                  onToggle={onToggleCategory}
                  onDelete={onDeleteCategory}
                />
              ))
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

// 🔁 Вынесенный компонент для переиспользования карточки категории
interface CategoryItemProps {
  category: Category;
  onToggle: (id: string, isActive: boolean) => void;
  onDelete: (id: string) => void;
}

function CategoryItem({ category, onToggle, onDelete }: CategoryItemProps) {
  return (
    <div className="flex items-center justify-between p-3 rounded-lg border">
      <div className="flex items-center gap-3">
        <div
          className="w-10 h-10 rounded-lg flex items-center justify-center"
          style={{ backgroundColor: `${category.color}20` }}
        >
          <span className="text-lg">{category.icon}</span>
        </div>
        <div>
          <div className="flex items-center gap-2">
            <span>{category.name}</span>
            {!category.isActive && <Badge variant="outline">Отключена</Badge>}
          </div>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <Switch
          checked={category.isActive}
          onCheckedChange={(checked: boolean) => onToggle(category.id, checked)}
        />
        <Button
          variant="ghost"
          size="icon"
          onClick={() => onDelete(category.id)}
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
      </div>
    </div>
  );
}
