package com.somesh.todoapp.UtilsService

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

class UtilService {
    fun closeKeyBoard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}