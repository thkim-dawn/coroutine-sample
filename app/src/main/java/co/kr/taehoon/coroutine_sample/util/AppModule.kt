package co.kr.taehoon.coroutine_sample.util

import co.kr.taehoon.coroutine_sample.R
import co.kr.taehoon.coroutine_sample.SearchViewModel
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val apiModule: Module = module {

    single() {
        val gson = GsonBuilder().setLenient().create()
        return@single Retrofit.Builder()
            .client(OkHttpClient.Builder().build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(GsonBuilder().run { GsonConverterFactory.create(gson) })
            .baseUrl("https://dapi.kakao.com")
            .client(get())
            .build()
            .create(ApiService::class.java)
    }

    single {

        val headerInterceptor: Interceptor =
            get(named("header"))

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return@single OkHttpClient().newBuilder()
            .connectTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(1000, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    single(named("header")) {
        return@single Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    "KakaoAK ${androidContext().getString(R.string.KAKAO_API_KEY)}"
                )
                .build().let {
                    chain.proceed(it)
                }
        }
    }
}

val viewModelModule: Module = module {
    viewModel {
        SearchViewModel()
    }
}

val appModules = listOf(
    apiModule,
    viewModelModule
)
