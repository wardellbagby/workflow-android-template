package com.wardellbagby.workflow_template

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.workflow1.ui.WorkflowLayout
import com.squareup.workflow1.ui.renderWorkflowIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject
  lateinit var activityProvider: ActivityProvider

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycle.addObserver(activityProvider)

    val model: AppViewModel by viewModels()
    setContentView(
      WorkflowLayout(this).apply {
        start(
          lifecycle = lifecycle,
          renderings = model.renderings
        )
      }
    )
  }
}

@HiltViewModel
class AppViewModel
@Inject constructor(
  savedState: SavedStateHandle,
  workflow: MainWorkflow
) : ViewModel() {
  val renderings: StateFlow<Any> =
    renderWorkflowIn(
      workflow = workflow,
      scope = viewModelScope,
      prop = Unit,
      savedStateHandle = savedState,
      interceptors = listOf(DebugWorkflowLoggingInterceptor)
    )
}
