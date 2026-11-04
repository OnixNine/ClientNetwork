package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.impl.s2.codecs.KeepAlivePacketCodec;
import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket;
import org.junit.jupiter.api.Test;
import ru.kseonyt.net.packet.PacketBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class KeepAlivePacketCodecTest {

    @Test
    public void roundTripKeepsEveryField() {
        KeepAlivePacket original = new KeepAlivePacket(0x0123456789ABCDEFL);
        KeepAlivePacketCodec codec = new KeepAlivePacketCodec();

        PacketBuffer buffer = PacketBuffer.allocate(256);
        codec.encode(original, buffer);
        buffer.resetReaderIndex();

        KeepAlivePacket decoded = codec.decode(buffer);

        assertEquals(original.id(), decoded.id());
    }
}
