package Concurrencia;
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

// importar la librería de monitores
import es.upm.babel.cclib.Monitor;

class MultiAlmacenMon implements MultiAlmacen {
    private int capacidad = 0;
    private Producto almacenado[] = null;
    private int aExtraer = 0;
    private int aInsertar = 0;
    private int nDatos = 0;

   // TODO: declaración de atributos extras necesarios
   // para exclusión mutua y sincronización por condición
   Monitor mutex;
   Monitor.Cond cAlmacenar;
   Monitor.Cond cExtraer;

   // Para evitar la construcción de almacenes sin inicializar la
   // capacidad 
   private MultiAlmacenMon() {
	  
   }

   public MultiAlmacenMon(int n) {
      almacenado = new Producto[n];
      aExtraer = 0;
      aInsertar = 0;
      capacidad = n;
      nDatos = 0;

      // TODO: inicialización de otros atributos
      mutex = new Monitor();
	   cAlmacenar = mutex.newCond();
	   cExtraer = mutex.newCond();
   }

   private int nDatos() {
         return nDatos;
   }
   
   private int nHuecos() {
      return capacidad - nDatos;
   }

   public void almacenar(Producto[] productos) {

      // TODO: implementación de código de bloqueo para 
      // exclusión muytua y sincronización condicional 
	   mutex.enter();
	   if(nHuecos()<productos.length) {
		   cAlmacenar.await();
	   }
      // Sección crítica
      for (int i = 0; i < productos.length; i++) {
         almacenado[aInsertar] = productos[i];
         nDatos++;
         aInsertar++;
         aInsertar %= capacidad;
      }
      	
      	
      // TODO: implementación de código de desbloqueo para
      // sincronización condicional y liberación de la exclusión mutua  
      	if (nHuecos() <= (capacidad / 2))
			cExtraer.signal();
      	mutex.leave();
   }

   public Producto[] extraer(int n) {
      Producto[] result = new Producto[n];

      // TODO: implementación de código de bloqueo para exclusión
      // mutua y sincronización condicional 
      mutex.enter();
      
      if(nDatos()>n) {
    	  cExtraer.await();;
      }
      
      // Sección crítica
      for (int i = 0; i < result.length; i++) {
         result[i] = almacenado[aExtraer];
         almacenado[aExtraer] = null;
         nDatos--;
         aExtraer++;
         aExtraer %= capacidad;
      }
      
      // TODO: implementación de código de desbloqueo para
      // sincronización condicional y liberación de la exclusión mutua  
      
      if (nHuecos() >= (capacidad / 2))
			cAlmacenar.signal();
      return result;
   }
}
