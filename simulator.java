import java.util.concurrent.Semaphore;
import java.util.*;

public class simulator {
	static volatile int[] request = new int[5];
	static volatile int[] destination = new int[5];
	
	static volatile Semaphore[] waiting = new Semaphore[5];
	static volatile Semaphore[] getOff = new Semaphore[5];
	
	static occupant[] oc = new occupant[20];
	static Random rand = new Random();
	
	static volatile Semaphore callCustodian = new Semaphore(0, false);
	static volatile Semaphore clean = new Semaphore(0, false);
	static volatile Semaphore doneCleaning = new Semaphore(0, false);
	static boolean CLEANING;
	
	public static void main(String[] args){
		for(int i = 0; i < 20; i++){
			oc[i] = new occupant(i, rand.nextInt(5), rand.nextInt(5));
			oc[i].start();
		}
		for(int i = 0; i < 5; i++){
			waiting[i] = new Semaphore(0,false);
			getOff[i] = new Semaphore(0,false);
		}
		
		elevator el = new elevator();
		el.start();
		
		custodian c = new custodian(0);
		c.start();
	}
}
