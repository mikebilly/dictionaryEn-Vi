package com.example.dictionaryenvi.Exercise.Utils;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Scene_Controller {

    public Scene_Controller() {

    }

    public void enter_newScene(String FXML_Path, String title, MouseEvent event, boolean slideFromRight) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_Path));
        try {
            Parent root = loader.load();

            Scene currentScene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) currentScene.getWindow();

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
            ImageView snapshot = new ImageView(currentScene.getRoot().snapshot(snapshotParameters, null));

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(snapshot, root);

            double sceneWidth = currentScene.getWidth();
            root.translateXProperty().set(slideFromRight ? sceneWidth : -sceneWidth);

            Scene newScene = new Scene(rootPane, sceneWidth, currentScene.getHeight());

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), snapshot);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                stage.setScene(newScene);
                stage.setTitle(title);

                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), root);
                transition.setToX(0);
                transition.play();

                transition.setOnFinished(eventTransition -> {
                    Timeline timeline = new Timeline();
                    KeyValue keyValue = new KeyValue(root.translateXProperty(), slideFromRight ? -30 : 30);
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(70), keyValue);
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.setAutoReverse(true);
                    timeline.setCycleCount(2);
                    timeline.play();
                });
            });
            fadeOut.play();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enter_newScene(String FXML_Path, String title, boolean slideFromRight) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_Path));
        try {
            Parent root = loader.load();

            Stage stage = new Stage();  // Create a new stage

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
            ImageView snapshot = new ImageView(root.snapshot(snapshotParameters, null));

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(snapshot, root);

            double sceneWidth = 960;
            double sceneHeight = 576;
            root.translateXProperty().set(slideFromRight ? sceneWidth : -sceneWidth);

            Scene newScene = new Scene(rootPane, sceneWidth, sceneHeight);

            stage.setScene(newScene);
            stage.setTitle(title);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), snapshot);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                stage.show();

                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), root);
                transition.setToX(0);
                transition.play();

                transition.setOnFinished(eventTransition -> {
                    Timeline timeline = new Timeline();
                    KeyValue keyValue = new KeyValue(root.translateXProperty(), slideFromRight ? -30 : 30);
                    KeyFrame keyFrame = new KeyFrame(Duration.millis(70), keyValue);
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.setAutoReverse(true);
                    timeline.setCycleCount(2);
                    timeline.play();
                });
            });
            fadeOut.play();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void simpleSetScene(String FXML_Path, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_Path));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
