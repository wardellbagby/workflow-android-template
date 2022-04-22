package com.wardellbagby.workflow_template

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DefaultApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    DynamicColors.applyToActivitiesIfAvailable(this)
  }
}
