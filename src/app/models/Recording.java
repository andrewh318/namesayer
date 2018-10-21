// Author: Andrew Hu and Vincent Tunnell

package app.models;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.*;

public class Recording {
    private String _name;
    private String _date;
    private String _path;
    private String _time;
    private String _trimmedPath;
    private int _numOfBadRecordings = 0;

    public Recording(String name, String date, String path, String trimmedPath, String time){
        _name = name;
        _date = date;
        _path = path;
        _trimmedPath = trimmedPath;
        _time = time;
        countBadRecordings();
    }

    public String getName(){
        return _name;
    }

    public String getPath(){
        return _path;
    }

    //Bad quality is appended to the string representation of the recording when the _bad field is true
    @Override
    public String toString() {
        String string = _name.replaceAll("%", " ") + " " + _date + " " + _time;
        return string;
    }


    public void playRecording(double volume) {
        BashCommand playRecording = new BashCommand("ffplay -loglevel panic -autoexit -nodisp -i '" + _trimmedPath + "'");
        playRecording.startProcess();
        try {
            playRecording.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void flagAsBad() {
        _numOfBadRecordings++;
        // write bad recording to file
        writeToFile();
    }

    public int getBadRecordings(){
        return _numOfBadRecordings;
    }

    private void writeToFile(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NamesModel.BADNAMESFILE,true));
            writer.append(this.toString());
            writer.newLine();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public float getRecordingLength(){
        // gets the recording length of the _trimmed_ files
        File audioFile = new File(_trimmedPath);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioInputStream.getFormat();

            long audioFilelength = audioFile.length();
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();

            float durationInSeconds = (audioFilelength / (frameSize * frameRate));

            return durationInSeconds;
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void countBadRecordings() {
        // Read the bad names file to see if the recording has any bad ratings associated with it
        BufferedReader br;
        // Count of how many bad recordings there are
        int numOfBadRecordings = 0;
        try {
            br = new BufferedReader(new FileReader(NamesModel.BADNAMESFILE));
            String st;
            while ((st = br.readLine()) != null) {
                // compare the recording to the current name
                if (st.equals(this.toString())){
                    numOfBadRecordings++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        _numOfBadRecordings = numOfBadRecordings;
    }

    public void normaliseAndTrimAudioFile() {
        int targetDB = -15;

        String getVolumeCommand = "ffmpeg -i ./" + _path + " -af 'volumedetect' -vn -sn -dn -f null /dev/null |& grep 'max_volume:'";
        BashCommand getVol = new BashCommand(getVolumeCommand);
        getVol.startProcess();

        try {
            getVol.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InputStream stdin = getVol.getProcess().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));

        String maxVolume = null;
        try {
            String line = reader.readLine();
            reader.close();
            maxVolume = line.substring(line.lastIndexOf(":") + 2, line.lastIndexOf("d") - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double maxVolumeInt = Double.parseDouble(maxVolume);

        double volumeChange = targetDB - maxVolumeInt;
        System.out.println("Volume change: " + volumeChange);

        String tempPath = NamesModel.TRIMMED_NORMALISED_DIRECTORY + "/output.wav";


        String normaliseCommand = "ffmpeg -i ./" + _path + " -af 'volume=" + volumeChange + "dB' " + "./" + tempPath;
        BashCommand normalise = new BashCommand(normaliseCommand);
        normalise.startProcess();
        try {
            normalise.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //trim
        String trimCommand = "ffmpeg -y -i ./" + _path + " -af silenceremove=1:0:-40dB" + " ./" + _trimmedPath;
        BashCommand trim = new BashCommand(trimCommand);
        trim.startProcess();

        try {
            trim.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String deleteTempCommand = "rm " + tempPath;
        BashCommand delete = new BashCommand(deleteTempCommand);
        delete.startProcess();

        try {
            delete.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}