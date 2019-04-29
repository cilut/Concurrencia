package Concurrencia;

public class CC_02_Carrera extends Thread  {
	private static int n = 0;
	private int nDecInc;
	public CC_02_Carrera (int nDecInc) {
		super();
		this.nDecInc=nDecInc;
	}
	
	public void decremento_n() {
		--n;
	}
	
	public void incremento_n() {
		++n;
	}
	
	public void run() {
		for(int i=0; i<nDecInc; i++) {
			
			decremento_n();
			incremento_n();

			
		}
	}
		
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//numM son el numero de  threats
		//numN numero de incrementos/decrementos
		int numM=20, numN=20;
		CC_02_Carrera [] hilo=new CC_02_Carrera[numM];
		for(int i=0; i<numM; i++){
			
		
			hilo[i]=new CC_02_Carrera(numN);
			hilo[i].start();
			
	}
		for(int i=0; i<numM; i++) {
			try {
				hilo[i].join() ;
				}
				catch ( InterruptedException e ) {
				}
			
		}
		System.out.println("Finalizado, el valor de n es: "+n);
	}
}

