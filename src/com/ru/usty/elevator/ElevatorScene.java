package com.ru.usty.elevator;

import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

    public static ElevatorScene scene;

    // Semaphores
    public static Semaphore exitedCountMutex;
    public static Semaphore personCountMutex;
    public static ArrayList<Semaphore> floors;

    public static boolean elevatorMayStop;
    public static int currentFloor;
    public static int numberOfPeopleInElevator;

	private int numberOfFloors;
	private int numberOfElevators;
    private Thread elevatorThread = null;

	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you

    ArrayList<Integer> exitedCount = null;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

        // TODO Ask the teacher!!!
        elevatorMayStop = true;
        if (elevatorThread != null) {
            if (elevatorThread.isAlive()) {
                try {
                    elevatorThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        elevatorMayStop = false;

        scene = this;
        floors = new ArrayList<Semaphore>();
        for (int i = 0; i < numberOfFloors; i++) {
            Semaphore temp = new Semaphore(0);
            floors.add(temp);
        }
        personCountMutex = new Semaphore(1);

        currentFloor = 0;
        numberOfPeopleInElevator = 0;
        elevatorThread = new Thread(new Elevator());
        elevatorThread.start();
		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 * 
		 * Also add any other code that initializes
		 * your system for a new run
		 * 
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
		}
		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

        Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
        thread.start();
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */

        ElevatorScene.scene.incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
        return thread;  //this means that the testSuite will not wait for the threads to finish
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int el) {

		return ElevatorScene.currentFloor;
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int el) {
		
		return ElevatorScene.numberOfPeopleInElevator;
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

    public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
        try {
            personCountMutex.acquire();
            personCount.set(floor, (personCount.get(floor) - 1));
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
        try {
            personCountMutex.acquire();
            personCount.set(floor, (personCount.get(floor) + 1));
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void personExitsAtFloor(int floor) {
        try {

            exitedCountMutex.acquire();
            exitedCount.set(floor, (exitedCount.get(floor) + 1));
            exitedCountMutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getExitedCountAtFloor(int floor) {
        if(floor < getNumberOfFloors()) {
            return exitedCount.get(floor);
        }
        else {
            return 0;
        }
    }

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

}
