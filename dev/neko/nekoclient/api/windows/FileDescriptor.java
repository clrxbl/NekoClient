package dev.neko.nekoclient.api.windows;

public class FileDescriptor {
   public static final int FILE_ATTRIBUTE_READONLY = 1;
   public static final int FILE_ATTRIBUTE_HIDDEN = 2;
   public static final int FILE_ATTRIBUTE_SYSTEM = 4;
   public static final int FILE_ATTRIBUTE_DIRECTORY = 16;
   public static final int FILE_ATTRIBUTE_ARCHIVE = 32;
   public static final int FILE_ATTRIBUTE_NORMAL = 128;
   public static final int FILE_ATTRIBUTE_TEMPORARY = 256;
   public static final int FILE_ATTRIBUTE_COMPRESSED = 2048;
   public static final int FILE_ATTRIBUTE_ENCRYPTED = 16384;
   private final String name;
   private final int flags;
   private final int attributes;

   public FileDescriptor(String name, int flags, int attributes) {
      this.name = name;
      this.flags = flags;
      this.attributes = attributes;
   }

   public final String getName() {
      return this.name;
   }

   public final int getFlags() {
      return this.flags;
   }

   public final int getAttributes() {
      return this.attributes;
   }

   public final boolean isReadOnly() {
      return this.is(1);
   }

   public final boolean isHidden() {
      return this.is(2);
   }

   public final boolean isSystem() {
      return this.is(4);
   }

   public final boolean isDirectory() {
      return this.is(16);
   }

   public final boolean isArchive() {
      return this.is(32);
   }

   public final boolean isNormal() {
      return this.is(128);
   }

   public final boolean isTemporary() {
      return this.is(256);
   }

   public final boolean isCompressed() {
      return this.is(2048);
   }

   public final boolean isEncrypted() {
      return this.is(16384);
   }

   public boolean is(int attribute) {
      return (this.attributes & attribute) != 0;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
