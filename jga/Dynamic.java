/**
 * The dynamic programming solution to the matrix problem.
 * Tali Peters
 * Jason Loucel
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Dynamic implements ActionListener
{
	private MatrixPanel elements;
	private ControlPanel controls;
	private int[][] dynamicValue;
	private int[][] predecessors;

	public Dynamic(MatrixPanel elements, ControlPanel controls) {
		this.elements = elements;
		this.controls = controls;
		dynamicValue = new int[MatrixPanel.SIZE][MatrixPanel.SIZE];
		predecessors = new int[MatrixPanel.SIZE][MatrixPanel.SIZE];
	}
	
	/**
	 * This will only be called when the "Dynamic"
	 * button is clicked.
	 * This is an implementation of the dynamic programming algorithm
	 * for the matrix problem. 
	 */
	public void actionPerformed(ActionEvent evt) {
		/**
		 * index represents the index of the lowest
		 * valued square for a given column.
		 */
		int index = 0;
		
		int smallest = Integer.MAX_VALUE;
		
		/**
		 * load dynamic array with values in first column
		 */
		for(int i=0; i<dynamicValue.length;i++){
			dynamicValue[i][0] = elements.valueOf(i, 0);
		}
		
		for (int i = 1; i < dynamicValue.length; i++) { 
			for(int j=0; j<dynamicValue.length;j++){
				smallest = Integer.MAX_VALUE;
				if(j!=0){
					if(dynamicValue[j-1][i-1] <smallest){
						smallest = dynamicValue[j-1][i-1];
						index = j-1;
					}
				}
				if(dynamicValue[j][i-1] < smallest){
					smallest = dynamicValue[j][i-1];
					index = j;
				}
				if(j!= dynamicValue.length-1){
					if(dynamicValue[j+1][i-1]< smallest){
						smallest = dynamicValue[j+1][i-1];
						index =j+1;
					}
				}
				dynamicValue[j][i] = elements.valueOf(j, i) + smallest;
				predecessors[j][i] = index;
			}
		}
		
		smallest = Integer.MAX_VALUE;
		for(int i=0; i<dynamicValue.length;i++){
			if(dynamicValue[i][dynamicValue.length-1]<smallest){
				smallest = dynamicValue[i][dynamicValue.length-1];
				index = i;
			}
		}
		
		for(int i = dynamicValue.length-1;i>=0;i--){
			if (elements.getColor(index, i).equals(Color.blue) 
					|| elements.getColor(index, i).equals(Color.magenta))
				elements.setValues(index, i, Color.magenta);
			else {
				elements.setValues(index, i, Color.red);
			}
			index = this.predecessors[index][i];
		}
		
		/**
		 * We are done. Now update the output for total cost.
		 */
		controls.setTotal(smallest);
	}
}
