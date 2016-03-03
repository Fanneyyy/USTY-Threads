package com.ru.usty.elevator;

public class Person implements Runnable {

    int sourceFloor, destinationFloor, elevator;

    public Person(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    @Override
    public void run() {
        try {

            ElevatorScene.floorsIn.get(sourceFloor).acquire(); //waiting for elevator

            this.elevator = ElevatorController.elevatorPick(sourceFloor);

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
