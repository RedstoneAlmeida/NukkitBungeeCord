package bunge.cord;

import bunge.cord.command.Command;
import bunge.cord.command.defaults.StatusCommand;
import bunge.cord.command.defaults.StopCommand;
import bunge.cord.network.Network;
import bunge.cord.network.protocol.DisconnectPacket;
import bunge.cord.utils.StorageClientInformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
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

    private Thread currentThread;

    public Server(String[] args){
        this.network = new Network(this);
        this.args = args;
    }

    public void start(){
        try {
            currentThread = Thread.currentThread();
            currentThread.setName("Server Thread");
            //reset();
            if(args.length >= 2){
                port = Integer.parseInt(args[0]);
                password = args[1];
            } else {
                port = 1111;
                password = "testpass";
            }
            ServerSocket serv = new ServerSocket();
            serv.bind(new InetSocketAddress("0.0.0.0", port));
            tick();
            reset();
            System.out.println("Iniciando o Servidor...");
            System.out.println(String.format("Port: %s Pass: %s", port, password));
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    // Le entao a palagra SAIR nao seja digitada
                    String linha = "";
                    Command command = null;
                    while (!linha.equals("SAIR")) {
                        linha = reader.readLine();
                        switch (linha.toLowerCase().replace("/", "")){
                            case "stop":
                                command = new StopCommand();
                                break;
                            case "status":
                                command = new StatusCommand();
                                break;
                        }
                        if(command != null) {
                            command.execute(this);
                        }
                    }
                }
                catch (IOException e) {
                    System.out.println("Erro: "+ e);
                }
            }).start();
            while (true){
                Socket clie = serv.accept();
                Client client = new Client(this, clie);
                client.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        DisconnectPacket pk = new DisconnectPacket();
        pk.serverId = 1039819381L;
        for(Client client : clients){
            client.dataPacket(pk);
        }
        try {
            Thread.sleep(1800);
            System.exit(0);
        } catch (InterruptedException e) {
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
                    Thread.sleep(500);
                    int count = 0;
                    for(long serverId : getInfoClients().keySet()){
                        StorageClientInformation info = getInfoClients().get(serverId);
                        count += info.getCountPlayers();
                    }
                    setOnlinePlayers(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean isPrimaryThread(){
        return (Thread.currentThread() == currentThread);
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
