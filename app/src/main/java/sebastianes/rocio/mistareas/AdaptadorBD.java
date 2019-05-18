package sebastianes.rocio.mistareas;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase encargada de la generacion y actualizacion de la base de datos
 * Created by Rocio on 05/01/2018.
 */

public class AdaptadorBD extends SQLiteOpenHelper {
    public static final String DATABASE = "misNotasDB";
    private  static  final  int DATABASE_VERSION = 4;

    public AdaptadorBD(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    //Metodo invocado cuando se crea la clase. Se encarga de iniciar la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Tabla Notas
        String sql = "CREATE TABLE Notas(id_nota INTEGER PRIMARY KEY AUTOINCREMENT, titulo_nota TEXT, descripcion_nota TEXT, imagen_nota BLOB,lugar_nota TEXT, latitud_nota REAL, longitud_nota REAL);";

        db.execSQL(sql);
    }

    //Metodo usado para la actualizacion de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXIST Notas";
        db.execSQL(sql);

        onCreate(db);
    }


}
