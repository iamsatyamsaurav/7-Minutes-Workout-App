package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private var binding:ActivityHistoryBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistoryActivity)  //This sets up the toolbarExercise view (likely a Toolbar defined in your layout) as the app's ActionBar

        if(supportActionBar!=null){  //Checks if the supportActionBar is not null (meaning it was successfully set).
            //If the supportActionBar is not null, this line adds a back button (an arrow) to the left side of the toolbar. This button is usually used for navigating back in the app.
            supportActionBar?.setDisplayHomeAsUpEnabled(true)  //this will show the back button on the toolbar
            supportActionBar!!.title="HISTORY"
        }


        //This sets up what happens when the user clicks the back button (the one added in the previous step)
        binding?.toolbarHistoryActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

        val historyDao=(application as WorkOutApp).db.historyDao()
        getAllCompletedDates(historyDao)
    }

    private fun getAllCompletedDates(historyDao: HistoryDao){
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect{
                allCompletedDatesList->
                if (allCompletedDatesList.isNotEmpty()) {
                    // Here if the List size is greater then 0 we will display the item in the recycler view or else we will show the text view that no data is available.
                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.GONE

                    // Creates a vertical Layout Manager
                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    // History adapter is initialized and the list is passed in the param.
                    val dates = ArrayList<String>()
                    for (date in allCompletedDatesList){
                        dates.add(date.date)
                    }
                    val historyAdapter = HistoryAdapter(ArrayList(dates))

                    // Access the RecyclerView Adapter and load the data into it
                    binding?.rvHistory?.adapter = historyAdapter
                } else {
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}