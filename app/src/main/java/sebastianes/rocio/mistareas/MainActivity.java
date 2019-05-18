package sebastianes.rocio.mistareas;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase principal desde donde parte nuestra aplicacion
 *
 */
public class MainActivity extends AppCompatActivity {

    ListView lista;
    ArrayList<nota> miListaNotas;
    notaDAO daoNota;
    NotasAdapter adaptador;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado);
        miListaNotas= new ArrayList<nota>();
        daoNota = new notaDAO();

        lista = (ListView) findViewById(R.id.ListView_listado);

        //Si pulso sobre una nota, entrare en la pantalla de edicion
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                nota elegida = (nota) parent.getItemAtPosition(position);
                accion(elegida);
            }
        });

        //Evento que hace que se pueda elimiinar una nota si pulso sobre ella durante largo tiempo
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                nota notaAeliminar = (nota) parent.getItemAtPosition(position);
                eliminar(notaAeliminar);
                return true;
            }
        });

        adaptador = new NotasAdapter(this,R.layout.entrada,miListaNotas) {
            @Override
            public void onEntrada(Object entrada, View view) {

                if (entrada != null) {
                    TextView texto_id_nota = (TextView) view.findViewById(R.id.textView_titulo);
                    if (texto_id_nota != null)
                        texto_id_nota.setText(((nota) entrada).getTitulo());

                    TextView texto_titulo_entrada = (TextView) view.findViewById(R.id.textView_descripcion);
                    if (texto_titulo_entrada != null)
                        texto_titulo_entrada.setText(((nota) entrada).getDescripcion());

                    ImageView imagen_entrada = (ImageView) view.findViewById(R.id.imagen_lista);
                    if(((nota) entrada).getImagen()!=null) {
                        imagen_entrada.setImageBitmap(BitmapFactory.decodeByteArray(((nota) entrada).getImagen(), 0, ((nota) entrada).getImagen().length));
                    }
                }
            }

        };

        muestraNotas();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barra_accion,menu);
        return true;
    }

    /**
     * Metodo que gestiona las distintas opciones de nuestro menú
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menuSalir:
                finish();
                return true;

            case R.id.menuNueva:
                accion(null);
                return true;

            case R.id.verMapa:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
                return true;

            default:
              return  super.onOptionsItemSelected(item);
        }
    }

    /**
     * Recorre todas las notas que tenemos en base de datos y nos la añade a nuestra lista de notas
     */
    public void muestraNotas(){
        miListaNotas.clear();
        Cursor c = daoNota.dameNotas(this);

        if(c.moveToFirst()!=false) {
            do{
                miListaNotas.add(new nota(c.getInt(0),c.getString(1),c.getString(2),c.getBlob(3),c.getString(4),c.getDouble(5),c.getDouble(6)));
            }while (c.moveToNext());
        }

        lista.setAdapter(adaptador);


    }

    /**
     * Metodo que según tengamos una nota seleccionada (edicion) o no (nuevaNota), lanza nuevo
     * Intent para gestionar la nota
     * @param elegida
     */
    public void accion(nota elegida){

        if(elegida==null){ //NUEVA NOTA
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),GestionaNotaActivity.class);
            intent.putExtra("tipoAccion",1);
            startActivity(intent);
        }else{ //EDICION DE NOTA
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),GestionaNotaActivity.class);
            intent.putExtra("nota",elegida);
            intent.putExtra("tipoAccion",2);
            startActivity(intent);
        }

    }

    /**
     * Eliminar nota pulsada. Antes de eliminar saca un mensaje de advertencia para que confirmemos su eliminacion
     * @param elegida
     */
    public void eliminar(final nota elegida){
        //Muestro mensaje de advertencia antes de eliminar la nota
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Desea eliminar la nota "+elegida.getTitulo()+"?")
                .setTitle("Advertencia")
                .setCancelable(false)
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Continuar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(daoNota.eliminarNota(MainActivity.this,elegida.getId()) >0){
                                    toast = Toast.makeText(MainActivity.this,"Nota eliminada correctamente",Toast.LENGTH_LONG);
                                    toast.show();
                                    muestraNotas();//actualizo la lista de notas
                                }else{
                                    toast = Toast.makeText(MainActivity.this,"La nota no se ha podido eliminar",Toast.LENGTH_LONG);
                                    toast.show();
                                }

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        miListaNotas.clear();
        muestraNotas();
    }

}
