import java.util.*;

public class custodian extends Thread {
	private int currentFloor;
	
	private Random rand = new Random();
	
	public custodian(int currentFloor){
		this.currentFloor = currentFloor;
	}
	
	public void run(){
		while (true) {
			//wait for request to clean
			try {
				simulator.callCustodian.acquire();
			} catch (Exception e) {}
			System.out.println("*Custodian gets called");
			
			//press button
			synchronized (simulator.request) {
				simulator.request[currentFloor]++;
			}
			System.out.println("*Custodian calls elevator on floor "+ currentFloor);
			
			//wait for elevator
			try {
				simulator.waiting[currentFloor].acquire();
			} catch (Exception e) {}
			System.out.println("*Custodian got on at floor "+ currentFloor);
			
			//press special button to wait for everyone to get off
			simulator.CLEANING = true;
			
			//wait until elevator is empty and on first floor
			try {
				simulator.clean.acquire();
				currentFloor = 0;
			} catch (Exception e) {}
			
			//request 1st floor in elevator
			synchronized (simulator.destination) {
				simulator.destination[0]++;
			}
			System.out.println("*Custodian requests floor 0");
			
			System.out.println("*Custodian begins cleaning on floor "+ currentFloor);
			
			//clean
			try {Thread.sleep(250);} 
			catch (InterruptedException e) {}
			System.out.println("*Custodian is done cleaning");
			simulator.CLEANING = false;
			simulator.doneCleaning.release();			
			
			//wait for elevator to get to floor and leave
			try {
				simulator.getOff[0].acquire();
			} catch (Exception e) {}
			System.out.println("*Custodian has left on floor 0");
			
			//sleep
			try{
				Thread.sleep((long) (15 * Math.log(1-rand.nextDouble())/(-200)));
			} catch(Exception e){}
		}
	}
}
