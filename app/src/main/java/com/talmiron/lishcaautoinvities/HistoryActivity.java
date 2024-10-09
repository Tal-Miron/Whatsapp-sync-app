package com.talmiron.lishcaautoinvities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements MeetingAdapter.OnItemClickListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // Initialize the SqlManager
        SqlManager.initialize(this);
        //SqlManager.DeleteRows(5);
        // Insert data
        //SqlManager.insert("שינוי תאריך", "סטטוס מפעל מידע אנשים מס' 2", "16/09", "16:30", "<<533312109>> ניסיון");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

/*        List<Meeting> meetingList = new ArrayList<>();

        // Add your meetings to the list
        for (int i = 1; i <= 148; i++) {
            meetingList.add(new Meeting(i, "Meeting " + i, "Title " + i, "27/08","17:15"));
        }*/

        List<Meeting> meetingList = Arrays.asList(SqlManager.GetHistoryActivityData());
        Collections.reverse(meetingList);
        MeetingAdapter adapter = new MeetingAdapter(meetingList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Meeting meeting) {
        String data = SqlManager.GetRawData(meeting.getId());
        Result result = new Result(data, this);
        Intent i = new Intent(this, ManageMessage.class);
        i.putExtra("result", result);
        startActivity(i);
        //Toast.makeText(this, "Clicked: " + meeting.getName(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Clicked: " + SqlManager.getRowCount(), Toast.LENGTH_SHORT).show();
    }


}



