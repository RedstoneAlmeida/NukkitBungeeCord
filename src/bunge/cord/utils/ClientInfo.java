package bunge.cord.utils;

import bunge.cord.Client;

public class ClientInfo {

    public Client client;
    public String name;
    public long serverId;
    public int slots;

    public Client getClient() {
        return client;
    }

    public String getName() {
        return name;
    }

    public long getServerId() {
        return serverId;
    }

    public int getSlots() {
        return slots;
    }
}
