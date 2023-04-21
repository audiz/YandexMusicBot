### Service for OrangePi_i96 board

Service file name and location:
```
/etc/systemd/system/telegram-bot.service
```

Command to check service
```
sudo service telegram-bot status
```

The **wrapper** script runs the jar app.

**yandex_cookies.env** contains Yandex Music cookies in rhe next format:

```
YANDEX_COOKIE="Session_id=CUTCUTCUT"
```
yandex_cookies.env must be accessible for a service script and the app user have access to write to it.

Using bot command you can update the file and set new cookies:
```
/login Session_id=CUTCUTCUT
```