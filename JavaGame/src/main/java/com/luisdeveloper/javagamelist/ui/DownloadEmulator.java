package com.luisdeveloper.javagamelist.ui;


import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.luisdeveloper.javagamelist.R;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadEmulator extends Fragment {

    Button btndownload_apk;
    public DownloadEmulator() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_emulator, container, false);


        Button btndownload_apk = view.findViewById(R.id.btndownload_apk);

        btndownload_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Uri uri = Uri.parse("https://github.com/luisalbertosierraalcantara/MEmulator/blob/master/APK/MEmulator-1.0-beta.apk?raw=true");
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MEmulator-1.0-beta.apk");
                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Descargando Memulator Beta v1.0...");
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    DownloadManager downloadManager = (DownloadManager) v.getContext().getApplicationContext().getSystemService(v.getContext().getApplicationContext().DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);

                }
        });

        return view;


    }



}
