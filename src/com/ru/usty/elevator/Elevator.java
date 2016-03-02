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

            if (roomInElevator > ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor)) {
                roomInElevator = ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor);
            }

            System.out.println("currentFloor " + ElevatorScene.currentFloor);

            ElevatorScene.floorsIn.get(ElevatorScene.currentFloor).release(roomInElevator);
            waitAmoment();

            ElevatorScene.scene.goToNextFloor(myNumber);
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));

            ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).release(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
            waitAmoment();
            ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).tryAcquire(ElevatorScene.numberOfPeopleInElevator.get(myNumber));
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
