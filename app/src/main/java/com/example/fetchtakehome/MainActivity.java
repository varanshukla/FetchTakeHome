package com.example.fetchtakehome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {

    private static String DATA_URL = "https://fetch-hiring.s3.amazonaws.com/hiring.json";

    List<Item> items;
    RecyclerView mRecyclerView;
    Retriever mRetriever;
    Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<>();
        mRetriever = new Retriever();
        mainHandler= new Handler(this.getMainLooper());
        mRetriever.start();
    }

    class Retriever extends Thread{
        @Override
        public void run(){
            String data = "";
            try {
                URL url = new URL(DATA_URL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(request.getInputStream()));
                String line;
                while( (line = bufferedReader.readLine()) != null){
                    data = data + line;
                }
                if(!data.isEmpty()){
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        int id = jsonobject.getInt( "id");
                        String name = jsonobject.getString("name");
                        int listId = jsonobject.getInt("listId");
                        if(!name.equals("") && !name.equals("null")) {
                            items.add(new Item(id, name, listId));
                        }
                    }
                }
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item x, Item y) {
                        return x.getName().compareTo(y.getName());
                    }
                });
                Collections.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(Item x, Item y) {
                        return Integer.compare(x.getListId(), y.getListId());
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    initList();
                }
            });
        }
    }

    private void initList() {
        mRecyclerView = findViewById(R.id.itemsView);
        MyAdapter mAdapter = new MyAdapter(this, items);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

}