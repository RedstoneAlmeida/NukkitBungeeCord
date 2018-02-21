package bunge;

import bunge.cord.Client;
import bunge.cord.Server;
import bunge.cord.network.protocol.DisconnectPacket;

public class BungeePE {

    public static void main(String[] args){
        Server server = new Server(args);
        server.start();
    }

}
