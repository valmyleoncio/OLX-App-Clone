package com.example.olxapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olxapp.R;
import com.example.olxapp.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter< AdapterAnuncios.MyViewHolder > {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent , false );
        return new MyViewHolder( item );
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get( position );

        holder.titulo.setText( anuncio.getTitulo() );
        holder.preco.setText( anuncio.getValor() );

        // Pega primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get( 0 );

        Picasso.get().load(urlCapa).into( holder.foto );


    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView preco;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTitulo);
            preco  = itemView.findViewById(R.id.textPreco);
            foto   = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}
