package com.tian.app.ai.ui.authorization.history

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tian.app.ai.R
import com.tian.app.ai.databinding.ActivityAuthorizationHistoryBinding

class AuthorizationHistoryActivity : ComponentActivity() {
    private lateinit var binding: ActivityAuthorizationHistoryBinding
    private lateinit var adapter: AuthorizationHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置返回按钮点击事件
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 初始化RecyclerView
        adapter = AuthorizationHistoryAdapter(getTestData())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AuthorizationHistoryActivity)
            adapter = this@AuthorizationHistoryActivity.adapter
        }
    }

    private fun getTestData(): List<AuthRecord> {
        return listOf(
            AuthRecord(
                id = "1",
                packageNo = "QR2025072251RQQQQ",
                authNo = "A123",
                robotNo = "R001",
                serialNo = "S001",
                callbackTime = "2024-06-01 12:00",
                createTime = "2024-06-01 11:00",
                remark = "首次授权",
                status = true
            ),
            AuthRecord(
                id = "2",
                packageNo = "QR2025072251RQQQQ",
                authNo = "A124",
                robotNo = "R001",
                serialNo = "S002",
                callbackTime = "2024-06-02 12:00",
                createTime = "2024-06-02 11:00",
                remark = "二次授权",
                status = false
            ),
            AuthRecord(
                id = "3",
                packageNo = "QR2025072251RQQQW",
                authNo = "A125",
                robotNo = "R002",
                serialNo = "S003",
                callbackTime = "2024-06-03 12:00",
                createTime = "2024-06-03 11:00",
                remark = "首次授权",
                status = true
            ),
            AuthRecord(
                id = "4",
                packageNo = "QR2025072251RQQQE",
                authNo = "A126",
                robotNo = "R003",
                serialNo = "S004",
                callbackTime = "2024-06-04 12:00",
                createTime = "2024-06-04 11:00",
                remark = "测试授权",
                status = true
            ),
            AuthRecord(
                id = "5",
                packageNo = "QR2025072251RQQQR",
                authNo = "A127",
                robotNo = "R004",
                serialNo = "S005",
                callbackTime = "2024-06-05 12:00",
                createTime = "2024-06-05 11:00",
                remark = "批量授权",
                status = false
            )
        )
    }
} 