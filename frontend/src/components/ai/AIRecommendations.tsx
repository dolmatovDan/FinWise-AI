import { AIRecommendation } from "../../types";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";
import { Badge } from "../ui/badge";

/**
 * Иконки рекомендаций по категориям.
 * Используются SVG-иконки из Lucide или аналогичного набора.
 */
const getIconByCategory = (category: string) => {
  const iconProps = {
    width: "20",
    height: "20",
    "stroke-width": "2",
    "stroke-linecap": "round",
    "stroke-linejoin": "round",
  };

  switch (category) {
    case "saving":
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          {...iconProps}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path d="M19 5c-1.5 0-2.8 1.4-3 2-3.5-1.5-11-.3-11 5 0 1.8 0 3 2 4.5V20h4v-2h3v2h4v-4c1-.5 1.7-1 2-2h2v-4h-2c0-1-.5-1.5-1-2V5z" />
          <path d="M2 9v1c0 1.1.9 2 2 2h1" />
          <path d="M16 11h.01" />
        </svg>
      );
    case "investment":
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          {...iconProps}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path d="m22 7-8.5 8.5a1.414 1.414 0 0 1-2 0L8 12l-5 5" />
          <path d="M16 7h6v6" />
        </svg>
      );
    case "budget":
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          {...iconProps}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <circle cx="12" cy="12" r="10" />
          <path d="M12 8v4" />
          <path d="M12 16h.01" />
        </svg>
      );
    default:
      return (
        <svg
          xmlns="http://www.w3.org/2000/svg"
          {...iconProps}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5" />
          <path d="M9 18h6" />
          <path d="M10 22h4" />
        </svg>
      );
  }
};

/**
 * Возвращает CSS-классы для цветовой индикации приоритета.
 */
const getPriorityBadgeClass = (priority: string): string => {
  switch (priority) {
    case "high":
      return "bg-red-100 text-red-800 border-red-200";
    case "medium":
      return "bg-yellow-100 text-yellow-800 border-yellow-200";
    case "low":
      return "bg-green-100 text-green-800 border-green-200";
    default:
      return "bg-gray-100 text-gray-800 border-gray-200";
  }
};

/**
 * Возвращает человекочитаемую строку для уровня приоритета.
 */
const getPriorityLabel = (priority: string): string => {
  switch (priority) {
    case "high":
      return "Высокий приоритет";
    case "medium":
      return "Средний приоритет";
    case "low":
      return "Низкий приоритет";
    default:
      return "Неизвестный приоритет";
  }
};

/**
 * Форматирует число как сумму в рублях (без копеек).
 */
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("ru-RU", {
    style: "currency",
    currency: "RUB",
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(value);
};

/**
 * Компонент отображения списка рекомендаций от ИИ.
 * Каждая карточка — отдельная рекомендация с иконкой, приоритетом и потенциальной экономией.
 */
export function AIRecommendations({
  recommendations,
}: {
  recommendations: AIRecommendation[];
}) {
  if (recommendations.length === 0) {
    return <p className="text-muted-foreground">Нет рекомендаций</p>;
  }

  return (
    <div className="space-y-4">
      {recommendations.map((rec) => (
        <Card
          key={rec.id}
          className="border-l-4"
          style={{
            borderLeftColor:
              rec.priority === "high"
                ? "hsl(0, 85%, 65%)" // red-500
                : rec.priority === "medium"
                ? "hsl(39, 95%, 51%)" // yellow-500
                : "hsl(124, 71%, 40%)", // green-500
          }}
        >
          <CardHeader>
            <div className="flex items-start justify-between gap-4">
              {/* Иконка категории */}
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-500 rounded-lg flex items-center justify-center text-white">
                  {getIconByCategory(rec.category)}
                </div>

                {/* Основной контент: заголовок и бейджи */}
                <div>
                  <CardTitle className="text-lg">{rec.title}</CardTitle>
                  <div className="flex items-center gap-2 mt-2 flex-wrap">
                    <Badge
                      variant="outline"
                      className={getPriorityBadgeClass(rec.priority)}
                    >
                      {getPriorityLabel(rec.priority)}
                    </Badge>
                    {rec.potentialSavings && (
                      <Badge
                        variant="outline"
                        className="bg-green-50 text-green-700 border-green-200"
                      >
                        Экономия: {formatCurrency(rec.potentialSavings)}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <p className="text-muted-foreground">{rec.description}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
