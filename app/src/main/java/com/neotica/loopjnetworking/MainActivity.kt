package com.neotica.loopjnetworking

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.neotica.loopjnetworking.databinding.ActivityMainBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding

    //Step 10.1: Create a new companion object containing TAG variable
    // that derives from MainActivity, simplename.
    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Step 3: Bind the view.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getHttp()
        binding.btnAllQuotes.setOnClickListener { getHttp() }
    }

    //Step 4: Make a new function for LoopJ getting the web api
    private fun getHttp(){
        //Step 5: Make the initial value of progress bar to visible
        binding.progressBar.visibility = View.VISIBLE
        //Step 6: Create a new variable for LoopJ's HttpClient
        val client = AsyncHttpClient()
        //Step 7: Set the URl
        val url = "https://quote-api.dicoding.dev/random"
        //Step 8: Make method to get client
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray?
            ) {
                //Step 9: Make the progressbar invisible onSuccess
                binding.progressBar.visibility = View.INVISIBLE

                //Step 10: Create variable for response body, wrap it with safe call.
                val result = responseBody?.let { String(it) }
                if (result != null) {
                    //Step 10.2: Add the companion object's TAG
                    Log.d(TAG, result)
                }
                //Step 11: Make a try catch method
                try {
                    //Step 12: Make variables for JSON Response.
                    val responseObject = result?.let { JSONObject(it) }
                    val quote = responseObject?.getString("en")
                    val author = responseObject?.get("author")

                    //Step 13: Bind the object to the view components
                    binding.apply {
                        tvQuote.text = quote
                        tvAuthor.text = author.toString()
                    }
                } catch (e: Exception){
                    //Step 14: Create toasts to exception
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                //Step 9: Make the progressbar invisible onFailure
                binding.progressBar.visibility = View.INVISIBLE
                //Step 15: Make the error Message variable
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}