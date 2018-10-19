package app.models;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class PracticeMode {
    private int _position = 0;
    private Name _currentName;
    private Playlist _playlist;

    // on construction the practice mode object is passed in a playlist
    public PracticeMode(Playlist playlist){
        _playlist = playlist;
        // initialize position to 0
        _position = 0;
        _currentName = _playlist.getPlaylist().get(_position);

    }

    // increments the position forward one
    public void nextName(){
        _position = Math.floorMod(_position + 1, _playlist.getPlaylist().size());
        _currentName = _playlist.getPlaylist().get(_position);
    }

    // decrements the position backward one
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

    public void playCurrentName(){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                _currentName.playRecording();
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
                model.normaliseAndTrimAudioFile(recording);

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

    public void playRecording(Recording recording){
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                recording.playRecording();
                return null;
            }
        };
        new Thread(task).start();
    }

    public void deleteRecording(Recording recording){
        _currentName.removeUserRecording(recording);
    }

    public void compareNames(Recording recording){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                _currentName.playRecording();
                recording.playRecording();
                return null;
            }
        };
        new Thread(task).start();
    }





}
