package com.example.quotify.Handler

import android.os.Build

class DeviceIdHandler {
    companion object {
        fun getToken(): String {
            val deviceInfo = StringBuilder()
            deviceInfo.append("Brand: ${Build.BRAND}\n")
            deviceInfo.append("Device: ${Build.DEVICE}\n")
            deviceInfo.append("Model: ${Build.MODEL}\n")
            deviceInfo.append("Manufacturer: ${Build.MANUFACTURER}\n")
            return deviceInfo.toString()
        }
    }


}