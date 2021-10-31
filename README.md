# ates
Awesome Task Exchange System (aTES)


<details>
  <summary>Flow Chart v1.0</summary>

## Flow chart

![Flowchart](img/ates-v1.jpg)
### Сервисы
Выбор сервисов естественно пришел из требований.

### Проблемы и отказоустойчивость
- На данный момент отказоустойчивость обеспечивается благодаря использованию брокеров сообщений при коммуникации между сервисами.
- Приложение должно работать до тех пор, пока сам сервис, его база и брокер доступны. Если зависимый сервис упадет, данные не должны потеряться.

### Спорные моменты
- Сейчас модуль Аналитики частично дублирует данные Аккаунтинга. Неочевиден учет переходящего отрицательного баланса.
- Создание новых пользователей не отображено на схеме

</details>


## Event Storming
![Event Storming](img/EventStorming.jpg)

## Data Model
![Data Model](img/DataModel.jpg)

## Domain Model
![DomainModel](img/DomainModel.jpg)

## Communication
*Note, login itself is out of scope.*
![Communication](img/Communication.jpg)

## Events  
![Events](img/Events.jpg)
