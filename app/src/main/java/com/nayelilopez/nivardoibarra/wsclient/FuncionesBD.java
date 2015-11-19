package com.nayelilopez.nivardoibarra.wsclient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by nivardoibarra on 11/18/15.
 */
public class FuncionesBD {
    //private static final String accionSoap = "http://tempuri.org/HelloWorld";
    //private static final String Metodo = "HelloWorld";
    private SQLHelper sqlhelper = null;
    private static final String namespace = "http://tempuri.org/";
    //private static final String url = "http://169.254.164.227:90/Service1.asmx";
    private static final String url = "http://www.w3schools.com/WebServices/TempConvert.asmx";
    private String[] tuplaInicial;
    private Context context;
    private String[] arreglo1DP, arreglo1DP2, arreglo1DC, arregloVectorClientes, arreglo1DPr;
    private String[] arregloTupla = new String[5];
    private String[][] arregloClientes, arregloProductos;
    private String[][] arregloPedidos, arregloPedidos2, arregloPedidosRemoto;
    private int cantidadRegistrosP, cantidadRegistrosP2;

    public FuncionesBD(Context contxt) {
        context = contxt;
        this.sqlhelper = new SQLHelper(context);
    }

    public void BorraBaseTablas() {
        try {
            SQLiteDatabase db = sqlhelper.getWritableDatabase();
            db.execSQL("DROP TABLE pedidos");
            db.execSQL("DROP TABLE clientes");
            db.execSQL("DROP TABLE productos");

    	/*db.execSQL("DELETE * FROM pedidos");
    	db.execSQL("DELETE * FROM clientes");
    	db.execSQL("DELETE * FROM productos");*/

            db.execSQL("CREATE TABLE pedidos (clave PRIMARY KEY ,empleado TEXT, producto TEXT, fecha TEXT, cantidad TEXT)");
            db.execSQL("CREATE TABLE clientes (idCliente PRIMARY KEY ,nombre TEXT)");
            db.execSQL("CREATE TABLE productos (idProducto PRIMARY KEY ,nombre TEXT)");

            db.close();
        } catch (Exception e) {
            mensaje("Error al borrar los registros");
        }
    }

    public String[][] selectPedidos() {

        String accionSoap = "http://tempuri.org/selectPedido";
        String Metodo = "selectPedido";
        String cadena = "";
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            cadena = resultado.toString();
            cantidadRegistrosP = determinaRegistros(cadena);
            arreglo1DP = new String[cantidadRegistrosP];
            arregloPedidos = new String[5][cantidadRegistrosP];
            almacenaRegistros(cadena);
            for (int l = 0; l < arreglo1DP.length; l++) {
                almacenaArreglo2D(arreglo1DP[l], l);
            }
        } catch (Exception e) {
            mensaje("Error 2...");
        }
        return arregloPedidos;
    }

    public int determinaRegistros(String cadenaEntera) {
        int numeroRegistros = 0;
        for (int k = 0; k < cadenaEntera.length(); k++) {
            if (cadenaEntera.charAt(k) == '*') {
                numeroRegistros++;
            }
        }
        return numeroRegistros;
    }

    public void almacenaRegistros(String cadena) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadena.length(); k++) {
            if (cadena.charAt(k) == '*') {
                arreglo1DP[posicion] = cadena.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
    }

    public void almacenaArreglo2D(String cadenaRegistro, int pos) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadenaRegistro.length(); k++) {
            if (cadenaRegistro.charAt(k) == ',') {
                arregloTupla[posicion] = cadenaRegistro.substring(inicio, k);
                arregloPedidos[posicion][pos] = cadenaRegistro.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        AgregarBaseDatos(Integer.parseInt(arregloTupla[0]), arregloTupla[1], arregloTupla[2], arregloTupla[3], arregloTupla[4]);

        //mensaje("IdPedido: "+arregloTupla[0]);
        //mensaje("Cliente: "+arregloTupla[1]);
        //mensaje("Producto: "+arregloTupla[2]);
        //mensaje("Fecha: "+arregloTupla[3]);
        //mensaje("Cantidad: "+arregloTupla[4]);
    }

    public String[] selectClientes() {
        String accionSoap = "http://tempuri.org/selectCliente";
        String Metodo = "selectCliente";
        String cadena = "";
        int cantidadRegistrosClientes = 0;
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            cadena = resultado.toString();
            //mensaje("tabla cliente: "+cadena);
            cantidadRegistrosClientes = determinaRegistrosC(cadena);
            arreglo1DC = new String[cantidadRegistrosClientes];
            almacenaRegistrosC(cadena);
            arregloClientes = new String[2][cantidadRegistrosClientes];
            arregloVectorClientes = new String[cantidadRegistrosClientes];
            for (int l = 0; l < cantidadRegistrosClientes; l++) {
                almacenaArreglo2DC(arreglo1DC[l], l);
            }
            //arregloVectorClientes = cortaArreglo(arregloClientes, cantidadRegistros);
        } catch (Exception e) {
            mensaje("Error 2...");
        }
        return arregloVectorClientes;
    }

    public int determinaRegistrosC(String cadenaEntera) {
        int numeroRegistros = 0;
        for (int k = 0; k < cadenaEntera.length(); k++) {
            if (cadenaEntera.charAt(k) == '*') {
                numeroRegistros++;
            }
        }
        //mensaje("numero de registros: "+numeroRegistros);
        return numeroRegistros;
    }

    public void almacenaRegistrosC(String cadena) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadena.length(); k++) {
            if (cadena.charAt(k) == '*') {
                arreglo1DC[posicion] = cadena.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        //mensaje(arreglo1DC[0]);
        //mensaje(arreglo1DC[1]);
    }

    public void almacenaArreglo2DC(String cadenaRegistro, int renglon) {
        String Nombre = "";
        int IdCliente = 0;
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadenaRegistro.length(); k++) {
            if (cadenaRegistro.charAt(k) == ',') {
                arregloClientes[posicion][renglon] = cadenaRegistro.substring(inicio, k);
                //arregloVectorClientes[] = cadenaRegistro.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        IdCliente = Integer.parseInt(arregloClientes[0][renglon]);
        Nombre = arregloClientes[1][renglon];
        AgregarCliente(IdCliente, Nombre);
        //mensaje("IdCliente: "+arregloClientes[0][renglon]);
        //mensaje("Nombre: "+arregloClientes[1][renglon]);
    }

    /*public String[] cortaArreglo(String[][] tablaCliente, int numeroDeElementos){
    	String [] nombreProductos =  new String [numeroDeElementos];
    	for(int k = 0; k < numeroDeElementos; k ++){
    		nombreProductos [k] = tablaCliente[k][1];
    		mensaje("nombre: "+nombreProductos[k]);
    	}
    	return nombreProductos;
    }*/

    public void selectProductos() {
        String accionSoap = "http://tempuri.org/selectProducto";
        String Metodo = "selectProducto";
        String cadena = "";
        int cantidadRegistrosProductos = 0;
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            cadena = resultado.toString();
            //mensaje("tabla Productos: "+cadena);
            cantidadRegistrosProductos = determinaRegistrosP(cadena);
            arreglo1DPr = new String[cantidadRegistrosProductos];
            almacenaRegistrosP(cadena);
            arregloProductos = new String[2][cantidadRegistrosProductos];
            //arregloVectorProducto = new String[cantidadRegistros];
            for (int l = 0; l < cantidadRegistrosProductos; l++) {
                almacenaArreglo2DP(arreglo1DPr[l], l);
            }
            //arregloVectorClientes = cortaArreglo(arregloClientes, cantidadRegistros);
        } catch (Exception e) {
            mensaje("Error 2...");
        }
        //return arregloVectorClientes;
    }

    public int determinaRegistrosP(String cadenaEntera) {
        int numeroRegistros = 0;
        for (int k = 0; k < cadenaEntera.length(); k++) {
            if (cadenaEntera.charAt(k) == '*') {
                numeroRegistros++;
            }
        }
        //mensaje("numero de registros: "+numeroRegistros);
        return numeroRegistros;
    }

    public void almacenaRegistrosP(String cadena) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadena.length(); k++) {
            if (cadena.charAt(k) == '*') {
                arreglo1DPr[posicion] = cadena.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        //mensaje(arreglo1DC[0]);
        //mensaje(arreglo1DC[1]);
    }

    public void almacenaArreglo2DP(String cadenaRegistro, int renglon) {
        String Nombre = "";
        int IdProducto = 0;
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadenaRegistro.length(); k++) {
            if (cadenaRegistro.charAt(k) == ',') {
                arregloProductos[posicion][renglon] = cadenaRegistro.substring(inicio, k);
                //arregloVectorClientes[] = cadenaRegistro.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        IdProducto = Integer.parseInt(arregloProductos[0][renglon]);
        Nombre = arregloProductos[1][renglon];
        AgregarProducto(IdProducto, Nombre);
        //mensaje("IdProducto: "+arregloProductos[0][renglon]);
        //mensaje("Nombre: "+arregloProductos[1][renglon]);
    }

    /*public String[] cortaArreglo(String[][] tablaCliente, int numeroDeElementos){
    	String [] nombreProductos =  new String [numeroDeElementos];
    	for(int k = 0; k < numeroDeElementos; k ++){
    		nombreProductos [k] = tablaCliente[k][1];
    		mensaje("nombre: "+nombreProductos[k]);
    	}
    	return nombreProductos;
    }*/

    public void subirRegistros() {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from pedidos", null);
        int numregistros = c.getCount();
        //mensaje("Registros movil: "+numregistros);
        int indice = 0;
        //verificaRegistro(1, "2" , "2", "11/10/11", "10");
        arregloPedidosRemoto = selectPedidosNoAlmacena();
        String[][] arregloPedidosLocal = new String[numregistros][5];
        if (c.moveToFirst()) {
            do {
                String clave = c.getString(0);
                String cliente = c.getString(1);
                String producto = c.getString(2);
                String fecha = c.getString(3);
                String cantidad = c.getString(4);
                arregloPedidosLocal[indice][0] = clave;
                arregloPedidosLocal[indice][1] = cliente;
                arregloPedidosLocal[indice][2] = producto;
                arregloPedidosLocal[indice][3] = fecha;
                arregloPedidosLocal[indice][4] = cantidad;
                ///String cadena=""+clave+" "+cliente+" "+" "+producto+" "+fecha+" "+cantidad;
                //mensaje(cadena);
                verificaRegistro(Integer.parseInt(clave), cliente, producto, fecha, cantidad);
                indice++;
            } while (c.moveToNext());
        }
        borraRegistro(arregloPedidosLocal, numregistros);
        //mensaje("BD actualizada");
        db.close();
    }

    public void verificaRegistro(int idPedido, String cliente, String producto, String fecha, String cantidad) {
        int indice = 0;
        boolean concurrencia = false;
        //mensaje("registroRemoto: "+arregloPedidosRemoto[0][0]);
        //mensaje("arreglo pedidos: "+arregloPedidos[0][indice]);
        while (indice < cantidadRegistrosP2) {
            if (idPedido == Integer.parseInt(arregloPedidos2[0][indice])) {
                concurrencia = true;
            }
            indice++;
        }
        if (concurrencia == true) {
            modificar(idPedido, cliente, producto, fecha, cantidad);
        } else {
            insertar(idPedido, cliente, producto, fecha, cantidad);
        }

    }

    public void modificar(int idPedido, String cliente, String producto, String fecha, String cantidad) {
        String accionSoap = "http://tempuri.org/modificarPedido";
        String Metodo = "modificarPedido";
        PropertyInfo IdPedido = new PropertyInfo();
        PropertyInfo Cliente = new PropertyInfo();
        PropertyInfo Producto = new PropertyInfo();
        PropertyInfo Fecha = new PropertyInfo();
        PropertyInfo Cantidad = new PropertyInfo();
        IdPedido.setName("idPedido");
        IdPedido.setValue(idPedido);
        Cliente.setName("idCliente");
        Cliente.setValue(Integer.parseInt(cliente));
        Producto.setName("idProducto");
        Producto.setValue(Integer.parseInt(producto));
        Fecha.setName("fecha");
        Fecha.setValue(fecha);
        Cantidad.setName("cantidad");
        Cantidad.setValue(cantidad);
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            request.addProperty(IdPedido);
            request.addProperty(Cliente);
            request.addProperty(Producto);
            request.addProperty(Fecha);
            request.addProperty(Cantidad);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            //cadena = resultado.toString();
            //mensaje("tabla Productos: "+cadena);
	         /*cantidadRegistros = determinaRegistrosP(cadena);
	     	 arreglo1DP = new String [cantidadRegistros];
	     	 almacenaRegistrosP(cadena);
	     	 arregloProductos = new String [2][cantidadRegistros];
	     	 //arregloVectorProducto = new String[cantidadRegistros];
	     	 	for(int l = 0; l < cantidadRegistros; l ++)
	     	 	{
	     	 		almacenaArreglo2DP(arreglo1DP[l], l);
	     	 	}
	     	 	//arregloVectorClientes = cortaArreglo(arregloClientes, cantidadRegistros);*/
        } catch (Exception e) {
            mensaje("Error al modificar...");
        }
    }

    public void insertar(int idPedido, String cliente, String producto, String fecha, String cantidad) {
        String accionSoap = "http://tempuri.org/insertarPedido";
        String Metodo = "insertarPedido";
        PropertyInfo IdPedido = new PropertyInfo();
        PropertyInfo Cliente = new PropertyInfo();
        PropertyInfo Producto = new PropertyInfo();
        PropertyInfo Fecha = new PropertyInfo();
        PropertyInfo Cantidad = new PropertyInfo();
        IdPedido.setName("idPedido");
        IdPedido.setValue(idPedido);
        Cliente.setName("idCliente");
        Cliente.setValue(Integer.parseInt(cliente));
        Producto.setName("idProducto");
        Producto.setValue(Integer.parseInt(producto));
        Fecha.setName("fecha");
        Fecha.setValue(fecha);
        Cantidad.setName("cantidad");
        Cantidad.setValue(cantidad);
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            request.addProperty(IdPedido);
            request.addProperty(Cliente);
            request.addProperty(Producto);
            request.addProperty(Fecha);
            request.addProperty(Cantidad);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
        } catch (Exception e) {
            mensaje("Error al insertar...");
        }
    }

    public void borraRegistro(String[][] arregloPedidosLocal, int numregistros) {
        if (cantidadRegistrosP2 > numregistros) {
            int indice = 0;

            while (indice < cantidadRegistrosP2) {
                boolean concurrencia = false;
                int indice2 = 0;
                while (indice2 < numregistros) {
                    if (Integer.parseInt(arregloPedidos2[0][indice]) == Integer.parseInt(arregloPedidosLocal[indice2][0])) {
                        concurrencia = true;
                    }
                    indice2++;
                }
                if (concurrencia == false) {
                    borrar(Integer.parseInt(arregloPedidos2[0][indice]));
                }
                indice++;
            }
        }
    }

    public void borrar(int idPedido) {
        String accionSoap = "http://tempuri.org/eliminarPedido";
        String Metodo = "eliminarPedido";
        PropertyInfo IdPedido = new PropertyInfo();
        IdPedido.setName("idPedido");
        IdPedido.setValue(idPedido);
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            request.addProperty(IdPedido);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
        } catch (Exception e) {
            mensaje("Error al insertar...");
        }
    }

    private void mensaje(String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void AgregarBaseDatos(int clave, String empleado, String producto, String fecha, String cantidad) {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        db.execSQL("INSERT INTO pedidos (clave,empleado,producto,fecha,cantidad) VALUES ('" + clave + "','" + empleado + "','" + producto + "','" + fecha + "','" + cantidad + "') ");
        db.close();
    }

    public void AgregarCliente(int IdCliente, String Nombre) {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        //db.execSQL("INSERT INTO clientes (idCliente,nombre,domicilio,telefono) VALUES ('1','Luis','zaragoza #35','7471123456')");
        db.execSQL("INSERT INTO clientes (idCliente,nombre) VALUES ('" + IdCliente + "','" + Nombre + "') ");
        //db.execSQL("INSERT INTO productos (idProducto,nombre,cantidad) VALUES ('1','fanta','500')");
        //db.execSQL("INSERT INTO clientes (idCliente,nombre,domicilio,telefono) VALUES ('2','carlos','galeana #5','7471123478')");
        //db.execSQL("INSERT INTO productos (idProducto,nombre,cantidad) VALUES ('2','galletas maria','700')");
        db.close();
    }

    public void AgregarProducto(int IdProducto, String Nombre) {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        db.execSQL("INSERT INTO productos (idProducto,nombre) VALUES ('" + IdProducto + "','" + Nombre + "') ");
        db.close();
    }


    public void ActualizarBaseDatos(int clave, String empleado, String producto, String fecha, String cantidad) {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        db.execSQL("UPDATE pedidos SET empleado='" + empleado + "', producto='" + producto + "', fecha='" + fecha + "', cantidad='" + cantidad + "' WHERE clave ='" + clave + "' ");
        db.close();
    }

    public void EliminarBaseDatos(String clave) {
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        db.execSQL("DELETE FROM pedidos WHERE clave ='" + clave + "'");
        db.close();
    }

    public String[][] selectPedidosNoAlmacena() {
        String accionSoap = "http://tempuri.org/selectPedido";
        String Metodo = "selectPedido";
        String cadena = "";
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            cadena = resultado.toString();
            cantidadRegistrosP2 = determinaRegistrosN(cadena);
            arreglo1DP2 = new String[cantidadRegistrosP2];
            arregloPedidos2 = new String[5][cantidadRegistrosP2];
            almacenaRegistrosN(cadena);
            for (int l = 0; l < arreglo1DP2.length; l++) {
                almacenaArreglo2DN(arreglo1DP2[l], l);
            }
        } catch (Exception e) {
            mensaje("Error 2...");
        }
        return arregloPedidos2;
    }

    public int determinaRegistrosN(String cadenaEntera) {
        int numeroRegistros = 0;
        for (int k = 0; k < cadenaEntera.length(); k++) {
            if (cadenaEntera.charAt(k) == '*') {
                numeroRegistros++;
            }
        }
        return numeroRegistros;
    }

    public void almacenaRegistrosN(String cadena) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadena.length(); k++) {
            if (cadena.charAt(k) == '*') {
                arreglo1DP2[posicion] = cadena.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
    }

    public void almacenaArreglo2DN(String cadenaRegistro, int pos) {
        int inicio = 0, posicion = 0;
        for (int k = 0; k < cadenaRegistro.length(); k++) {
            if (cadenaRegistro.charAt(k) == ',') {
                //arregloTupla[posicion] = cadenaRegistro.substring(inicio, k);
                arregloPedidos2[posicion][pos] = cadenaRegistro.substring(inicio, k);
                inicio = k + 1;
                posicion++;
            }
        }
        //AgregarBaseDatos(Integer.parseInt(arregloTupla[0]),arregloTupla[1],arregloTupla[2],arregloTupla[3],arregloTupla[4]);

        //mensaje("IdPedido: "+arregloTupla[0]);
        //mensaje("Cliente: "+arregloTupla[1]);
        //mensaje("Producto: "+arregloTupla[2]);
        //mensaje("Fecha: "+arregloTupla[3]);
        //mensaje("Cantidad: "+arregloTupla[4]);

    }

    public String getCelsius() {
        /*String accionSoap = "http://tempuri.org/CelsiusToFahrenheit";
        String Metodo = "CelsiusToFahrenheit";
        String cadena = "";
        PropertyInfo Cantidad = new PropertyInfo();
        Cantidad.setName("Celsius");
        Cantidad.setValue(32);
        try {

            SoapObject request = new SoapObject(namespace, Metodo);
            request.addProperty(Cantidad);
            //request.addProperty("Celsius", 30);
            mensaje("Here 00");
            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            sobre.dotNet = true;
            sobre.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(url);
            transporte.call(accionSoap, sobre);
            mensaje("Here 01");
            SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
            mensaje("Here 02");
            cadena = resultado.toString();

        } catch (Exception e) {
            mensaje("Error 2...");
        }*/
        return "";
    }

}