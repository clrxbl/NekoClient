package dev.neko.nekoclient.api.info;

public class OperatingSystem {
   private final String name;
   private final String version;
   private final String architecture;
   private final int processors;
   private final long totalPhysicalMemory;
   private final String processorName;

   public OperatingSystem(String name, String version, String architecture, int processors, long totalPhysicalMemory, String processorName) {
      this.name = name;
      this.version = version;
      this.architecture = architecture;
      this.processors = processors;
      this.totalPhysicalMemory = totalPhysicalMemory;
      this.processorName = processorName;
   }

   public final String getName() {
      return this.name;
   }

   public final String getVersion() {
      return this.version;
   }

   public final String getArchitecture() {
      return this.architecture;
   }

   public final int getProcessors() {
      return this.processors;
   }

   public final long getTotalPhysicalMemory() {
      return this.totalPhysicalMemory;
   }

   public final String getProcessorName() {
      return this.processorName;
   }
}
