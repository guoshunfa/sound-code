package sun.management.jdp;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import sun.management.VMManagement;

public final class JdpController {
   private static JdpController.JDPControllerRunner controller = null;

   private JdpController() {
   }

   private static int getInteger(String var0, int var1, String var2) throws JdpException {
      try {
         return var0 == null ? var1 : Integer.parseInt(var0);
      } catch (NumberFormatException var4) {
         throw new JdpException(var2);
      }
   }

   private static InetAddress getInetAddress(String var0, InetAddress var1, String var2) throws JdpException {
      try {
         return var0 == null ? var1 : InetAddress.getByName(var0);
      } catch (UnknownHostException var4) {
         throw new JdpException(var2);
      }
   }

   private static Integer getProcessId() {
      try {
         RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
         Field var1 = var0.getClass().getDeclaredField("jvm");
         var1.setAccessible(true);
         VMManagement var2 = (VMManagement)var1.get(var0);
         Method var3 = var2.getClass().getDeclaredMethod("getProcessId");
         var3.setAccessible(true);
         Integer var4 = (Integer)var3.invoke(var2);
         return var4;
      } catch (Exception var5) {
         return null;
      }
   }

   public static synchronized void startDiscoveryService(InetAddress var0, int var1, String var2, String var3) throws IOException, JdpException {
      int var4 = getInteger(System.getProperty("com.sun.management.jdp.ttl"), 1, "Invalid jdp packet ttl");
      int var5 = getInteger(System.getProperty("com.sun.management.jdp.pause"), 5, "Invalid jdp pause");
      var5 *= 1000;
      InetAddress var6 = getInetAddress(System.getProperty("com.sun.management.jdp.source_addr"), (InetAddress)null, "Invalid source address provided");
      UUID var7 = UUID.randomUUID();
      JdpJmxPacket var8 = new JdpJmxPacket(var7, var3);
      String var9 = System.getProperty("sun.java.command");
      if (var9 != null) {
         String[] var10 = var9.split(" ", 2);
         var8.setMainClass(var10[0]);
      }

      var8.setInstanceName(var2);
      String var14 = System.getProperty("java.rmi.server.hostname");
      var8.setRmiHostname(var14);
      var8.setBroadcastInterval((new Integer(var5)).toString());
      Integer var11 = getProcessId();
      if (var11 != null) {
         var8.setProcessId(var11.toString());
      }

      JdpBroadcaster var12 = new JdpBroadcaster(var0, var6, var1, var4);
      stopDiscoveryService();
      controller = new JdpController.JDPControllerRunner(var12, var8, var5);
      Thread var13 = new Thread(controller, "JDP broadcaster");
      var13.setDaemon(true);
      var13.start();
   }

   public static synchronized void stopDiscoveryService() {
      if (controller != null) {
         controller.stop();
         controller = null;
      }

   }

   private static class JDPControllerRunner implements Runnable {
      private final JdpJmxPacket packet;
      private final JdpBroadcaster bcast;
      private final int pause;
      private volatile boolean shutdown;

      private JDPControllerRunner(JdpBroadcaster var1, JdpJmxPacket var2, int var3) {
         this.shutdown = false;
         this.bcast = var1;
         this.packet = var2;
         this.pause = var3;
      }

      public void run() {
         while(true) {
            try {
               if (!this.shutdown) {
                  this.bcast.sendPacket(this.packet);

                  try {
                     Thread.sleep((long)this.pause);
                  } catch (InterruptedException var3) {
                  }
                  continue;
               }
            } catch (IOException var4) {
            }

            try {
               this.stop();
               this.bcast.shutdown();
            } catch (IOException var2) {
            }

            return;
         }
      }

      public void stop() {
         this.shutdown = true;
      }

      // $FF: synthetic method
      JDPControllerRunner(JdpBroadcaster var1, JdpJmxPacket var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
