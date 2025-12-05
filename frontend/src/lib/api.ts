import { Transaction, Category, User, AIRecommendation } from '../types';
import { mockTransactions, mockCategories, mockAIRecommendations } from './mockData';

// Эти функции подготовлены для подключения к реальному API
// Просто замените mock данные на fetch/axios запросы к вашему backend

export const api = {
  // Auth API
  auth: {
    login: async (email: string, password: string): Promise<{ user: User; token: string }> => {
      // TODO: Заменить на реальный API вызов
      // const response = await fetch('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) });
      // return response.json();
      
      await new Promise(resolve => setTimeout(resolve, 500)); // Simulate API delay
      return {
        user: { id: 'user1', email, name: 'Иван Петров' },
        token: 'mock-jwt-token',
      };
    },
    
    register: async (email: string, password: string, name: string): Promise<{ user: User; token: string }> => {
      // TODO: Заменить на реальный API вызов
      // const response = await fetch('/api/auth/register', { method: 'POST', body: JSON.stringify({ email, password, name }) });
      // return response.json();
      
      await new Promise(resolve => setTimeout(resolve, 500));
      return {
        user: { id: 'user1', email, name },
        token: 'mock-jwt-token',
      };
    },
    
    logout: async (): Promise<void> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
    },
  },

  // Transactions API
  transactions: {
    getAll: async (): Promise<Transaction[]> => {
      // TODO: Заменить на реальный API вызов
      // const response = await fetch('/api/transactions', { headers: { 'Authorization': `Bearer ${token}` } });
      // return response.json();
      
      await new Promise(resolve => setTimeout(resolve, 300));
      return mockTransactions;
    },
    
    create: async (transaction: Omit<Transaction, 'id'>): Promise<Transaction> => {
      // TODO: Заменить на реальный API вызов
      // const response = await fetch('/api/transactions', { method: 'POST', body: JSON.stringify(transaction) });
      // return response.json();
      
      await new Promise(resolve => setTimeout(resolve, 300));
      return { ...transaction, id: Date.now().toString() } as Transaction;
    },
    
    update: async (id: string, transaction: Partial<Transaction>): Promise<Transaction> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
      const existing = mockTransactions.find(t => t.id === id);
      return { ...existing, ...transaction } as Transaction;
    },
    
    delete: async (id: string): Promise<void> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
    },
  },

  // Categories API
  categories: {
    getAll: async (): Promise<Category[]> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
      return mockCategories;
    },
    
    create: async (category: Omit<Category, 'id'>): Promise<Category> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
      return { ...category, id: Date.now().toString() } as Category;
    },
    
    update: async (id: string, category: Partial<Category>): Promise<Category> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
      const existing = mockCategories.find(c => c.id === id);
      return { ...existing, ...category } as Category;
    },
    
    delete: async (id: string): Promise<void> => {
      // TODO: Заменить на реальный API вызов
      await new Promise(resolve => setTimeout(resolve, 300));
    },
  },

  // AI Recommendations API
  ai: {
    getRecommendations: async (): Promise<AIRecommendation[]> => {
      // TODO: Заменить на реальный API вызов к AI сервису
      // const response = await fetch('/api/ai/recommendations');
      // return response.json();
      
      await new Promise(resolve => setTimeout(resolve, 500));
      return mockAIRecommendations;
    },
  },
};
