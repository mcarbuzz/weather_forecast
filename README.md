# Weather Forecast

<div align="center">

**Умное и красивое приложение для отслеживания погоды с поддержкой избранных городов**

</div>

<div align="center">
  <img src="https://github.com/user-attachments/assets/e11c6280-68b9-47a0-8569-be6b30e9ad4e" width="200" alt="Главный экран"/>
  <img src="https://github.com/user-attachments/assets/86c86b71-a5e8-44f4-bb7e-9cfba1fb80bc" width="200" alt="Прогноз на несколько дней"/>
  <img src="https://github.com/user-attachments/assets/2615a64f-1a2e-49bd-a0f8-996553e80109" width="200" alt="Избранные города"/>
  <img src="https://github.com/user-attachments/assets/d2833afa-1a1d-4059-8782-88dfacf3ab36" width="200" alt="Поиск городов"/>
</div>

## О проекте

Weather Forecast — современное Android-приложение для просмотра актуальной погоды и прогноза на несколько дней. Приложение предлагает интуитивно понятный интерфейс, персонализацию и удобное управление избранными локациями.

## Возможности

### **Текущая погода**
- Отображение температуры, влажности, скорости ветра и атмосферного давления
- Иконки погодных условий с анимацией
- Подробное описание текущих погодных условий
- Индекс UV и видимость

<div align="center">
  <img src="https://github.com/user-attachments/assets/e11c6280-68b9-47a0-8569-be6b30e9ad4e" width="300" alt="Текущая погода"/>
</div>

### **Подробный прогноз**
- Почасовой прогноз на 24 часа
- Подробный прогноз на 7 дней
- Графики изменения температуры
- Вероятность осадков

<div align="center">
  <img src="https://github.com/user-attachments/assets/86c86b71-a5e8-44f4-bb7e-9cfba1fb80bc" width="300" alt="Прогноз"/>
</div>

### **Избранные города**
- Быстрый доступ к погоде в избранных локациях
- Управление списком городов
- Автоматическое обновление данных

<div align="center">
  <img src="https://github.com/user-attachments/assets/2615a64f-1a2e-49bd-a0f8-996553e80109" width="300" alt="Избранные города"/>
</div>

### **Умный поиск**
- Поиск городов по названию
- Автодополнение при вводе
- Добавление найденных городов в избранное

<div align="center">
  <img src="https://github.com/user-attachments/assets/d2833afa-1a1d-4059-8782-88dfacf3ab36" width="300" alt="Поиск городов"/>
</div>

### **Настройки**
- Выбор единиц измерения (ºC/ºF, км/ч/м/с)
- Настройка интервала обновления
- Тема оформления (светлая/тёмная/авто)
- Уведомления о погодных изменениях

<div align="center">
  <img src="https://github.com/user-attachments/assets/26488bbf-075b-4b30-a590-8592375d6a8d" width="300" alt="Настройки"/>
</div>

## Технологии

### **Языки и фреймворки**
- **Kotlin** — основной язык разработки
- **Jetpack Compose** — современный UI toolkit

### **Архитектура**
- **MVVM (Model-View-ViewModel)** — архитектурный паттерн
- **Repository Pattern** — работа с данными
- **Clean Architecture** — разделение ответственности

### **Библиотеки**
- **Retrofit + Gson** — сетевые запросы и парсинг JSON
- **Room Database** — локальное хранение избранных городов
- **Coroutines + Flow** — асинхронные операции
- **Dagger Hilt** — внедрение зависимостей
- **Coil** — загрузка и кэширование изображений
- **Material Design 3** — современный дизайн

### **API**
- **OpenWeatherMap API** — получение данных о погоде
- **Geocoding API** — поиск и геолокация городов
