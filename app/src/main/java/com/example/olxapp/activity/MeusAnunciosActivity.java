package com.example.olxapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.example.olxapp.adapter.AdapterAnuncios;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.example.olxapp.helper.RecyclerItemClickListener;
import com.example.olxapp.model.Anuncio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import com.example.olxapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncio;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        // Inicializar componentes
        inicializarComponentes();

        // Configurações iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Meus_anuncios")
                .child( ConfiguracaoFirebase.getIdUsuario() );


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

            }
        });


        // Configurar RecyclerView
        adapterAnuncios = new AdapterAnuncios(anuncios, this);
        recyclerAnuncio.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncio.setHasFixedSize(true);
        recyclerAnuncio.setAdapter( adapterAnuncios );


        // Recupera anúncio para o usuário
        recuperarAnuncios();

        // Adiciona evento de clique no recyclerview
        recyclerAnuncio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnuncio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Anuncio anuncioSelecionado = anuncios.get( position );
                                anuncioSelecionado.remover();
                                adapterAnuncios.notifyDataSetChanged();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    private void inicializarComponentes(){

        recyclerAnuncio = findViewById(R.id.recyclerAnuncios);

    }

    private void recuperarAnuncios(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage(" Carregando... ")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncios.clear();
                for ( DataSnapshot ds : snapshot.getChildren() ){
                    anuncios.add( ds.getValue( Anuncio.class ) );
                }

                Collections.reverse( anuncios );
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
