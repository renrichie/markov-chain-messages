package view;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TwitterFX extends Application {

	private Scene scene;
	
	public static void main (String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		// Create the scene
        stage.setResizable(false);
        stage.setTitle("Twitter Web View");
        scene = new Scene(new Browser(),750,500, Color.web("#666970"));
        stage.setScene(scene);
        //scene.getStylesheets().add("css/style.css");        
        stage.show();
	}

}

class Browser extends Region {
	
	private final WebView browser = new WebView();
	private final WebEngine webEngine = browser.getEngine();
	
	public Browser() {
		// Apply the styles
        getStyleClass().add("browser");
        
        // Load the web page
        File f = new File("html/twitter.html");
        webEngine.load(f.toURI().toString());
        
        // Add the web view to the scene
        getChildren().add(browser);
	}
	
	private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override 
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override 
    protected double computePrefWidth(double height) {
        return 750;
    }
 
    @Override 
    protected double computePrefHeight(double width) {
        return 500;
    }
}
