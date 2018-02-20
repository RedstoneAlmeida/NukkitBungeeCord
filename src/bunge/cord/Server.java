package bunge.cord;

import bunge.cord.network.Network;
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
    public int tick = 20;

    public Server(){
        this.network = new Network(this);
    }

    public void start(){
        try {
            Thread thread = Thread.currentThread();
            thread.setName("Server Thread");
            tick();
            ServerSocket serv = new ServerSocket(1111);
            System.out.println("Iniciando o Servidor...");
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
                    tick = 20 - getNetwork().getPacketsToProcess().size();
                    for(Client client : getClients()){
                        if(!client.isConnected()) continue;
                        client.processPackets();
                    }
                    Thread.sleep(1000);
                    tick = 20;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tickThread.start();
    }

    public Network getNetwork() {
        return network;
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
