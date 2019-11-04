package program.gui.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import program.gui.controller.DataVisualizer;

public class WriteFileExample {
	
	private static final int N_TEST = 1000000;

	public static void main(String[] args) {

		double[] arr = new double[N_TEST];
		
		Map<String,Double> map = new HashMap<>();	

		for (int i = 0; i < arr.length; i++) {	//filling up the array with values
			int r = new Random().nextInt(20);
			arr[i] =  r;
		}
		
		for (int i = 0; i < arr.length; i++) {		//filling up the map with values
			if(map.containsKey(arr[i]+""))map.put(arr[i]+"",map.get(arr[i]+"")+1);
			else map.put(arr[i]+"",1.0);
		}
			
		DataVisualizer.writeData(map, "MAPTest");	//Writes into a file the map

		DataVisualizer.writeData(arr, "ArrayTest"); //Writes into a file the array
	}

}
