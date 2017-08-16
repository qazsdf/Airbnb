package com.android.airbnb.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.airbnb.DetailHouseActivity;
import com.android.airbnb.R;
import com.android.airbnb.domain.reservation.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomCalendar extends AppCompatActivity implements View.OnClickListener, GridAdapter.OnTextChangedListener {

    private ImageView calendarBtnClose;
    private TextView calendarDelete;
    private TextView checkinDateTxt;
    private TextView checkoutDateTxt;
    private TextView calendarResultTxt;
    private RecyclerView calendarRecycler;
    private Button calendarBtnSave;
    private CalendarData data;
    private List<CalendarData> calendarDatas;
    private EndlessScrollListener scrollListener;
    private CustomCalendarAdapter calendarAdapter;

    public static String beginDate = "";
    public static String endDate = "";
    private String housePk = "";
    private List<Reservation> reservations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_calendar);
        calendarDatas = new ArrayList<>();
        getCalendarData();
        initView();
        getHousePk();
        setOnClick();
        setAdapter();
        setOnScrollListener();
    }

    private void getHousePk() {
        Intent intent = getIntent();
        housePk = intent.getStringExtra(DetailHouseActivity.HOUSE_PK);
    }


    // 추후
    private void getCalendarData() {

        List<String> days;
        int dayCount = 0;
        int currentYear = Integer.parseInt(Utils.DateUtil.getCurrentYear());
        int currentMonth = Integer.parseInt(Utils.DateUtil.getCurrentMonth());
        Log.e("CustomCalendar", "current month : " + currentMonth);

        int tempMonth = 0;
        // Utils로 추후에 빼기
        // 12월 넘을 경우, 년도 ++ 처리 및 월 reset
        for (int i = currentMonth; i <= currentMonth + 12; i++) {
            tempMonth = i;
            if (i > 12) {
                tempMonth = i - 12;
                if (i % 12 == 1) {
                    currentYear++;
                }
            }
            days = new ArrayList<>();
            dayCount = Utils.CalendarUtil.getDaysInMonth(currentYear, tempMonth);
            data = new CalendarData();
            data.setFirstWeekDay(Utils.CalendarUtil.getFirstWeekDay(currentYear, tempMonth));

            for (int k = 1; k < data.getFirstWeekDay(); k++) {
                days.add(" ");
            }

            for (int j = 1; j <= dayCount; j++) {
                days.add(j + "");
            }
            data.setYear(currentYear + "");
            data.setDays(days);
            data.setMonth(tempMonth + "");
            data.setWeekDaysCount(Utils.CalendarUtil.getWeekCount(currentYear, tempMonth));
            Log.e("CustomCalendar", data.toString());
            calendarDatas.add(data);
            Log.e("CustomCalendar", "month :: " + tempMonth + ", data size :: " + calendarDatas.size());
        }
    }

    private LinearLayoutManager manager = new LinearLayoutManager(this);

    private void setAdapter() {
        calendarAdapter = new CustomCalendarAdapter(calendarDatas, housePk, this, this);
        calendarRecycler.setAdapter(calendarAdapter);
        calendarRecycler.setLayoutManager(manager);
        calendarRecycler.setItemViewCacheSize(12);
    }

    // ========================== [ scrollListener code ] ========================================
    // 위쪽 혹은 아래쪽에 새로운 달력 데이터를 로드해야할 경우, setOnScrollListener를 통해 이벤트를 감지한다.
    private void setOnScrollListener() {
        scrollListener = new EndlessScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        calendarRecycler.addOnScrollListener(scrollListener);
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    private void sadjflkjsdlfkj() {
        // 1. First, clear the array of data
        calendarDatas.clear();
        // 2. Notify the adapter of the update
        calendarAdapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
        // 3. Reset endless scroll listener when performing a new search
        scrollListener.resetState();
    }

    // ================================================================================================
    private void initView() {
        calendarBtnClose = (ImageView) findViewById(R.id.calendar_btn_close);
        calendarDelete = (TextView) findViewById(R.id.calendar_delete);
        checkinDateTxt = (TextView) findViewById(R.id.checkin_date_txt);
        checkoutDateTxt = (TextView) findViewById(R.id.checkout_date_txt);
        calendarRecycler = (RecyclerView) findViewById(R.id.calendar_list);
        calendarBtnSave = (Button) findViewById(R.id.calendar_btn_Save);
        calendarResultTxt = (TextView) findViewById(R.id.calendar_result_txt);
    }

    private void setOnClick() {
        calendarBtnClose.setClickable(true);
        calendarDelete.setClickable(true);
        calendarDelete.setClickable(true);

        calendarBtnClose.setOnClickListener(this);
        calendarBtnSave.setOnClickListener(this);
        calendarDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_btn_close:
                Toast.makeText(v.getContext(), "close", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;

            case R.id.calendar_btn_Save:
                Toast.makeText(v.getContext(), "save", Toast.LENGTH_SHORT).show();
                break;

            case R.id.calendar_delete:
                Toast.makeText(v.getContext(), "del", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String calculatePeriod(String beginDate, String endDate) {
        long result = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date formattedBegin = sdf.parse(beginDate);
            Date formattedEnd = sdf.parse(endDate);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = formattedEnd.getTime() - formattedBegin.getTime();
            result = diff / (24 * 60 * 60 * 1000);
            System.out.println("날짜차이=" + result);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result + "";
    }

    private boolean flag = false;

    @Override
    public void checkInChanged(String selectedCheckInDate) {
        Log.e("CustomCalendar", selectedCheckInDate);
        if (flag) {
            checkoutDateTxt.setText("");
            checkinDateTxt.setText("");
            calendarResultTxt.setText("체크인 날짜와 체크아웃 날짜를 선택해주세요.");
        }
        checkinDateTxt.setText(selectedCheckInDate);
    }

    @Override
    public void checkOutChanged(String selectedCheckOutDate) {
        checkoutDateTxt.setText(selectedCheckOutDate);
        String result = calculatePeriod(beginDate, endDate);
        Log.e("CustomCalendar", "do calendarAdapter.findSelectedGridAdapter() ===== ");
        calendarAdapter.findSelectedGridAdapter();
        calendarAdapter.setSelectedHolders();
        calendarResultTxt.setText(result + "박을 선택했습니다.");
        flag = true;
    }

    @Override
    public void calculatedDate(String result) {
        // TODO 불필요할 경우, 반드시 코드 제거하기!
    }
}
