package com.strubber.dashboard.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import com.strubber.dashboard.model.ReceiveProductDataModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class ReceiveProductAdapter(val context: Context, val list: ArrayList<ReceiveProductDataModel>,val saleID:String) :
    RecyclerView.Adapter<ReceiveProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_receive_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tvPartnumber.text = list[position].partnumber
        holder.tvOrderNumber.text = list[position].orderNumber
        holder.tvCustomerName.text = list[position].customerName

        val qty =
            NumberFormat.getNumberInstance(Locale.getDefault()).format((list[position].quantity.toDouble()).toInt())
        holder.tvQuantity.text = qty

        holder.tvUpName.text = list[position].tsName

        val dateFormat = SimpleDateFormat("yyyy-MM-dd H:m", Locale.getDefault()).parse(list[position].tsCreate)
        holder.tvDate.text = dateFormat.changeDateFormat("dd/MM/yyyy HH:mm")

        //check date red
        if (list[position].dateDiff < -1) {
            holder.tvPartnumber.setTextColor(Color.RED)
            holder.tvDayOver.visibility = View.VISIBLE
            val str = "(${abs(list[position].dateDiff)} วัน)"
            holder.tvDayOver.text = str
        } else {
            holder.tvPartnumber.setTextColor(Color.GRAY)
            holder.tvDayOver.visibility = View.GONE
        }

        //event layout click
        holder.constraintLayout.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.custom_dialog_receive_product)
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            //init variable
            val edtPartnumber = dialog.findViewById<EditText>(R.id.edt_dialog_partnumber)
            val edtOrder = dialog.findViewById<EditText>(R.id.edt_dialog_order)
            val edtQuantity = dialog.findViewById<EditText>(R.id.edt_dialog_qty)
            val edtReceiveQty = dialog.findViewById<EditText>(R.id.edt_dialog_receive_number)
            val txtLabelPartnumber = dialog.findViewById<TextView>(R.id.txt_label_partnumber)

            val btnSave = dialog.findViewById<Button>(R.id.btn_save_receive_product)
            val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel_receive_product)

            //set value
            if (list[position].func == "produce") {
                txtLabelPartnumber.text = "เลขที่บิลการ์ด"
                edtPartnumber.setText(list[position].billcard)
            } else {
                txtLabelPartnumber.text = "พาร์ทนัมเบอร์"
                edtPartnumber.setText(list[position].partnumber)
            }
            edtOrder.setText(list[position].orderNumber)
            edtQuantity.setText(qty)


            btnSave.setOnClickListener {
                if (edtReceiveQty.text.toString() != "") {

                    val qtyReceive = edtReceiveQty.text.toString().toInt()
                    if(qtyReceive <= qty.toInt() || qtyReceive <= list[position].qtyRemain.toInt()){
                        //do
                        Log.d(TAG, "do this: ")

                        saveReceiveProduct(
                            list[position].func,
                            list[position].id,
                            list[position].partID,
                            qtyReceive.toString(),
                            LoginActivity.name
                        )
                        val str = "${list[position].func}\n${list[position].id}\n${list[position].partID}\n${qtyReceive.toString()}\n${LoginActivity.name}"
                        Log.d(TAG, "$str: ")

                        list.removeAt(holder.adapterPosition)
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }else{
                        //don't
                        SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE).apply {
                            titleText = "คำเตือน"
                            contentText = "ไม่สามารถบันทึกข้อมูลได้\nเนื่องจากจำนวนที่รับ มากกว่า จำนวนส่งหรือคงเหลือ"
                            show()
                        }
                    }

                } else {
                    edtReceiveQty.error = "กรุณาใส่จำนวนรับด้วย"
                    edtReceiveQty.requestFocus()
                }
            }
            btnCancel.setOnClickListener { dialog.cancel() }

            dialog.show()
        }
    }


    private fun saveReceiveProduct(func: String, id: String, part_id: String, qtyReceive: String, tsName: String) {
//        val url = "http://192.168.20.226:8081/api/save_receive_product.php/"
        val url = "http://jobtackingsoftware.strubberdata.com/api/save_receive_product.php/"

        val dialogSaving = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังบันทึกข้อมูล..."
            setCancelable(false)
            show()
        }
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener {

                SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE).apply {
                    contentText = "บันทึกข้อมูลสำเร็จ"
                    setCancelable(false)
                    show()
                }
                //refresh badge on ReceiveProductActivity.kt
                (context as ReceiveProductActivity).getAllBadgeReceiveProduct(saleID)

                dialogSaving.dismissWithAnimation()
            },
            Response.ErrorListener { e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                dialogSaving.dismissWithAnimation()
            }) {
            override fun getParams(): Map<String, String> = mapOf(
                "func" to func,
                "id" to id,
                "part_id" to part_id,
                "qty_receive" to qtyReceive,
                "ts_name" to tsName
            )
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(stringRequest)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPartnumber = itemView.findViewById<TextView>(R.id.txt_receive_partnumber)
        val tvOrderNumber = itemView.findViewById<TextView>(R.id.txt_receive_order)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.txt_receive_customer_name)
        val tvQuantity = itemView.findViewById<TextView>(R.id.txt_receive_quantity)
        val tvUpName = itemView.findViewById<TextView>(R.id.txt_receive_tsname)
        val tvDate = itemView.findViewById<TextView>(R.id.txt_receive_date)
        val tvDayOver = itemView.findViewById<TextView>(R.id.txt_day_over)
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraint_layout)
    }
}