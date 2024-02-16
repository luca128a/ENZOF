package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity extends AppCompatActivity {
    private EditText correo;
    private EditText contrasena;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        correo=findViewById(R.id.correo);
        contrasena=findViewById(R.id.contrasena);
        mAuth=FirebaseAuth.getInstance();
    }
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent i= new Intent(getApplicationContext(), Menu.class);
            startActivity(i);
            Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
        } else {
            return;
        }
    }
    //Iniciar Sesion------------------------------------------------------------------
    public void IniciarSesion (View view){
        String Correo = correo.getText().toString();
        String Contrasena = contrasena.getText().toString();
        //Alerta si hay alguna variable vacía
        if (TextUtils.isEmpty(Correo) && TextUtils.isEmpty(Contrasena)) {
            Toast.makeText(this, "Por favor ingrese nombre de usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Correo)) {
            Toast.makeText(this, "Por favor ingrese nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Contrasena)) {
            Toast.makeText(this, "Por favor ingrese contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(Correo,Contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sesion inicada correctamente.",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i= new Intent(getApplicationContext(), Menu.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
    public void IrARegistro (View view) {
        Intent i = new Intent(this, Registrarse.class);
        startActivity(i);
    }
}
