package bunge.cord.command.defaults;

import bunge.cord.Server;
import bunge.cord.command.Command;
import bunge.cord.utils.StorageClientInformation;

/**
 * Created by ASUS on 21/02/2018.
 */
public class StatusCommand extends Command {

    @Override
    public void execute(Server server) {
        System.out.println("MaxOnline: " + server.getMaxPlayers());
        System.out.println("Online: " + server.getOnlinePlayers());
        for(long serverId : server.getInfoClients().keySet()){
            StorageClientInformation info = server.getInfoClients().get(serverId);
            System.out.println(String.format("Conected: %s(%s)", info.getName(), serverId));
        }
    }
}
