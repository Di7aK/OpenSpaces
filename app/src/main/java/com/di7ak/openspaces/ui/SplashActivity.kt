package com.di7ak.openspaces.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.di7ak.openspaces.R
import com.di7ak.openspaces.ui.base.BaseActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fetchParameters()
    }

    private fun fetchParameters() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.default_config)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 30//3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, R.string.error_loading_configuration_file, Toast.LENGTH_SHORT).show()
                }
                finish()
                overridePendingTransition(0, 0)
            }
    }
}