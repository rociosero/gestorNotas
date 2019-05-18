package sebastianes.rocio.mistareas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Clase notaDAO que es la encargada de realizar todas las acciones
 * de insertar, eliminar, modificar, búsqueda, etc de nuestras notas en base de datos
 * Created by Rocio on 05/01/2018.
 */

public class notaDAO {

    /**
     * Funcion que devuelve cursor al listado de notas
     * @param context
     * @return cursor
     */
    public Cursor dameNotas(Context context){
        AdaptadorBD db = new AdaptadorBD(context);

        String columnas[] = new String[] {"id_nota","titulo_nota","descripcion_nota", "imagen_nota","lugar_nota","latitud_nota","longitud_nota"};

        Cursor c = db.getReadableDatabase().query("Notas",columnas,null,null, null, null, null);
        return c;
    }

    /**
     * Funcion que devuelve cursor a la nota con id pasado como parametro
     * @param context
     * @param id
     * @return cursor
     */
    public Cursor dameNota(Context context,int id){
        AdaptadorBD db = new AdaptadorBD(context);

        String columnas[] = new String[] {"id_nota","titulo_nota","descripcion_nota","imagen_nota","lugar_nota","latitud_nota","longitud_nota"};
        String[] args = new String[]{String.valueOf(id)};
        Cursor c = db.getReadableDatabase().query("Notas",columnas,"id_nota=?",args, null, null, null);
        return c;
    }
    /**
     * Funcion que añade una nueva nota a la tabla Notas.
    *   @return -1 si error
    *             ID nueva nota si exito
    */
    public int anadirNota(Context context, nota nuevaNota){
        int result = -1;
        AdaptadorBD db = new AdaptadorBD(context);

        ContentValues valores = new ContentValues();
        valores.put("titulo_nota",nuevaNota.getTitulo());
        valores.put("descripcion_nota",nuevaNota.getDescripcion());
        valores.put("imagen_nota",nuevaNota.getImagen());
        valores.put("lugar_nota",nuevaNota.getLugar());
        valores.put("latitud_nota",nuevaNota.getLatitud());
        valores.put("longitud_nota",nuevaNota.getLongitud());

        result = (int) db.getWritableDatabase().insert("Notas",null,valores);
        return result;
    }

    /**
     * Funcion que modifica una nota
     * @param context
     * @param editaNota
     * @return numero de filas afectadas
     */
    public int modificaNota(Context context, nota editaNota){
        AdaptadorBD db = new AdaptadorBD(context);

        ContentValues valores = new ContentValues();
        valores.put("titulo_nota",editaNota.getTitulo());
        valores.put("descripcion_nota",editaNota.getDescripcion());
        valores.put("imagen_nota",editaNota.getImagen());
        valores.put("lugar_nota",editaNota.getLugar());
        valores.put("latitud_nota",editaNota.getLatitud());
        valores.put("longitud_nota",editaNota.getLongitud());
        String[] args = new String[]{String.valueOf(editaNota.getId())};

        int result = db.getWritableDatabase().update("Notas",valores,"id_nota=?",args);
        return result;
    }

    /**
     * Funcion que elimina
     * @param context
     * @param id nota a eliminar
     * @return numero de filas afectadas
     */
    public int eliminarNota(Context context, int id){
        AdaptadorBD db = new AdaptadorBD(context);

        String[] args = new String[]{String.valueOf(id)};

        int result = db.getWritableDatabase().delete("Notas","id_nota=?",args);
        return result;
    }
}
