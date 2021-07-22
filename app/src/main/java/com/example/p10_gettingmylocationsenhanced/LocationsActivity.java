package com.example.p10_gettingmylocationsenhanced;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class LocationsActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> al, alFav;
    ArrayAdapter<String> aa, aaFav;

    String folderLocation;

    Button btnRefresh, btnFav;
    TextView tvNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        btnRefresh = findViewById(R.id.btnRefresh);
        btnFav = findViewById(R.id.btnFavourites);
        tvNum = findViewById(R.id.tvNum);
        lv = findViewById(R.id.lv);

        al = new ArrayList<String>();
        alFav = new ArrayList<String>();

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

        aaFav = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alFav);

        tvNum.setText("Number of records: " + al.size());

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.clear();

                folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
                File targetFile = new File(folderLocation, "data.txt");

                if (targetFile.exists() == true) {
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
                }

                aa.notifyDataSetChanged();

                tvNum.setText("Number of records: " + al.size());

            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alFav.clear();

                folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
                File targetFile = new File(folderLocation, "favourites.txt");

                if (targetFile.exists() == true) {
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);

                        String line = br.readLine();
                        while (line != null) {
                            alFav.add(line);
                            line = br.readLine();
                        }

                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(LocationsActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                aaFav.notifyDataSetChanged();
                lv.setAdapter(aaFav);

                tvNum.setText("Number of records: " + alFav.size());

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = al.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(LocationsActivity.this);
                builder.setMessage("Add this location in your favourite list?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
                                    File targetFile_I = new File(folderLocation, "favourites.txt");
                                    FileWriter writer_I = new FileWriter(targetFile_I, true);
                                    writer_I.write(selected + "\n");
                                    writer_I.flush();
                                    writer_I.close();
                                } catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "Failed to write!", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }
}