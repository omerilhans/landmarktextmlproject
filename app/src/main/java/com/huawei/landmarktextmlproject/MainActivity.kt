package com.huawei.landmarktextmlproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        var imageBitmap: Bitmap? = null
        var remoteLandmarkResponse: MLRemoteLandmark? = null
        var mainActivityContext: MainActivity? = null

        private const val MY_CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 300
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        mainActivityContext = this

        take_picture_icon.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            }
        }

        pick_image_icon.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_REQUEST_CODE
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200)
            }
        }

        this@MainActivity.intent = intent
        intent!!.extras
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) { //  resim seçildiğinde yapılacaklar
                val bitmapFromCamera = data?.extras!!["data"] as Bitmap?
                val intentFromCamera = Intent(this, LandmarkRecognizationActivity::class.java)
                imageBitmap = bitmapFromCamera
                startActivity(intentFromCamera)
            }
        }
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                val pickedImage: Uri? = data?.data
                // Let's read picked image path using content resolver
                try {
                    val bitmapFromPicked =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, pickedImage)
                    Log.i("Bitmap on pick:", bitmapFromPicked.toString())
                    imageBitmap = bitmapFromPicked
                    val intentFromGallery = Intent(this, LandmarkRecognizationActivity::class.java)
                    startActivity(intentFromGallery)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun open_translate(view: View?) {
        val intent = Intent(this, TranslatorActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 200)
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
