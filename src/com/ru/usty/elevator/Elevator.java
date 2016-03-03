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
            System.out.println(roomInElevator);
            waitAmoment();

            if (roomInElevator > ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor.get(myNumber))) {
                roomInElevator = ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor.get(myNumber));
            }

            System.out.println("Room In Elevator " + roomInElevator);

            ElevatorScene.floorsIn.get(ElevatorScene.currentFloor.get(myNumber)).release(roomInElevator);
            waitAmoment();

            ElevatorScene.scene.goToNextFloor(myNumber);
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(myNumber).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(myNumber).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();
            System.out.println("myNumber: " + myNumber);
            System.out.println("currentFloor: " + ElevatorScene.currentFloor.get(myNumber));
            System.out.println("myNumber: " + myNumber);

            ElevatorScene.floorsOut.get(myNumber).get(ElevatorScene.currentFloor.get(myNumber)).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
        }
    }
    private void waitAmoment() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
