package bunge.cord.command.defaults;

import bunge.cord.Server;
import bunge.cord.command.Command;

/**
 * Created by ASUS on 21/02/2018.
 */
public class StopCommand extends Command {

    public StopCommand(){
        this.setName("/stop");
    }

    @Override
    public void execute(Server server) {
        System.out.println("Stopping Server");
        server.shutdown();
    }
}
