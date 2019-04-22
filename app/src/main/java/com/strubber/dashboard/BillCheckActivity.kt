package com.strubber.dashboard

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.strubber.dashboard.adapter.CheckbillAdapter
import com.strubber.dashboard.model.CheckBillDataModel
import kotlinx.android.synthetic.main.activity_bill_check.*

class BillCheckActivity : AppCompatActivity() {

    private val listSaleName = ArrayList<String>()
    private val listSaleNameID = ArrayList<String>()

    private val listCheckBill = ArrayList<CheckBillDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_check)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = intent?.extras?.getString("TITLE_NAME")

        listSaleName.clear()
        listSaleNameID.clear()
        getSaleName()

        //spinner event
         spn_sale_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
             override fun onNothingSelected(p0: AdapterView<*>?) {
                 TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
             }

             override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 getCheckBillList(listSaleNameID[p2])
             }
         }
    }

    private fun getSaleName() {
        val url = "http://roomdatasoftware.strubberdata.com/api/get_spinner_packing.php?func=s_name"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                for (i in 0 until response.length()) {
                    val jsonObj = response.getJSONObject(i)

                    val name = jsonObj.getString("name")
                    val id = jsonObj.getString("staff_id")

                    listSaleNameID.add(id)
                    listSaleName.add(name)

                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listSaleName)
                spn_sale_name.adapter = adapter

            },
            Response.ErrorListener {
                Toast.makeText(this, "can't connect to server.", Toast.LENGTH_SHORT).show()
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun getCheckBillList(saleID: String) {
        listCheckBill.clear()

//        val url = "http://192.168.20.226:8081/api/get_check_bill_data_list.php?id=$saleID"
        val url = "http://jobtackingsoftware.strubberdata.com/api/get_check_bill_data_list.php?id=$saleID"

        val dialogLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังโหลดข้อมูล..."
            setCancelable(false)
            show()
        }

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {

                if (it.length() < 1) {
                    txt_show_no_more.visibility = View.VISIBLE
                } else {
                    txt_show_no_more.visibility = View.GONE
                }

                for (i in 0 until it.length()) {
                    val jsonObj = it.getJSONObject(i)

                    val ivID = jsonObj.getString("iv_id")
                    val no = jsonObj.getString("no")
                    val innerDate = jsonObj.getJSONObject("date")
                    val date = innerDate.getString("date")
                    val cusName = jsonObj.getString("customer_name")
                    val value = jsonObj.getString("net2")
                    val tsName = jsonObj.getString("ts_name")
                    val innerDateCreate = jsonObj.getJSONObject("ts_create")
                    val tsCreate = innerDateCreate.getString("date")
                    val remark = jsonObj.getString("remark")

                    val data = CheckBillDataModel(ivID, no, date, cusName, value, tsName, tsCreate, remark, saleID)
                    listCheckBill.add(data)
                }
                val adapter = CheckbillAdapter(this, listCheckBill)
                recycler_view_check_bill.layoutManager = LinearLayoutManager(this)
                recycler_view_check_bill.adapter = adapter

                dialogLoading.dismissWithAnimation()
            },
            Response.ErrorListener {
                Toast.makeText(this, "can't fetch data.", Toast.LENGTH_SHORT).show()
                dialogLoading.dismissWithAnimation()
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
