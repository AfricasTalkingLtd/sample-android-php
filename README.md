# Android + PHP + AfricasTalking

> Simple demo setting up SMS and Airtime from and Android app using a PHP backend and Africa's Talking API


**Start the backend server**

```shell
cd backend
php -S 192.168.0.15:3000
```

**Start and run the android app**

```shell
# add backendUrl=192.168.0.15:3000 to your local.properties
cd android
gradle installDebug
```

