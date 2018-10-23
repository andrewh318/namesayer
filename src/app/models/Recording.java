// Author: Andrew Hu and Vincent Tunnell

package app.models;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
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

    private void countBadRecordings() {
        // Read the bad names file to see if the recording has any bad ratings associated with it
        BufferedReader br;
        // Count of how many bad recordings there are
        int numOfBadRecordings = 0;
        try {
            br = new BufferedReader(new FileReader(NamesModel.BAD_NAMES_FILE));
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

    public void playRecording() {
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(NamesModel.BAD_NAMES_FILE,true));
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




    /**
     * Normalises a recording by max volume to -15db. The new file will always have a peak volume of -15db.
     */
    private void normaliseRecording(){
        //Define the targetDb to be -15db.
        int targetDB = -15;

        //This command gets the max volume of the audio file as a string in this format:
        // [Parsed_volumedetect_0 @ 0x56456000be20] max_volume: -20.9 dB
        String getVolumeCommand = "ffmpeg -i ./" + _path + " -af 'volumedetect' -vn -sn -dn -f null /dev/null |& grep 'max_volume:'";
        BashCommand getVol = new BashCommand(getVolumeCommand);
        getVol.startProcess();
        try {
            getVol.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Read the input stream to get the string printed in the terminal by the command above
        InputStream stdin = getVol.getProcess().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));

        String maxVolume = null;
        try {
            String line = reader.readLine();
            reader.close();
            //Parse the string to extract only the number of decibels
            maxVolume = line.substring(line.lastIndexOf(":") + 2, line.lastIndexOf("d") - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Calculate the difference between the max volume and the target max volume
        double maxVolumeInt = Double.parseDouble(maxVolume);
        double volumeChange = targetDB - maxVolumeInt;

        //Apply the volume command to increase or decrease the audio file by the difference to the target
        String normaliseCommand = "ffmpeg -y -i ./" + _path + " -af 'volume=" + volumeChange + "dB' " + "./" + NamesModel.TEMP_PATH;
        BashCommand normalise = new BashCommand(normaliseCommand);
        normalise.startProcess();
        try {
            normalise.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a new file containing a version of the recording with all silence quieter than -35db removed.
     */
    private void trimRecording(){
        //Remove all sounds quieter than -35dB
        String trimCommand = "ffmpeg -y -i ./" + NamesModel.TEMP_PATH + " -af silenceremove=1:0:-35dB" + " ./" + _trimmedPath;
        BashCommand trim = new BashCommand(trimCommand);
        trim.startProcess();

        try {
            trim.getProcess().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void normaliseAndTrimAudioFile(){
        normaliseRecording();
        trimRecording();
    }

    public String getName(){
        return _name;
    }

    public String getPath(){
        return _path;
    }

    //Bad quality is appended to the string representation of the recording when the _bad field is true
    @Override
    public String toString(){
        String string = _name.replaceAll("%", " ") + " " + _date + " " + _time;
        return string;
    }
}