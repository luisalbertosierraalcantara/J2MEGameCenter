package com.luisdeveloper.javagamelist.services;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.luisdeveloper.javagamelist.R;
import com.luisdeveloper.javagamelist.model.games_object;
import com.luisdeveloper.javagamelist.rest_services.game_json;
import com.luisdeveloper.javagamelist.rest_services.httpResquest;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.room.entity.games_table;
import com.luisdeveloper.javagamelist.ui.ImageCache;
import com.luisdeveloper.javagamelist.ui.ListGame;
import com.luisdeveloper.javagamelist.ui.ListGameDetails;
import com.luisdeveloper.javagamelist.ui.MainActivity;
import com.luisdeveloper.javagamelist.ui.UrlServices;
import com.luisdeveloper.javagamelist.ui.gamesDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class updateGamesListServices  extends JobService  {
    private static final String TAG = "updateGamesListServices";
    private Button button;
    private List<games_object> GameList;
    private List<Jobtask> tasks;
    private ImageCache imageCache;
    private final int RESULT_LOAD_IMAGE = 1;

    Jobtask backgroundTask;
    @Override
    public boolean onStartJob(final JobParameters job) {

        if (isOnline()) {
            imageCache = new ImageCache(this, -1);

            tasks = new ArrayList<>();

            backgroundTask = new Jobtask();
            UrlServices url = new UrlServices();
            backgroundTask.execute(url.RESTSERVICE);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    public class Jobtask extends AsyncTask<String, String, List<games_object>> {
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                Toast.makeText(getApplicationContext(),"Descargando datos...",Toast.LENGTH_LONG).show();
            }
            tasks.add(this);
        }

        @Override
        protected List<games_object> doInBackground(String... params) {
            httpResquest http = new httpResquest();
            String content = http.GET(params[0]); ///consigue el json

            GameList = game_json.parseJSON(content);


            for (int i= 0; i <= GameList.size() -1; i++)
            {
                saveImageToCahe(GameList.get(i).getPicture(),GameList.get(i).getName());
            }

            return GameList;
        }

        protected void onPostExecute(List<games_object> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                Toast.makeText(getApplicationContext(),"Datos descargados exitosamente...",Toast.LENGTH_LONG).show();
            }

            if (result == null) {
                Toast.makeText(getApplicationContext(), "Servicio no Disponible Intente mas Tarde!", Toast.LENGTH_LONG).show();
                return;
            }

            GameList = result;
            updateDatabase(GameList);

        }
    }

    private void saveImageToCahe(String url, String imageName) {
        Bitmap bmImg = imageCache.getBitmap(url);
        File filename;
        try {
            String path1 = Environment.getExternalStorageDirectory().toString();
            Log.i("in save()", "after mkdir");
            File file = new File(path1 + "/" + "JavaGameList");
            if (!file.exists())
                file.mkdirs();
            filename = new File(file.getAbsolutePath() + "/" + imageName
                    + ".jpg");
            Log.i("in save()", "after file");
            FileOutputStream out = new FileOutputStream(filename);
            Log.i("in save()", "after outputstream");
            bmImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("in save()", "after outputstream closed");
            //File parent = filename.getParentFile();
            ContentValues image = getImageContent(filename,imageName);
            Uri result = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
            Toast.makeText(getApplicationContext(),
                    "File is Saved in  " + filename, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ContentValues getImageContent(File parent,String imageName) {
        ContentValues image = new ContentValues();
        image.put(MediaStore.Images.Media.TITLE, "JavaGameList");
        image.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
        image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString()
                .toLowerCase().hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
                .toLowerCase());
        image.put(MediaStore.Images.Media.SIZE, parent.length());
        image.put(MediaStore.Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }


    //llenar lista de canales
    protected void updateDatabase(List<games_object> data) {

        room_database database = room_database.getAppDatabase(this);;


        ArrayList<String> GameName = new ArrayList<>();
        String msg = "";


        //GameName.add(database.GamesDao().getAllGames()[0].name);

        for (games_table games: database.GamesDao().getAllGames()) {  //busca datos en la base de datos
            GameName.add(games.name); //llena lista
        }

        int old = GameName.size();
        int New = data.size();
        int resp;
        resp = old - New;


        if (GameName.size() != 0)  //actualiza
        {
            gamesDatabase.daoAccess(database).UpdateGamestoDb(data);
            msg = "actualizaron " + Integer.toString(resp) + " registros ";

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("listOptionsPref","0");
            editor.putBoolean("checkboxPref",false);
            editor.commit();
            //prefs.setString("listOptionsPref", "0");
        }
        else if (GameName.size() == 0) //inserta
        {
            gamesDatabase.daoAccess(database).InsertGamestoDb(data);
            msg = "insertaron " + Integer.toString(resp) + " registros ";
        }

        Toast.makeText(getApplicationContext(),"Se "+msg+" correctamente!!",Toast.LENGTH_SHORT).show();

        //Intent i = new Intent(getApplicationContext(),MainActivity.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //getApplicationContext().startActivity(i);
        //System.exit(0);
    }
}
