package sebastianes.rocio.mistareas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.google.android.gms.appindexing.AppIndex.*;

/**
 * Esta clase es la encargada de gestionar una nota en concreto.
 * Created by Rocio on 26/01/2018.
 */

public class GestionaNotaActivity extends AppCompatActivity {

    Button btnGuardarNota, btnCancelar;
    ImageButton btnLocalizacion,btnEliminar;
    notaDAO daoNota = new notaDAO();
    nota miNota;
    static int TIPO;//1 nueva nota, 2 editar nota
    String titulo, descripcion;
    double latitud, longitud;
    byte[] imagenAsignada;
    EditText TITULO, DESCRIPCION;
    TextView LUGAR, LAT, LNG;
    ImageView IMAGEN;
    Context contexto;

    /**
     * Constantes para identificar la accion realizada (tomar una fotografia
     * o bien seleccionarla de la galeria)
     */
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    /**
     * Variable que define el nombre para el archivo donde escribiremos
     * la fotografia de tama–o completo al tomarla.
     */
    private String name = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private int PLACE_PICKER_REQUEST = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionanota);
        contexto = this;

        name = Environment.getExternalStorageDirectory() + "/test.jpg";

        btnGuardarNota = (Button) findViewById(R.id.boton_guardarNota);
        btnCancelar = (Button) findViewById(R.id.boton_cancelar);
        btnLocalizacion=(ImageButton) findViewById(R.id.boton_localizacion);
        btnEliminar = (ImageButton)findViewById(R.id.boton_eliminar);
        LUGAR =(TextView) findViewById(R.id.lb_nombre_localizacion);
        LAT =(TextView) findViewById(R.id.lb_lat_loc);
        LNG =(TextView) findViewById(R.id.lb_lng_loc);
        imagenAsignada = null;

        //Recuperamos los datos pasados al intent
        Bundle bundle = this.getIntent().getExtras();
        TIPO = bundle.getInt("tipoAccion");


        if (TIPO == 2) {//edicion
            miNota = (nota) this.getIntent().getExtras().getSerializable("nota");
            btnEliminar.setVisibility(View.VISIBLE);
        } else {//nueva nota
            miNota = new nota(null,"","",imagenAsignada,getString(R.string.pulse_mapa_para_seleccionar),(double)0,(double)0);
            btnEliminar.setVisibility(View.INVISIBLE);
        }

        TITULO = (EditText) findViewById(R.id.et_nuevo_titulo);
        DESCRIPCION = (EditText) findViewById(R.id.et_nueva_descripcion);
        IMAGEN = (ImageView) findViewById(R.id.imagen_nota);

        TITULO.setText(miNota.getTitulo());
        DESCRIPCION.setText(miNota.getDescripcion());
        if(miNota.getImagen()!=null){
            IMAGEN.setImageBitmap(BitmapFactory.decodeByteArray(miNota.getImagen(), 0, miNota.getImagen().length));
        }
        LUGAR.setText(miNota.getLugar());
        LAT.setText(String.valueOf(miNota.getLatitud()));
        LNG.setText(String.valueOf(miNota.getLongitud()));

        IMAGEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignaImagen(v);
            }
        });
        btnGuardarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msj = "";
                int resultado;
                miNota.setTitulo(TITULO.getText().toString());
                miNota.setDescripcion(DESCRIPCION.getText().toString());
                miNota.setImagen(imagenAsignada);
                miNota.setLatitud(latitud);
                miNota.setLongitud(longitud);
                miNota.setLugar(LUGAR.getText().toString());

                switch (TIPO) {
                    case 1://nueva nota
                        resultado = daoNota.anadirNota(contexto, miNota);
                        if (resultado == -1) {
                            msj = getString(R.string.error_nueva_nota);
                        } else {
                            msj = getString(R.string.nueva_nota_ok);
                        }

                        break;
                    case 2://edita nota
                        resultado = daoNota.modificaNota(GestionaNotaActivity.this, miNota);
                        if (resultado > 0) {
                            msj = getString(R.string.edit_nota_ok);
                        } else {
                            msj = getString(R.string.error_edit_nota);
                        }
                        break;
                }

                Mensaje(msj);
                finish();
            }
        });


        btnLocalizacion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(GestionaNotaActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(miNota.getId()>0){
                    String msj = "";
                    int resultado;
                    resultado =daoNota.eliminarNota(GestionaNotaActivity.this,miNota.getId());
                    if(resultado>0){
                        msj=getString(R.string.nota_eliminada);
                    }else{
                        msj=getString(R.string.nota_no_eliminada);
                    }
                    Mensaje(msj);
                    finish();
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(API).build();
    }


    public void Mensaje(String msj) {
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void asignaImagen(View v) {
        AlertDialog.Builder getImageFrom = new AlertDialog.Builder(GestionaNotaActivity.this);
        getImageFrom.setTitle("SELECCIONA ORIGEN:");
        final CharSequence[] opsChars = {"Camara", "Galeria"};
        getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, TAKE_PICTURE);
                    }

                } else if (which == 1) { //GALERIA
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, SELECT_PICTURE);
                }
                dialog.dismiss();
            }

        });
        AlertDialog alert = getImageFrom.create();
        alert.show();

    }


    /**
     * Este metodo recoge los datos del intent lanzado.
     *
     * Los resultados recogidos son bien de la Galeria de Fotos, bien de la Cámara o del Place Picker
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        Uri selectedImage;

        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                imagenAsignada = stream.toByteArray();

                IMAGEN.setImageBitmap(imageBitmap);
            } else if (requestCode == SELECT_PICTURE) {
                Uri seleccion = data.getData();
                try {
                    Bitmap yourSelectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), seleccion);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    imagenAsignada = stream.toByteArray();

                    IMAGEN.setImageBitmap(yourSelectedImage);

                } catch (IOException e) {
                    Toast.makeText(this, "Ha ocurrido un error al mostrar la imagen", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }else if (requestCode == PLACE_PICKER_REQUEST) {
                   Place place = PlacePicker.getPlace(this,data);

                    LatLng location = place.getLatLng();

                   latitud = location.latitude;
                   longitud = location.longitude;

                   LUGAR.setText(place.getAddress());
                   LAT.setText(String.valueOf(latitud));
                   LNG.setText(String.valueOf(longitud));

            }
        }


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {

        Thing object = new Thing.Builder()
                .setName("GestionaNota Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}