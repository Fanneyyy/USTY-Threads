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
            boolean middleFloors = false;
            for (int i = 1; i < ElevatorScene.scene.getNumberOfFloors()-1; i++) {
                if (ElevatorScene.scene.isButtonPushedAtFloor(i)) { //TODO Mér dettur í hug að þetta sé til ignora ekki miðjuhæðina?
                    middleFloors = true;
                }
            }
            if (middleFloors &&
                    (ElevatorScene.currentFloor.get(myNumber) == 0 ||
                    ElevatorScene.currentFloor.get(myNumber) == (ElevatorScene.scene.getNumberOfFloors()-1))) {
                roomInElevator = ElevatorScene.MAX_IN_ELEVATOR_ON_TOP_AND_BOT - ElevatorScene.numberOfPeopleInElevator.get(myNumber);
            } else {
                roomInElevator = ElevatorScene.MAX_IN_ELEVATOR - ElevatorScene.numberOfPeopleInElevator.get(myNumber);
            }
            waitAmoment();

            if (roomInElevator >= 0) {              //TODO Ef að það eru fleiri en 1 hæð?
                try {
                    ElevatorScene.elevatorOpenMutex.acquire();
                    ElevatorScene.elevatorOpen = myNumber;
                    if (ElevatorScene.scene.goingUp.get(myNumber)) {
                        if (roomInElevator > ElevatorScene.scene.personsGoingUp.get(ElevatorScene.currentFloor.get(myNumber))) {
                            roomInElevator = ElevatorScene.scene.personsGoingUp.get(ElevatorScene.currentFloor.get(myNumber));  //TODO What is this?
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
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(myNumber).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(myNumber).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
        }
    }
    private void waitAmoment() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
