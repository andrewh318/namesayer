package app.models;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;

public class ShopModel {
    private SimpleIntegerProperty currentMoney;
    private boolean isPurpleUnlocked = false;
    private boolean isBlueUnlocked = false;

    public ShopModel(){
        currentMoney = new SimpleIntegerProperty();
        createShopState();
    }


    public int getMoney(){
        return currentMoney.get();
    }

    public SimpleIntegerProperty getMoneyBinding(){
        return currentMoney;
    }

    public void setMoney(int money){
        currentMoney.set(money);
    }


    public boolean getPurpleUnlocked(){
        return isPurpleUnlocked;
    }
    public boolean getBlueUnlocked(){
        return isBlueUnlocked;
    }

    public void setPurpleUnlock(boolean status){
        isPurpleUnlocked = status;
    }

    public void setBlueUnlocked(boolean status){
        isBlueUnlocked = status;
    }

    // reads in a serializable object and builds the application state
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

    private void setUpDefaultState(){
        currentMoney.set(NamesModel.DEFAULT_MONEY);
        isPurpleUnlocked = false;
        isBlueUnlocked = false;
    }


    public void saveStateToFile(){
        try {
            // create a proxy object that implements serializable to hold application staet
            ShopSave shopSave = new ShopSave(this.currentMoney.get(), this.isPurpleUnlocked, this.isBlueUnlocked);

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
