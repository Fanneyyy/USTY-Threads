package com.ru.usty.elevator;

public class Elevator implements Runnable {

    @Override
    public void run() {
        if (ElevatorScene.elevatorMayStop) {
            System.out.println("elevator may stop");
            return;
        }
        while (true) {
            int roomInElevator = 6 - ElevatorScene.numberOfPeopleInElevator;
            for (int i = 0; i < roomInElevator; i++) {
                ElevatorScene.floors.get(ElevatorScene.currentFloor).release();
            }
            System.out.println("number of people in elevator: " + ElevatorScene.numberOfPeopleInElevator);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ElevatorScene.scene.goToNextFloor();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < ElevatorScene.numberOfPeopleInElevator; i++) {
                ElevatorScene.waitInElevatorMutex.release();
            }
            System.out.println("number of people in elevator after: " + ElevatorScene.numberOfPeopleInElevator);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int peopleInElevator = ElevatorScene.numberOfPeopleInElevator;
            for (int i = 0; i < peopleInElevator; i++) {
                ElevatorScene.floors.get(ElevatorScene.currentFloor).release();
            }
        }
    }
}
