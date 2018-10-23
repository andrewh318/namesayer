

package app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import javax.sound.sampled.*;

/**
 * This class is the controller for the test mic screen. It is responsible for displaying the current microphone level
 * to the user.
 * The majority of the code in this class is from the URL below with some changes.
 * https://stackoverflow.com/questions/15870666/calculating-microphone-volume-trying-to-find-max
 * @author: Andrew Hu and Vincent Tunnell
 */
public class TestMicController {
    private TargetDataLine _line;
    @FXML private JFXButton closeButton;
    @FXML private JFXProgressBar micLevel;

    /**
     * Initialize the mic input and enter the main while loop that checks the mic level from the system.
     */
    public void initialize(){
        setUpMicInput();
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                while (true){
                    if (isCancelled()){
                        break;
                    } else {
                        double micLevel = getMicLevel();
                        updateProgress(micLevel, 1);
                    }
                }
                return null;
            }
        };
        micLevel.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }


    /**
     * Calculates the rms level of the mic based on a byte array created in setUpMicInput
     */
    public double calculateRMSLevel(byte[] audioData){
        // audioData might be buffered data read from a data line
        long lSum = 0;
        for(int i=0; i<audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for(int j=0; j<audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return ((Math.pow(averageMeanSquare,0.5d) + 0.5) / 100);
    }

    /**
     * Reads data from the microphone
     */
    public void setUpMicInput(){

        // Open a TargetDataLine for getting microphone input & sound level
        _line = null;
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100, 16, 2, 4, 44100, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); //     format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("The line is not supported.");
        }
        // Obtain and open the line.
        try {
            _line = (TargetDataLine) AudioSystem.getLine(info);
            _line.open(format);
            _line.start();
        } catch (LineUnavailableException ex) {
            System.out.println("The TargetDataLine is Unavailable.");
        }
    }

    /**
     * Gets the current level of the mic
     * @return the mic level as a double
     */
    public double getMicLevel(){
        byte[] bytes = new byte[_line.getBufferSize() / 5];
        _line.read(bytes, 0, bytes.length);
        return calculateRMSLevel(bytes);
    }

    @FXML
    private void onCloseButtonClicked(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
