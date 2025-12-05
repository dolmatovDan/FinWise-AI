// frontend/src/lib/transactionMapper.ts

import { Transaction, TransactionDTO, Category } from '../types';/**
 * Преобразует DTO от бэкенда в фронтенд-модель.
 */
export const dtoToTransaction = (dto: TransactionDTO): Transaction => ({
  id: dto.id,
  userId: dto.user_id.toString(), // Бэкенд присылает number → конвертируем в string
  amount: dto.amount,
  type: dto.type,
  // Временно: categoryId = имя категории (пока бэкенд не поддерживает ID)
  categoryId: dto.category,
  description: dto.description,
  date: new Date(dto.date), // Парсим ISO-строку в Date
});

/**
 * Формирует payload для отправки на бэкенд при создании транзакции.
 */
export const buildCreatePayload = (
  amount: number,
  type: 'income' | 'expense',
  categoryName: string,
  description: string,
  date: Date,
  userId: number = 1
) => ({
  user_id: userId,
  amount,
  category: categoryName, // ← строка, как ожидает бэкенд
  description,
  type,
  date: date.toISOString().split('T')[0], // "2025-11-15"
});