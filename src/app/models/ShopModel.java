package app.models;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.*;

public class ShopModel {
    private SimpleIntegerProperty currentMoney;
    private boolean isPurpleUnlocked = false;
    private boolean isBlueUnlocked = false;

    public ShopModel(){
        currentMoney = new SimpleIntegerProperty();
        createMoneyFile();
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

    // keeps track of the number of points user has accumulated
    public void createMoneyFile(){
        File file = new File(NamesModel.MONEYFILE);
        try {
            file.createNewFile();
            // check if file is empty, skip the rest of steps
            if (file.length() == 0){
                return;
            } else {
                // else if there is a value in teh file
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String moneyFromFile = reader.readLine();
                // check if it is a valid integer, throws exception if not
                int money = Integer.parseInt(moneyFromFile);
                // set the application global money to money from file
                this.currentMoney.set(money);
            }
        } catch(IOException e){
            e.printStackTrace();
        } catch (NumberFormatException e){
            // if money from file is not valid set money to default
            this.currentMoney.set(0);
        }
    }

    public void saveMoneyToFile(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(NamesModel.MONEYFILE));
            writer.write(String.valueOf(currentMoney.get()));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
