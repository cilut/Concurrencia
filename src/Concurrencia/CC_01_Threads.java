package Concurrencia;
//import java.util.concurrent.*;

public class CC_01_Threads extends Thread {
	private static int t_actual=0;
	private static CC_01_Threads [] ar;
	private static int [] ar_id;
	
	
	private static int n_thread=7;//N
	private long t_sleep=2000;
	
	
	
	
	
	
	
	public void run() {
		try {
			Thread.sleep(t_sleep);
		}catch(InterruptedException e) {
			
		}
		System.out.println("Thread nr: " +(ar_id[t_actual++]) );
			
	}
	

	public static void main(String[] args) throws InterruptedException{
		
		ar=new CC_01_Threads [n_thread];
		ar_id=new int [n_thread];
		for(int i=0; i<ar.length;i++) {
		
			ar[i]= new CC_01_Threads();
			ar_id[i]=i;
			
			ar[i].start();
			}
		for(int i=0;i<ar.length;i++) {
			ar[i].join();
		} 
		System.out.println("Ya ha finalizado todos los hilos");		
	}			
}
