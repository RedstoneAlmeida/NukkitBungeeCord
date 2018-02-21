package bunge.cord.network.protocol;

public interface ProtocolInfo {

    byte HANDLER_PACKET = 0x01;
    byte CONNECTION_PACKET = 0x02;
    byte DISCONNECTION_PACKET = 0x03;
    byte INFORMATION_PACKET = 0x04;
    byte SERVER_INFORMATION_PACKET = 0x05;

}
