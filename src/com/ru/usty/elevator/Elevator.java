package com.ru.usty.elevator;

public class Elevator implements Runnable {

    @Override
    public void run() {
        while (true) {
            if (ElevatorScene.elevatorMayStop) {
                return;
            }
            int roomInElevator = 6 - ElevatorScene.numberOfPeopleInElevator;
            if (roomInElevator > ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor)) {
                roomInElevator = ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor);
            }

            ElevatorScene.floorsIn.get(ElevatorScene.currentFloor).release(roomInElevator);
            waitAmoment();

            ElevatorScene.scene.goToNextFloor();
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.release(ElevatorScene.numberOfPeopleInElevator);
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.tryAcquire(ElevatorScene.numberOfPeopleInElevator);

            ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).release(ElevatorScene.numberOfPeopleInElevator);
            waitAmoment();
            ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).tryAcquire(ElevatorScene.numberOfPeopleInElevator);
        }
    }

    private void waitAmoment() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
