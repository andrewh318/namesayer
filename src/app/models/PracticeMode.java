package app.models;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class PracticeMode {
    private int _position = 0;
    private Name _currentName;
    private Playlist _playlist;

    /**
     * On construction the practice mode takes in a playlist
     * @param playlist Playlist that the user selected to practice
     */
    public PracticeMode(Playlist playlist){
        _playlist = playlist;
        // initialize position to 0
        _position = 0;
        _currentName = _playlist.getPlaylist().get(_position);

    }

    /**
     * Increments the position forward by one
     */
    public void nextName(){
        _position = Math.floorMod(_position + 1, _playlist.getPlaylist().size());
        _currentName = _playlist.getPlaylist().get(_position);
    }

    /**
     * Decrements the position backward by one
     */
    public void previousName(){
        _position = Math.floorMod(_position - 1, _playlist.getPlaylist().size());
        _currentName = _playlist.getPlaylist().get(_position);
    }

    public int getNumPlaylistItems(){
        return _playlist.getNumberOfItems();
    }

    public int getPosition(){
        return _position;
    }

    public Name getCurrentName(){
        return _currentName;
    }

    public void playCurrentName(double volume){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                _currentName.playRecording(volume);
                return null;
            }
        };
        new Thread(task).start();
    }

    public Task<Void> recordName(NamesModel model){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Recording recording = _currentName.createRecordingObject();
                _currentName.record(recording);
                recording.normaliseAndTrimAudioFile();

                // user recordings are bounded to the GUI so need to add the recording on the application thread
                Platform.runLater(() -> {
                    _currentName.addUserRecording(recording);
                });

                return null;
            }
        };
        new Thread(task).start();
        return task;
    }

    public void playRecording(Recording recording, double volume){
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                recording.playRecording(volume);
                return null;
            }
        };
        new Thread(task).start();
    }

    public void deleteRecording(Recording recording){
        _currentName.removeUserRecording(recording);
    }

    public void compareNames(Recording recording, double volume){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                _currentName.playRecording(volume);
                recording.playRecording(volume);
                return null;
            }
        };
        new Thread(task).start();
    }





}
