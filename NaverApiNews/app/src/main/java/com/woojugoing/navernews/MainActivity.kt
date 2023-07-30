package com.woojugoing.navernews

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.woojugoing.navernews.databinding.ActivityMainBinding
import com.woojugoing.navernews.databinding.RowBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

  lateinit var activityMainBinding: ActivityMainBinding
  private val adapter = Adapter()
  private val newsList: MutableList<Items> = mutableListOf()

  val clientID = "EaaZXbc7EIQKr6IByAYs"
  val clientSecret = "bLbzmeTBSN"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(activityMainBinding.root)

    activityMainBinding.run {
      buttonInfo.run {
        setOnClickListener {
          val builder = AlertDialog.Builder(context)
          builder.run {
            setTitle("검색 부가 기능")
            setMessage(
                """
                      [1] 여러 페이지 이동 가능
                      [2] 뉴스 정렬 가능 ( 정확도 / 최신 )
                  """
                    .trimIndent())
            setPositiveButton("확인", null)
            show()
          }
        }
      }
      searchView.run {
        var sort = "date"
        var start = 1
        inflateMenu(R.menu.menu)

        editText.setOnEditorActionListener { v, actionId, event ->
          searchNews("${searchView.editText.text}", start, sort)
          false
        }

        setOnMenuItemClickListener {
          when (it.itemId) {
            R.id.sort -> {
              if (sort == "date") {
                sort = "sim"
                start = 1
                Toast.makeText(this@MainActivity, "뉴스를 정확도 순으로 검색합니다.", Toast.LENGTH_SHORT).show()
                searchNews("${searchView.editText.text}", start, sort)
              } else if (sort == "sim") {
                sort = "date"
                start = 1
                Toast.makeText(this@MainActivity, "뉴스를 최신 순으로 검색합니다.", Toast.LENGTH_SHORT).show()
                searchNews("${searchView.editText.text}", start, sort)
              }
              false
            }
            R.id.next -> {
              start += 15
              searchNews("${searchView.editText.text}", start, sort)
              if (start >= 100) {
                start = 1
                Toast.makeText(this@MainActivity, "뉴스의 정확도를 위해 리셋 했습니다.", Toast.LENGTH_SHORT).show()
                searchNews("${searchView.editText.text}", start, sort)
              }
            }
            R.id.search -> {
              searchNews("${searchView.editText.text}", start, sort)
            }
          }
          true
        }
      }

      recyclerView.run {
        adapter = this@MainActivity.adapter
        layoutManager = LinearLayoutManager(this@MainActivity)
        addItemDecoration(
            MaterialDividerItemDecoration(context, MaterialDividerItemDecoration.VERTICAL))
      }
    }
  }

  private fun searchNews(query: String, start: Int, sort: String) {
    val retrofit =
        Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val api = retrofit.create(NaverAPI::class.java)
    val callGetSearchNews = api.getSearchNews(clientID, clientSecret, query, 15, start, sort)
    callGetSearchNews.enqueue(
        object : Callback<ResultGetSearchNews> {
          override fun onResponse(
              call: Call<ResultGetSearchNews>,
              response: Response<ResultGetSearchNews>
          ) {
            val result = response.body()
            result?.items?.let {
              adapter.setNewsItems(it)
              adapter.notifyDataSetChanged()
            }
            Log.d("성공", "${response.raw()}")
          }
          override fun onFailure(call: Call<ResultGetSearchNews>, t: Throwable) {}
        })
  }

  inner class Adapter() : RecyclerView.Adapter<Adapter.HolderClass>() {

    fun setNewsItems(items: List<Items>) {
      newsList.clear()
      newsList.addAll(items)
      notifyDataSetChanged()
    }

    inner class HolderClass(rowBinding: RowBinding) : RecyclerView.ViewHolder(rowBinding.root) {
      val textViewRow: TextView

      init {
        textViewRow = rowBinding.textViewRow
        rowBinding.root.setOnClickListener {
          var intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsList[adapterPosition].originallink))
          startActivity(intent)
        }
      }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClass {
      val rowBinding = RowBinding.inflate(layoutInflater)
      val holderClass = HolderClass(rowBinding)

      val params =
          LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
      rowBinding.root.layoutParams = params

      return holderClass
    }

    override fun getItemCount(): Int {
      return newsList.size
    }

    override fun onBindViewHolder(holder: HolderClass, position: Int) {
      var title = newsList[position].title
      title = Html.fromHtml(title).toString()
      holder.textViewRow.text = title
    }
  }
}

data class ResultGetSearchNews(
    var lastBuildDate: String = "",
    var total: Int = 0,
    var start: Int = 0,
    var display: Int = 0,
    var items: List<Items>
)

data class Items(
    var title: String = "",
    var originallink: String = "",
    var link: String = "",
    var description: String = "",
    var pubDate: String = ""
)

interface NaverAPI {
  @GET("v1/search/news.json")
  fun getSearchNews(
      @Header("X-Naver-Client-Id") clientId: String,
      @Header("X-Naver-Client-Secret") clientSecret: String,
      @Query("query") query: String,
      @Query("display") display: Int? = null,
      @Query("start") start: Int? = null,
      @Query("sort") sort: String = "sim"
  ): Call<ResultGetSearchNews>
}

// 새로운 UI 장점
// 전체적인 속도가 빨라졌다고 생각함
// UI가 전 버전 보다 가시성이 좋음

// 새로운 UI 단점
// SqliteBrowser Plugin 사용 불가. 새로운 플긴 찾아봐야 됨
// xml 파일 Code/Design 변경 버튼으로 불가
//
