package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.impl.c2.codecs.ClientChatMessagePacketCodec;
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket;
import org.junit.jupiter.api.Test;
import ru.kseonyt.net.packet.PacketBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class ClientChatMessagePacketCodecTest {

    @Test
    public void roundTripKeepsEveryField() {
        ClientChatMessagePacket original =
                new ClientChatMessagePacket("fabric-mod", "hello world", "Onix");
        ClientChatMessagePacketCodec codec = new ClientChatMessagePacketCodec();

        PacketBuffer buffer = PacketBuffer.allocate(256);
        codec.encode(original, buffer);
        buffer.resetReaderIndex();

        ClientChatMessagePacket decoded = codec.decode(buffer);

        assertEquals(original.sender(), decoded.sender());
        assertEquals(original.message(), decoded.message());
        assertEquals(original.author(), decoded.author());
    }
}
