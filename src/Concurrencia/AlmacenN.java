package Concurrencia;

import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Almacen;

class AlmacenN implements Almacen {
   private int capacidad = 0;
   private Producto[] almacenado = null;
   private int nDatos = 0;
   private int aExtraer = 0;
   private int aInsertar = 0;

   Semaphore lleno=new Semaphore(0);
   Semaphore vacia=new Semaphore(capacidad);
   Semaphore mutex=new Semaphore(1);
   
 

   public AlmacenN(int n) {
      capacidad = n;
      almacenado = new Producto[capacidad];
      nDatos = 0;
      aExtraer = 0;
      aInsertar = 0;

      
   }

   public void almacenar(Producto producto) {
      
	  vacia.await();
	  mutex.await();
     
	  almacenado[aInsertar] = producto;
      nDatos++;
      aInsertar++;
      aInsertar %= capacidad;

      
      lleno.signal();
      mutex.signal();
      
   }

   public Producto extraer() {
      Producto result;

      lleno.await();
      mutex.await();
      
      result = almacenado[aExtraer];
      almacenado[aExtraer] = null;
      nDatos--;
      aExtraer++;
      aExtraer %= capacidad;

      vacia.signal();
      mutex.signal(); 
      return result;
   }
}
