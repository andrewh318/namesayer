// Author: Andrew Hu and Vincent Tunnell

package app.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Name implements Comparable {
    private String _name;
    private ObservableList<Recording> _databaseRecordings = FXCollections.observableArrayList();
    private ObservableList<Recording> _userRecordings = FXCollections.observableArrayList();

    public Name(String name){
        _name = name;
    }

    public String getName(){
        return _name;
    }

    public void addUserRecording(Recording recording){
        _userRecordings.add(0,recording);
    }

    public void addDatabaseRecording(Recording recording){
        _databaseRecordings.add(recording);
    }

    /**
     * Overrides toString so name will be printed correctly by list view
     * @return Returns name followed by how many repeated entries in database there are
     */
    @Override
    public String toString(){
        // if theres only a single recording associated with a name, simply just display the name
        if (_databaseRecordings.size() == 1 || _databaseRecordings.size() == 0){
            return _name;
        } else {
            String name = _name + " (" + _databaseRecordings.size() + " recordings)";
            return name;
        }
    }

    /**
     * Plays the best recording in the list of database recordings
     * @param volume Volume in which the recoding should be played at
     */
    public void playRecording(double volume){
        getBestRecording().playRecording(volume);
    }

    /**
     * Given a recording object, this method searches the list of recordings in this object and deletes it.
     * @param recording Recording the user wants to delete
     */
    public void removeUserRecording(Recording recording) {
        // delete the recording object from this name object
        _userRecordings.remove(recording);
        // delete the actual file from system
        File file = new File(recording.getPath());
        if (!file.delete()) {
            System.out.println("Could not delete file " + recording.getPath());
        }
    }

    /**
     * Creates a custom user recording object using the current date and time of the system.
     * @return
     */
    public Recording createRecordingObject() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        Date d = new Date();
        String dateAndTime = dateFormat.format(d);
        String[] dateAndTimeArray = dateAndTime.split(" ");

        String date = dateAndTimeArray[0];
        String time = dateAndTimeArray[1];
        String stringName = getName();


        String path = NamesModel.USERRECORDINGSDIRECTORY + "/se206_" + date + "_" + time + "_" + stringName + ".wav";

        String trimmedPath = NamesModel.TRIMMED_NORMALISED_DIRECTORY + "/" + path;

        return new Recording(stringName, date, path, trimmedPath, time);
    }

    /**
     * Creates an audio file in wav format based on a 5 second capture of the microphone
     * @param recording The recording object that represents the name that will be recorded
     */
    public Process record(Recording recording) {

        String audioCommand = "ffmpeg -f alsa -i default -t 5 " + "./" + recording.getPath();
        BashCommand create = new BashCommand(audioCommand);
        create.startProcess();

        return create.getProcess();
    }

    public String getCleanName() {
        return getName();
    }


    public ObservableList<Recording> getDatabaseRecordings(){
        return _databaseRecordings;
    }

    public ObservableList<Recording> getUserRecordings(){
        return _userRecordings;
    }

    // return the best database recording

    /**
     * Returns the best database recording in the list of names (determined by how many flags there are on each name)
     * @return
     */
    public Recording getBestRecording(){
        // loop through the all the recordings and find the one with the highest rating
        Recording bestRecording = _databaseRecordings.get(0);
        for (Recording recording : _databaseRecordings){
            if (recording.getBadRecordings() < bestRecording.getBadRecordings()){
                bestRecording = recording;
            }
        }
        return bestRecording;
    }

    // flags the current recording (best recording) as poor quality

    /**
     * Flags the current recording that is playing as poor quality
     * @return
     */
    public boolean flagRecording(){
        getBestRecording().flagAsBad();
        // return true as single names can be flagged
        return true;
    }

    public float getRecordingLength(){
        return getBestRecording().getRecordingLength();
    }

    public void normaliseBestRecording() {
        getBestRecording().normaliseAndTrimAudioFile();
    }

    @Override
    public int compareTo(Object o) {
        Name name = (Name) o;
        return getName().compareTo(name.getName());
    }

    /*@Override
    public int compareTo(Name name) {
        return 0;
    }*/
}