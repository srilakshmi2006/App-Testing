package com.example.oralpred

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val pointsContainer = findViewById<LinearLayout>(R.id.pointsContainer)
        val btnEmailHelp = findViewById<Button>(R.id.btnEmailHelp)

        backBtn.setOnClickListener { finish() }

        val title = intent.getStringExtra("TITLE") ?: "Information"
        val points = intent.getStringArrayExtra("POINTS") ?: arrayOf()
        val showHelp = intent.getBooleanExtra("SHOW_HELP", false)

        tvTitle.text = title

        for (point in points) {
            val tv = TextView(this).apply {
                text = point
                setTextColor(android.graphics.Color.parseColor("#334155"))
                textSize = 16f
                setPadding(0, 0, 0, 24)
            }
            pointsContainer.addView(tv)
        }

        if (showHelp) {
            btnEmailHelp.visibility = View.VISIBLE
            btnEmailHelp.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@oralpred.com")
                }
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }
        }
    }
}
