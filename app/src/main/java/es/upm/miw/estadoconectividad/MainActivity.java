package es.upm.miw.estadoconectividad;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    static final String LOG_TAG = "MiW";

    static final String URL_RECURSO = "http://www.etsisi.upm.es/robots.txt"; // approx 11 secs
    //static final String URL_RECURSO = "https://recursosweb.prisaradio.com/podcasts/619p.xml"; // approx 14 secs
    //static final String URL_RECURSO = "http://www.cope.es/api/es/programas/el-partidazo-de-cope/audios/rss.xml"; // approx 140 secs


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hayConexion()) {
            Toast.makeText(
                    getApplicationContext(),
                    getString(R.string.txtNoHayConex),
                    Toast.LENGTH_LONG
            ).show();
        } else {

            // NetworkOnMainThreadException
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            mostrarPeticion(URL_RECURSO, (TextView) findViewById(R.id.tvContenido));
        }
    }

    /**
     * Recupera un recurso y lo muestra en el TextView tvContenido
     * @param recurso
     */
    void mostrarPeticion(String recurso, TextView tvContenido) {
        HttpURLConnection con = null;

        try {
            tvContenido.setText("");
            URL mUrl = new URL(recurso);

            con = (HttpURLConnection) mUrl.openConnection();
            BufferedReader fin = new BufferedReader(
                    new InputStreamReader(
                            con.getInputStream()
                    )
            );

            // Mostrar resultado...
            String linea = fin.readLine();
            while (linea != null) {
                Thread.sleep(100);
                tvContenido.append(linea + '\n');
                Log.i(LOG_TAG, linea);
                linea = fin.readLine();
            }

            fin.close();
        } catch (Exception e) {
            // tratamiento error
            Log.e(LOG_TAG, e.toString());
        } finally {
            if (con != null) con.disconnect();
        }
    }

    /**
     * Determina si el dispositivo está conectado
     *
     * @return valor lógico
     */
    public boolean hayConexion() {
        ConnectivityManager conMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
}
