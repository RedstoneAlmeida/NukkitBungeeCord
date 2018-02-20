package bunge.cord;

import bunge.cord.Server;
import bunge.cord.network.protocol.*;
import bunge.cord.utils.ClientInfo;
import bunge.cord.utils.StorageClientInformation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Client extends Thread {

    private Socket socket;
    private Server server;
    private StorageClientInformation info;
    private long serverId = 1000L;
    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Client(Server server, Socket socket){
        this.socket = socket;
        this.setName("Client Thread - " + this.getId());
        this.server = server;
        server.addClient(this);
        setConnected(true);
    }

    private Queue<DataPacket> packetsToWrite = new LinkedList<>();

    public Queue<DataPacket> getPacketsToWrite() {
        return packetsToWrite;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if(!isConnected()) return;
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                byte id = entrada.readByte();
                int lenght = entrada.readInt();
                byte[] buffer = new byte[lenght];
                entrada.readFully(buffer);

                server.getNetwork().processPacket(id, buffer);

                new Thread(() -> {
                    while (!getPacketsToWrite().isEmpty()) {
                        DataPacket pk = getPacketsToWrite().poll();
                        pk.encode();
                        try {
                            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());
                            saida.writeByte(pk.pid());
                            byte[] buffered = pk.getBuffer();
                            saida.writeInt(buffered.length);
                            saida.write(buffered);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            if(e instanceof SocketException){
                return;
            }
            e.printStackTrace();
        }
    }

    public void processPackets(){
        while (!server.getNetwork().getPacketsToProcess().isEmpty()){
            DataPacket pk = server.getNetwork().getPacketsToProcess().poll();
            handlePacket(pk);
        }
    }

    public void handlePacket(DataPacket packet){
        switch (packet.pid()){
            case ProtocolInfo.CONNECTION_PACKET:
                System.out.println("Recebido Pacote");
                ConnectionPacket connectionPacket = (ConnectionPacket) packet;
                String serverName = connectionPacket.name;
                String password = connectionPacket.password;
                this.serverId = connectionPacket.serverId;
                int slots = connectionPacket.slots;

                if(!password.equalsIgnoreCase(server.password)){
                    DisconnectPacket pk = new DisconnectPacket();
                    pk.serverId = this.serverId;
                    this.dataPacket(pk);
                    return;
                }
                if(server.getInfoClients().containsKey(serverId)){
                    System.out.println("ServerID já está em uso, Servidor já registrado!");
                    return;
                }
                server.getInfoClients().put(serverId, info = new StorageClientInformation(serverName, slots));
                System.out.println("Registrado: " + serverId);
                System.out.println(String.format("Nome: %s, Slots: %s", serverName, slots));
                break;
            case ProtocolInfo.HANDLER_PACKET:
                break;
            case ProtocolInfo.DISCONNECTION_PACKET:
                DisconnectPacket diss = (DisconnectPacket) packet;
                System.out.println("Desconectado o cliente: " + diss.serverId);
                setConnected(false);
                if(server.getInfoClients().containsKey(diss.serverId)){
                    server.getInfoClients().remove(diss.serverId);
                    return;
                }
                if(server.getClients().contains(this)){
                    server.delClient(this);
                }
                this.stop();
                break;
            case ProtocolInfo.INFORMATION_PACKET:
                InformationPacket mess = (InformationPacket) packet;
                if(server.getInfoClients().containsKey(mess.serverId)) {
                    System.out.println(server.getInfoClients().get(mess.serverId).getName() + " - " + mess.message);
                }
                for(Client client : server.getClients()){
                    client.dataPacket(mess);
                }
                break;
        }
    }

    public void dataPacket(DataPacket packet){
        server.getNetwork().putPacket(this, packet);
    }

    public StorageClientInformation getInfo() {
        return info;
    }

    public long getServerId() {
        return serverId;
    }

    public int getPort(){
        return socket.getPort();
    }

    public String getAddress(){
        return socket.getInetAddress().getHostAddress();
    }

    private Socket getSocket() {
        return socket;
    }
}
