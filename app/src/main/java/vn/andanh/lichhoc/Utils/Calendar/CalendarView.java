package vn.andanh.lichhoc.Utils.Calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;

import vn.andanh.lichhoc.R;


public class CalendarView extends LinearLayout implements AdapterView.OnItemClickListener {
    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 35;
    private OnDayClickListener dayListener;
    private OnMonthClickListener monthListener;
    ArrayList<EventCalendar> cells;
    // current displayed month
    private Calendar currentDate = Calendar.getInstance();
    private LinkedHashMap<Integer, EventCalendar> events;
    private String dateFormat;
    private static int MOUNTH = 1;
    // internal components
    private LinearLayout header;
    private RelativeLayout btnPrev;
    private RelativeLayout btnNext;
    private TextView txtDate;
    private GridView grid;
    public static int textSize = 0;
    public static int textNumber = 0;
    // seasons' rainbow
    int[] rainbow = new int[]{
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[]{2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};


    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);
        try {
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            String DATE_FORMAT = "yyyy/MM";
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    public void setEvent(LinkedHashMap<Integer, EventCalendar> event) {
        this.events = event;
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = (LinearLayout) findViewById(R.id.calendar_header);
        btnPrev = (RelativeLayout) findViewById(R.id.calendar_prev);
        btnNext = (RelativeLayout) findViewById(R.id.calendar_next);
        txtDate = (TextView) findViewById(R.id.calendar_date_display);
        grid = (GridView) findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
                Calendar mDay = new GregorianCalendar(1900, 1, 1);
                Calendar maxDay = new GregorianCalendar(1900, 1, 1);

                mDay.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                mDay.set(Calendar.MONTH, MOUNTH);
                mDay.set(Calendar.DAY_OF_MONTH, currentDate.getActualMinimum(Calendar.DAY_OF_MONTH));

                maxDay.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                maxDay.set(Calendar.MONTH, MOUNTH);
                maxDay.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                maxDay.set(Calendar.HOUR_OF_DAY, 23);
                maxDay.set(Calendar.MINUTE, 59);
                maxDay.set(Calendar.SECOND, 59);
                if(monthListener != null) {
                    monthListener.onMonthClicked(mDay.getTime(), maxDay.getTime());
                }
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
                Calendar mDay = new GregorianCalendar(1900, 1, 1);
                Calendar maxDay = new GregorianCalendar(1900, 1, 1);
                mDay.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                mDay.set(Calendar.MONTH, MOUNTH);
                mDay.set(Calendar.DAY_OF_MONTH, currentDate.getActualMinimum(Calendar.DAY_OF_MONTH));

                maxDay.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                maxDay.set(Calendar.MONTH, MOUNTH);
                maxDay.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                maxDay.set(Calendar.HOUR_OF_DAY, 23);
                maxDay.set(Calendar.MINUTE, 59);
                maxDay.set(Calendar.SECOND, 59);
                if(monthListener != null) {
                    monthListener.onMonthClicked(mDay.getTime(), maxDay.getTime());
                }
            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {
        cells = new ArrayList<>();
        Calendar calendar = (Calendar) currentDate.clone();
        MOUNTH = calendar.get(Calendar.MONTH);
        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            EventCalendar event = new EventCalendar();
            event.date = calendar.getTime();
            cells.add(event);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events, MOUNTH));

        // update title
        // chuyển title  theo ngôn ngữ
        /*if (!YukiPreferences.getLanguage().equals("日本語")) {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            txtDate.setText(format.format(currentDate.getTime().getTime()));
        } else {*/
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate.getTime());
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);

            String yearMonth = "";
            if(month < 10){
                yearMonth = year + "/0" + month;
            }else{
                yearMonth = year + "/" + month;
            }

            txtDate.setText(yearMonth);
        /*}*/
        // set header color according to current season
        int months = currentDate.get(Calendar.MONTH);
        int season = monthSeason[months];
        int color = rainbow[season];

        header.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EventCalendar evDay = (EventCalendar) parent.getItemAtPosition(position);
        if (dayListener != null) {
            Date d = evDay.date;
            dayListener.onDayClicked(parent, view, position, id, d);
        }
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        if (grid != null) {
            dayListener = listener;
            grid.setOnItemClickListener(this);
        }
    }

    public void onMonthChangedListener(OnMonthClickListener listener) {
        if (grid != null) {
            monthListener = listener;
        }
    }


    public interface OnDayClickListener {
        void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Date day);
    }

    public interface OnMonthClickListener {
        void onMonthClicked(Date fromday, Date toDate);
    }

    private class CalendarAdapter extends ArrayAdapter<EventCalendar> {
        // days with events
        private LinkedHashMap<Integer, EventCalendar> eventDays;
        Context context;
        // for view inflation
        private LayoutInflater inflater;
        private int Yuki_MONTH = 0;
        int DateLoaded = -1;

        CalendarAdapter(Context context, ArrayList<EventCalendar> days, LinkedHashMap<Integer, EventCalendar> eventDays, int Month) {
            super(context, R.layout.calendar_day, days);
            this.context = context;
            this.eventDays = eventDays;
            Yuki_MONTH = Month;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getPosition(EventCalendar item) {
            return super.getPosition(item);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Nullable
        @Override
        public EventCalendar getItem(int position) {
            return super.getItem(position);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            // day in question
            if (DateLoaded != position) {
                Date date = getItem(position).date;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                Calendar today = Calendar.getInstance();

                // inflate item if it does not exist yet
                if (view == null) {
                    view = inflater.inflate(R.layout.calendar_day, parent, false);
                }
                // if this day has an event, specify event image
                view.setBackgroundResource(0);
                if (month != Yuki_MONTH) {
                    // if this day is outside current month, grey it out
                /*((TextView) view.findViewById(R.id.day)).setTextColor(ContextCompat.getColor(context, R.color.greyed_out));*/
                    view.setVisibility(GONE);
                    return view;
                }
                // today
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.itemDate);
                layout.setOrientation(LinearLayout.VERTICAL);
                LayoutParams relativeParams = new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                if (eventDays != null) {
                    int xBreak = 1;
                    ArrayList<String> arrEvent = new ArrayList<>();
                    for (Integer integer : eventDays.keySet()) {
                        if (xBreak > 3) {
                            break;
                        }
                        EventCalendar eventDate = eventDays.get(integer);
                        Calendar c = Calendar.getInstance();
                        c.setTime(eventDate.date);
                        if (Yuki_MONTH == month) {
                            if (c.get(Calendar.DATE) == day && c.get(Calendar.MONTH) == month && c.get(Calendar.YEAR) == year) {
                                if (!arrEvent.contains(eventDate.title)) {
                                    TextView tv = new TextView(context);
                                    tv.setText(eventDate.title);
                                    arrEvent.add(eventDate.title);
                                    tv.setTextSize(7);
                                    tv.setTextColor(ContextCompat.getColor(context, R.color.grid_row_bg));
                                    relativeParams.setMargins(2, 0, 2, 2);
                                    layout.addView(tv, relativeParams);
                                    tv.setBackgroundResource(eventDate.drawable);
                                    tv.setGravity(Gravity.CENTER);
                                    xBreak++;
                                }
                            }
                        }
                    }
                }


                // clear styling
                ((TextView) view.findViewById(R.id.day)).setTypeface(null, Typeface.NORMAL);
                ((TextView) view.findViewById(R.id.day)).setTextColor(Color.BLACK);
                if (textNumber != 0) {
                    ((TextView) view.findViewById(R.id.day)).setTextSize(textNumber);
                }

                // set color date of week
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int x = c.get(Calendar.DAY_OF_WEEK);
                switch (x) {
                    case Calendar.SUNDAY:
                        ((TextView) view.findViewById(R.id.day)).setTextColor(ContextCompat.getColor(context, R.color.checkin_suspension));
                        break;
                    case Calendar.SATURDAY:
                        ((TextView) view.findViewById(R.id.day)).setTextColor(ContextCompat.getColor(context, R.color.color_light_blue));
                        break;
                    default:
                        break;
                }


                if (day == today.get(Calendar.DATE) && month == today.get(Calendar.MONTH) && year == today.get(Calendar.YEAR)) {
                    // if it is today, set it to blue/bold
                    ((TextView) view.findViewById(R.id.day)).setTypeface(null, Typeface.BOLD);
                    ((TextView) view.findViewById(R.id.day)).setTextColor(ContextCompat.getColor(context, R.color.today));
                }


                // set text
                ((TextView) view.findViewById(R.id.day)).setText(String.valueOf(date.getDate()));

                DateLoaded = position;
            }
            return view;
        }
    }
}
