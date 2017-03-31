package com.example.usuario.aplicacionmaps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usuario.aplicacionmaps.logic.Estacion;
import com.example.usuario.aplicacionmaps.persistencia.ServicioEstacion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class ContactFragment extends Fragment implements LocationListener {

    MapView mapView;
    GoogleMap gooMap;
    public static final String URL_DATOS = "http://www.knowlinemieds.com/";
    private LatLng miUbicacion;
    private int aux=0;
    private TextView Ncorriente, NDiesel, NExtra, NGnv, titulo, disponible;
    private List<Estacion> datos = new ArrayList<>();

    public ContactFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.map1);
        mapView.onCreate(savedInstanceState);

        if (mapView != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    gooMap=googleMap;
                    cargaDatos();
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    gooMap.setMyLocationEnabled(true);
                    LatLng ub = new LatLng(5.547931, -73.350728);
                    // Updates the location and zoom of the MapView
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ub, 12);
                    //gooMap.moveCamera(cameraUpdate);
                    informacionWindow();
                }
            });

        }
    }

    public void cargaDatos(){
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
                    for (Estacion estacion: datos) {
                        LatLng aux = new LatLng(estacion.getLatitud_estacion(), estacion.getLongitud_estacion());
                        //int id = getResources().getIdentifier("icono"+estacion.getMarca_mayorista().toLowerCase(), "drawable" , getPackageName());
                        gooMap.addMarker(new MarkerOptions()
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
                Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void informacionWindow(){
        gooMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View informacion = getActivity().getLayoutInflater().inflate(R.layout.mostrar, null);

                titulo = (TextView) informacion.findViewById(R.id.titulo);
                disponible = (TextView) informacion.findViewById(R.id.disponible);

                Ncorriente = (TextView) informacion.findViewById(R.id.tGasolina);
                NDiesel = (TextView) informacion.findViewById(R.id.tdisel);
                NExtra = (TextView) informacion.findViewById(R.id.textra);
                NGnv = (TextView) informacion.findViewById(R.id.tgnv);

                for (Estacion estacion: datos) {
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

    public LatLng getMiUbicacion() {
        return miUbicacion;
    }

    public void setMiUbicacion(LatLng miUbicacion) {
        aux += aux + 1;
        if (aux==1)
            gooMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 13));
        this.miUbicacion = miUbicacion;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
}
