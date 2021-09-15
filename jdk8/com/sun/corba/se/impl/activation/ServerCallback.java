package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation._ServerImplBase;
import java.lang.reflect.Method;
import org.omg.CORBA.ORB;

class ServerCallback extends _ServerImplBase {
   private ORB orb;
   private transient Method installMethod;
   private transient Method uninstallMethod;
   private transient Method shutdownMethod;
   private Object[] methodArgs;

   ServerCallback(ORB var1, Method var2, Method var3, Method var4) {
      this.orb = var1;
      this.installMethod = var2;
      this.uninstallMethod = var3;
      this.shutdownMethod = var4;
      var1.connect(this);
      this.methodArgs = new Object[]{var1};
   }

   private void invokeMethod(Method var1) {
      if (var1 != null) {
         try {
            var1.invoke((Object)null, this.methodArgs);
         } catch (Exception var3) {
            ServerMain.logError("could not invoke " + var1.getName() + " method: " + var3.getMessage());
         }
      }

   }

   public void shutdown() {
      ServerMain.logInformation("Shutdown starting");
      this.invokeMethod(this.shutdownMethod);
      this.orb.shutdown(true);
      ServerMain.logTerminal("Shutdown completed", 0);
   }

   public void install() {
      ServerMain.logInformation("Install starting");
      this.invokeMethod(this.installMethod);
      ServerMain.logInformation("Install completed");
   }

   public void uninstall() {
      ServerMain.logInformation("uninstall starting");
      this.invokeMethod(this.uninstallMethod);
      ServerMain.logInformation("uninstall completed");
   }
}
