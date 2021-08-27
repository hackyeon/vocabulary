package com.example.servervocavolley

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.servervocavolley.databinding.ActivityMainBinding
import com.example.servervocavolley.dialogfragment.JoinDialogFragment
import com.example.servervocavolley.dialogfragment.TextAlertDialogFragment
import org.json.JSONObject

class MainActivity : AppCompatActivity(), JoinDialogFragment.JoinDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var spf: SharedPreferences
    private var joinDialogFragment = JoinDialogFragment("회원가입","아이디", "비밀번호")
    private val baseURL = "https://skek933.cafe24.com/"
    private val getLogin = "study/login.php"
    private val getJoin = "study/join.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initView()
        clickedButton()
    }

    private fun initView() {
        spf = getSharedPreferences("saveId", MODE_PRIVATE)

        var id = spf.getString("id", null)
        var password = spf.getString("password", null)
        if (id != null && password != null) {
            loginSuccess(id, password)
        } else {
            // todo 인터넷 관련
            // todo 딱힐 할게없나?
            // 여기다 queue를 미리 초기화 해도 되는지 물어보기
        }
    }

    private fun clickedButton() {
        binding.loginButton.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            var url = baseURL + getLogin

            var stringRequest = object: StringRequest(
                Request.Method.POST, url,
                { response ->
                    var result = JSONObject(response).getString("result")
                    if (result == "OK") {
                        createAlertDialog("로그인 성공!")
                        loginSuccess(
                            binding.idEditText.text.toString(),
                            SHA256.encryptPassword(binding.passwordEditText.text.toString())
                        )
                    } else {
                        createAlertDialog(JSONObject(response).getString("msg"))
                    }
                }, {
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String,String> = HashMap()
                    params["user_id"] = binding.idEditText.text.toString()
                    params["password"] = SHA256.encryptPassword(binding.passwordEditText.text.toString())
                    return params
                }
            }
            queue.add(stringRequest)
        }

        binding.joinButton.setOnClickListener {
            joinDialogFragment.show(supportFragmentManager, "joinDialog")
        }
    }

    private fun createAlertDialog(message: String) {
        TextAlertDialogFragment(message).show(supportFragmentManager, "alertDialog")
    }

    private fun loginSuccess(id: String, password: String) {
        spf.edit {
            putString("id", id)
            putString("password", password)
        }
        val intent: Intent = Intent(this@MainActivity, VocaActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }

    override fun joinClickedPassButton(dialog: JoinDialogFragment, first: String, second: String) {
        //first: 아이디 second: 비밀번호
        val queue = Volley.newRequestQueue(this)
        var url = baseURL + getJoin

        var stringRequest =object: StringRequest(
            Method.POST, url,
            { response ->
                var result = JSONObject(response).getString("result")
                var message = ""
                if(result == "OK"){
                    message = "가입 성공!"
                    joinDialogFragment.dismiss()
                    loginSuccess(first, SHA256.encryptPassword(second))
                }else {
                    message = "사용할 수 없는 아이디입니다."
                }
                createAlertDialog(message)
            }, {}
        ){
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String,String> = HashMap()
                params["user_id"] = first
                params["password"] = SHA256.encryptPassword(second)
                return params
            }
        }
        queue.add(stringRequest)
    }


    override fun joinRequestData(dialog: DialogFragment) {
        val message = "정보를 모두 입력하세요."
        createAlertDialog(message)
    }
}