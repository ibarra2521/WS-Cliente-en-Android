package com.nayelilopez.nivardoibarra.wsclient;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {
    private String[] datos;
    private EditText clave;
    private Spinner producto;
    private EditText fecha;
    private EditText cantidad;
    private Spinner cliente;
    private String[][] clientes, productos;
    //private String[] soloClientes = new String[4];
    private boolean insertarRegistros = false;
    private FuncionesBD objetoFunciones;

    //
    private Thread thread;
    private Handler handler = new Handler();
    private String fromCurrency = "USD";
    private String toCurrency = "LKR";
    private String webResponse = "";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //
        objetoFunciones = new FuncionesBD(this);
        clave = (EditText) findViewById(R.id.edttClave);
        cliente = (Spinner) findViewById(R.id.spnnrCliente);
        producto = (Spinner) findViewById(R.id.spnnrProducto);
        fecha = (EditText) findViewById(R.id.edttFecha);
        cantidad = (EditText) findViewById(R.id.edttCantidad);

        //TODO Mejorar el manejo de la base de datos al crearla e ingresar Clientes, Productos y Pedidos

        //For testing
        //AgregarClienteProductos ();
        //objetoFunciones.selectPedidos();
        /////////////objetoFunciones.AgregarCliente(11, "Nivardo Ibarra");
        /*objetoFunciones.AgregarProducto(1, "MacBook Pro");
        objetoFunciones.AgregarProducto(2, "iMac");
        objetoFunciones.AgregarProducto(3, "MacBook Air");
        objetoFunciones.AgregarBaseDatos(1, "El mismo empleado", "MacBook Pro", "18/11/15", "30");
        objetoFunciones.AgregarBaseDatos(2, "El mismo empleado", "MacBook Air", "18/11/15", "20");*/
        //objetoFunciones.subirRegistros();

        cargarCliente();
        cargarProducto();
        actualizar();

        // CON ESTO SE PRUEBA EL CONSUMO DE UN SERVICIO WEB
        //startWebAccess();
    }

    public void onAdd(View botton){
        mensaje("onAdd");
        try{
            objetoFunciones.AgregarBaseDatos(Integer.valueOf(clave.getText().toString()),clientes[cliente.getSelectedItemPosition()][0],productos[producto.getSelectedItemPosition()][0],fecha.getText().toString(),cantidad.getText().toString());
            //mensaje("evento boton agregar");
            mensaje("Registro Agregado Exitosamente");
            actualizar();
            clave.setText("");
            fecha.setText("");
            cantidad.setText("");
        }catch(Exception e)
        {
            mensaje("Error al Agregar Pedido");
        }
    }

    public void descargar(View botton) {
        //mensaje("descargar");
        try
        {
            //mensaje("evento boton descargar ");

            //mensaje("descargando ped...");
            objetoFunciones.BorraBaseTablas();
            objetoFunciones.selectClientes();
            objetoFunciones.selectProductos();
            objetoFunciones.selectPedidos();
            cargarCliente();
            cargarProducto();
            mensaje("Descarga Exitosa");
            actualizar();
        }catch (Exception e)
        {
            mensaje("Error al Descargar los Catalogos");
        }



    }

    public void subir(View botton)
    {
        mensaje("subir");
        try{
            //mensaje("evento boton subir");
            objetoFunciones.subirRegistros();
            mensaje("Actualizacion de Pedidos Exitosamente");
        }catch(Exception e)
        {
            mensaje("Error al Subir los Pedidos a la BD");
        }
    }

    public void  AgregarClienteProductos (){
        if(insertarRegistros != true){
            SQLHelper sqlhelper = new SQLHelper (this);
            SQLiteDatabase db = sqlhelper.getWritableDatabase();
            db.execSQL("INSERT INTO clientes (idCliente,nombre,domicilio,telefono) VALUES ('1','Luis','zaragoza #35','7471123456')");
            db.execSQL("INSERT INTO productos (idProducto,nombre,cantidad) VALUES ('1','fanta','500')");
            db.execSQL("INSERT INTO clientes (idCliente,nombre,domicilio,telefono) VALUES ('2','carlos','galeana #5','7471123478')");
            db.execSQL("INSERT INTO productos (idProducto,nombre,cantidad) VALUES ('2','galletas maria','700')");
            db.close();
        }
        insertarRegistros = true;
    }

    public void actualizar(){
        mensaje("actualizar");
        SQLHelper sqlhelper = new SQLHelper (this);
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        //TextView textView4 = (TextView) findViewById(R.id.textView4);
        //String cadena ="Pedidos : \n  Pedido  -  Cliente - Producto - Fecha - Cantidad\n";
        String []args = new String []{"%"};
        int i = 0;
        Cursor c = db.rawQuery("select clave,empleado, producto, fecha, cantidad  from pedidos where empleado like ? ",args);
        final int elementos = c.getCount()*5;
        datos = new String[elementos];

        for(int h=1; h<=elementos; h++)
            datos[h-1] = "0";
        if (c.moveToFirst()) {
            do {
                String clave = c.getString(0);
                String cliente = c.getString(1);
                String producto = c.getString(2);
                String fecha = c.getString(3);
                String cantidad = c.getString(4);
                // cadena = cadena+clave+"  -  "+ cliente+"  -  "+ producto+"  -  "+ fecha+"  -  "+ cantidad+"\n";
                datos[i]=""+clave; datos[i+1]=""+cliente; datos[i+2]=""+producto; datos[i+3]=""+fecha; datos[i+4]=""+cantidad;
                i=i+5;
            } while(c.moveToNext());
            //textView4.setText(cadena);
            ArrayAdapter<String> adaptador =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);

            final GridView grdOpciones = (GridView)findViewById(R.id.gridView);

            grdOpciones.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent,
                                                   android.view.View v, int position, long id) {
                            //lblMensaje.setText("Seleccionado: " + datos[position]);
                            mostrarRegistro(position, elementos);
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                            //lblMensaje.setText("");
                        }
                    });
            grdOpciones.setAdapter(adaptador);
            //mensaje("Pedidos Actuales...");

        }
        db.close();
    }

    public void mostrarRegistro(int posicion, int elements){
        elements = elements / 5;
        int numeroElemento = 0;
        for(int j = 1; j <= elements; j++){
            if((posicion >= numeroElemento) && (posicion <= numeroElemento+4))
                break;
            numeroElemento = numeroElemento +5;
        }
        clave.setText(datos[numeroElemento]);
        //empleado.setText(datos[numeroElemento+1]);
        //producto.setText(datos[numeroElemento+2]);
        fecha.setText(datos[numeroElemento+3]);
        cantidad.setText(datos[numeroElemento+4]);
        cliente.setFocusable(true);
        cliente.setSelection(devolverPosicionElemento(datos[numeroElemento+1],clientes));
        producto.setSelection(devolverPosicionElemento(datos[numeroElemento+2],productos));
    }

    public void onEliminar(View botton){
        mensaje("onEliminar");
        try{
            //mensaje("evento boton eliminar ");

            objetoFunciones.EliminarBaseDatos(clave.getText().toString());
            mensaje("Registro Eliminado Correctamente");
            actualizar();
            //empleado.setText("");
        }catch(Exception e)
        {
            mensaje("Error al Eliminar el Pedido");
        }
    }



    public void onRefresh(View botton){
        mensaje("onRefresh");
        try{
            //mensaje("evento boton Refrescar");

            objetoFunciones.ActualizarBaseDatos(Integer.valueOf(clave.getText().toString()),clientes[cliente.getSelectedItemPosition()][0],productos[producto.getSelectedItemPosition()][0],fecha.getText().toString(),cantidad.getText().toString());
            mensaje("Registro Actualizado Existosamente");
            actualizar();
            clave.setText("");
            fecha.setText("");
            cantidad.setText("");
        }catch(Exception e)
        {
            mensaje("Error al Actualizar Pedido");
        }
    }

    public int devolverPosicionElemento(String id, String [][] arregloBusqueda){
        int posicion = 0;
        for(int k = 0; k < arregloBusqueda.length; k ++){
            if(id.equals(arregloBusqueda[k][0]))
                posicion = k;
        }
        return posicion;
    }

    public void cargarCliente(){
        //objetoFunciones.selectClientes();
        mensaje("cargarCliente");
        SQLHelper sqlhelper = new SQLHelper (this);
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from clientes", null);
        int j=0;
        final int elementos = c.getCount();
        clientes = new String[elementos][2];
        if (c.moveToFirst()) {
            do {
                String idCliente = c.getString(0);
                String nombre = c.getString(1);
                clientes[j][0]=""+idCliente;
                clientes[j][1]=""+nombre;
                j++;
            } while(c.moveToNext());
        }
        ArrayAdapter<String> adaptador =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cortaArreglo(clientes, elementos));
        //ArrayAdapter<String> adaptador =
        //  	new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, objetoFunciones.selectClientes());

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cliente.setAdapter(adaptador);
        db.close();
    }

    public void cargarProducto(){
        mensaje("cargarProducto");
        SQLHelper sqlhelper = new SQLHelper (this);
        SQLiteDatabase db = sqlhelper.getWritableDatabase();
        Cursor prod = db.rawQuery("select * from productos", null);
        int h=0;
        final int cantidadProducto = prod.getCount();
        //mensaje("Numero de clientes: "+cantidadProducto);
        productos = new String[cantidadProducto][2];
        if (prod.moveToFirst()) {
            do {
                String idProducto = prod.getString(0);
                String nombre = prod.getString(1);
                productos[h][0]=""+idProducto;
                productos[h][1]=""+nombre;
                h++;
            } while(prod.moveToNext());
        }
        ArrayAdapter<String> adaptador =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cortaArreglo(productos, cantidadProducto));
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        producto.setAdapter(adaptador);
        db.close();

    }

    public String[] cortaArreglo(String[][] tablaCliente, int numeroDeElementos){
        String [] nombreClientes =  new String [numeroDeElementos];
        for(int k = 0; k < numeroDeElementos; k ++){
            nombreClientes [k] = tablaCliente[k][1];
        }
        return nombreClientes;
    }
    private void mensaje(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    //
    public void startWebAccess(){
        thread = new Thread(){
            public void run(){
                String NAMESPACE = "http://www.webserviceX.NET/";
                String URL = "http://www.webservicex.net/CurrencyConvertor.asmx";
                String SOAP_ACTION = "http://www.webserviceX.NET/ConversionRate";
                String METHOD_NAME = "ConversionRate";

                try{
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    PropertyInfo fromProp =new PropertyInfo();
                    fromProp.setName("FromCurrency");
                    fromProp.setValue(fromCurrency);
                    fromProp.setType(String.class);
                    request.addProperty(fromProp);

                    PropertyInfo toProp =new PropertyInfo();
                    toProp.setName("ToCurrency");
                    toProp.setValue(toCurrency);
                    toProp.setType(String.class);
                    request.addProperty(toProp);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
                    webResponse = response.toString();
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                handler.post(createUI);
            }
        };

        thread.start();
    }

    final Runnable createUI = new Runnable() {

        public void run(){
            //textView.setText("1 "+fromCurrency+" = "+webResponse+" "+toCurrency);
            mensaje("1 "+fromCurrency+" = "+webResponse+" "+toCurrency);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
