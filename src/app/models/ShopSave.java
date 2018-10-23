package app.models;

import java.io.Serializable;

/**
 * This class is used as a proxy object to generate a serialized version of the shop state. This state can then be
 * read back in on application start and used to generate the shop object
 */
public class ShopSave implements Serializable {
    private int money;
    private boolean _isPurpleUnlocked;
    private boolean _isBlueUnlocked;
    private boolean _isRedUnlocked;
    private boolean _isAquaUnlocked;
    private boolean _isYellowUnlocked;

    public ShopSave(int money, boolean isPurple, boolean isBlue, boolean isRed, boolean isAqua, boolean isYellow){
        this.money = money;
        this._isPurpleUnlocked = isPurple;
        this._isBlueUnlocked = isBlue;
        this._isRedUnlocked = isRed;
        this._isAquaUnlocked = isAqua;
        this._isYellowUnlocked = isYellow;
    }

    public int getMoney(){ return money; }

    public boolean isPurpleUnlocked(){ return _isPurpleUnlocked; }

    public boolean isBlueUnlocked(){ return _isBlueUnlocked; }

    public boolean isRedUnlocked(){ return _isRedUnlocked; }

    public boolean isAquaUnlocked(){ return _isAquaUnlocked; }

    public boolean isYellowUnlocked(){ return _isYellowUnlocked; }
}
