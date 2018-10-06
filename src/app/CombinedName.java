package app;

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

    public void addName(Name name) {
        names.add(name);
    }
}
