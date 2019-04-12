package com.strubber.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.strubber.dashboard.fragment.ReceiveProductFragment
import kotlinx.android.synthetic.main.activity_receive_product.*

class ReceiveProductActivity : AppCompatActivity() {

    lateinit var fragment: ReceiveProductFragment
    lateinit var arg: Bundle

    private val listSaleName = ArrayList<String>()
    private val listSaleID = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_product)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = intent?.extras?.getString("TITLE_NAME")

        initBottomNavigation()

        listSaleName.clear()
        listSaleID.clear()
        getSaleName()

        spn_sale_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                //get badge
                getAllBadgeReceiveProduct(listSaleID[p2])

                ///start first fragment on start
                fragment = ReceiveProductFragment()
                arg = Bundle()
                arg.putString("TYPE_RECEIVE", typeReceive(bottom_navigation.currentItem))
                arg.putString("SALE_ID", listSaleID[p2])
                fragment.arguments = arg
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, fragment).commit()

                //handle event tab
                bottom_navigation.setOnTabSelectedListener { position, wasSelected ->
                    fragment = ReceiveProductFragment()
                    arg = Bundle()

                    when (position) {
                        0 -> {
                            arg.putString("TYPE_RECEIVE", "finished")
                            arg.putString("SALE_ID", listSaleID[p2])
                            fragment.arguments = arg
                            supportFragmentManager.beginTransaction().replace(R.id.container_fragment, fragment)
                                .commit()
                        }
                        1 -> {
                            arg.putString("TYPE_RECEIVE", "purchase")
                            arg.putString("SALE_ID", listSaleID[p2])
                            fragment.arguments = arg
                            supportFragmentManager.beginTransaction().replace(R.id.container_fragment, fragment)
                                .commit()
                        }
                        2 -> {
                            arg.putString("TYPE_RECEIVE", "produce")
                            arg.putString("SALE_ID", listSaleID[p2])
                            fragment.arguments = arg
                            supportFragmentManager.beginTransaction().replace(R.id.container_fragment, fragment)
                                .commit()
                        }
                        else -> {
                            Log.d(TAG, "on else switch ")
                        }
                    }
                    true
                }


            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun initBottomNavigation() {

        //create items
        val item1 = AHBottomNavigationItem("8.1 สำเร็จรูป", R.drawable.ic_package, R.color.colorAccent)
        val item2 = AHBottomNavigationItem("8.2 จัดซื้อ", R.drawable.ic_shopping_cart, R.color.colorAccent)
        val item3 = AHBottomNavigationItem("8.3 ผลิต", R.drawable.ic_manufacture, R.color.colorAccent)

        //add item
        bottom_navigation!!.addItem(item1)
        bottom_navigation!!.addItem(item2)
        bottom_navigation!!.addItem(item3)

        //set bg color
        bottom_navigation.defaultBackgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)

        //change color on click
        bottom_navigation.accentColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottom_navigation.inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)
    }

    private fun getSaleName() {
        val url = "http://roomdatasoftware.strubberdata.com/api/get_spinner_packing.php?func=s_name"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                for (i in 0 until response.length()) {
                    val jsonObj = response.getJSONObject(i)
                    val name = jsonObj.getString("name")
                    val id = jsonObj.getString("staff_id")

                    listSaleID.add(id)
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

    private fun typeReceive(index : Int):String = when (index) {
        0 -> "finished"
        1 -> "purchase"
        2 -> "produce"
        else->{""}
    }

    fun getAllBadgeReceiveProduct(saleID: String) {
        val url = "http://192.168.20.226:8081/api/get_badge_receive_product.php?id=$saleID"
        //val url = "http://jobtackingsoftware.strubberdata.com/api/get_badge_receive_product.php/"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                for (i in 0 until it.length()) {
                    val jsonObj = it.getJSONObject(i)
                    val finished = jsonObj.getString("finished_count")
                    val purchase = jsonObj.getString("purchase_count")
                    val produce = jsonObj.getString("produce_count")

                    bottom_navigation.setNotification(finished, 0)
                    bottom_navigation.setNotification(purchase, 1)
                    bottom_navigation.setNotification(produce, 2)
                }
            },
            Response.ErrorListener { Log.d(TAG, "can't get badge") })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}
