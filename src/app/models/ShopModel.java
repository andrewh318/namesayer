package app.models;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;

public class ShopModel {
    private SimpleIntegerProperty currentMoney;
    private boolean _isPurpleUnlocked = false;
    private boolean _isBlueUnlocked = false;
    private boolean _isRedUnlocked = false;
    private boolean _isAquaUnlocked = false;
    private boolean _isYellowUnlocked = false;

    public ShopModel(){
        currentMoney = new SimpleIntegerProperty();
        createShopState();
    }


    public int getMoney(){ return currentMoney.get(); }

    public void setMoney(int money){ currentMoney.set(money); }

    // returns the IntegerProperty version of the current money so it can be used to bind label
    public SimpleIntegerProperty getMoneyBinding(){return currentMoney; }

    public boolean getPurpleUnlocked(){ return _isPurpleUnlocked; }
    public boolean getBlueUnlocked(){ return _isBlueUnlocked; }
    public boolean getRedUnlocked(){ return _isRedUnlocked; }
    public boolean getYellowUnlocked(){ return _isYellowUnlocked; }
    public boolean getAquaUnlocked(){ return _isAquaUnlocked; }

    public void setPurpleUnlock(boolean status){ _isPurpleUnlocked = status; }
    public void setBlueUnlocked(boolean status){ _isBlueUnlocked = status; }
    public void setRedUnlocked(boolean status){ _isRedUnlocked = status; }
    public void setYellowUnlocked(boolean status){ _isYellowUnlocked = status; }
    public void setAquaUnlocked(boolean status){ _isAquaUnlocked = status; }

    /**
     * In case there is no previous application state, the shop state will be set to this default one
     */
    private void setUpDefaultState(){
        currentMoney.set(NamesModel.DEFAULT_MONEY);
        _isPurpleUnlocked = false;
        _isBlueUnlocked = false;
        _isRedUnlocked = false;
        _isAquaUnlocked = false;
        _isYellowUnlocked = false;
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
                _isPurpleUnlocked = save.isPurpleUnlocked();
                _isBlueUnlocked = save.isBlueUnlocked();
                _isRedUnlocked = save.isRedUnlocked();
                _isAquaUnlocked = save.isAquaUnlocked();
                _isYellowUnlocked = save.isYellowUnlocked();
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
            ShopSave shopSave = new ShopSave(this.currentMoney.get(), this._isPurpleUnlocked, this._isBlueUnlocked,
                    this._isRedUnlocked, this._isAquaUnlocked, this._isYellowUnlocked);

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
