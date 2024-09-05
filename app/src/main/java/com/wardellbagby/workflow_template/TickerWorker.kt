package com.wardellbagby.workflow_template

import android.os.CountDownTimer
import com.squareup.workflow1.Worker
import com.squareup.workflow1.asWorker
import com.wardellbagby.workflow_template.TickerOutput.Finished
import com.wardellbagby.workflow_template.TickerOutput.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

sealed interface TickerOutput {
  data class Update(val remainingTime: Duration) : TickerOutput
  data object Finished : TickerOutput
}

/**
 * A [Worker] is a way of doing asynchronous work in a [com.squareup.workflow1.Workflow]. [Worker]s
 * are thin wrappers around a [Flow].
 *
 * This [Worker] is simple: it wraps a [CountDownTimer], converts it into a [Flow], and then makes
 * that into a [Worker].
 *
 * This is a convenient way of making a [Worker], by declaring it into another file, but you can
 * also inline a [Worker] directly by doing something like this in your
 * [com.squareup.workflow1.StatefulWorkflow.render]/[com.squareup.workflow1.StatelessWorkflow.render]:
 *
 * ```
 * context.runningWorker(
 *   worker = Worker.from { /* return some value */ } // alternatively, use Worker.create to emit multiple items
 * ) { output -> // handle the output }
 * ```
 *
 * The easiest way of thinking about [Worker]s really are that they're just [Flow]s. If you can make
 * it into a [Flow], a simple [asWorker] call onto that [Flow] turns it right into a [Worker]!
 */
class TickerWorker private constructor(
  private val duration: Duration
) : Worker<TickerOutput> {

  // A bit overkill but doing a Factory injection allows us to easily add extra parameters to
  // the constructor without having to do a big refactor, and is a nice pattern to follow if you
  // have a Worker that needs dependencies, since it might also need input. In this case, we have
  // input that should change for each Worker (duration), and no dependencies yet.
  // It's much easier to just inline your Workers, though. See the kdoc for TickerWorker for that
  // example.
  // We also don't use Dagger's assisted inject here because I couldn't get it working and I'm lazy.
  class Factory @Inject constructor() {
    fun create(duration: Duration) = TickerWorker(duration = duration)
  }

  override fun run(): Flow<TickerOutput> {
    return callbackFlow {
      val timer = object : CountDownTimer(
        /* millisInFuture = */ duration.inWholeMilliseconds,
        /* countDownInterval = */ 500L
      ) {
        override fun onTick(millisUntilFinished: Long) {
          trySend(Update(millisUntilFinished.milliseconds))
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
  }
}