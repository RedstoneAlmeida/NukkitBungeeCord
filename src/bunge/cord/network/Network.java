package bunge.cord.network;

import bunge.cord.Client;
import bunge.cord.Server;
import bunge.cord.network.protocol.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ASUS on 19/02/2018.
 */
public class Network {

    @SuppressWarnings("unchecked")
    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private Queue<DataPacket> packetsToProcess = new LinkedList<>();

    private final Server server;

    public Network(Server server){
        this.registerPackets();
        this.server = server;
    }

    public Queue<DataPacket> getPacketsToProcess() {
        return packetsToProcess;
    }

    public void processPacket(byte id, byte[] buffer){
        DataPacket pk = getPacket(id);
        pk.setBuffer(buffer);
        pk.decode();
        getPacketsToProcess().add(pk);
    }

    public void putPacket(Client client, DataPacket packet){
        client.getPacketsToWrite().add(packet);
    }

    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                System.out.println("Logger");
            }
        }
        return null;
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public void registerPackets(){
        this.registerPacket(ProtocolInfo.CONNECTION_PACKET, ConnectionPacket.class);
        this.registerPacket(ProtocolInfo.HANDLER_PACKET, HandlerPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECTION_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.INFORMATION_PACKET, InformationPacket.class);
        this.registerPacket(ProtocolInfo.SERVER_INFORMATION_PACKET, ServerInformationPacket.class);
    }

}
