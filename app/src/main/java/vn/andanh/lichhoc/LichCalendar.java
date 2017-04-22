package vn.andanh.lichhoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import java.util.Date;

import vn.andanh.lichhoc.Utils.Calendar.CalendarView;

public class LichCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_calendar);
        CalendarView calendarView = (CalendarView) findViewById(R.id.calenDisplay);
        calendarView.onMonthChangedListener(new CalendarView.OnMonthClickListener() {
            @Override
            public void onMonthClicked(Date fromday, Date toDate) {

            }
        });
        calendarView.setOnDayClickListener(new CalendarView.OnDayClickListener() {
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Date day) {

            }
        });
    }
}
