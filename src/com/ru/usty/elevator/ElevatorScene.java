package com.ru.usty.elevator;

import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Random;
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
	public static final int VISUALIZATION_WAIT_TIME = 400;  //milliseconds
    public static final int MAX_IN_ELEVATOR = 6;
    public static final int MAX_IN_ELEVATOR_ON_TOP_AND_BOT = 4; // for top and bottom floor to stop starving middle

    public static ElevatorScene scene;

    // Semaphores
    public static ArrayList<Semaphore> exitedCountMutex;
    public static Semaphore personCountMutex;
    public static Semaphore elevatorOpenMutex;
    public static ArrayList<Semaphore> waitInElevatorMutex;
    public static ArrayList<Semaphore> floorsInGoingUp;
    public static ArrayList<Semaphore> floorsInGoingDown;
    public static ArrayList<ArrayList<Semaphore>> floorsOut;

    // public
    public static boolean elevatorMayStop;
    public static int elevatorOpen;
    public static ArrayList<Integer> currentFloor;
    public static ArrayList<Integer> numberOfPeopleInElevator;
    public ArrayList<Boolean> goingUp;

    // private
    private int numberOfFloors;
	private int numberOfElevators;
    private Thread elevatorThread = null;
	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you
	ArrayList<Integer> personsGoingUp;
    ArrayList<Integer> personsGoingDown;
    ArrayList<Integer> exitedCount = null;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {
        Random random = new Random(234645236); // to make elevator start at random floor (primarily for multiple elevators)

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
        elevatorOpen = 0;

        scene = this;

        floorsInGoingUp = new ArrayList<Semaphore>();
        floorsInGoingDown = new ArrayList<Semaphore>();
        for (int i = 0; i < numberOfFloors; i++) {
            floorsInGoingUp.add(new Semaphore(0));
            floorsInGoingDown.add(new Semaphore(0));

        }
        personCountMutex = new Semaphore(1);
        elevatorOpenMutex = new Semaphore(1);
        waitInElevatorMutex = new ArrayList<Semaphore>();

        goingUp = new ArrayList<Boolean>();
        numberOfPeopleInElevator = new ArrayList<Integer>();
        currentFloor = new ArrayList<Integer>();
        exitedCountMutex = new ArrayList<Semaphore>();
        floorsOut = new ArrayList<ArrayList<Semaphore>>();
        for (int i = 0 ; i < numberOfElevators; i++) {
            floorsOut.add(new ArrayList<Semaphore>());
            waitInElevatorMutex.add(new Semaphore(0));
            for (int j = 0; j < numberOfFloors; j++) {
                floorsOut.get(i).add(new Semaphore(0));

            }
        }
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;

        personCount = new ArrayList<Integer>();
        personsGoingUp = new ArrayList<Integer>();
        personsGoingDown = new ArrayList<Integer>();
        for(int i = 0; i < numberOfFloors; i++) {
            this.personCount.add(0);
            this.personsGoingUp.add(0);
            this.personsGoingDown.add(0);
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

        for (int i = 0; i < numberOfElevators; i++) {
            int elevatorSpawn =  random.nextInt((numberOfFloors - 1) + 1);
            elevatorThread = new Thread(new Elevator(i));
            numberOfPeopleInElevator.add(0);
            goingUp.add(true);
            currentFloor.add(elevatorSpawn);
            exitedCountMutex.add(new Semaphore(1));
            elevatorThread.start();
        }

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


	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {
        Person person = new Person(sourceFloor, destinationFloor);
        Thread thread = new Thread(person);
        thread.start();
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */
        if (person.goingUp) {
            ElevatorScene.scene.incrementNumberOfPeopleWaitingAtFloor(sourceFloor, true);
        } else {
            ElevatorScene.scene.incrementNumberOfPeopleWaitingAtFloor(sourceFloor, false);
        }
        return thread;  //this means that the testSuite will not wait for the threads to finish
	}

    // elevator will not go up or down if it is empty and there are no people to get
    public void goToNextFloor(int el) {
        if (goingUp.get(el) && getNumberOfPeopleInElevator(el) == 0) {
            goingUp.set(el, false);
            for (int i = getCurrentFloorForElevator(el); i < getNumberOfFloors(); i++) {
                if (isButtonPushedAtFloor(i)) {
                    goingUp.set(el, true);
                }
            }
        } else if (!goingUp.get(el) && getNumberOfPeopleInElevator(el) == 0) {
            goingUp.set(el, true);
            for (int i = getCurrentFloorForElevator(el); i >= 0; i--) {
                if (isButtonPushedAtFloor(i)) {
                    goingUp.set(el, false);
                }
            }
        }
        if (ElevatorScene.currentFloor.get(el) >= (this.numberOfFloors-1)) {
            goingUp.set(el, false);
        } else if (ElevatorScene.currentFloor.get(el) <= 0) {
            goingUp.set(el, true);
        }

        if (goingUp.get(el)) {
            currentFloor.set(el, (currentFloor.get(el) + 1));
            if (ElevatorScene.currentFloor.get(el) >= (this.numberOfFloors-1)) {
                goingUp.set(el, false);
            }
        } else {
            currentFloor.set(el, (currentFloor.get(el) - 1));
            if (ElevatorScene.currentFloor.get(el) <= 0) {
                goingUp.set(el, true);
            }
        }
    }

    public boolean areAllMiddleFloorsEmpty() {
        for (int i = 1; i < ElevatorScene.scene.getNumberOfFloors()-1; i++) {
            if (ElevatorScene.scene.isButtonPushedAtFloor(i)) {
                return false;
            }
        }
        return true;
    }

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int el) {
		return currentFloor.get(el);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int el) {
		return numberOfPeopleInElevator.get(el);
	}

    public void decrementNumberOfPeopleInElevator(int el) {
        try {
            personCountMutex.acquire();
            numberOfPeopleInElevator.set(el, (numberOfPeopleInElevator.get(el) - 1));
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incrementNumberOfPeopleInElevator(int el) {
        try {
            personCountMutex.acquire();
            numberOfPeopleInElevator.set(el, (numberOfPeopleInElevator.get(el) + 1));
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

    public int getNumberOfPeopleWaitingAtFloorGoingUp(int floor) {

        return personsGoingUp.get(floor);
    }

    public int getNumberOfPeopleWaitingAtFloorGoingDown(int floor) {

        return personsGoingDown.get(floor);
    }

    public void decrementNumberOfPeopleWaitingAtFloor(int floor, boolean goingUp) {
        try {
            personCountMutex.acquire();
            personCount.set(floor, (personCount.get(floor) - 1));
            if (goingUp) {
                personsGoingUp.set(floor, (personsGoingUp.get(floor) - 1));
            } else {
                personsGoingDown.set(floor, (personsGoingDown.get(floor) - 1));
            }
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incrementNumberOfPeopleWaitingAtFloor(int floor, boolean goingUp) {
        try {
            personCountMutex.acquire();
            personCount.set(floor, (personCount.get(floor) + 1));
            if (goingUp) {
                personsGoingUp.set(floor, (personsGoingUp.get(floor) + 1));
            } else {
                personsGoingDown.set(floor, (personsGoingDown.get(floor) + 1));
            }
            personCountMutex.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void personExitsAtFloor(int floor, int el) {
        try {

            exitedCountMutex.get(el).acquire();
            exitedCount.set(floor, (exitedCount.get(floor) + 1));
            exitedCountMutex.get(el).release();

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
