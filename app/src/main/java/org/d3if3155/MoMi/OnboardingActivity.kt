package org.d3if3155.MoMi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.coroutines.launch
import org.d3if3155.MoMi.data.SettingDataStore
import org.d3if3155.MoMi.data.dataStore
import org.d3if3155.MoMi.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private val layoutDataStore by lazy { SettingDataStore(this.dataStore) }

    private lateinit var binding: ActivityOnboardingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentMainActivity = Intent(this, MainActivity::class.java)

        lifecycleScope.launch {
            layoutDataStore.isFirstTime.collect {
                if (!it) {
                    startActivity(intentMainActivity)
                    finish()
                }
            }
        }



    }
}