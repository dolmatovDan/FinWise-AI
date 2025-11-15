// frontend/src/types/index.ts

export type TransactionType = 'income' | 'expense';

export interface Category {
  id: string;
  name: string;
  type: TransactionType;
  icon: string;
  color: string;
  isActive: boolean;
}

// ✅ Тип для фронтенда (UI)
export interface Transaction {
  id: string;
  userId: string;          // или number — решите позже
  amount: number;
  type: TransactionType;
  categoryId: string;      // ссылка на Category.id
  description: string;
  date: Date;              // объект Date — удобно для форм
}

// ✅ Тип для API (то, что приходит/уходит на бэкенд)
export interface TransactionDTO {
  id: string;
  user_id: number;         // бэкенд использует number
  amount: number;
  type: TransactionType;
  category: string;        // строка — имя категории
  description: string;
  date: string;            // "YYYY-MM-DD"
}

export interface User {
  id: string;
  email: string;
  name: string;
}

export interface AIRecommendation {
  id: string;
  title: string;
  description: string;
  category: 'saving' | 'budget' | 'investment';
  priority: 'high' | 'medium' | 'low';
  potentialSavings?: number;
}

export interface FinancialStats {
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  savingsRate: number;
}