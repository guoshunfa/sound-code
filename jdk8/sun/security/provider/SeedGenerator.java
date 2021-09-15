package sun.security.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import sun.security.util.Debug;

abstract class SeedGenerator {
   private static SeedGenerator instance;
   private static final Debug debug = Debug.getInstance("provider");

   public static void generateSeed(byte[] var0) {
      instance.getSeedBytes(var0);
   }

   abstract void getSeedBytes(byte[] var1);

   static byte[] getSystemEntropy() {
      final MessageDigest var1;
      try {
         var1 = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException var3) {
         throw new InternalError("internal error: SHA-1 not available.", var3);
      }

      byte var2 = (byte)((int)System.currentTimeMillis());
      var1.update(var2);
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            try {
               Properties var2 = System.getProperties();
               Enumeration var3 = var2.propertyNames();

               while(var3.hasMoreElements()) {
                  String var1x = (String)var3.nextElement();
                  var1.update(var1x.getBytes());
                  var1.update(var2.getProperty(var1x).getBytes());
               }

               SeedGenerator.addNetworkAdapterInfo(var1);
               File var4 = new File(var2.getProperty("java.io.tmpdir"));
               int var5 = 0;
               DirectoryStream var6 = Files.newDirectoryStream(var4.toPath());
               Throwable var7 = null;

               try {
                  Random var8 = new Random();
                  Iterator var9 = var6.iterator();

                  while(var9.hasNext()) {
                     Path var10 = (Path)var9.next();
                     if (var5 < 512 || var8.nextBoolean()) {
                        var1.update(var10.getFileName().toString().getBytes());
                     }

                     if (var5++ > 1024) {
                        break;
                     }
                  }
               } catch (Throwable var19) {
                  var7 = var19;
                  throw var19;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var18) {
                           var7.addSuppressed(var18);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (Exception var21) {
               var1.update((byte)var21.hashCode());
            }

            Runtime var22 = Runtime.getRuntime();
            byte[] var23 = SeedGenerator.longToByteArray(var22.totalMemory());
            var1.update(var23, 0, var23.length);
            var23 = SeedGenerator.longToByteArray(var22.freeMemory());
            var1.update(var23, 0, var23.length);
            return null;
         }
      });
      return var1.digest();
   }

   private static void addNetworkAdapterInfo(MessageDigest var0) {
      try {
         Enumeration var1 = NetworkInterface.getNetworkInterfaces();

         while(var1.hasMoreElements()) {
            NetworkInterface var2 = (NetworkInterface)var1.nextElement();
            var0.update(var2.toString().getBytes());
            if (!var2.isVirtual()) {
               byte[] var3 = var2.getHardwareAddress();
               if (var3 != null) {
                  var0.update(var3);
                  break;
               }
            }
         }
      } catch (Exception var4) {
      }

   }

   private static byte[] longToByteArray(long var0) {
      byte[] var2 = new byte[8];

      for(int var3 = 0; var3 < 8; ++var3) {
         var2[var3] = (byte)((int)var0);
         var0 >>= 8;
      }

      return var2;
   }

   static {
      String var0 = SunEntries.getSeedSource();
      if (!var0.equals("file:/dev/random") && !var0.equals("file:/dev/urandom")) {
         if (var0.length() != 0) {
            try {
               instance = new SeedGenerator.URLSeedGenerator(var0);
               if (debug != null) {
                  debug.println("Using URL seed generator reading from " + var0);
               }
            } catch (IOException var2) {
               if (debug != null) {
                  debug.println("Failed to create seed generator with " + var0 + ": " + var2.toString());
               }
            }
         }
      } else {
         try {
            instance = new NativeSeedGenerator(var0);
            if (debug != null) {
               debug.println("Using operating system seed generator" + var0);
            }
         } catch (IOException var3) {
            if (debug != null) {
               debug.println("Failed to use operating system seed generator: " + var3.toString());
            }
         }
      }

      if (instance == null) {
         if (debug != null) {
            debug.println("Using default threaded seed generator");
         }

         instance = new SeedGenerator.ThreadedSeedGenerator();
      }

   }

   static class URLSeedGenerator extends SeedGenerator {
      private String deviceName;
      private InputStream seedStream;

      URLSeedGenerator(String var1) throws IOException {
         if (var1 == null) {
            throw new IOException("No random source specified");
         } else {
            this.deviceName = var1;
            this.init();
         }
      }

      private void init() throws IOException {
         final URL var1 = new URL(this.deviceName);

         try {
            this.seedStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
               public InputStream run() throws IOException {
                  if (var1.getProtocol().equalsIgnoreCase("file")) {
                     File var1x = SunEntries.getDeviceFile(var1);
                     return new FileInputStream(var1x);
                  } else {
                     return var1.openStream();
                  }
               }
            });
         } catch (Exception var3) {
            throw new IOException("Failed to open " + this.deviceName, var3.getCause());
         }
      }

      void getSeedBytes(byte[] var1) {
         int var2 = var1.length;
         int var3 = 0;

         try {
            while(var3 < var2) {
               int var4 = this.seedStream.read(var1, var3, var2 - var3);
               if (var4 < 0) {
                  throw new InternalError("URLSeedGenerator " + this.deviceName + " reached end of file");
               }

               var3 += var4;
            }

         } catch (IOException var5) {
            throw new InternalError("URLSeedGenerator " + this.deviceName + " generated exception: " + var5.getMessage(), var5);
         }
      }
   }

   private static class ThreadedSeedGenerator extends SeedGenerator implements Runnable {
      private byte[] pool = new byte[20];
      private int start;
      private int end;
      private int count;
      ThreadGroup seedGroup;
      private static byte[] rndTab = new byte[]{56, 30, -107, -6, -86, 25, -83, 75, -12, -64, 5, -128, 78, 21, 16, 32, 70, -81, 37, -51, -43, -46, -108, 87, 29, 17, -55, 22, -11, -111, -115, 84, -100, 108, -45, -15, -98, 72, -33, -28, 31, -52, -37, -117, -97, -27, 93, -123, 47, 126, -80, -62, -93, -79, 61, -96, -65, -5, -47, -119, 14, 89, 81, -118, -88, 20, 67, -126, -113, 60, -102, 55, 110, 28, 85, 121, 122, -58, 2, 45, 43, 24, -9, 103, -13, 102, -68, -54, -101, -104, 19, 13, -39, -26, -103, 62, 77, 51, 44, 111, 73, 18, -127, -82, 4, -30, 11, -99, -74, 40, -89, 42, -76, -77, -94, -35, -69, 35, 120, 76, 33, -73, -7, 82, -25, -10, 88, 125, -112, 58, 83, 95, 6, 10, 98, -34, 80, 15, -91, 86, -19, 52, -17, 117, 49, -63, 118, -90, 36, -116, -40, -71, 97, -53, -109, -85, 109, -16, -3, 104, -95, 68, 54, 34, 26, 114, -1, 106, -121, 3, 66, 0, 100, -84, 57, 107, 119, -42, 112, -61, 1, 48, 38, 12, -56, -57, 39, -106, -72, 41, 7, 71, -29, -59, -8, -38, 79, -31, 124, -124, 8, 91, 116, 99, -4, 9, -36, -78, 63, -49, -67, -87, 59, 101, -32, 92, 94, 53, -41, 115, -66, -70, -122, 50, -50, -22, -20, -18, -21, 23, -2, -48, 96, 65, -105, 123, -14, -110, 69, -24, -120, -75, 74, 127, -60, 113, 90, -114, 105, 46, 27, -125, -23, -44, 64};

      ThreadedSeedGenerator() {
         this.start = this.end = 0;

         try {
            MessageDigest var1 = MessageDigest.getInstance("SHA");
         } catch (NoSuchAlgorithmException var4) {
            throw new InternalError("internal error: SHA-1 not available.", var4);
         }

         final ThreadGroup[] var2 = new ThreadGroup[1];
         Thread var3 = (Thread)AccessController.doPrivileged(new PrivilegedAction<Thread>() {
            public Thread run() {
               ThreadGroup var1;
               ThreadGroup var2x;
               for(var2x = Thread.currentThread().getThreadGroup(); (var1 = var2x.getParent()) != null; var2x = var1) {
               }

               var2[0] = new ThreadGroup(var2x, "SeedGenerator ThreadGroup");
               Thread var3 = new Thread(var2[0], ThreadedSeedGenerator.this, "SeedGenerator Thread");
               var3.setPriority(1);
               var3.setDaemon(true);
               return var3;
            }
         });
         this.seedGroup = var2[0];
         var3.start();
      }

      public final void run() {
         try {
            while(true) {
               synchronized(this) {
                  while(this.count >= this.pool.length) {
                     this.wait();
                  }
               }

               byte var3 = 0;
               int var2 = 0;

               for(int var1 = 0; var1 < 64000 && var2 < 6; ++var2) {
                  try {
                     SeedGenerator.ThreadedSeedGenerator.BogusThread var4 = new SeedGenerator.ThreadedSeedGenerator.BogusThread();
                     Thread var5 = new Thread(this.seedGroup, var4, "SeedGenerator Thread");
                     var5.start();
                  } catch (Exception var12) {
                     throw new InternalError("internal error: SeedGenerator thread creation error.", var12);
                  }

                  int var15 = 0;

                  for(long var16 = System.currentTimeMillis() + 250L; System.currentTimeMillis() < var16; ++var15) {
                     synchronized(this) {
                        ;
                     }
                  }

                  var3 ^= rndTab[var15 % 255];
                  var1 += var15;
               }

               synchronized(this) {
                  this.pool[this.end] = var3;
                  ++this.end;
                  ++this.count;
                  if (this.end >= this.pool.length) {
                     this.end = 0;
                  }

                  this.notifyAll();
               }
            }
         } catch (Exception var14) {
            throw new InternalError("internal error: SeedGenerator thread generated an exception.", var14);
         }
      }

      void getSeedBytes(byte[] var1) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = this.getSeedByte();
         }

      }

      byte getSeedByte() {
         try {
            synchronized(this) {
               while(true) {
                  if (this.count > 0) {
                     break;
                  }

                  this.wait();
               }
            }
         } catch (Exception var7) {
            if (this.count <= 0) {
               throw new InternalError("internal error: SeedGenerator thread generated an exception.", var7);
            }
         }

         synchronized(this) {
            byte var1 = this.pool[this.start];
            this.pool[this.start] = 0;
            ++this.start;
            --this.count;
            if (this.start == this.pool.length) {
               this.start = 0;
            }

            this.notifyAll();
            return var1;
         }
      }

      private static class BogusThread implements Runnable {
         private BogusThread() {
         }

         public final void run() {
            try {
               for(int var1 = 0; var1 < 5; ++var1) {
                  Thread.sleep(50L);
               }
            } catch (Exception var2) {
            }

         }

         // $FF: synthetic method
         BogusThread(Object var1) {
            this();
         }
      }
   }
}
