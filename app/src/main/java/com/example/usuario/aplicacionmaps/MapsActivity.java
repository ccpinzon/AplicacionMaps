package com.example.usuario.aplicacionmaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.usuario.aplicacionmaps.logic.Estacion;
import com.example.usuario.aplicacionmaps.persistencia.ServicioEstacion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,LocationListener{

    private GoogleMap mMap;
    public static final String URL_DATOS = "http://www.knowlinemieds.com/";
    private LatLng miUbicacion;
    private int aux=0;
    private TextView Ncorriente, NDiesel, NExtra, NGnv, titulo, disponible;
    private List<Estacion> datos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button ubication = (Button) findViewById(R.id.ubication);

        ubication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMiUbicacion()!=null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getMiUbicacion(), 13));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL_DATOS)
                .build();

        ServicioEstacion servicioEstacion = retrofit.create(ServicioEstacion.class);

        Call<List<Estacion>> call = servicioEstacion.getEstaciones();

        call.enqueue(new Callback<List<Estacion>>() {
            @Override
            public void onResponse(Call<List<Estacion>> call, Response<List<Estacion>> response) {
                datos = response.body();
                try {
                    for (Estacion estacion: getDatos()) {
                        LatLng aux = new LatLng(estacion.getLatitud_estacion(), estacion.getLongitud_estacion());
                        //int id = getResources().getIdentifier("icono"+estacion.getMarca_mayorista().toLowerCase(), "drawable" , getPackageName());
                        mMap.addMarker(new MarkerOptions()
                                .title(String.valueOf(estacion.getId_estacion()))
                                .snippet(estacion.getMarca_mayorista())
                                .position(aux));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Estacion>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        informacionWindow();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(isOnline()) {
                    Intent intent = new Intent(MapsActivity.this, ServiciosEstacion.class);
                    intent.putExtra("idEstacion", marker.getTitle());
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"COMPRUEBE SU CONEXION A INTERNET", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("QUIERES SALIR DE LA APLICACION?")
                .setTitle("ESPERA");

        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void informacionWindow(){
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View informacion = getLayoutInflater().inflate(R.layout.mostrar, null);

                titulo = (TextView) informacion.findViewById(R.id.titulo);
                disponible = (TextView) informacion.findViewById(R.id.disponible);

                Ncorriente = (TextView) informacion.findViewById(R.id.tGasolina);
                NDiesel = (TextView) informacion.findViewById(R.id.tdisel);
                NExtra = (TextView) informacion.findViewById(R.id.textra);
                NGnv = (TextView) informacion.findViewById(R.id.tgnv);

                for (Estacion estacion: getDatos()) {
                    if (estacion.getId_estacion()==Integer.parseInt(marker.getTitle())){
                        titulo.setText(estacion.getNombre_estacion());
                        disponible.setText(marker.getTitle());

                        Ncorriente.setText(String.valueOf(estacion.getPrecio_corriente()));
                        NDiesel.setText(String.valueOf(estacion.getPrecio_diesel()));
                        NExtra.setText(String.valueOf(estacion.getPrecio_extra()));
                        NGnv.setText(String.valueOf(estacion.getPrecio_gnv()));
                    }
                }

                return informacion;
            }
        });
    }


    public List<Estacion> getDatos() {
        return datos;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.setMiUbicacion(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getMiUbicacion() {
        return miUbicacion;
    }

    public void setMiUbicacion(LatLng miUbicacion) {
        aux += aux + 1;
        if (aux==1)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 13));
        this.miUbicacion = miUbicacion;
    }
    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
