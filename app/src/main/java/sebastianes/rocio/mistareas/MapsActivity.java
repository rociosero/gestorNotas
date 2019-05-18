package sebastianes.rocio.mistareas;

/**
 * Created by Rocio on 06/02/2018.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private notaDAO daoNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        daoNota = new notaDAO();

        //administramos el fragmento
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Añadimos los marcadores de nuestras notas
        Cursor c = daoNota.dameNotas(this);

        if(c.moveToFirst()!=false) {
            LatLng martir;
            nota martirNota;
            do{
                martirNota = new nota(c.getInt(0),c.getString(1),c.getString(2),c.getBlob(3),c.getString(4),c.getDouble(5),c.getDouble(6));
                martir = new LatLng(martirNota.getLatitud(),martirNota.getLongitud());
                mMap.addMarker(new MarkerOptions().position(martir).title(martirNota.getTitulo()).snippet(martirNota.getDescripcion()));
            }while (c.moveToNext());
            //movemos la camara a la ultima nota guardada
            mMap.moveCamera(CameraUpdateFactory.newLatLng(martir));
        }


        //cuando pulsamos encima de un marcador
        mMap.setOnMarkerClickListener(this);

        //si se pulsa en otro sitio del mapa
        mMap.setOnMapClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        //latLng latitud y longitud de tipo double

    }
}
