package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.impl.s2.codecs.SystemMessagePacketCodec;
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket;
import org.junit.jupiter.api.Test;
import ru.kseonyt.net.packet.PacketBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class SystemMessagePacketCodecTest {

    @Test
    public void roundTripKeepsEveryField() {
        SystemMessagePacket original = new SystemMessagePacket("WARN", "Authentication timeout");
        SystemMessagePacketCodec codec = new SystemMessagePacketCodec();

        PacketBuffer buffer = PacketBuffer.allocate(256);
        codec.encode(original, buffer);
        buffer.resetReaderIndex();

        SystemMessagePacket decoded = codec.decode(buffer);

        assertEquals(original.level(), decoded.level());
        assertEquals(original.message(), decoded.message());
    }
}
