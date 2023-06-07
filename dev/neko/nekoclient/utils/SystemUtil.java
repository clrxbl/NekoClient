package dev.neko.nekoclient.utils;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class SystemUtil {
   private static HardwareAbstractionLayer hardware = null;

   public static int getAvailableProcessors() {
      return Runtime.getRuntime().availableProcessors();
   }

   public static long getTotalPhysicalMemory() {
      try {
         if (Objects.nonNull(hardware)) {
            return hardware.getMemory().getTotal();
         }
      } catch (Throwable var2) {
      }

      try {
         if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
            return ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
         }
      } catch (Throwable var1) {
      }

      return 0L;
   }

   public static String getProcessorName() {
      if (Objects.nonNull(hardware)) {
         try {
            return hardware.getProcessor().getProcessorIdentifier().getName();
         } catch (Throwable var1) {
         }
      }

      return "";
   }

   public static byte[] envBytes(int offset, String... properties) {
      String value = System.getenv(properties[offset]);
      return Objects.isNull(value) ? (offset + 1 >= properties.length ? new byte[0] : propertyBytes(offset + 1, properties)) : value.getBytes();
   }

   public static byte[] envBytes(String... properties) {
      return envBytes(1, properties);
   }

   public static byte[] envBytes(String env) {
      String value = System.getenv(env);
      return Objects.isNull(value) ? new byte[0] : value.getBytes();
   }

   public static String env(String env) {
      return new String(envBytes(env));
   }

   public static byte[] propertyBytes(String property) {
      String value = System.getProperty(property);
      return Objects.isNull(value) ? new byte[0] : value.getBytes();
   }

   public static byte[] propertyBytes(int offset, String... properties) {
      String value = System.getProperty(properties[offset]);
      return Objects.isNull(value) ? (offset + 1 >= properties.length ? new byte[0] : propertyBytes(offset + 1, properties)) : value.getBytes();
   }

   public static byte[] propertyBytes(String... properties) {
      return propertyBytes(1, properties);
   }

   public static String property(String property) {
      return new String(propertyBytes(property));
   }

   public static String property(String... property) {
      return new String(propertyBytes(property));
   }

   static {
      try {
         hardware = new SystemInfo().getHardware();
      } catch (Throwable var1) {
      }
   }
}
