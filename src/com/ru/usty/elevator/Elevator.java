package com.ru.usty.elevator;

public class Elevator implements Runnable {
    int myNumber, roomInElevator;

    public Elevator(int elevatorNumber) {
        this.myNumber = elevatorNumber;
        this.roomInElevator = ElevatorScene.MAX_IN_ELEVATOR;
    }
    @Override
    public void run() {
        while (true) {
            if (ElevatorScene.elevatorMayStop) {
                return;
            }
            // The code will check if all middle floors are empty,
            // if not the code will lower the number of people the elevator will release
            // the MAX_IN_ELEVATOR_ON_TOP_AND_BOT can be changed as needed in ElevatorScene
            // Post: if middle floors are not empty and the elevator is on top or bottom
            // it will take in fewer people, preventing a starve in the middle floors
            boolean middleFloorsEmpty = ElevatorScene.scene.areAllMiddleFloorsEmpty();
            if (!middleFloorsEmpty &&
                    (ElevatorScene.currentFloor.get(myNumber) == 0 ||
                    ElevatorScene.currentFloor.get(myNumber) == (ElevatorScene.scene.getNumberOfFloors()-1))) {
                roomInElevator = ElevatorScene.MAX_IN_ELEVATOR_ON_TOP_AND_BOT - ElevatorScene.numberOfPeopleInElevator.get(myNumber);
            } else {
                roomInElevator = ElevatorScene.MAX_IN_ELEVATOR - ElevatorScene.numberOfPeopleInElevator.get(myNumber);
            }
            waitAmoment();

            if (roomInElevator > 0) { // if elevator is full, it doesn't need to release any people
                try {
                    ElevatorScene.elevatorOpenMutex.acquire(); // should make sure that only one elevator is releasing each person
                    ElevatorScene.elevatorOpen = myNumber; // updates the public value in ElevatorScene so a person can know which elevator it's entering
                    if (ElevatorScene.scene.goingUp.get(myNumber)) {
                        // Checks if there are enough people available to fill the elevator, otherwise it will take the remaining ones
                        if (roomInElevator > ElevatorScene.scene.personsGoingUp.get(ElevatorScene.currentFloor.get(myNumber))) {
                            roomInElevator = ElevatorScene.scene.personsGoingUp.get(ElevatorScene.currentFloor.get(myNumber));
                        }
                        ElevatorScene.floorsInGoingUp.get(ElevatorScene.currentFloor.get(myNumber)).release(roomInElevator);
                    } else {
                        if (roomInElevator > ElevatorScene.scene.personsGoingDown.get(ElevatorScene.currentFloor.get(myNumber))) {
                            roomInElevator = ElevatorScene.scene.personsGoingDown.get(ElevatorScene.currentFloor.get(myNumber));
                        }
                        ElevatorScene.floorsInGoingDown.get(ElevatorScene.currentFloor.get(myNumber)).release(roomInElevator);
                    }
                    waitAmoment();
                    ElevatorScene.elevatorOpenMutex.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitAmoment();
            }

            ElevatorScene.scene.goToNextFloor(myNumber);

            ElevatorScene.waitInElevatorMutex.get(myNumber).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(myNumber).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            // acquires again the people left in the elevator
            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
        }
    }
    // is needed for visualization and to prevent concurrency issues
    private void waitAmoment() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
