package edu.northeastern.group26.littlemood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoodActivity extends AppCompatActivity {
    private TextView img1;
    private TextView img2;
    private TextView img3;
    private TextView img4;
    private TextView img5;
    private TextView img6;
    private TextView img7;
    private TextView img8;
    private ImageView close;
    private LinearLayout wait,all;


    public static ArrayList<Emoji> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);

        all = findViewById(R.id.all);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);
        img7 = findViewById(R.id.img7);
        img8 = findViewById(R.id.img8);
        close = findViewById(R.id.close);
        wait = findViewById(R.id.wait);
        wait.setVisibility(View.VISIBLE);
        begin();

        img1.setOnClickListener(this::OnClick1);
        img2.setOnClickListener(this::OnClick1);
        img3.setOnClickListener(this::OnClick1);
        img4.setOnClickListener(this::OnClick1);
        img5.setOnClickListener(this::OnClick1);
        img6.setOnClickListener(this::OnClick1);
        img7.setOnClickListener(this::OnClick1);
        img8.setOnClickListener(this::OnClick1);
    }

    private void OnClick1(View view) {
        Intent intent = new Intent(MoodActivity.this, JournalActivity.class);
        if(view.getId()==R.id.img1){
            intent.putExtra("name", img1.getText().toString());
        }else if(view.getId()==R.id.img2){
            intent.putExtra("name", img2.getText().toString());
        }else if(view.getId()==R.id.img3){
            intent.putExtra("name", img3.getText().toString());
        }else if(view.getId()==R.id.img4){
            intent.putExtra("name", img4.getText().toString());
        }else if(view.getId()==R.id.img5){
            intent.putExtra("name", img5.getText().toString());
        }else if(view.getId()==R.id.img6){
            intent.putExtra("name", img6.getText().toString());
        }else if(view.getId()==R.id.img7){
            intent.putExtra("name", img7.getText().toString());
        }else if(view.getId()==R.id.img8){
            intent.putExtra("name", img8.getText().toString());
        }
        // Retrieve the data from the intent
        int year = getIntent().getIntExtra("YEAR", -1);
        int month = getIntent().getIntExtra("MONTH", -1);
        int day = getIntent().getIntExtra("DAY", -1);

        intent.putExtra("YEAR", year);
        intent.putExtra("MONTH", month);
        intent.putExtra("DAY", day);
        startActivity(intent);
    }

    Handler myhandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.what==SUCCESS){

                String s=message.getData().getString("data");
                System.out.println("data="+s);
                try {
                    JSONArray jsonArray=new JSONArray(s) ;
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Emoji item = new Emoji(jsonObject.optString("slug"), jsonObject.optString("character")
                                , jsonObject.optString("group") , jsonObject.optString("subGroup"));

                        if(item.getSubGroup().equals("face-smiling") ){
                        }
                        arrayList.add(item);
                        Log.d("JsonToEntity:", item.toString());
                    }

                    for (Emoji e:arrayList
                    ) {
                        System.out.println(e.toString());
                        if(e.getSlug().equals("e1-0-grinning-face")){
                            img1.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-6-grinning-face-with-sweat")){
                            img2.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-6-face-with-tears-of-joy")){
                            img3.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-7-frowning-face")){
                            img4.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-6-winking-face")){
                            img5.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e1-0-smiling-face-with-halo")){
                            img6.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-6-beaming-face-with-smiling-eyes")){
                            img7.setText(e.getCharacter());
                        }else if(e.getSlug().equals("e0-6-loudly-crying-face")){
                            img8.setText(e.getCharacter());
                        }
                    }
                    wait.setVisibility(View.GONE);

                    all.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
            return false;
        }
    });
    private static final int SUCCESS = 100;
    void begin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = askApi3();
                Log.d("jsonData：",jsonData);
                JSONObject jsonObject = null;
                Message message=Message.obtain();
                Bundle bundle=new Bundle();
                bundle.putString("data",jsonData);
                message.setData(bundle);
                message.what=SUCCESS;
                myhandler.sendMessage(message);
            }
        }).start();
    }

    String askApi3(){
        try{
            String url1="https://emoji-api.com/categories/smileys-emotion?access_key=6593596d7d910ecc406aa4d589a1ccf566373406";
            URL url=new URL(url1);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            if(connection.getResponseCode()==200){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder=new StringBuilder();
                String strRead = null;
                while ((strRead = bufferedReader.readLine()) != null) {
                    stringBuilder.append(strRead+"\r\n");
                }
                bufferedReader.close();
                inputStream.close();
                connection.disconnect();
                return stringBuilder.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
            return  e.toString();
        }
        return "-1";
    }
}