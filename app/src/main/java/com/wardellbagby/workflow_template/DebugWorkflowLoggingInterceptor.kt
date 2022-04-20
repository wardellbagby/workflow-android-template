package com.wardellbagby.workflow_template

import android.util.Log
import com.squareup.workflow1.SimpleLoggingWorkflowInterceptor

object DebugWorkflowLoggingInterceptor : SimpleLoggingWorkflowInterceptor() {
  override fun log(text: String) {
    if (BuildConfig.DEBUG) {
      Log.v("Workflow Breadcrumb", text)
    }
  }
}
