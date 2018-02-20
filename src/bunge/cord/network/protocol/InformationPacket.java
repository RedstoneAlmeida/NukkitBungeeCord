package bunge.cord.network.protocol;

/**
 * Created by ASUS on 19/02/2018.
 */
public class InformationPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.INFORMATION_PACKET;

    public String message;
    public long serverId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.message = this.getString();
        this.serverId = this.getLong();
    }

    @Override
    public void encode() {
        this.putString(message);
        this.putLong(serverId);
    }
}
