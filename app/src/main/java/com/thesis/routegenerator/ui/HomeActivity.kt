package com.thesis.routegenerator.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.thesis.routegenerator.R

class HomeActivity : AppCompatActivity()  {


    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_SplashScreen)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val button = findViewById<Button>(R.id.home_next_btn)
        button.setOnClickListener{
            switchActivity()
        }
    }

    private fun switchActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}