package realization;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {

    private StringProperty name;
    private StringProperty type;
    private IntegerProperty cost;
    private IntegerProperty amount;
    private IntegerProperty bought;


    @Override
    public String toString() {
        return name.get() + " " + amount.get() + " " + type.get() + " "
                + cost.get() + " " + bought.get();
    }

    public Item(String name, String type, Integer amount,
                Integer bought, Integer cost) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.cost = new SimpleIntegerProperty(cost);
        this.amount = new SimpleIntegerProperty(amount);
        this.bought = new SimpleIntegerProperty(bought);
    }

    public int getCost() {
        return cost.get();
    }

    public IntegerProperty costProperty() {
        return cost;
    }

    public void setCost(int cost) {
        //TODO setCost create check with exception invalidIntValue
        this.cost.set(cost);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        //TODO setName create check with exception invalidStringValue
        this.name.set(name);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        //TODO setName create check with exception invalidStringValue
        this.type.set(type);
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public void setAmount(int amount) {
        //TODO setCost create check with exception invalidIntValue
        this.amount.set(amount);
    }

    public int getBought() {
        return bought.get();
    }

    public IntegerProperty boughtProperty() {
        return bought;
    }

    public void setBought(int bought) {
        this.bought.set(bought);
    }
}
