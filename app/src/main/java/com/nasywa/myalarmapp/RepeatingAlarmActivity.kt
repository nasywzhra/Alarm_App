package com.nasywa.myalarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nasywa.myalarmapp.Fragment.TimePickerFragment
import com.nasywa.myalarmapp.databinding.ActivityMainBinding
import com.nasywa.myalarmapp.room.Alarm
import com.nasywa.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RepeatingAlarmActivity : AppCompatActivity(), View.OnClickListener,
    TimePickerFragment.DialogTimeListener {
    private var binding: ActivityMainBinding? = null

    private lateinit var alarmReceiver: AlarmReceiver

    val db by lazy { AlarmDB(this) }

    companion object {
        private const val TIME_PICKER_REPEAT_TAG = "TagPickerRepeat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_repeating_alarm)

        btn_set_time_repeating.setOnClickListener(this)
        btn_add_set_repeating_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_set_time_repeating -> {
                val timePickerFragmentReapiting = TimePickerFragment()
                timePickerFragmentReapiting.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
            }
            R.id.btn_add_set_repeating_alarm -> {
                val repeatTime = tv_repeating_time.text.toString()
                val repeatMessage = et_note_repeating.text.toString()

                alarmReceiver.setRepeatingAlarm(
                    this, AlarmReceiver.TYPE_REPEATING,
                    repeatTime,
                    repeatMessage
                )

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().AddAlarm(
                        Alarm(
                            0,
                            repeatTime,
                            "Repeating Alarm",
                            repeatMessage,
                            AlarmReceiver.TYPE_REPEATING
                        )
                    )
                    finish()
                }
            }

        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val timeFormatRepeting = SimpleDateFormat("HH:mm", Locale.getDefault())
        when (tag) {
            TIME_PICKER_REPEAT_TAG -> tv_repeating_time.text =
                timeFormatRepeting.format(calendar.time)
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }

}
