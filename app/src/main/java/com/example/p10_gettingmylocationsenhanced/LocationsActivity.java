package com.example.p10_gettingmylocationsenhanced;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class LocationsActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> al;
    ArrayAdapter<String> aa;

    String folderLocation;

    Button btnRefresh;
    TextView tvNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        btnRefresh = findViewById(R.id.btnRefresh);
        tvNum = findViewById(R.id.tvNum);
        lv = findViewById(R.id.lv);

        al = new ArrayList<String>();

        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
        File targetFile = new File(folderLocation, "data.txt");

        if (targetFile.exists() == true) {
            String data = "";
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);

                String line = br.readLine();
                while (line != null) {
                    al.add(line);
                    line = br.readLine();
                }

                br.close();
                reader.close();
            } catch (Exception e) {
                Toast.makeText(LocationsActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Log.d("Content", data);
        }

        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al);
        lv.setAdapter(aa);

        tvNum.setText("Number of records: " + al.size());

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.clear();

                folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
                File targetFile = new File(folderLocation, "data.txt");

                if (targetFile.exists() == true) {
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);

                        String line = br.readLine();
                        while (line != null) {
                            al.add(line);
                            line = br.readLine();
                        }

                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(LocationsActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Log.d("Content", data);
                }

                aa.notifyDataSetChanged();

                tvNum.setText("Number of records: " + al.size());

            }
        });

    }
}