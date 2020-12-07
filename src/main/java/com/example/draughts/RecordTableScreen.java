package com.example.draughts;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
///A RecordTableScreen class is a responsibility for showing Times Records of Users
public class RecordTableScreen extends AppCompatActivity {
    TextView title;
    List<Map<String, String>> data;
    SharedPreferences sp;//dataBase

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_table_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        data = new ArrayList<>();
        final ListView record_list = findViewById(R.id.data_table);
        SetData();
        String [ ] from = {"name","time"};
        int [] to = {R.id.user_name, R.id.game_time};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data ,R.layout.cells_table,from,to);
        record_list.setAdapter(simpleAdapter);
        title = findViewById(R.id.title_record);
        NameLevel();
    }
    //A function Set Data of Layout by level Selected
    private void SetData() {
        String tag = getIntent().getStringExtra("level");
        sp = getSharedPreferences(tag, MODE_PRIVATE);

        HashMap<String, String> keys = (HashMap<String, String>) sp.getAll();
        HashMap<String, Winner> map = new  HashMap<String, Winner>();
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            map.put(entry.getKey(),new Winner(entry.getKey(),tag,entry.getValue()));
        }
        List<Winner> winnerSorted = new ArrayList<>(map.values());
        Collections.sort(winnerSorted);
        for (Winner a : winnerSorted) {
            HashMap<String, String> i = new HashMap<>();
            i.put("name", a.user_name);
            i.put("time", a.time);
            data.add(i);
        }
    }
    private void  NameLevel() {
       String temp = getIntent().getStringExtra("level");
        if(temp.equals("2"))
            title.setText(R.string.easy_level);
        if(temp.equals("3"))
            title.setText(R.string.medium_level);
        if(temp.equals("4"))
            title.setText(R.string.difficult_level);
    }
}


