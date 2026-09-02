package com.steptrack.pro.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

/**
 * Many OEMs (Samsung, Xiaomi, Huawei, OnePlus, etc.) apply aggressive battery
 * management on top of stock Android's Doze/App Standby, and will kill a
 * foreground service anyway unless the user explicitly whitelists the app.
 * This is the #1 real-world cause of "steps stopped counting overnight"
 * complaints for fitness apps, so we surface an explicit opt-out request
 * rather than relying on the foreground-service flag alone.
 */
object BatteryOptimizationUtils {

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    /**
     * Launches the system dialog that lets the user directly grant the
     * exemption for this app (no extra permission declaration needed for
     * this specific intent action on API 23+).
     */
    fun requestIgnoreBatteryOptimizations(context: Context) {
        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    }

    /** Fallback: opens the general battery-optimization settings list if the
     *  direct-request intent isn't handled on a given OEM skin. */
    fun openBatteryOptimizationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
    }
}
