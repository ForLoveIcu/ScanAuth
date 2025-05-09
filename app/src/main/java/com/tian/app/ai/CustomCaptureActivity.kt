package com.tian.app.ai

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomCaptureActivity : CaptureActivity() {
    private var isTorchOn = false
    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zxing_capture)
        
        barcodeView = findViewById(R.id.zxing_barcode_scanner)
        val btnFlash = findViewById<ImageButton>(R.id.btn_flash)
        val btnGallery = findViewById<ImageButton>(R.id.btn_gallery)
        val tvTip = findViewById<TextView>(R.id.tv_center_tip)
        tvTip.text = "识别二维码"

        // 设置手电筒按钮
        btnFlash.setOnClickListener {
            isTorchOn = !isTorchOn
            if (isTorchOn) {
                barcodeView.setTorchOn()
                btnFlash.setImageResource(R.drawable.ic_flash_on)  // 开启状态图标
            } else {
                barcodeView.setTorchOff()
                btnFlash.setImageResource(R.drawable.ic_flash_off)  // 关闭状态图标
            }
        }

        // 设置相册按钮
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    companion object {
        private const val REQUEST_CODE_GALLERY = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val intent = Intent()
                intent.putExtra("SCAN_RESULT", uri.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
} 