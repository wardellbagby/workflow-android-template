package com.wardellbagby.workflow_template

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.workflow1.ui.Screen
import com.squareup.workflow1.ui.ViewEnvironment
import com.squareup.workflow1.ui.compose.LocalWorkflowEnvironment
import com.squareup.workflow1.ui.compose.WorkflowRendering
import com.squareup.workflow1.ui.compose.withComposeInteropSupport
import com.squareup.workflow1.ui.renderWorkflowIn
import com.wardellbagby.workflow_template.theming.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * The single Activity of this application. Its job is to host the single View Model of this app
 * that will host the singular root Workflow. If you're thinking of adding a new Activity, you
 * probably shouldn't. Instead, make changes to [AppWorkflow] to have a new state that renders a
 * new child Workflow instead.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject
  lateinit var activityProvider: ActivityProvider

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Allow [activityProvider] to get an instance to this activity. If other classes need similar
    // functionality, refactor this to instead use Dagger's "IntoSet" to provide all classes that
    // need to observe the activity's lifecycle.
    lifecycle.addObserver(activityProvider)

    val model: AppViewModel by viewModels()

    setContent {
      val rendering by model.renderings.collectAsState()
      AppTheme {
        // ViewEnvironment is a map that our Workflow UI code will have access to. Mostly useful
        // for storing UI concerns in it so that your Workflow doesn't have to worry about it. Think
        // things like a date or currency formatter. Anything you add to the environment here will
        // be available to ALL UI rendered by a Workflow, no matter where it is. You can also
        // override what the environment is directly in a Workflow by using EnvironmentScreen. Not
        // useful for this app, but useful to know overall.
        val environment = remember {
          // This bit here allows us to arbitrarily nest both Compose and View screens. Basically
          // required for any Compose app, even if you aren't explicitly using Views since some of
          // the Workflow built-in types are rendered using Views.
          ViewEnvironment.EMPTY.withComposeInteropSupport()
        }
        CompositionLocalProvider(LocalWorkflowEnvironment provides environment) {
          WorkflowRendering(rendering = rendering)
        }
      }
    }
  }
}

/**
 * The View Model that stores and runs the root Workflow. With how Workflows work, View Models
 * as a concept aren't needed as Workflows are effectively View Models themselves. By that, I mean
 * that Workflows allow you to have a single source of state and to create a model from that state
 * that can be used by your view layer in order to render UI.
 *
 * However, we need a single View Model at the root level here in order to keep our Workflows
 * outside of the normal Android lifecycle. Therefore, we use a View Model to host the root
 * Workflow and handle its renderings.
 *
 * Much of this is machinery that you only need a surface-level understanding of; it's unlikely this
 * will ever need any changes, as all new features should be Workflows themselves, and those
 * Workflows should be children of [AppWorkflow]. Due to that, those will all use the Workflow
 * machinery instead of View Models.
 */
@HiltViewModel
class AppViewModel
@Inject constructor(
  savedState: SavedStateHandle,
  workflow: AppWorkflow
) : ViewModel() {
  val renderings: StateFlow<Screen> =
    renderWorkflowIn(
      workflow = workflow,
      scope = viewModelScope,
      savedStateHandle = savedState,
      interceptors = listOf(DebugWorkflowLoggingInterceptor)
    )
}
