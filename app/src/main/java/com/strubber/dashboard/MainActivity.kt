package com.strubber.dashboard

import android.annotation.SuppressLint
import android.app.Application
import android.app.Person
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.strubber.dashboard.adapter.ItemAdapter
import com.strubber.dashboard.model.App
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

///global var and func
const val TAG = "logd"

fun Date.changeDateFormat(pattern:String): String {
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(this)
}

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        getImageProfile()

        setProfileNavDrawer()

        setRecyclerView()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = Intent()
        when(item.itemId){
            R.id.nav_shipping_date->{

            }
            R.id.nav_receive_product->{

            }
            R.id.nav_check_bill->{

            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.nav_exit -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun setProfileNavDrawer() {
        val headerView = navigation_view.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.txt_header_name)
        //val headerPosition = headerView.findViewById<TextView>(R.id.txt_header_position)

        headerName.text = LoginActivity.name
    }

    private fun setRecyclerView() {
        recycler_view_section1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_view_section1.isNestedScrollingEnabled = false
        recycler_view_section1.adapter = ItemAdapter(this, getMenu(), 0, recycler_view_section1)

//        recycler_view_section2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recycler_view_section2.isNestedScrollingEnabled = false
//        recycler_view_section2.adapter = ItemAdapter(this, getMenu2(), 1, recycler_view_section2)
//
//        recycler_view_section3.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recycler_view_section3.isNestedScrollingEnabled = false
//        recycler_view_section3.adapter = ItemAdapter(this, getMenu2(), 2, recycler_view_section3)
    }

    private fun getImageProfile() {
        val headerView = navigation_view.getHeaderView(0)
        val headerImage = headerView.findViewById<ImageView>(R.id.profile_image)


        val url = "http://hrmsoftware.strubberdata.com/personnel_img/getImagePersonal.php?id=${LoginActivity.personalID}"
        val request = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener {
                for (i in 0 until it.length()) {
                    val jsonObj = it.getJSONObject(i)
                    val imageProfile = "http://hrmsoftware.strubberdata.com/personnel_img/${jsonObj.getString("image")}"
                    Glide.with(this).load(imageProfile).placeholder(R.drawable.person_blank).error(R.drawable.person_blank).into(headerImage)
                }
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "can't connect to server!!", Toast.LENGTH_SHORT).show()
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    private fun getMenu(): List<App> {
        val apps: ArrayList<App> = ArrayList()
        apps.add(App("กำหนดลงเรือ", R.drawable.ic_ship))
        apps.add(App("รับสินค้า", R.drawable.ic_receive))
        apps.add(App("ตรวจบิลขาย", R.drawable.ic_checklist))
        return apps
    }

    private fun getMenu2(): List<App> {
        val apps: ArrayList<App> = ArrayList()
        apps.add(App("TITLE01", R.drawable.ic_docs_48dp))
        apps.add(App("TITLE02", R.drawable.ic_docs_48dp))
        apps.add(App("TITLE03", R.drawable.ic_docs_48dp))
        apps.add(App("TITLE04 ", R.drawable.ic_docs_48dp))
        apps.add(App("TITLE05 ", R.drawable.ic_docs_48dp))
        return apps
    }
}
