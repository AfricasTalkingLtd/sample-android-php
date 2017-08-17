# Android + PHP + AfricasTalking

> Simple demo setting up SMS and Airtime from an Android app using a PHP backend and Africa's Talking API


**Start the backend server**

```shell
# set your app username and api key in backend/index.php
cd backend
php -S 192.168.0.15:3000
```

**Start and run the android app**

```shell
# add backendUrl=192.168.0.15:3000 to your local.properties
cd android
gradle installDebug
```

**And Voilà!**

<img src="android/screenshot.png" width="25%">
