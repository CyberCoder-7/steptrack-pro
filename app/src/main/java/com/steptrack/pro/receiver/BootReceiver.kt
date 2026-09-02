package com.steptrack.pro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.steptrack.pro.data.service.StepCounterService
import dagger.hilt.android.AndroidEntryPoint

/**
 * Restarts step tracking after a device reboot, since:
 *  1. Sensor.TYPE_STEP_COUNTER resets to 0 on reboot (handled by baseline
 *     rollover logic in StepSensorManager), and
 *  2. The foreground service itself does not survive a reboot and must be
 *     explicitly restarted.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val serviceIntent = Intent(context, StepCounterService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
