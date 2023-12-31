package com.example.dictionaryenvi.Exercise.Exercises.Dictation;

import com.backend.Exercise.Exercises.Dictation.Dictation;

import com.example.dictionaryenvi.Exercise.Utils.Exercise_Controller;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import static com.example.dictionaryenvi.Exercise.ExerciseScene.ExerciseScene_Controller.*;
import static com.example.dictionaryenvi.Exercise.Utils.GlobalProperties.*;

public class Dictation_Controller extends Exercise_Controller<Dictation> {
    @FXML
    private Button submitButton;

    @FXML
    private ImageView audioIcon;

    @FXML
    private TextField answerTextField;

    private Dictation exercise;

    private MediaPlayer mediaPlayer;

    @Override
    protected Stage getStage() {
        return (Stage) answerTextField.getScene().getWindow();
    }

    @Override
    protected void closeStage() {
        Stage stage = getStage();
        stage.hide();
    }

    private void setQuestion(String question) {
        this.question.setText(question);
    }

    @Override
    protected void setQuestion(Dictation dictation) {
        setQuestion(dictation.getSentenceWithBlank());
        this.exercise = dictation;
    }

    @Override
    protected void extraInit() {
        submitButton.setOnMouseEntered(event -> {
            playHover();
        });

        audioIcon.setOnMouseEntered(event -> {
            playHover();
        });
    }

    @Override
    protected void generateQuestion() {
        timerManager.startTimer();
        setQuestion(globalCurrentDictation);
        setScoreLabel();
        setQuestionIndexLabel();
        resetButtonColor(submitButton);
    }

    @FXML
    public void playAudio() {
        String audioUrl = exercise.getAudioTranslation().getAudioLink();
        Media media = new Media(audioUrl);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            stopBreathingAnimation();
        }

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnPlaying(() -> startBreathingAnimation());

        mediaPlayer.setOnEndOfMedia(() -> {
            stopBreathingAnimation();
        });

        mediaPlayer.play();
    }

    private ScaleTransition breathingAnimation;

    private void startBreathingAnimation() {
        if (breathingAnimation == null) {
            audioIcon.setScaleX(1.0);
            audioIcon.setScaleY(1.0);

            breathingAnimation = new ScaleTransition(Duration.seconds(1), audioIcon);
            breathingAnimation.setByX(0.5);
            breathingAnimation.setByY(0.5);
            breathingAnimation.setCycleCount(ScaleTransition.INDEFINITE);
            breathingAnimation.setAutoReverse(true);
            breathingAnimation.play();
        }
    }

    private void stopBreathingAnimation() {
        if (breathingAnimation != null) {
            breathingAnimation.setOnFinished(event -> {
                audioIcon.setScaleX(1.0);
                audioIcon.setScaleY(1.0);
            });

            breathingAnimation.stop();
            breathingAnimation = null;
        }
    }

    @Override
    protected String getUserAnswer() {
        return answerTextField.getText();
    }

    private void setButtonColor(Button button, Color color) {
        String style = "-fx-background-color: " + toRGBCode(color) + ";";
        style += "-fx-effect: dropshadow(gaussian, " + toRGBCode(color) + ", 10, 0, 0, 0);";

        button.setStyle(style);
    }

    private void resetButtonColor(Button button) {
        button.setStyle("");
    }

    @Override
    @FXML
    protected void submitAnswer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        stopBreathingAnimation();

        String userAnswer = getUserAnswer();
        timerManager.stopTimer();
        System.out.println("Submitted answer: " + userAnswer);
        if (userAnswer != null) {
            if (exercise.isCorrect(userAnswer)) {
                playCorrectEffect();
                globalScore += 1;
//                saveUserScore();
                System.out.println("Correct!");
                setButtonColor(submitButton, Color.GREEN);
                showAlert(true, exercise.getCorrectAnswer(), exercise.getAudioTranslation().getTranslation());

            } else {
                playIncorrectEffect();
                setButtonColor(submitButton, Color.RED);
                System.out.println("Incorrect, the correct answer is " + exercise.getCorrectAnswer() + ".");
                showAlert(false, exercise.getCorrectAnswer(), exercise.getAudioTranslation().getTranslation());
            }
            generateQuestion();
        } else {
            System.out.println("Please enter valid answer");
        }

        answerTextField.clear();
        globalShowingDictation = false;

        Platform.runLater(() -> {
            System.out.println("set run later in dictation");
            globalShowingDictationProperty.set(false);
            if (!globalIsRunningExerciseProperty.get()) {
                showScoreAfterFinish();
            }
        });
        System.out.println("CLOSING DICTATION");
        System.out.println("global Dic FALSE " + globalShowingDictation);
    }
}
