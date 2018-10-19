// Author: Andrew Hu and Vincent Tunnell

package app.models;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

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
    public void playRecording(double volume) {
        Media media = new Media(new File(_trimmedPath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
        mediaPlayer.setAutoPlay(true);
        new MediaView(mediaPlayer);
        try {
            Thread.sleep((long) media.getDuration().toMillis());
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

    public void normaliseAndTrimAudioFile() {

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

        double volumeChange = 0 - maxVolumeInt;

        String tempPath = NamesModel.TRIMMED_NORMALISED_DIRECTORY + "/output.wav";

        if (volumeChange != 0) {
            String normaliseCommand = "ffmpeg -i ./" + _path + " -af 'volume=" + volumeChange + "dB' " + "./" + tempPath;
            BashCommand normalise = new BashCommand(normaliseCommand);
            normalise.startProcess();
            try {
                normalise.getProcess().waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        String trimCommand = "ffmpeg -y -i ./" + tempPath + " -af silenceremove=1:0:-30dB" + " ./" + _trimmedPath;
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