package bunge.cord;

import bunge.cord.network.Network;
import bunge.cord.network.protocol.DisconnectPacket;
import bunge.cord.utils.StorageClientInformation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private List<Client> clients = new ArrayList<>();
    private Map<Long, StorageClientInformation> infoClients = new HashMap<>();

    private Network network;
    public int tick = 128;

    public int onlinePlayers = 0;
    public int maxPlayers = 0;

    private String[] args;

    public int port;
    public String password;

    public Server(String[] args){
        this.network = new Network(this);
        this.args = args;
    }

    public void start(){
        try {
            Thread thread = Thread.currentThread();
            thread.setName("Server Thread");
            tick();
            //reset();
            if(args.length >= 2){
                port = Integer.parseInt(args[0]);
                password = args[1];
            } else {
                port = 1111;
                password = "testpass";
            }
            ServerSocket serv = new ServerSocket(1111);
            System.out.println("Iniciando o Servidor...");
            System.out.println(String.format("Port: %s Pass: %s", port, password));
            while (true){
                Socket clie = serv.accept();
                Client client = new Client(this, clie);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tick(){
        Thread tickThread = new Thread(() -> {
            while (true){
                try {
                    tick = 128 - getNetwork().getPacketsToProcess().size();
                    for(Client client : getClients()){
                        if(!client.isConnected()) continue;
                        client.processPackets();
                    }
                    Thread.sleep(1000);
                    tick = 128;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tickThread.start();
    }

    public void reset(){
        new Thread(() -> {
            while (true){
                try {
                    Thread.sleep(6000);
                    System.out.println(String.format("ServerTick: %s", tick));
                    System.out.println(String.format("Online: %s", onlinePlayers));
                    System.out.println(String.format("Max: %s", maxPlayers));
                    setOnlinePlayers(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Network getNetwork() {
        return network;
    }

    public int getMaxPlayers(){
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getOnlinePlayers(){
        return this.onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void addClient(Client client){
        this.clients.add(client);
    }

    public void delClient(Client client){
        if(clients.contains(client)){
            if(getInfoClients().containsKey(client.getServerId())){
                getInfoClients().remove(client.getServerId());
            }
            this.clients.remove(client);
        }
    }

    public Map<Long, StorageClientInformation> getInfoClients() {
        return infoClients;
    }
}
