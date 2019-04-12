package com.strubber.dashboard

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    companion object {
        var personalID = ""
        var loginID = ""
        var name = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edt_password.transformationMethod = PasswordTransformationMethod()

        val loginPreference = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val loginPresEditor = loginPreference.edit()

        //get value from preference
        val savePrefs = loginPreference.getBoolean("saveLoginPrefs", false)
        if (savePrefs) {
            edt_username.setText(loginPreference.getString("username", ""))
            edt_password.setText(loginPreference.getString("password", ""))
            chk_remem.isChecked = true
        }
        var lastTimeClick: Long = 0
        btn_login.setOnClickListener {


            if (SystemClock.elapsedRealtime() - lastTimeClick < 1000) {
                return@setOnClickListener
            }
            lastTimeClick = SystemClock.elapsedRealtime()


            val username = edt_username.text.toString().trim()
            val password = edt_password.text.toString().trim()

            //check edt empty
            when {
                username.isEmpty() -> edt_username.error = "ใส่ username ด้วย"
                password.isEmpty() -> edt_password.error = "ใส่ password ด้วย"
                else -> {

                    //save value to preference
                    if (chk_remem.isChecked) {
                        loginPresEditor.apply {
                            this.putBoolean("saveLoginPrefs", true)
                            this.putString("username", username)
                            this.putString("password", password)
                            loginPresEditor.apply()
                        }
                    } else {
                        loginPresEditor.clear()
                        loginPresEditor.commit()
                    }

                    //call Login
                    checkLogin(username, password)
                }
            }
        }
    }

    private fun checkLogin(user: String, pass: String) {
        val dialogLoading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).apply {
            contentText = "กำลังเข้าสู่ระบบ..."
            setCancelable(false)
            show()
        }
        val url = "http://hrmsoftware.strubberdata.com/personnel_img/check_login.php?user=$user&pass=$pass"
        val request = JsonArrayRequest(Request.Method.GET,url,null,
            Response.Listener {jsonArray->
                if(jsonArray.length() < 1){

                    SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE).apply {
                        contentText = "Username/Password ไม่ถูกต้อง...\nกรุณาติดต่อเจ้าหน้าที่"
                        setCancelable(false)
                        confirmText = "ตกลง"
                        show()
                        dialogLoading.dismissWithAnimation()
                    }

                }else{
                    for(i in 0 until jsonArray.length()){
                        val jsonObj = jsonArray.getJSONObject(i)

                        loginID = jsonObj.getString("login_id")
                        personalID = jsonObj.getString("login_personnal")
                        name = jsonObj.getString("login_name")
                    }
                    dialogLoading.dismissWithAnimation()
                    startActivity(Intent(this,MainActivity::class.java))
                }
            },
            Response.ErrorListener {
                Toast.makeText(applicationContext, "can't connect to server!!", Toast.LENGTH_SHORT).show()
                dialogLoading.dismissWithAnimation()
            })

        val queue = Volley.newRequestQueue(this)
        queue.add(request)

    }
}
