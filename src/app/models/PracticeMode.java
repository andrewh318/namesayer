package app.models;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;

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

    public int getNumPlaylistItems(){ return _playlist.getNumberOfItems(); }

    public int getPosition(){ return _position; }

    public Name getCurrentName(){ return _currentName; }

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

    /**
     * Calls methods to create the recording object, record audio, and changes the behaviour of the record button.
     * @param recordButton the record button
     * @return the task that this method runs on
     */
    public Task<Void> recordName(Button recordButton){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Recording recording = _currentName.createRecordingObject();
                Process recordProcess = _currentName.record(recording);
                //Change the action of the record button to destroy the process as it is now a stop button
                recordButton.setOnAction(e -> {
                    recordProcess.destroy();
                });
                try {
                    recordProcess.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
            protected Void call() {
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
            protected Void call() {
                _currentName.playRecording();
                recording.playRecording();
                return null;
            }
        };
        new Thread(task).start();
    }
}
