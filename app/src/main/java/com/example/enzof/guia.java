package com.example.enzof;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

public class guia extends AppCompatActivity {
    private Spinner spinnerM;
    private Spinner spinnerI;
    private String[] opcionesM = {"Músculo", "Bíceps", "Tríceps", "Cuádriceps"};
    private String[] opcionesI = {"Información acerca de...", "Electrodos", "Explicación del ejercicio", "Video ejercicio"};
    private String musculoSeleccionado;
    private String informacionSeleccionada;
    private LinearLayout layout_sin_R;
    private LinearLayout layout_sin_M;
    private LinearLayout layout_b_e;
    private LinearLayout layout_t_e;
    private LinearLayout layout_c_e;
    private LinearLayout layout_b_i;
    private LinearLayout layout_t_i;
    private LinearLayout layout_c_i;
    private LinearLayout layout_video;
    private VideoView videoView;
    private MediaController mediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guia);
        //DECLARAR
        spinnerM = findViewById(R.id.lista_guia);
        spinnerI = findViewById(R.id.lista_guia2);
        layout_sin_R = findViewById(R.id.sin_seleccionarR);
        layout_sin_M = findViewById(R.id.sin_seleccionar_menu);
        layout_b_i = findViewById(R.id.biceps_info);
        layout_t_i = findViewById(R.id.triceps_info);
        layout_c_i = findViewById(R.id.cuadriceps_info);
        layout_b_e = findViewById(R.id.biceps_electrodos);
        layout_t_e = findViewById(R.id.triceps_electrodos);
        layout_c_e = findViewById(R.id.cuadriceps_electrodos);
        videoView = findViewById(R.id.videoView);
        layout_video = findViewById(R.id.lay_video);
        //CONFIGURACION SEGUN SI VIENE DE MENU o DE REGISTRO -------------------------------------------------------------
        Intent intent = getIntent();
        musculoSeleccionado = intent.getStringExtra("musculoSeleccionado");
        boolean desdeRegistro = intent.getBooleanExtra("desdeRegistro", false);
        if (desdeRegistro) {
            // VIENE DESDE EL REGISTRO, EL SPINNER DE MUSCULO NO SE PUEDE CAMBIAR. Y SE MUESTRA QUE SELECCIONE INFORMACION
            ArrayAdapter<String> adapterM = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{musculoSeleccionado});
            adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerM.setAdapter(adapterM);
            spinnerM.setEnabled(false);
            layout_sin_R.setVisibility(View.VISIBLE);

        } else {
            //VIENE DESDE EL MENU ASI QUE PUEDE CAMBIAR EL MUSCULO SELECCIONADO Y SI LO HACE VA A CAMBIAR PANTALLA.
            ArrayAdapter<String> adapterM = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesM);
            adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerM.setAdapter(adapterM);
            spinnerM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        musculoSeleccionado = opcionesM[position];
                        layout_sin_R.setVisibility(View.VISIBLE);
                        cambiarpantalla();
                    } else{
                        musculoSeleccionado = null;
                        dejarvacio();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    dejarvacio();
                    musculoSeleccionado = null;
                }
            });
        }
        //CONFIGURACION DEL SPINNER DE INFORMACION
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesI);
        adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerI.setAdapter(adapterD);
        spinnerI.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    informacionSeleccionada = opcionesI[position];
                    cambiarpantalla();
                } else{
                    dejarvacio();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dejarvacio();
            }
        });
    }
    private void cambiarpantalla() {
        dejarvacio();
        layout_sin_M.setVisibility(View.GONE);
        layout_sin_R.setVisibility(View.GONE);
        if (musculoSeleccionado==null) {
            Toast.makeText(guia.this, "Seleccione músculo", Toast.LENGTH_SHORT).show();
        } else if ((opcionesI != null && opcionesI.length > 0 && spinnerI.getSelectedItemPosition() == 0)) {
            Toast.makeText(guia.this, "Seleccione acerca de que quiere saber más", Toast.LENGTH_SHORT).show();
        } else {
            if (musculoSeleccionado.equals("Bíceps") && informacionSeleccionada.equals("Electrodos")) {
                layout_b_e.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("Tríceps") && informacionSeleccionada.equals("Electrodos")) {
                layout_t_e.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("Cuádriceps") && informacionSeleccionada.equals("Electrodos")) {
                layout_c_e.setVisibility(View.VISIBLE);
            } else if (informacionSeleccionada.equals("Video ejercicio")) {
                layout_video.setVisibility(View.VISIBLE);
                reproducirVideo();
            } else if (musculoSeleccionado.equals("Bíceps") && informacionSeleccionada.equals("Explicación del ejercicio")) {
                layout_b_i.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("Cuádriceps") && informacionSeleccionada.equals("Explicación del ejercicio")) {
                layout_c_i.setVisibility(View.VISIBLE);
            } else if (musculoSeleccionado.equals("Tríceps") && informacionSeleccionada.equals("Explicación del ejercicio")) {
                layout_t_i.setVisibility(View.VISIBLE);
            } else {
                //nada por ahora
            }
        }
    }
    private void reproducirVideo() {
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Reinicia el video una vez que haya terminado
                videoView.start();
            }
        });
        int videoResource;
        switch (musculoSeleccionado) {
            case "Bíceps":
                videoResource = R.raw.biceps1;
                break;
            case "Tríceps":
                videoResource = R.raw.pecho1;
                break;
            case "Cuádriceps":
                videoResource = R.raw.abdominales1;
                break;
            default:
                // Si no se selecciona un músculo válido, muestra un mensaje de error y detén la reproducción del video
                Toast.makeText(guia.this, "Músculo no válido", Toast.LENGTH_SHORT).show();
                videoView.stopPlayback();
                return;
        }
        String videoPath = "android.resource://" + getPackageName() + "/" + videoResource;
        videoView.setVideoPath(videoPath);
        videoView.start();
    }
    public void dejarvacio() {
        layout_c_e.setVisibility(View.GONE);
        layout_t_e.setVisibility(View.GONE);
        layout_b_e.setVisibility(View.GONE);
        layout_c_i.setVisibility(View.GONE);
        layout_t_i.setVisibility(View.GONE);
        layout_b_i.setVisibility(View.GONE);
        layout_video.setVisibility(View.GONE);
    }
    public void IrAMenu(View view) {
        Intent i = new Intent(this, Menu.class);
        startActivity(i);
    }
}