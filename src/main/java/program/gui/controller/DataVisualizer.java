package program.gui.controller;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 *This version is 1.0 can only read a certain type of data and maps can only take numbers as key
 *
 *New version will be able to read from excel files, draw other types of graphic, will have a better GUI
 *and will be able to read and write other type of data structures
 *
 * @author RuiMenoita
 */
public class DataVisualizer extends Application{

	private static final String FILE_DIR = "src/main/resources/";
	@FXML private HBox checkBoxPanel;
	@FXML private VBox chartPanel;
	
	private Stage window;
	private int chartId = 0;

	



	/**
	 * @param strings   data to be add to chart (Must be in the correct format)
	 * @param fileName	chart title
	 * 
	 * This method add a BarChart<String,Number> to charPanel
	 * with a title having the chart id plus the filename
	 */
	@SuppressWarnings("unchecked")
	private void drawChart(Map<String, Double> values, String name) {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		
		xAxis.setAutoRanging(true);
		yAxis.setAutoRanging(true);
		yAxis.setForceZeroInRange(true);
		
		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle(chartId+" "+name);
		
		XYChart.Series<String, Number> dataSet = new XYChart.Series<String, Number>();
		
		double[] xValues = new double[values.keySet().size()];
		
		List<String> list = new ArrayList<>();
		list.addAll(values.keySet());
		
		for (int i = 0; i < list.size(); i++) 
			xValues[i] = Double.parseDouble(list.get(i));
		
		Arrays.sort(xValues);					
		
		for (double x : xValues) 
			dataSet.getData().add(new XYChart.Data<>( Double.toString(x),values.get(Double.toString(x))));

		chart.getData().addAll(dataSet);
		
		checkBoxPanel.getChildren().add(new CheckBox(chartId+" "+name));
		chartPanel.getChildren().add(chart);
		chartId++;
	}


	
	
	
	
	/**
	 * Opens a new DataVisulaizer Window 
	 */
	@FXML
	public void openNewWindow(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("dataVisualizerScene.fxml")); 
			Parent node = loader.load();
			Scene scene = new Scene(node);
			Stage stage = new Stage();

			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Reads an file and displays data as a BarChar
	 */
	@FXML
	public void addChart(ActionEvent event) {
		FileChooser fc = new FileChooser();	
		fc.setInitialDirectory(new File(FILE_DIR));				//set's default directory
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));	//set's file type to be choose

		File selectedFile = fc.showOpenDialog(null);			//Opens file chooser

		if(selectedFile != null && selectedFile.getName().matches(".+\\.txt")) {		//validates file

			try(Scanner s = new Scanner(selectedFile)){			//opens file

				Map<String,Double> values = new HashMap<>();

				boolean error = false;	

				while(s.hasNextLine()) {
					String line = s.nextLine();

					String[] splited = line.split(":");
					
					try {
						
						if(splited.length == 2) 									//checks data format
							values.put(splited[0].trim(),Double.parseDouble(splited[1]));
						else
							throw new IllegalStateException();
						
					}catch (NumberFormatException | IllegalStateException e) {
						showErrorDialog("Wrong txt fromat\nLine error: "+line);
						error = true;
						break;
					}
				}

				if(!error) drawChart(values,selectedFile.getName());			//draw chart

			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}

		}else  showErrorDialog("Invalid file");
	}
	
	
	
	/**
	 * Removes all the selected charts on chackBoxPanel 
	 */
	@FXML
	public void removeChart(ActionEvent event) {
		checkBoxPanel.getChildren();
		List<Node> removeBox = new ArrayList<Node>();
		List<Node> removeChart = new ArrayList<Node>();
		
		for (Node checkBox : checkBoxPanel.getChildren()) {
			
			if(((CheckBox)checkBox).isSelected()) {
				
				for(Node chart : chartPanel.getChildren()) {
					
					if(((Chart)chart).getTitle().equals(((CheckBox)checkBox).getText())) {
						removeChart.add(chart);
						removeBox.add(checkBox);
					}
					
				}
			}
		}
		
		checkBoxPanel.getChildren().removeAll(removeBox);
		chartPanel.getChildren().removeAll(removeChart);
	}


	/**
	 * This method writes a text file with the data given 
	 * inside java resources
	 * 
	 * (!Attention) If file already exits this will override it
	 * 
	 * @param data data to be written
	 * @param fileName name of the file that will contain data
	 */
	public static void writeData(Map<String,Double> data, String fileName) {
		 File file = new File(FILE_DIR+fileName+".txt");

		 try {
			file.createNewFile(); 												//create new file
	        System.out.println("File was created! -> "+file.getAbsolutePath());
		} catch (IOException e1) {e1.printStackTrace();} 
		 

         try(PrintWriter pw = new PrintWriter(file)) {
        	 
        	 for (String x : data.keySet()) 
        		 pw.println(x+" : "+data.get(x));
				
         }catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * This method writes a file with the data given 
	 * inside of java resources
	 * 
	 * (!Attention) If file already exits this will override it
	 * 
	 * @param data data to be written
	 * @param fileName name of the file that will contain data
	 */
	public static void writeData(double[] data, String fileName) {
		Map<String,Double> map = new HashMap<String, Double>();
		for (int i = 0; i < data.length; i++) {
			if(map.containsKey(data[i]+"")) 
				map.put(data[i]+"",map.get(data[i]+"")+1.0);
		else 
				map.put(data[i]+"",1.0);
		}
		
		writeData(map,fileName);
	}
	
	
	
	
	/**
	 * Displays a error dialog with  @param error message
	 */
	private void showErrorDialog(String error) {
		Platform.runLater(() -> {
			Alert dialog = new Alert(AlertType.ERROR);
			dialog.setTitle("Error");
			dialog.setContentText(error);
			dialog.initOwner(window);
			dialog.show();
		});
	}

	

	/**
	 * Loads and displays dataVisualizerScene.fxml file
	 */
	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("dataVisualizerScene.fxml")); 
		Parent node = loader.load();
		Scene scene = new Scene(node);

		window.setScene(scene);
		window.show();
	}
	
	public static void show(String[] args) {
		launch(args);
	}
}
