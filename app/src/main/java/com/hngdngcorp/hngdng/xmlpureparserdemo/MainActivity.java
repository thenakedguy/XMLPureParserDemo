package com.hngdngcorp.hngdng.xmlpureparserdemo;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnLoad;
    private RecyclerView recyclerView;

    private String url = "http://docbao.com.vn/rss/export/home.rss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 LoadingTask loadingTask = new LoadingTask();
                 loadingTask.execute(url);

            }
        });
    }

    class LoadingTask extends AsyncTask<String,Long, List<News>>{

        @Override
        protected List<News> doInBackground(String... strings) {
            List<News> newsList = new ArrayList<>();

            // khong cap nhat trong ham nay
            try {
                //khoi tai doi tuong URL de req
                URL url = new URL(strings[0]);
                //mở kết nốt URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //lay du lieu va gan = doi tuong inputStream
                InputStream inputStream = httpURLConnection.getInputStream();

                XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);

                XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

                xmlPullParser.setInput(inputStream,"utf-8");

                int evenType = xmlPullParser.getEventType();
                News news = null;
                String s = "";
                while (evenType!= XmlPullParser.END_DOCUMENT){

                    String name = xmlPullParser.getName();
                    switch (evenType){
                        case XmlPullParser.START_TAG:
                            if (name.equals("items")){
                                news = new News();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            s = xmlPullParser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (news != null && name.equalsIgnoreCase("title")){
                                news.title = s;
                            }else
                            if (news != null && name.equalsIgnoreCase("description")){
                                news.description = s;
                            }else
                            if (news != null && name.equalsIgnoreCase("pubDate")){
                                news.pubDate = s;
                            }else
                            if (news != null && name.equalsIgnoreCase("link")){
                                news.link = s;
                            }else if(name.equalsIgnoreCase("item")){
                                newsList.add(news);
                            }
                            break;
                    }
                    evenType = xmlPullParser.next();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("MalformedURLException",e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException",e.getMessage());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Log.e("XmlPullParserException",e.getMessage());
            } catch (RuntimeException e){
                e.printStackTrace();
                Log.e("RuntimeException",e.getMessage());
            }


            return newsList;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            // xu ly du lieu hien thi tren man hinh tai day
            Toast.makeText(MainActivity.this,"Size :" + news.size(),Toast.LENGTH_LONG).show();
        }

    }

}
