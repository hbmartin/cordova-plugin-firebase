# cordova-plugin-firebase-realtime-database
Cordova plugin for Google Firebase Realtime Database
Based on the the [Firebase Notification / FCM plugin](https://github.com/arnesson/cordova-plugin-firebase)


## Support
Please file bugs in [Issues](https://github.com/hbmartin/cordova-plugin-firebase-realtime-database/issues)
Ask for help in #cordova on the [Firebase Community Slack](https://firebase-community.appspot.com)

## Installation
See npm package for versions - https://www.npmjs.com/package/cordova-plugin-firebase-realtime-database

Install the by running:
```
cordova plugin add cordova-plugin-firebase-realtime-database --save
```
Download your Firebase configuration files, GoogleService-Info.plist for iOS and google-services.json for Android, and place them in the root folder of your Cordova project:

```
- My Project/
    platforms/
    plugins/
    www/
    config.xml
    google-services.json       <--
    GoogleService-Info.plist   <--
    ...
```

See https://support.google.com/firebase/answer/7015592 for details how to download the files from firebase.

This plugin uses a hook (after prepare) that copies the configuration files to the right place, namely platforms/ios/\<My Project\>/Resources for iOS and platforms/android for Android.

**Note that the Firebase SDK requires the configuration files to be present and valid, otherwise your app will crash on boot or Firebase features won't work.**

### Notes about PhoneGap Build

Hooks does not work with PhoneGap Build. This means you will have to manually make sure the configuration files are included. One way to do that is to make a private fork of this plugin and replace the placeholder config files (see src/ios and src/android) with your actual ones.

## Methods

### ref

Get a reference to a child path:
```
var firebaseThings = window.FirebaseDatabasePlugin.ref('things');
```
this is the preferred way to use FirebaseDatabasePlugin

### updateChildren

At a particular reference, update the given keys:
```
firebaseThings.updateChildren({
    'thing1' : 'aaa',
    'thing2' : 'bbb'
});
```
returns Promise

### setValue

At a particular reference, set the given value:
```
firebaseThings.child('thing1').setValue('ccc');
```
returns Promise

### setDatabasePersistent

Choose whether data should be persisted on disk, i.e. between app relaunches
```
window.FirebaseDatabasePlugin.setDatabasePersistent(true);
```

### signInWithEmailAndPassword

Choose whether data should be persisted on disk, i.e. between app relaunches
```
window.FirebaseDatabasePlugin.signInWithEmailAndPassword(email, password);
```
returns Promise
