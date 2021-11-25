package com.example.olxapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.olxapp.R;
import com.example.olxapp.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo, descricao, valor, estado;
    private Anuncio anuncioSelecionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        // Configurar toolbar
        getSupportActionBar().setTitle("Detalhe produto");

        // Inicializar componentes
        inicializarComponentes();

        // Recuperar anúncio para exibição
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra( "anuncioSelecionado");

        if ( anuncioSelecionado != null ){

            titulo.setText( anuncioSelecionado.getTitulo() );
            descricao.setText( anuncioSelecionado.getDescricao() );
            estado.setText( anuncioSelecionado.getEstado() );
            valor.setText( anuncioSelecionado.getValor() );

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get( position );
                    Picasso.get().load( urlString ).into( imageView );
                }
            };

            carouselView.setPageCount( anuncioSelecionado.getFotos().size() );
            carouselView.setImageListener( imageListener );

        }
    }

    private void inicializarComponentes(){

        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        valor = findViewById(R.id.textValorDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);

    }

    public void vizualizarTelefone( View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity( i );
    }
}