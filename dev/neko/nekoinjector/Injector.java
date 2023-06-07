package dev.neko.nekoinjector;

import dev.neko.nekoinjector.utils.EncodingUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Injector {
   public static MethodNode loadInstallerNode(InputStream clazz, String ref) throws IOException {
      ClassReader installerClassReader = new ClassReader(clazz);
      ClassNode installerNode = new ClassNode();
      installerClassReader.accept(installerNode, 0);
      MethodNode methodNode = installerNode.methods
         .stream()
         .filter(methodNode2 -> Objects.equals(methodNode2.name, "load"))
         .findFirst()
         .orElseThrow(IllegalStateException::new);

      for(AbstractInsnNode instruction : methodNode.instructions) {
         if (instruction instanceof LdcInsnNode) {
            LdcInsnNode ldc = (LdcInsnNode)instruction;
            if (ldc.cst instanceof String && Objects.equals(ldc.cst, "REF")) {
               ldc.cst = Objects.isNull(ref) ? null : EncodingUtil.encodeToByteFormat(EncodingUtil.reverseBytes(ref.getBytes()));
            }
         }
      }

      return methodNode;
   }

   public static String findMainClass(Manifest manifest) {
      return !Objects.isNull(manifest) && !Objects.isNull(manifest.getMainAttributes()) && manifest.getMainAttributes().containsKey(Name.MAIN_CLASS)
         ? String.format("%s.class", manifest.getMainAttributes().getValue(Name.MAIN_CLASS).replaceAll("\\.", "/"))
         : null;
   }
}
