package com.ru.usty.elevator;

public class Elevator implements Runnable {

    @Override
    public void run() {
        if (ElevatorScene.elevatorMayStop) {
            System.out.println("elevator may stop");
            return;
        }
        for (int i = 0; i < 16; i++) {
            ElevatorScene.floors.get(ElevatorScene.currentFloor).release();
        }
    }
}
