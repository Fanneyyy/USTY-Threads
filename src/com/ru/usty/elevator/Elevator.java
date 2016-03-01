package com.ru.usty.elevator;

public class Elevator implements Runnable {

    private int numberOfPeople;
    public Elevator() {
        numberOfPeople = 0;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void decrementNumberOfPeople() {
        numberOfPeople--;
    }

    public void incrementNumberOfPeople() {
        numberOfPeople++;
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
