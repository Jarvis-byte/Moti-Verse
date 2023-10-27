package com.example.quotify.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class SplashScreen : AppCompatActivity() {
    lateinit var splashimg: ImageView
    lateinit var appname: TextView
    lateinit var made_by: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.quotify.R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        appname = findViewById<TextView>(com.example.quotify.R.id.textView)
        splashimg = findViewById<ImageView>(com.example.quotify.R.id.imageView)
        Glide.with(this).load(com.example.quotify.R.drawable.icon).into(splashimg)
        // lottieAnimationView = findViewById(R.id.lottie);
        // lottieAnimationView = findViewById(R.id.lottie);
        val topAnim: Animation =
            AnimationUtils.loadAnimation(this, com.example.quotify.R.anim.top_anim)
        val bottomAnim: Animation =
            AnimationUtils.loadAnimation(this, com.example.quotify.R.anim.bottom_anim)
        made_by = findViewById<TextView>(com.example.quotify.R.id.textView2)
        splashimg.setAnimation(topAnim)
        appname.setAnimation(bottomAnim)
        made_by.setAnimation(bottomAnim)
        splashimg.animate().translationY(-2500f).setDuration(1000).startDelay = 5000
        appname.animate().translationY(2000f).setDuration(1000).startDelay = 5000
        made_by.animate().translationY(1500f).setDuration(1000).startDelay = 5000

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        if (width <= 720) {
            println("LOL")
            appname.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(com.example.quotify.R.dimen.text_size_huge)
            )
        }

        Handler().postDelayed(Runnable {
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }, 2000)
    }
}