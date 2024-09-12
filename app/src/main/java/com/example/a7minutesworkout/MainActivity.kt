package com.example.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityMainBinding

//View binding in Android is a feature that generates a binding class for each XML layout file in your project.
// This binding class allows you to easily access views (like buttons, text fields, etc.) directly from the XML without needing to use findViewById().
// reduces the chance of error of accessing different element with same id
class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)  //root here is the main parent "constraint layout" of the activity_main.xml

        binding?.flStart?.setOnClickListener{
            val intent= Intent(this,ExerciseActivity::class.java)
            startActivity(intent)
        }
        binding?.flBMI?.setOnClickListener{
            val intent= Intent(this,BMIActivity::class.java)
            startActivity(intent)
        }
        binding?.flHistory?.setOnClickListener {
            val intent=Intent(this,HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}