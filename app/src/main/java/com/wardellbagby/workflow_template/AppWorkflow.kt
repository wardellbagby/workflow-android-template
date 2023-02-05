package com.wardellbagby.workflow_template

import android.os.CountDownTimer
import android.os.Parcelable
import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.Worker
import com.squareup.workflow1.action
import com.squareup.workflow1.asWorker
import com.squareup.workflow1.runningWorker
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.container.BodyAndOverlaysScreen
import com.squareup.workflow1.ui.container.Overlay
import com.squareup.workflow1.ui.toParcelable
import com.squareup.workflow1.ui.toSnapshot
import com.wardellbagby.workflow_template.AppWorkflow.CurrentScreen.Compose
import com.wardellbagby.workflow_template.AppWorkflow.CurrentScreen.View
import com.wardellbagby.workflow_template.AppWorkflow.CurrentScreen.ViewBinding
import com.wardellbagby.workflow_template.AppWorkflow.State
import com.wardellbagby.workflow_template.TickerOutput.Finished
import com.wardellbagby.workflow_template.TickerOutput.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
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
 * What you would want in a real application is for your root and child Workflows to all render
 * this, and to combine those renderings together until you get to a single root Workflow that
 * returns this. You should never render a [BodyAndOverlaysScreen] that has a nested
 * [BodyAndOverlaysScreen], however. That will crash. So instead, you might do something like this:
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
 * instead of [BodyAndOverlaysScreen].
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
@Inject constructor() : StatefulWorkflow<Unit, State, Nothing, AppRendering>() {
  @Parcelize
  enum class CurrentScreen : Parcelable {
    Compose, View, ViewBinding
  }

  /**
   * The state for this application. There are many ways to organize your state; one of most common
   * is to use a sealed interface (or sealed class) to model the different states that you're in.
   * If this were to use that approach, there would be a different state for every screen that could
   * be displayed, and clicking a button would transition to that different state. That approach is
   * very useful when your different screens all use different stateful values. However, because
   * of the simplicity here, all of these screens need the same value ([remainingTime]) so we just
   * use a data class with a [screen] property in it, and use that [screen] property to say which
   * screen we're on.
   *
   * That sealed interface state might look like this:
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
   * Making your state Parcelable (and using Parcelize) is very useful as it makes the
   * [initialState] and [snapshotState] implementations easy since Workflows have useful helpers to
   * convert from a [Snapshot] to a [Parcelable] and vice-versa.
   *
   * @param remainingTime How much time is remaining before we transition to the next screen.
   * @param screen The screen that is currently being displayed.
   */
  @Parcelize
  data class State(
    val remainingTime: JavaDuration = DEFAULT_DURATION,
    val screen: CurrentScreen
  ) : Parcelable

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = snapshot?.toParcelable() ?: State(screen = Compose)

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): AppRendering {
    // This is a Worker, which is how you do asynchronous operations inside of a Workflow. Workers
    // are effectively wrappers around a Flow (check the implementation of Worker.ticker to see
    // how that can work) that integrate with the Workflow machinery. An important part of Workers
    // are their "keys", which is how two Workers that output the same type are differentiated by
    // the Workflow machinery. Workflows will continue to run the same Worker forever so long as
    // runningWorker is called with a Worker that matches the type and key from a previous render
    // pass. Since this Worker always runs, we use the "key" here to let the machinery know that it
    // should be reset when the screen bit of our state changes. When the key changes, the Workflow
    // thinks "hey this is a new Worker, not the same as the one I saw last time!) and starts it
    // again from scratch.
    context.runningWorker(
      Worker.ticker(duration = renderState.remainingTime),
      key = renderState.screen.name
    ) {
      when (it) {
        is Update -> action {
          state = state.copy(remainingTime = it.remainingTime)
        }
        Finished -> action {
          state = State(screen = state.screen.getNextScreen())
        }
      }
    }

    // Usually, these are inlined into the screen creation, but since they all do the same here,
    // we just create it here and share it.
    val onClick = context.eventHandler {
      state = State(screen = state.screen.getNextScreen())
    }

    val remainingTime = renderState.remainingTime.formatAsSeconds()
    return when (renderState.screen) {
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

  private fun CurrentScreen.getNextScreen(): CurrentScreen {
    return when (this) {
      Compose -> View
      View -> ViewBinding
      ViewBinding -> Compose
    }
  }
}

// 10.999 makes it so we actually show 10 seconds for a bit.
private val DEFAULT_DURATION = 10.999.seconds.toJavaDuration()

private sealed interface TickerOutput {
  data class Update(val remainingTime: JavaDuration) : TickerOutput
  object Finished : TickerOutput
}

private fun Worker.Companion.ticker(
  duration: JavaDuration,
): Worker<TickerOutput> {
  return callbackFlow {
    val timer = object : CountDownTimer(
      /* millisInFuture = */ duration.toKotlinDuration().inWholeMilliseconds,
      /* countDownInterval = */ 500L
    ) {
      override fun onTick(millisUntilFinished: Long) {
        trySend(Update(millisUntilFinished.milliseconds.toJavaDuration()))
      }

      override fun onFinish() {
        trySend(Finished)
      }
    }
    timer.start()

    awaitClose {
      timer.cancel()
    }
  }
    .flowOn(Dispatchers.Main)
    .asWorker()
}

private fun JavaDuration.formatAsSeconds(): String {
  return when (val seconds = toKotlinDuration().inWholeSeconds) {
    1L -> "1 second"
    else -> "$seconds seconds"
  }
}
