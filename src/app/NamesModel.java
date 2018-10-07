// Author: Andrew Hu and Vincent Tunnell

package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NamesModel {
    private ObservableList<Name> _databaseNames = FXCollections.observableArrayList();
    private ObservableList<Name> _userNames = FXCollections.observableArrayList();
    private ObservableList<Playlist> _allPlaylists = FXCollections.observableArrayList();
    private ObservableList<Name> _combinedNames = FXCollections.observableArrayList();
    private Playbar _playbar = new Playbar();

    public static final String DATABASERECORDINGSDIRECTORY = "names";
    public static final String USERRECORDINGSDIRECTORY = "userNames";
    public static final String PLAYLISTS_DIRECTORY = "playlists";
    public static final String BADNAMESFILE = "BadNames.txt";
    public static final String DEFAULT_PLAYLIST_NAME = "Default Playlist";
    public static final String NEW_PLAYLIST_NAME = "New Playlist";
    public static final String TRIMMED_NORMALISED_DIRECTORY = "trimmedNormalised";
    public static final String COMBINED_NAMES_DIRECTORY = "combinedNames";



    public ObservableList<Name> getDatabaseNames() {
        return _databaseNames;
    }

    public ObservableList<Name> getUserNames() {
        return _userNames;
    }

    public ObservableList<Playlist> getPlaylists() {
        return _allPlaylists;
    }

    public Playbar getPlaybar() {
        return _playbar;
    }


    // renamed this from 'readDirectory' to setUp() toto prevent confusion
    public void setUp(){
        // remove all database/user names when directories are read
        // to prevent double reading of names

        clearPlaylists();

        deleteFolder(new File(NamesModel.TRIMMED_NORMALISED_DIRECTORY));

        makeDirectories();
        readDirectories();

        createErrorFile();

        setUpDefaultPlaylist();
    }

    private void makeDirectories() {
        new File(DATABASERECORDINGSDIRECTORY).mkdir();
        new File(USERRECORDINGSDIRECTORY).mkdir();
        new File(PLAYLISTS_DIRECTORY).mkdir();
        new File(COMBINED_NAMES_DIRECTORY).mkdir();

        new File(TRIMMED_NORMALISED_DIRECTORY).mkdir();
        new File(TRIMMED_NORMALISED_DIRECTORY + "/" + DATABASERECORDINGSDIRECTORY).mkdir();
        new File(TRIMMED_NORMALISED_DIRECTORY + "/" + USERRECORDINGSDIRECTORY).mkdir();
        new File(TRIMMED_NORMALISED_DIRECTORY + "/" + COMBINED_NAMES_DIRECTORY).mkdir();
    }

    private void clearPlaylists() {
        _allPlaylists.clear();
        _combinedNames.clear();
        _databaseNames.clear();
        _userNames.clear();
    }


    private void setUpDefaultPlaylist(){
        if (_allPlaylists.size() == 0){
            // if there are no playlists create a default playlist
            Playlist playlist = new Playlist(DEFAULT_PLAYLIST_NAME);
            _allPlaylists.add(playlist);
        }
    }

    private void createErrorFile() {
        File file = new File(BADNAMESFILE);
        try {
            file.createNewFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void readDirectories() {

        for (File file : new File(DATABASERECORDINGSDIRECTORY).listFiles()) {
            readDatabaseRecording(file);
        }

        for (File file : new File(USERRECORDINGSDIRECTORY).listFiles()) {
            readUserRecording(file);
        }

        for (File file : new File(COMBINED_NAMES_DIRECTORY).listFiles()) {
            readCombinedRecording(file);
        }

        for (File file : new File(PLAYLISTS_DIRECTORY).listFiles()) {
            readPlaylist(file);
        }


    }





    private Recording parseFilename(File file) {

        String fileName = file.getName();
        String directory = file.getParent();

        //Extracts information from the filename and directory
        String path = directory + "/" + fileName;
        String trimmedPath = TRIMMED_NORMALISED_DIRECTORY + "/" + path;
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        String[] parts = fileName.split("_");
        String date = parts[1];
        String time = parts[2];
        String stringName = parts[3].substring(0, 1).toUpperCase() + parts[3].substring(1);

        //Creates a new recording object with the extracted information
        return new Recording(stringName, date, path, trimmedPath, time);
    }



    private void readCombinedRecording(File file) {

        //Parse the file name and create a new recording object
        Recording recording = parseFilename(file);

        normaliseAndTrimAudioFile(recording);


        //Find the name in _combinedNames that has the same name
        CombinedName combinedName = (CombinedName) searchListOfName(_combinedNames, recording.getName());

        //If there is none, create a new combinedName
        if (combinedName == null) {
            combinedName = new CombinedName(recording.getName());
            _combinedNames.add(combinedName);
        }

        //Add the recording to the name object
        combinedName.addUserRecording(recording);

        //Split name of recording by the replacement for space, "%"
        String[] stringNames = recording.getName().split("%");

        //Find the name object corresponding to each string and add it to the combined Name
        for (String stringName : stringNames) {
            stringName = stringName.trim();
            stringName = stringName.toLowerCase();
            stringName = stringName.substring(0, 1).toUpperCase() + stringName.substring(1);

            Name name = searchListOfName(_databaseNames, stringName);

            if (name != null) {
                combinedName.addName(name);
            } else {
                System.out.println(name + "not found in database.");
            }
        }
    }

    private int countBadRecordings(Recording recording) {
        // Read the bad names file to see if the recording has any bad ratings associated with it
        BufferedReader br;
        // Count of how many bad recordings there are
        int numOfBadRecordings = 0;
        try {
            br = new BufferedReader(new FileReader(BADNAMESFILE));
            String st;
            while ((st = br.readLine()) != null) {
                // compare the recording to the current name
                if (st.equals(recording.toString())){
                    numOfBadRecordings++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numOfBadRecordings;
    }

    //Parses a wav file specified by filename, creates a recording object, and adds it to the appropriate name object
    //in the database
    private void readDatabaseRecording(File file) {

        Recording recording = parseFilename(file);

        normaliseAndTrimAudioFile(recording);

        int numBadRecordings = countBadRecordings(recording);

        recording.setBadRecordings(numBadRecordings);

        Name name = searchListOfName(_databaseNames, recording.getName());

        //if the name is not in the database so create a new Name object and add it to the database
        if (name == null) {
            name = new Name(recording.getName());
            _databaseNames.add(name);
        }

        name.addDatabaseRecording(recording);
    }

    public void readUserRecording(File file) {
        Recording recording = parseFilename(file);

        normaliseAndTrimAudioFile(recording);

        Name name = searchListOfName(_databaseNames, recording.getName());

        if (name == null) {
            System.out.println("Name not found");
        } else {
            name.addUserRecording(recording);
        }
    }

    //Method that reads a playlist file
    public List<String> readPlaylist(File file) {

        String playlistName = file.getName().substring(0, file.getName().lastIndexOf('.'));

        Playlist playlist = new Playlist(playlistName);

        List<String> invalidNames = new ArrayList<String>();

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {

                st = formatNamesString(st);

                Name name = findName(st);


                if (name == null) {
                    invalidNames.add(st);
                } else {
                    playlist.addName(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addPlaylist(playlist);

        return invalidNames;
    }



    //Method that reads in a string in the playlist or search bar format, and returns either an existing combined name,
    //an existing databasename, or a new combined name.

    public Name findName(String names) {

        //If the string is empty return null
        if (names.isEmpty()){
            return null;
        }

        //Split up the string into individual names
        String[] namesArray = names.split("%");

        //Find and return the corresponding combined name
        CombinedName combinedName;
        if (namesArray.length > 1) {
            combinedName = (CombinedName) searchListOfName(_combinedNames, names);

            if (combinedName == null) {
                combinedName = new CombinedName(names);
            } else {
                return combinedName;
            }
            //Find and return the corresponding name
        } else {
            Name name = searchListOfName(_databaseNames, names);

            if (name == null) {
                return null;
            } else {
                return name;
            }
        }

        //Add names to new combined name and if an invalid name is found, return null
        for (String nameString : namesArray) {

            Name name = searchListOfName(_databaseNames, nameString);

            if (name != null) {
                combinedName.addName(name);
            } else {
                return null;
            }
        }

        //Return the new combined name
        return combinedName;


    }

    public String formatNamesString (String names) {

        String formattedName = "";

        names = names.replaceAll("-", " ");
        String[] namesArray = names.split(" ");

        for (String name : namesArray) {
            name = name.trim();
            name = name.toLowerCase();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            formattedName = formattedName + name + "%";
        }

        formattedName = formattedName.substring(0, formattedName.length() - 1);

        System.out.println(formattedName);

        return formattedName;
    }


    public void addPlaylist(Playlist playlist){
        _allPlaylists.add(playlist);
    }

    public void deletePlaylist(Playlist playlist){
        _allPlaylists.remove(playlist);
    }

    public void savePlaylists(){
        ObservableList<Playlist> playlists = this.getPlaylists();

        // first delete all existing playlists to prevent duplication after renaming
        File[] files = new File(PLAYLISTS_DIRECTORY).listFiles();
        for(File file: files ){
            if (!file.isDirectory()){
                file.delete();
            }
        }


        // loop through list of playlists
        for (Playlist playlist : playlists){
            // for each playlist create a new BufferedReader
            ObservableList<Name> listNames = playlist.getPlaylist();
            String playlistName = playlist.getName();
            try {
                FileWriter fileWriter = new FileWriter("playlists/" + playlistName + ".txt");
                BufferedWriter out = new BufferedWriter(fileWriter);
                // for each list of names (for each playlist entry)
                for (Name name: listNames){
                    // as each playlist entry can contain multiple names, print entries names on the same line
                    out.append(name.getCleanName() + " ");
                    // after a playlist entry has finished printing, add a new line to indicate next entry
                    out.newLine();
                }
                // after playlist is complete, close the writer so a new one can be created for next playlist
                out.close();
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    public void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private Name searchListOfName(List<Name> namesList, String stringName) {

        for (Name name : namesList) {
            if (name.getName().equals(stringName)) {
                return name;
            }
        }

        return null;
    }

    public void normaliseAndTrimAudioFile(Recording recording) {

        String audioCommand = "ffmpeg -i ./" + recording.getPath() + " -af silenceremove=1:0:-40dB" + " ./" + recording.getTrimmedPath();
        BashCommand create = new BashCommand(audioCommand);
        create.startProcess();
    }
}