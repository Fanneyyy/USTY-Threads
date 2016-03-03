package com.ru.usty.elevator;

public class Elevator implements Runnable {
    int myNumber;


    public Elevator(int elevatorNumber) {
        this.myNumber = elevatorNumber;
    }
    @Override
    public void run() {
        while (true) {
            if (ElevatorScene.elevatorMayStop) {
                return;
            }
            int roomInElevator = 6 - ElevatorScene.numberOfPeopleInElevator.get(myNumber);
            waitAmoment();

            if (ElevatorScene.scene.goingUp.get(myNumber)) {
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
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
