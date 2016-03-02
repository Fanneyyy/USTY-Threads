package com.ru.usty.elevator;

public class Elevator implements Runnable {

    @Override
    public void run() {
        while (true) {
            if (ElevatorScene.elevatorMayStop) {
                System.out.println("elevator may stop");
                return;
            }
            int roomInElevator = 6 - ElevatorScene.numberOfPeopleInElevator;
            if (roomInElevator > ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor)) {
                roomInElevator = ElevatorScene.scene.personCount.get(ElevatorScene.currentFloor);
            }
            for (int i = 0; i < roomInElevator; i++) {
                ElevatorScene.floorsIn.get(ElevatorScene.currentFloor).release();
            }
            waitAmoment();
            ElevatorScene.floorsIn.get(ElevatorScene.currentFloor).release(0);
            ElevatorScene.scene.goToNextFloor();
            waitAmoment();
            for (int i = 0; i < ElevatorScene.numberOfPeopleInElevator; i++) {
                ElevatorScene.waitInElevatorMutex.release();
            }
            waitAmoment();
            int peopleInElevator = ElevatorScene.numberOfPeopleInElevator;
            for (int i = 0; i < peopleInElevator; i++) {
                ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).release();
            }
            waitAmoment();
            while(ElevatorScene.floorsOut.get(ElevatorScene.currentFloor).tryAcquire()) {

            }
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
