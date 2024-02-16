package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;


public class RS_tres_ejercicio extends AppCompatActivity {
    //1. DECLARACIONES
    private Spinner SpinnerE;
    private String musculoSeleccionado;
    private String ladocorporal;
    private String tipoejercicio;
    private String[] opcionesE = {"Tipo de ejercicio","1. Cíclico con carga", "2. Cíclico sin carga", "3. Estático"};
    private ImageButton BContinuar;
    private NumberPicker PickerP;
    private float pesoSeleccionado = 0;
    private LinearLayout fondo;
    private LinearLayout carga_abd;
    private LinearLayout carga_abd_estatico;
    private LinearLayout carga_biceps;
    private LinearLayout carga_biceps_estatico;
    private LinearLayout carga_espalda;
    private LinearLayout carga_espalda_estatico;
    private LinearLayout carga_pectoral;
    private LinearLayout carga_pectoral_estatico;
    private LinearLayout carga_hombro;
    private LinearLayout carga_hombro_estatico;
    private LinearLayout sin_pecho;
    private LinearLayout sin_espalda;
    private LinearLayout sin_biceps;
    private LinearLayout sin_antebrazo;
    private LinearLayout sin_femoral;
    private LinearLayout sin_cuadriceps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_de_ejercicio);
        //1. DECLARACIÓN de PARAMETROS ------------------------------------------------------------------
        SpinnerE = findViewById(R.id.lista_tipoejercicio);
        BContinuar = findViewById(R.id.btn_confirmar);
        musculoSeleccionado = getIntent().getStringExtra("musculoSeleccionado");
        ladocorporal = getIntent().getStringExtra("ladoseleccionado");
        PickerP = findViewById(R.id.numberpicker);
        fondo = findViewById(R.id.fondo);
        carga_abd =findViewById(R.id.carga_abd);
        carga_abd_estatico = findViewById(R.id.carga_abd_est);
        carga_biceps = findViewById(R.id.carga_biceps);
        carga_biceps_estatico = findViewById(R.id.carga_biceps_est);
        carga_espalda = findViewById(R.id.carga_espalda);
        carga_espalda_estatico = findViewById(R.id.carga_espalda_est);
        carga_pectoral = findViewById(R.id.carga_pectoral);
        carga_pectoral_estatico = findViewById(R.id.carga_pectoral_est);
        carga_hombro = findViewById(R.id.carga_hombros);
        carga_hombro_estatico = findViewById(R.id.carga_hombros_est);
        sin_pecho = findViewById(R.id.sin_pectoral);
        sin_espalda = findViewById(R.id.sin_espalda);
        sin_biceps = findViewById(R.id.sin_biceps);
        sin_antebrazo = findViewById(R.id.sin_antebrazo);
        sin_femoral = findViewById(R.id.sin_femoral);
        sin_cuadriceps = findViewById(R.id.sin_cuadriceps);
        //2. CONFIGURACION del SPINNER -------------------------------------------------------------
        ArrayAdapter<String> adapterL = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesE);
        adapterL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerE.setAdapter(adapterL);
        SpinnerE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    tipoejercicio = opcionesE[position];
                    cambiarpantalla();
                } else {
                    dejarvacio();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        //3. CONFIGURACION DEL PESO-----------------------------------------------------------------
        PickerP.setMinValue(0);
        PickerP.setMaxValue(44); // 43 = (22 - 0.5) / 0.5
        String[] displayValues = new String[45];
        float value = 0;
        for (int i = 0; i < 45; i++) {
            displayValues[i] = String.format(Locale.getDefault(), "%.1f kg", value);
            value += 0.5f;
        }
        PickerP.setDisplayedValues(displayValues);
        pesoSeleccionado = PickerP.getValue() * 0.5f;
        //4. BOTON CONTINUAR -> Envia peso + Musculo + Tipo ejercicio + Lado corporal
        BContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesoSeleccionado = PickerP.getValue() * 0.5f;

                if ((opcionesE != null && opcionesE.length > 0 && SpinnerE.getSelectedItemPosition() == 0)) {
                    Toast.makeText(RS_tres_ejercicio.this, "Seleccione un tipo de ejercicio", Toast.LENGTH_SHORT).show();
                } else {
                    if ((tipoejercicio.equals("1. Cíclico con carga") || tipoejercicio.equals("3. Estático")) && (pesoSeleccionado == 0)) {
                        Toast.makeText(RS_tres_ejercicio.this, "Seleccione el peso a utilizar", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(RS_tres_ejercicio.this, RS_cuatro_registro.class);
                        intent.putExtra("musculoSeleccionado", musculoSeleccionado);
                        intent.putExtra("ladoSeleccionado", ladocorporal);
                        intent.putExtra("ejercicioSeleccionado", tipoejercicio);
                        intent.putExtra("pesoSeleccionado", pesoSeleccionado);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    // 5. FUNCIONES PARA CAMBIAR PANTALLA ------------------------------------------------
    public void dejarvacio() {
        fondo.setVisibility(View.VISIBLE);
        carga_abd.setVisibility(View.GONE);
        carga_abd_estatico.setVisibility(View.GONE);
        carga_biceps.setVisibility(View.GONE);
        carga_biceps_estatico.setVisibility(View.GONE);
        carga_espalda.setVisibility(View.GONE);
        carga_espalda_estatico.setVisibility(View.GONE);
        carga_pectoral_estatico.setVisibility(View.GONE);
        carga_pectoral.setVisibility(View.GONE);
        carga_hombro_estatico.setVisibility(View.GONE);
        carga_hombro.setVisibility(View.GONE);
        sin_espalda.setVisibility(View.GONE);
        sin_biceps.setVisibility(View.GONE);
        sin_antebrazo.setVisibility(View.GONE);
        sin_femoral.setVisibility(View.GONE);
        sin_pecho.setVisibility(View.GONE);
        sin_cuadriceps.setVisibility(View.GONE);
    }
    private void cambiarpantalla() {
        dejarvacio();
        if (tipoejercicio.equals("1. Cíclico con carga")) {
            dejarvacio();
            PickerP.setEnabled(true);
            Toast.makeText(this, "Por favor, seleccione peso de la carga", Toast.LENGTH_SHORT).show();
            if (musculoSeleccionado.equals("1. Abdomen (Abdominales)") || musculoSeleccionado.equals("9. Muslo anterior (Cuadríceps)") || musculoSeleccionado.equals("10. Muslo posterior (Isquiotibiales)") || musculoSeleccionado.equals("12. Pierna (Pantorilla)")) {
                fondo.setVisibility(View.GONE);
                carga_abd.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("2. Antebrazo") || musculoSeleccionado.equals("3. Brazo anterior (Bíceps)")) {
                fondo.setVisibility(View.GONE);
                carga_biceps.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("4. Brazo posterior (Tríceps)") || musculoSeleccionado.equals("11. Pecho (Pectorales)")) {
                fondo.setVisibility(View.GONE);
                carga_pectoral.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("5. Espalda alta (Trapecio)") || musculoSeleccionado.equals("6. Espalda baja (Lumbares)") || musculoSeleccionado.equals("7. Espalda lateral (Dorsales)")) {
                fondo.setVisibility(View.GONE);
                carga_espalda.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("8. Hombros")) {
                fondo.setVisibility(View.GONE);
                carga_hombro.setVisibility(View.VISIBLE);
            } else {
                //nada
            }
        } else if (tipoejercicio.equals("2. Cíclico sin carga")) {
            dejarvacio();
            PickerP.setEnabled(false);
            PickerP.setValue(0);
            if (musculoSeleccionado.equals("8. Hombros") || musculoSeleccionado.equals("1. Abdomen (Abdominales)") || musculoSeleccionado.equals("11. Pecho (Pectorales)") || musculoSeleccionado.equals("4. Brazo posterior (Tríceps)")) {
                fondo.setVisibility(View.GONE);
                sin_pecho.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("2. Antebrazo")) {
                fondo.setVisibility(View.GONE);
                sin_antebrazo.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("3. Brazo anterior (Bíceps)")) {
                fondo.setVisibility(View.GONE);
                sin_biceps.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("5. Espalda alta (Trapecio)") || musculoSeleccionado.equals("6. Espalda baja (Lumbares)") || musculoSeleccionado.equals("7. Espalda lateral (Dorsales)")) {
                fondo.setVisibility(View.GONE);
                sin_espalda.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("10. Muslo posterior (Isquiotibiales)") || musculoSeleccionado.equals("12. Pierna (Pantorilla)")) {
                fondo.setVisibility(View.GONE);
                sin_femoral.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("9. Muslo anterior (Cuadríceps)")) {
                fondo.setVisibility(View.GONE);
                sin_cuadriceps.setVisibility(View.VISIBLE);
            } else {
                //nada
            }
        } else if (tipoejercicio.equals("3. Estático")) {
            PickerP.setEnabled(true);
            Toast.makeText(this, "Por favor, seleccione peso de la carga", Toast.LENGTH_SHORT).show();

            if (musculoSeleccionado.equals("1. Abdomen (Abdominales)") || musculoSeleccionado.equals("9. Muslo anterior (Cuadríceps)") || musculoSeleccionado.equals("10. Muslo posterior (Isquiotibiales)") || musculoSeleccionado.equals("12. Pierna (Pantorilla)")) {
                fondo.setVisibility(View.GONE);
                carga_abd_estatico.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("2. Antebrazo") || musculoSeleccionado.equals("3. Brazo anterior (Bíceps)")) {
                fondo.setVisibility(View.GONE);
                carga_biceps_estatico.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("4. Brazo posterior (Tríceps)") || musculoSeleccionado.equals("11. Pecho (Pectorales)")) {
                fondo.setVisibility(View.GONE);
                carga_pectoral_estatico.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("5. Espalda alta (Trapecio)") || musculoSeleccionado.equals("6. Espalda baja (Lumbares)") || musculoSeleccionado.equals("7. Espalda lateral (Dorsales)")) {
                fondo.setVisibility(View.GONE);
                carga_espalda_estatico.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("8. Hombros")) {
                fondo.setVisibility(View.GONE);
                carga_hombro_estatico.setVisibility(View.VISIBLE);
            } else {
                //nada
            }
        } else {
            //Nada
        }
    }
    // 6. VOLVER ATRAS
    public void IrALado(View view) {
        Intent i = new Intent(this, RS_dos_ladocorporal.class);
        startActivity(i);
    }
}

