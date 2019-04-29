package Concurrencia;
// Exclusión mutua con espera activa.
//
// Intentar garantizar la exclusión mutua en sc_inc y sc_dec sin
// utilizar más mecanismo de concurrencia que el de la espera activa
// (nuevas variables y bucles).
//
// Las propiedades que deberán cumplirse:
// - Garantía mutual exclusión (exclusión mútua): nunca hay dos
//   procesos ejecutando secciones críticas de forma simultánea.
// - Ausencia de deadlock (interbloqueo): los procesos no quedan
//   "atrapados" para siempre.
// - Ausencia de starvation (inanición): si un proceso quiere acceder
//   a su sección crítica entonces es seguro que alguna vez lo hace.
// - Ausencia de esperas innecesarias: si un proceso quiere acceder a
//   su sección crítica y ningún otro proceso está accediendo ni
//   quiere acceder entonces el primero puede acceder.
//
// Ideas:
// - Una variable booleana en_sc que indica que algún proceso está
//   ejecutando en la sección crítica?
// - Una variable booleana turno?
// - Dos variables booleanas en_sc_inc y en_sc_dec que indican que un
//   determinado proceso (el incrementador o el decrementador) está
//   ejecutando su sección crítica?
// - Combinaciones?

class CC_03_MutexEA {
	
   static final int N_PASOS = 10000;
    
  
   volatile static int n = 0;
   volatile static boolean en_scI=false;
   volatile static boolean en_scD=false;
   volatile static boolean turno_inc=true;
   
   // Sección no crítica
   
   static void sc_inc() {
      n++;
   }

   static void sc_dec() {
      n--;
   }

   
   static class Incrementador extends Thread {
      public void run () {
         for (int i = 0; i < N_PASOS; i++) {
           
        	CC_03_MutexEA.en_scD=true;
        	while (CC_03_MutexEA.en_scI) {
        		CC_03_MutexEA.en_scD=false;
        		CC_03_MutexEA.en_scD=true;
        	}
            	
        	sc_inc();
        	CC_03_MutexEA.en_scD=false;
         }
      }
   }

  
   static class Decrementador extends Thread {
      public void run () {
         for (int i = 0; i < N_PASOS; i++) {

        	CC_03_MutexEA.en_scI = true;
            while (CC_03_MutexEA.en_scD) {
            	CC_03_MutexEA.en_scI=false;
            	CC_03_MutexEA.en_scI=true;
            }
            sc_dec();
            CC_03_MutexEA.en_scI = false;
         }
      }
   }

   public static final void main(final String[] args)
      throws InterruptedException
      
   {
      // Creamos las tareas
	   Thread t1 = new Incrementador();
      Thread t2 = new Decrementador();

      // Las ponemos en marcha
      t1.start();
      t2.start();

      // Esperamos a que terminen
      t1.join();
      t2.join();

      // Simplemente se muestra el valor final de la variable:
      System.out.println(n);
      System.out.println("El calor de nuestra variable es: "+n);
	  	
      
      
   }
}
