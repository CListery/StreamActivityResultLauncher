package com.yh.sarl.demo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yh.sarl.demo.launcher.PermissionsLauncher
import com.yh.sarl.launcher.BaseLauncher.Companion.bindLauncher
import com.yh.sarl.launcher.ContractType
import com.yh.sarl.launcher.SimpleLauncher
import com.yh.sarl.launcher.SimpleLauncher.Companion.simpleLauncher
import com.yh.sarl.onFailure
import com.yh.sarl.onSuccess

class ActMain : AppCompatActivity() {

    private lateinit var resultTxt: TextView
    private lateinit var resultImg: ImageView

    private val permissionsLauncher: PermissionsLauncher = bindLauncher(this)
    private val openMediaLauncher: SimpleLauncher<Array<String>, Uri> =
        simpleLauncher(ContractType.OpenDocument, this)
    private val takePictureLauncher: SimpleLauncher<Unit, Bitmap> =
        simpleLauncher(ContractType.TakePicturePreview, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_main)

        findViewById<View>(R.id.btn_open_media).setOnClickListener {
            cleanResult()

            permissionsLauncher
                .input(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
                .next(openMediaLauncher)
                .input(arrayOf("image/*"))
                .checker { null != it }
                .launch()
                .onSuccess {
                    setResultTxt(it.toString())
                }.onFailure {
                    setResultTxt(it.stackTraceToString())
                }
        }

        findViewById<View>(R.id.btn_take_picture).setOnClickListener {
            cleanResult()

            permissionsLauncher
                .input(arrayOf(android.Manifest.permission.CAMERA))
                .next(takePictureLauncher)
                .checker { null != it }
                .launch()
                .onSuccess {
                    setResultImg(it)
                }
                .onFailure {
                    setResultTxt(it.stackTraceToString())
                }
        }

        resultTxt = findViewById(R.id.txt_result)
        resultImg = findViewById(R.id.img_result)

    }

    private fun setResultImg(img: Bitmap) {
        resultImg.setImageBitmap(img)
        resultImg.visibility = View.VISIBLE
    }

    private fun setResultTxt(msg: String) {
        resultTxt.text = msg
        resultTxt.visibility = View.VISIBLE
    }

    private fun cleanResult() {
        resultTxt.text = ""
        resultTxt.visibility = View.GONE
        resultImg.setImageDrawable(null)
        resultImg.visibility = View.GONE
    }

}