package com.example.chexanhfe.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.chexanhfe.R
import com.example.chexanhfe.utils.AnimationUtils
import com.example.chexanhfe.utils.PosterPagerAdapter

class StartActivity : AppCompatActivity() {
    private lateinit var txtSkip: TextView
    private lateinit var txtContinue: TextView
    private lateinit var arrowIcon: ImageView // arrow_down (di chuyển cùng bảng)
    private lateinit var arrowBottom: ImageView // arrow trong bottom_bar
    private lateinit var slidingPanel: ConstraintLayout
    private lateinit var bottomBar: LinearLayout
    private lateinit var btnExperience: Button
    private lateinit var btnRegister: Button
    private var isPanelVisible = false
    private var initialTranslation = 0f
    private lateinit var posterPager: ViewPager2
    private var adapter: PosterPagerAdapter? = null
    private var poster = listOf(
        R.drawable.matcha,
        R.drawable.duongnhan,
        R.drawable.thaomoc,
    )

    private val handler = Handler(Looper.getMainLooper())
    private val scrollPoster = 4000L // cuộn 3s
    private var currentPage = 0

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = adapter?.itemCount ?: 0
            if (itemCount == 0) return

            val nextPage = (posterPager.currentItem + 1) % itemCount

            val pageWidth = posterPager.width
            if (nextPage != 0) {
                smoothScrollByFakeDrag(pageWidth, 400L) // 500ms trượt nhẹ
            } else {
                posterPager.setCurrentItem(0, true) // reset về đầu không animation
            }

            currentPage = nextPage
            handler.postDelayed(this, scrollPoster)
        }
    }

    fun smoothScrollByFakeDrag(distance: Int, duration: Long) {
        if (posterPager.beginFakeDrag()) {
            val interval = 10L
            val steps = (duration / interval).toInt()
            val delta = distance / steps

            var step = 0
            handler.post(object : Runnable {
                override fun run() {
                    if (posterPager.isFakeDragging && step < steps) {
                        posterPager.fakeDragBy(-delta.toFloat()) // -delta = cuộn sang phải
                        step++
                        handler.postDelayed(this, interval)
                    } else {
                        if (posterPager.isFakeDragging) {
                            posterPager.endFakeDrag()
                        }
                    }
                }
            })
        }
    }


    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.started)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        // Khởi tạo views
        txtSkip = findViewById(R.id.txtSkip)
        txtContinue = findViewById(R.id.txtContinue)
        arrowIcon = findViewById(R.id.arrow_down)
        arrowBottom = findViewById(R.id.arrow)
        slidingPanel = findViewById(R.id.sliding_panel)
        bottomBar = findViewById(R.id.bottom_bar)
        btnExperience = findViewById(R.id.experience_button)
        btnRegister = findViewById(R.id.register_button)


        posterPager = findViewById(R.id.posterRecyclerView)
        adapter = PosterPagerAdapter(poster)
        posterPager.adapter = adapter

        posterPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(autoScrollRunnable)
                    }

                    ViewPager2.SCROLL_STATE_IDLE -> {
                        handler.postDelayed(autoScrollRunnable, scrollPoster)
                    }
                }
            }
        })


        // Điều chỉnh vị trí ban đầu của sliding panel và arrow_down
        val panelHeight = 400 // Chiều cao panel_background (dp)
        val bottomBarHeight = 48 + 40 // Chiều cao ước tính của bottom_bar
        initialTranslation =
            (panelHeight + bottomBarHeight).toFloat() * resources.displayMetrics.density
        slidingPanel.translationY = initialTranslation
        arrowIcon.translationY = initialTranslation

        // Click listener cho các mũi tên để toggle bảng
        arrowIcon.setOnClickListener {
            togglePanel(initialTranslation)
        }
        arrowBottom.setOnClickListener {
            togglePanel(initialTranslation)
        }

        txtSkip.setOnClickListener {
            // { TODO }
        }

        txtContinue.setOnClickListener {
            togglePanel(initialTranslation)
        }


        btnExperience.setOnClickListener {
            // { TODO }
        }

        // chuyển sang activity đăng ký
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Touch listener cho root layout để đóng bảng khi chạm bên ngoài
        setupOutsideTouchListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOutsideTouchListener() {
        val rootLayout = findViewById<ConstraintLayout>(R.id.started)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isPanelVisible) {
                // Lấy tọa độ toàn cục của panel
                val panelRect = Rect()
                slidingPanel.getGlobalVisibleRect(panelRect)

                // Kiểm tra xem touch có nằm ngoài panel không
                if (!panelRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    // Trì hoãn để đảm bảo không bị trùng lặp với các sự kiện click khác
                    Handler(Looper.getMainLooper()).postDelayed({
                        togglePanel(initialTranslation)
                    }, 100)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Thêm phương thức bổ sung để xử lý xung đột touch event
        rootLayout.isClickable = true
        rootLayout.isFocusable = true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Kiểm tra nếu panel đang mở và người dùng chạm vào ngoài panel
        if (isPanelVisible && event.action == MotionEvent.ACTION_DOWN) {
            val panelRect = Rect()
            slidingPanel.getGlobalVisibleRect(panelRect)

            if (!panelRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                togglePanel(initialTranslation)
                return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoScrollRunnable, scrollPoster)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(autoScrollRunnable)
    }

    private fun togglePanel(initialTranslation: Float, forceSlideUp: Boolean = !isPanelVisible) {
        if (forceSlideUp) {
            AnimationUtils.slideUpPanel(slidingPanel, slidingPanel.translationY, bottomBar)
            AnimationUtils.slideUpPanel(arrowIcon, arrowIcon.translationY, bottomBar)
            isPanelVisible = true
        } else {
            AnimationUtils.slideDownPanel(
                slidingPanel, initialTranslation - slidingPanel.translationY, bottomBar
            )
            AnimationUtils.slideDownPanel(
                arrowIcon, initialTranslation - arrowIcon.translationY, bottomBar
            )
            isPanelVisible = false
        }
    }
}