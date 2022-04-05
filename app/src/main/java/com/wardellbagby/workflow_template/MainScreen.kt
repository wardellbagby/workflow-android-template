package com.wardellbagby.workflow_template

import android.view.Gravity
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import com.google.android.material.button.MaterialButton
import com.squareup.workflow1.ui.AndroidViewRendering
import com.squareup.workflow1.ui.BuilderViewFactory
import com.squareup.workflow1.ui.bindShowRendering
import com.squareup.workflow1.ui.compose.composeViewFactory

data class ComposeScreen(
  val onClick: () -> Unit
) : AndroidViewRendering<ComposeScreen> {
  override val viewFactory = composeViewFactory<ComposeScreen> { rendering, _ ->
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Button(onClick = rendering.onClick) {
        Text("Hello from Compose-land!")
      }
    }
  }
}

data class ViewScreen(
  val onClick: () -> Unit
) : AndroidViewRendering<ViewScreen> {
  override val viewFactory =
    BuilderViewFactory(type = ViewScreen::class) { initialRendering, initialViewEnvironment, contextForNewView, container ->
      val button = MaterialButton(contextForNewView).apply {
        text = "Hello from View-land!"
      }
      LinearLayout(contextForNewView).apply {
        gravity = Gravity.CENTER

        addView(button)

        fun update(rendering: ViewScreen) {
          button.setOnClickListener { rendering.onClick() }
        }

        bindShowRendering(initialRendering, initialViewEnvironment) { rendering, _ ->
          update(rendering)
        }
      }
    }
}
