package com.example.usuario.aplicacionmaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aplicacionmaps.logic.Estacion;
import com.example.usuario.aplicacionmaps.logic.EstacionInformacion;
import com.example.usuario.aplicacionmaps.logic.Servicio;
import com.example.usuario.aplicacionmaps.persistencia.ServicioInfoEstacion;
import com.example.usuario.aplicacionmaps.persistencia.ServicioInfoServicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiciosEstacion extends AppCompatActivity {

    private TextView idEstacion, nombreEstacion, direccionEstacion, departamento,
            mayorista, telefonoFijo, telefonoMovil, nombreServicio, tipoServicio;
    private String idEs;
    public static final String URL_DATOS = "http://www.knowlinemieds.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios_estacion);

        idEstacion = (TextView) findViewById(R.id.idEstacion);
        nombreEstacion = (TextView) findViewById(R.id.nombreEstacion);
        direccionEstacion = (TextView) findViewById(R.id.direccionEstacion);
        departamento = (TextView) findViewById(R.id.departamento);
        mayorista = (TextView) findViewById(R.id.mayorista);
        telefonoFijo = (TextView) findViewById(R.id.telefonoFijo);
        telefonoMovil = (TextView) findViewById(R.id.telefonoMovil);
        nombreServicio = (TextView) findViewById(R.id.nombreServicio);
        tipoServicio = (TextView) findViewById(R.id.tipoServicio);

        idEs = getIntent().getStringExtra("idEstacion");
        getDatosEstacion();
        getServiciosEstacion();

    }

    public void getDatosEstacion(){

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL_DATOS)
                .build();

        ServicioInfoEstacion infoEstacion = retrofit.create(ServicioInfoEstacion.class);

        Map<String, String> data = new HashMap<>();
        data.put("id_estacion", idEs);

        Call<EstacionInformacion> informacionCall = infoEstacion.getInfoEstacion(data);

        informacionCall.enqueue(new Callback<EstacionInformacion>() {
            @Override
            public void onResponse(Call<EstacionInformacion> call, Response<EstacionInformacion> response) {
                idEstacion.setText(String.valueOf(response.body().getId_estacion()));
                nombreEstacion.setText(response.body().getNombre_estacion());
                direccionEstacion.setText(response.body().getDireccion_estacion());
                departamento.setText(response.body().getNombre_departamento());
                mayorista.setText(response.body().getMarca_mayorista());
                telefonoFijo.setText(response.body().getTel_fijo_estacion());
                telefonoMovil.setText(response.body().getTel_movil_estacion());
            }

            @Override
            public void onFailure(Call<EstacionInformacion> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CAPTURA DE DATOS" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getServiciosEstacion(){

        Retrofit retrofit2 = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL_DATOS)
                .build();

        ServicioInfoServicio servicio = retrofit2.create(ServicioInfoServicio.class);

        Map<String, String> dataa = new HashMap<>();
        dataa.put("id_estacion", idEs);

        Call<List<Servicio>> listCall = servicio.getServicios(dataa);

        listCall.enqueue(new Callback<List<Servicio>>() {
            @Override
            public void onResponse(Call<List<Servicio>> call, Response<List<Servicio>> response) {
                try{
                    nombreServicio.setText(response.body().get(0).getNombre_servicio());
                    tipoServicio.setText(response.body().get(0).getTipo_servicio());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Servicio>> call, Throwable t) {

            }
        });

    }

}
