package com.ru.usty.elevator;


public class ElevatorController {
    public static int elevatorPick(int floor, boolean goingUp) {
        int numberOfElevators = ElevatorScene.scene.getNumberOfElevators();
        int tempValue = 6;
        int pickedElevator = 0;
        for (int i = 0; i < numberOfElevators; i++) {
            if (tempValue > ElevatorScene.numberOfPeopleInElevator.get(i) && ElevatorScene.scene.getCurrentFloorForElevator(i) == floor && ElevatorScene.scene.goingUp.get(i) == goingUp) {
                tempValue = ElevatorScene.numberOfPeopleInElevator.get(i);
                pickedElevator = i;
            }
        }
        return pickedElevator;
    }
}
