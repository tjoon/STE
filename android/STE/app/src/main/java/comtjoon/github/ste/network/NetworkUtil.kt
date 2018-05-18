package comtjoon.github.ste.network

import android.util.Base64
import comtjoon.github.ste.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers




class NetworkUtil {
    companion object {
        fun getRetrofit(): RetrofitInterface {

            var rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())

            return Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RetrofitInterface::class.java)
        }

        fun getRetrofit(email: String, password: String): RetrofitInterface {

            var credentials = "$email:$password"
            var basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            var httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor { chain ->

                var original = chain.request()
                var builder = original.newBuilder()
                        .addHeader("Authorization", basic)
                        .method(original.method(), original.body())
                chain.proceed(builder.build())

            }

            var rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())

            return Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(httpClient.build())
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RetrofitInterface::class.java)
        }

        fun getRetrofit(token: String): RetrofitInterface {

            var httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor { chain ->

                var original = chain.request()
                var builder = original.newBuilder()
                        .addHeader("x-access-token", token)
                        .method(original.method(), original.body())
                chain.proceed(builder.build())

            }

            var rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())

            return Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(httpClient.build())
                    .addCallAdapterFactory(rxAdapter)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RetrofitInterface::class.java)
        }

    }
}