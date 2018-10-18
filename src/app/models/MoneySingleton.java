package app.models;

public class MoneySingleton {
    private static MoneySingleton _instance;
    private int money = 0;
    private boolean isPurpleUnlocked = false;
    private boolean isBlueUnlocked = false;

    public static MoneySingleton getInstance(){
        if (_instance == null){
            _instance = new MoneySingleton();
            return _instance;
        } else {
            return _instance;
        }
    }

    private MoneySingleton(){

    }

    public int getMoney(){
        return money;
    }

    public void setMoney(int money){
        this.money = money;
    }

    public boolean getPurpleUnlocked(){
        return isPurpleUnlocked;
    }
    public boolean getBlueUnlocked(){
        return isBlueUnlocked;
    }
    public void setPurpleUnlocked(boolean bool){
        isPurpleUnlocked = bool;
    }
    public void setBlueUnlocked(boolean bool){
        isBlueUnlocked = bool;
    }
}
