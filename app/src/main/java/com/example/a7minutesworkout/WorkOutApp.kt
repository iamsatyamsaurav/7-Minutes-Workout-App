package com.example.a7minutesworkout

import android.app.Application
import eu.tutorials.a7_minutesworkoutapp.HistoryDatabase

class WorkOutApp: Application() {

    val db: HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}