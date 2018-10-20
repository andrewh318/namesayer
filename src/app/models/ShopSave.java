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

    public ShopSave(int money, boolean isPurple, boolean isBlue){
        this.money = money;
        this.isPurpleUnlocked = isPurple;
        this.isBlueUnlocked = isBlue;
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
}
