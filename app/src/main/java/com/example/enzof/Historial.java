package com.example.enzof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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


public class Historial extends AppCompatActivity {
    // INFORMACIÓN DE LA SESIÓN --------------------------------------------------------------------
    private Spinner spinnerF;
    private TextView txtmusculo;
    private TextView txtlado;
    private TextView txtejercicio;
    private TextView txtpeso;
    private TextView txtfatigado;
    private TextView txttiempof;
    // LAYOUTS -------------------------------------------------------------------------------------
    private LinearLayout layout_info;
    private LinearLayout layout_fatiga;
    private LinearLayout layout_infografico;
    private LinearLayout layout_negro;
    private ImageButton BotonAtrasH;
    private ImageButton BotonAdelanteH;
    // DATO GRAFICO --------------------------------------------------------------------------------
    private LineChart mChart;
    private LineChart mChartMF;
    private float horizontalRMS;
    private float horizontalMF;
    private ArrayList<Double> puntos;
    private ArrayList<Entry> entriesRMS;
    private ArrayList<Entry> entriesMF;
    // BASSE de DATOS ------------------------------------------------------------------------------
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseAuth auth;
    private String FechaSeleccionada;
    // ARCHIVO PDF ---------------------------------------------------------------------------------
    private Uri uri;
    private String emailsend;
    private PdfDocument document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        // DECLARACIÓN DE LA BASE de DATOS ---------------------------------------------------------
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("RegistrosEMG");
        myRef.keepSynced(false);
        auth = FirebaseAuth.getInstance();
        // DECLARACIÓN DE COSITAS DEL XML ----------------------------------------------------------
        spinnerF = findViewById(R.id.lista_fechas);
        txtmusculo = findViewById(R.id.musculo_medido);
        txtlado = findViewById(R.id.lado_corporal);
        txtejercicio = findViewById(R.id.tipoejercicio);
        txtpeso = findViewById(R.id.peso_seleccionado);
        txtfatigado = findViewById(R.id.si_no_fatiga);
        txttiempof = findViewById(R.id.tiempo_fatigado);
        mChart = findViewById(R.id.grafico);
        mChartMF = findViewById(R.id.graficoMF);
        layout_fatiga = findViewById(R.id.xml2_fatiga);
        layout_info = findViewById(R.id.xml1_info);
        layout_negro = findViewById(R.id.xml_negro);
        layout_infografico = findViewById(R.id.xml3_info);
        BotonAdelanteH = findViewById(R.id.BAdelanteXML);
        BotonAtrasH = findViewById(R.id.BAtrasXML);
        // CONFIGURACIÓN DE SI VIENE DE MENU o DE REGISTRO -----------------------------------------
        Intent intent = getIntent();
        FechaSeleccionada = intent.getStringExtra("FechaSeleccionada");
        boolean desdeRegistro = intent.getBooleanExtra("desdeRegistro", false);
        // FECHAS del FIREBASE ---------------------------------------------------------------------
        myRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> fechasList = new ArrayList<>();
                fechasList.add("Seleccione una fecha");
                for (DataSnapshot fechaSnapshot : dataSnapshot.getChildren()) {
                    String fecha = fechaSnapshot.getKey();
                    // Filtrar nodos que no comienzan con "p"
                    if (!fecha.startsWith("p")) {
                        fechasList.add(fecha);
                    }
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Historial.this,
                        android.R.layout.simple_spinner_item, fechasList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerF.setAdapter(spinnerAdapter);

                if (desdeRegistro) {
                    spinnerF.setSelection(spinnerAdapter.getCount() - 1); // Establecer la última opción
                    spinnerF.setEnabled(false); // Deshabilitar el spinner
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error aquí
                Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        spinnerF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedFecha = adapterView.getItemAtPosition(position).toString();
                FechaSeleccionada = selectedFecha;
                // LAYOUTS -------------------------------------------------------------------------
                if (position != 0){
                    layout_negro.setVisibility(View.GONE);
                    layout_info.setVisibility(View.VISIBLE);
                    layout_fatiga.setVisibility(View.GONE);
                    layout_infografico.setVisibility(View.GONE);
                    BotonAdelanteH.setVisibility(View.VISIBLE);
                    BotonAtrasH.setVisibility(View.INVISIBLE);
                } else {
                    layout_info.setVisibility(View.GONE);
                    layout_infografico.setVisibility(View.GONE);
                    layout_fatiga.setVisibility(View.GONE);
                    layout_negro.setVisibility(View.VISIBLE);
                }
                //RECUPERA EL MUSCULO MEDIDO -------------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("musculo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.getValue(String.class);
                        txtmusculo.setText(data);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                //RECUPERA EL PESO SELECCIONADO ----------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("peso").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Float data = dataSnapshot.getValue(Float.class);
                        txtpeso.setText(String.valueOf(data));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                //RECUPERA SI SE FATIGA O NO -------------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("presento").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.getValue(String.class);
                        txtfatigado.setText(data);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                //RECUPERA LADO CORPORAL------------------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("ladocorporal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.getValue(String.class);
                        txtlado.setText(data);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                // RECUPERA TIPO de EJERCICIO ------------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("tipoejercicio").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String data = dataSnapshot.getValue(String.class);
                        txtejercicio.setText(data);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                // RECUPERA EL TIEMPO de FATIGA ----------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("tiempof").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Double data = dataSnapshot.getValue(Double.class);
                        txttiempof.setText(String.valueOf(data));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                // RECUPERA LAS LINEAS HORIZONTALES ------------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("linea_regresion").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Double data = dataSnapshot.getValue(Double.class);
                        if (data != null) {
                            horizontalRMS = data.floatValue();
                        } else {
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("linea_regresionMF").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Double data = dataSnapshot.getValue(Double.class);
                        if (data != null) {
                            horizontalMF = data.floatValue();
                        } else {
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                // RECUPERA LOS PUNTOS DEL RMS y DEL MF --------------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("rms").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Double> puntosRMSlist = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Double>>() {});
                        if (puntosRMSlist != null) {
                            entriesRMS = new ArrayList<>();
                            for (int i = 0; i < puntosRMSlist.size(); i++) {
                                entriesRMS.add(new Entry(i, puntosRMSlist.get(i).floatValue()));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("mf").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Double> puntosMFList = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<Double>>() {});
                        if (puntosMFList != null) {
                            entriesMF = new ArrayList<>();
                            for (int i = 0; i < puntosMFList.size(); i++) {
                                entriesMF.add(new Entry(i, puntosMFList.get(i).floatValue()));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                //RECUPERA EL VECTOR DE PUNTOS // GRAFICOS -----------------------------------------
                myRef.child(auth.getCurrentUser().getUid()).child(selectedFecha).child("vector").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            puntos = (ArrayList<Double>) dataSnapshot.getValue();
                            if (puntos != null) {
                                ArrayList<Entry> entries_RL_RMS = new ArrayList<>();
                                ArrayList<Entry> entries_RL_MF = new ArrayList<>();
                                entries_RL_RMS.add(new Entry(0, puntos.get(0).floatValue()));
                                entries_RL_RMS.add(new Entry(entriesRMS.size(), puntos.get(1).floatValue()));
                                entries_RL_MF.add(new Entry(0, puntos.get(2).floatValue()));
                                entries_RL_MF.add(new Entry(entriesMF.size(), puntos.get(3).floatValue()));
                                // GRAFICO RMS -----------------------------------------------------
                                LineDataSet dataSet_RL_RMS = new LineDataSet(entries_RL_RMS, "RMS regresión lineal");
                                dataSet_RL_RMS.setMode(LineDataSet.Mode.LINEAR);
                                dataSet_RL_RMS.setColor(Color.parseColor("#88D8B0"));
                                dataSet_RL_RMS.setCircleColor(Color.parseColor("#88D8B0"));
                                dataSet_RL_RMS.setDrawCircleHole(false);
                                dataSet_RL_RMS.setDrawValues(false);
                                dataSet_RL_RMS.setLineWidth(4f);
                                ArrayList<Entry> horizontalEntries_RMS = new ArrayList<>();
                                horizontalEntries_RMS.add(new Entry(0, horizontalRMS));
                                horizontalEntries_RMS.add(new Entry(entriesRMS.size(), horizontalRMS));
                                LineDataSet horizontalDataSet_RMS = new LineDataSet(horizontalEntries_RMS, "Límite fatiga");
                                horizontalDataSet_RMS.enableDashedLine(10f, 5f, 0f);
                                horizontalDataSet_RMS.setColor(Color.parseColor("#F96E5A"));
                                horizontalDataSet_RMS.setDrawCircles(false);
                                horizontalDataSet_RMS.setDrawValues(false);
                                horizontalDataSet_RMS.setLineWidth(2f);
                                LineDataSet dataSet_RMS = new LineDataSet(entriesRMS, "RMS");
                                dataSet_RMS.setDrawCircles(true);
                                dataSet_RMS.setCircleRadius(4f);
                                dataSet_RMS.setCircleColor(Color.parseColor("#FED06A"));
                                dataSet_RMS.setColor(Color.parseColor("#FED06A"));
                                dataSet_RMS.setDrawValues(false);
                                dataSet_RMS.setDrawFilled(false);
                                dataSet_RMS.setLineWidth(0f);
                                LineData linedataRMS = new LineData(dataSet_RL_RMS, dataSet_RMS,horizontalDataSet_RMS);
                                mChart.setData(linedataRMS);
                                mChart.setBackgroundColor(Color.WHITE);
                                    // CONFIGURA EJES-----------------------------------------------
                                YAxis leftAxis = mChart.getAxisLeft();
                                YAxis yAxisLeft = mChart.getAxisLeft();
                                YAxis yAxisRight = mChart.getAxisRight();
                                yAxisLeft.setTextColor(Color.BLACK);
                                yAxisLeft.setDrawGridLines(false);
                                yAxisRight.setEnabled(false);
                                leftAxis.setDrawLabels(false);
                                XAxis xAxis = mChart.getXAxis();
                                xAxis.setDrawGridLines(false);
                                xAxis.setEnabled(false);
                                xAxis.setDrawAxisLine(true);
                                xAxis.setAxisLineColor(Color.BLACK);
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                Description description = mChart.getDescription();
                                description.setEnabled(false);
                                mChart.invalidate();
                                // GRAFICO MF ------------------------------------------------------
                                LineDataSet dataSet_RL_MF = new LineDataSet(entries_RL_MF, "MF regresión lineal");
                                dataSet_RL_MF.setMode(LineDataSet.Mode.LINEAR);
                                dataSet_RL_MF.setColor(Color.parseColor("#88D8B0"));
                                dataSet_RL_MF.setCircleColor(Color.parseColor("#88D8B0"));
                                dataSet_RL_MF.setDrawValues(false);
                                dataSet_RL_MF.setLineWidth(4f);
                                ArrayList<Entry> horizontalEntries_MF = new ArrayList<>();
                                horizontalEntries_MF.add(new Entry(0, horizontalMF));
                                horizontalEntries_MF.add(new Entry(entriesMF.size(), horizontalMF));
                                LineDataSet horizontalDataSet_MF = new LineDataSet(horizontalEntries_MF, "Límite fatiga");
                                horizontalDataSet_MF.enableDashedLine(10f, 5f, 0f);
                                horizontalDataSet_MF.setColor(Color.parseColor("#F96E5A"));
                                horizontalDataSet_MF.setDrawCircles(false);
                                horizontalDataSet_MF.setDrawValues(false);
                                horizontalDataSet_MF.setLineWidth(2f);
                                LineDataSet dataSet_MF = new LineDataSet(entriesMF, "MF");
                                dataSet_MF.setDrawCircleHole(true);
                                dataSet_MF.setCircleRadius(4f);
                                dataSet_MF.setCircleColor(Color.parseColor("#FED06A"));
                                dataSet_MF.setColor(Color.parseColor("#FED06A"));
                                dataSet_MF.setDrawValues(false);
                                dataSet_MF.setDrawFilled(false);
                                LineData linedataMF = new LineData(dataSet_RL_MF, dataSet_MF,horizontalDataSet_MF);
                                mChartMF.setData(linedataMF);
                                mChartMF.setBackgroundColor(Color.WHITE);
                                // CONFIGURA EJES-----------------------------------------------
                                YAxis leftAxisMF = mChartMF.getAxisLeft();
                                YAxis yAxisLeftMF = mChartMF.getAxisLeft();
                                YAxis yAxisRightMF = mChartMF.getAxisRight();
                                yAxisLeftMF.setTextColor(Color.BLACK);
                                yAxisLeftMF.setDrawGridLines(false);
                                yAxisRightMF.setEnabled(false);
                                leftAxisMF.setDrawLabels(false);
                                XAxis xAxisMF = mChartMF.getXAxis();
                                xAxisMF.setDrawGridLines(false);
                                xAxisMF.setEnabled(false);
                                xAxisMF.setDrawAxisLine(true);
                                xAxisMF.setAxisLineColor(Color.BLACK);
                                xAxisMF.setPosition(XAxis.XAxisPosition.BOTTOM);
                                Description descriptionMF = mChartMF.getDescription();
                                descriptionMF.setEnabled(false);
                                mChartMF.invalidate();
                            } else {
                                Toast.makeText(Historial.this, "La lista de puntos no tiene suficientes elementos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Historial.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                layout_fatiga.setVisibility(View.GONE);
                layout_info.setVisibility(View.GONE);
            }
        });
        document = new PdfDocument();
        BotonAdelanteH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layout_info.getVisibility() == View.VISIBLE) {
                    BotonAtrasH.setVisibility(View.VISIBLE);
                    layout_info.setVisibility(View.GONE);
                    layout_fatiga.setVisibility(View.VISIBLE);
                } else if (layout_fatiga.getVisibility() == View.VISIBLE){
                    BotonAdelanteH.setVisibility(View.INVISIBLE);
                    layout_fatiga.setVisibility(View.GONE);
                    layout_infografico.setVisibility(View.VISIBLE);
                }
            }
        });
        BotonAtrasH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layout_infografico.getVisibility() == View.VISIBLE) {
                    BotonAdelanteH.setVisibility(View.VISIBLE);
                    layout_infografico.setVisibility(View.GONE);
                    layout_fatiga.setVisibility(View.VISIBLE);
                } else if (layout_fatiga.getVisibility() == View.VISIBLE) {
                    BotonAtrasH.setVisibility(View.INVISIBLE);
                    layout_fatiga.setVisibility(View.GONE);
                    layout_info.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    // PDF TABLA CON DATOS DE LA SESION  -----------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void PDFdatos() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_historial,null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            this.getDisplay().getRealMetrics(displayMetrics);
        }
        else this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY));
        view.layout(0,0,displayMetrics.widthPixels,displayMetrics.heightPixels);
        // Tamaño de la hoja A4 (210mm x 297mm)
        int pageWidth = (int) (210 * displayMetrics.density);
        int pageHeight = (int) (297 * displayMetrics.density);
        // Margen de 2 cm convertido a píxeles
        float margin = 2 * displayMetrics.density;
        // Coordenadas para el área de contenido (excluyendo el margen)
        float contentLeft = margin;
        float contentTop = margin;
        float contentRight = pageWidth - margin;
        float contentBottom = pageHeight - margin;
        // Usa el document global en lugar de crear uno nuevo
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        // Añadir un borde gris alrededor del área de contenido (2 cm del margen)
        Paint bordePaginaPaint = new Paint();
        bordePaginaPaint.setColor(Color.BLACK);
        bordePaginaPaint.setStyle(Paint.Style.STROKE);
        bordePaginaPaint.setStrokeWidth(4); // Ancho del borde (ajústalo según tus necesidades)
        // Dibujar el borde alrededor del área de contenido
        canvas.drawRect(contentLeft, contentTop, contentRight, contentBottom, bordePaginaPaint);
        // Dibujar el logo en el centro del PDF con tamaño ajustable
        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.log); // Reemplaza R.drawable.tu_logo con el ID de tu logo
        float logoWidth = 100; // Ajusta el ancho del logo según tus necesidades
        // Calcular la proporción para ajustar el alto según el ancho
        float aspectRatio = (float) logoBitmap.getHeight() / (float) logoBitmap.getWidth();
        float logoHeight = logoWidth * aspectRatio;
        // Calcular las coordenadas para la esquina inferior derecha
        float logoLeft = pageWidth - margin - logoWidth;
        float logoTop = pageHeight - margin - logoHeight;
        // Configurar una matriz de transformación para ajustar el tamaño del logo
        Matrix matrix = new Matrix();
        matrix.postScale(logoWidth / logoBitmap.getWidth(), logoHeight / logoBitmap.getHeight());
        // Aplicar la matriz de transformación al bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(logoBitmap, 0, 0, logoBitmap.getWidth(), logoBitmap.getHeight(), matrix, true);
        // Dibujar el logo en la esquina inferior derecha del PDF
        canvas.drawBitmap(resizedBitmap, logoLeft, logoTop, null);
        // Agregar espacio en blanco
        float espacioBlanco = 20; // Ajusta según tus necesidades
        float titleY = 100 + espacioBlanco;
        // Agregar título centrado y subrayado
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(36);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppins_bold);
        titlePaint.setTypeface(typeface);
        String titleText = "Resultados de la sesión";
        float titleX = canvas.getWidth() / 2;
        canvas.drawText(titleText, titleX, titleY, titlePaint);
        // Calcular la longitud del texto para determinar la posición del subrayado
        float titleWidth = titlePaint.measureText(titleText);
        float lineStartX = titleX - titleWidth / 2;
        float lineEndX = titleX + titleWidth / 2;
        float lineY = titleY + 10; // Ajusta según tus necesidades
        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, titlePaint);
        // Agregar la fecha en un tamaño de fuente más pequeño
        float fechaY = titleY + 60; // Ajusta según tus necesidades
        float fechaX = 50; // Ajusta según tus necesidades, este valor representa el margen izquierdo
        float fechaTextSize = 24; // Ajusta según tus necesidades
        Paint fechaPaint = new Paint();
        fechaPaint.setColor(Color.BLACK);
        fechaPaint.setTextSize(fechaTextSize);
        fechaPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Fecha: " + FechaSeleccionada, fechaX, fechaY, fechaPaint);
        // Después de dibujar la fecha
        float espacioDespuesDeFecha = 50; // Ajusta según tus necesidades
        float tablaY = fechaY + espacioDespuesDeFecha;
        // Dibujar la tabla
        float columna1X = 50; // Ajusta según tus necesidades
        float columna2X = 300; // Ajusta según tus necesidades
        float filaHeight = 40; // Ajusta según tus necesidades
        float columna2Width = 250; // Ajusta según tus necesidades
        // Configurar el tamaño y el estilo del texto en la tabla
        float textoSize = 18; // Ajusta según tus necesidades
        Paint textoPaint = new Paint();
        textoPaint.setColor(Color.BLACK);
        textoPaint.setTextSize(textoSize);
        textoPaint.setTextAlign(Paint.Align.LEFT);
        // Configurar el estilo de la línea para los bordes de la tabla
        Paint bordePaint = new Paint();
        bordePaint.setColor(Color.BLACK);
        bordePaint.setStyle(Paint.Style.STROKE);
        bordePaint.setStrokeWidth(2); // Ancho del borde
        // Matriz de textos para cada celda
        String[][] textosTabla = {
                {"Músculo medido", txtmusculo.getText().toString()},
                {"Lado corporal", txtlado.getText().toString()},
                {"Tipo de ejercicio", txtejercicio.getText().toString()},
                {"Peso utilizado (kg)", txtpeso.getText().toString()},
                {"Presento fatiga", txtfatigado.getText().toString()},
                {"Tiempo de fatiga", txttiempof.getText().toString()}
        };
        // Dibujar la tabla
        for (int i = 0; i < 6; i++) {
            float filaY = tablaY + i * filaHeight;
            // Dibujar celda de la columna 1
            canvas.drawRect(columna1X, filaY, columna2X, filaY + filaHeight, bordePaint);
            // Agregar texto alineado verticalmente en el centro y en el borde izquierdo de la celda de la columna 1
            float textoX1 = columna1X + 10; // Ajusta el espacio desde el borde izquierdo según tus necesidades
            float textoY1 = filaY + filaHeight / 2 + 5;
            canvas.drawText(textosTabla[i][0], textoX1, textoY1, textoPaint);
            // Dibujar celda de la columna 2
            canvas.drawRect(columna2X, filaY, columna2X + columna2Width, filaY + filaHeight, bordePaint);
            // Agregar texto alineado verticalmente en el centro y en el borde izquierdo de la celda de la columna 2
            float textoX2 = columna2X + 10; // Ajusta el espacio desde el borde izquierdo según tus necesidades
            float textoY2 = filaY + filaHeight / 2 + 5;
            canvas.drawText(textosTabla[i][1], textoX2, textoY2, textoPaint);
        }
        document.finishPage(page);
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        String fileName = "Historial " + FechaSeleccionada + ".pdf";
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
        SendMail(uri);
    }
    private void SendMail(Uri pdfAttachment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese la dirección de correo electrónico");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String emailsend = input.getText().toString();
                // ENVIO DIRECTO
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        final String senderEmail = "xxxx@gmail.com";
                        final String senderPassword = "Contraseña aca";
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
                            message.setSubject("Historial ENZOF");
                            MimeBodyPart messageBodyPart = new MimeBodyPart();
                            messageBodyPart.setText("Adjunto se encuentra el archivo PDF con los resultados de la sesión " + FechaSeleccionada);
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

                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            DataSource source = new FileDataSource(pdfFile);
                            attachmentPart.setDataHandler(new DataHandler(source));
                            attachmentPart.setFileName(pdfFile.getName());
                            // Combinar mensaje y archivo adjunto
                            Multipart multipart = new MimeMultipart();
                            multipart.addBodyPart(messageBodyPart);
                            multipart.addBodyPart(attachmentPart);
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
    //----------------------------------------------------------------------------------------------
    // FUNCION GUARDAR y MAIL-----------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    private void savePDFAndroidQ() {
        //GUARDA EL ARCHIVO
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        String fileName = "Resultado_de_la_sesion_" + FechaSeleccionada + ".pdf";
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
        Uri contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uri = resolver.insert(contentUri, contentValues);
        try {
            OutputStream outputStream = resolver.openOutputStream(uri);
            document.writeTo(outputStream);
            outputStream.close();
            // DIRECCION DE CORREO ELECTRONICO
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ingrese la dirección de correo electrónico");
            // Configurar el campo de entrada de texto en el cuadro de diálogo
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            builder.setView(input);
            // Configurar los botones del cuadro de diálogo
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Obtener la dirección de correo electrónico ingresada por el usuario
                    emailsend = input.getText().toString();
                    // ENVIO DEL MAIL
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    List<ResolveInfo> resolvedIntentActivities = getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resolvedIntentActivities) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsend});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Resultado de la sesión " + FechaSeleccionada);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Adjunto se encuentran los resultados de la sesión " + FechaSeleccionada);
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Agrega permisos de lectura
                    emailIntent.setPackage("com.google.android.gm");
                    // Inicia la actividad para enviar el correo electrónico
                    startActivity(Intent.createChooser(emailIntent, "Enviar correo electrónico"));
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Mostrar el cuadro de diálogo
            builder.show();
        } catch (IOException e) {
            Log.e("mylog", "Error al guardar el archivo PDF: " + e.toString());
            Toast.makeText(this, "Error al guardar el archivo PDF", Toast.LENGTH_SHORT).show();
        }
    }
    //----------------------------------------------------------------------------------------------
    // BOTONES --------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    public void btn_PDF(View view) {
        PDFdatos();
    }
    public void IrAMenuH(View view){
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
}