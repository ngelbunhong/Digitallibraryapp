package com.library.digitallibrary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * A helper extension function on any View to hide the soft keyboard.
 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * A helper extension function on a View that sets up a touch listener to hide the
 * keyboard when the user taps on any non-EditText view.
 */
@SuppressLint("ClickableViewAccessibility")
fun View.setupHideKeyboardOnTap() {
    // Set up a listener on the parent layout
    if (this !is EditText) {
        this.setOnTouchListener { _, _ ->
            // When a non-EditText view is touched, hide the keyboard
            this.hideKeyboard()
            // Return false so the touch event is not consumed and can be passed to other views (like for scrolling)
            false
        }
    }

    // If the view is a container, recursively apply the listener to its children
    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            val innerView = this.getChildAt(i)
            innerView.setupHideKeyboardOnTap()
        }
    }
}