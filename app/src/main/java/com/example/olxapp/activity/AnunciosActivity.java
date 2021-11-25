package com.example.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.olxapp.R;
import com.example.olxapp.adapter.AdapterAnuncios;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.example.olxapp.helper.RecyclerItemClickListener;
import com.example.olxapp.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> anunciosLista = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        // Inicializar componentes
        inicializarComponentes();

        // Configurações iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Anuncios");

        // Configurar RecyclerView
        adapterAnuncios = new AdapterAnuncios(anunciosLista, this);
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

        // Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerAnunciosPublicos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncioSelecionado = anunciosLista.get( position );

                        Intent i = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                        i.putExtra( "anuncioSelecionado", anuncioSelecionado );
                        startActivity( i );
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if ( autenticacao.getCurrentUser() == null ){

            menu.setGroupVisible(R.id.grup_deslogado, true);

        }else {

            menu.setGroupVisible(R.id.grup_logado, true);

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                finish();
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnuncioPublicos);
        buttonRegiao    = findViewById(R.id.buttonRegiao);
        buttonCategoria = findViewById(R.id.buttonCategoria);


    }

    private void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage(" Carregando... ")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosLista.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for ( DataSnapshot estados : snapshot.getChildren() ){

                    for ( DataSnapshot categoria : estados.getChildren() ){

                        for ( DataSnapshot anuncios : categoria.getChildren()){

                            Anuncio anuncio = anuncios.getValue( Anuncio.class );
                            anunciosLista.add( anuncio );

                        }
                    }

                }

                Collections.reverse( anunciosLista );
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void filtrarPorEstado( View view ){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        // Configurar Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);

        String[] estados = getResources().getStringArray(R.array.estados);

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adapterEstados.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerEstado.setAdapter( adapterEstados );

        dialogEstado.setView( viewSpinner );


        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;

            }
        });

        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    private void recuperarAnunciosPorEstado() {

        // Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Anuncios")
                .child( filtroEstado );

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anunciosLista.clear();

                for ( DataSnapshot categoria : snapshot.getChildren() ){

                    for ( DataSnapshot anuncios : categoria.getChildren()){

                        Anuncio anuncio = anuncios.getValue( Anuncio.class );
                        anunciosLista.add( anuncio );

                    }
                }

                Collections.reverse( anunciosLista  );
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void filtrarPorCategoria( View view ){

        if ( filtrandoPorEstado == true){



            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
            dialogCategoria.setTitle("Selecione a categoria desejada");

            // Configurar Spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);

            String[] categoria = getResources().getStringArray(R.array.categorias);

            ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    categoria
            );
            adapterCategoria.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spinnerCategoria.setAdapter( adapterCategoria );

            dialogCategoria.setView( viewSpinner );


            dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();

                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();


        }else{
            Toast.makeText(this, "Escolha primeiro uma região!", Toast.LENGTH_SHORT).show();
        }

    }

    private void recuperarAnunciosPorCategoria() {

        // Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Anuncios")
                .child( filtroEstado )
                .child( filtroCategoria );

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anunciosLista.clear();

                for ( DataSnapshot anuncios : snapshot.getChildren()){

                    Anuncio anuncio = anuncios.getValue( Anuncio.class );
                    anunciosLista.add( anuncio );

                }

                Collections.reverse( anunciosLista );
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}