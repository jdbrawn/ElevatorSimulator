import java.util.*;

public class occupant extends Thread {
	private int currentFloor;
	private int ID;
	private int nextFloor;
	
	private Random rand = new Random();
	
	public occupant(int ID, int currentFloor, int nextFloor){
		this.ID = ID;
		this.currentFloor = currentFloor;
		this.nextFloor = nextFloor;
	}
	
	public void run(){
		while (true) {
			//press button
			synchronized (simulator.request) {
				simulator.request[currentFloor]++;
			}
			System.out.println("Occupant "+ ID + " calls elevator on floor "+ currentFloor);
			
			//wait for elevator
			try {
				simulator.waiting[currentFloor].acquire();
			} catch (Exception e) {}
			System.out.println("Occupant "+ ID + " got on at floor "+ currentFloor);
			
			//request random floor in elevator
			synchronized (simulator.destination) {
				simulator.destination[nextFloor]++;
			}
			System.out.println("Occupant "+ ID + " requests floor "+ nextFloor);
			
			//wait for elevator to get to floor and leave
			try {
				simulator.getOff[nextFloor].acquire();
			} catch (Exception e) {}
			System.out.println("Occupant "+ ID + " has left on floor "+ nextFloor);
			
			//sleep then reset variables
			try{
				Thread.sleep((long) (15 * Math.log(1-rand.nextDouble())/(-200)));
			} catch(Exception e){}
			currentFloor = nextFloor;
			nextFloor = rand.nextInt(5);
		}
	}
}
