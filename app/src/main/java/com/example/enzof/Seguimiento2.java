package com.example.enzof;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.LinkProperties;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.example.enzof.modelo.RegistrosEMG;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Guard;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
public class Seguimiento2 extends AppCompatActivity {
    // VARIABLES ELEGIDAS --------------------------------------------------------------------------
    private String Musculo;
    private String LadoCorporal;
    private String Ejercicio;
    private Float Peso1;
    private Float Peso2;
    // VARIABLES FIREBASE --------------------------------------------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseAuth auth;
    // VARIABLES del XML ---------------------------------------------------------------------------
    private TextView Texto1;
    private TextView Texto2;
    private TextView Texto3;
    private String Clave;
    private String Sub;
    private String Clave2;
    private LinearLayout layout_barras;
    private LinearLayout layout_torta;
    private LinearLayout leyendaTM;
    private LinearLayout leyendaTE;
    private BarChart barChart;
    private PieChart pieChart1;
    private PieChart pieChart2;
    //ENVIO de PDF por MAIL-------------------------------------------------------------------------
    private PdfDocument document;
    private String emailsend;
    private List<List<String>> Tabla;
    private List<DataSnapshot> nodosFiltrados;
    private Float PorcentajeF;
    private List<String> LFecha;
    private List<String> LMusculo;
    private List<String> LEjercicio;
    private List<String> LLado;
    private List<String> LFatiga;
    private List<Float> LPeso;
    private List<Double> LTFatiga;
    private ImageButton BMail;
    private File fileB;
    private File fileP1;
    private File fileP2;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento2);
        //DECLARACIÓN VARIABLES del XML ------------------------------------------------------------
        layout_barras = findViewById(R.id.layout_GBarras);
        layout_torta = findViewById(R.id.layout_GTortas);
        layout_barras.setVisibility(View.VISIBLE);
        layout_torta.setVisibility(View.GONE);
        leyendaTM = findViewById(R.id.LEYENDA_TM);
        leyendaTE = findViewById(R.id.LEYENDA_TE);
        barChart = findViewById(R.id.barChart);
        pieChart1 = findViewById(R.id.pieChart1);
        pieChart2 = findViewById(R.id.pieChart2);
        pieChart1.setBackgroundColor(Color.BLACK);
        pieChart2.setBackgroundColor(Color.BLACK);
        Texto1 = findViewById(R.id.texto1);
        Texto2 = findViewById(R.id.texto2);
        Texto3 = findViewById(R.id.texto3);
        Clave2 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        BMail = findViewById(R.id.BotonEmail);
        //FIREBASE ---------------------------------------------------------------------------------
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("RegistrosEMG");
        myRef.keepSynced(false);
        auth = FirebaseAuth.getInstance();
        //VALORES de la VENTANA ANTERIOR -----------------------------------------------------------
        Intent intent = getIntent();
        LadoCorporal = intent.getStringExtra("LadoCorporal");
        Musculo = intent.getStringExtra("Musculo");
        Ejercicio = intent.getStringExtra("Ejercicio");
        Peso1 = intent.getFloatExtra("Peso1", 0.0f);
        Peso2 = intent.getFloatExtra("Peso2", 0.0f);
        //DECLARACION de las LISTAS ----------------------------------------------------------------
        LFecha = new ArrayList<>();
        LMusculo = new ArrayList<>();
        LEjercicio = new ArrayList<>();
        LLado = new ArrayList<>();
        LPeso = new ArrayList<>();
        LFatiga = new ArrayList<>();
        LTFatiga = new ArrayList<>();
        nodosFiltrados = new ArrayList<>();
        //ENVIO de MAIL ----------------------------------------------------------------------------
        document = new PdfDocument();
        Tabla = new ArrayList<>();
        // RECUPERO DATOS DE FIREBASE SEGUN LO SELECCIONADO ----------------------------------------
        if (!Musculo.equals("Todos") && !Ejercicio.equals("Todos")) {
            Sub = Musculo.trim().toLowerCase() + " del " + LadoCorporal.trim().toLowerCase() + "\n Ejercicio " + Ejercicio.trim().toLowerCase();
            Clave = " ";
            pieChart2.setVisibility(View.INVISIBLE);
            myRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //OBTIENE LOS VALORES de LAS CLAVES --------------------------------------------
                    for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                        String ladocorporal = fechaSnapshot.child("ladocorporal").getValue(String.class);
                        String musculo = fechaSnapshot.child("musculo").getValue(String.class);
                        String tipoejercicio = fechaSnapshot.child("tipoejercicio").getValue(String.class);
                        Float peso = fechaSnapshot.child("peso").getValue(Float.class);
                        //VERIFICA CUALES COINCIDEN CON LO SELECCIONADO ----------------------------
                        if (ladocorporal != null && ladocorporal.equals(LadoCorporal)
                                && musculo != null && musculo.equals(Musculo)
                                && tipoejercicio != null && tipoejercicio.equals(Ejercicio)
                                && peso != null && peso >= Peso1 && peso <= Peso2) {
                            nodosFiltrados.add(fechaSnapshot);
                            // OBTIENE VALORES y AGREGA A LAS LISTAS -------------------------------
                            LFecha.add((fechaSnapshot.getKey()).split(" ")[0]);
                            LPeso.add(fechaSnapshot.child("peso").getValue(Float.class));
                            LFatiga.add(fechaSnapshot.child("presento").getValue(String.class));
                            LTFatiga.add(fechaSnapshot.child("tiempof").getValue(Double.class));
                        }
                    }
                    //COMPLETO LA TABLA por POSIBLE PDF --------------------------------------------
                    Tabla.add(Arrays.asList("Fecha", "Peso utilizado (kg)", "Presento fatiga", "Tiempo de fatiga (seg)"));
                    for (int i = 0; i < LFecha.size(); i++) {
                        List<String> fila = new ArrayList<>();
                        fila.add(LFecha.get(i));
                        fila.add(String.valueOf(LPeso.get(i)));
                        fila.add(LFatiga.get(i));
                        fila.add(String.valueOf(LTFatiga.get(i)));
                        Tabla.add(fila);
                    }
                    GraficoBarras(LFecha, LTFatiga, LPeso, null, null);
                    GraficoTorta(LFatiga);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Seguimiento2.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (Musculo.equals("Todos") && !Ejercicio.equals("Todos")) {
            Clave = " ";
            Sub = LadoCorporal.trim().toLowerCase() + "\n Ejercicio: " + Ejercicio.trim().toLowerCase();
            Texto3.setText("% Músculos trabajados");
            leyendaTM.setVisibility(View.VISIBLE);
            pieChart2.setVisibility(View.VISIBLE);
            myRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //OBTIENE LOS VALORES de LAS CLAVES --------------------------------------------
                    for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                        String ladocorporal = fechaSnapshot.child("ladocorporal").getValue(String.class);
                        String musculo = fechaSnapshot.child("musculo").getValue(String.class);
                        String tipoejercicio = fechaSnapshot.child("tipoejercicio").getValue(String.class);
                        Float peso = fechaSnapshot.child("peso").getValue(Float.class);
                        //VERIFICA CUALES COINCIDEN CON LO SELECCIONADO ----------------------------
                        if (ladocorporal != null && ladocorporal.equals(LadoCorporal)
                                && tipoejercicio != null && tipoejercicio.equals(Ejercicio)
                                && peso != null && peso >= Peso1 && peso <= Peso2) {
                            nodosFiltrados.add(fechaSnapshot);
                            // OBTIENE VALORES y AGREGA A LAS LISTAS -------------------------------
                            LFecha.add((fechaSnapshot.getKey()).split(" ")[0]);
                            LPeso.add(fechaSnapshot.child("peso").getValue(Float.class));
                            LFatiga.add(fechaSnapshot.child("presento").getValue(String.class));
                            LTFatiga.add(fechaSnapshot.child("tiempof").getValue(Double.class));
                            LMusculo.add(fechaSnapshot.child("musculo").getValue(String.class));
                        }
                    }
                    //COMPLETO LA TABLA por POSIBLE PDF --------------------------------------------
                    Tabla.add(Arrays.asList("Fecha", "Musculo", "Presento fatiga", "Fatiga (seg)"));
                    for (int i = 0; i < LFecha.size(); i++) {
                        List<String> fila = new ArrayList<>();
                        fila.add(LFecha.get(i));
                        fila.add(LMusculo.get(i));
                        fila.add(LFatiga.get(i));
                        fila.add(String.valueOf(LTFatiga.get(i)));
                        Tabla.add(fila);
                    }
                    //GRAFICOS en PANTALLA ---------------------------------------------------------
                    GraficoBarras(LFecha, LTFatiga, LPeso, LMusculo, null);
                    GraficoTorta(LFatiga);
                    GraficoTortaMT(LMusculo);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Seguimiento2.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!Musculo.equals("Todos") && Ejercicio.equals("Todos")) {
            Clave = Musculo.trim().toLowerCase() + " del " + LadoCorporal.trim().toLowerCase();
            Sub = Clave;
            Texto3.setText("% Tipos de ejercicio realizados");
            leyendaTE.setVisibility(View.VISIBLE);
            pieChart2.setVisibility(View.VISIBLE);
            myRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //OBTIENE LOS VALORES de LAS CLAVES --------------------------------------------
                    for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                        String ladocorporal = fechaSnapshot.child("ladocorporal").getValue(String.class);
                        String musculo = fechaSnapshot.child("musculo").getValue(String.class);
                        Float peso = fechaSnapshot.child("peso").getValue(Float.class);
                        //VERIFICA CUALES COINCIDEN CON LO SELECCIONADO ----------------------------
                        if (ladocorporal != null && ladocorporal.equals(LadoCorporal)
                                && musculo != null && musculo.equals(Musculo)
                                && peso != null && peso >= Peso1 && peso <= Peso2) {
                            nodosFiltrados.add(fechaSnapshot);
                            // OBTIENE VALORES y AGREGA A LAS LISTAS -------------------------------
                            LFecha.add((fechaSnapshot.getKey()).split(" ")[0]);
                            LPeso.add(fechaSnapshot.child("peso").getValue(Float.class));
                            LFatiga.add(fechaSnapshot.child("presento").getValue(String.class));
                            LTFatiga.add(fechaSnapshot.child("tiempof").getValue(Double.class));
                            LEjercicio.add(fechaSnapshot.child("tipoejercicio").getValue(String.class));
                        }
                    }
                    //COMPLETO LA TABLA por POSIBLE PDF --------------------------------------------
                    Tabla.add(Arrays.asList("Fecha", "Tipo de ejercicio", "Peso utilizado (kg)", "Presento fatiga", "Fatiga (seg)"));
                    for (int i = 0; i < LFecha.size(); i++) {
                        List<String> fila = new ArrayList<>();
                        fila.add(LFecha.get(i));
                        fila.add(LEjercicio.get(i));
                        fila.add(String.valueOf(LPeso.get(i)));
                        fila.add(LFatiga.get(i));
                        fila.add(String.valueOf(LTFatiga.get(i)));
                        Tabla.add(fila);
                    }
                    //GRAFICOS en PANTALLA ---------------------------------------------------------
                    GraficoBarras(LFecha, LTFatiga, LPeso, null, LEjercicio);
                    GraficoTorta(LFatiga);
                    GraficoTortaET(LEjercicio);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Seguimiento2.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Clave = "Todas las sesiones entre " + Peso1 + " - " + Peso2 + " kg.";
            Sub = Clave;
            leyendaTM.setVisibility(View.VISIBLE);
            pieChart2.setVisibility(View.INVISIBLE);
            myRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //OBTIENE LOS VALORES de LAS CLAVES --------------------------------------------
                    for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                        String ladocorporal = fechaSnapshot.child("ladocorporal").getValue(String.class);
                        Float peso = fechaSnapshot.child("peso").getValue(Float.class);
                        //VERIFICA CUALES COINCIDEN CON LO SELECCIONADO ----------------------------
                        if (ladocorporal != null && ladocorporal.equals(LadoCorporal)
                                && peso != null && peso >= Peso1 && peso <= Peso2) {
                            nodosFiltrados.add(fechaSnapshot);
                            // OBTIENE VALORES y AGREGA A LAS LISTAS -------------------------------
                            LFecha.add((fechaSnapshot.getKey()).split(" ")[0]);
                            LPeso.add(fechaSnapshot.child("peso").getValue(Float.class));
                            LFatiga.add(fechaSnapshot.child("presento").getValue(String.class));
                            LTFatiga.add(fechaSnapshot.child("tiempof").getValue(Double.class));
                            LEjercicio.add(fechaSnapshot.child("tipoejercicio").getValue(String.class));
                            LMusculo.add(fechaSnapshot.child("musculo").getValue(String.class));
                        }
                    }
                    //COMPLETO LA TABLA por POSIBLE PDF --------------------------------------------
                    Tabla.add(Arrays.asList("Fecha", "Musculo", "Tipo de ejercicio", "Peso utilizado (kg)", "Presento fatiga", "Fatiga (seg)"));
                    for (int i = 0; i < LFecha.size(); i++) {
                        List<String> fila = new ArrayList<>();
                        fila.add(LFecha.get(i));
                        fila.add(LMusculo.get(i));
                        fila.add(LEjercicio.get(i));
                        fila.add(String.valueOf(LPeso.get(i)));
                        fila.add(LFatiga.get(i));
                        fila.add(String.valueOf(LTFatiga.get(i)));
                        Tabla.add(fila);
                    }
                    //GRAFICOS en PANTALLA ---------------------------------------------------------
                    GraficoBarras(LFecha, LTFatiga, LPeso, LMusculo, LEjercicio);
                    GraficoTorta(LFatiga);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Seguimiento2.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Texto1.setText(Sub);
        Texto2.setText(Sub);
    }
    //----------------------------------------------------------------------------------------------
    // BOTONES -------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    public void IrATortas(View view){
        layout_barras.setVisibility(View.GONE);
        layout_torta.setVisibility(View.VISIBLE);
    }
    public void IrABarra(View view){
        layout_torta.setVisibility(View.GONE);
        layout_barras.setVisibility(View.VISIBLE);
        BMail.setVisibility(View.VISIBLE);
    }
    public void IrAMenuS1(View view){
        Intent i = new Intent(this, Seguimiento.class);
        startActivity(i);
    }
    public void btn_email(View view){
       GraficoLayouts();
       if (pieChart2.getVisibility() == View.VISIBLE) {
           GuardarGrafico(pieChart2, "% " + Clave + Clave2, false);
       }
       GuardarGrafico(pieChart1, "% de fatiga" + Clave + Clave2, true);
       PDFdatos();
       SendMail(fileB,fileP1,fileP2, uri);
    }
    //----------------------------------------------------------------------------------------------
    // GRAFICO -------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void GraficoBarras(List<String> LFecha, List<Double> LTFatiga, List<Float> LPeso, @Nullable List<String> LMusculo, @Nullable List<String> LEjercicio) {
        //DECLARACIONES COMUNES
        ArrayList barEntriesArrayList = new ArrayList<>();
        BarDataSet DataSetF;
        ArrayList<BarEntry> fatigaEntries = new ArrayList<>();
        //OPCION 1: MUSCULO y EJERCICIO UNICO ------------------------------------------------------
        if ((LMusculo == null) && (LEjercicio == null)) {
            int ColorF = Color.parseColor("#52AEBB");
            for (int i = 0; i < LFecha.size(); i++) {
                barEntriesArrayList.add(new BarEntry(i,LTFatiga.get(i).floatValue()));
            }
            for (int i = 0; i < LTFatiga.size(); i++) {
                fatigaEntries.add(new BarEntry(i, LTFatiga.get(i).floatValue()));
            }
            DataSetF = new BarDataSet(fatigaEntries, "Tiempo de aparición de la fatiga (seg)");
            DataSetF.setValueTextSize(12f);  // Ajusta el tamaño según tus necesidades
            DataSetF.setValueTextColor(Color.WHITE);
            DataSetF.setColor(ColorF);
        } else if ((LMusculo == null) && (LEjercicio != null)){
            //OPCION de TODOS los ejercicios
            List<Integer> ColoresF = new ArrayList<>();
            for (int i = 0; i < LFatiga.size(); i++) {
                // EJERCICIO PARA LOS COLORES DE LA BARRA ----------------------------------------------
                int ColorD, ColorF;
                String Ejercicio = LEjercicio.get(i);
                if (Ejercicio.equals("Cíclico con carga")) {
                    ColorF = Color.parseColor("#0BB3A8");
                } else if (Ejercicio.equals("Cíclico sin carga")) {
                    ColorF = Color.parseColor("#FFC55F");
                } else {
                    ColorF = Color.parseColor("#FFB2C5");
                }
                ColoresF.add(ColorF);
                fatigaEntries.add(new BarEntry(i, LTFatiga.get(i).floatValue()));
            }
            DataSetF = new BarDataSet(fatigaEntries, "Tiempo de aparición de la fatiga (seg)");
            DataSetF.setValueTextSize(12f);
            DataSetF.setValueTextColor(Color.WHITE);
            DataSetF.setColors(ColoresF);
        } else if (((LEjercicio == null) && (LMusculo != null)) ||((LEjercicio != null) && (LMusculo != null))) {
            //OPCION de TODOS LOS MUSCULOS
            List<Integer> ColoresF = new ArrayList<>();
            for (int i = 0; i < LFatiga.size(); i++) {
                // EJERCICIO PARA LOS COLORES DE LA BARRA ----------------------------------------------
                int ColorF;
                String Musculo = LMusculo.get(i);
                if (Musculo.equals("Abdomen ")) {
                    ColorF = Color.parseColor("#28AC00");
                } else if (Musculo.equals("Antebrazo")) {
                    ColorF = Color.parseColor("#0087EE");
                } else if (Musculo.equals("Brazo anterior ")) {
                    ColorF = Color.parseColor("#DA06AC");
                } else if (Musculo.equals("Brazo posterior ")) {
                    ColorF = Color.parseColor("#7521BE");
                } else if (Musculo.equals("Espalda alta ")) {
                    ColorF = Color.parseColor("#04909D");
                } else if (Musculo.equals("Espalda baja ")) {
                    ColorF = Color.parseColor("#76EEEB");
                } else if (Musculo.equals("Espalda lateral ")) {
                    ColorF = Color.parseColor("#008095");
                } else if (Musculo.equals("Hombros")) {
                    ColorF = Color.parseColor("#D08699");
                } else if (Musculo.equals("Muslo anterior ")) {
                    ColorF = Color.parseColor("#F7CE00");
                } else if (Musculo.equals("Muslo posterior ")) {
                    ColorF = Color.parseColor("#FF8202");
                } else if (Musculo.equals("Pecho ")) {
                    ColorF = Color.parseColor("#E0201B");
                } else {
                    ColorF = Color.parseColor("#FFAD49");
                }
                ColoresF.add(ColorF);
                // BUSCO LOS DATOS de la DURACION y TIEMPO de FATIGA -----------------------------------
                fatigaEntries.add(new BarEntry(i, LTFatiga.get(i).floatValue()));
            }
            // CONFIGURACIÓN DE LAS BARRAS -------------------------------------------------------------
            DataSetF = new BarDataSet(fatigaEntries, "Tiempo de aparición de la fatiga (seg)");
            DataSetF.setValueTextSize(12f);
            DataSetF.setValueTextColor(Color.WHITE);
            DataSetF.setColors(ColoresF);
        }
        else {
            int ColorF = Color.parseColor("#52AEBB");
            for (int i = 0; i < LFecha.size(); i++) {
                barEntriesArrayList.add(new BarEntry(i,LTFatiga.get(i).floatValue()));
            }
            for (int i = 0; i < LTFatiga.size(); i++) {
                fatigaEntries.add(new BarEntry(i, LTFatiga.get(i).floatValue()));
            }
            DataSetF = new BarDataSet(fatigaEntries, "Tiempo de aparición de la fatiga (seg)");
            DataSetF.setValueTextSize(12f);  // Ajusta el tamaño según tus necesidades
            DataSetF.setValueTextColor(Color.WHITE);
            DataSetF.setColor(ColorF);
        }
        //AGREGA LOS CONJUNTOS DE DATOS AL GRAFICO -------------------------------------------------
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(DataSetF);
        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
        //CONFIGURACIÓN DE LOS EJES ----------------------------------------------------------------
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(LFecha));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-30);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisMaximum(LFecha.size());
        barChart.setVisibleXRangeMaximum(8);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(12f);
        leftAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(60f);
        barChart.getAxisRight().setDrawGridLines(false);
        //MAS CONFIGURACIONES ----------------------------------------------------------------------
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.animateY(1500);
        barChart.moveViewToX(0);
        barChart.invalidate();
        //CONFIGURACION: SELECCIONA LA BARRA y APARECE el PESO UTILIZADO ---------------------------
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX(); // Obtener el índice de la barra seleccionada
                if (index >= 0 && index < LPeso.size()) {
                    float pesoSeleccionado = LPeso.get(index);
                    Toast.makeText(getApplicationContext(), "Peso utilizado (kg): " + pesoSeleccionado, Toast.LENGTH_SHORT).show();
                    if ((LMusculo != null) && (LEjercicio != null)) {
                        String ejercicioSeleccionado = LEjercicio.get(index);
                        String MusculoSelec = LMusculo.get(index);
                        Toast.makeText(getApplicationContext(), "Ejercicio: " + ejercicioSeleccionado, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Parte corporal: " + MusculoSelec, Toast.LENGTH_SHORT).show();
                    }
                    if ((LMusculo != null) && (LEjercicio == null)) {
                        String MusculoSelec = LMusculo.get(index);
                        Toast.makeText(getApplicationContext(), "Parte corporal: " + MusculoSelec, Toast.LENGTH_SHORT).show();
                    }
                    if ((LMusculo == null) && (LEjercicio != null)) {
                        String ejercicioSeleccionado = LEjercicio.get(index);
                        Toast.makeText(getApplicationContext(), "Ejercicio: " + ejercicioSeleccionado, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onNothingSelected() {
                // No hacer nada cuando no se selecciona ninguna barra
            }
        });
    }
    private void GraficoTorta(List<String> fatigaLista) {
        PieChart mChart = findViewById(R.id.pieChart1);
        int ColorSi = Color.parseColor("#D7CFA9");
        int ColorNo = Color.parseColor("#D78067");
        int ColorPr = Color.parseColor("#714B51");
        //CALCULO de CADA UNO ----------------------------------------------------------------------
        int countSi = 0;
        int countNo = 0;
        int countPr = 0;
        for (String valor : fatigaLista) {
            if (valor.equals("Si")) {
                countSi++;
            } else if (valor.equals("No")) {
                countNo++;
            } else if (valor.equals("Es probable")) {
                countPr++;
            }
        }
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(countSi, "Presento fatiga muscular"));
        pieEntries.add(new PieEntry(countNo, "No presento fatiga muscular"));
        pieEntries.add(new PieEntry(countPr, "Pudo haber presentado fatiga muscular"));
        //VALOR PARA EL PDF ------------------------------------------------------------------------
        PorcentajeF = ((float) (countSi + countPr) / (countSi + countNo + countPr)) * 100;
        // CONFIGURA el GRAFICO---------------------------------------------------------------------
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(15);
        mChart.setHoleColor(Color.BLACK); // Color del agujero
        mChart.setTransparentCircleRadius(5);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        // CONFIGURACION NUMEROS QUE APARECEN EN EL GRÁFICO ----------------------------------------
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Porcentaje de presencia de fatiga muscular");
        int[] colors = new int[]{ColorSi, ColorNo, ColorPr};
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentFormatter(mChart));
        PieData pieData = new PieData(pieDataSet);
        for (PieEntry entry : pieEntries) {
            if (entry.getValue() > 0) {
                entry.setLabel(entry.getLabel() + " " + entry.getValue() + "%");
            } else {
                entry.setLabel(""); // Dejar el texto vacío si el valor es cero
            }
        }
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setSelectionShift(5f);
        // CONFIGURACION de la LEYENDA -------------------------------------------------------------
        Legend legend = mChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE); // Color del texto de la leyenda
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setDrawInside(false); // No dibujar dentro del gráfico
        mChart.setDrawSliceText(false); // Ocultar el texto dentro de las rebanadas
        List<LegendEntry> entries = new ArrayList<>();
        entries.add(new LegendEntry("Presento fatiga muscular", Legend.LegendForm.SQUARE, 10f, 2f, null,ColorSi));
        entries.add(new LegendEntry("No presento fatiga muscular", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorNo));
        entries.add(new LegendEntry("Pudo haber presentado fatiga", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorPr));
        legend.setCustom(entries);
        mChart.setData(pieData);
        mChart.invalidate();
    }
    private void GraficoTortaMT(List<String> musculoLista) {
        PieChart mChart = findViewById(R.id.pieChart2);
        int abdomen = 0;
        int antebrazo = 0;
        int brazo_ant = 0;
        int brazo_pos = 0;
        int espalda_alta = 0;
        int espalda_baja = 0;
        int espalda_lat = 0;
        int hombros = 0;
        int muslo_ant = 0;
        int muslo_pos = 0;
        int pecho = 0;
        int pierna = 0;
        int Color1 = Color.parseColor("#28AC00");
        int Color2 = Color.parseColor("#0087EE");
        int Color3 = Color.parseColor("#DA06AC");
        int Color4 = Color.parseColor("#7521BE");
        int Color5 = Color.parseColor("#04909D");
        int Color6 = Color.parseColor("#76EEEB");
        int Color7 = Color.parseColor("#008095");
        int Color8 = Color.parseColor("#D08699");
        int Color9 = Color.parseColor("#F7CE00");
        int Color10 = Color.parseColor("#FF8202");
        int Color11 = Color.parseColor("#E0201B");
        int Color12 = Color.parseColor("#FFAD49");
        for (String valor : musculoLista) {
            if (valor.equals("Abdomen ")) {abdomen++;
            } else if (valor.equals("Antebrazo")) {
                antebrazo++;
            } else if (valor.equals("Brazo anterior ")) {
                brazo_ant++;
            } else if (valor.equals("Brazo posterior ")){
                brazo_pos++;
            } else if (valor.equals("Espalda alta ")){
                espalda_alta++;
            } else if (valor.equals("Espalda baja ")) {
                espalda_baja++;
            } else if (valor.equals("Espalda lateral ")) {
                espalda_lat++;
            } else if (valor.equals("Hombros")) {
                hombros++;
            } else if (valor.equals("Muslo anterior ")) {
                muslo_ant++;
            } else if (valor.equals("Muslo posterior ")) {
                muslo_pos++;
            } else if (valor.equals("Pecho ")) {
                pecho++;
            } else if (valor.equals("Pierna ")) {
                pierna++;
            }
        }
        List<PieEntry> pieEntries = new ArrayList<>();
        if (abdomen > 0) pieEntries.add(new PieEntry(abdomen, "Abdomen"));
        if (antebrazo > 0) pieEntries.add(new PieEntry(antebrazo, "Antebrazo"));
        if (brazo_ant > 0) pieEntries.add(new PieEntry(brazo_ant, "Brazo anterior"));
        if (brazo_pos > 0) pieEntries.add(new PieEntry(brazo_pos, "Brazo posterior"));
        if (espalda_alta > 0) pieEntries.add(new PieEntry(espalda_alta, "Espalda alta"));
        if (espalda_baja > 0) pieEntries.add(new PieEntry(espalda_baja, "Espalda baja"));
        if (espalda_lat > 0) pieEntries.add(new PieEntry(espalda_lat, "Espalda lateral"));
        if (hombros > 0) pieEntries.add(new PieEntry(hombros, "Hombros"));
        if (muslo_ant > 0) pieEntries.add(new PieEntry(muslo_ant, "Muslo anterior"));
        if (muslo_pos > 0) pieEntries.add(new PieEntry(muslo_pos, "Muslo posterior"));
        if (pecho > 0) pieEntries.add(new PieEntry(pecho, "Pecho"));
        if (pierna > 0) pieEntries.add(new PieEntry(pierna, "Pierna"));
        mChart.setUsePercentValues(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(15);
        mChart.setHoleColor(Color.BLACK); // Color del agujero
        mChart.setTransparentCircleRadius(5);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Tipos de ejercicio realizados");
        int[] colors = new int[]{Color1, Color2, Color3,Color4, Color5, Color6, Color7, Color8, Color9, Color10, Color11, Color12};
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentFormatter(mChart));
        PieData pieData = new PieData(pieDataSet);
        for (PieEntry entry : pieEntries) {
            if (entry.getValue() > 0) {
                entry.setLabel(entry.getLabel() + " " + entry.getValue() + "%");
            } else {
                entry.setLabel("");
            }
        }
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setSelectionShift(5f);
        Legend legend = mChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE); // Color del texto de la leyenda
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setDrawInside(false); // No dibujar dentro del gráfico
        mChart.setDrawSliceText(false); // Ocultar el texto dentro de las rebanadas
        List<LegendEntry> entries = new ArrayList<>();
        entries.add(new LegendEntry("Abdomen", Legend.LegendForm.SQUARE, 10f, 2f, null,Color1));
        entries.add(new LegendEntry("Antebrazo", Legend.LegendForm.SQUARE, 10f, 2f, null, Color2));
        entries.add(new LegendEntry("Brazo anterior", Legend.LegendForm.SQUARE, 10f, 2f, null, Color3));
        entries.add(new LegendEntry("Brazo posterior", Legend.LegendForm.SQUARE, 10f, 2f, null, Color4));
        entries.add(new LegendEntry("Espalda alta", Legend.LegendForm.SQUARE, 10f, 2f, null, Color5));
        entries.add(new LegendEntry("Espalda baja", Legend.LegendForm.SQUARE, 10f, 2f, null, Color6));
        entries.add(new LegendEntry("Espalda lateral", Legend.LegendForm.SQUARE, 10f, 2f, null, Color7));
        entries.add(new LegendEntry("Hombros", Legend.LegendForm.SQUARE, 10f, 2f, null, Color8));
        entries.add(new LegendEntry("Muslo anterior", Legend.LegendForm.SQUARE, 10f, 2f, null, Color9));
        entries.add(new LegendEntry("Muslo posterior", Legend.LegendForm.SQUARE, 10f, 2f, null, Color10));
        entries.add(new LegendEntry("Pecho", Legend.LegendForm.SQUARE, 10f, 2f, null, Color11));
        entries.add(new LegendEntry("Pierna", Legend.LegendForm.SQUARE, 10f, 2f, null, Color12));
        legend.setCustom(entries);
        mChart.setData(pieData);
        mChart.invalidate();
    }
    private void GraficoTortaET(List<String> LEjercicio) {
        int C1 = 0;
        int C2 = 0;
        int C3 = 0;
        int Color1 = Color.parseColor("#0BB3A8");
        int Color2 = Color.parseColor("#FFC55F");
        int Color3 = Color.parseColor("#FFB2C5");
        for (int i = 0; i < LEjercicio.size(); i++) {
            String valor = LEjercicio.get(i);
            if (valor.equals("Cíclico con carga")) {
                C1++;
            } else if (valor.equals("Cíclico sin carga")) {
                C2++;
            } else if (valor.equals("Estático")) {
                C3++;
            }
        }
        List<PieEntry> pieEntries = new ArrayList<>();
        if (C1 > 0) pieEntries.add(new PieEntry(C1, "Cíclico con carga"));
        if (C2 > 0) pieEntries.add(new PieEntry(C2, "Cíclico sin carga"));
        if (C3 > 0) pieEntries.add(new PieEntry(C3, "Estático"));;
        pieChart2.setUsePercentValues(true);
        pieChart2.setDrawHoleEnabled(true);
        pieChart2.setHoleRadius(15);
        pieChart2.setHoleColor(Color.BLACK); // Color del agujero
        pieChart2.setTransparentCircleRadius(5);
        pieChart2.setRotationAngle(0);
        pieChart2.setRotationEnabled(true);
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Tipos de ejercicio realizados");
        int[] colors = new int[]{Color1, Color2, Color3};
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart2));
        PieData pieData = new PieData(pieDataSet);
        for (PieEntry entry : pieEntries) {
            if (entry.getValue() > 0) {
                entry.setLabel(entry.getLabel() + " " + entry.getValue() + "%");
            } else {
                entry.setLabel("");
            }
        }
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setSelectionShift(5f);
        Legend legend = pieChart2.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE); // Color del texto de la leyenda
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setDrawInside(false); // No dibujar dentro del gráfico
        pieChart2.setDrawSliceText(false); // Ocultar el texto dentro de las rebanadas
        List<LegendEntry> entries = new ArrayList<>();
        entries.add(new LegendEntry("Cíclico con carga", Legend.LegendForm.SQUARE, 10f, 2f, null,Color1));
        entries.add(new LegendEntry("Cíclico sin carga", Legend.LegendForm.SQUARE, 10f, 2f, null, Color2));
        entries.add(new LegendEntry("Estático", Legend.LegendForm.SQUARE, 10f, 2f, null, Color3));
        legend.setCustom(entries);
        pieChart2.setData(pieData);
        pieChart2.invalidate();
    }
    //----------------------------------------------------------------------------------------------
    // FUNCION EMAIL--------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void GraficoLayouts() {
        layout_barras.setVisibility(View.VISIBLE);
        layout_barras.setDrawingCacheEnabled(true);
        layout_barras.buildDrawingCache();
        Bitmap bitmapB = Bitmap.createBitmap(layout_barras.getWidth(), layout_barras.getHeight(), Bitmap.Config.ARGB_8888);
        layout_barras.draw(new Canvas(bitmapB));
        String Nombre1 = "Progreso por sesiones " + Clave + Clave2;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        fileB = new File(path, Nombre1 + ".png");
        try {
            FileOutputStream outputStreamB = new FileOutputStream(fileB);
            bitmapB.compress(Bitmap.CompressFormat.PNG, 100, outputStreamB);
            outputStreamB.flush();
            outputStreamB.close();
            MediaScannerConnection.scanFile(this,
                    new String[]{fileB.getAbsolutePath()},
                    new String[]{"image/png"},
                    null);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el gráfico", Toast.LENGTH_SHORT).show();
        }
    }
    private void GuardarGrafico(PieChart chart, String fileName, Boolean b) {
        if (chart != null) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, fileName + ".png"); // Agregar el nombre del archivo
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                Bitmap chartBitmap = chart.getChartBitmap();
                chartBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                // Notificar al sistema sobre el nuevo archivo
                MediaScannerConnection.scanFile(this,
                        new String[]{file.getAbsolutePath()},
                        new String[]{"image/png"},
                        null);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar el gráfico" + fileName, Toast.LENGTH_SHORT).show();
            }
            if (b){
                fileP1 = file;
            } else {
                fileP2 = file;
            }
        } else {
            // Manejar el caso en que el gráfico sea nulo
            Toast.makeText(this, "El gráfico es nulo", Toast.LENGTH_SHORT).show();
        }
    }
    private void PDFdatos() {
        //CALCULO LA CANTIDAD DE PAGINAS NECESARIAS ------------------------------------------------
        int Filas = Tabla.size();
        int NPaginas = (int) Math.ceil((double) Filas / 7);
        for (int paginaActual = 1; paginaActual <= NPaginas; paginaActual++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, paginaActual).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            // PAGINAS con BORDE--------------------------------------------------------------------
            Paint bordePaginaPaint = new Paint();
            bordePaginaPaint.setColor(Color.BLACK);
            bordePaginaPaint.setStyle(Paint.Style.STROKE);
            bordePaginaPaint.setStrokeWidth(4);
            canvas.drawRect(28, 28, 814, 567, bordePaginaPaint);
            //ICONO ENZO ---------------------------------------------------------------------------
            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.log);
            float logoWidth = 60;
            float aspectRatio = (float) logoBitmap.getHeight() / (float) logoBitmap.getWidth();
            float logoHeight = logoWidth * aspectRatio;
            float logoLeft = 814 - logoWidth - 10;
            float logoTop = 567 - logoHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(logoWidth / logoBitmap.getWidth(), logoHeight / logoBitmap.getHeight());
            Bitmap resizedBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoBitmap.getWidth(), logoBitmap.getHeight(), matrix, true);
            canvas.drawBitmap(resizedBitmap, logoLeft, logoTop, null);
            //TITULO DEL PDF--------------------------------------------------------------------
            Paint titlePaint = new Paint();
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTextSize(36);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_bold);
            titlePaint.setTypeface(typeface);
            String titleText = "Seguimiento";
            float titleX = canvas.getWidth() / 2;
            canvas.drawText(titleText, titleX, 100, titlePaint);
            canvas.drawLine((titleX - titlePaint.measureText(titleText) / 2), 110, (titleX + titlePaint.measureText(titleText) / 2), 110, titlePaint);
            //SUBTITULO del PDF --------------------------------------------------------------------
            Paint subtitulo = new Paint();
            subtitulo.setColor(Color.BLACK);
            subtitulo.setTextSize(14);
            subtitulo.setTextAlign(Paint.Align.CENTER);
            String TextoSub;
            if (Musculo.equals("Todos") && Ejercicio.equals("Todos")) {
                //OPCION TODOS LOS MUSCULOS y TODOS los EJERCICIOS
                TextoSub = "Todas las sesiones con peso entre " + Peso1 + " - " + Peso2 + " kg";
            } else if (Musculo.equals("Todos") && !Ejercicio.equals("Todos")) {
                TextoSub = "Todos los músculos del " + LadoCorporal.trim().toLowerCase() + " con ejercicio " + Ejercicio.trim().toLowerCase() + " y pesos entre " + Peso1 + " - " + Peso2 + " kg";
            } else if (Ejercicio.equals("Todos") && !Musculo.equals("Todos")) {
                TextoSub = Musculo.trim() + " del " + LadoCorporal.trim().toLowerCase() + " con pesos entre " + Peso1 + " - " + Peso2 + " kg";
            } else {
                TextoSub = Musculo.trim() + " del " + LadoCorporal.trim().toLowerCase() + " con ejercicio " + Ejercicio.trim().toLowerCase() + " y pesos entre " + Peso1 + " - " + Peso2 + " kg";
            }
            canvas.drawText(TextoSub, canvas.getWidth() / 2, 140, subtitulo);
            //CONFIGURACION de la TABLA (BORDE y LETRA)---------------------------------------------
            float textoSize;
            if (Tabla.get(0).size() == 7) {
                textoSize = 10;
            } else {
                textoSize = 14; // Ajusta según tus necesidades
            }
            Paint textoPaintTitulo = new Paint();
            textoPaintTitulo.setColor(Color.BLACK);
            textoPaintTitulo.setTextSize(textoSize);
            textoPaintTitulo.setTypeface(typeface);
            textoPaintTitulo.setTextAlign(Paint.Align.CENTER);
            Paint textoPaint = new Paint();
            textoPaint.setColor(Color.BLACK);
            textoPaint.setTextSize(textoSize);
            textoPaint.setTextAlign(Paint.Align.LEFT);
            Paint bordePaint = new Paint();
            bordePaint.setColor(Color.BLACK);
            bordePaint.setStyle(Paint.Style.STROKE);
            bordePaint.setStrokeWidth(2); // Ancho del borde
            //CONFIGURACIÓN DE LA TABLA ENCABEZADO -------------------------------------------------
            float anchoColumna = 730f / Tabla.get(0).size();
            float tablaY = 160;
            if (paginaActual < (NPaginas)) {
                for (int k=0; k<8 && k<Tabla.size(); k++){
                    for (int j = 0; j < Tabla.get(0).size(); j++) {
                        float columnaX = j * anchoColumna + 56; // Inicio en x=56
                        float columnaWidth = anchoColumna;
                        canvas.drawRect(columnaX, tablaY, columnaX + columnaWidth, tablaY + 40, bordePaint);
                        float textoX = columnaX + (columnaWidth / 2) - (textoPaint.measureText(Tabla.get(0).get(j)) / 2);
                        float textoY = tablaY + 40 / 2 + 5;
                        int indiceFila;
                        if (k==0){
                            canvas.drawText(Tabla.get(0).get(j), textoX, textoY, textoPaint);
                        } else {
                            indiceFila = k + 7 * (paginaActual-1);
                            canvas.drawText(Tabla.get(indiceFila).get(j), columnaX + 10, textoY, textoPaint);
                        }
                    }
                    tablaY += 40;
                }
            } else {
                int auxiliar = (int) Tabla.size() - 1 - 7 * (NPaginas - 1);
                for (int k = 0; k < auxiliar; k++) {
                    for (int j = 0; j < Tabla.get(0).size(); j++) {
                        float columnaX = j * anchoColumna + 56; // Inicio en x=56
                        float columnaWidth = anchoColumna;
                        canvas.drawRect(columnaX, tablaY, columnaX + columnaWidth, tablaY + 40, bordePaint);
                        float textoX = columnaX + (columnaWidth / 2) - (textoPaint.measureText(Tabla.get(0).get(j)) / 2);
                        float textoY = tablaY + 40 / 2 + 5;
                        int indiceFila;
                        if (k == 0) {
                            canvas.drawText(Tabla.get(0).get(j), textoX, textoY, textoPaint);
                        } else {
                            indiceFila = k + 7 * (paginaActual - 1);
                            canvas.drawText(Tabla.get(indiceFila).get(j), columnaX + 10, textoY, textoPaint);
                        }
                    }
                    tablaY += 40;
                }
            }
            //FINALIZA LA PAGINA -------------------------------------------------------------------
            document.finishPage(page);
        }
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        String fileName = "Seguimiento " + Clave2 + ".pdf";
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        uri = resolver.insert(contentUri, contentValues);
        try {
            OutputStream outputStream = resolver.openOutputStream(uri);
            document.writeTo(outputStream);
            outputStream.close();
            // Escanear el archivo para que aparezca en la galería o en otras aplicaciones
            MediaScannerConnection.scanFile(this,
                    new String[]{uri.toString()},
                    new String[]{"application/pdf"},
                    null);
        } catch (IOException e) {
        }
    }
    private void SendMail(File attachment, File attachment2, File attachment3,Uri pdfAttachment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese la dirección de correo electrónico");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    emailsend = input.getText().toString();
                    // ENVIO DIRECTO
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            final String senderEmail = "xxxx@gmail.com";
                            final String senderPassword = "contraseña aca";
                            Properties props = new Properties();
                            props.put("mail.smtp.host", "smtp.gmail.com");
                            props.put("mail.smtp.port", "587");
                            props.put("mail.smtp.auth", "true");
                            props.put("mail.smtp.starttls.enable", "true");
                            Session session = Session.getInstance(props,
                                    new Authenticator() {
                                        protected PasswordAuthentication getPasswordAuthentication() {
                                            return new PasswordAuthentication(senderEmail, senderPassword);
                                        }
                                    });
                            try {
                                Message message = new MimeMessage(session);
                                message.setFrom(new InternetAddress(senderEmail));
                                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailsend));
                                message.setSubject("Seguimiento ENZOF");
                                MimeBodyPart messageBodyPart = new MimeBodyPart();
                                messageBodyPart.setText("En el siguiente mail se encuentra adjunto: \n " +
                                        "- Tabla con información acerca de las sesiones seleccionadas\n" +
                                        "- Gráfico con el porcentaje de presencia de fatiga en las sesiones seleccionadas\n" +
                                        "- Gráfico de barras con información acerca del tiempo de aparición de la fatiga a lo largo del tiempo");
                                MimeBodyPart attachmentPart1 = new MimeBodyPart();
                                DataSource source1 = new FileDataSource(attachment);
                                attachmentPart1.setDataHandler(new DataHandler(source1));
                                attachmentPart1.setFileName(attachment.getName());
                                MimeBodyPart attachmentPart2 = new MimeBodyPart();
                                DataSource source2 = new FileDataSource(attachment2);
                                attachmentPart2.setDataHandler(new DataHandler(source2));
                                attachmentPart2.setFileName(attachment2.getName());
                                Multipart multipart = new MimeMultipart();
                                multipart.addBodyPart(messageBodyPart);
                                multipart.addBodyPart(attachmentPart1);
                                multipart.addBodyPart(attachmentPart2);
                                if (attachment3 != null) {
                                    MimeBodyPart attachmentPart3 = new MimeBodyPart();
                                    DataSource source3 = new FileDataSource(attachment3);
                                    attachmentPart3.setDataHandler(new DataHandler(source3));
                                    attachmentPart3.setFileName(attachment3.getName());
                                    multipart.addBodyPart(attachmentPart3);

                                }
                                //parte del PDF
                                String filePath;
                                if ("content".equals(pdfAttachment.getScheme())) {
                                    Cursor cursor = getContentResolver().query(pdfAttachment, null, null, null, null);
                                    if (cursor != null) {
                                        cursor.moveToFirst();
                                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                                        filePath = cursor.getString(index);
                                        cursor.close();
                                    } else {
                                        filePath = pdfAttachment.getPath();
                                    }
                                } else {
                                    filePath = pdfAttachment.getPath();
                                }
                                File pdfFile = new File(filePath);
                                MimeBodyPart pdfAttachmentPart = new MimeBodyPart();
                                DataSource pdfSource = new FileDataSource(pdfFile);
                                pdfAttachmentPart.setDataHandler(new DataHandler(pdfSource));
                                pdfAttachmentPart.setFileName(pdfFile.getName());
                                multipart.addBodyPart(pdfAttachmentPart);
                                // Establecer el contenido del mensaje
                                message.setContent(multipart);
                                // Enviar correo electrónico
                                Transport.send(message);
                                return true;
                            } catch (MessagingException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        @Override
                        protected void onPostExecute(Boolean success) {
                            if (success) {
                                Toast.makeText(getApplicationContext(), "Correo electrónico enviado correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al enviar el correo electrónico", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}