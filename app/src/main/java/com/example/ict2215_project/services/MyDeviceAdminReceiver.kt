package com.example.ict2215_project.services

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.os.UserHandle
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import android.os.Bundle

import com.example.ict2215_project.MainActivity
import com.example.ict2215_project.R

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    // Currently not in used 
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        val manager = context.getSystemService(ComponentActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(context.applicationContext, MyDeviceAdminReceiver::class.java)

        manager.setProfileName(componentName, context.getString(R.string.profile_name))

        val intent = Intent(context, MainActivity::class.java)

        // intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, componentName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Log.e("action", intent.action.toString())
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d("DeviceOwnerCheck", "App is device owner!")
    }
}