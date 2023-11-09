package com.jesusinfante.keanupetsparcial;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.jesusinfante.keanupetsparcial.datos.MascotaContract;
public class MascotaCursorAdapter extends CursorAdapter {

    MascotaCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.lista_items,parent,false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nombreTextView =  view.findViewById(R.id.nombre);
        TextView resumenTextView =  view.findViewById(R.id.resumen);
        TextView resumendosTextView =  view.findViewById(R.id.resumendos);

        int nombreColumnIndex = cursor.getColumnIndex(MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA);
        int tipoColumnIndex = cursor.getColumnIndex(MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA);
        int razaColumnIndex = cursor.getColumnIndex(MascotaContract.MascotaEntry.COLUMNA_RAZA_MASCOTA);

        String nombreMascota = cursor.getString(nombreColumnIndex);
        String tipoMascota  = "";
        switch (cursor.getString(tipoColumnIndex)){
            case "1":
                tipoMascota = "Perro 🐩";
                break;
            case "2":
                tipoMascota = "Gato 🐈";
                break;
            case "3":
                tipoMascota = "Ave 🦆";
                break;
            case "4":
                tipoMascota = "Roedor 🐭";
                break;
            default:
                tipoMascota ="Tipo No Identificado 🐾";
        }
        String razaMascota = cursor.getString(razaColumnIndex);

        if (TextUtils.isEmpty(razaMascota)) {
            razaMascota = context.getString(R.string.unknown_breed);
        }

        nombreTextView.setText(nombreMascota);
        resumendosTextView.setText(tipoMascota);
        resumenTextView.setText(razaMascota);
    }
}
