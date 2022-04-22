package com.wardellbagby.workflow_template

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides instances of the currently running [Activity], if one is available.
 *
 * This should be used for instances where an [Activity] is needed, such as launching an intent. For
 * other cases, prefer injecting the application context by putting a context in your injectable
 * constructor with the [dagger.hilt.android.qualifiers.ApplicationContext] annotation.
 *
 * ```
 * @Inject constructor(
 *   @ApplicationContext val context: Context
 * )
 * ```
 *
 * This is preferred over using Hilt's [dagger.hilt.android.qualifiers.ActivityContext] annotation
 * to inject an Activity as Hilt requires that anything that wants to inject an Activity be scoped
 * to at least [dagger.hilt.android.scopes.ActivityScoped], but Workflows tend to run in the
 * [dagger.hilt.android.scopes.ViewModelScoped] so that they aren't lost due to configuration
 * changes. This gives us a way around that, with the downside that it's possible that [activity]
 * might be null when accessed, and so needs to be handled.
 */
@Singleton
class ActivityProvider
@Inject constructor() : DefaultLifecycleObserver {
  var activity: Activity? = null

  override fun onCreate(owner: LifecycleOwner) {
    activity =
      owner as? Activity ?: error("Owner isn't an Activity; it's a ${owner.javaClass.simpleName}!")
  }

  override fun onDestroy(owner: LifecycleOwner) {
    activity = null
  }
}
