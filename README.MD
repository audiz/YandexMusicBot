+ Procfile, в котором устанавливается тип процесса (worker, web  и т.п.) и команда для запуска приложения
+ system.properties, в котором нужно указать версию Java, если она отлична от 8
```
mvn clean install

//предварительно зарегистрироваться на heroku
heroku login
heroku create <имя приложения>
git push heroku master

//добавить имя и токен бота (получены от BotFather) в переменные окружения
heroku config:set BOT_NAME=<имя бота>
heroku config:set BOT_TOKEN=<токен бота>

//удостовериться, что переменные окружения установлены
heroku config:get BOT_NAME
heroku config:get BOT_TOKEN

//установить количество контейнеров (dynos) для типа процесса worker (тип устанавливается в Procfile)
heroku ps:scale worker=1

// если нужно посмотреть запущенные журналы
heroku logs --tail
```

