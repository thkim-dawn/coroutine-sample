package co.kr.taehoon.coroutine_sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import co.kr.taehoon.coroutine_sample.BR
import io.reactivex.disposables.CompositeDisposable


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(){
    lateinit var viewDataBinding: T
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    abstract val layoutResourceId: Int
    //Binding 객체를 얻은 후 수행되어야 할 작업을 위한 추상 메소드 ex) 리소스 초기화
    abstract fun initAfterBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView<T>(this, layoutResourceId).apply{
            setVariable(BR.activity, this@BaseActivity)
            lifecycleOwner = this@BaseActivity
        }

        initAfterBinding()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}