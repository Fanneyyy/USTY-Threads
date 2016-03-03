package com.ru.usty.elevator;

import org.lwjgl.Sys;

public class Person implements Runnable {

    int sourceFloor, destinationFloor, index;

    public Person(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.index = -1;
    }

    @Override
    public void run() {
        try {
            ElevatorScene.floorsIn.get(sourceFloor).acquire(); //waiting for elevator
            index = ElevatorScene.scene.elevatorOpen;
            ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
            ElevatorScene.scene.incrementNumberOfPeopleInElevator(ElevatorScene.elevators.get(index));

            ElevatorScene.waitInElevatorMutex.get(index).acquire(); //waiting in elevator
            ElevatorScene.floorsOut.get(index).get(destinationFloor).acquire(); //waiting to leave elevator on correct floor
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElevatorScene.scene.decrementNumberOfPeopleInElevator(ElevatorScene.elevators.get(ElevatorScene.scene.elevatorOpen));

        ElevatorScene.scene.personExitsAtFloor(destinationFloor);
    }
}
