package com.example.servervocavolley

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.servervocavolley.adapter.ViewPagerAdapter
import com.example.servervocavolley.databinding.ActivityVocaBinding
import com.example.servervocavolley.dataclass.Voca
import com.example.servervocavolley.dialogfragment.JoinDialogFragment
import com.example.servervocavolley.dialogfragment.SpinnerDialogFragment
import com.example.servervocavolley.dialogfragment.TextAlertDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject

class VocaActivity : AppCompatActivity(), JoinDialogFragment.JoinDialogListener,
    SpinnerDialogFragment.SpinnerDialogListener {
    private lateinit var binding: ActivityVocaBinding
    private lateinit var spf: SharedPreferences
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val baseURL = "https://skek933.cafe24.com/"
    private val getLogin = "study/login.php"
    private val getVocaSelect = "study/voca_select.php"
    private val getVocaInsert = "study/voca_insert.php"
    private val getVocaDelete = "study/voca_delete.php"
    private val getVocaUpdate = "study/voca_update.php"
    private val getVocaSelectAll = "study/voca_select_all.php"
    private var myVocaList = mutableListOf<Voca>()
    private var allVocaList = mutableListOf<Voca>()
    private var token = ""
    private var isBackPressed = false
    private var isDeleteButtonClicked = false
    private var joinDialogFragment = JoinDialogFragment("추가하기", "영어", "한글")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVocaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        initView()
        clickedButton()
    }

    private fun initView() {
        spf = getSharedPreferences("saveId", MODE_PRIVATE)
        viewPagerAdapter = ViewPagerAdapter(this as FragmentActivity, myVocaList, allVocaList)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "모두보기"
                else -> "내꺼보기"
            }
        }.attach()

        var queue = Volley.newRequestQueue(this)
        var url = baseURL + getLogin

        var stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                var result = JSONObject(response).getString("result")
                if (result == "OK") {
                    token = JSONObject(response).getString("token")
                    binding.memberInfoTextView.text = intent.getStringExtra("id") + " 님"
                    loadData()
                } else {
                    logout()
                }
            }, {}
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = intent.getStringExtra("id")!!
                params["password"] = intent.getStringExtra("password")!!
                // 단방향: sha256
                // 양방향: AES256 - key + 데이터
                return params
            }
        }
        queue.add(stringRequest)
    }


    private fun clickedButton() {
        binding.logoutTextView.setOnClickListener {
            logout()
        }

        binding.insertTextView.setOnClickListener {
            joinDialogFragment.show(supportFragmentManager, "insert")
        }

        binding.updateTextView.setOnClickListener {
            isDeleteButtonClicked = false
            SpinnerDialogFragment(this@VocaActivity, myVocaList, isDeleteButtonClicked).show(
                supportFragmentManager,
                "deleteFragment"
            )
        }

        binding.deleteTextView.setOnClickListener {
            isDeleteButtonClicked = true
            SpinnerDialogFragment(this@VocaActivity, myVocaList, isDeleteButtonClicked).show(
                supportFragmentManager,
                "deleteFragment"
            )
        }

        binding.refreshTextView.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            loadData()
        }

        binding.openMenuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.closeMenuButton.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        }

    }

    private fun logout() {
        spf.edit {
            putString("id", null)
            putString("password", null)
        }
        val intent = Intent(this@VocaActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadData() {
        var queue = Volley.newRequestQueue(this)
        if (token != "") {
            var urlToken = "?token=$token"
            var url = baseURL + getVocaSelect + urlToken

            var stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    myVocaList.clear()
                    var jsonObject = JSONObject(response)
                    if (!jsonObject.isNull("voca_list")) {
                        var jsonArray = jsonObject.getJSONArray("voca_list")
                        for (i in 0 until jsonArray.length()) {
                            myVocaList.add(
                                Voca(
                                    jsonArray.getJSONObject(i).getString("eng"),
                                    jsonArray.getJSONObject(i).getString("kor"),
                                    jsonArray.getJSONObject(i).getInt("idx")
                                )
                            )
                        }
                        viewPagerAdapter.myDataChange()
                    }
                }, {}
            )
            queue.add(stringRequest)
        }

        var url = baseURL + getVocaSelectAll
        var stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                var jsonObject = JSONObject(response)
                if (!jsonObject.isNull("voca_list")) {
                    var jsonArray = jsonObject.getJSONArray("voca_list")
                    allVocaList.clear()
                    for (i in 0 until jsonArray.length()) {
                        allVocaList.add(
                            Voca(
                                jsonArray.getJSONObject(i).getString("eng"),
                                jsonArray.getJSONObject(i).getString("kor"),
                                jsonArray.getJSONObject(i).getInt("idx")
                            )
                        )
                    }
                    viewPagerAdapter.allDataChange()
                }
            }, {}
        )
        queue.add(stringRequest)
    }

    override fun joinClickedPassButton(dialog: JoinDialogFragment, first: String, second: String) {
        //first: 영단어 second: 한글
        val queue = Volley.newRequestQueue(this)
        var urlEng = "?eng=$first"
        var urlKor = "&kor=$second"
        var urlToken = "&token=$token"
        var url = baseURL + getVocaInsert + urlEng + urlKor + urlToken
        var stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                var result = JSONObject(response).getString("result")
                if (result == "OK") {
                    super.joinClickedPassButton(dialog, first, second)
                    joinDialogFragment.dismiss()
                    loadData()
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                }
            }, {}
        )
        queue.add(stringRequest)
    }

    override fun joinRequestData(dialog: DialogFragment) {
        val message = "빈칸 없이 입력하세요."
        createAlertDialog(message)
    }

    private fun createAlertDialog(message: String) {
        TextAlertDialogFragment(message).show(supportFragmentManager, "alertDialog")
    }

    override fun spinnerClickedPassButton(
        dialog: DialogFragment,
        eng: String,
        kor: String,
        idx: Int
    ) {
        val queue = Volley.newRequestQueue(this)
        // isClickedButton 0:update 1:delete
        var idxUrl = "?idx=$idx"
        var deleteUrl = baseURL + getVocaDelete + idxUrl
        var deleteRequest = StringRequest(
            Request.Method.GET, deleteUrl,
            { response ->
                var result = JSONObject(response).getString("result")
                if (result == "OK") {
                    dialog.dismiss()
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                    createAlertDialog("삭제 완료!")
                    loadData()
                    isDeleteButtonClicked = false
                } else {
                    createAlertDialog("삭제 실패!")
                }
            }, {
                createAlertDialog("삭제 실패!")
            }
        )

        var engUrl = "&eng=$eng"
        var korUrl = "&kor=$kor"
        var updateUrl = baseURL + getVocaUpdate + idxUrl + engUrl + korUrl
        var updateRequest = StringRequest(
            Request.Method.GET, updateUrl,
            { response ->
                var result = JSONObject(response).getString("result")
                if (result == "OK") {
                    dialog.dismiss()
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
                    createAlertDialog("수정 완료!")
                    loadData()
                } else {
                    createAlertDialog("수정 실패!")
                }
            }, {
                createAlertDialog("수정 실패!")
            }
        )


        if (isDeleteButtonClicked) {
            queue.add(deleteRequest)
        } else {
            queue.add(updateRequest)
        }
    }

    override fun spinnerClickedCancelButton(dialog: DialogFragment) {
        isDeleteButtonClicked = false
    }

    override fun spinnerIsEmptyEditText(dialog: DialogFragment) {
        var message = "변경할 단어를 입력하세요."
        createAlertDialog(message)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            if (isBackPressed) {
                // todo 여기서 finish를 하는게 맞는지 super.onBackPressed를 하는게 맞는지 확인
                finish()
//                super.onBackPressed()
            } else {
                Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                isBackPressed = true
                handler.sendEmptyMessageDelayed(0, 1500)
            }
        }
    }

    var handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 0: isBackPressed 변경
            if (msg.what == 0) {
                isBackPressed = false
            }
        }
    }

}