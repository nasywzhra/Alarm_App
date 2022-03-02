package com.nasywa.myalarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nasywa.myalarmapp.Fragment.DatePickerFragment
import com.nasywa.myalarmapp.Fragment.TimePickerFragment
import com.nasywa.myalarmapp.databinding.ActivityMainBinding
import com.nasywa.myalarmapp.room.Alarm
import com.nasywa.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {
    private var binding: ActivityMainBinding? = null

    private lateinit var alarmReceiver: AlarmReceiver

    val db by lazy { AlarmDB(this) }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePicker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_one_time_alarm)

        btn_set_date_one_time.setOnClickListener(this)
        btn_set_time_one_time.setOnClickListener(this)

        btn_add_set_one_time_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_set_date_one_time -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            R.id.btn_set_time_one_time -> {
                val timePickerFragmentOneTime = TimePickerFragment()
                timePickerFragmentOneTime.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            R.id.btn_add_set_one_time_alarm -> {
                val onceDate = tv_once_date.text.toString()
                val onceTime = tv_once_time.text.toString()
                val onceMessage = tv_note_one_time.text.toString()

                alarmReceiver.setOneTimeAlarm(
                    this, AlarmReceiver.TYPE_ONE_TIME,
                    onceDate,
                    onceTime,
                    onceMessage
                )

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().AddAlarm(
                        Alarm(0, onceTime, onceDate, onceMessage, AlarmReceiver.TYPE_ONE_TIME)

                    )
                    finish()
                }
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dateFormatOneTime = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        tv_once_date.text = dateFormatOneTime.format(calendar.time)

    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormatOneTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        when (tag) {
            TIME_PICKER_ONCE_TAG -> tv_once_time.text = timeFormatOneTime.format(calendar.time)
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}