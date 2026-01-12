package de.diskostu.spoolstack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.diskostu.spoolstack.data.FilamentRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SpoolstackApplication : Application() {

    @Inject
    lateinit var filamentRepository: FilamentRepository

    override fun onCreate() {
        super.onCreate()

        // Skip initialization if in Robolectric test environment
        if (System.getProperty("robolectric.enabled") != "true") {
            MainScope().launch {
                filamentRepository.initializeColors(InitialColors.initialColors)
            }
        }
    }
}
