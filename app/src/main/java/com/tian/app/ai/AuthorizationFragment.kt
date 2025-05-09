package com.tian.app.ai

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanOptions
import com.tian.app.ai.ui.authorization.history.AuthorizationHistoryActivity
import com.tian.app.ai.CustomCaptureActivity

class AuthorizationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置扫码按钮点击事件
        view.findViewById<MaterialButton>(R.id.btnScan).setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("识别二维码")
            options.setBeepEnabled(true)
            options.setOrientationLocked(true)
            options.setBarcodeImageEnabled(true)
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setCaptureActivity(CustomCaptureActivity::class.java)
            startActivity(options.createScanIntent(requireContext()))
        }

        // 设置历史记录按钮点击事件
        view.findViewById<MaterialButton>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(requireContext(), AuthorizationHistoryActivity::class.java))
        }
    }
} 