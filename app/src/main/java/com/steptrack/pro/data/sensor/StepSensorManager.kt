package com.steptrack.pro.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.steptrack.pro.data.datastore.PreferencesManager
import com.steptrack.pro.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed class SensorAvailability {
    object Available : SensorAvailability()
    object StepCounterUnavailable : SensorAvailability()
    object PermissionDenied : SensorAvailability()
}

/**
 * Wraps the Android sensor framework for step tracking.
 *
 * Key design decisions:
 * - TYPE_STEP_COUNTER is preferred: it's a low-power hardware counter that
 *   reports the total steps since the last device boot. It never resets on
 *   its own, so we persist a "baseline" (the hardware value at the start of
 *   today) and compute todaySteps = latestHardwareValue - baseline.
 * - TYPE_STEP_DETECTOR is used as a secondary/fallback signal for devices
 *   where the counter is unreliable, and to provide a live "step just
 *   happened" pulse for smoother UI animation.
 * - Device reboot resets the hardware counter to 0. We detect this because
 *   a fresh reading lower than our stored baseline means a reboot occurred;
 *   in that case, we roll today's accumulated steps forward and re-baseline
 *   at 0 rather than losing progress.
 */
@Singleton
class StepSensorManager @Inject constructor(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) : SensorEventListener {

    private val sensorManager: SensorManager? =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val stepCounterSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val stepDetectorSensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

    private val _availability = MutableStateFlow<SensorAvailability>(SensorAvailability.Available)
    val availability: StateFlow<SensorAvailability> = _availability.asStateFlow()

    private var baselineHardwareValue: Int? = null
    private var baselineDate: String? = null
    private var accumulatedBeforeReboot = 0

    fun isStepCounterAvailable(): Boolean = stepCounterSensor != null

    fun start() {
        if (stepCounterSensor == null && stepDetectorSensor == null) {
            _availability.value = SensorAvailability.StepCounterUnavailable
            return
        }
        scope.launch {
            val stored = preferencesManager.getStepBaseline()
            val today = DateUtils.today()
            if (stored != null && stored.second == today) {
                baselineHardwareValue = stored.first
                baselineDate = today
            } else {
                // New day or first run: baseline will be set on first sensor event.
                baselineDate = today
                accumulatedBeforeReboot = 0
            }
        }

        stepCounterSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        stepDetectorSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> handleStepCounterEvent(event.values[0].toInt())
            Sensor.TYPE_STEP_DETECTOR -> {
                // Each event = exactly one step detected; used as a fallback/live pulse.
                if (stepCounterSensor == null) {
                    _todaySteps.value = _todaySteps.value + 1
                }
            }
        }
    }

    private fun handleStepCounterEvent(hardwareValue: Int) {
        val today = DateUtils.today()

        // Day rolled over while the service kept running.
        if (baselineDate != today) {
            baselineDate = today
            baselineHardwareValue = hardwareValue
            accumulatedBeforeReboot = 0
            _todaySteps.value = 0
            persistBaseline(hardwareValue, today)
            return
        }

        val baseline = baselineHardwareValue
        if (baseline == null) {
            baselineHardwareValue = hardwareValue
            persistBaseline(hardwareValue, today)
            _todaySteps.value = accumulatedBeforeReboot
            return
        }

        if (hardwareValue < baseline) {
            // Device rebooted: hardware counter restarted from 0.
            // Preserve steps already counted today, then re-baseline at the new value.
            accumulatedBeforeReboot = _todaySteps.value
            baselineHardwareValue = hardwareValue
            persistBaseline(hardwareValue, today)
            return
        }

        val delta = hardwareValue - baseline
        _todaySteps.value = accumulatedBeforeReboot + delta
    }

    private fun persistBaseline(hardwareValue: Int, date: String) {
        scope.launch { preferencesManager.setStepBaseline(hardwareValue, date) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op: step counter/detector accuracy changes don't require handling here.
    }
}
