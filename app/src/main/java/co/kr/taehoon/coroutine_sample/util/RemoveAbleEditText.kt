package co.kr.taehoon.coroutine_sample.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import co.kr.taehoon.coroutine_sample.R


open class RemoveAbleEditText : AppCompatEditText,
    View.OnFocusChangeListener, View.OnTouchListener {

    private var clearDrawable: Drawable? = null


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setTextColor(resources.getColor(R.color.baseTextColor, null))
        setHintTextColor(resources.getColor(R.color.subTextColor, null))

        background = ContextCompat.getDrawable(context,
            R.drawable.drawable_removeable_et)
        clearDrawable = ContextCompat.getDrawable(context,
            R.drawable.ic_cancel)?.apply {
            setBounds(
                0,
                0,
                intrinsicWidth,
                intrinsicWidth
            )
            setClearDrawableVisible(false)
        }
        isSingleLine = true
        setOnTouchListener(this)
        onFocusChangeListener = this
    }

    private fun setClearDrawableVisible( isVisible: Boolean) {
        clearDrawable?.setVisible(isVisible, false)
        setCompoundDrawables(null, null, if (isVisible) clearDrawable else null, null)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (isFocused) {
            setClearDrawableVisible(text?.length ?: 0 > 0)
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val x = motionEvent?.x ?: 0.0f

        motionEvent?.let { event ->
            clearDrawable?.let {
                if (it.isVisible && (x > width - paddingRight - it.intrinsicWidth)) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        text = null
                    }
                    return true
                }
            }
        }

        return false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            setClearDrawableVisible(text?.isNotEmpty() ?: false)
        } else {
            setClearDrawableVisible(false)
        }
    }
}