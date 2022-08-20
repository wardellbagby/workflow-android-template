package com.wardellbagby.workflow_template

import android.view.Gravity
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.google.android.material.button.MaterialButton
import com.squareup.workflow1.ui.AndroidScreen
import com.squareup.workflow1.ui.ScreenViewFactory
import com.squareup.workflow1.ui.ScreenViewHolder
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.compose.ComposeScreen

// These screens are left here as an example; real screens should be self-contained in their own
// files, named based on the feature they are for, and scoped to the same package as their Workflow.
// AppWorkflow likely shouldn't render any screens directly either; just delegate to other
// Workflows. Use these screens as guidelines (minus the hard-coded strings; use string resources!)
// but delete them once there's not needed!

data class HelloComposeScreen(
  val onClick: () -> Unit
) : ComposeScreen {
  @Composable
  override fun Content(viewEnvironment: ViewEnvironment) {
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(onClick = onClick) {
        Text("Hello from Compose-land!")
      }
    }
  }
}

data class HelloViewScreen(
  val onClick: () -> Unit
) : AndroidScreen<HelloViewScreen> {
  override val viewFactory: ScreenViewFactory<HelloViewScreen> =
    ScreenViewFactory.fromCode { _, initialViewEnvironment, contextForNewView, _ ->
      val button = MaterialButton(contextForNewView).apply {
        text = "Hello from View-land!"
      }
      val view = LinearLayout(contextForNewView).apply {
        gravity = Gravity.CENTER

        addView(button)
      }

      ScreenViewHolder(initialViewEnvironment, view) { rendering: HelloViewScreen, _ ->
        button.setOnClickListener { rendering.onClick() }
      }
    }
}
