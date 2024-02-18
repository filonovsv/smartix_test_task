# Тестовое задание для соискателя на должность Junior Java Spring Developer
Перед запуском требуется изменить параметры авторизации в базе данных (resources/application.properties).
## Документация API
### Создать пользователя
#### request:
```
POST /create
```
body:
```
{
    "phone": string,
    "password": string,
    "name": string, (optional)
    "surname": string, (optional)
    "patronymic": string, (optional)
    "gender": string("male"/"female"), (optional)
    "email": string, (optional)
    "birthday": string($date-time) (optional)
}

```
### Изменить пользователя
#### request:
```
POST /update

...
Authorization: Basic base64_encode(login:password)
...
```
body:
```
{
    "phone": string, (optional)
    "password": string, (optional)
    "name": string, (optional)
    "surname": string, (optional)
    "patronymic": string, (optional)
    "gender": string("male"/"female"), (optional)
    "email": string, (optional)
    "birthday": string($date-time) (optional)
}
```
### Получить полную информацию о пользователе
#### request:
```
GET /me

...
Authorization: Basic base64_encode(login:password)
...
```
#### response:
```
{ 
    "id": integer($int64), 
    "phone": string, 
    "password": string, 
    "rubles": integer($int64),
    "copecks": integer($int64), 
    "name": string, 
    "surname": string, 
    "patronymic": string,
    "gender": string("male"/"female"),
    "email": string,
    "birthday": string($date-time) 
}
```
### Узнать баланс
#### request:
```
GET /balance

...
Authorization: Basic base64_encode(login:password)
...
```
#### response:
```
{
    "phone": string
    "rubles": integer($int64),
    "copecks": integer($int64)
}
```
### Выполнить перевод
#### request:
```
POST /pay

...
Authorization: Basic base64_encode(login:password)
...
```
body:
```
{
    "receiver": string
    "rubles": integer($int64)
    "copecks": integer($int64)
}
```
### Получить историю переводов
Для получения полной истории:
#### request:
```
GET /history

...
Authorization: Basic base64_encode(login:password)
...
```
Для получения истории постранично:
#### request:
```
GET /history?page={page_number}&size={page_size}

...
Authorization: Basic base64_encode(login:password)
...
```
#### response:
```
[
    {
        "id": integer($int64),
        "receiver": string,
        "rubles": integer($int64),
        "copecks": integer($int64),
        "date": string($date-time)
    },
    ...
]
```
