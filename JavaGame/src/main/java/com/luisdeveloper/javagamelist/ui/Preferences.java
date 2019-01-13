package com.luisdeveloper.javagamelist.ui;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.widget.Toast;

import com.luisdeveloper.javagamelist.R;
import com.luisdeveloper.javagamelist.rest_services.game_recordcount;
import com.luisdeveloper.javagamelist.room.database.room_database;

public class Preferences  extends  AppCompatPreferenceActivity implements  SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String borrardb = prefs.getString("listOptionsPref", "0");
        boolean notif = prefs.getBoolean("checkboxPref",false);


        room_database database = room_database.getAppDatabase(this);;

        if (borrardb.equals("1")) {
            database.GamesDao().deleteAllGame();
            Toast.makeText(getApplicationContext(),"Base de datos borrada!",Toast.LENGTH_SHORT).show();
            game_recordcount r = new game_recordcount();
            r.dbdelete = 1;

            SharedPreferences cargaInicial = getSharedPreferences("CargaInicial", Context.MODE_PRIVATE);
            SharedPreferences.Editor cargaInicialEditor = cargaInicial.edit();
            cargaInicialEditor.putString("cargado","0");
            cargaInicialEditor.commit();
            //System.exit(0);
            //Application a = new Application();
            //a.onTerminate();
            /*AlertDialog.Builder builder = new  AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Estado de Base de datos");
            builder.setMessage("Base de datos borrada!");
            builder.setNeutralButton("OK",  new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    //System.exit(0);
                }
            });

            builder.create().show();   */
        }

        if (notif)
            Toast.makeText(getApplicationContext(),"Notificacion para uso futuro!",Toast.LENGTH_SHORT).show();


    }

}
