package com.ru.usty.elevator;

import com.ru.usty.elevator.visualization.TestSuite;

public class ElevatorMainProgram {

	public static void main(String[] args) {

		try {

			TestSuite.startVisualization();

/***EXPERIMENT HERE BUT THIS WILL BE CHANGED DURING GRADING***/

			Thread.sleep(2000);

			TestSuite.runTest(5);

			Thread.sleep(2000);


/*************************************************************/

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.exit(0);
	}
}
