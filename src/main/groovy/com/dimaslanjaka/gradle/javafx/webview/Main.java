package com.dimaslanjaka.gradle.javafx.webview;

import com.dimaslanjaka.gradle.javafx.vbox.VBox;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
	VBox vb = new VBox();
	Logger log = LoggerFactory.getLogger("JavaFx.WebView");

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		vb.setId("root");

		URI uri = URI.create("http://facebook.com");
		Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
		// initialize first cookies
		headers.put("Set-Cookie", Collections.singletonList("name=value"));

		WebView browser = new WebView();
		WebEngine engine = browser.getEngine();
		String url = "http://google.com/";
		engine.setJavaScriptEnabled(true);
		engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");

		engine.setOnError(new EventHandler<WebErrorEvent>() {
			@Override
			public void handle(WebErrorEvent event) {
				log.error(event.getMessage());
			}
		});
		engine.setOnStatusChanged(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
				log.info(event.getData());
			}
		});
		engine.getLoadWorker().stateProperty().addListener(
						(observable, oldValue, newValue) -> {
							for(HttpCookie httpCookie : new java.net.CookieManager().getCookieStore().get(uri)) {
								System.out.println("test> " + " # " + httpCookie.toString() + " - "+httpCookie.getSecure());
							}
						}
		);
		engine.load(url);

		vb.setPadding(new Insets(0, 0, 0, 0));
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER);
		vb.getChildren().addAll(browser);

		Scene scene = new Scene(vb);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
