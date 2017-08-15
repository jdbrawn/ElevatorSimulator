import java.util.concurrent.Semaphore;

public class elevator extends Thread {
	String direction = "UP";
	int currentFloor;
	int floorCount;
	static volatile Semaphore capacity = new Semaphore(3, false);
	
	public void run() {
		while (true) {
			switch (direction) {
			case "UP":
				for(int floor = currentFloor; floor < 5; floor++){
					if(simulator.destination[floor] > 0 || (capacity.availablePermits() > 0 && simulator.request[floor] > 0)) {
						floorCount += Math.abs(floor - currentFloor);
						if (floorCount >= 80){
							floorCount = 0;
							simulator.callCustodian.release();
						}
						try {
							Thread.sleep(Math.abs(floor - currentFloor) * 15);
						} catch (Exception e) {}
						currentFloor = floor;
						//System.out.println("*ON FLOOR " + currentFloor + " going UP");
						
						//release people who want to get off
						while(simulator.destination[floor] >  0){
							synchronized (simulator.destination) {
								simulator.destination[floor]--;
							}
							simulator.getOff[floor].release();
							capacity.release();
						}
						
						if (simulator.CLEANING && capacity.availablePermits() == 2){
							//go to first floor
							floor = 0;
							try {
								Thread.sleep(Math.abs(floor - currentFloor) * 15);
							} catch (Exception e) {}
							currentFloor = floor;
							
							simulator.clean.release();
							try {
								simulator.doneCleaning.acquire();
							} catch (InterruptedException e) {}
						}
						
						try {Thread.sleep(250);} 
						catch (InterruptedException e) {}
						
						if (!simulator.CLEANING) {
							//let people on who want to get on
							while(capacity.availablePermits() > 0 && simulator.request[floor] > 0){
								synchronized (simulator.request) {
									simulator.request[floor]--;
								}
								simulator.waiting[floor].release();
								try {
									capacity.acquire();
								} catch (InterruptedException e) {}
							}
							
							while(simulator.destination[floor] >  0){
								synchronized (simulator.destination) {
									simulator.destination[floor]--;
								}
								simulator.getOff[floor].release();
								capacity.release();
							}
						}
					}		
				}
				direction = "DOWN";
			case "DOWN":
				for(int floor = currentFloor; floor >= 0; floor--){
					if(simulator.destination[floor] > 0 || (capacity.availablePermits() > 0 && simulator.request[floor] > 0)) {
						floorCount += Math.abs(floor - currentFloor);
						if (floorCount >= 80){
							floorCount = 0;
							simulator.callCustodian.release();
						}
						try {
							Thread.sleep(Math.abs(floor - currentFloor) * 15);
						} catch (Exception e) {}
						currentFloor = floor;
						//System.out.println("*ON FLOOR " + currentFloor + " going DOWN");
						
						//release people who want to get off
						while(simulator.destination[floor] >  0){
							synchronized (simulator.destination) {
								simulator.destination[floor]--;
							}
							simulator.getOff[floor].release();
							capacity.release();
						}
						
						if (simulator.CLEANING && capacity.availablePermits() == 2){
							//go to first floor
							floor = 0;
							try {
								Thread.sleep(Math.abs(floor - currentFloor) * 15);
							} catch (Exception e) {}
							currentFloor = floor;
							
							simulator.clean.release();
							try {
								simulator.doneCleaning.acquire();
							} catch (InterruptedException e) {}
							
							while(simulator.destination[floor] >  0){
								synchronized (simulator.destination) {
									simulator.destination[floor]--;
								}
								simulator.getOff[floor].release();
								capacity.release();
							}
						}
						
						try {Thread.sleep(250);} 
						catch (InterruptedException e) {}
						
						if (!simulator.CLEANING) {
							//let people on who want to get on
							while(capacity.availablePermits() > 0 && simulator.request[floor] > 0){
								synchronized (simulator.request) {
									simulator.request[floor]--;
								}
								simulator.waiting[floor].release();
								try {
									capacity.acquire();
								} catch (InterruptedException e) {}
							}
						}
					}		
				}
				direction = "UP";
			}
		}
	}
}
