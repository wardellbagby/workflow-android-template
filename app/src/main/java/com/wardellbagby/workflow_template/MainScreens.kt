package com.wardellbagby.workflow_template

import android.view.Gravity
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.google.android.material.button.MaterialButton
import com.squareup.workflow1.ui.AndroidViewRendering
import com.squareup.workflow1.ui.BuilderViewFactory
import com.squareup.workflow1.ui.bindShowRendering
import com.squareup.workflow1.ui.compose.composeViewFactory
import com.wardellbagby.workflow_template.theming.AppTheme

// These screens are left here as an example; real screens should be self-contained in their own
// files, named based on the feature they are for, and scoped to the same package as their Workflow.
// MainWorkflow likely shouldn't render any screens directly either; just delegate to other
// Workflows. Use these screens as guidelines (minus the hard-coded strings; use string resources!)
// but delete them once there's not needed!

data class ComposeScreen(
  val onClick: () -> Unit
) : AndroidViewRendering<ComposeScreen> {
  override val viewFactory = composeViewFactory<ComposeScreen> { rendering, _ ->
    // TODO(wardell): Once the Workflow library has been updated so that [withCompositionRoot]
    // doesn't stop [AndroidViewRendering]s from working correctly, this should be removed and
    // replaced with withCompositionRoot.
    // https://github.com/square/workflow-kotlin/issues/698 (The ray/ui-update branch needs to be
    // merged into main)
    AppTheme {
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
}

data class ViewScreen(
  val onClick: () -> Unit
) : AndroidViewRendering<ViewScreen> {
  override val viewFactory =
    BuilderViewFactory(
      type = ViewScreen::class
    ) { initialRendering, initialViewEnvironment, contextForNewView, _ ->
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
