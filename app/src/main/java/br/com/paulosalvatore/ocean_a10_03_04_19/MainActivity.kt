package br.com.paulosalvatore.ocean_a10_03_04_19

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://www.acritica.com/uploads/news/image/731486/show_unnamed.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this).load(imageUrl).into(ivImage)

        Picasso.get().load(imageUrl).into(ivImage)
    }

    fun workerThread(view: View) {
        // Ainda estamos na UIThread (ou MainThread)
        Thread(Runnable {
            // Estamos na WorkerThread
            val bitmap = loadImage(imageUrl)

            ivImage.post {
                ivImage.setImageBitmap(bitmap)
            }
        }).start()
    }

    private fun loadImage(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            return BitmapFactory.decodeStream(
                    url.openConnection().getInputStream()
            )
        } catch (e: Exception) {
            Log.e("LOAD_IMAGE", "Error loading image.", e)
        }

        return null
    }

    fun asyncTask(view: View) {
        MyAsyncTask().execute()

        /*doAsync {
            val bitmap = loadImage(imageUrl)

            uiThread {
                ivImage.setImageBitmap(bitmap)
            }
        }*/
    }

    inner class MyAsyncTask : AsyncTask<Void, Void, Bitmap?>() {

        lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            progressDialog = ProgressDialog.show(
                    this@MainActivity,
                    "Wait",
                    "Loading image..."
            )
        }

        override fun doInBackground(vararg params: Void?) = loadImage(imageUrl)

        override fun onPostExecute(result: Bitmap?) {
            ivImage.setImageBitmap(result)

            progressDialog.dismiss()
        }
    }
}
