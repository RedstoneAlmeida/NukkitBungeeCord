package bunge.cord.utils;

public class StorageClientInformation {

    private String name;
    private int slots = 20;

    public StorageClientInformation(String name, int slots){
        this.name = name;
        this.slots = slots;
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }
}
