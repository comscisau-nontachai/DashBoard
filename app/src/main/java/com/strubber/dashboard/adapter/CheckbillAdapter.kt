package com.strubber.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.strubber.dashboard.*
import com.strubber.dashboard.model.CheckBillDataModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckbillAdapter(val context: Context, val list: ArrayList<CheckBillDataModel>) :
    RecyclerView.Adapter<CheckbillAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_check_bill, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvBillNumber.text = list[position].no
        holder.tvBillCustomerName.text = list[position].cusName
        holder.tvBillValue.text = list[position].value
        holder.tvBillUpName.text = list[position].upName
        holder.tvBillRemark.text = list[position].remark

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(list[position].date)
        holder.tvBillDate.text = dateFormat.changeDateFormat("dd/MM/yyyy")

        //event click list
        holder.layout_constraint.setOnClickListener {

            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).apply {
                val str = list[position].no
                contentText = "ต้องการยืนยันบิล : $str"
                confirmText = "ยืนยัน"
                setConfirmClickListener {
                    updateCheckBill(list[position].ivID, LoginActivity.name,position)
                    dismissWithAnimation()
                }
                cancelText = "ยกเลิก"
                setCancelClickListener {
                    dismissWithAnimation()
                }
                show()
            }
        }
    }

    private fun updateCheckBill(ivID: String, name: String,position:Int) {
//        val url = "http://192.168.20.226:8081/api/update_check_bill.php/"
        val url = "http://jobtackingsoftware.strubberdata.com/api/update_check_bill.php/"

        val dialogSaving = SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังบันทึกข้อมูล..."
            setCancelable(false)
            show()
        }
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener {

                SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE).apply {
                    contentText = "บันทึกข้อมูลสำเร็จ"
                    setCancelable(false)
                    show()
                }
                //remove item
                list.removeAt(position)
                notifyItemRemoved(position)

                dialogSaving.dismissWithAnimation()
            },
            Response.ErrorListener {e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                dialogSaving.dismissWithAnimation()
            }) {
            override fun getParams(): Map<String, String> = mapOf(
                "name" to name,
                "iv_id" to ivID
            )
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(stringRequest)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBillNumber = itemView.findViewById<TextView>(R.id.txt_bill_number)
        val tvBillDate = itemView.findViewById<TextView>(R.id.txt_bill_date)
        val tvBillCustomerName = itemView.findViewById<TextView>(R.id.txt_bill_customer_name)
        val tvBillValue = itemView.findViewById<TextView>(R.id.txt_bill_value)
        val tvBillUpName = itemView.findViewById<TextView>(R.id.txt_bill_ts_name)
        val tvBillRemark = itemView.findViewById<TextView>(R.id.txt_bill_remark)
        val layout_constraint = itemView.findViewById<ConstraintLayout>(R.id.constraint_layout)
    }
}
