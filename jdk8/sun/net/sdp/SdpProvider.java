package sun.net.sdp;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import sun.net.NetHooks;
import sun.security.action.GetPropertyAction;

public class SdpProvider extends NetHooks.Provider {
   private static final int MAX_PORT = 65535;
   private final boolean enabled;
   private final List<SdpProvider.Rule> rules;
   private PrintStream log;

   public SdpProvider() {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("com.sun.sdp.conf")));
      if (var1 == null) {
         this.enabled = false;
         this.rules = null;
      } else {
         List var2 = null;
         if (var1 != null) {
            try {
               var2 = loadRulesFromFile(var1);
            } catch (IOException var7) {
               fail("Error reading %s: %s", var1, var7.getMessage());
            }
         }

         PrintStream var3 = null;
         String var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("com.sun.sdp.debug")));
         if (var4 != null) {
            var3 = System.out;
            if (var4.length() > 0) {
               try {
                  var3 = new PrintStream(var4);
               } catch (IOException var6) {
               }
            }
         }

         this.enabled = !var2.isEmpty();
         this.rules = var2;
         this.log = var3;
      }
   }

   private static int[] parsePortRange(String var0) {
      int var1 = var0.indexOf(45);

      try {
         int[] var2 = new int[2];
         if (var1 < 0) {
            boolean var3 = var0.equals("*");
            var2[0] = var3 ? 0 : Integer.parseInt(var0);
            var2[1] = var3 ? '\uffff' : var2[0];
         } else {
            String var6 = var0.substring(0, var1);
            if (var6.length() == 0) {
               var6 = "*";
            }

            String var4 = var0.substring(var1 + 1);
            if (var4.length() == 0) {
               var4 = "*";
            }

            var2[0] = var6.equals("*") ? 0 : Integer.parseInt(var6);
            var2[1] = var4.equals("*") ? '\uffff' : Integer.parseInt(var4);
         }

         return var2;
      } catch (NumberFormatException var5) {
         return new int[0];
      }
   }

   private static void fail(String var0, Object... var1) {
      Formatter var2 = new Formatter();
      var2.format(var0, var1);
      throw new RuntimeException(var2.out().toString());
   }

   private static List<SdpProvider.Rule> loadRulesFromFile(String var0) throws IOException {
      Scanner var1 = new Scanner(new File(var0));

      try {
         ArrayList var2 = new ArrayList();

         while(var1.hasNextLine()) {
            String var3 = var1.nextLine().trim();
            if (var3.length() != 0 && var3.charAt(0) != '#') {
               String[] var4 = var3.split("\\s+");
               if (var4.length != 3) {
                  fail("Malformed line '%s'", var3);
               } else {
                  SdpProvider.Action var5 = null;
                  SdpProvider.Action[] var6 = SdpProvider.Action.values();
                  int var7 = var6.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     SdpProvider.Action var9 = var6[var8];
                     if (var4[0].equalsIgnoreCase(var9.name())) {
                        var5 = var9;
                        break;
                     }
                  }

                  if (var5 == null) {
                     fail("Action '%s' not recognized", var4[0]);
                  } else {
                     int[] var22 = parsePortRange(var4[2]);
                     if (var22.length == 0) {
                        fail("Malformed port range '%s'", var4[2]);
                     } else if (var4[1].equals("*")) {
                        var2.add(new SdpProvider.PortRangeRule(var5, var22[0], var22[1]));
                     } else {
                        var7 = var4[1].indexOf(47);

                        try {
                           if (var7 < 0) {
                              InetAddress[] var24 = InetAddress.getAllByName(var4[1]);
                              InetAddress[] var26 = var24;
                              int var10 = var24.length;

                              for(int var11 = 0; var11 < var10; ++var11) {
                                 InetAddress var12 = var26[var11];
                                 int var13 = var12 instanceof Inet4Address ? 32 : 128;
                                 var2.add(new SdpProvider.AddressPortRangeRule(var5, var12, var13, var22[0], var22[1]));
                              }
                           } else {
                              InetAddress var23 = InetAddress.getByName(var4[1].substring(0, var7));
                              int var25 = -1;

                              try {
                                 var25 = Integer.parseInt(var4[1].substring(var7 + 1));
                                 if (var23 instanceof Inet4Address) {
                                    if (var25 < 0 || var25 > 32) {
                                       var25 = -1;
                                    }
                                 } else if (var25 < 0 || var25 > 128) {
                                    var25 = -1;
                                 }
                              } catch (NumberFormatException var18) {
                              }

                              if (var25 > 0) {
                                 var2.add(new SdpProvider.AddressPortRangeRule(var5, var23, var25, var22[0], var22[1]));
                              } else {
                                 fail("Malformed prefix '%s'", var4[1]);
                              }
                           }
                        } catch (UnknownHostException var19) {
                           fail("Unknown host or malformed IP address '%s'", var4[1]);
                        }
                     }
                  }
               }
            }
         }

         ArrayList var21 = var2;
         return var21;
      } finally {
         var1.close();
      }
   }

   private void convertTcpToSdpIfMatch(FileDescriptor var1, SdpProvider.Action var2, InetAddress var3, int var4) throws IOException {
      boolean var5 = false;
      Iterator var6 = this.rules.iterator();

      while(var6.hasNext()) {
         SdpProvider.Rule var7 = (SdpProvider.Rule)var6.next();
         if (var7.match(var2, var3, var4)) {
            SdpSupport.convertSocket(var1);
            var5 = true;
            break;
         }
      }

      if (this.log != null) {
         String var8 = var3 instanceof Inet4Address ? var3.getHostAddress() : "[" + var3.getHostAddress() + "]";
         if (var5) {
            this.log.format("%s to %s:%d (socket converted to SDP protocol)\n", var2, var8, var4);
         } else {
            this.log.format("%s to %s:%d (no match)\n", var2, var8, var4);
         }
      }

   }

   public void implBeforeTcpBind(FileDescriptor var1, InetAddress var2, int var3) throws IOException {
      if (this.enabled) {
         this.convertTcpToSdpIfMatch(var1, SdpProvider.Action.BIND, var2, var3);
      }

   }

   public void implBeforeTcpConnect(FileDescriptor var1, InetAddress var2, int var3) throws IOException {
      if (this.enabled) {
         this.convertTcpToSdpIfMatch(var1, SdpProvider.Action.CONNECT, var2, var3);
      }

   }

   private static class AddressPortRangeRule extends SdpProvider.PortRangeRule {
      private final byte[] addressAsBytes;
      private final int prefixByteCount;
      private final byte mask;

      AddressPortRangeRule(SdpProvider.Action var1, InetAddress var2, int var3, int var4, int var5) {
         super(var1, var4, var5);
         this.addressAsBytes = var2.getAddress();
         this.prefixByteCount = var3 >> 3;
         this.mask = (byte)(255 << 8 - var3 % 8);
      }

      public boolean match(SdpProvider.Action var1, InetAddress var2, int var3) {
         if (var1 != this.action()) {
            return false;
         } else {
            byte[] var4 = var2.getAddress();
            if (var4.length != this.addressAsBytes.length) {
               return false;
            } else {
               for(int var5 = 0; var5 < this.prefixByteCount; ++var5) {
                  if (var4[var5] != this.addressAsBytes[var5]) {
                     return false;
                  }
               }

               if (this.prefixByteCount < this.addressAsBytes.length && (var4[this.prefixByteCount] & this.mask) != (this.addressAsBytes[this.prefixByteCount] & this.mask)) {
                  return false;
               } else {
                  return super.match(var1, var2, var3);
               }
            }
         }
      }
   }

   private static class PortRangeRule implements SdpProvider.Rule {
      private final SdpProvider.Action action;
      private final int portStart;
      private final int portEnd;

      PortRangeRule(SdpProvider.Action var1, int var2, int var3) {
         this.action = var1;
         this.portStart = var2;
         this.portEnd = var3;
      }

      SdpProvider.Action action() {
         return this.action;
      }

      public boolean match(SdpProvider.Action var1, InetAddress var2, int var3) {
         return var1 == this.action && var3 >= this.portStart && var3 <= this.portEnd;
      }
   }

   private interface Rule {
      boolean match(SdpProvider.Action var1, InetAddress var2, int var3);
   }

   private static enum Action {
      BIND,
      CONNECT;
   }
}
