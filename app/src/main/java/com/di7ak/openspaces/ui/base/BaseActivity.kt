package com.di7ak.openspaces.ui.base

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.di7ak.openspaces.R
import com.di7ak.openspaces.utils.ThemeColors

open class BaseActivity : AppCompatActivity() {
    companion object {
        private const val DARK_THEME = "dark_theme"
    }
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getPreferences(MODE_PRIVATE)
        val enabled = prefs.getBoolean(DARK_THEME, false)
        val config = baseContext.resources.configuration
        val isDark = config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        if(enabled != isDark) changeTheme(enabled)

        ThemeColors(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.changeTheme) {
            val config = baseContext.resources.configuration
            val isDark = config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            changeTheme(!isDark)
        }
        return super.onOptionsItemSelected(item)
    }

    fun changeTheme(dark: Boolean) {
        prefs.edit().putBoolean(DARK_THEME, dark).apply()
        val mode = if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        Toast.makeText(this, R.string.restart_to_apply_changes, Toast.LENGTH_SHORT).show()
    }
}