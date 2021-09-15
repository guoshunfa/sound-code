package java.util.jar;

import com.sun.java.util.jar.pack.PackerImpl;
import com.sun.java.util.jar.pack.UnpackerImpl;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedMap;
import sun.security.action.GetPropertyAction;

public abstract class Pack200 {
   private static final String PACK_PROVIDER = "java.util.jar.Pack200.Packer";
   private static final String UNPACK_PROVIDER = "java.util.jar.Pack200.Unpacker";
   private static Class<?> packerImpl;
   private static Class<?> unpackerImpl;

   private Pack200() {
   }

   public static synchronized Pack200.Packer newPacker() {
      return (Pack200.Packer)newInstance("java.util.jar.Pack200.Packer");
   }

   public static Pack200.Unpacker newUnpacker() {
      return (Pack200.Unpacker)newInstance("java.util.jar.Pack200.Unpacker");
   }

   private static synchronized Object newInstance(String var0) {
      String var1 = "(unknown)";

      try {
         Class var2 = "java.util.jar.Pack200.Packer".equals(var0) ? packerImpl : unpackerImpl;
         if (var2 == null) {
            var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0, "")));
            if (var1 != null && !var1.equals("")) {
               var2 = Class.forName(var1);
            } else if ("java.util.jar.Pack200.Packer".equals(var0)) {
               var2 = PackerImpl.class;
            } else {
               var2 = UnpackerImpl.class;
            }
         }

         return var2.newInstance();
      } catch (ClassNotFoundException var3) {
         throw new Error("Class not found: " + var1 + ":\ncheck property " + var0 + " in your properties file.", var3);
      } catch (InstantiationException var4) {
         throw new Error("Could not instantiate: " + var1 + ":\ncheck property " + var0 + " in your properties file.", var4);
      } catch (IllegalAccessException var5) {
         throw new Error("Cannot access class: " + var1 + ":\ncheck property " + var0 + " in your properties file.", var5);
      }
   }

   public interface Unpacker {
      String KEEP = "keep";
      String TRUE = "true";
      String FALSE = "false";
      String DEFLATE_HINT = "unpack.deflate.hint";
      String PROGRESS = "unpack.progress";

      SortedMap<String, String> properties();

      void unpack(InputStream var1, JarOutputStream var2) throws IOException;

      void unpack(File var1, JarOutputStream var2) throws IOException;

      /** @deprecated */
      @Deprecated
      default void addPropertyChangeListener(PropertyChangeListener var1) {
      }

      /** @deprecated */
      @Deprecated
      default void removePropertyChangeListener(PropertyChangeListener var1) {
      }
   }

   public interface Packer {
      String SEGMENT_LIMIT = "pack.segment.limit";
      String KEEP_FILE_ORDER = "pack.keep.file.order";
      String EFFORT = "pack.effort";
      String DEFLATE_HINT = "pack.deflate.hint";
      String MODIFICATION_TIME = "pack.modification.time";
      String PASS_FILE_PFX = "pack.pass.file.";
      String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";
      String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
      String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
      String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
      String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
      String PROGRESS = "pack.progress";
      String KEEP = "keep";
      String PASS = "pass";
      String STRIP = "strip";
      String ERROR = "error";
      String TRUE = "true";
      String FALSE = "false";
      String LATEST = "latest";

      SortedMap<String, String> properties();

      void pack(JarFile var1, OutputStream var2) throws IOException;

      void pack(JarInputStream var1, OutputStream var2) throws IOException;

      /** @deprecated */
      @Deprecated
      default void addPropertyChangeListener(PropertyChangeListener var1) {
      }

      /** @deprecated */
      @Deprecated
      default void removePropertyChangeListener(PropertyChangeListener var1) {
      }
   }
}
