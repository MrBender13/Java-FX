package realization;

public class ItemData {
    private String name;
    private String type;
    private Integer cost;
    private Integer amount;
    private Integer bought;

    public ItemData(String name, String type, Integer amount, Integer bought, Integer cost) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.amount = amount;
        this.bought = bought;
    }

    public ItemData(Item toCopy) {
        this.name = toCopy.getName();
        this.type = toCopy.getType();
        this.cost = toCopy.getCost();
        this.amount = toCopy.getAmount();
        this.bought = toCopy.getBought();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getCost() {
        return cost;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getBought() {
        return bought;
    }
}