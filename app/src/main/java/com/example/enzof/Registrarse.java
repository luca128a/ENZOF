package com.example.enzof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class Registrarse extends AppCompatActivity {
    private EditText correo;
    private EditText contraseña;
    private EditText contraseña2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        correo=findViewById(R.id.Introducir_correo);
        contraseña=findViewById(R.id.contraseña1);
        contraseña2=findViewById(R.id.contraseña2);
        mAuth = FirebaseAuth.getInstance();
    }
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    public void RegistrarUsuario (View view){
        String Correo = correo.getText().toString();
        String Contrasena = contraseña.getText().toString();
        String Contrasena2 = contraseña2.getText().toString();
        //Muestra alertas
        if (TextUtils.isEmpty(Correo) && TextUtils.isEmpty(Contrasena) && TextUtils.isEmpty(Contrasena2)) {
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
        if (TextUtils.isEmpty(Contrasena2)) {
            Toast.makeText(this, "Por favor confirme contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Contrasena.equals(Contrasena2)) {
            mAuth.createUserWithEmailAndPassword(Correo,Contrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "Usuario creado correctamente.",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i= new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
//                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                                //                             updateUI(null);
                            }
                        }
                    });

        }else {
            Toast.makeText(this,"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void IrAMain (View view){
        Intent i= new Intent(this, MainActivity.class);
        startActivity(i);
    }

}