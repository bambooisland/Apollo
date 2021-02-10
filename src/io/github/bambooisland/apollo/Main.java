package io.github.bambooisland.apollo;

import java.awt.SplashScreen;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static UIController con = null;

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			con = loader.getController();
			PlayerManager.init();
			stage.setMaximized(true);
			stage.setTitle("Apollo Music Player");
			stage.show();
			
//			-splash:data/splash.png
			SplashScreen splash = SplashScreen.getSplashScreen();
			if(splash != null) {
				splash.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
}
