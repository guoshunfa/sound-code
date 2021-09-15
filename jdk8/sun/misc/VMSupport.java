package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class VMSupport {
   private static Properties agentProps = null;

   public static synchronized Properties getAgentProperties() {
      if (agentProps == null) {
         agentProps = new Properties();
         initAgentProperties(agentProps);
      }

      return agentProps;
   }

   private static native Properties initAgentProperties(Properties var0);

   private static byte[] serializePropertiesToByteArray(Properties var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(4096);
      Properties var2 = new Properties();
      Set var3 = var0.stringPropertyNames();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String var6 = var0.getProperty(var5);
         var2.put(var5, var6);
      }

      var2.store((OutputStream)var1, (String)null);
      return var1.toByteArray();
   }

   public static byte[] serializePropertiesToByteArray() throws IOException {
      return serializePropertiesToByteArray(System.getProperties());
   }

   public static byte[] serializeAgentPropertiesToByteArray() throws IOException {
      return serializePropertiesToByteArray(getAgentProperties());
   }

   public static boolean isClassPathAttributePresent(String var0) {
      try {
         Manifest var1 = (new JarFile(var0)).getManifest();
         return var1 != null && var1.getMainAttributes().getValue(Attributes.Name.CLASS_PATH) != null;
      } catch (IOException var2) {
         throw new RuntimeException(var2.getMessage());
      }
   }

   public static native String getVMTemporaryDirectory();
}
