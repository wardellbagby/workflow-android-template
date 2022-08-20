package com.wardellbagby.workflow_template

import android.os.Parcelable
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.container.BodyAndOverlaysScreen
import com.squareup.workflow1.ui.container.Overlay
import com.squareup.workflow1.ui.toParcelable
import com.squareup.workflow1.ui.toSnapshot
import com.wardellbagby.workflow_template.AppWorkflow.State
import com.wardellbagby.workflow_template.AppWorkflow.State.ViewingComposeScreen
import com.wardellbagby.workflow_template.AppWorkflow.State.ViewingViewScreen
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

typealias AppRendering = BodyAndOverlaysScreen<Screen, Overlay>

/**
 * The root Workflow that this app runs.
 *
 * The state that this Workflow uses can be thought of as the app's state. All of your major screen
 * transitions should happen here. For instance, if your app would have a settings screen, there
 * should likely be a new `ViewingSettings` state added here that will run a new `SettingsWorkflow`
 * that you'd handle.
 */
class AppWorkflow
@Inject constructor() : StatefulWorkflow<Unit, State, Nothing, AppRendering>() {

  sealed class State : Parcelable {
    @Parcelize
    object ViewingComposeScreen : State()

    @Parcelize
    object ViewingViewScreen : State()
  }

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = snapshot?.toParcelable() ?: ViewingComposeScreen

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): AppRendering {

    return when (renderState) {
      is ViewingComposeScreen -> HelloComposeScreen(
        onClick = context.eventHandler {
          state = ViewingViewScreen
        }
      )
      is ViewingViewScreen -> HelloViewScreen(
        onClick = context.eventHandler {
          state = ViewingComposeScreen
        }
      )
    }.let { AppRendering(it) }
  }

  override fun snapshotState(state: State): Snapshot {
    return state.toSnapshot()
  }
}
