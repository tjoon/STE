package comtjoon.github.ste.network


import comtjoon.github.ste.model.Response
import comtjoon.github.ste.model.User
import retrofit2.http.*
import rx.Observable


interface RetrofitInterface {

    @POST("users")
    fun register(@Body user: User): Observable<Response>

    @POST("authenticate")
    fun login(): Observable<Response>

    @GET("users/{email}")
    fun getProfile(@Path("email") email: String): Observable<User>

    @PUT("users/{email}")
    fun changePassword(@Path("email") email: String, @Body user: User): Observable<Response>

    @POST("users/{email}/password")
    fun resetPasswordInit(@Path("email") email: String): Observable<Response>

    @POST("users/{email}/password")
    fun resetPasswordFinish(@Path("email") email: String, @Body user: User): Observable<Response>

    @HTTP(method = "DELETE", path = "users/{email}", hasBody = true)
    fun withdraw(@Path("email") email: String, @Body user : User) : Observable<Response>


    //@DELETE("users/{email}")
    //fun withdraw(@Path("email") email: String, @Body user : User) : Observable<Response>
}