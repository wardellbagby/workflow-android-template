package com.wardellbagby.workflow_template

import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.squareup.workflow1.ui.AndroidScreen
import com.squareup.workflow1.ui.ScreenViewFactory
import com.squareup.workflow1.ui.ScreenViewHolder
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.compose.ComposeScreen
import com.wardellbagby.workflow_template.databinding.HelloViewBinding

// These screens are left here as an example; real screens should be self-contained in their own
// files, named based on the feature they are for, and scoped to the same package as their Workflow.
// AppWorkflow likely shouldn't render any screens directly either; just delegate to other
// Workflows. Use these screens as guidelines (minus the hard-coded strings; use string resources!)
// but delete them once there's not needed!

data class HelloComposeScreen(
  val remainingTime: String,
  val onClick: () -> Unit
) : ComposeScreen {
  @OptIn(ExperimentalUnitApi::class)
  @Composable
  override fun Content(viewEnvironment: ViewEnvironment) {
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(onClick = onClick) {
        Text("Hello from Compose-land!")
      }
      Text(
        text = "Automatically transitioning in $remainingTime",
        fontSize = TextUnit(14f, TextUnitType.Sp)
      )
    }
  }
}

data class HelloViewScreen(
  val remainingTime: String,
  val onClick: () -> Unit
) : AndroidScreen<HelloViewScreen> {
  override val viewFactory: ScreenViewFactory<HelloViewScreen> =
    ScreenViewFactory.fromCode { _, initialViewEnvironment, contextForNewView, _ ->
      val button = MaterialButton(contextForNewView).apply {
        text = "Hello from View-land!"
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
      }
      val label = MaterialTextView(contextForNewView).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
      }
      val view = LinearLayout(contextForNewView).apply {
        gravity = Gravity.CENTER
        orientation = LinearLayout.VERTICAL

        addView(button)
        addView(label)
      }

      ScreenViewHolder(initialViewEnvironment, view) { rendering: HelloViewScreen, _ ->
        button.setOnClickListener { rendering.onClick() }
        label.text = "Automatically transitioning in ${rendering.remainingTime}"
      }
    }
}

data class HelloViewBindingScreen(
  val remainingTime: String,
  val onClick: () -> Unit
) : AndroidScreen<HelloViewBindingScreen> {
  override val viewFactory: ScreenViewFactory<HelloViewBindingScreen> =
    ScreenViewFactory.fromViewBinding(HelloViewBinding::inflate) { rendering, _ ->
      viewButton.setOnClickListener { rendering.onClick() }
      viewLabel.text = "Automatically transitioning in ${rendering.remainingTime}"
    }
}
