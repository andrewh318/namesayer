package app.models;

import java.io.Serializable;

/**
 * This class is used as a proxy object to generate a serialized version of the shop state. This state can then be
 * read back in on application start and used to generate the shop object
 */
public class ShopSave implements Serializable {
    private int money;
    private boolean isPurpleUnlocked;
    private boolean isBlueUnlocked;
    private boolean isRedUnlocked;
    private boolean isAquaUnlocked;
    private boolean isYellowUnlocked;

    public ShopSave(int money, boolean isPurple, boolean isBlue, boolean isRed, boolean isAqua, boolean isYellow){
        this.money = money;
        this.isPurpleUnlocked = isPurple;
        this.isBlueUnlocked = isBlue;
        this.isRedUnlocked = isRed;
        this.isAquaUnlocked = isAqua;
        this.isYellowUnlocked = isYellow;
    }

    public int getMoney() {
        return money;
    }

    public boolean isPurpleUnlocked() {
        return isPurpleUnlocked;
    }

    public boolean isBlueUnlocked() {
        return isBlueUnlocked;
    }

    public boolean isRedUnlocked(){
        return isRedUnlocked;
    }
    public boolean isAquaUnlocked(){
        return isAquaUnlocked;
    }
    public boolean isYellowUnlocked(){
        return isYellowUnlocked;
    }
}
