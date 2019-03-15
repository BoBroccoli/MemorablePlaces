package com.example.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public void addPlace(View view){
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);
    }
    static ArrayList<String> places = new ArrayList<String>();
    static ArrayList<LatLng> locations = new ArrayList<>();
    static ArrayList<String> latitude = new ArrayList<>();
    static ArrayList<String> longitude = new ArrayList<>();
    static  ListView listView;
    static  ArrayAdapter<String> adapter;
    SharedPreferences sharedPreferences;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);

        places =(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",  ObjectSerializer.serialize(new ArrayList<String>())));
        latitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitude", ObjectSerializer.serialize(new ArrayList<String>())));
        longitude = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitude", ObjectSerializer.serialize(new ArrayList<String>())));



        for(int i = 0 ; i< latitude.size(); i++){
            locations.add(new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i))));
        }
        //locations = (ArrayList<LatLng>) ObjectSerializer.deserialize(sharedPreferences.getString("Locations", ObjectSerializer.serialize(new ArrayList<String>())));

        button = (Button)findViewById(R.id.button) ;
        listView = (ListView)findViewById(R.id.list);


        if(getIntent()!=null) {
            Intent maps = getIntent();
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }

}
