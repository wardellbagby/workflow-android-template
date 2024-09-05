package com.wardellbagby.workflow_template

import android.os.Parcelable
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import com.squareup.workflow1.runningWorker
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.navigation.BodyAndOverlaysScreen
import com.squareup.workflow1.ui.navigation.Overlay
import com.squareup.workflow1.ui.toParcelable
import com.squareup.workflow1.ui.toSnapshot
import com.wardellbagby.workflow_template.AppWorkflow.Phase.Compose
import com.wardellbagby.workflow_template.AppWorkflow.Phase.View
import com.wardellbagby.workflow_template.AppWorkflow.Phase.ViewBinding
import com.wardellbagby.workflow_template.AppWorkflow.State
import com.wardellbagby.workflow_template.TickerOutput.Finished
import com.wardellbagby.workflow_template.TickerOutput.Update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

/**
 * This is the root rendering type of this Workflow, type-aliased to make it more obvious.
 *
 * [BodyAndOverlaysScreen] is supported by default in Android Workflows, and is generally what you
 * want your root Workflow to render. The [BodyAndOverlaysScreen.body] is the root most screen that
 * should be displayed, and [BodyAndOverlaysScreen.overlays] are overlays that will display on top
 * of it.
 *
 * What you would generally want in a real application is for your root Workflow to render
 * [BodyAndOverlaysScreen], the children of your root Workflow to render Screen, Overlay,
 * List<Overlay>, or a custom rendering type, and to combine those renderings together until the
 * root Workflow can finally render its own [BodyAndOverlaysScreen] with all of its child rendering.
 *
 * This might look like this:
 *
 * ```
 * render(): BodyAndOverlaysScreen<Screen, Overlay> {
 *   val childRendering = context.renderChild(myChildWorkflow)
 *   return BodyAndOverlayScreen(body = MyBodyScreen, overlays = childRendering.overlays)
 * }
 * ```
 *
 * That's a trivialized example; but you can deal with your child renderings in whatever form makes
 * the best sense for what you're trying to accomplish. If your child Workflows only return an
 * Overlay (or multiple overlays), then best to have them return that as their rendering type
 * instead of [BodyAndOverlaysScreen]. If your children render a body screen with its own set of
 * overlays, its best to use a custom rendering type for that that DOESN'T implement [Screen].
 *
 * Something like:
 *
 * ```
 * data class MyChildRenderingType(
 *   val body: Screen,
 *   val firstOverlay: Overlay
 * )
 * ```
 *
 */
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
@Inject constructor(
  private val tickerWorkerFactory: TickerWorker.Factory
) : StatefulWorkflow<Unit, State, Nothing, BodyAndOverlaysScreen<Screen, Overlay>>() {
  @Parcelize
  enum class Phase : Parcelable {
    Compose, View, ViewBinding
  }

  /**
   * The state for this application. There are many ways to organize your state; one of most common
   * is to use a sealed interface (or sealed class) to model the different states that you're in.
   * If this were to use that approach, there would be a different state for every screen that could
   * be displayed, and clicking a button would transition to that different state. That approach is
   * very useful when your different screens all use different stateful values. However, because
   * of the simplicity here, all of these screens need the same value ([remainingTime]) so we just
   * use a data class with a [phase] property in it, and use that [phase] property to say which
   * "phase" we're currently in. The approached used for this Workflow is called the "phase"
   * approach, because it separates Workflow state into common properties that are valid for every
   * phase, while still having distinct phases. We call it the phase approach to differentiate it
   * from "state", since that's an overloaded term.
   *
   * The sealed interface state approach for this same Workflow would look like this:
   *
   * ```
   * @Parcelize
   * sealed interface State : Parcelable {
   *  data class ViewingComposeScreen(val remainingTime: JavaDuration): State
   *  data class ViewingViewScreen(val remainingTime: JavaDuration): State
   *  data class ViewingViewBindingScreen(val remainingTime: JavaDuration): State
   * }
   * ```
   *
   * It's usually bad form to name states like what they're named in that sealed interface example,
   * but for this Workflow, it would be appropriate. You usually want to name your states after what
   * they want to accomplish, not after what they're currently displaying. For instance, naming a
   * Workflow state "EnteringCustomerDetails" would be a good name, but naming it
   * "ViewingEnterCustomerDetailScreen" would be a bad one.
   *
   * Making your state Parcelable (and using Parcelize) is very useful as it makes the
   * [initialState] and [snapshotState] implementations easy since Workflows have useful helpers to
   * convert from a [Snapshot] to a [Parcelable] and vice-versa.
   *
   * There's no truly wrong way to organize your Workflow state, so use this as an example of
   * common ways to do it, but not the only way. Feel free to experiment and figure out what
   * works for you!
   *
   * @param remainingTime How much time is remaining before we transition to the next screen.
   * @param phase The "phase" that the Workflow is currently in.
   */
  @Parcelize
  data class State(
    val remainingTime: JavaDuration = DEFAULT_DURATION,
    val phase: Phase
  ) : Parcelable

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = snapshot?.toParcelable() ?: State(phase = Compose)

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): AppRendering {
    // This is a Worker, which is how you do asynchronous operations inside of a Workflow. Workers
    // are wrappers around a Flow (check the implementation of TickerWorker to see how that works)
    // that integrate with the Workflow machinery. An important part of Workers are their "keys",
    // which are how two Workers that output the same type are differentiated by
    // the Workflow machinery. Workflows will continue to run the same Worker forever so long as
    // runningWorker is called with a Worker that matches the type and key from a previous render
    // pass. Since this Worker always runs, we use the "key" here to let the machinery know that it
    // should be reset when the screen bit of our state changes. When the key changes, the Workflow
    // thinks "hey this is a new Worker, not the same as the one I saw last time!" and starts it
    // again from scratch.
    context.runningWorker(
      // It's okay that we're "recreating" this Worker every render because we're not actually
      // restarting it since the type and the key will be the same. This isn't exactly performant
      // though, so hold a reference if your Worker creation is expensive!
      worker = tickerWorkerFactory.create(renderState.remainingTime.toKotlinDuration()),
      key = renderState.phase.name
    ) { output ->
      when (output) {
        is Update -> action {
          state = state.copy(remainingTime = output.remainingTime.toJavaDuration())
        }

        Finished -> action {
          state = State(phase = state.phase.getNextScreen())
        }
      }
    }

    // Usually, these are inlined into the screen creation, but since they all do the same here,
    // we just create it here and share it.
    val onClick = context.eventHandler {
      state = State(phase = state.phase.getNextScreen())
    }

    val remainingTime = renderState.remainingTime.formatAsSeconds()
    return when (renderState.phase) {
      Compose -> HelloComposeScreen(
        remainingTime = remainingTime,
        onClick = onClick
      )

      View -> HelloViewScreen(
        remainingTime = remainingTime,
        onClick = onClick
      )

      ViewBinding -> HelloViewBindingScreen(
        remainingTime = remainingTime,
        onClick = onClick
      )
    }.let { AppRendering(it) }
  }

  override fun snapshotState(state: State): Snapshot {
    return state.toSnapshot()
  }

  private fun Phase.getNextScreen(): Phase {
    return when (this) {
      Compose -> View
      View -> ViewBinding
      ViewBinding -> Compose
    }
  }
}

// 10.999 makes it so we actually show 10 seconds for a bit.
private val DEFAULT_DURATION = 10.999.seconds.toJavaDuration()

private fun JavaDuration.formatAsSeconds(): String {
  return when (val seconds = toKotlinDuration().inWholeSeconds) {
    1L -> "1 second"
    else -> "$seconds seconds"
  }
}
