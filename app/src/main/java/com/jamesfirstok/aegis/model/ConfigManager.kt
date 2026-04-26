package com.jamesfirstok.aegis.model

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * AEGIS TACTICAL CONFIGURATION MANAGER - SOVEREIGN VERSION
 * تم دمج الوظائف الأساسية مع تشفير عالي المستوى لضمان سرية الترددات
 */
class ConfigManager(context: Context) {

    private val PREFS_NAME = "AegisSovereignConfig_v7"
    private val SATELLITE_LINK_KEY = "sat_link_active"
    private val RECON_FREQUENCY = "recon_freq_khz"

    private val prefs: SharedPreferences

    init {
        // إنشاء مفتاح السيادة المشفر (Master Key)
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // استخدام الوعاء المشفر حصراً لحماية إحداثيات الرصد والترددات
        this.prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // تفعيل الربط مع الأقمار الصناعية (بشكل مشفر)
    fun setSatelliteLink(active: Boolean) {
        prefs.edit().putBoolean(SATELLITE_LINK_KEY, active).apply()
    }

    // تحديث تردد الرصد (SIGINT) لضمان القفز الترددي الآمن
    fun updateFrequency(freq: Int) {
        prefs.edit().putInt(RECON_FREQUENCY, freq).apply()
    }

    // استعادة التردد المخزن مع العودة للتردد الآمن 433MHz عند الفشل
    fun getStoredFrequency(): Int {
        return prefs.getInt(RECON_FREQUENCY, 433000)
    }
}
