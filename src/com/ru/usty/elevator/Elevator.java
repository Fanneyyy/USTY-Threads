package com.ru.usty.elevator;

public class Elevator implements Runnable {

    private int numberOfPeople, currentFloor;
    public Elevator() {
        numberOfPeople = 0;
        currentFloor = 0;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    @Override
    public void run() {
        if (ElevatorScene.elevatorMayStop) {
            System.out.println("elevator may stop");
            return;
        }
        for (int i = 0; i < 16; i++) {
            try {
                ElevatorScene.elevatorWaitMutex.acquire();
                ElevatorScene.floor1.release();
                ElevatorScene.elevatorWaitMutex.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
