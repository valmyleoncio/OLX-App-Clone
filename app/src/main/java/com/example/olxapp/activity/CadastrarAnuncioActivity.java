package com.example.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.olxapp.R;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.example.olxapp.helper.Permissoes;
import com.example.olxapp.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private ImageView image1, image2, image3;
    private Spinner campoEstado, campoCategoria;
    private MaskEditText campoTelefone;
    private AlertDialog dialog;

    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();
    private Anuncio anuncio;

    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        // Inicializar componentes
        inicializarComponentes();

        // Configurações iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Validar permissões
        Permissoes.validarPermissoes( permissoes, this, 1);

        // Config. Spinner
        carregarDadosSpinner();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado : grantResults ){

            if ( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageCadastro1:
                escolherImagem( 1 );
                break;

            case R.id.imageCadastro2:
                escolherImagem( 2);
                break;

            case R.id.imageCadastro3:
                escolherImagem( 3);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == Activity.RESULT_OK ){

            // Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            // Configura imagem no ImageView
            if ( requestCode == 1 ) {
                image1.setImageURI( imagemSelecionada );

            }else if ( requestCode == 2) {
                image2.setImageURI( imagemSelecionada );

            }else if ( requestCode == 3 ) {
                image3.setImageURI( imagemSelecionada );
            }

            listaFotosRecuperadas.add( caminhoImagem );

        }
    }


    private void inicializarComponentes(){

        campoTitulo    = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor     = findViewById(R.id.editValor);
        campoTelefone  = findViewById(R.id.editTelefone);
        image1         = findViewById(R.id.imageCadastro1);
        image2         = findViewById(R.id.imageCadastro2);
        image3         = findViewById(R.id.imageCadastro3);
        campoEstado    = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);

        // Configura localidade para pt -> portugues BR -> Brasil
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale( locale );

        // Adciona evento de click
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage(" Para utilizar o App é necessário aceitar as permissões");
        builder.setCancelable( false );
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void validarDadosAnuncio( View view){

        String estado    = campoEstado.getSelectedItem().toString();
        String cateforia = campoCategoria.getSelectedItem().toString();
        String titulo    = campoTitulo.getText().toString();
        String valor     = String.valueOf(campoValor.getRawValue());
        String telefone  = "";
        if ( campoTelefone.getUnMasked() != null ){
            telefone = campoTelefone.getUnMasked();
        }
        String descricao = campoDescricao.getText().toString();

        if (listaFotosRecuperadas.size() != 0) {

            if (!titulo.isEmpty()) {

                if (!valor.isEmpty() && !valor.equals("0")) {

                    if (!telefone.isEmpty() && telefone.length() >= 10) {

                        if (!descricao.isEmpty()) {

                            anuncio = configurarAnuncio();
                            salvarAnuncio();

                        } else {
                            exibirMensagemError("Preencha a descricao do produto!");
                        }

                    } else {
                        exibirMensagemError("Digite seu numero de telefone completo!");
                    }

                } else {
                    exibirMensagemError("Preencha o valor desejado!");
                }

            } else {
                exibirMensagemError("Preencha o titulo!");
            }

        } else {
            exibirMensagemError("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemError(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    public  void salvarAnuncio(){

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage(" Salvando Anúncio ")
                .setCancelable(false)
                .build();
        dialog.show();

        // Salvar imagem no Storage
        for (int i = 0; i < listaFotosRecuperadas.size(); i++){

            String urlImagem = listaFotosRecuperadas.get( i );
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage( urlImagem, tamanhoLista, i );

        }

    }

    private void salvarFotoStorage(String url, int totalFotos, int i) {

        // Criar nó no storage
        StorageReference imagemAnuncio = storage.child("Imagens")
                .child("Anuncios")
                .child( anuncio.getIdAnuncio() )
                .child("imagem" + i);
        // Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse( url ) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                       Uri uri = task.getResult();
                       String urlConvertida = uri.toString();
                       listaUrlFotos.add( urlConvertida );

                       if ( totalFotos == listaUrlFotos.size() ){
                           anuncio.setFotos( listaUrlFotos );
                           anuncio.salvar();

                           dialog.dismiss();
                           finish();
                       }

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                exibirMensagemError("Falha ao fazer upload");

            }
        });

    }

    private Anuncio configurarAnuncio(){

        String estado    = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo    = campoTitulo.getText().toString();
        String valor     = campoValor.getText().toString();
        String telefone  = campoTelefone.getUnMasked();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado( estado );
        anuncio.setCateforia( categoria );
        anuncio.setTitulo( titulo );
        anuncio.setValor( valor );
        anuncio.setTelefone( telefone );
        anuncio.setDescricao( descricao );

        return anuncio;
    }

    public  void escolherImagem( int requestCode ){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    private void carregarDadosSpinner() {

        String[] estados = getResources().getStringArray(R.array.estados);
        String[] categoria = getResources().getStringArray(R.array.categorias);


        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );
        adapterEstados.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        campoEstado.setAdapter( adapterEstados );



        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                categoria
        );
        adapterCategoria.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        campoCategoria.setAdapter( adapterCategoria );
    }

}