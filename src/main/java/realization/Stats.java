package realization;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.joda.time.DateTime;

public class Stats {
    private StringProperty itemName;
    private IntegerProperty amount;
    private IntegerProperty cost;
    private DateTime time;
    private StringProperty owner;

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    private StringProperty type;

    public Stats(String itemName, Integer amount, Integer cost, DateTime time,
                 String owner, String type) {
        this.itemName = new SimpleStringProperty(itemName);
        this.amount = new SimpleIntegerProperty(amount);
        this.cost = new SimpleIntegerProperty(cost);
        this.time = time;
        this.owner = new SimpleStringProperty(owner);
        this.type = new SimpleStringProperty(type);
    }

    public String getItemName() {
        return itemName.get();
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public int getCost() {
        return cost.get();
    }

    public IntegerProperty costProperty() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost.set(cost);
    }

    public String getTime() {
        return Integer.toString(time.getDayOfMonth()) + "." + time.getMonthOfYear() + "."  +
                + time.getYear() + "  " + time.getHourOfDay() + ":" + time.getMinuteOfDay();
    }

    public String getDate() {
        return Integer.toString(time.getDayOfMonth()) + "." + time.getMonthOfYear() + "."  +
                + time.getYear();
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getOwner() {
        return owner.get();
    }

    public StringProperty ownerProperty() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }
}
