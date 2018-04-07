package cog.com.kotlin_google_plus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.squareup.timessquare.CalendarPickerView
import java.util.*





class CcalenderActivity : AppCompatActivity() {

    var calendar_view: CalendarPickerView? = null
    var calendar: Calendar? = null
    var date: Date? = null
    var dates: List<Date>? = null
    var btn_show_dates: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ccalender)

        calendar_view= findViewById<CalendarPickerView>(R.id.calendar_view)
        btn_show_dates= findViewById<Button>(R.id.btn_show_dates)

        calendar = Calendar.getInstance()
        calendar?.add(Calendar.MONTH,6)

        date = Date()

        calendar_view?.init(date, calendar?.time)?.inMode(CalendarPickerView.SelectionMode.RANGE)

        calendar_view?.setOnDateSelectedListener(object : CalendarPickerView.OnDateSelectedListener {
            override fun onDateSelected(date: Date) {

                Toast.makeText(applicationContext, "Selected Date : " + date.toString(), Toast.LENGTH_SHORT).show()

            }

            override fun onDateUnselected(date: Date) {

                Toast.makeText(applicationContext, "UnSelected Date : " + date.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        dates = calendar_view?.selectedDates
        btn_show_dates?.setOnClickListener {
            for (i in 0 until calendar_view?.getSelectedDates()!!.size) {

                //here you can fetch all dates
                Toast.makeText(applicationContext, "show dates "+calendar_view!!.getSelectedDates()!!.size, Toast.LENGTH_SHORT).show()

            }
        }

    }
}
