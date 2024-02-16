package com.example.enzof;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void IrAMain(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }
    public void IrASync(View view) {
        Intent i = new Intent(this, RS_uno_cuerpo.class);
        startActivity(i);
    }
    public void IrAejemplo(View view) {
        Intent i = new Intent(this, Historial.class);
        startActivity(i);
    }
    public void IrASeguimiento(View view) {
        Intent i = new Intent(this, Seguimiento.class);
        startActivity(i);
    }
    public void IrAInfo(View view) {
        Intent i = new Intent(this, Informacion.class);
        startActivity(i);
    }
    public void IrCalibracion(View view) {
        Intent i = new Intent(this, C_uno_musculo.class);
        startActivity(i);
    }
}