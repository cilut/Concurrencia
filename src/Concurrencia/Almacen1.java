package Concurrencia;
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Almacen;

// TODO: importar la clase de los semáforos.

/**
 * Implementación de la clase Almacen que permite el almacenamiento
 * de producto y el uso simultáneo del almacen por varios threads.
 */
class Almacen1 implements Almacen {
   // Producto a almacenar: null representa que no hay producto
   private Producto almacenado = null;
   Semaphore Espacios = new Semaphore(1);
   Semaphore Productos = new Semaphore(0);
   Semaphore Mutex = new Semaphore(1);
   // TODO: declaración e inicialización de los semáforos
   // necesarios

   public Almacen1() {
   }

   public void almacenar(Producto producto) {
      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder almacenar.

      // Sección crítica
	  Espacios.await();
	  almacenado = producto;
      Productos.signal();

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder extraer.
   }

   public Producto extraer() {
      Producto result;

      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder extraer.

      // Sección crítica
      Productos.await();
      result = almacenado;
      almacenado = null;
      Espacios.signal();

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder almacenar.

      return result;
   }
}
