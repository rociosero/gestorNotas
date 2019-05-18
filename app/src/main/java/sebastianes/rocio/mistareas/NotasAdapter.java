package sebastianes.rocio.mistareas;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Clase adaptador del listado de notas.
 * Created by Rocio on 16/01/2018.
 */

public abstract class NotasAdapter extends BaseAdapter {

   private Context context;
    private ArrayList<?> misNotas;
    private int R_layout_IdView;

    public NotasAdapter(Context context,int R_layout_IdView, ArrayList<?> misNotas) {
        super();
        this.misNotas = misNotas;
        this.context = context;
        this.R_layout_IdView = R_layout_IdView;
    }

    @Override
    public View getView(int position, View view, ViewGroup pariente) {
        if(view==null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R_layout_IdView,null);
        }
        onEntrada(misNotas.get(position),view);
        return view;
    }


    @Override
    public int getCount() {
        return misNotas.size();
    }

    @Override
    public Object getItem(int position) {
        return misNotas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract void onEntrada(Object entrada,View view);

}
