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
            ElevatorScene.floors.get(sourceFloor).acquire(); //waiting
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Person thread finished");
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
        System.out.println("Climing up elevator shaft");
        ElevatorScene.scene.personExitsAtFloor(destinationFloor);
    }
}
