// frontend/src/lib/api/transactionApi.ts

import axios from 'axios';
import { Transaction } from '../../types';

const api = axios.create({ baseURL: '/api/v1' });

// Данные для создания — как ожидает бэкенд
export type TransactionCreatePayload = {
  user_id: number;
  amount: number;
  category: string;        // ← строка
  description: string;
  type: 'income' | 'expense';
  date: string;            // "YYYY-MM-DD"
};

export const transactionApi = {
  create: (data: TransactionCreatePayload) => api.post('/transactions', data),

  list: (params?: {
    user_id?: number;
    type?: 'income' | 'expense';
    category?: string;
    page?: number;
    page_size?: number;
  }) => api.get<{ data: Transaction[]; total?: number }>('/transactions', { params }),

  getById: (id: string) => api.get<{ data: Transaction }>(`/transactions/${id}`),

  update: (id: string, data: Omit<TransactionCreatePayload, 'user_id'>) =>
    api.put(`/transactions/${id}`, data),

  delete: (id: string) => api.delete(`/transactions/${id}`),
};