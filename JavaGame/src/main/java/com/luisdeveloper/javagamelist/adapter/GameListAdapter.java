package com.luisdeveloper.javagamelist.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.luisdeveloper.javagamelist.R;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.ui.ListGameDetails;
import com.luisdeveloper.javagamelist.ui.gamesDatabase;

import java.net.URI;
import java.util.List;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {

    private List<String> List_Name,List_Desc,List_url,List_categoria;
    private List<Integer> List_Favoritos;
    private List<byte[]> List_image;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private String path = Environment.getExternalStorageDirectory().toString();

    // data is passed into the constructor
    public GameListAdapter(Context context, List<String> name,List<String> desc,List<byte[]> image,List<String> url,List<String> categoria,List<Integer> favoritos) {
        this.mInflater = LayoutInflater.from(context);
        this.List_Name = name;
        this.List_Desc = desc;
        this.List_image = image;
        this.List_url = url;
        this.List_categoria = categoria;
        this.List_Favoritos = favoritos;
        this.mContext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_recycler_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String name = List_Name.get(position);
        final String desc = List_Desc.get(position);
        final String url = List_url.get(position);
        final String categoria = List_categoria.get(position);
        final int favorite = List_Favoritos.get(position);

        holder.idTitles.setText(name);
        holder.idDesc.setText(desc);


        //holder.btnFavorite.setBackground(R.drawable.ic_star_black_24dp);

        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (favorite == 1) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.btnFavorite.setBackgroundDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_black_24dp));
            } else {
                holder.btnFavorite.setBackground(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_black_24dp));
            }
        }
        else
        {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.btnFavorite.setBackgroundDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_border_black_24dp));
            } else {
                holder.btnFavorite.setBackground(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_border_black_24dp));
            }
        }

        Bitmap bitmap = null;

        //if (List_image.get(position) != null) {
        //    bitmap = BitmapFactory.decodeByteArray(List_image.get(position), 0, List_image.get(position).length);
        //    //holder.idPic.setImageResource(R.drawable.ic_game_demo);
        //}

        bitmap = BitmapFactory.decodeFile( path + "/" + "JavaGameList/"+List_Name.get(position)+".jpg");

        if (bitmap == null)
            holder.idPic.setImageResource(R.drawable.ic_app_icon);
        else {
            holder.idPic.setImageBitmap(bitmap);
            //bitmap = null;
        }

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(),ListGameDetails.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("name",name);
                i.putExtra("desc",desc);
                i.putExtra("url",url);
                i.putExtra("categoria",categoria);
                mContext.getApplicationContext().startActivity(i);
            }
        });

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+".jar");
                request.allowScanningByMediaScanner();
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Descargando Juego...");
                request.setVisibleInDownloadsUi(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager downloadManager = (DownloadManager) mContext.getApplicationContext().getSystemService(mContext.getApplicationContext().DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_database database = room_database.getAppDatabase(mContext.getApplicationContext());
            if (favorite == 1) {
                database.GamesDao().QuitarFavoritosGameByID(position + 1);
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnFavorite.setBackgroundDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_border_black_24dp));
                } else {
                    holder.btnFavorite.setBackground(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_border_black_24dp));
                }
            }
            else
            {
                database.GamesDao().updateGameByID(position + 1);

                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnFavorite.setBackgroundDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_black_24dp));
                } else {
                    holder.btnFavorite.setBackground(ContextCompat.getDrawable(mContext.getApplicationContext(), R.drawable.ic_star_black_24dp));
                }
            }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return List_Name.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView idTitles,idDesc;
        ImageView idPic;
        ImageButton btnDetails,btnDownload,btnFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            idTitles = itemView.findViewById(R.id.idtitles);
            idDesc = itemView.findViewById(R.id.idDesc);
            idPic = itemView.findViewById(R.id.idPic);
            btnDetails = itemView.findViewById(R.id.idMoreDetails);
            btnDownload = itemView.findViewById(R.id.idDownload);
            btnFavorite = itemView.findViewById(R.id.idfavorite);
            itemView.setOnClickListener(this);

          /*  btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext.getApplicationContext(),ListGameDetails.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //v.getContext().startActivity(i);
                    mContext.getApplicationContext().startActivity(i);
                }
            }); */
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
            {
                mClickListener.onItemClick(view, getAdapterPosition());
            }

        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return List_Name.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}