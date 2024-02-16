package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

public class Seguimiento extends AppCompatActivity {

    private Spinner SpinnerM;
    private String Musculo;
    private String[] opcionesM = {"Zona del cuerpo","0. Todos", "1. Abdomen (Abdominales)","2. Antebrazo", "3. Brazo anterior (Bíceps)", "4. Brazo posterior (Tríceps)","5. Espalda alta (Trapecio)","6. Espalda baja (Lumbares)", "7. Espalda lateral (Dorsales)", "8. Hombros","9. Muslo anterior (Cuadríceps)", "10. Muslo posterior (Isquiotibiales)", "11. Pecho (Pectorales)", "12. Pierna (Pantorilla)"};
    private Spinner SpinnerL;
    private String LadoCorporal;
    private String[] opcionesL = {"Lado corporal", "1. Lado Derecho", "2. Lado Izquierdo"};
    private Spinner SpinnerE;
    private String Ejercicio;
    private String[] opcionesE = {"Tipo de ejercicio","0. Todos","1. Cíclico con carga", "2. Cíclico sin carga", "3. Estático"};
    private ImageButton btn_continuar;
    private NumberPicker Picker1;
    private NumberPicker Picker2;
    private float peso1;
    private float peso2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento);
        //CONFIGURACIÓN DE SPINNER------------------------------------------------------------------
        SpinnerM = findViewById(R.id.lista_musculo);
        SpinnerL = findViewById(R.id.lista_ladocorporal);
        SpinnerE = findViewById(R.id.lista_tipoejercicio);
        //OPCIONES de CADA UNO----------------------------------------------------------------------
        ArrayAdapter<String> adapterM = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesM);
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerM.setAdapter(adapterM);
        ArrayAdapter<String> adapterL = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesL);
        adapterL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerL.setAdapter(adapterL);
        ArrayAdapter<String> adapterE = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesE);
        adapterE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerE.setAdapter(adapterE);
        SpinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String MusculoSeleccionado = opcionesM[position];
                //Se queda solo con el músculo -----------------------------------------------------
                String[] partes = MusculoSeleccionado.split("\\.\\s");
                if (partes.length >= 2) {
                    String resultado = partes[1];
                    int indiceParentesis = resultado.indexOf("(");
                    if (!MusculoSeleccionado.equals("Todos") && indiceParentesis != -1) {
                        // Extraer la subcadena antes del paréntesis
                        Musculo = resultado.substring(0, indiceParentesis);
                    } else {
                        Musculo = resultado;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        SpinnerL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String LadoSeleccionado = opcionesL[position];
                //Se queda solo con el músculo -----------------------------------------------------
                String[] partes = LadoSeleccionado.split("\\.\\s");
                if (partes.length >= 2) {
                    LadoCorporal = partes[1];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        SpinnerE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String EjercicioSeleccionado = opcionesE[position];

                //Se queda solo con el músculo -----------------------------------------------------
                String[] partes = EjercicioSeleccionado.split("\\.\\s");
                if (partes.length >= 2) {
                    Ejercicio = partes[1];
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        // CONFIGURACION DE LOS PICKERS ------------------------------------------------------------
        Picker1 = findViewById(R.id.numberpicker1);
        Picker2 = findViewById(R.id.numberpicker2);
        Picker1.setValue(0);
        Picker2.setValue(22);
        Picker1.setMinValue(0);
        Picker1.setMaxValue(44); // 43 = (22 - 0.5) / 0.5
        String[] displayValues = new String[45];
        float value = 0;
        for (int i = 0; i < 45; i++) {
            displayValues[i] = String.format(Locale.getDefault(), "%.1f kg", value);
            value += 0.5f;
        }
        Picker1.setDisplayedValues(displayValues);
        peso1 = Picker1.getValue() * 0.5f;
        Picker2.setMinValue(0);
        Picker2.setMaxValue(44); // 43 = (22 - 0.5) / 0.5
        Picker2.setValue(44);
        String[] displayValues2 = new String[45];
        float value2 = 0;
        for (int i = 0; i < 45; i++) {
            displayValues2[i] = String.format(Locale.getDefault(), "%.1f kg", value2);
            value2 += 0.5f;
        }
        Picker2.setDisplayedValues(displayValues2);
        peso2 = Picker2.getValue() * 0.5f;
        Picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Actualizar el valor de peso1 cuando cambia el picker
                peso1 = newVal * 0.5f;
            }
        });
        Picker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Actualizar el valor de peso2 cuando cambia el picker
                peso2 = newVal * 0.5f;
            }
        });
        //BOTON CONTINUAR --------------------------------------------------------------------------
        btn_continuar = findViewById(R.id.btn_continuar);
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Musculo == null) {
                    Toast.makeText(Seguimiento.this, "Seleccione la zona del cuerpo para continuar", Toast.LENGTH_SHORT).show();
                } else if (LadoCorporal == null) {
                    Toast.makeText(Seguimiento.this, "Seleccione el lado corporal para continuar", Toast.LENGTH_SHORT).show();
                } else if (Ejercicio == null) {
                    Toast.makeText(Seguimiento.this, "Seleccione el tipo de ejercicio para continuar", Toast.LENGTH_SHORT).show();
                } else if (peso1 > peso2) {
                    Toast.makeText(Seguimiento.this, "Seleccione un rango de peso valido", Toast.LENGTH_SHORT).show();
                } else {
                    continuar();
                }
            }
        });
    }
    private void continuar () {
        Intent intent = new Intent(Seguimiento.this, Seguimiento2.class);
        // Agregar los valores como extras al Intent
        intent.putExtra("LadoCorporal", LadoCorporal);
        intent.putExtra("Musculo",Musculo);
        intent.putExtra("Ejercicio", Ejercicio);
        intent.putExtra("Peso1", peso1);
        intent.putExtra("Peso2", peso2);
        startActivity(intent);
    }
    public void IrAMenuS2(View view){
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
}