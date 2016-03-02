package com.ru.usty.elevator;

public class Person implements Runnable {

    int sourceFloor, destinationFloor;

    public Person(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    @Override
    public void run() {
        try {
            ElevatorScene.floorsIn.get(sourceFloor).acquire(); //waiting for elevator
            ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
            ElevatorScene.scene.incrementNumberOfPeopleInElevator(0);
            ElevatorScene.waitInElevatorMutex.acquire(); //waiting in elevator
            ElevatorScene.floorsOut.get(destinationFloor).acquire(); //waiting to leave elevator on correct floor
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElevatorScene.scene.personExitsAtFloor(destinationFloor);
        ElevatorScene.scene.decrementNumberOfPeopleInElevator(0);
    }
}
