package com.dicoding.intermediate.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.Button
import androidx.core.content.ContextCompat
import com.dicoding.intermediate.R

class CustomButton : Button {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setBackgroundColor(ContextCompat.getColor(context, R.color.navy))
        setTextColor(ContextCompat.getColor(context, R.color.white))
        setPadding(16, 16, 16, 16)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
