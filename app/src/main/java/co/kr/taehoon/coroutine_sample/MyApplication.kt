package co.kr.taehoon.coroutine_sample

import android.app.Application
import co.kr.taehoon.coroutine_sample.util.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(appModules)
        }
    }
}