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

import java.util.Arrays;

public class C_dos_ladocorporal extends AppCompatActivity {
    //1. DECLARACIONES PARAMETROS ------------------------------------------------------------------
    private Spinner SpinnerM;
    private Spinner SpinnerL;
    private String musculoSeleccionado;
    private String[] opcionesM = {"Zona del cuerpo","1. Abdomen (Abdominales)","2. Antebrazo", "3. Brazo anterior (Bíceps)", "4. Brazo posterior (Tríceps)","5. Espalda alta (Trapecio)","6. Espalda baja (Lumbares)", "7. Espalda lateral (Dorsales)", "8. Hombros","9. Muslo anterior (Cuadríceps)", "10. Muslo posterior (Isquiotibiales)", "11. Pecho (Pectorales)", "12. Pierna (Pantorilla)"};
    private String ladocorporal;
    private String[] opcionesL = {"Lado corporal", "1. Lado Derecho", "2. Lado Izquierdo"};
    private ImageButton BContinuar;
    private ImageButton BVolver;
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
        setContentView(R.layout.activity_cdos_ladocorporal);
        //DECLARACIÓN de PARAMETROS
        SpinnerM = findViewById(R.id.lista_musculo);
        SpinnerL = findViewById(R.id.lista_lado);
        BContinuar = findViewById(R.id.btn_confirmar);
        m_abdominal = findViewById(R.id.elec_abdomen);
        m_antebrazo = findViewById(R.id.elec_antebrazo);
        m_biceps = findViewById(R.id.elec_biceps);
        m_triceps = findViewById(R.id.elec_triceps);
        m_espaldabaja = findViewById(R.id.elec_espaldabaja);
        m_trapecio = findViewById(R.id.elec_espaldaalta);
        m_dorsales = findViewById(R.id.elec_espaldalateral);
        m_hombros = findViewById(R.id.elec_hombros);
        m_cuadriceps = findViewById(R.id.elec_cuadriceps);
        m_femoral = findViewById(R.id.elec_femoral);
        m_pectoral = findViewById(R.id.elec_pectoral);
        m_pierna = findViewById(R.id.elec_pantorilla);
        //1. MUSCULO SELECCIONADO + SPINNER
        musculoSeleccionado = getIntent().getStringExtra("musculoSeleccionado");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerM.setAdapter(adapter);
        int posicionMusculoSeleccionado = Arrays.asList(opcionesM).indexOf(musculoSeleccionado);
        if (posicionMusculoSeleccionado >= 0) {
            SpinnerM.setSelection(posicionMusculoSeleccionado);
        }
        SpinnerM.setEnabled(false);
        //2. SPINNER LADO CORPORAL
        ArrayAdapter<String> adapterL = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesL);
        adapterL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerL.setAdapter(adapterL);
        SpinnerL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ladocorporal = opcionesL[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        // 3. IMAGENES------------------------------------------------------------------------------
        cambiarpantalla();
        // 4. BOTON CONTINUAR -> ENVIA MUSCULO + LADO CORPORAL
        BContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((opcionesL != null && opcionesL.length > 0 && SpinnerL.getSelectedItemPosition() == 0)) {
                    Toast.makeText(C_dos_ladocorporal.this, "Seleccione un lado", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(C_dos_ladocorporal.this, C_tres_ejercicio.class);
                    intent.putExtra("musculoSeleccionado", musculoSeleccionado);
                    intent.putExtra("ladoseleccionado", ladocorporal);
                    startActivity(intent);
                }
            }
        });
    }
    private void cambiarpantalla() {
        //dejarvacio();
        if (musculoSeleccionado.equals("1. Abdomen (Abdominales)")) {
            m_abdominal.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("2. Antebrazo")) {
            m_antebrazo.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("3. Brazo anterior (Bíceps)")) {
            m_biceps.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("4. Brazo posterior (Tríceps)")) {
            m_triceps.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("5. Espalda alta (Trapecio)")) {
            m_trapecio.setVisibility(View.VISIBLE);
        }  else if (musculoSeleccionado.equals("6. Espalda baja (Lumbares)")) {
            m_espaldabaja.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("7. Espalda lateral (Dorsales)")) {
            m_dorsales.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("8. Hombros")) {
            m_hombros.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("9. Muslo anterior (Cuadríceps)")) {
            m_cuadriceps.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("10. Muslo posterior (Isquiotibiales)")) {
            m_femoral.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("11. Pecho (Pectorales)")) {
            m_pectoral.setVisibility(View.VISIBLE);
        } else if (musculoSeleccionado.equals("12. Pierna (Pantorilla)")) {
            m_pierna.setVisibility(View.VISIBLE);
        } else {
            //nada
        }
    }
    // 6. VOLVER ATRAS
    public void IrAParteCuerpo(View view) {
        Intent i = new Intent(this, RS_uno_cuerpo.class);
        startActivity(i);
    }
}