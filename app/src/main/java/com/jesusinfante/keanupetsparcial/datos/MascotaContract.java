package com.jesusinfante.keanupetsparcial.datos;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MascotaContract {

    private MascotaContract() {
    }

    // Constantes para el contenido uri.
    public static final String CONTENT_AUTHORITY = "com.jesusinfante.keanupetsparcial";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_MASCOTAS = "mascotas";


    /**
     * Clase interna que define valores constantes para la tabla de mascotas.
     */
    public static final class MascotaEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MASCOTAS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MASCOTAS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MASCOTAS;


        public final static String NOMBRE_TABLA = "mascotas";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMNA_NOMBRE_MASCOTA = "nombre";

        public final static String COLUMNA_RAZA_MASCOTA = "raza";

        public final static String COLUMNA_TIPO_MASCOTA = "tipo";
        public final static String COLUMNA_SEXO_MASCOTA = "sexo";

        public final static String COLUMNA_PESO_MASCOTA = "peso";

        public static final int TIPO_DESCONOCIDO = 0;
        public static final int TIPO_PERRO = 1;
        public static final int TIPO_GATO = 2;
        public static final int TIPO_AVE = 3;
        public static final int TIPO_ROEDOR = 4;
        public static final int SEXO_DESCONOCIDO = 0;
        public static final int SEXO_MACHO = 1;
        public static final int SEXO_HEMBRA = 2;


        public static boolean isValidSexo(int sexo) {
            return sexo == SEXO_DESCONOCIDO || sexo == SEXO_MACHO || sexo == SEXO_HEMBRA;
        }

        public static boolean isValidTipo(int tipo) {
            return tipo == TIPO_DESCONOCIDO || tipo == TIPO_PERRO || tipo == TIPO_GATO || tipo == TIPO_AVE || tipo == TIPO_ROEDOR;
        }
    }

}

