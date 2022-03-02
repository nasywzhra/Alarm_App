package com.nasywa.myalarmapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nasywa.myalarmapp.adapter.AlarmAdapter
import com.nasywa.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmReceiver: AlarmReceiver
    val db by lazy { AlarmDB(this) }

    override fun onResume() {
        super.onResume()
        db.alarmDao().getAlarm().observe(this@MainActivity){
            alarmAdapter.setData(it)
            Log.d("MainActivity", "dbresponse: $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTimeToday()
        initDateToday()
        initAlarmType()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        alarmAdapter = AlarmAdapter()
        rv_reminder_alarm.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alarmAdapter

            swipeToDelete(this)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView?) {
        ItemTouchHelper(object  : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val typeOfAlarm = alarmAdapter.alarms[viewHolder.adapterPosition].type
                alarmReceiver.cancelAlarm(this@MainActivity, typeOfAlarm)

                val deleteTime = alarmAdapter.alarms[viewHolder.adapterPosition]

                //delete item
                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().deleteAlarm(deleteTime)
                }
                alarmAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(applicationContext, "Success Delete Alarm", Toast.LENGTH_LONG).show()
            }

        }).attachToRecyclerView(recyclerView)
        }



    private fun initAlarmType() {
        view_set_one_time_alarm.setOnClickListener {
            startActivity(Intent(this, OneTimeAlarmActivity::class.java))
        }
        view_set_repeating_alarm.setOnClickListener {
            startActivity(Intent(this, RepeatingAlarmActivity::class.java))
        }
    }


    private fun initDateToday() {
        val dateNow: Date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("E, dd MMM yyy", Locale.getDefault())
        val formattedDate: String = dateFormat.format(dateNow)

        tv_date_today.text = formattedDate
    }

    private fun initTimeToday() {
        val timeNow = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val formattedTime = timeFormat.format(timeNow.time)

        tv_time_today.text = formattedTime
    }
}