# Setup for MacOS (VScode)

## Requirements

    1. Android Studio (download other components and device management only)
    2. VSCode + avdmanager extension
    3. Java SDK
    4. Kotlin + vscode extension

## Guide

Follow [this guide](https://github.com/toroxx/vscode-avdmanager/blob/main/README-Setup.md)

Use [avdmanager guide](https://github.com/toroxx/vscode-avdmanager) to create AVDs and execute from VSCode

`export ANDROID_HOME=/<installation location>/android-sdk` to set android sdk environment path 

## Other Information

Not all android devices can be access root shell via `adb root` as stated [here](https://stackoverflow.com/questions/43923996/adb-root-is-not-working-on-emulator-cannot-run-as-root-in-production-builds).
Avoid using any system images that has Google playstore. Google API is ok.

Tested and can have root shell versions:
    
    - Android Tiramisu Google APIs ARM64 v8a

Also, ensure API level is 25 or higher to have simulated WiFi [stated here](https://stackoverflow.com/questions/7876302/enabling-wifi-on-android-emulator)

[This guide](https://www.vogella.com/tutorials/AndroidBuild/article.html) shows how to use gradle to build and test android applications