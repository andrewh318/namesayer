package app;

import java.io.File;
import java.util.ArrayList;
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
}
