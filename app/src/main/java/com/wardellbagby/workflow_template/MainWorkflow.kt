package com.wardellbagby.workflow_template

import android.os.Parcelable
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.ui.toParcelable
import com.squareup.workflow1.ui.toSnapshot
import com.wardellbagby.workflow_template.MainWorkflow.State
import com.wardellbagby.workflow_template.MainWorkflow.State.ViewingComposeScreen
import com.wardellbagby.workflow_template.MainWorkflow.State.ViewingViewScreen
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * The root Workflow that this app runs.
 *
 * The state that this Workflow uses can be thought of as the app's state. All of your major screen
 * transitions should happen here. For instance, if your app would have a settings screen, there
 * should likely be a new `ViewingSettings` state added here that will run a new `SettingsWorkflow`
 * that you'd handle.
 */
class MainWorkflow
@Inject constructor() : StatefulWorkflow<Unit, State, Nothing, Any>() {

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
  ): Any {

    return when (renderState) {
      is ViewingComposeScreen -> ComposeScreen(
        onClick = context.eventHandler {
          state = ViewingViewScreen
        }
      )
      is ViewingViewScreen -> ViewScreen(
        onClick = context.eventHandler {
          state = ViewingComposeScreen
        }
      )
    }
  }

  override fun snapshotState(state: State): Snapshot {
    return state.toSnapshot()
  }
}
