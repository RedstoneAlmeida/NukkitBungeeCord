package bunge;

import bunge.cord.network.BinaryStream;
import bunge.cord.network.protocol.ConnectionPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ASUS on 19/02/2018.
 */
public class Test {

    public static void main(String[] args){
        try {
            Socket socket = new Socket("localhost", 1111);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ConnectionPacket pk = new ConnectionPacket();
            pk.name = "Teste";
            pk.serverId = 10000L;
            pk.slots = 30;
            pk.encode();
            out.writeByte(pk.pid());
            byte[] buffer = pk.getBuffer();
            out.writeInt(buffer.length);
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
