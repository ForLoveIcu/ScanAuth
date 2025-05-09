package com.tian.app.ai.ui.authorization

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tian.app.ai.CustomCaptureActivity
import com.tian.app.ai.databinding.FragmentAuthorizationBinding
import com.tian.app.ai.ui.authorization.history.AuthorizationHistoryActivity

class AuthorizationFragment : Fragment() {
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置扫码按钮点击事件
        binding.btnScan.setOnClickListener {
            // 启动扫码
            startActivity(Intent(requireContext(), CustomCaptureActivity::class.java))
        }

        // 设置历史记录按钮点击事件
        binding.btnHistory.setOnClickListener {
            // 启动授权历史页面
            startActivity(Intent(requireContext(), AuthorizationHistoryActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 