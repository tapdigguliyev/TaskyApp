/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.taskie.ui.notes.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.raywenderlich.android.taskie.R
import com.raywenderlich.android.taskie.model.PriorityColor
import com.raywenderlich.android.taskie.model.Task
import com.raywenderlich.android.taskie.model.request.AddTaskRequest
import com.raywenderlich.android.taskie.networking.RemoteApi
import com.raywenderlich.android.taskie.utils.toast
import kotlinx.android.synthetic.main.fragment_dialog_new_task.*

/**
 * Dialog fragment to create a new task.
 */
class AddTaskDialogFragment : DialogFragment() {

  private var taskAddedListener: TaskAddedListener? = null
  private val remoteApi = RemoteApi()

  interface TaskAddedListener {
    fun onTaskAdded(task: Task)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, R.style.FragmentDialogTheme)
  }

  fun setTaskAddedListener(listener: TaskAddedListener) {
    taskAddedListener = listener
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_dialog_new_task, container)
  }

  override fun onStart() {
    super.onStart()
    dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initUi()
    initListeners()
  }

  private fun initUi() {
    context?.let {
      prioritySelector.adapter =
          ArrayAdapter<PriorityColor>(it, android.R.layout.simple_spinner_dropdown_item,
              PriorityColor.values())
      prioritySelector.setSelection(0)
    }
  }

  private fun initListeners() = saveTaskAction.setOnClickListener { saveTask() }

  private fun saveTask() {
    if (isInputEmpty()) {
      context?.toast(getString(R.string.empty_fields))
      return
    }

    val title = newTaskTitleInput.text.toString()
    val content = newTaskDescriptionInput.text.toString()
    val priority = prioritySelector.selectedItemPosition + 1

    remoteApi.addTask(AddTaskRequest(title, content, priority)) { task, error ->
      if (task != null) {
        onTaskAdded(task)
      } else if (error != null) {
        onTaskAddFailed()
      }
    }
    clearUi()
  }


  private fun clearUi() {
    newTaskTitleInput.text.clear()
    newTaskDescriptionInput.text.clear()
    prioritySelector.setSelection(0)
  }

  private fun isInputEmpty(): Boolean = TextUtils.isEmpty(
      newTaskTitleInput.text) || TextUtils.isEmpty(newTaskDescriptionInput.text)

  private fun onTaskAdded(task: Task) {
    taskAddedListener?.onTaskAdded(task)
    dismiss()
  }

  private fun onTaskAddFailed() {
    this.activity?.toast("Something went wrong!")
  }
}