package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class RS_uno_cuerpo extends AppCompatActivity {
    //1. DECLARACIONES PARAMETROS ------------------------------------------------------------------
    private Spinner SpinnerM;
    private String musculoSeleccionado;
    private String[] opcionesM = {"Zona del cuerpo","1. Abdomen (Abdominales)","2. Antebrazo", "3. Brazo anterior (Bíceps)", "4. Brazo posterior (Tríceps)","5. Espalda alta (Trapecio)","6. Espalda baja (Lumbares)", "7. Espalda lateral (Dorsales)", "8. Hombros","9. Muslo anterior (Cuadríceps)", "10. Muslo posterior (Isquiotibiales)", "11. Pecho (Pectorales)", "12. Pierna (Pantorilla)"};
    private ImageButton BContinuar;
    private ImageButton BVolver;
    private ImageView musculos;
    private ImageView m_abdominal;
    private ImageView m_antebrazo;
    private ImageView m_biceps;
    private ImageView m_triceps;
    private ImageView m_espaldabaja;
    private ImageView m_trapecio;
    private ImageView m_dorsales;
    private ImageView m_gluteos;
    private ImageView m_hombros;
    private ImageView m_cuadriceps;
    private ImageView m_femoral;
    private ImageView m_pectoral;
    private ImageView m_pierna;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partecuerpofeb);
        // Declaración de los parámetros
        SpinnerM = findViewById(R.id.lista_musculo);
        BContinuar = findViewById(R.id.btn_confirmar);
        musculos = findViewById(R.id.musculos);
        m_abdominal = findViewById(R.id.m_abdominal);
        m_antebrazo = findViewById(R.id.m_antebrazo);
        m_biceps = findViewById(R.id.m_biceps);
        m_triceps = findViewById(R.id.m_triceps);
        m_espaldabaja = findViewById(R.id.m_espaldabaja);
        m_trapecio = findViewById(R.id.m_espaldaalta);
        m_dorsales = findViewById(R.id.m_dorsal);
        m_gluteos = findViewById(R.id.m_gluteo);
        m_hombros = findViewById(R.id.m_hombro);
        m_cuadriceps = findViewById(R.id.m_cuadriceps);
        m_femoral = findViewById(R.id.m_femoral);
        m_pectoral = findViewById(R.id.m_pectoral);
        m_pierna = findViewById(R.id.m_pantorilla);
        // 2. Configuración del Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerM.setAdapter(adapter);
        SpinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    musculoSeleccionado = opcionesM[position];
                    cambiarpantalla();
                } else {
                    dejarvacio();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dejarvacio();
            }
        });
        // 3. BOTON CONTINUAR  -----------------------------------------------------------
        BContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((opcionesM != null && opcionesM.length > 0 && SpinnerM.getSelectedItemPosition() == 0)) {
                    Toast.makeText(RS_uno_cuerpo.this, "Seleccione una zona corporal", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RS_uno_cuerpo.this, RS_dos_ladocorporal.class);
                    intent.putExtra("musculoSeleccionado", musculoSeleccionado);
                    startActivity(intent);
                }
            }
        });
    }
    // 4. Opcion dejar vacio ------------------------------------------
    public void dejarvacio() {
        musculos.setVisibility(View.VISIBLE);
        m_abdominal.setVisibility(View.GONE);
        m_antebrazo.setVisibility(View.GONE);
        m_biceps.setVisibility(View.GONE);
        m_triceps.setVisibility(View.GONE);
        m_espaldabaja.setVisibility(View.GONE);
        m_trapecio.setVisibility(View.GONE);
        m_dorsales.setVisibility(View.GONE);
        m_gluteos.setVisibility(View.GONE);
        m_hombros.setVisibility(View.GONE);
        m_cuadriceps.setVisibility(View.GONE);
        m_femoral.setVisibility(View.GONE);
        m_pectoral.setVisibility(View.GONE);
        m_pierna.setVisibility(View.GONE);
    }
    // 5. Opcion cambiar la pantalla ----------------------------------
    private void cambiarpantalla() {
        dejarvacio();
        if (musculoSeleccionado==null) {
            Toast.makeText(RS_uno_cuerpo.this, "Seleccione músculo", Toast.LENGTH_SHORT).show();
        } else if ((opcionesM != null && opcionesM.length > 0 && SpinnerM.getSelectedItemPosition() == 0)) {
            Toast.makeText(RS_uno_cuerpo.this, "Seleccione acerca de que quiere saber más", Toast.LENGTH_SHORT).show();
        } else {
            if (musculoSeleccionado.equals("1. Abdomen (Abdominales)")) {
                musculos.setVisibility(View.GONE);
                m_abdominal.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("2. Antebrazo")) {
                musculos.setVisibility(View.GONE);
                m_antebrazo.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("3. Brazo anterior (Bíceps)")) {
                musculos.setVisibility(View.GONE);
                m_biceps.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("4. Brazo posterior (Tríceps)")) {
                musculos.setVisibility(View.GONE);
                m_triceps.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("5. Espalda alta (Trapecio)")) {
                musculos.setVisibility(View.GONE);
                m_trapecio.setVisibility(View.VISIBLE);
            }  else if (musculoSeleccionado.equals("6. Espalda baja (Lumbares)")) {
                musculos.setVisibility(View.GONE);
                m_espaldabaja.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("7. Espalda lateral (Dorsales)")) {
                musculos.setVisibility(View.GONE);
                m_dorsales.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("8. Hombros")) {
                musculos.setVisibility(View.GONE);
                m_hombros.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("9. Muslo anterior (Cuadríceps)")) {
                musculos.setVisibility(View.GONE);
                m_cuadriceps.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("10. Muslo posterior (Isquiotibiales)")) {
                musculos.setVisibility(View.GONE);
                m_femoral.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("11. Pecho (Pectorales)")) {
                musculos.setVisibility(View.GONE);
                m_pectoral.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("12. Pierna (Pantorilla)")) {
                musculos.setVisibility(View.GONE);
                m_pierna.setVisibility(View.VISIBLE);
            } else {
                //nada
            }
        }
    }
    // 6. Boton ir a menu ---------------------------------------------
    public void IrAMenuC(View view) {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
}