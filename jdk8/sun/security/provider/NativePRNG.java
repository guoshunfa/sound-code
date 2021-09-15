package sun.security.provider;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProviderException;
import java.security.SecureRandomSpi;
import java.util.Arrays;
import sun.security.util.Debug;

public final class NativePRNG extends SecureRandomSpi {
   private static final long serialVersionUID = -6599091113397072932L;
   private static final Debug debug = Debug.getInstance("provider");
   private static final String NAME_RANDOM = "/dev/random";
   private static final String NAME_URANDOM = "/dev/urandom";
   private static final NativePRNG.RandomIO INSTANCE;

   private static URL getEgdUrl() {
      String var0 = SunEntries.getSeedSource();
      URL var1;
      if (var0.length() != 0) {
         if (debug != null) {
            debug.println("NativePRNG egdUrl: " + var0);
         }

         try {
            var1 = new URL(var0);
            if (!var1.getProtocol().equalsIgnoreCase("file")) {
               return null;
            }
         } catch (MalformedURLException var3) {
            return null;
         }
      } else {
         var1 = null;
      }

      return var1;
   }

   private static NativePRNG.RandomIO initIO(final NativePRNG.Variant var0) {
      return (NativePRNG.RandomIO)AccessController.doPrivileged(new PrivilegedAction<NativePRNG.RandomIO>() {
         public NativePRNG.RandomIO run() {
            File var1;
            File var2;
            switch(var0) {
            case MIXED:
               File var4 = null;
               URL var3;
               if ((var3 = NativePRNG.getEgdUrl()) != null) {
                  try {
                     var4 = SunEntries.getDeviceFile(var3);
                  } catch (IOException var7) {
                  }
               }

               if (var4 != null && var4.canRead()) {
                  var1 = var4;
               } else {
                  var1 = new File("/dev/random");
               }

               var2 = new File("/dev/urandom");
               break;
            case BLOCKING:
               var1 = new File("/dev/random");
               var2 = new File("/dev/random");
               break;
            case NONBLOCKING:
               var1 = new File("/dev/urandom");
               var2 = new File("/dev/urandom");
               break;
            default:
               return null;
            }

            if (NativePRNG.debug != null) {
               NativePRNG.debug.println("NativePRNG." + var0 + " seedFile: " + var1 + " nextFile: " + var2);
            }

            if (var1.canRead() && var2.canRead()) {
               try {
                  return new NativePRNG.RandomIO(var1, var2);
               } catch (Exception var6) {
                  return null;
               }
            } else {
               if (NativePRNG.debug != null) {
                  NativePRNG.debug.println("NativePRNG." + var0 + " Couldn't read Files.");
               }

               return null;
            }
         }
      });
   }

   static boolean isAvailable() {
      return INSTANCE != null;
   }

   public NativePRNG() {
      if (INSTANCE == null) {
         throw new AssertionError("NativePRNG not available");
      }
   }

   protected void engineSetSeed(byte[] var1) {
      INSTANCE.implSetSeed(var1);
   }

   protected void engineNextBytes(byte[] var1) {
      INSTANCE.implNextBytes(var1);
   }

   protected byte[] engineGenerateSeed(int var1) {
      return INSTANCE.implGenerateSeed(var1);
   }

   static {
      INSTANCE = initIO(NativePRNG.Variant.MIXED);
   }

   private static class RandomIO {
      private static final long MAX_BUFFER_TIME = 100L;
      private static final int MAX_BUFFER_SIZE = 65536;
      private static final int MIN_BUFFER_SIZE = 32;
      private int bufferSize;
      File seedFile;
      private final InputStream seedIn;
      private final InputStream nextIn;
      private OutputStream seedOut;
      private boolean seedOutInitialized;
      private volatile SecureRandom mixRandom;
      private byte[] nextBuffer;
      private int buffered;
      private long lastRead;
      private int change_buffer;
      private static final int REQ_LIMIT_INC = 1000;
      private static final int REQ_LIMIT_DEC = -100;
      private final Object LOCK_GET_BYTES;
      private final Object LOCK_GET_SEED;
      private final Object LOCK_SET_SEED;

      private RandomIO(File var1, File var2) throws IOException {
         this.bufferSize = 256;
         this.change_buffer = 0;
         this.LOCK_GET_BYTES = new Object();
         this.LOCK_GET_SEED = new Object();
         this.LOCK_SET_SEED = new Object();
         this.seedFile = var1;
         this.seedIn = new FileInputStream(var1);
         this.nextIn = new FileInputStream(var2);
         this.nextBuffer = new byte[this.bufferSize];
      }

      private SecureRandom getMixRandom() {
         SecureRandom var1 = this.mixRandom;
         if (var1 == null) {
            synchronized(this.LOCK_GET_BYTES) {
               var1 = this.mixRandom;
               if (var1 == null) {
                  var1 = new SecureRandom();

                  try {
                     byte[] var3 = new byte[20];
                     readFully(this.nextIn, var3);
                     var1.engineSetSeed(var3);
                  } catch (IOException var5) {
                     throw new ProviderException("init failed", var5);
                  }

                  this.mixRandom = var1;
               }
            }
         }

         return var1;
      }

      private static void readFully(InputStream var0, byte[] var1) throws IOException {
         int var2 = var1.length;

         int var4;
         for(int var3 = 0; var2 > 0; var2 -= var4) {
            var4 = var0.read(var1, var3, var2);
            if (var4 <= 0) {
               throw new EOFException("File(s) closed?");
            }

            var3 += var4;
         }

         if (var2 > 0) {
            throw new IOException("Could not read from file(s)");
         }
      }

      private byte[] implGenerateSeed(int var1) {
         synchronized(this.LOCK_GET_SEED) {
            byte[] var10000;
            try {
               byte[] var3 = new byte[var1];
               readFully(this.seedIn, var3);
               var10000 = var3;
            } catch (IOException var5) {
               throw new ProviderException("generateSeed() failed", var5);
            }

            return var10000;
         }
      }

      private void implSetSeed(byte[] var1) {
         synchronized(this.LOCK_SET_SEED) {
            if (!this.seedOutInitialized) {
               this.seedOutInitialized = true;
               this.seedOut = (OutputStream)AccessController.doPrivileged(new PrivilegedAction<OutputStream>() {
                  public OutputStream run() {
                     try {
                        return new FileOutputStream(RandomIO.this.seedFile, true);
                     } catch (Exception var2) {
                        return null;
                     }
                  }
               });
            }

            if (this.seedOut != null) {
               try {
                  this.seedOut.write(var1);
               } catch (IOException var5) {
                  throw new ProviderException("setSeed() failed", var5);
               }
            }

            this.getMixRandom().engineSetSeed(var1);
         }
      }

      private void ensureBufferValid() throws IOException {
         long var1 = System.currentTimeMillis();
         int var3 = 0;
         if (this.buffered > 0) {
            if (var1 - this.lastRead < 100L) {
               return;
            }

            --this.change_buffer;
         } else {
            ++this.change_buffer;
         }

         if (this.change_buffer > 1000) {
            var3 = this.nextBuffer.length * 2;
         } else if (this.change_buffer < -100) {
            var3 = this.nextBuffer.length / 2;
         }

         if (var3 > 0) {
            if (var3 <= 65536 && var3 >= 32) {
               this.nextBuffer = new byte[var3];
               if (NativePRNG.debug != null) {
                  NativePRNG.debug.println("Buffer size changed to " + var3);
               }
            } else if (NativePRNG.debug != null) {
               NativePRNG.debug.println("Buffer reached limit: " + this.nextBuffer.length);
            }

            this.change_buffer = 0;
         }

         this.lastRead = var1;
         readFully(this.nextIn, this.nextBuffer);
         this.buffered = this.nextBuffer.length;
      }

      private void implNextBytes(byte[] var1) {
         try {
            this.getMixRandom().engineNextBytes(var1);
            int var2 = var1.length;

            int var4;
            for(int var3 = 0; var2 > 0; var2 -= var4) {
               byte[] var7;
               synchronized(this.LOCK_GET_BYTES) {
                  this.ensureBufferValid();
                  int var5 = this.nextBuffer.length - this.buffered;
                  if (var2 > this.buffered) {
                     var4 = this.buffered;
                     this.buffered = 0;
                  } else {
                     var4 = var2;
                     this.buffered -= var2;
                  }

                  var7 = Arrays.copyOfRange(this.nextBuffer, var5, var5 + var4);
               }

               for(int var6 = 0; var4 > var6; ++var6) {
                  var1[var3] ^= var7[var6];
                  ++var3;
               }
            }

         } catch (IOException var11) {
            throw new ProviderException("nextBytes() failed", var11);
         }
      }

      // $FF: synthetic method
      RandomIO(File var1, File var2, Object var3) throws IOException {
         this(var1, var2);
      }
   }

   public static final class NonBlocking extends SecureRandomSpi {
      private static final long serialVersionUID = -1102062982994105487L;
      private static final NativePRNG.RandomIO INSTANCE;

      static boolean isAvailable() {
         return INSTANCE != null;
      }

      public NonBlocking() {
         if (INSTANCE == null) {
            throw new AssertionError("NativePRNG$NonBlocking not available");
         }
      }

      protected void engineSetSeed(byte[] var1) {
         INSTANCE.implSetSeed(var1);
      }

      protected void engineNextBytes(byte[] var1) {
         INSTANCE.implNextBytes(var1);
      }

      protected byte[] engineGenerateSeed(int var1) {
         return INSTANCE.implGenerateSeed(var1);
      }

      static {
         INSTANCE = NativePRNG.initIO(NativePRNG.Variant.NONBLOCKING);
      }
   }

   public static final class Blocking extends SecureRandomSpi {
      private static final long serialVersionUID = -6396183145759983347L;
      private static final NativePRNG.RandomIO INSTANCE;

      static boolean isAvailable() {
         return INSTANCE != null;
      }

      public Blocking() {
         if (INSTANCE == null) {
            throw new AssertionError("NativePRNG$Blocking not available");
         }
      }

      protected void engineSetSeed(byte[] var1) {
         INSTANCE.implSetSeed(var1);
      }

      protected void engineNextBytes(byte[] var1) {
         INSTANCE.implNextBytes(var1);
      }

      protected byte[] engineGenerateSeed(int var1) {
         return INSTANCE.implGenerateSeed(var1);
      }

      static {
         INSTANCE = NativePRNG.initIO(NativePRNG.Variant.BLOCKING);
      }
   }

   private static enum Variant {
      MIXED,
      BLOCKING,
      NONBLOCKING;
   }
}
