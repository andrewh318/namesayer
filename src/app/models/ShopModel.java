package app.models;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;

public class ShopModel {
    private SimpleIntegerProperty currentMoney;
    private boolean isPurpleUnlocked = false;
    private boolean isBlueUnlocked = false;
    private boolean isRedUnlocked = false;
    private boolean isAquaUnlocked = false;
    private boolean isYellowUnlocked = false;

    public ShopModel(){
        currentMoney = new SimpleIntegerProperty();
        createShopState();
    }


    public int getMoney(){
        return currentMoney.get();
    }
    public void setMoney(int money){
        currentMoney.set(money);
    }

    // returns the IntegerProperty version of the current money so it can be used to bind label
    public SimpleIntegerProperty getMoneyBinding(){
        return currentMoney;
    }


    public boolean getPurpleUnlocked(){
        return isPurpleUnlocked;
    }
    public boolean getBlueUnlocked(){
        return isBlueUnlocked;
    }
    public boolean getRedUnlocked(){
        return isRedUnlocked;
    }
    public boolean getYellowUnlocked(){
        return isYellowUnlocked;
    }
    public boolean getAquaUnlocked(){
        return isAquaUnlocked;
    }

    public void setPurpleUnlock(boolean status){
        isPurpleUnlocked = status;
    }
    public void setBlueUnlocked(boolean status){
        isBlueUnlocked = status;
    }
    public void setRedUnlocked(boolean status){
        isRedUnlocked = status;
    }
    public void setYellowUnlocked(boolean status){
        isYellowUnlocked = status;
    }
    public void setAquaUnlocked(boolean status){
        isAquaUnlocked = status;
    }

    // in the case that there is no previous application state, this is the default status

    /**
     * In case there is no previous application state, the shop state will be set to this default one
     */
    private void setUpDefaultState(){
        currentMoney.set(NamesModel.DEFAULT_MONEY);
        isPurpleUnlocked = false;
        isBlueUnlocked = false;
        isRedUnlocked = false;
        isAquaUnlocked = false;
        isYellowUnlocked = false;
    }

    /**
     * Reads in a serializable object and builds the application state
     */
    public void createShopState(){
        File file = new File(NamesModel.APPLICATION_STATE);
        try {
            file.createNewFile();
            // check if file is empty, if it is set up application state to be default
            if (file.length() == 0){
                setUpDefaultState();
                return;
            } else {
                // attempt to deserialize object
                ShopSave save;
                FileInputStream fileIn = new FileInputStream(NamesModel.APPLICATION_STATE);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                save = (ShopSave) in.readObject();
                in.close();
                fileIn.close();

                // build this application state from the proxy object
                isPurpleUnlocked = save.isPurpleUnlocked();
                isBlueUnlocked = save.isBlueUnlocked();
                isRedUnlocked = save.isRedUnlocked();
                isAquaUnlocked = save.isAquaUnlocked();
                isYellowUnlocked = save.isYellowUnlocked();
                currentMoney.set(save.getMoney());

            }
        } catch(IOException e){
            e.printStackTrace();
        } catch(ClassNotFoundException e){
            // if serialized file has been modified, just simply set up default application state
            setUpDefaultState();
            e.printStackTrace();
        }
    }

    /**
     * Saves the application state to a serialized object so it can be read back in
     */
    public void saveStateToFile(){
        try {
            // create a proxy object that implements serializable to hold application staet
            ShopSave shopSave = new ShopSave(this.currentMoney.get(), this.isPurpleUnlocked, this.isBlueUnlocked,
                    this.isRedUnlocked, this.isAquaUnlocked, this.isYellowUnlocked);

            FileOutputStream fileOut = new FileOutputStream(NamesModel.APPLICATION_STATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(shopSave);

            out.close();
            fileOut.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
