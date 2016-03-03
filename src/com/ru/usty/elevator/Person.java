package com.ru.usty.elevator;

public class Person implements Runnable {

    int sourceFloor, destinationFloor, elevator;
    boolean goingUp;

    public Person(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        if (destinationFloor > sourceFloor) {
            this.goingUp = true;
        } else {
            this.goingUp = false;
        }
        this.elevator = -1;
    }

    @Override
    public void run() {
        try {
            while (this.elevator == -1) {
                this.elevator = ElevatorController.elevatorPick(sourceFloor, goingUp);
            }

            ElevatorScene.floorsIn.get(sourceFloor).acquire(); //waiting for elevator

            ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);

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
