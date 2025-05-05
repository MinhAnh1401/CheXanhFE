package com.example.chexanhfe.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chexanhfe.R
import com.example.chexanhfe.utils.AnimationUtils

class StartActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
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
        videoView = findViewById(R.id.layoutVideo)
        txtSkip = findViewById(R.id.txtSkip)
        txtContinue = findViewById(R.id.txtContinue)
        arrowIcon = findViewById(R.id.arrow_down)
        arrowBottom = findViewById(R.id.arrow)
        slidingPanel = findViewById(R.id.sliding_panel)
        bottomBar = findViewById(R.id.bottom_bar)
        btnExperience = findViewById(R.id.experience_button)
        btnRegister = findViewById(R.id.register_button)

        // Cài đặt VideoView để phát video
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.video_start}")
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }

        // Điều chỉnh vị trí ban đầu của sliding panel và arrow_down
        val panelHeight = 400 // Chiều cao panel_background (dp)
        val bottomBarHeight = 48 + 40 // Chiều cao ước tính của bottom_bar
        initialTranslation = (panelHeight + bottomBarHeight).toFloat() * resources.displayMetrics.density
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Touch listener cho root layout để đóng bảng khi chạm bên ngoài
        val rootLayout = findViewById<ConstraintLayout>(R.id.started)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isPanelVisible) {
                val panelRect = Rect()
                slidingPanel.getHitRect(panelRect)
                if (!panelRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    togglePanel(initialTranslation)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePanel(initialTranslation: Float, forceSlideUp: Boolean = !isPanelVisible) {
        if (forceSlideUp) {
            AnimationUtils.slideUpPanel(slidingPanel, slidingPanel.translationY, bottomBar)
            AnimationUtils.slideUpPanel(arrowIcon, arrowIcon.translationY, bottomBar)
            isPanelVisible = true
        } else {
            AnimationUtils.slideDownPanel(slidingPanel, initialTranslation - slidingPanel.translationY, bottomBar)
            AnimationUtils.slideDownPanel(arrowIcon, initialTranslation - arrowIcon.translationY, bottomBar)
            isPanelVisible = false
        }
    }
}