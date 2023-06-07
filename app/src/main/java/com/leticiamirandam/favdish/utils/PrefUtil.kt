package com.leticiamirandam.favdish.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.leticiamirandam.favdish.presentation.MainActivity

object PrefUtil {

    private const val TIMER_STATE_ID = "com.leticiamirandam.favdish.timer_state"
    private const val ALARM_SET_TIME_ID = "com.leticiamirandam.favdish.backgrounded_time"
    private const val SECONDS_REMAINING_ID = "com.leticiamirandam.favdish.seconds_remaining"
    private const val TIMER_LENGTH_ID = "com.leticiamirandam.favdish.timer_length"
    private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID =
        "com.leticiamirandam.favdish.previous_timer_length_seconds"

    fun setTimerLength(context: Context, timeInMinutes: Int) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putInt(TIMER_LENGTH_ID, timeInMinutes)
        editor.apply()
    }

    fun getTimerLength(context: Context): Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(TIMER_LENGTH_ID, 1)
    }

    fun getPreviousTimerLengthSeconds(context: Context): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
    }

    fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
        editor.apply()
    }

    fun getTimerState(context: Context): MainActivity.TimerState {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
        return MainActivity.TimerState.values()[ordinal]
    }

    fun setTimerState(state: MainActivity.TimerState, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        val ordinal = state.ordinal
        editor.putInt(TIMER_STATE_ID, ordinal)
        editor.apply()
    }

    fun getAlarmSetTime(context: Context): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getLong(ALARM_SET_TIME_ID, 0)
    }

    fun setAlarmSetTime(time: Long, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(ALARM_SET_TIME_ID, time)
        editor.apply()
    }

    fun getSecondsRemaining(context: Context): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getLong(SECONDS_REMAINING_ID, 0)
    }

    fun setSecondsRemaining(seconds: Long, context: Context) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putLong(SECONDS_REMAINING_ID, seconds)
        editor.apply()
    }
}