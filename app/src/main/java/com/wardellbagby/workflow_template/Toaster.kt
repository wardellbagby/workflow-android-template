package com.wardellbagby.workflow_template

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Toaster
@Inject constructor(@ApplicationContext private val context: Context) {
  fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }
}
