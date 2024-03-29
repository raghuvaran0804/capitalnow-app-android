package com.capitalnowapp.mobile.kotlin.utils

import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class NoMenuEditText : TextInputEditText {
    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }

    private fun init() {
        this.customSelectionActionModeCallback = BlockedActionModeCallback()
        this.isLongClickable = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            setInsertionDisabled()
        }
        return super.onTouchEvent(event)
    }

    /**
     * This method sets TextView#Editor#mInsertionControllerEnabled field to false
     * to return false from the Editor#hasInsertionController() method to PREVENT showing
     * of the insertionController from EditText
     * The Editor#hasInsertionController() method is called in  Editor#onTouchUpEvent(MotionEvent event) method.
     */
    private fun setInsertionDisabled() {
        try {
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editorObject = editorField[this]
            val editorClass = Class.forName("android.widget.Editor")
            val mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled")
            mInsertionControllerEnabledField.isAccessible = true
            mInsertionControllerEnabledField[editorObject] = false
        } catch (ignored: Exception) {
            // ignore exception here
        }
    }

    override fun isSuggestionsEnabled(): Boolean {
        return false
    }

    private inner class BlockedActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }
}
