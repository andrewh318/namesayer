// Author: Andrew Hu and Vincent Tunnell

package app.models;

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
    }

    public String getName(){
        return _name;
    }

    public String getDate(){
        return _date;
    }

    public String getPath(){
        return _path;
    }

    public String getTrimmedPath() { return _trimmedPath; }

    //Bad quality is appended to the string representation of the recording when the _bad field is true
    @Override
    public String toString() {
        String string = _name.replaceAll("%", " ") + " " + _date + " " + _time;
        return string;
    }

    //Uses AudioClip and syncLatch to play a recording and not allow overlap when this method is called twice on the
    //same thread
    public void playRecording() {
        String audioCommand = "ffplay -loglevel panic -autoexit -nodisp -i " + _trimmedPath;

        BashCommand cmd = new BashCommand(audioCommand);
        cmd.startProcess();
        try {
            cmd.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void flagAsBad() {
        _numOfBadRecordings++;
        // write bad recording to file
        writeToFile();
    }

    public void setBadRecordings(int num){
        _numOfBadRecordings = num;
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
        // gets the recording length of the TRIMMED files
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
}