import { useState, useEffect } from "react";
import { LoginPage } from "./components/auth/LoginPage";
import { RegisterPage } from "./components/auth/RegisterPage";
import { Dashboard } from "./components/dashboard/Dashboard";
import { TransactionsPage } from "./components/transactions/TransactionsPage";
import { CategoryManager } from "./components/categories/CategoryManager";
import { AIRecommendations } from "./components/ai/AIRecommendations";
import { Transaction, Category, User, FinancialStats } from "./types";
import { api } from "./lib/api";
import { mockAIRecommendations } from "./lib/mockData";
import { Button } from "./components/ui/button";
import { Tabs, TabsList, TabsTrigger } from "./components/ui/tabs";
import { FinancialWellbeingPage } from "./components/wellbeing/FinancialWellbeingPage";

type Page =
  | "dashboard"
  | "transactions"
  | "categories"
  | "ai-recommendations"
  | "wellbeing";
export default function App() {
  const [user, setUser] = useState<User | null>(null);
  const [authPage, setAuthPage] = useState<"login" | "register">("login");
  const [currentPage, setCurrentPage] = useState<Page>("dashboard");

  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [recommendations, setRecommendations] = useState(mockAIRecommendations);

  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user) {
      loadData();
    }
  }, [user]);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [transactionsData, categoriesData, recommendationsData] =
        await Promise.all([
          api.transactions.getAll(),
          api.categories.getAll(),
          api.ai.getRecommendations(),
        ]);

      setTransactions(transactionsData);
      setCategories(categoriesData);
      setRecommendations(recommendationsData);
    } catch (error) {
      console.error("Error loading data");
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogin = async (email: string, password: string) => {
    try {
      const { user, token } = await api.auth.login(email, password);
      setUser(user);
      localStorage.setItem("token", token);
    } catch (error) {
      console.error("Login error");
    }
  };

  const handleRegister = async (
    email: string,
    password: string,
    name: string
  ) => {
    try {
      const { user, token } = await api.auth.register(email, password, name);
      setUser(user);
      localStorage.setItem("token", token);
    } catch (error) {
      console.error("Registration error");
    }
  };

  const handleLogout = async () => {
    await api.auth.logout();
    setUser(null);
    localStorage.removeItem("token");
    setTransactions([]);
    setCategories([]);
  };

  const handleAddTransaction = async (
    transaction: Omit<Transaction, "id" | "userId">
  ) => {
    try {
      const newTransaction = await api.transactions.create({
        ...transaction,
        userId: user!.id,
      });
      setTransactions([...transactions, newTransaction]);
    } catch (error) {
      console.error("Error adding transaction");
    }
  };

  const handleDeleteTransaction = async (id: string) => {
    try {
      await api.transactions.delete(id);
      setTransactions(transactions.filter((t) => t.id !== id));
    } catch (error) {
      console.error("Error deleting transaction");
    }
  };

  const handleAddCategory = async (category: Omit<Category, "id">) => {
    try {
      const newCategory = await api.categories.create(category);
      setCategories([...categories, newCategory]);
    } catch (error) {
      console.error("Error creating category");
    }
  };

  const handleToggleCategory = async (id: string, isActive: boolean) => {
    try {
      const updated = await api.categories.update(id, { isActive });
      setCategories(categories.map((c) => (c.id === id ? updated : c)));
    } catch (error) {
      console.error("Error updating category");
    }
  };

  const handleDeleteCategory = async (id: string) => {
    try {
      await api.categories.delete(id);
      setCategories(categories.filter((c) => c.id !== id));
    } catch (error) {
      console.error("Error deleting category");
    }
  };

  const calculateStats = (): FinancialStats => {
    const totalIncome = transactions
      .filter((t) => t.type === "income")
      .reduce((sum, t) => sum + t.amount, 0);

    const totalExpenses = transactions
      .filter((t) => t.type === "expense")
      .reduce((sum, t) => sum + t.amount, 0);

    const balance = totalIncome - totalExpenses;
    const savingsRate = totalIncome > 0 ? (balance / totalIncome) * 100 : 0;

    return { totalIncome, totalExpenses, balance, savingsRate };
  };

  if (!user) {
    if (authPage === "login") {
      return (
        <LoginPage
          onLogin={handleLogin}
          onNavigateToRegister={() => setAuthPage("register")}
        />
      );
    } else {
      return (
        <RegisterPage
          onRegister={handleRegister}
          onNavigateToLogin={() => setAuthPage("login")}
        />
      );
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Header */}
      <header className="border-b bg-white/80 backdrop-blur-sm sticky top-0 z-10">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-purple-600 rounded-xl flex items-center justify-center">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  className="text-white"
                >
                  <path d="M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z" />
                </svg>
              </div>
              <div>
                <h1 className="text-xl">FinWise AI</h1>
                <p className="text-sm text-muted-foreground">
                  Привет, {user.name}!
                </p>
              </div>
            </div>
            <Button variant="outline" onClick={handleLogout}>
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
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                <polyline points="16 17 21 12 16 7" />
                <line x1="21" x2="9" y1="12" y2="12" />
              </svg>
              Выйти
            </Button>
          </div>
        </div>
      </header>

      {/* Navigation */}
      <div className="border-b bg-white/80 backdrop-blur-sm">
        <div className="container mx-auto px-4">
          <Tabs
            value={currentPage}
            onValueChange={(value: string) => setCurrentPage(value as Page)}
          >
            <TabsList className="h-12">
              <TabsTrigger value="dashboard" className="gap-2">
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
                  <rect width="7" height="9" x="3" y="3" rx="1" />
                  <rect width="7" height="5" x="14" y="3" rx="1" />
                  <rect width="7" height="9" x="14" y="12" rx="1" />
                  <rect width="7" height="5" x="3" y="16" rx="1" />
                </svg>
                Dashboard
              </TabsTrigger>
              <TabsTrigger value="transactions" className="gap-2">
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
                  <path d="M7 16V4M17 8v12M3 12h4m10-6h4M12 20h4m-8-4h4" />
                </svg>
                Транзакции
              </TabsTrigger>
              <TabsTrigger value="categories" className="gap-2">
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
                  <path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z" />
                  <circle cx="7.5" cy="7.5" r=".5" fill="currentColor" />
                </svg>
                Категории
              </TabsTrigger>
              <TabsTrigger value="ai-recommendations" className="gap-2">
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
                  <path d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5" />
                  <path d="M9 18h6" />
                  <path d="M10 22h4" />
                </svg>
                AI Советы
              </TabsTrigger>
              <TabsTrigger value="wellbeing" className="gap-2">
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
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                </svg>
                Благополучие
              </TabsTrigger>
            </TabsList>
          </Tabs>
        </div>
      </div>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {isLoading ? (
          <div className="flex items-center justify-center h-64">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
              <p className="text-muted-foreground">Загрузка данных...</p>
            </div>
          </div>
        ) : (
          <>
            {currentPage === "dashboard" && (
              <Dashboard
                transactions={transactions}
                categories={categories}
                recommendations={recommendations}
                stats={calculateStats()}
                onNavigateToTransactions={() => setCurrentPage("transactions")}
              />
            )}

            {currentPage === "transactions" && (
              <TransactionsPage
                transactions={transactions}
                categories={categories}
                onAddTransaction={handleAddTransaction}
                onDeleteTransaction={handleDeleteTransaction}
              />
            )}

            {currentPage === "categories" && (
              <CategoryManager
                categories={categories}
                onAddCategory={handleAddCategory}
                onToggleCategory={handleToggleCategory}
                onDeleteCategory={handleDeleteCategory}
              />
            )}

            {currentPage === "ai-recommendations" && (
              <div className="space-y-4">
                <h2>AI Рекомендации по финансам</h2>
                <p className="text-muted-foreground">
                  Персонализированные рекомендации на основе анализа ваших
                  финансов
                </p>
                <AIRecommendations recommendations={recommendations} />
              </div>
            )}
            {currentPage === "wellbeing" && (
              <FinancialWellbeingPage
                transactions={transactions}
                categories={categories}
              />
            )}
          </>
        )}
      </main>
    </div>
  );
}
