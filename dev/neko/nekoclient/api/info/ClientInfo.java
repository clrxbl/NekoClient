package dev.neko.nekoclient.api.info;

public class ClientInfo {
   private final OperatingSystem operatingSystem;
   private final User user;
   private final String hardwareId;
   private final String ref;
   private final boolean virtualMachine;

   public ClientInfo(OperatingSystem operatingSystem, User user, String hardwareId, String ref, boolean virtualMachine) {
      this.operatingSystem = operatingSystem;
      this.user = user;
      this.hardwareId = hardwareId;
      this.ref = ref;
      this.virtualMachine = virtualMachine;
   }

   public final OperatingSystem getOperatingSystem() {
      return this.operatingSystem;
   }

   public final User getUser() {
      return this.user;
   }

   public final String getHardwareId() {
      return this.hardwareId;
   }

   public final String getRef() {
      return this.ref;
   }

   public final boolean isVirtualMachine() {
      return this.virtualMachine;
   }
}
