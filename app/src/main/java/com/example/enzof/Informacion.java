package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class Informacion extends AppCompatActivity {
    private LinearLayout l_pregunta1;
    private LinearLayout l_pregunta2;
    private LinearLayout l_pregunta3;
    private LinearLayout l_pregunta4;
    private LinearLayout l_pregunta5;
    private LinearLayout l_pregunta6;
    private LinearLayout l_pregunta7;
    private LinearLayout l_preguntaIND;
    private Spinner spinnerP;
    private String[] opcionesP = {"Información acerca de...", "¿Qué hace ENZOF?", "¿Cómo sincronizo el módulo Bluetooth?", "¿Cuándo usar ENZOF?", "¿Qué necesito?", "¿Cuál es el peso adecuado para los ejercicios?", "¿Con qué regularidad la debo usar?", "No tengo mancuernas ¿Qué hago?", "¿Qué significan los indicadores de fatiga?"};
    private String preguntaSeleccionada;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        //DECLARAR
        spinnerP = findViewById(R.id.lista_preguntas);
        layout = findViewById(R.id.layoutge);
        l_pregunta1 = findViewById(R.id.pregunta1);
        l_pregunta2 = findViewById(R.id.pregunta2);
        l_pregunta3 = findViewById(R.id.pregunta3);
        l_pregunta4 = findViewById(R.id.pregunta4);
        l_pregunta5 = findViewById(R.id.pregunta5);
        l_pregunta6 = findViewById(R.id.pregunta6);
        l_pregunta7 = findViewById(R.id.pregunta7);
        l_preguntaIND = findViewById(R.id.preguntaIND);
        //CONFIGURACION DEL SPINNER DE INFORMACION
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesP);
        adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerP.setAdapter(adapterD);
        spinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    preguntaSeleccionada = opcionesP[position];
                    dejarvacio();
                    cambiarpantalla();
                    Toast.makeText(Informacion.this, "Selecciono" + preguntaSeleccionada, Toast.LENGTH_SHORT).show();
                } else {
                    dejarvacio();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void dejarvacio() {
        l_pregunta1.setVisibility(View.GONE);
        l_pregunta2.setVisibility(View.GONE);
        l_pregunta3.setVisibility(View.GONE);
        l_pregunta4.setVisibility(View.GONE);
        l_pregunta5.setVisibility(View.GONE);
        l_pregunta6.setVisibility(View.GONE);
        l_pregunta7.setVisibility(View.GONE);
        l_preguntaIND.setVisibility(View.GONE);
    }
    public void IrAMenuI(View view) {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
    private void cambiarpantalla() {
        if (preguntaSeleccionada.equals("¿Qué hace ENZOF?")) {
            l_pregunta1.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Cómo sincronizo el módulo Bluetooth?")) {
            l_pregunta2.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Cuándo usar ENZOF?")) {
            l_pregunta3.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Qué necesito?")) {
            l_pregunta4.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Cuál es el peso adecuado para los ejercicios?")) {
            l_pregunta5.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Con qué regularidad la debo usar?")) {
            l_pregunta6.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("No tengo mancuernas ¿Qué hago?")) {
            l_pregunta7.setVisibility(View.VISIBLE);
        } else if (preguntaSeleccionada.equals("¿Qué significan los indicadores de fatiga?")) {
            l_preguntaIND.setVisibility(View.VISIBLE);
        }
    }
}