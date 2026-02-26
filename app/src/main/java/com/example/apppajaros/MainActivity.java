package com.example.apppajaros;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // busco los elementos de la pantalla
        VideoView vvw_FondoPajaro = findViewById(R.id.vvw_FondoPajaro);
        Button btn_IrInicioSesion = findViewById(R.id.btn_IrInicioSesion);
        Button btn_IrRegistro = findViewById(R.id.btn_IrRegistro);

        // compruebo que el video existe antes de liarla
        if (vvw_FondoPajaro != null) {

            // monto la ruta usando el nombre del archivo que has metido en raw
            String ruta = "android.resource://" + getPackageName() + "/" + R.raw.pajaro_volando;
            vvw_FondoPajaro.setVideoURI(Uri.parse(ruta));

            vvw_FondoPajaro.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    // saco las medidas de la pantalla y del video
                    float videoAncho = mp.getVideoWidth();
                    float videoAlto = mp.getVideoHeight();
                    float pantallaAncho = vvw_FondoPajaro.getWidth();
                    float pantallaAlto = vvw_FondoPajaro.getHeight();

                    // si las medidas son validas hago la magia matematica
                    if (videoAncho > 0 && videoAlto > 0 && pantallaAncho > 0 && pantallaAlto > 0) {

                        // calculo la proporcion que necesito estirar en cada lado
                        float escalaX = pantallaAncho / videoAncho;
                        float escalaY = pantallaAlto / videoAlto;

                        // pillo la escala mas grande para que no queden bordes negros
                        float escalaMaxima = Math.max(escalaX, escalaY);

                        // calculo la escala final para centrarlo (efecto centerCrop real)
                        float escalaFinalX = (videoAncho * escalaMaxima) / pantallaAncho;
                        float escalaFinalY = (videoAlto * escalaMaxima) / pantallaAlto;

                        // le aplico tu zoom del 1.3 para tapar la marca de agua
                        vvw_FondoPajaro.setScaleX(escalaFinalX * 1.3f);
                        vvw_FondoPajaro.setScaleY(escalaFinalY * 1.3f);
                    }

                    // bucle infinito y arranco
                    mp.setLooping(true);
                    vvw_FondoPajaro.start();
                }
            });
        }

        // logica para ir al login
        if (btn_IrInicioSesion != null) {
            btn_IrInicioSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(MainActivity.this, IniciarSesion.class);
                    startActivity(intencion);
                }
            });
        }

        // logica para ir al registro
        if (btn_IrRegistro != null) {
            btn_IrRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intencion = new Intent(MainActivity.this, RegistrarseActivity.class);
                    startActivity(intencion);
                }
            });
        }
    }
}