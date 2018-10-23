package app.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents an instance of a combined name (custom names). It is responsible for transferring commands
 * to the individual recording objects.
 * @author: Andrew Hu and Vincent Tunnell
 */
public class CombinedName extends Name {
    private List<Name> _names = new ArrayList<>();

    public CombinedName(String name){ super(name); }

    @Override
    /**
     * Plays each recording consecutively
     */
    public void playRecording(){
        for (Name name : _names) {
            name.playRecording();
        }
    }

    @Override
    /**
     * Loops over all the names in teh list of names, sums the length of all the best recordings
     */
    public float getRecordingLength(){
        float totalLength = 0;
        for (Name name : _names){
            float length = name.getRecordingLength();
            totalLength = totalLength + length;
        }
        return totalLength;
    }

    public void addName(Name name){ _names.add(name); }

    @Override
    /**
     * Creates a recording object that encapsulates the state of a recording for 'this' name
     */
    public Recording createRecordingObject(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date d = new Date();
        String dateAndTime = dateFormat.format(d);
        String[] dateAndTimeArray = dateAndTime.split(" ");

        String date = dateAndTimeArray[0];
        String time = dateAndTimeArray[1];
        String stringName = getName();


        String path = NamesModel.COMBINED_NAMES_DIRECTORY + "/se206_" + date + "_" + time + "_" + stringName + ".wav";

        String trimmedPath = NamesModel.TRIMMED_NORMALISED_DIRECTORY + "/" + path;

        return new Recording(stringName, date, path, trimmedPath, time);
    }


    @Override
    /**
     * Overriding toString as when combined names are read in from file, need to replace % with spaces.
     */
    public String toString(){ return super.getName().replaceAll("%", " "); }

    /**
     * There should be no best recording for combined names
     * @return Always returns null so class that calls this method knows it is a combined name
     */
    @Override
    public Recording getBestRecording(){ return null; }

    /**
     * Users can not flag combined names
     * @return Always returns false.
     */
    @Override
    public boolean flagRecording(){
        // custom names cannot be flagged
        return false;
    }

    /**
     * Gets a clean representation of the combined name with spaces between names
     * @return clean representation of combined name
     */
    @Override
    public String getCleanName(){ return getName().replaceAll("%", " "); }

    @Override
    /**
     * Loops through each name, and normalizes each one
     */
    public void normaliseBestRecording(){
        for (Name name : _names) {
            name.getBestRecording().normaliseAndTrimAudioFile();
        }
    }
}
