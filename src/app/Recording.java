// Author: Andrew Hu and Vincent Tunnell

package app;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class Recording {
    private String _name;
    private String _date;
    private String _path;
    private String _time;
    private String _fullPath;
    private Boolean _bad = false;

    public Recording(String name, String date, String path, String fullPath, String time){
        _name = name;
        _date = date;
        _path = path;
        _fullPath = fullPath;
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

    public String getFullPath() { return _fullPath; }

    //Bad quality is appended to the string representation of the recording when the _bad field is true
    @Override
    public String toString() {
        String string = _name + " " + _date + " " + _time;
        if (_bad) {
            string = string + " (Bad Quality)";
        }
        return string;
    }

    //Uses AudioClip and syncLatch to play a recording and not allow overlap when this method is called twice on the
    //same thread
    public void playRecording() {


        CountDownLatch syncLatch = new CountDownLatch(1);

        InputStream in = null;
        try {
            File audioFile = new File(_path);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            Clip audioClip = (Clip) AudioSystem.getClip();

            audioClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    syncLatch.countDown();
                }
            });

            audioClip.open(audioStream);
            audioClip.start();

            syncLatch.await();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setBad() {
        _bad = true;
    }
}