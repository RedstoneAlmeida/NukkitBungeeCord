package bunge;

import bunge.cord.Client;
import bunge.cord.Server;
import bunge.cord.network.protocol.DisconnectPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BungeePE {

    public static void main(String[] args){
        try {
            Server server = new Server(args);
            server.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
