package co.kr.taehoon.coroutine_sample.util


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import co.kr.taehoon.coroutine_sample.R
import co.kr.taehoon.coroutine_sample.SearchViewModel.Companion.ERROR_NETWORK
import co.kr.taehoon.coroutine_sample.SearchViewModel.Companion.ERROR_NO_INPUT
import co.kr.taehoon.coroutine_sample.SearchViewModel.Companion.ERROR_NO_RESULT
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target

object BindingAdapterUtil {

    @JvmStatic
    @BindingAdapter("visible")
    fun visible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    @JvmStatic
    @BindingAdapter("setImage")
    fun setImage(imageView: ImageView, url: String?) {
        Glide.with(imageView.context).load(url).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .override(Target.SIZE_ORIGINAL)
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("setErrorMsg")
    fun setErrorMsg(textView: TextView, error: String?) {
        val context = textView.context
        when (error) {
            ERROR_NO_INPUT -> textView.text = context.getString(R.string.error_input_query)
            ERROR_NETWORK -> textView.text = context.getString(R.string.error_network)
            ERROR_NO_RESULT -> textView.text = context.getString(R.string.error_not_exist_result)
            else-> textView.text = ""
        }
    }

}