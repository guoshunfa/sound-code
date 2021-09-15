package sun.management;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;

public class HotSpotDiagnostic implements HotSpotDiagnosticMXBean {
   public void dumpHeap(String var1, boolean var2) throws IOException {
      String var3 = "jdk.management.heapdump.allowAnyFileSuffix";
      PrivilegedAction var4 = () -> {
         return Boolean.parseBoolean(System.getProperty(var3, "false"));
      };
      boolean var5 = (Boolean)AccessController.doPrivileged(var4);
      if (!var5 && !var1.endsWith(".hprof")) {
         throw new IllegalArgumentException("heapdump file must have .hprof extention");
      } else {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkWrite(var1);
            Util.checkControlAccess();
         }

         this.dumpHeap0(var1, var2);
      }
   }

   private native void dumpHeap0(String var1, boolean var2) throws IOException;

   public List<VMOption> getDiagnosticOptions() {
      List var1 = Flag.getAllFlags();
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Flag var4 = (Flag)var3.next();
         if (var4.isWriteable() && var4.isExternal()) {
            var2.add(var4.getVMOption());
         }
      }

      return var2;
   }

   public VMOption getVMOption(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name cannot be null");
      } else {
         Flag var2 = Flag.getFlag(var1);
         if (var2 == null) {
            throw new IllegalArgumentException("VM option \"" + var1 + "\" does not exist");
         } else {
            return var2.getVMOption();
         }
      }
   }

   public void setVMOption(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("name cannot be null");
      } else if (var2 == null) {
         throw new NullPointerException("value cannot be null");
      } else {
         Util.checkControlAccess();
         Flag var3 = Flag.getFlag(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("VM option \"" + var1 + "\" does not exist");
         } else if (!var3.isWriteable()) {
            throw new IllegalArgumentException("VM Option \"" + var1 + "\" is not writeable");
         } else {
            Object var4 = var3.getValue();
            if (var4 instanceof Long) {
               try {
                  long var5 = Long.parseLong(var2);
                  Flag.setLongValue(var1, var5);
               } catch (NumberFormatException var7) {
                  IllegalArgumentException var6 = new IllegalArgumentException("Invalid value: VM Option \"" + var1 + "\" expects numeric value");
                  var6.initCause(var7);
                  throw var6;
               }
            } else if (var4 instanceof Boolean) {
               if (!var2.equalsIgnoreCase("true") && !var2.equalsIgnoreCase("false")) {
                  throw new IllegalArgumentException("Invalid value: VM Option \"" + var1 + "\" expects \"true\" or \"false\".");
               }

               Flag.setBooleanValue(var1, Boolean.parseBoolean(var2));
            } else {
               if (!(var4 instanceof String)) {
                  throw new IllegalArgumentException("VM Option \"" + var1 + "\" is of an unsupported type: " + var4.getClass().getName());
               }

               Flag.setStringValue(var1, var2);
            }

         }
      }
   }

   public ObjectName getObjectName() {
      return Util.newObjectName("com.sun.management:type=HotSpotDiagnostic");
   }
}
