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
            ElevatorScene.semaphore1.acquire(); //waiting
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Person thread finished");
        ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
    }
}