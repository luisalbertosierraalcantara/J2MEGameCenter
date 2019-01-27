package com.luisdeveloper.javagamelist.ui;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.provider.MediaStore.Images;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.luisdeveloper.javagamelist.R;
import com.luisdeveloper.javagamelist.rest_services.game_json;
import com.luisdeveloper.javagamelist.model.games_object;
import com.luisdeveloper.javagamelist.rest_services.game_recordcount;
import com.luisdeveloper.javagamelist.rest_services.httpResquest;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.room.entity.games_table;
import com.luisdeveloper.javagamelist.services.updateGamesListServices;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListGame.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private Button button;

    private List<Restask> tasks;
    private List<games_object> GameList;

    private ImageCache imageCache;
    private final int RESULT_LOAD_IMAGE = 1;

    private int winStartSeconds = 30;
    private int winEndSeconds = 60;
    private int initialBackoffSeconds = 30;
    private int maximumBackoffSeconds = 3600;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Permitions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);

        imageCache = new ImageCache(this, -1);

        tasks = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean JobScheduled = prefs.getBoolean("JobScheduled",true);

        if (isOnline()) {

                if (JobScheduled) {
                    //Intent i = new Intent(this, updateGamesListServices.class);
                    //startService(i);

                    String Job_Tag = "my_job_tag";
                    //final int horas = (int) TimeUnit.HOURS.toSeconds(6); // Every 1 hour periodicity expressed as seconds
                    //final int minutos = (int) TimeUnit.MINUTES.toSeconds(15); // a small(ish) window of time when triggering is OK

                    FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
                    Job job = jobDispatcher.newJobBuilder().
                            setService(updateGamesListServices.class).
                            setLifetime(Lifetime.FOREVER).
                            setRecurring(true).
                            setTag(Job_Tag).
                            setTrigger(Trigger.executionWindow(winStartSeconds,winEndSeconds)).  //3600x 4 = 14400 (4)horas en total
                            //setTrigger(Trigger.executionWindow(10, 15)).
                                    setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                                    setReplaceCurrent(false).
                                    setConstraints(Constraint.ON_ANY_NETWORK)
                            .build();
                    jobDispatcher.mustSchedule(job);
                    Toast.makeText(this, "Job Scheduled iniciado...", Toast.LENGTH_SHORT).show();
                }

            //Shared preferences para carga inicial


            SharedPreferences cargaInicial = getSharedPreferences("CargaInicial",Context.MODE_PRIVATE);
            String defaultValue = "0";
            String cargadoAntes = cargaInicial.getString("cargado",defaultValue);

            if (cargadoAntes.equals("0"))
            {
                UrlServices url = new UrlServices();
                HttpRequestData(url.RESTSERVICE);
            }

             BeginTransationFragment();

        } else {

            BuildMessagesNoInternet();
            BeginTransationFragment();
        }


    }


    public void BeginTransationFragment()
    {
        //Init fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ListGame listGame = new ListGame();
        transaction.replace(R.id.contenedor,listGame);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_memulator) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            DownloadEmulator emulator = new DownloadEmulator();
            transaction.replace(R.id.contenedor,emulator);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        game_recordcount menu = new game_recordcount();

        ActionBar actionBar = getSupportActionBar();

        if (id == R.id.nav_emulator) {
            actionBar.setTitle("Emulator");
            //Init fragment
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            DownloadEmulator emulator = new DownloadEmulator();
            transaction.replace(R.id.contenedor,emulator);
            transaction.commit();

        } else if (id == R.id.nav_home) {
            menu.menu_selected = "home";
            actionBar.setTitle("Home");
            BeginTransationFragment();
        } else if (id == R.id.nav_adventure) {
            actionBar.setTitle("Aventuras");
            menu.menu_selected = "aventuras";
            BeginTransationFragment();
        } else if (id == R.id.nav_puzzle) {
            actionBar.setTitle("Estrategia");
            menu.menu_selected = "estrategia";
            BeginTransationFragment();
        } else if (id == R.id.nav_action) {
            actionBar.setTitle("Accion");
            menu.menu_selected = "accion";
            BeginTransationFragment();
        } else if (id == R.id.nav_combat) {
            actionBar.setTitle("Combate");
            menu.menu_selected = "combate";
            BeginTransationFragment();
        } else if (id == R.id.nav_race) {
            actionBar.setTitle("Carreras");
            menu.menu_selected = "carreras";
            BeginTransationFragment();
        } else if (id == R.id.nav_favorite) {
            actionBar.setTitle("Favoritos");
            menu.menu_selected = "favoritos";
            BeginTransationFragment();
        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(getApplicationContext(),Preferences.class);
            startActivity(settings);
        } else if (id == R.id.nav_about) {
            Intent about = new Intent(getApplicationContext(),About.class);
            startActivity(about);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        game_recordcount r = new game_recordcount();

        if (r.dbdelete == 1) {
            //this.recreate();
            //Runtime.getRuntime().exit(0);
            System.exit(0);
            r.dbdelete = 0;
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
                ContentValues image = getImageContent(filename, imageName);
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
        image.put(Images.Media.TITLE, "JavaGameList");
        image.put(Images.Media.DISPLAY_NAME, imageName);
        image.put(Images.Media.DESCRIPTION, "App Image");
        image.put(Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(Images.Media.MIME_TYPE, "image/jpg");
        image.put(Images.Media.ORIENTATION, 0);
        image.put(Images.ImageColumns.BUCKET_ID, parent.toString()
                .toLowerCase().hashCode());
        image.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
                .toLowerCase());
        image.put(Images.Media.SIZE, parent.length());
        image.put(Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }

    public void BuildMessagesNoInternet()
    {

        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Ups no tienes Internet!");
        builder.setMessage("Para mantenerte al dia es necesario una conexion WIFI/3G/4G LTE.");
        builder.setNeutralButton("OK",  new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                //finish();
            }
        });

        builder.create().show();

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

    //cargar request de json
    private void HttpRequestData(String uri) {

        if (GameList != null) {
            if (GameList.size() > 0)
                GameList.clear();
        }

        Restask task = new Restask(this);
        task.execute(uri);
    }

    private class Restask extends AsyncTask<String, String, List<games_object>> {
        private ProgressDialog dialog;

        public Restask(MainActivity activity)
        {
            dialog = new ProgressDialog(activity);
        }
        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                dialog.setMessage("Descargando datos...");
                dialog.show();
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
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Servicio no Disponible Intente mas Tarde!", Toast.LENGTH_LONG).show();
                return;
            }

            GameList = result;
            prosessDb prosess = new prosessDb(getApplicationContext());
            prosess.updateDatabase(GameList);
            BeginTransationFragment();
        }
    }



}
