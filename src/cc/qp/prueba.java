package cc.qp;

import java.util.LinkedList;

public class prueba {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final QuePasa sharedResource = new QuePasaMonitor();
	    // final QuePasa sharedResource = new QuePasaCSP();
	    
	    // usuarios 
	    LinkedList<Integer> usarios = new LinkedList<Integer>();
	    
	    
	    LinkedList<String> grupos = new LinkedList<String>();
	    for (int i=0; i<numUsarios; i++) {
	      grupos.add("grupo_"+i);
	    }
	    
	    // Creacion de los usuarios del sistema
	    for (int i=0; i<numUsarios; i++) {
	      LinkedList<Integer> otrosUsarios = new LinkedList<Integer>();
	      for (int j=0; j<numUsarios; j++) {
	        if (i != j) otrosUsarios.add(j);
	      }
	      
	      SimulatedEscritor escritor =
	        new SimulatedEscritor
	        (i,
	         sharedResource,
	         grupos.get(i),
	         otrosUsarios);
	     
	    /*SimulatedLector lector =
	        new SimulatedLector
	        (i,
	         sharedResource,
	          escritor);
	      */escritor.start();
	      //lector.start();
	    }
	  }
	}

}
