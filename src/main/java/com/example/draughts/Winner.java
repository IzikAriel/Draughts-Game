package com.example.draughts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Winner implements  Comparable<Winner> {

    public String user_name;
    public String level;
    public String time;

    public Winner(String user_name, String level, String time) {
        this.user_name = user_name;
        this.level = level;
        this.time = time;
    }

    @Override
    public int compareTo(Winner winner) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(this.time);
            Date date2 = sdf.parse(winner.time);

            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
