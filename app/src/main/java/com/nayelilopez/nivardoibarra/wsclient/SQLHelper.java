package com.nayelilopez.nivardoibarra.wsclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by nivardoibarra on 11/18/15.
 */
public class SQLHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="BDPedidosFine7.db";

    public SQLHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pedidos (clave PRIMARY KEY ,empleado TEXT, producto TEXT, fecha TEXT, cantidad TEXT)");
        db.execSQL("CREATE TABLE clientes (idCliente PRIMARY KEY ,nombre TEXT)");
        db.execSQL("CREATE TABLE productos (idProducto PRIMARY KEY ,nombre TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        android.util.Log.v("Constants", "Upgrading database ,which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Materias");
        onCreate(db);

    }

}
