package com.luisdeveloper.javagamelist.ui;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luisdeveloper.javagamelist.R;


//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
public class ListGameDetails extends AppCompatActivity {
    private String name,desc,url,categoria;
    private Button btnDownload;
    private TextView TvTitle,TvDesc,tvCategory;
    private String path = Environment.getExternalStorageDirectory().toString();
    private ImageView PictureDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_game_details);


        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        desc = extras.getString("desc");
        url = extras.getString("url");
        categoria = extras.getString("categoria");

        TvTitle = (TextView)findViewById(R.id.titleDetails);
        TvDesc = (TextView)findViewById(R.id.tvDescDetails);
        btnDownload = (Button) findViewById(R.id.btndownload_apk);
        PictureDetails = (ImageView) findViewById(R.id.PictureDetails);
        tvCategory= (TextView)findViewById(R.id.tvCategoryDetails);

        TvTitle.setText(name);
        TvDesc.setText(desc);
        tvCategory.setText(categoria);
        Bitmap bitmap = BitmapFactory.decodeFile( path + "/" + "JavaGameList/"+name+".jpg");

        if (bitmap == null)
            PictureDetails.setImageResource(R.drawable.ic_app_icon);
        else {
            PictureDetails.setImageBitmap(bitmap);
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Uri uri = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Descargando Juego...");
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);
                }
        });

    }
}
