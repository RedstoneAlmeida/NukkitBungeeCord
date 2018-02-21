package bunge.cord.command;

import bunge.cord.Server;

public abstract class Command {

    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(Server server);

}
