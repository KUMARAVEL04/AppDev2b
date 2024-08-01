package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayInputStream
import java.io.InputStream
data class DataResponse(
    val obstacleLimit:Int=0,
    val image: String? = null
)

data class WordResponse(
    val word:String
)
data class ObstacleList(
    val obstacleCourse:MutableList<String>
)
data class LuckEffect(
    val type:Int,
    val amount:Int,
    val description:String
)

interface ChaseDeuxInterface {
    @GET("/obstacleLimit")
    suspend fun getCollision(): Response<DataResponse>

    @GET("/hitHindrance")
    suspend fun getLuck(): Response<LuckEffect>

    @POST("/randomWord")
    suspend fun getWord(@Body length:Map<String,Int>): Response<WordResponse>

    @POST("/obstacleCourse")
    suspend fun getCourse(@Body extent:Map<String,Int>): Response<ObstacleList>


    @Headers("Content-Type:item/png")
    @GET("/image")
    suspend fun getImage(@Query("character") character: String): Response<ResponseBody>

}
object RetrofitInstance {
    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    val api:ChaseDeuxInterface by lazy {
        Retrofit.Builder().baseUrl("https://chasedeux.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ChaseDeuxInterface::class.java)
    }
}

object NetworkUtils {
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}