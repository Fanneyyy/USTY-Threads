package com.ru.usty.elevator;

public class Elevator implements Runnable {

    int index;
    public Elevator(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        while (true) {

            if (ElevatorScene.elevatorMayStop) {
                return;
            }
            int roomInElevator = 6 - ElevatorScene.scene.getNumberOfPeopleInElevator(index);
            int currentFloor = ElevatorScene.scene.getCurrentFloorForElevator(index);
            if (roomInElevator > ElevatorScene.scene.personCount.get(currentFloor)) {
                roomInElevator = ElevatorScene.scene.personCount.get(ElevatorScene.scene.getCurrentFloorForElevator(index));
            }

            try {
                ElevatorScene.openingDoorMutex.acquire();
                    ElevatorScene.scene.elevatorOpen = index;
                    System.out.println("Opening Elevator number: " + index);
                    ElevatorScene.floorsIn.get(currentFloor).release(roomInElevator);
                    waitAmoment();
                    ElevatorScene.floorsIn.get(currentFloor).tryAcquire(6-roomInElevator);
                ElevatorScene.openingDoorMutex.release();
                waitAmoment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ElevatorScene.scene.goToNextFloor(this);
            waitAmoment();
            currentFloor = ElevatorScene.scene.getCurrentFloorForElevator(index);

            ElevatorScene.waitInElevatorMutex.get(index).release(ElevatorScene.scene.getNumberOfPeopleInElevator(index));
            waitAmoment();

            ElevatorScene.waitInElevatorMutex.get(index).tryAcquire(ElevatorScene.scene.getNumberOfPeopleInElevator(index));

            ElevatorScene.floorsOut.get(index).get(currentFloor).release(ElevatorScene.scene.getNumberOfPeopleInElevator(index));
            waitAmoment();
            ElevatorScene.floorsOut.get(index).get(currentFloor).tryAcquire(ElevatorScene.scene.getNumberOfPeopleInElevator(index));
        }
    }

    private void waitAmoment() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
