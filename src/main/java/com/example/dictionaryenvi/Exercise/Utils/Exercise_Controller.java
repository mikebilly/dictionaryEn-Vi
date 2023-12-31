package com.example.dictionaryenvi.Exercise.Utils;

import com.backend.Exercise.Utils.Exercise;
import com.example.dictionaryenvi.HomePage.HomePage;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.dictionaryenvi.Exercise.ExerciseScene.ExerciseScene_Controller.*;
import static com.example.dictionaryenvi.Exercise.Utils.GlobalProperties.*;

public abstract class Exercise_Controller<T extends Exercise> extends Scene_Controller {
    public void goToHome(MouseEvent mouseEvent) {
        HomePage.moveToHomePageNavbar(mouseEvent);
    }

    public void goToGame(MouseEvent mouseEvent) {
//        HomePage.moveToExerciseNavbar(mouseEvent);
    }

    public void goToLearn(MouseEvent mouseEvent) {
        HomePage.moveToLearnTopicWordNavbar(mouseEvent);
    }

    public void clickUserInfo(MouseEvent mouseEvent) {
        HomePage.clickUserInfoNavbar(mouseEvent);
    }

    public void clickLearnWordOfDay(MouseEvent mouseEvent) {
        HomePage.moveToLearnWordOfDayNavbar(mouseEvent);
    }

    public void clickEdict(MouseEvent mouseEvent) {
        HomePage.moveToDictionaryNavbar(mouseEvent);
    }

    @FXML
    protected Label timerLabel;

    protected TimerManager timerManager;

    @FXML
    protected Label question;

    @FXML
    protected Label scoreLabel;

    @FXML
    protected Label questionIndexLabel;

    private MediaPlayer correctMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/dictionaryenvi/Exercise/common/assets/correct.mp3").toString()));
    private MediaPlayer incorrectMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/dictionaryenvi/Exercise/common/assets/incorrect.mp3").toString()));
    private MediaPlayer hoverMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/dictionaryenvi/Exercise/common/assets/hover.wav").toString()));
    private MediaPlayer congratulationsMediaPlayer = new MediaPlayer(new Media(getClass().getResource("/com/example/dictionaryenvi/Exercise/common/assets/congratulations.mp3").toString()));

    protected void playHover() {
        hoverMediaPlayer.stop();
        hoverMediaPlayer.seek(Duration.ZERO);
        hoverMediaPlayer.play();
    }

    protected void playEffect(MediaPlayer player) {
        stopAllEffects();
        player.play();
    }

    protected void stopAllEffects() {
        correctMediaPlayer.stop();
        incorrectMediaPlayer.stop();
    }

    protected void playCorrectEffect() {
        playEffect(correctMediaPlayer);
    }

    protected void playIncorrectEffect() {
        playEffect(incorrectMediaPlayer);
    }

    protected abstract void extraInit();

    protected String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    protected abstract void closeStage();

    @FXML
    public void initialize() {
        extraInit();

        if (globalTimerLabel == null) {
            globalTimerLabel = new Label();
        }
        if (globalTimerManager == null) {
            globalTimerManager = new TimerManager(globalTimerLabel, globalDurations, this::handleTimeout);
        }

        timerLabel.textProperty().bind(globalTimerLabel.textProperty());
        timerManager = globalTimerManager;

        generateQuestion();
    }

    private void handleTimeout() {
        System.out.println("HANDLE TIMEOUT HERE");
        saveUserScore();

        Platform.runLater(() -> {
            showScoreAfterFinish();
        });
    }

    protected void showAlert(boolean isCorrect, String correctAnswer, String extraContent) {
        String title;
        String content;
        if (isCorrect) {
            title = "Correct!";
            content = "Congrats, you got a new point!";
        } else {
            title = "Incorrect";
            content = "Sorry, the correct answer is: " + correctAnswer + ".";
        }

        content += "\n\n" + extraContent;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        ImageView imageView = new ImageView();
        String imagePath = isCorrect ? "/com/example/dictionaryenvi/Exercise/common/assets/correct.png" : "/com/example/dictionaryenvi/Exercise/common/assets/incorrect.png";
        imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
        double fitSize = 50;
        imageView.setFitWidth(fitSize);
        imageView.setFitHeight(fitSize);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        contentLabel.getStyleClass().add("content");

        HBox hbox = new HBox(imageView, contentLabel);
        hbox.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane(hbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(scrollPane);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);

        if (isCorrect) {
            dialogPane.getStyleClass().add("correct-alert");
        } else {
            dialogPane.getStyleClass().add("incorrect-alert");
        }

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButtonType);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        if (okButton != null) {
            okButton.getStyleClass().add("ok-button");
        } else {
            System.err.println("OK button not found in the dialog");
        }

        Node titleNode = dialogPane.lookup(".header-panel");
        if (titleNode instanceof Label) {
            Label titleLabel = (Label) titleNode;
            titleLabel.getStyleClass().add("header-panel");
        }

        String cssFile = getClass().getResource("/com/example/dictionaryenvi/Exercise/common/CSS/Alert.css").toExternalForm();
        dialogPane.getStylesheets().add(cssFile);

        final double[] xOffset = new double[1];
        final double[] yOffset = new double[1];

        EventHandler<MouseEvent> mousePressedHandler = event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();

            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.setOpacity(0.7);
        };

        EventHandler<MouseEvent> mouseDraggedHandler = event -> {
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset[0]);
            stage.setY(event.getScreenY() - yOffset[0]);
        };

        EventHandler<MouseEvent> mouseReleasedHandler = event -> {
            Stage stage = (Stage) dialogPane.getScene().getWindow();
            stage.setOpacity(1.0);
        };

        dialogPane.setOnMousePressed(mousePressedHandler);
        dialogPane.setOnMouseDragged(mouseDraggedHandler);
        dialogPane.setOnMouseReleased(mouseReleasedHandler);

        scrollPane.setOnMousePressed(mousePressedHandler);
        scrollPane.setOnMouseDragged(mouseDraggedHandler);
        scrollPane.setOnMouseReleased(mouseReleasedHandler);
        hbox.setOnMousePressed(mousePressedHandler);
        hbox.setOnMouseDragged(mouseDraggedHandler);
        hbox.setOnMouseReleased(mouseReleasedHandler);

        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.showAndWait();
    }

    protected abstract String getUserAnswer();

    protected abstract void submitAnswer();

    protected abstract Stage getStage();

    protected abstract void setQuestion(T exercise);

    protected abstract void generateQuestion();

    protected void setScoreLabel() {
        this.scoreLabel.setText("Score: " + globalScore);
    }

    protected void setQuestionIndexLabel() {
        this.questionIndexLabel.setText("Question: " + (globalExerciseIndex) + "/" + globalExerciseListSize);
    }

    Stage dummyStage;

    protected void showScoreAfterFinish() {
        saveUserScore();

        timerManager.stopTimer();

        closeOpenAlerts();

        playEffect(congratulationsMediaPlayer);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Time is up!");
        dialog.setHeaderText(null);

        Label contentLabel = new Label("Congratulations! Your score is: " + globalScore);

        contentLabel.getStyleClass().add("content-label");

        dialog.getDialogPane().setContent(contentLabel);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);

        String cssFile = getClass().getResource("/com/example/dictionaryenvi/Exercise/common/CSS/ScoreAfterFinish.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(cssFile);

        dialog.initStyle(StageStyle.UNDECORATED);

        dialog.setOnHiding(dialogEvent -> {
            goBack(null);
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.schedule(() -> {
                Platform.runLater(() -> {
                    globalIsRunningExercise = false;
                    globalShowingDictation = false;
                    globalShowingMultipleChoice = false;
                });
                executorService.shutdown();
            }, 1, TimeUnit.SECONDS);
        });

        dialog.showAndWait();
    }

    private void closeOpenAlerts() {
        List<Window> openWindows = javafx.stage.Window.getWindows();

        openWindows.stream()
                .filter(window -> window instanceof Stage)
                .map(stage -> (Stage) stage)
                .filter(stage -> stage.getOwner() == null && stage.getModality() == Modality.APPLICATION_MODAL)
                .forEach(Stage::close);
    }

    @FXML
    protected void goBack(MouseEvent event) {
        saveUserScore();

        timerManager.resetTimer(globalDurations);
        timerManager.stopTimer();

        Platform.runLater(() -> {
            globalIsRunningExerciseProperty.set(false);
            globalShowingMultipleChoiceProperty.set(false);
            globalShowingDictationProperty.set(false);
        });

        Stage curStage;
        if (event == null) {
            curStage = getStage();
            if (curStage == null) {
                curStage = exerciseStage;
            }
        } else {
            curStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        }

        if (curStage == null) {
            System.err.println("Current stage is null");
            return;
        }
        simpleSetScene("/com/example/dictionaryenvi/HomePage/HomePage.fxml", curStage);
    }

}
