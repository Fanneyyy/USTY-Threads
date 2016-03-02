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
            System.out.println("Hello inside person");

            ElevatorScene.floorsIn.get(sourceFloor).acquire(); //waiting for elevator
            System.out.println("Hello inside person 2");

            ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
            System.out.println("Hello inside person 3");


            ElevatorScene.scene.incrementNumberOfPeopleInElevator(ElevatorController.elevatorPick());
            System.out.println("Hello inside person 4");

            ElevatorScene.waitInElevatorMutex.acquire(); //waiting in elevator
            System.out.println("Hello inside person 5");

            ElevatorScene.floorsOut.get(destinationFloor).acquire(); //waiting to leave elevator on correct floor
            System.out.println("Hello inside person 6");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElevatorScene.scene.personExitsAtFloor(destinationFloor);
        ElevatorScene.scene.decrementNumberOfPeopleInElevator(0);
    }
}
