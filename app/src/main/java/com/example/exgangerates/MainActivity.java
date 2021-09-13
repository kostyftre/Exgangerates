package com.example.exgangerates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private Document doc;
    private Thread secondThread;
    private Runnable runnable;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;
    private Button buttonReload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


        buttonReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                init();




            }
        });
    }

   private void reloadAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Данные успешно обновлены!");
        builder.setMessage("Ура!");
        AlertDialog dialog = builder.create();

        dialog.show();
   }
    private void reloadAlertF(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Проблема с подключением к сети.");
        builder.setMessage("О, нет!");
        AlertDialog dialog = builder.create();

        dialog.show();
    }




    private void init(){


        int a=1;
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this,R.layout.list_item_1,arrayList ,getLayoutInflater());
        listView.setAdapter(adapter);
        runnable = new Runnable() {
            @Override
            public void run() {
               getWeb();
            }


        };

        buttonReload = findViewById(R.id.reload);
        secondThread = new Thread(runnable);
        secondThread.start();

        if (isNetworkConnected()){
            reloadAlert();
        }
        else reloadAlertF();



    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private void getWeb(){
        try {
            //биба
            doc = Jsoup.connect("https://minfin.com.ua/currency/").get();
            Elements tables = doc.getElementsByTag("tbody");
            Element ourTable = tables.get(0);
            Elements elementsFromTable = ourTable.children();
            Element dollar = elementsFromTable.get(0);
            Elements dollarElements = dollar.children();
            Log.v("MyLog","Всё кул, данные с аайта пришли");
            //Log.v("MyLog","poo : " + ourTable.children().get(0).text());


            for (int i = 0; i<ourTable.childrenSize(); i++){

                ListItemClass items = new ListItemClass();

                items.setData_1(ourTable.children().get(i).child(0).text());
                //Log.v("MyLog",Integer.toString(ourTable.children().get(1).child(0).text().length()));

                items.setData_2(ourTable.children().get(i).child(1).text().substring(0,6) + " / " + ourTable.children().get(i).child(1).text().substring(ourTable.children().get(i).child(1).text().length()-6,ourTable.children().get(i).child(1).text().length()));
               // items.setData_3(ourTable.children().get(i).child(2).text().substring(0,6));
                items.setData_3( parse3(ourTable.children().get(i).child(2).text()));
                items.setData_4(ourTable.children().get(i).child(3).text());
                arrayList.add(items);

            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });


        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    String parse3(String tt){

        String result="";

        for (int i=0;true;i++){
            if (tt.charAt(i)!='.'){
                result=result+tt.charAt(i);
            }
            else {
                for (int j=i;j<i+4;j++){
                    result=result+tt.charAt(j);

                }
                return result;
            }
        }


    }

}