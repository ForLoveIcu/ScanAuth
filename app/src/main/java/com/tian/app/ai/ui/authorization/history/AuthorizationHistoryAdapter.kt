package com.tian.app.ai.ui.authorization.history

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.tian.app.ai.R
import com.tian.app.ai.databinding.ItemAuthorizationHistoryBinding

class AuthorizationHistoryAdapter(
    private val records: List<AuthRecord>
) : RecyclerView.Adapter<AuthorizationHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAuthorizationHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: AuthRecord) {
            binding.apply {
                tvPackageNo.text = "包裹号：${record.packageNo}"
                tvAuthNo.text = "授权号：${record.authNo}"
                tvRobotNo.text = "机器人编号：${record.robotNo}"
                tvSerialNo.text = "序列号：${record.serialNo}"
                tvCallbackTime.text = "回调时间：${record.callbackTime}"
                tvCreateTime.text = "创建时间：${record.createTime}"
                tvRemark.text = "备注：${record.remark}"
                tvStatus.text = if (record.status) "成功" else "失败"
                tvStatus.setTextColor(
                    itemView.context.getColor(
                        if (record.status) R.color.success else R.color.error
                    )
                )
                tvUserName.text = record.userName

                // 设置编辑按钮点击事件
                ivEditUser.setOnClickListener {
                    showEditDialog(record)
                }

                // 设置使用人文本点击事件
                tvUserName.setOnClickListener {
                    showEditDialog(record)
                }
            }
        }

        private fun showEditDialog(record: AuthRecord) {
            val dialogView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.dialog_edit_user, null)
            val editText = dialogView.findViewById<TextInputEditText>(R.id.etUserName)
            editText.setText(record.userName)

            AlertDialog.Builder(itemView.context)
                .setTitle("编辑使用人")
                .setView(dialogView)
                .setPositiveButton("确定") { _, _ ->
                    val newName = editText.text.toString()
                    record.userName = newName
                    notifyItemChanged(adapterPosition)
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAuthorizationHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount() = records.size
} 