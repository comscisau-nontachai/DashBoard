package com.strubber.dashboard.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import com.strubber.dashboard.R
import com.strubber.dashboard.TAG
import com.strubber.dashboard.adapter.ReceiveProductAdapter
import com.strubber.dashboard.model.ReceiveProductDataModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_receive_product.*
import org.json.JSONArray

class ReceiveProductFragment : androidx.fragment.app.Fragment() {
    /**
     * finished : 1
     * purchase : 4
     * produce : 5
     * */

    private val listReceiveProduct = ArrayList<ReceiveProductDataModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receive_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeProduct = arguments!!.getString("TYPE_RECEIVE")
        val saleID = arguments!!.getString("SALE_ID")

        when(typeProduct){
            "finished" -> getReceiveProduct(saleID!!,"finished")
            "purchase" -> getReceiveProduct(saleID!!,"purchase")
            "produce" -> getReceiveProduct(saleID!!,"produce")
        }
    }
    private fun getReceiveProduct(saleID:String,func:String){
        val dialogLoading = SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังโหลดข้อมูล..."
            setCancelable(false)
            show()
        }

        val url = "http://192.168.20.226:8081/api/get_receive_product_data_list.php"
        //val url = "http://jobtackingsoftware.strubberdata.com/api/get_receive_product_data_list.php/"
        val request = object : StringRequest(Request.Method.POST,url,
            Response.Listener<String> {
                listReceiveProduct.clear()
                val jsonArr = JSONArray(it)

                if(jsonArr.length() < 1)
                    txt_show_no_more.visibility = View.VISIBLE
                else
                    txt_show_no_more.visibility = View.GONE

                for(i in 0 until jsonArr.length()){
                    val jsonObj = jsonArr.getJSONObject(i)

                    val partID = jsonObj.getString("part")
                    val id = jsonObj.getString("id")
                    val billcard = jsonObj.getString("no_billcard")
                    val partnumber = jsonObj.getString("partnumber")
                    val order = jsonObj.getString("doc_no")
                    val customerName = jsonObj.getString("customer_name")
                    val quantity = jsonObj.getString("qty")
                    val tsName = jsonObj.getString("ts_name")
                    val innerDate  = jsonObj.getJSONObject("ts_create")
                    val tsCreate = innerDate.getString("date")
                    val qtyRemain = jsonObj.getString("qty_remain")
                    val dateDiff = jsonObj.getInt("getcre_diff")

                    val data = ReceiveProductDataModel(partID,id,partnumber,billcard,order,customerName,quantity,tsName,tsCreate,func,qtyRemain,dateDiff)
                    listReceiveProduct.add(data)
                }
                val adapter = ReceiveProductAdapter(requireContext(),listReceiveProduct,saleID)
                recycler_view_receive_product.layoutManager = LinearLayoutManager(context)
                recycler_view_receive_product.adapter = adapter

                dialogLoading.dismissWithAnimation()
            },
            Response.ErrorListener {
                Toast.makeText(requireContext(), "can't connect to server.", Toast.LENGTH_SHORT).show()
                dialogLoading.dismissWithAnimation()
            }) {
            override fun getParams(): Map<String, String> = mapOf(
                "id" to saleID,
                "func" to func
            )
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }


}
