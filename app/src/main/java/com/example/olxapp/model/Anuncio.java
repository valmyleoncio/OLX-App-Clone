package com.example.olxapp.model;

import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Anuncio implements Serializable {

    private String idAnuncio;
    private String estado;
    private String cateforia;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;
    private List<String> fotos;

    public Anuncio() {

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Meus_anuncios");
        this.setIdAnuncio( anuncioRef.push().getKey() );

    }


    public void salvar(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Meus_anuncios");

        anuncioRef.child(idUsuario)
                .child(getIdAnuncio())
                .setValue(this);

        salvarAnuncioPublico();

    }

    public void salvarAnuncioPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Anuncios");

        anuncioRef.child( getEstado() )
                .child( getCateforia() )
                .child(getIdAnuncio())
                .setValue(this);

    }


    public void remover(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Meus_anuncios")
                .child( idUsuario )
                .child( getIdAnuncio() );

        anuncioRef.removeValue();
        removerAnuncioPublico();

    }

    public void removerAnuncioPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Anuncios")
                .child(getEstado())
                .child(getCateforia())
                .child(getIdAnuncio());

        anuncioRef.removeValue();


    }


    public String getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCateforia() {
        return cateforia;
    }

    public void setCateforia(String cateforia) {
        this.cateforia = cateforia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
