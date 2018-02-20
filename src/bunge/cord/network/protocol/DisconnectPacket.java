package bunge.cord.network.protocol;

/**
 * Created by ASUS on 19/02/2018.
 */
public class DisconnectPacket extends DataPacket {

    public static byte NETWORK_ID = ProtocolInfo.DISCONNECTION_PACKET;

    public long serverId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.serverId = this.getLong();
    }

    @Override
    public void encode() {
        this.putLong(serverId);
    }
}
