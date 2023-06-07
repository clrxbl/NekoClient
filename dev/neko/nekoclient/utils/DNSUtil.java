package dev.neko.nekoclient.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class DNSUtil {
   private static final InitialDirContext DIRECTORY;

   public static DNSUtil.DNSEntry resolveMinecraft(String domain) throws NamingException {
      Attributes attributes = DIRECTORY.getAttributes(String.format("dns:///_minecraft._tcp.%s", domain), new String[]{"SRV"});
      if (Objects.isNull(attributes)) {
         return null;
      } else {
         Attribute srvRecords = attributes.get("SRV");
         if (!Objects.isNull(srvRecords) && srvRecords.size() > 0) {
            NamingEnumeration<?> enumeration = srvRecords.getAll();
            List<DNSUtil.DNSEntry> dnsEntries = new ArrayList<>();

            while(enumeration.hasMore()) {
               String line = (String)enumeration.next();
               if (line.endsWith(".")) {
                  line = line.substring(0, line.length() - 1);
               }

               String[] split = line.split(" ");
               dnsEntries.add(new DNSUtil.DNSEntry(Integer.parseInt(split[0]), Integer.parseInt(split[1]), split[3], Integer.parseInt(split[2])));
            }

            int maxPriority = dnsEntries.stream().min(Comparator.comparingInt(DNSUtil.DNSEntry::getPriority)).orElseThrow(RuntimeException::new).getPriority();
            return dnsEntries.stream()
               .filter(dnsEntry -> Objects.equals(dnsEntry.getPriority(), maxPriority))
               .max(Comparator.comparingInt(DNSUtil.DNSEntry::getWeight))
               .orElse(null);
         } else {
            return null;
         }
      }
   }

   static {
      Hashtable<String, String> env = new Hashtable<>();
      env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
      env.put("java.naming.provider.url", "dns:");

      try {
         DIRECTORY = new InitialDirContext(env);
      } catch (NamingException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static class DNSEntry {
      private final int priority;
      private final int weight;
      private final String host;
      private final int port;

      public DNSEntry(int priority, int weight, String host, int port) {
         this.priority = priority;
         this.weight = weight;
         this.host = host;
         this.port = port;
      }

      public final int getPort() {
         return this.port;
      }

      public final String getHost() {
         return this.host;
      }

      public final int getPriority() {
         return this.priority;
      }

      public final int getWeight() {
         return this.weight;
      }
   }
}
