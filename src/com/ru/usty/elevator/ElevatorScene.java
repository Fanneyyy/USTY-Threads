package com.ru.usty.elevator;

import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    public static Semaphore openingDoorMutex;
    public static ArrayList<Semaphore> waitInElevatorMutex;
    public static ArrayList<Semaphore> floorsIn;
    public static ArrayList<ArrayList<Semaphore>> floorsOut;
    public static ArrayList<Elevator> elevators;

    public static boolean elevatorMayStop;
    public int elevatorOpen;
    ArrayList<Integer> currentFloor;
    ArrayList<Integer> numberOfPeopleInElevator;
    ArrayList<Thread> elevatorThreads;

	private int numberOfFloors;
	private int numberOfElevators;
    ArrayList<Boolean> goingUp;
    private Thread elevatorThread = null;

	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you

    ArrayList<Integer> exitedCount = null;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

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
        elevatorOpen = -1;

        scene = this;
        floorsIn = new ArrayList<Semaphore>();
        for (int i = 0; i < numberOfFloors; i++) {
            floorsIn.add(new Semaphore(0));
        }
        floorsOut = new ArrayList<ArrayList<Semaphore>>();
        waitInElevatorMutex = new ArrayList<Semaphore>();
        for (int i = 0 ; i < numberOfElevators; i++) {
            floorsOut.add(new ArrayList<Semaphore>());
            waitInElevatorMutex.add(new Semaphore(0));
            for (int j = 0; j < numberOfFloors; j++) {
                floorsOut.get(i).add(new Semaphore(0));
            }
        }
        personCountMutex = new Semaphore(1);
        openingDoorMutex = new Semaphore(1);

        goingUp = new ArrayList<Boolean>();
        numberOfPeopleInElevator = new ArrayList<Integer>();
        currentFloor = new ArrayList<Integer>();
        elevators = new ArrayList<Elevator>();
        elevatorThreads = new ArrayList<Thread>();

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

        for (int i = 0; i < numberOfElevators; i++) {
            numberOfPeopleInElevator.add(0);
            currentFloor.add(0);
            goingUp.add(true);
            elevators.add(new Elevator(i));
            elevatorThreads.add(new Thread(elevators.get(i)));
            elevatorThreads.get(i).start();
        }
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

    public void goToNextFloor(Elevator elevator) {
        int index = elevators.indexOf(elevator);
        if (currentFloor.get(index) >= (this.numberOfFloors-1)) {
            goingUp.set(index, false);
        } else if (currentFloor.get(index) <= 0) {
            goingUp.set(index, true);
        }
        if (goingUp.get(index)) {
            currentFloor.set(index, currentFloor.get(index)+1);
        } else {
            currentFloor.set(index, currentFloor.get(index)-1);
        }
    }

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int index) {
		return currentFloor.get(index);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int index) {
		return numberOfPeopleInElevator.get(index);
	}

    public void decrementNumberOfPeopleInElevator(Elevator elevator) {
        int index = elevators.indexOf(elevator);
        try {
            personCountMutex.acquire();
            numberOfPeopleInElevator.set(index, numberOfPeopleInElevator.get(index)-1);
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incrementNumberOfPeopleInElevator(Elevator elevator) {
        int index = elevators.indexOf(elevator);
        try {
            personCountMutex.acquire();
            numberOfPeopleInElevator.set(index, numberOfPeopleInElevator.get(index)+1);
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
	public boolean isElevatorOpen(int index) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(index));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

}
