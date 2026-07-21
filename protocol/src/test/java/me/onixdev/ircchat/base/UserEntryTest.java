package me.onixdev.ircchat.base;

import me.onixdev.ircchat.base.entity.UserEntry;
import me.onixdev.ircchat.base.entity.UserEntryCodec;
import org.junit.jupiter.api.Test;
import ru.kseonyt.net.packet.PacketBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntryTest {
    @Test
    public void userEntrySerializationDeserializationTest() {
        UserEntry original = new UserEntry("Onix", "Developer");
        UserEntryCodec codec = new UserEntryCodec();

        PacketBuffer buffer = PacketBuffer.allocate(256);
        codec.encode(original, buffer);
        buffer.resetReaderIndex();
        UserEntry decoded = codec.decode(buffer);
        assertEquals(original.username(), decoded.username());
        assertEquals(original.role(), decoded.role());
    }

}