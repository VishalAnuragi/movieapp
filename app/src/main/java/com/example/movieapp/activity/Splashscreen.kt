package com.example.movieapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.movieapp.R
import com.example.movieapp.activity.main.MainActivity
import com.example.movieapp.data.api.language
import kotlinx.android.synthetic.main.activity_splashscreen.*

class Splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        all.setOnClickListener { language = ""
        startNewActivity()
        }

        hindi.setOnClickListener { language = "hi"
            startNewActivity()
        }

        english.setOnClickListener { language = "en"
            startNewActivity()
        }


    }

    private fun startNewActivity() {
        val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
    }
}