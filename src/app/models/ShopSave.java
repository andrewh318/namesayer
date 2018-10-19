package app.models;

import java.io.Serializable;

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
