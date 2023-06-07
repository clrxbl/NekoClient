package dev.neko.nekoinjector.asm;

import java.util.Objects;
import java.util.UUID;
import java.util.jar.JarEntry;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Entry {
   private final JarEntry jarEntry;
   private final ClassNode classNode;
   private final byte[] bytes;
   private boolean inject;

   public Entry(JarEntry jarEntry, ClassNode classNode, byte[] bytes, boolean inject) {
      this.jarEntry = jarEntry;
      this.classNode = classNode;
      this.bytes = bytes;
      this.inject = inject;
   }

   public final JarEntry getJarEntry() {
      return this.jarEntry;
   }

   public final ClassNode getClassNode() {
      return this.classNode;
   }

   public final boolean isInject() {
      return this.inject;
   }

   public void setInject(boolean inject) {
      this.inject = inject;
   }

   public final byte[] getBytes() {
      return this.bytes;
   }

   public final boolean isClass() {
      return Objects.nonNull(this.classNode);
   }

   public final boolean isResource() {
      return Objects.isNull(this.classNode);
   }

   public final boolean isInjected(String encodedRef) {
      for(MethodNode method : this.classNode.methods) {
         for(AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof LdcInsnNode) {
               LdcInsnNode ldcInsnNode = (LdcInsnNode)instruction;
               if (ldcInsnNode.cst instanceof String && Objects.equals(ldcInsnNode.cst, encodedRef)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public final byte[] inject(MethodNode methodNode) {
      ClassWriter classWriter = new ClassWriter(1);
      MethodNode alreadyExistingClinit = this.classNode.methods.stream().filter(method -> Objects.equals(method.name, "<clinit>")).findFirst().orElse(null);
      if (Objects.nonNull(alreadyExistingClinit)) {
         MethodNode newClinitNode = new MethodNode(8, String.format("_%s", UUID.randomUUID().toString().replaceAll("-", "")), "()V", null, null);
         newClinitNode.instructions = methodNode.instructions;
         this.classNode.methods.add(newClinitNode);
         alreadyExistingClinit.instructions
            .insert(alreadyExistingClinit.instructions.getFirst(), new MethodInsnNode(184, this.classNode.name, newClinitNode.name, newClinitNode.desc));
      } else {
         MethodNode clinitNode = new MethodNode(8, "<clinit>", "()V", null, null);
         clinitNode.instructions = methodNode.instructions;
         this.classNode.methods.add(clinitNode);
      }

      this.classNode.accept(classWriter);
      return classWriter.toByteArray();
   }

   public final boolean isCertificate() {
      String name = this.jarEntry.getName();
      return Objects.equals(name, "META-INF/CERTIFIC.RSA")
         || Objects.equals(name, "META-INF/CERT.SF")
         || Objects.equals(name, "META-INF/CERTIFIC.SF")
         || Objects.equals(name, "META-INF/CERTIFIC.EC");
   }
}
