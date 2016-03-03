package com.ru.usty.elevator;

public class Person implements Runnable {

    int sourceFloor, destinationFloor, elevator;
    boolean goingUp;

    public Person(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.elevator = 0;
        // Added to know whether a person is going up or down
        if (destinationFloor > sourceFloor) {
            this.goingUp = true;
        } else {
            this.goingUp = false;
        }
    }

    @Override
    public void run() {
        try {
            if (goingUp) {
                ElevatorScene.floorsInGoingUp.get(sourceFloor).acquire(); //waiting for elevator, going up
            } else {
                ElevatorScene.floorsInGoingDown.get(sourceFloor).acquire(); //waiting for elevator, going down
            }
            this.elevator = ElevatorScene.elevatorOpen; // find the elevator the person entered

            ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor, goingUp);
            ElevatorScene.scene.incrementNumberOfPeopleInElevator(elevator);

            ElevatorScene.waitInElevatorMutex.get(elevator).acquire(); //waiting in elevator

            ElevatorScene.floorsOut.get(elevator).get(destinationFloor).acquire(); //waiting to leave elevator on correct floor

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElevatorScene.scene.personExitsAtFloor(destinationFloor, elevator);
        ElevatorScene.scene.decrementNumberOfPeopleInElevator(elevator);
    }
}
