import { Transaction, Category, AIRecommendation, FinancialStats } from '../../types';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { TransactionList } from '../transactions/TransactionList';
import { FinancialCharts } from '../charts/FinancialCharts';
import { AIRecommendations } from '../ai/AIRecommendations';
import { Button } from '../ui/button';

interface DashboardProps {
  transactions: Transaction[];
  categories: Category[];
  recommendations: AIRecommendation[];
  stats: FinancialStats;
  onNavigateToTransactions: () => void;
}

export function Dashboard({ transactions, categories, recommendations, stats, onNavigateToTransactions }: DashboardProps) {
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('ru-RU', {
      style: 'currency',
      currency: 'RUB',
      minimumFractionDigits: 0,
    }).format(value);
  };

  const sortedTransactions = [...transactions].sort((a, b) => 
    new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  return (
    <div className="space-y-6">
      {/* Stats Cards */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Баланс</CardTitle>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-muted-foreground"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{formatCurrency(stats.balance)}</div>
            <p className="text-xs text-muted-foreground mt-1">
              Текущий остаток средств
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Доходы</CardTitle>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-green-600"><circle cx="12" cy="12" r="10"/><path d="m16 12-4-4-4 4"/><path d="M12 16V8"/></svg>
          </CardHeader>
          <CardContent>
            <div className="text-2xl text-green-600">{formatCurrency(stats.totalIncome)}</div>
            <p className="text-xs text-muted-foreground mt-1">
              За текущий период
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Расходы</CardTitle>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-red-600"><circle cx="12" cy="12" r="10"/><path d="M12 8v8"/><path d="m8 12 4 4 4-4"/></svg>
          </CardHeader>
          <CardContent>
            <div className="text-2xl text-red-600">{formatCurrency(stats.totalExpenses)}</div>
            <p className="text-xs text-muted-foreground mt-1">
              За текущий период
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm">Норма накоплений</CardTitle>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-muted-foreground"><path d="m22 7-8.5 8.5a1.414 1.414 0 0 1-2 0L8 12l-5 5"/><path d="M16 7h6v6"/></svg>
          </CardHeader>
          <CardContent>
            <div className="text-2xl">{stats.savingsRate.toFixed(1)}%</div>
            <p className="text-xs text-muted-foreground mt-1">
              От общего дохода
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Charts */}
      <FinancialCharts transactions={transactions} categories={categories} />

      {/* Recent Transactions and AI Recommendations */}
      <div className="grid gap-6 md:grid-cols-2">
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3>Последние операции</h3>
            <Button variant="outline" size="sm" onClick={onNavigateToTransactions}>
              Все операции
            </Button>
          </div>
          <TransactionList 
            transactions={sortedTransactions} 
            categories={categories} 
            limit={5}
          />
        </div>

        <div className="space-y-4">
          <h3>AI Рекомендации</h3>
          <AIRecommendations recommendations={recommendations.slice(0, 2)} />
        </div>
      </div>
    </div>
  );
}
