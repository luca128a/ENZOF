package com.example.enzof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.enzof.modelo.RegistrosEMG;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;
import org.jtransforms.fft.DoubleFFT_1D;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class Calibracion extends AppCompatActivity {

    //1. DECLARACIONES PARAMETROS ------------------------------------------------------------------
    private Spinner SpinnerM;
    private Spinner SpinnerL;
    private Spinner SpinnerE;
    private TextView PickerP;
    private static final int NumVent = 15;
    double referencia2 = 0;
    double referencia2F = 0;
    double referencia3 = 0;
    double referencia3F = 0;
    private double[] RMS;
    private double[] MEAN_FFT;
    private String[] opcionesM = {"Zona del cuerpo", "1. Abdomen (Abdominales)", "2. Antebrazo", "3. Brazo anterior (Bíceps)", "4. Brazo posterior (Tríceps)", "5. Espalda alta (Trapecio)", "6. Espalda baja (Lumbares)", "7. Espalda lateral (Dorsales)", "8. Hombros", "9. Muslo anterior (Cuadríceps)", "10. Muslo posterior (Isquiotibiales)", "11. Pecho (Pectorales)", "12. Pierna (Pantorilla)"};
    private String[] opcionesE = {"Tipo de ejercicio", "1. Cíclico con carga", "2. Cíclico sin carga", "3. Estático"};
    private String[] opcionesL = {"Lado corporal", "1. Lado Derecho", "2. Lado Izquierdo"};
    private String Musculo;
    private String Lado;
    private String Ejercicio;
    private float Peso;
    // VECTOR PORCENTAJES --------------------------------------------------------------------------
    private String Clave;
    private int Fila1;
    private int Fila2;
    private int Columna;
    // GUARDADO de la INFO ---------------------------------------------------------------------
    private String FechaDia;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    FirebaseAuth auth;
    private Boolean Start;
    private ArrayList<Integer> vector;
    private double tiempo_fatiga;
    private String presento_fatiga;
    // VIDEO o GRAFICO -------------------------------------------------------------------------
    private VideoView videoView;
    private MediaController mediaController;
    private Switch SwitchG;
    private RelativeLayout layout_video;
    private RelativeLayout layout_grafico;
    private LinearLayout layout_cuentaregresiva;
    private TextView cuentaregresiva;
    private LineChart mChart;
    private ArrayList<Entry> entries = new ArrayList<>();
    private LineDataSet dataSet;
    private static final int MAX_ENTRIES = 600;
    private int yValue = 0;
    // BOTONES ---------------------------------------------------------------------------------
    private ImageButton BIniciar;
    private ImageButton BSincronizar;
    private ImageButton BDetener;
    private RelativeLayout RIniciar;
    private RelativeLayout RDetener;
    // TIEMPO de la SESION ---------------------------------------------------------------------
    private long tiempoInicio;
    private long tiempoActual;
    private long tiempoTranscurrido;
    private Boolean bdetener;
    // BLUETOOTH -------------------------------------------------------------------------------
    BluetoothAdapter myBT;
    private Set<BluetoothDevice> pairedDevices;
    int Request_enable_BT;
    private BluetoothSocket BTSocket;
    private InputStream SocketInputStream;
    private OutputStream SocketOutputStream;
    private BluetoothDevice ENZOF;
    private UUID uuid;
    // RECIBIR DATOS ---------------------------------------------------------------------------
    boolean ReciboDatos=false;
    boolean stop=false;
    int count = 0;
    List<Double> datosArray2 = new CopyOnWriteArrayList<>();
    long duracionSegundos =0;
    // PROCESAMIENTO ---------------------------------------------------------------------------
    private static final int SAMPLE_RATE = 1000;
    private static final double LOW_CUTOFF_FREQUENCY = 20.0;
    private static final double HIGH_CUTOFF_FREQUENCY = 450.0;
    private static final double windowSizeSeconds = 0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibracion);
        // DECLARACIÓN FIREBASE  -------------------------------------------------------------------
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("RegistrosEMG");
        auth = FirebaseAuth.getInstance();
        // GRAFICO SEÑAL en VIVO -------------------------------------------------------------------
        mChart = findViewById(R.id.chart1);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawLabels(false);
        YAxis yl = mChart.getAxisRight();
        yl.setTextColor(Color.WHITE);
        yl.setAxisMinimum(0f);
        yl.setAxisMaximum(5.2f);
        yl.setDrawGridLines(true);
        mChart.setAutoScaleMinMaxEnabled(false);
        dataSet = createSet(entries);
        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        // DECLARACION de VARIABLES-----------------------------------------------------------------
        Start = false;
        vector = new ArrayList<Integer>();
        RIniciar = findViewById(R.id.RIniciar);
        RIniciar.setVisibility(View.VISIBLE);
        RDetener = findViewById(R.id.RDetener);
        BIniciar = findViewById(R.id.btn_iniciar);
        BDetener = findViewById(R.id.btn_detener);
        SwitchG = findViewById(R.id.switch1);
        bdetener = false;
        final VideoView video = findViewById(R.id.videoView);
        layout_grafico = findViewById(R.id.layout_grafico);
        layout_video = findViewById(R.id.layout_video);
        layout_cuentaregresiva = findViewById(R.id.layout_cuentaregresivaC);
        cuentaregresiva = findViewById(R.id.texto_cuentaregresivaC);
        // CONFIGURACION de LOS SPINNERS + RECUPERA INFO -------------------------------------------
        SpinnerM = findViewById(R.id.lista_musculo);
        SpinnerL = findViewById(R.id.lado_corporal);
        SpinnerE = findViewById(R.id.lista_tipo_ejercicio);
        PickerP = findViewById(R.id.numberpicker);
        // MUSCULO -----------------------------------------------------------------------------
        Musculo = getIntent().getStringExtra("musculoSeleccionado");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerM.setAdapter(adapter);
        int posicionMusculoSeleccionado = Arrays.asList(opcionesM).indexOf(Musculo);
        if (posicionMusculoSeleccionado >= 0) {
            SpinnerM.setSelection(posicionMusculoSeleccionado);
        }
        SpinnerM.setEnabled(false);
        Columna = Integer.parseInt(Musculo.substring(0, Musculo.indexOf('.'))) - 1;
        // LADO CORPORAL -----------------------------------------------------------------------
        Lado = getIntent().getStringExtra("ladoSeleccionado");
        ArrayAdapter<String> adapterL = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesL);
        adapterL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerL.setAdapter(adapterL);
        int posicionLadoSeleccionado = Arrays.asList(opcionesL).indexOf(Lado);
        if (posicionLadoSeleccionado >= 0) {
            SpinnerL.setSelection(posicionLadoSeleccionado);
        }
        SpinnerL.setEnabled(false);
        Fila2 = Integer.parseInt(Lado.substring(0, Lado.indexOf('.'))) - 1;
        // TIPO de EJERCICIO -------------------------------------------------------------------
        Ejercicio = getIntent().getStringExtra("ejercicioSeleccionado");
        ArrayAdapter<String> adapterE = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesE);
        adapterE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerE.setAdapter(adapterE);
        int posicionEjercicioSeleccionado = Arrays.asList(opcionesE).indexOf(Ejercicio);
        if (posicionEjercicioSeleccionado >= 0) {
            SpinnerE.setSelection(posicionEjercicioSeleccionado);
        }
        SpinnerE.setEnabled(false);
        Fila1 = Integer.parseInt(Ejercicio.substring(0, Ejercicio.indexOf('.'))) - 1;
        // PESO ELEGIDO ------------------------------------------------------------------------
        Peso = getIntent().getFloatExtra("pesoSeleccionado", 0.0f);
        PickerP.setText(String.valueOf(Peso));
        // BOTON SINCRONIZAR con BT ----------------------------------------------------------------
        BSincronizar = findViewById(R.id.btn_conectar);
        myBT = BluetoothAdapter.getDefaultAdapter();
        Request_enable_BT = 1;
        ENZOF = myBT.getRemoteDevice("00:22:06:01:9C:8F");
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BSincronizar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!myBT.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 0);
                    Toast.makeText(getBaseContext(), "Se inició el BT", Toast.LENGTH_SHORT).show();
                }
                try {
                    if (BTSocket != null) {
                        BTSocket.close();
                        Toast.makeText(getApplicationContext(), "Ya se encuentra conectado al dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Conectando...", Toast.LENGTH_SHORT).show();
                        BTSocket = ENZOF.createRfcommSocketToServiceRecord(uuid);
                        BTSocket.connect();
                        Toast.makeText(getApplicationContext(), "Conectado al dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error al conectar Bluetooth", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        // INICIO de la CALIBRACION ----------------------------------------------------------------
        BIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BTSocket == null) {
                    Toast.makeText(getApplicationContext(), "El módulo Bluetooth no está conectado", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        SocketInputStream = BTSocket.getInputStream();
                        SocketOutputStream = BTSocket.getOutputStream();
                    } catch (IOException e) {
                        Log.e("Bluetooth", "Error al crear el socket Bluetooth", e);
                    }
                    if (SwitchG.isChecked()) {
                        layout_cuentaregresiva.setVisibility(View.VISIBLE);
                    } else {
                        layout_video.setVisibility(View.VISIBLE);
                        reproducirVideo();
                    }
                    RIniciar.setVisibility(View.GONE);
                    RDetener.setVisibility(View.VISIBLE);
                    tiempoInicio = System.currentTimeMillis();
                    Start = true;
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("es", "AR"));
                    sdf.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
                    String fechaHoraActual = sdf.format(calendar.getTime());
                    FechaDia = fechaHoraActual;
                    beginListenForData();
                }
            }
        });
        BDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bdetener == true) {
                    enviarSenal0();
                    guardadoInfo();
                } else {
                    Toast.makeText(getApplicationContext(),"No ha comenzado la medición, espere por favor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // FUNCIONES COMUNICACION CON BT ---------------------------------------------------------------
    private void enviarSenal0() {
        if (BTSocket != null && BTSocket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Bluetooth", "Sending signal 0");
                    try {
                        OutputStream outputStream = BTSocket.getOutputStream();
                        stop=true;
                        ReciboDatos=false;
                        count = 0;
                        outputStream.write("0".getBytes());
                    } catch (IOException e) {
                        Log.e("Bluetooth", "Error sending signal:", e);
                    }
                }
            }).start();
        }
    }
    private void enviarSenal1() {
        try {
            String signal = "1";
            SocketOutputStream.write(signal.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // FUNCION del VIDEO ---------------------------------------------------------------------------
    private void reproducirVideo() {
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.VISIBLE);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        int videoResource;
        //CAMBIO de VIDEOS--------------------------------------------------------------------------
        if (Ejercicio.equals("1. Cíclico con carga")) {
            if (Musculo.equals("11. Pecho (Pectorales)") || Musculo.equals("4. Brazo posterior (Tríceps)")) {
                videoResource = R.raw.pecho1;
            } else if (Musculo.equals("12. Pierna (Pantorilla)") || Musculo.equals("1. Abdomen (Abdominales)") || Musculo.equals("9. Muslo anterior (Cuadríceps)") || Musculo.equals("10. Muslo posterior (Isquiotibiales)")) {
                videoResource = R.raw.abdominales1;
            } else if (Musculo.equals("2. Antebrazo") || Musculo.equals("3. Brazo anterior (Bíceps)")) {
                videoResource = R.raw.biceps1;
            } else if (Musculo.equals("5. Espalda alta (Trapecio)") || Musculo.equals("6. Espalda baja (Lumbares)") || Musculo.equals("7. Espalda lateral (Dorsales)")) {
                videoResource = R.raw.espalda2;
            } else {
                videoResource = R.raw.hombros1;
            }
        } else if (Ejercicio.equals("2. Cíclico sin carga")) {
            if (Musculo.equals("8. Hombros") || Musculo.equals("11. Pecho (Pectorales)") || Musculo.equals("1. Abdomen (Abdominales)") || Musculo.equals("4. Brazo posterior (Tríceps)")) {
                videoResource = R.raw.pecho2;
            } else if ( Musculo.equals("12. Pierna (Pantorilla)") ||  Musculo.equals("10. Muslo posterior (Isquiotibiales)")) {
                videoResource = R.raw.femoral2;
            } else if (Musculo.equals("2. Antebrazo") || Musculo.equals("3. Brazo anterior (Bíceps)")) {
                videoResource = R.raw.antebrazo2;
            } else if (Musculo.equals("5. Espalda alta (Trapecio)") || Musculo.equals("6. Espalda baja (Lumbares)") || Musculo.equals("7. Espalda lateral (Dorsales)")) {
                videoResource = R.raw.espalda2;
            } else {
                videoResource = R.raw.cuadriceps2;
            }
        } else {
            if (Musculo.equals("11. Pecho (Pectorales)") || Musculo.equals("4. Brazo posterior (Tríceps)")) {
                videoResource = R.raw.pecho3;
            } else if (Musculo.equals("12. Pierna (Pantorilla)") || Musculo.equals("1. Abdomen (Abdominales)") || Musculo.equals("9. Muslo anterior (Cuadríceps)") || Musculo.equals("10. Muslo posterior (Isquiotibiales)")) {
                videoResource = R.raw.abdominales3;
            } else if (Musculo.equals("2. Antebrazo") || Musculo.equals("3. Brazo anterior (Bíceps)")) {
                videoResource = R.raw.biceps3;
            } else if (Musculo.equals("5. Espalda alta (Trapecio)") || Musculo.equals("6. Espalda baja (Lumbares)") || Musculo.equals("7. Espalda lateral (Dorsales)")) {
                videoResource = R.raw.espalda3;
            } else {
                videoResource = R.raw.hombro3;
            }
        }
        String videoPath = "android.resource://" + getPackageName() + "/" + videoResource;
        videoView.setVideoPath(videoPath);
        videoView.start();
    }
    // FUNCION RECIBIR DATOS -----------------------------------------------------------------------
    private void beginListenForData() {
        Thread dataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dataInputStream = new DataInputStream(SocketInputStream);
                String signal = "1";
                try {
                    SocketOutputStream.write(signal.getBytes());
                    ReciboDatos=true;
                    datosArray2.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while ((true || !stop) & ReciboDatos) {
                    try {
                        byte highByte = dataInputStream.readByte();
                        if (count == 0 && highByte == 0) {
                            highByte = dataInputStream.readByte();
                        }
                        byte lowByte = dataInputStream.readByte();
                        int valorRecibido = (highByte & 0xFF) | ((lowByte & 0xFF) << 8);
                        if ((lowByte & 0x80) != 0) {
                            valorRecibido |= 0xFFFF0000;
                        }
                        yValue = yValue+1;
                        if (yValue % 1 == 0) {
                            final int finalValorRecibido = valorRecibido;
                            final int finalYValue = yValue;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addEntry(finalValorRecibido, finalYValue);
                                }
                            });
                        }
                        vector.add(valorRecibido);
                        count ++;
                    } catch (IOException e) {
                        e.printStackTrace();
                        enviarSenal1();
                    }
                }
            }
        });
        dataThread.start();
    }
    private void addEntry(int y, int x) {
        LineData data = mChart.getData();
        tiempoActual = System.currentTimeMillis();
        tiempoTranscurrido = tiempoActual - tiempoInicio;
        duracionSegundos = tiempoTranscurrido / 1000;
        System.out.println("duracion" + duracionSegundos);
        if (duracionSegundos==0 && SwitchG.isChecked()){
            layout_cuentaregresiva.setVisibility(View.VISIBLE);
            cuentaregresiva.setText("ESPERE UN MOMENTO");
        } else if (duracionSegundos == 20 && SwitchG.isChecked()) {
            cuentaregresiva.setText("COMIENCE CON \nEL EJERCICIO");
        } else if (duracionSegundos == 30 && SwitchG.isChecked()) {
            layout_cuentaregresiva.setVisibility(View.GONE);
            layout_grafico.setVisibility(View.VISIBLE);
        } else if (duracionSegundos == 60) {
            BDetener.setVisibility(View.VISIBLE);
            bdetener = true;
        }
        if (data != null && data.getDataSetCount() > 0) {
            if (dataSet != null && data.getDataSetCount() > 0) {
                for (int i = 0; i < dataSet.getEntryCount() - 1; i++) {
                    Entry entry = dataSet.getEntryForIndex(i + 1);
                    entry.setX(i);
                }
                float voltaje = (float) (y * 5.0 / 1023.0);
                entries.add(new Entry(dataSet.getEntryCount(), voltaje));
                if (entries.size() > MAX_ENTRIES) {
                    entries.remove(0);
                }
                dataSet.notifyDataSetChanged();
                data.notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(MAX_ENTRIES);
                mChart.moveViewToX(dataSet.getEntryCount() - MAX_ENTRIES);
                YAxis yAxisLeft = mChart.getAxisLeft();
                yAxisLeft.setAxisMinimum(0f);
                yAxisLeft.setAxisMaximum(5.2f);
                mChart.invalidate();
            }else{System.out.println("este es el problema");}
        }else{System.out.println("este es el problema");}
    }
    // FUNCION GUARDADO de INFO --------------------------------------------------------------------
    private void guardadoInfo() {
        System.out.println("guardado de info entre");
        AlertDialog.Builder builder = new AlertDialog.Builder(Calibracion.this);
        builder.setTitle("Guardar medición");
        builder.setMessage("¿Desea guardar la medición?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // CALCULO de los PORCENTAJES-------------------------------------------------------
                ArrayList<Double> puntos = filtrado(vector);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Calibracion.this, Menu.class);
                startActivity(i);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // FUNCIONES de PROCESAMIENTO ------------------------------------------------------------------
    public ArrayList<Double> filtrado(ArrayList<Integer> vector) {
        //1. CONVIERTE EL ARRAYLIST A DOUBLE -------------------------------------------------------
        double[] arreglo = new double[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            double elemento = vector.get(i).doubleValue();
            arreglo[i] = elemento * 1023 / 5;
        }
        //CALCULO DE LA MEDIA Y SE LE RESTA---------------------------------------------------------
        double mean = calculateMean(arreglo);
        double[] emgWithMeanSubtracted = subtractMean(arreglo, mean);
        //3.FILTRO PASA-BANDAS----------------------------------------------------------------------
        double[] emg_f_pb = applyBandPassFilter(emgWithMeanSubtracted);
        //4.RECTIFICADO ----------------------------------------------------------------------------
        double[] emg_rect = new double[emg_f_pb.length];
        for (int i = 0; i < emg_f_pb.length; i++) {
            emg_rect[i] = Math.abs(emg_f_pb[i]);
        }
        // CALCULO LOS PORCENTAJES -----------------------------------------------------------------
            // RMS ---------------------------------------------------------------------------------
        List<double[]> windowsRMS = generateWindows2(emg_rect, NumVent);
        double[] RMS_total = new double[windowsRMS.size()];
        double porcentajeRMS;
        for (int i = 0; i < windowsRMS.size(); i++) {
            double [] window = windowsRMS.get(i);
            RMS_total[i] = RMS_f(window, SAMPLE_RATE);
        }
        RMS =  Arrays.copyOfRange(RMS_total, 3, RMS_total.length);
        for (int k = 0; k < 3; k++) {
            referencia3 += RMS[k]/3;
        }
        for (int k = RMS.length - 3; k < RMS.length; k++) {
            referencia3F += RMS[k]/3;
        }
        porcentajeRMS = referencia3F / referencia3;
        System.out.println("referencia inicial: " + referencia3);
        System.out.println("referencia final: " + referencia3F + "porcentaje" + porcentajeRMS);
        System.out.println("Elementos de RMS:");
        for (int i = 0; i < RMS.length; i++) {
            System.out.println(RMS[i]);
        }
        //--------------------------------------------------------------------------
        double porcentajeMF;
        List<double[]> windowsMF = generateWindows2(emg_f_pb, NumVent);
        double[] MEAN_FFT_total = new double[windowsMF.size()];
        for (int i = 0; i < windowsMF.size(); i++) {
            double[] window = windowsMF.get(i);
            double[] Caracteristicas = periodograma(window, SAMPLE_RATE);
            MEAN_FFT_total[i] = Caracteristicas[0];
        }
        MEAN_FFT =  Arrays.copyOfRange(MEAN_FFT_total, 3, RMS_total.length);
        for (int k = 0; k < 3; k++) {
            referencia2 += MEAN_FFT[k]/3;
        }
        for (int k = MEAN_FFT.length - 3; k < MEAN_FFT.length; k++) {
            referencia2F += MEAN_FFT[k]/3;
        }
        porcentajeMF = referencia2F / referencia2;
        System.out.println("referencia inicial: " + referencia2);
        System.out.println("referencia final: " + referencia2F + "porcentaje" + porcentajeMF);
        System.out.println("Elementos de RMS:");
        for (int i = 0; i < MEAN_FFT.length; i++) {
            System.out.println(MEAN_FFT[i]);
        }
        ArrayList<Double> RMSlista = new ArrayList<Double>();
        if (porcentajeRMS > 1.15 && porcentajeMF < 0.95) {
            Toast.makeText(this, "Calibración realizada con éxito", Toast.LENGTH_SHORT).show();
        } else if (porcentajeRMS > 1.15 && porcentajeMF > 0.95) {
            porcentajeMF = 0;
            Toast.makeText(this, "Calibración RMS realizada con éxito", Toast.LENGTH_SHORT).show();
        } else if (porcentajeMF < 0.95 && porcentajeRMS < 1.15) {
            porcentajeRMS = 0;
            Toast.makeText(this, "Calibración MF realizada con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "% fuera del rango recomendado. No se ha guardado", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Menu.class);
            startActivity(i);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference ref = myRef.child(userId).child("porcentajes").child(String.valueOf(Fila1)).child(String.valueOf(Fila2)).child(String.valueOf(Columna));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtener el valor en la posición (1, 1, 1)
                double value = dataSnapshot.getValue(Double.class);
                // Verificar si el valor es igual a cero
                if (value == 0) {
                    // Hacer algo si el valor es cero
                    //ref.setValue(porcentajeRMS);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejo de errores
                Log.e("Firebase", "Error al obtener datos", databaseError.toException());
            }
        });
        DatabaseReference ref2 = myRef.child(userId).child("porcentajes2").child(String.valueOf(Fila1)).child(String.valueOf(Fila2)).child(String.valueOf(Columna));
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot2) {
                // Obtener el valor en la posición (1, 1, 1)
                double value2 = dataSnapshot2.getValue(Double.class);
                // Verificar si el valor es igual a cero
                if (value2 == 0) {
                    // Hacer algo si el valor es cero
                    // ref2.setValue(porcentajeF);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError2) {
                // Manejo de errores
                Log.e("Firebase", "Error al obtener datos", databaseError2.toException());
            }
        });
        return RMSlista;
    }
    private static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public double[] periodograma(double[] x, int fs ) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fs);
        double[] spectrum = new double[fs];
        fft.realForward(x);
        for (int i = 0; i < fs / 2 + 1; i++) {
            double real = x[2 * i];
            double imag = x[2 * i + 1];
            spectrum[i] = (real * real + imag * imag) / fs;
        }
        double[] frec=new double[spectrum.length];
        for (int i = 0; i < fs / 2 + 1; i++) {
            double frequency = i * (fs / 2.0) / (fs / 2.0 + 1);
            frec[i]=frequency;
        }
        double MNFl;
        double MDFl;
        double aa = 0;
        double bb = 0;
        double bb2 = 0;
        for (int i = 0; i < spectrum.length; i++) {
            aa += frec[i] * spectrum[i];
            bb += spectrum[i];
            if (i != 0) {
                bb2 += spectrum[i];
            }
        }
        MNFl = aa / bb;
        MDFl = 0.5 * bb2;
        return new double[] {MNFl, MDFl};
    }
    public Double linearRegression(double[] y) {
        String cadena = arrayToString(y);
        int n = y.length;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += y[i];
            sumXY += i * y[i];
            sumXX += i * i;
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        double slope = (sumXY - n * meanX * meanY) / (sumXX - n * meanX * meanX);
        double intercept = meanY - slope * meanX;
        double yo = intercept;
        double yf = slope * (n - 1) + intercept;
        double porcentaje = Math.abs((yf/yo));
        String mensaje = "\n yo: " + yo + "\nyf: " + yf + "\n%: " + porcentaje + "\n Slope " + slope;
        showAlert(this, "Valores de yo y yf", mensaje + cadena );
        return porcentaje;
    }
    private String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        // Agregar cada número del array a la cadena, separado por un espacio
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            // Agregar un espacio después de cada número, excepto el último
            if (i < array.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    public List<double[]> generateWindows2(double[] signal, int NumVent) {
        int signalLength = signal.length;
        int windowLength = (int) Math.floor(signalLength / (float) NumVent);
        System.out.println("Cantidad de datos por ventana: " + windowLength);
        List<double[]> windows = new ArrayList<>();
        for (int i = 0; i < NumVent; i++) {
            int start = i * windowLength;
            int end = Math.min(start + windowLength, signalLength);
            double[] window = new double[end - start];
            System.arraycopy(signal, start, window, 0, end - start);
            windows.add(window);
        }
        return windows;
    }
    public double RMS_f(double[] senal, int f) {
        double a = 0.0;
        double rms;
        for (int i = 0; i < senal.length; i++) {
            a += Math.pow(senal[i], 2);
        }
        rms = Math.sqrt(a / (senal.length * (1.0 / f)));
        return rms;
    }
    @Contract(pure = true)
    private double calculateMean(@NonNull double[] signal) {
        double sum = 0.0;
        for (double value : signal) {
            sum += value;
        }
        return sum / signal.length;
    }
    @NonNull
    @Contract(pure = true)
    private double[] subtractMean(@NonNull double[] signal, double mean) {
        double[] result = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            result[i] = signal[i] - mean;
        }
        return result;
    }
    @NonNull
    public static double[] applyBandPassFilter(@NonNull double[] signal) {
        int n = signal.length;
        // Perform forward FFT
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        double[] frequencyDomainSignal = new double[2 * n];
        System.arraycopy(signal, 0, frequencyDomainSignal, 0, n);
        fft.realForwardFull(frequencyDomainSignal);
        // Apply the band-pass filter to the frequency domain signal
        double[] filteredSignal = new double[2 * n];
        int lowIndex = (int) Math.ceil(LOW_CUTOFF_FREQUENCY * n / SAMPLE_RATE);
        int highIndex = (int) Math.floor(HIGH_CUTOFF_FREQUENCY * n / SAMPLE_RATE);
        System.arraycopy(frequencyDomainSignal, 0, filteredSignal, 0, 2 * n);
        for (int i = 0; i < lowIndex; i++) {
            filteredSignal[2 * i] = 0.0;
            filteredSignal[2 * i + 1] = 0.0;
        }
        for (int i = highIndex + 1; i < n; i++) {
            filteredSignal[2 * i] = 0.0;
            filteredSignal[2 * i + 1] = 0.0;
        }
        // Perform inverse FFT
        fft.realInverse(filteredSignal, true);
        // Extract the filtered signal from the complex-valued result
        double[] result = new double[n];
        System.arraycopy(filteredSignal, 0, result, 0, n);
        return result;
    }
    public static double redondear(double valor, int decimales) {
        if (decimales < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(decimales, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    // FUNCIONES para GRAFICO ----------------------------------------------------------------------
    private LineDataSet createSet(ArrayList<Entry> hola) {
        LineDataSet set = new LineDataSet(hola, "EMG [volts]");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Habilita la curva suavizada (cubic)
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(1f);
        set.setFillAlpha(255);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,117,177));
        set.setDrawValues(false);
        return set;
    }
}