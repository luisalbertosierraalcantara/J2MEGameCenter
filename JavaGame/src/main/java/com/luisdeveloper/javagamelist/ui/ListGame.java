package com.luisdeveloper.javagamelist.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luisdeveloper.javagamelist.R;
import com.luisdeveloper.javagamelist.adapter.GameListAdapter;
import com.luisdeveloper.javagamelist.rest_services.game_recordcount;
import com.luisdeveloper.javagamelist.room.database.room_database;
import com.luisdeveloper.javagamelist.room.entity.games_table;

import java.util.ArrayList;

//**************************************************************************************
//                              CREATED BY LUIS A. SIERRA
//**************************************************************************************
public class ListGame extends Fragment implements GameListAdapter.ItemClickListener {

    GameListAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public ListGame() {
        // Required empty public constructor
    }

    public static ListGame newInstance(String param1, String param2) {
        ListGame fragment = new ListGame();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_game, container, false);


        // data to populate the RecyclerView with
        ArrayList<String> GameName = new ArrayList<>();
        ArrayList<String> GameDesc = new ArrayList<>();
        ArrayList<byte[]> GameImage = new ArrayList<>();
        ArrayList<String> GameUrl = new ArrayList<>();
        ArrayList<String> GameCategoria = new ArrayList<>();
        ArrayList<Integer> GameFavoritos = new ArrayList<>();

        room_database database = room_database.getAppDatabase(getActivity().getApplicationContext());

        game_recordcount menu = new game_recordcount();


        if (menu.menu_selected.equals("home")) {

            for (games_table games: database.GamesDao().getAllGames()) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }

        }
        else if (menu.menu_selected.equals("accion"))
        {
            for (games_table games: database.GamesDao().selectCategory("Accion")) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }
        }
        else if (menu.menu_selected.equals("estrategia"))
        {
            for (games_table games: database.GamesDao().selectCategory("Estrategia")) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }

        }
        else if (menu.menu_selected.equals("aventuras"))
        {
            for (games_table games: database.GamesDao().selectCategory("Aventuras")) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }
        }
        else if (menu.menu_selected.equals("combate"))
        {
            for (games_table games: database.GamesDao().selectCategory("Combate")) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }

        }
        else if (menu.menu_selected.equals("carreras"))
        {
            for (games_table games: database.GamesDao().selectCategory("Carrera")) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }

        }
        else if (menu.menu_selected.equals("favoritos"))
        {
            for (games_table games: database.GamesDao().selectFavoritos(1)) {
                GameName.add(games.name);
                GameDesc.add(games.desc);
                GameImage.add(games.image);
                GameUrl.add(games.url);
                GameCategoria.add(games.categoria);
                GameFavoritos.add(games.favoritos);
            }

        }
        
        // set up the RecyclerView from room database
        RecyclerView recyclerView = view.findViewById(R.id.ListaPrincipal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new GameListAdapter(getActivity().getApplicationContext(), GameName, GameDesc,GameImage,GameUrl,GameCategoria,GameFavoritos);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(getActivity().getApplicationContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }
    
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
