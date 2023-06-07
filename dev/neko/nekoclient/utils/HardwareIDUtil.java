package dev.neko.nekoclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class HardwareIDUtil {
   public static String generateHardwareID() throws NoSuchAlgorithmException, IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      Enumeration<? extends NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
      if (Objects.nonNull(enumeration)) {
         List<NetworkInterface> networkInterfaces = new ArrayList<>();

         while(enumeration.hasMoreElements()) {
            networkInterfaces.add(enumeration.nextElement());
         }

         networkInterfaces.stream().sorted(Comparator.comparing(NetworkInterface::getName)).filter(networkInterface -> {
            try {
               return networkInterface.isUp() && !networkInterface.isVirtual();
            } catch (SocketException var2x) {
               return false;
            }
         }).map(networkInterface -> {
            try {
               byte[] address = networkInterface.getHardwareAddress();
               return !Objects.isNull(address) && address.length != 0 ? address : null;
            } catch (SocketException var2x) {
               return null;
            }
         }).filter(Objects::nonNull).forEach(bytes -> {
            try {
               byteArrayOutputStream.write(bytes);
            } catch (IOException var3x) {
            }
         });
      }

      byteArrayOutputStream.write(SystemUtil.propertyBytes("os.name"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("os.arch"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("user.name"));
      String hostName = InetAddress.getLocalHost().getHostName();
      if (Objects.nonNull(hostName)) {
         byteArrayOutputStream.write(hostName.getBytes());
      }

      byteArrayOutputStream.write(SystemUtil.propertyBytes("user.country"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("user.language"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("user.home"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("os.version"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("line.separator"));
      byteArrayOutputStream.write(SystemUtil.propertyBytes("file.separator"));
      byteArrayOutputStream.write(String.valueOf(SystemUtil.getAvailableProcessors()).getBytes());
      byteArrayOutputStream.write(String.valueOf(SystemUtil.getTotalPhysicalMemory()).getBytes());
      byteArrayOutputStream.write(SystemUtil.envBytes("PROCESSOR_IDENTIFIER"));
      return HashUtil.generateMD5Hash(byteArrayOutputStream.toByteArray());
   }
}
