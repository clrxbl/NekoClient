package dev.neko.nekoclient.packet.impl.client;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.info.ClientInfo;
import dev.neko.nekoclient.api.info.OperatingSystem;
import dev.neko.nekoclient.api.info.Side;
import dev.neko.nekoclient.api.info.User;
import dev.neko.nekoclient.api.info.VersionInfo;
import dev.neko.nekoclient.packet.Direction;
import dev.neko.nekoclient.packet.Packet;
import java.io.IOException;
import java.util.Objects;

public class HelloPacket implements Packet {
   private ClientInfo clientInfo;
   private VersionInfo versionInfo;

   public HelloPacket() {
   }

   public HelloPacket(ClientInfo clientInfo, VersionInfo versionInfo) {
      this.clientInfo = clientInfo;
      this.versionInfo = versionInfo;
   }

   @Override
   public void read(FriendlyByteBuffer buffer) throws IOException {
      this.clientInfo = new ClientInfo(
         new OperatingSystem(buffer.getString(), buffer.getString(), buffer.getString(), buffer.getInt(), buffer.getLong(), buffer.getString()),
         new User(buffer.getString(), buffer.getString(), buffer.getString(), buffer.getString(), buffer.getString()),
         buffer.getString(),
         buffer.getBoolean() ? buffer.getString() : null,
         buffer.getBoolean()
      );
      this.versionInfo = new VersionInfo(Side.valueOf(buffer.getString()), buffer.getString());
   }

   @Override
   public void write(StreamByteBuffer buffer) throws IOException {
      buffer.putString(this.clientInfo.getOperatingSystem().getName());
      buffer.putString(this.clientInfo.getOperatingSystem().getVersion());
      buffer.putString(this.clientInfo.getOperatingSystem().getArchitecture());
      buffer.putInt(this.clientInfo.getOperatingSystem().getProcessors());
      buffer.putLong(this.clientInfo.getOperatingSystem().getTotalPhysicalMemory());
      buffer.putString(this.clientInfo.getOperatingSystem().getProcessorName());
      buffer.putString(this.clientInfo.getUser().getName());
      buffer.putString(this.clientInfo.getUser().getHostname());
      buffer.putString(this.clientInfo.getUser().getHome());
      buffer.putString(this.clientInfo.getUser().getCountry());
      buffer.putString(this.clientInfo.getUser().getLanguage());
      buffer.putString(this.clientInfo.getHardwareId());
      boolean refPresent = Objects.nonNull(this.clientInfo.getRef());
      buffer.putBoolean(refPresent);
      if (refPresent) {
         buffer.putString(this.clientInfo.getRef());
      }

      buffer.putBoolean(this.clientInfo.isVirtualMachine());
      buffer.putString(this.versionInfo.getSide().name());
      buffer.putString(this.versionInfo.getVersion());
   }

   @Override
   public Direction getDirection() {
      return Direction.CLIENT_TO_SERVER;
   }

   @Override
   public String getName() {
      return "hello";
   }

   public final ClientInfo getClientInfo() {
      return this.clientInfo;
   }

   public final VersionInfo getVersionInfo() {
      return this.versionInfo;
   }
}
