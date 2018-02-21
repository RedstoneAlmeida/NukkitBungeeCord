package bunge.cord.utils;

public class StorageClientInformation {

    private String name;
    private int slots = 20;
    private int online = 0;

    public StorageClientInformation(String name, int slots){
        this.name = name;
        this.slots = slots;
    }

    public void setPlayerCount(int online) {
        this.online = online;
    }

    public int getCountPlayers() {
        return online;
    }

    public String getName() {
        return name;
    }

    public int getSlots() {
        return slots;
    }
}
