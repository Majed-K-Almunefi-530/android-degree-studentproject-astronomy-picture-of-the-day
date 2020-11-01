package com.barmej.apod;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.LogManager;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    String body ="https://api.nasa.gov/planetary/apod?api_key=4vhGSRP9HvCSQWSzHLCz3OSr74cj1eOoTr3a1g3C&date=";
    public String date = "2020-9-12";
    private static String URL ="https://api.nasa.gov/planetary/apod?api_key=4vhGSRP9HvCSQWSzHLCz3OSr74cj1eOoTr3a1g3C&date=2020-9-12" ;
    private static String mediatype;
    private static String videourl;
    public TextView title;
    public TextView decription;
    public ImageView imageview;
    public ImageView image;
    public  ProgressBar pro;
    public  WebView myWebView;
    int num = 0;
    int yOn = 0;
    String shareurl;

    String sharePics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title1);
        decription = findViewById(R.id.desc123);

        imageview = findViewById(R.id.img_picture_view);
        image = findViewById(R.id.image1);
        pro = findViewById(R.id.progressBar);
        //final WebView web = findViewById(R.id.wv_video_player);

        myWebView = findViewById(R.id.wv_video_player);
        image.setVisibility(View.INVISIBLE);

        if(num == 0){
            objectrequest(imageview,pro,title,decription,myWebView);
        }

    }

    public void objectrequest(final ImageView imageview, final ProgressBar pro, final TextView title, final TextView decription, final WebView myWebView){
        RequestQueue queue = Volley.newRequestQueue(this);
        pro.setVisibility(View.VISIBLE);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            title.setText(response.getString("title").toString());
                            decription.setText(response.getString("explanation").toString());
                            mediatype = response.getString("media_type").toString();
                            // Glide.with(MainActivity.this).load(response.getString("url").toString()).into(imageview);
                            if(mediatype.equals("image")){


                                Glide.with(MainActivity.this).load(response.getString("url").toString()).into(imageview);
                                sharePics =(response.getString("explanation").toString());
                                pro.setVisibility(View.INVISIBLE);
                                myWebView.setVisibility(View.INVISIBLE);
                                imageview.setVisibility(View.VISIBLE);
                                yOn = 0;
                                shareurl =(response.getString("url").toString());

                            }else if( mediatype.equals("video")){
                                /*myWebView.getSettings().setJavaScriptEnabled(true);
                                myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                                myWebView.getSettings().setLoadWithOverviewMode(true);
                                myWebView.getSettings().setUseWideViewPort(true);*/

                                pro.setVisibility(View.INVISIBLE);
                                imageview.setVisibility(View.INVISIBLE);
                                myWebView.setVisibility(View.VISIBLE);
                                videourl = response.getString("url").toString();
                                //myWebView.loadUrl(videourl);
                                yOn = 1;

                                shareurl =(response.getString("url").toString());



                                String dataUrl =
                                        "<html>" +
                                                "<body>" +
                                                "<iframe width=\"100%\" height=\"100%\" src=\""+videourl+"\" frameborder=\"0\" allowfullscreen/>" +
                                                "</body>" +
                                                "</html>";

                                WebView myWebView = findViewById(R.id.wv_video_player);

                                WebSettings webSettings = myWebView.getSettings();

                                webSettings.setJavaScriptEnabled(true);
                                myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                                myWebView.getSettings().setLoadWithOverviewMode(true);
                                myWebView.getSettings().setUseWideViewPort(true);
                                myWebView.loadData(dataUrl, "text/html", "utf-8");
                            }else{
                                pro.setVisibility(View.VISIBLE);
                                myWebView.setVisibility(View.INVISIBLE);
                                imageview.setVisibility(View.INVISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pro.setVisibility(View.INVISIBLE);
                        Context context = getApplicationContext();
                        CharSequence text = "Error no pics available change date";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
        );
        queue.add(objectRequest);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pick_day){
            showdatepickerdialog();
            return true;
        }else if(item.getItemId() == R.id.action_about){
            about();
            return true;
        }else if(item.getItemId() == R.id.action_share){
            shareImageWithOther();
            return true;
        }else if (item.getItemId() == R.id.action_download_hd){
            DownloadImageFromPath(URL);
            return true;
        }else{
            return true;
        }
    }


    private void showdatepickerdialog(){
        DatePickerDialog datePickerDialog =new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        num++;
        month = month + 1;
        date = year +"-"+month+"-"+dayOfMonth;
        URL = body+date;
        objectrequest(imageview,pro,title,decription,myWebView);


    }

    public void about(){
        Intent i = new Intent(MainActivity.this,AboutActivity.class);
        startActivity(i);
    }








    public void shareImageWithOther() {
        // كود مشاركة الصورة هنا

        String mTitle = title.getText().toString();
        String mDesc = decription.getText().toString();
        if ( yOn == 0){

        }

        String mOverAll = mTitle + "\n" + mDesc + "\n" + shareurl;
        Intent ShareIntent = new Intent();
        ShareIntent.setAction(Intent.ACTION_SEND);
        ShareIntent.putExtra(Intent.EXTRA_TEXT, mOverAll);

        ShareIntent.setType("text/plain");
        startActivity(ShareIntent);

    }

    public void DownloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        ImageView iv = (ImageView) findViewById(R.id.img_picture_view);
        int responseCode = -1;
        try {

            java.net.URL url;//"http://192.xx.xx.xx/mypath/img1.jpg
            url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                iv.setImageBitmap(bmp);
            }

            Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {

            Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
        }
    }
}
