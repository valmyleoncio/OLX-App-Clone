package com.example.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.olxapp.R;
import com.example.olxapp.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicializar componentes
        inicializarComponentes();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if ( !email.isEmpty() ){

                    if ( !senha.isEmpty() ){

                        // Verificar estado do switch
                        if (tipoAcesso.isChecked()){ // Cadastro

                            autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                        finish();

                                    }else{

                                        String erroExcecao = "";

                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte";
                                        }catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor, digite uma e-mail válido";
                                        }catch (FirebaseAuthUserCollisionException e) {
                                            erroExcecao = "Esta conta ja foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(CadastroActivity.this,"Error: " + erroExcecao, Toast.LENGTH_SHORT).show();
                                        
                                    }

                                }
                            });

                        }else{ // Login

                            autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if ( task.isSuccessful() ){

                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                        finish();

                                    }else {

                                        Toast.makeText(CadastroActivity.this, "Erro ao fazer o login : " + task.getException(), Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                        }


                    }else{
                        Toast.makeText(CadastroActivity.this, "Preencha a Senha", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void inicializarComponentes(){

        botaoAcessar = findViewById(R.id.buttonAcesso);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

    }
}