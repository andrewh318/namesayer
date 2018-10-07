package app;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CombinedName extends Name {

    private List<Name> names = new ArrayList<>();

    public CombinedName(String name) {
        super(name);
    }


    @Override
    public void playRecording() {
        for (Name name : names) {
            name.playRecording();
        }
    }

    @Override
    // loop over all the names in the list, sum the length of all the best recordings
    public float getRecordingLength(){
        float totalLength = 0;
        for (Name name : names){
            float length = name.getRecordingLength();
            totalLength = totalLength + length;
        }
        return totalLength;
    }

    public void addName(Name name) {
        names.add(name);
    }

    @Override
    public Recording createRecordingObject() {
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
    public String toString() {
        return getName().replaceAll("%", " ");
    }

    // there should be no best recording for combined names
    @Override
    public Recording getBestRecording(){
        return null;
    }

    // users cannot flag custom namse
    @Override
    public boolean flagRecording(){
        // custom names cannot be flagged
        return false;
    }

}
