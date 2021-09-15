package java.security;

import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.jca.GetInstance;
import sun.security.jca.Providers;
import sun.security.util.Debug;

public class SecureRandom extends Random {
   private static final Debug pdebug = Debug.getInstance("provider", "Provider");
   private static final boolean skipDebug = Debug.isOn("engine=") && !Debug.isOn("securerandom");
   private Provider provider;
   private SecureRandomSpi secureRandomSpi;
   private String algorithm;
   private static volatile SecureRandom seedGenerator = null;
   static final long serialVersionUID = 4940670005562187L;
   private byte[] state;
   private MessageDigest digest;
   private byte[] randomBytes;
   private int randomBytesUsed;
   private long counter;

   public SecureRandom() {
      super(0L);
      this.provider = null;
      this.secureRandomSpi = null;
      this.digest = null;
      this.getDefaultPRNG(false, (byte[])null);
   }

   public SecureRandom(byte[] var1) {
      super(0L);
      this.provider = null;
      this.secureRandomSpi = null;
      this.digest = null;
      this.getDefaultPRNG(true, var1);
   }

   private void getDefaultPRNG(boolean var1, byte[] var2) {
      String var3 = getPrngAlgorithm();
      if (var3 == null) {
         var3 = "SHA1PRNG";
         this.secureRandomSpi = new sun.security.provider.SecureRandom();
         this.provider = Providers.getSunProvider();
         if (var1) {
            this.secureRandomSpi.engineSetSeed(var2);
         }
      } else {
         try {
            SecureRandom var4 = getInstance(var3);
            this.secureRandomSpi = var4.getSecureRandomSpi();
            this.provider = var4.getProvider();
            if (var1) {
               this.secureRandomSpi.engineSetSeed(var2);
            }
         } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
         }
      }

      if (this.getClass() == SecureRandom.class) {
         this.algorithm = var3;
      }

   }

   protected SecureRandom(SecureRandomSpi var1, Provider var2) {
      this(var1, var2, (String)null);
   }

   private SecureRandom(SecureRandomSpi var1, Provider var2, String var3) {
      super(0L);
      this.provider = null;
      this.secureRandomSpi = null;
      this.digest = null;
      this.secureRandomSpi = var1;
      this.provider = var2;
      this.algorithm = var3;
      if (!skipDebug && pdebug != null) {
         pdebug.println("SecureRandom." + var3 + " algorithm from: " + this.provider.getName());
      }

   }

   public static SecureRandom getInstance(String var0) throws NoSuchAlgorithmException {
      GetInstance.Instance var1 = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, var0);
      return new SecureRandom((SecureRandomSpi)var1.impl, var1.provider, var0);
   }

   public static SecureRandom getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, var0, var1);
      return new SecureRandom((SecureRandomSpi)var2.impl, var2.provider, var0);
   }

   public static SecureRandom getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("SecureRandom", SecureRandomSpi.class, var0, var1);
      return new SecureRandom((SecureRandomSpi)var2.impl, var2.provider, var0);
   }

   SecureRandomSpi getSecureRandomSpi() {
      return this.secureRandomSpi;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public String getAlgorithm() {
      return this.algorithm != null ? this.algorithm : "unknown";
   }

   public synchronized void setSeed(byte[] var1) {
      this.secureRandomSpi.engineSetSeed(var1);
   }

   public void setSeed(long var1) {
      if (var1 != 0L) {
         this.secureRandomSpi.engineSetSeed(longToByteArray(var1));
      }

   }

   public void nextBytes(byte[] var1) {
      this.secureRandomSpi.engineNextBytes(var1);
   }

   protected final int next(int var1) {
      int var2 = (var1 + 7) / 8;
      byte[] var3 = new byte[var2];
      int var4 = 0;
      this.nextBytes(var3);

      for(int var5 = 0; var5 < var2; ++var5) {
         var4 = (var4 << 8) + (var3[var5] & 255);
      }

      return var4 >>> var2 * 8 - var1;
   }

   public static byte[] getSeed(int var0) {
      if (seedGenerator == null) {
         seedGenerator = new SecureRandom();
      }

      return seedGenerator.generateSeed(var0);
   }

   public byte[] generateSeed(int var1) {
      return this.secureRandomSpi.engineGenerateSeed(var1);
   }

   private static byte[] longToByteArray(long var0) {
      byte[] var2 = new byte[8];

      for(int var3 = 0; var3 < 8; ++var3) {
         var2[var3] = (byte)((int)var0);
         var0 >>= 8;
      }

      return var2;
   }

   private static String getPrngAlgorithm() {
      Iterator var0 = Providers.getProviderList().providers().iterator();

      while(var0.hasNext()) {
         Provider var1 = (Provider)var0.next();
         Iterator var2 = var1.getServices().iterator();

         while(var2.hasNext()) {
            Provider.Service var3 = (Provider.Service)var2.next();
            if (var3.getType().equals("SecureRandom")) {
               return var3.getAlgorithm();
            }
         }
      }

      return null;
   }

   public static SecureRandom getInstanceStrong() throws NoSuchAlgorithmException {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("securerandom.strongAlgorithms");
         }
      });
      if (var0 != null && var0.length() != 0) {
         String var1 = var0;

         while(true) {
            while(var1 != null) {
               Matcher var2;
               if ((var2 = SecureRandom.StrongPatternHolder.pattern.matcher(var1)).matches()) {
                  String var3 = var2.group(1);
                  String var4 = var2.group(3);

                  try {
                     if (var4 == null) {
                        return getInstance(var3);
                     }

                     return getInstance(var3, var4);
                  } catch (NoSuchProviderException | NoSuchAlgorithmException var6) {
                     var1 = var2.group(5);
                  }
               } else {
                  var1 = null;
               }
            }

            throw new NoSuchAlgorithmException("No strong SecureRandom impls available: " + var0);
         }
      } else {
         throw new NoSuchAlgorithmException("Null/empty securerandom.strongAlgorithms Security Property");
      }
   }

   private static final class StrongPatternHolder {
      private static Pattern pattern = Pattern.compile("\\s*([\\S&&[^:,]]*)(\\:([\\S&&[^,]]*))?\\s*(\\,(.*))?");
   }
}
