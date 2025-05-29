package com.h1194.cuantracker.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.h1194.cuantracker.R

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs) {

    private var parentTextInputLayout: TextInputLayout? = null

    init {
        gravity = Gravity.CENTER_VERTICAL
        post {
            parentTextInputLayout = this.parent?.parent as? TextInputLayout
        }
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                s?.let {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                        parentTextInputLayout?.error = null
                    } else {
                        parentTextInputLayout?.error = context.getString(R.string.email_error)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                post {
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
    }
}