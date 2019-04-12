package com.strubber.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.strubber.dashboard.adapter.ShippingAdapter
import com.strubber.dashboard.model.ShippingDataModel
import kotlinx.android.synthetic.main.activity_shiping.*
import kotlin.collections.ArrayList

class ShippingActivity : AppCompatActivity() {

    private val listSaleName = ArrayList<String>()
    private val listSaleNameID = ArrayList<String>()

    private val listShippingData = ArrayList<ShippingDataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shiping)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = intent?.extras?.getString("TITLE_NAME")

        listSaleNameID.clear()
        listSaleName.clear()
        
        getSaleName()

        spn_sale_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(applicationContext, "nothing to select!!!", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d(TAG, ":${listSaleNameID[p2]} ")
                getShippingList(listSaleNameID[p2])
            }
        }

    }


    private fun getSaleName() {
        val url = "http://roomdatasoftware.strubberdata.com/api/get_spinner_packing.php?func=s_name"

        val request = JsonArrayRequest(Request.Method.GET,url,null,
            Response.Listener{response ->
                for (i in 0 until response.length()){
                    val jsonObj = response.getJSONObject(i)

                    val name = jsonObj.getString("name")
                    val id = jsonObj.getString("staff_id")

                    listSaleNameID.add(id)
                    listSaleName.add(name)

                }
                val adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,listSaleName)
                spn_sale_name.adapter = adapter

            },
            Response.ErrorListener {
                Toast.makeText(this, "can't connect to server.", Toast.LENGTH_SHORT).show()
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun getShippingList(saleID:String){

        listShippingData.clear()

        val url = "http://192.168.20.226:8081/api/get_shipping_data_list.php?id=$saleID"
//        val url = "http://jobtackingsoftware.strubberdata.com/api/get_shipping_data_list.php?id=$id"

        val dialogLoading = SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังโหลดข้อมูล..."
            setCancelable(false)
            show()
        }
        val request = JsonArrayRequest(Request.Method.GET,url,null,
            Response.Listener {

                if(it.length() < 1)
                    txt_show_no_more.visibility = View.VISIBLE
                else
                    txt_show_no_more.visibility = View.GONE
                
                for (i in 0 until it.length()){
                    val jsonObj = it.getJSONObject(i)

                    val trace = jsonObj.getString("trace_id")
                    val customerName = jsonObj.getString("customer_name")
                    val docOrder = jsonObj.getString("doc_no")
                    val value = jsonObj.getString("sum")

                    val time = jsonObj.getJSONObject("deliver_date")
                    val shippingDate = time.getString("date")

                    val diffDate = jsonObj.getInt("diffdate")

                     val data = ShippingDataModel(trace,docOrder,customerName,shippingDate,value,diffDate,saleID)
                    listShippingData.add(data)
                }
                val adapter = ShippingAdapter(this,listShippingData)
                recycler_view_shipping_date.layoutManager = LinearLayoutManager(this)
                recycler_view_shipping_date.adapter = adapter

                dialogLoading.dismissWithAnimation()

            },
            Response.ErrorListener {
                Toast.makeText(this, "can't connect to server.", Toast.LENGTH_SHORT).show()
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
