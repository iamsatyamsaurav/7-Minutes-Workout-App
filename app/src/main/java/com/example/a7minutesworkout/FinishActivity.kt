package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.a7minutesworkout.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinishActivity : AppCompatActivity() {
    private var binding:ActivityFinishBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //for inserting into database
        val historyDao=(application as WorkOutApp).db.historyDao()
        addDateToDatabase(historyDao)


        setSupportActionBar(binding?.toolbarExercise)  //This sets up the toolbarExercise view (likely a Toolbar defined in your layout) as the app's ActionBar

        if(supportActionBar!=null){  //Checks if the supportActionBar is not null (meaning it was successfully set).
            //If the supportActionBar is not null, this line adds a back button (an arrow) to the left side of the toolbar. This button is usually used for navigating back in the app.
            supportActionBar?.setDisplayHomeAsUpEnabled(true)  //this will show the back button on the toolbar
        }


        //This sets up what happens when the user clicks the back button (the one added in the previous step). In this case, it will call onBackPressed(), which navigates the
        // user back to the previous screen or activity.
        binding?.toolbarExercise?.setNavigationOnClickListener{
            onBackPressed()
        }


        binding?.btnFinish?.setOnClickListener {
            finish()  //remove from the stack
        }


    }

    private fun addDateToDatabase(historyDao: HistoryDao){
        val c=Calendar.getInstance()
        val dateTime=c.time
        Log.i("Date:",""+dateTime)

        val sdf=SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date=sdf.format(dateTime)  //it will return formatted date as String
        Log.i("Formatted Date", date)

        lifecycleScope.launch{
            historyDao.insert(HistoryEntity(date))
            Log.i("Date is","added successfully")
        }
    }
}