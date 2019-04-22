package com.strubber.dashboard.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.strubber.dashboard.*
import com.strubber.dashboard.model.ShippingDataModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShippingAdapter(val context: Context, val list: ArrayList<ShippingDataModel>) :
    RecyclerView.Adapter<ShippingAdapter.ViewHolder>() {

    var shippingDateSelect = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_shipping, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvOrderNumber.text = list[position].order
        holder.tvCustomerName.text = list[position].customerName

        val dateFormat = SimpleDateFormat("yyyy-MM-dd").parse(list[position].shippingDate)
        holder.tvShippingDate.text = dateFormat.changeDateFormat("dd/MM/yyyy")
        holder.tvValue.text = list[position].value

        //on click list order
        holder.constraintLayout.setOnClickListener {
            showDialogShipping(
                dateFormat.changeDateFormat("dd/MM/yyyy"),
                list[position].traceID,
                LoginActivity.name,
                list[position].order,
                list[position].saleID
            )
        }

        if (list[position].diffDate < 0) {
            holder.tvShippingDate.setTextColor(Color.RED)
        } else {
            holder.tvShippingDate.setTextColor(Color.GRAY)
        }
    }

    private fun spiltDate(shippingDate: String): List<String> {
        val dateStr = shippingDate
        val spilter = "/"
        return dateStr.split(spilter)
    }

    private fun showDialogShipping(shippingDate: String, traceID: String, name: String, order: String, saleID: String) {

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog_shipping_date)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val imageView = dialog.findViewById<ImageView>(R.id.img_select_date_shipping)
        val edtText = dialog.findViewById<EditText>(R.id.edt_shipping_date)
        val btnSave = dialog.findViewById<Button>(R.id.btn_save_shipping_date)
        val btnExit = dialog.findViewById<Button>(R.id.btn_cancel_shipping_date)

        edtText.setText(shippingDate)

        showDialogDate(edtText, imageView, spiltDate(shippingDate))

        btnSave.setOnClickListener {

            dialog.dismiss()
            val dialogLoading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE).apply {
                contentText = "กำลังบันทึกข้อมูล..."
                setCancelable(false)
                show()
            }
            val prevDate = SimpleDateFormat("dd/MM/yyyy").parse(shippingDate)

            if (shippingDateSelect == "")
                shippingDateSelect = shippingDate
            else
                shippingDateSelect

            val selectDate = SimpleDateFormat("dd/MM/yyyy").parse(shippingDateSelect)

//            val url = "http://192.168.20.226:8081/api/insert_shipping_date.php/"
            val url = "http://jobtackingsoftware.strubberdata.com/api/insert_shipping_date.php/"
            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { s ->
                    // Your success code here

                    dialogLoading.dismissWithAnimation()
                    SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE).apply {
                        contentText = "บันทึกข้อมูลสำเร็จ"
                        setCancelable(false)
                        show()
                    }
                    //refresh
                    (context as ShippingActivity).getShippingList(saleID)

                },
                Response.ErrorListener { e ->
                    Toast.makeText(context, "can't insert data ${e.message}", Toast.LENGTH_SHORT).show()
                    dialogLoading.dismissWithAnimation()
                }) {
                override fun getParams(): Map<String, String> = mapOf(
                    "prev_date" to prevDate.changeDateFormat("yyyy-MM-dd"),
                    "deliver_date" to selectDate.changeDateFormat("yyyy-MM-dd"),
                    "ts_name" to name,
                    "trace_id" to traceID,
                    "doc_no" to order
                )
            }
            val queue = Volley.newRequestQueue(context)
            queue.add(stringRequest)

        }
        btnExit.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }


    private fun showDialogDate(editText: EditText, imageView: ImageView, str: List<String>) {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = str[0].toInt()


        imageView.setOnClickListener {
            val dpd =
                DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    val dateSelect = "$dayOfMonth/${monthOfYear + 1}/$year"
                    editText.setText(dateSelect)
                    shippingDateSelect = dateSelect

                }, year, (str[1].toInt()) - 1, day)
            dpd.show()

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderNumber = itemView.findViewById<TextView>(R.id.txt_order_number)
        val tvCustomerName = itemView.findViewById<TextView>(R.id.txt_customer)
        val tvShippingDate = itemView.findViewById<TextView>(R.id.txt_shipping_date)
        val tvValue = itemView.findViewById<TextView>(R.id.txt_value)
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraint_layout)
    }
}
