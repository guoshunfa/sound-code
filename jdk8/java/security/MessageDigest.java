package java.security;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import sun.security.util.Debug;

public abstract class MessageDigest extends MessageDigestSpi {
   private static final Debug pdebug = Debug.getInstance("provider", "Provider");
   private static final boolean skipDebug = Debug.isOn("engine=") && !Debug.isOn("messagedigest");
   private String algorithm;
   private static final int INITIAL = 0;
   private static final int IN_PROGRESS = 1;
   private int state = 0;
   private Provider provider;

   protected MessageDigest(String var1) {
      this.algorithm = var1;
   }

   public static MessageDigest getInstance(String var0) throws NoSuchAlgorithmException {
      try {
         Object[] var2 = Security.getImpl(var0, "MessageDigest", (String)null);
         Object var1;
         if (var2[0] instanceof MessageDigest) {
            var1 = (MessageDigest)var2[0];
         } else {
            var1 = new MessageDigest.Delegate((MessageDigestSpi)var2[0], var0);
         }

         ((MessageDigest)var1).provider = (Provider)var2[1];
         if (!skipDebug && pdebug != null) {
            pdebug.println("MessageDigest." + var0 + " algorithm from: " + ((MessageDigest)var1).provider.getName());
         }

         return (MessageDigest)var1;
      } catch (NoSuchProviderException var3) {
         throw new NoSuchAlgorithmException(var0 + " not found");
      }
   }

   public static MessageDigest getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         Object[] var2 = Security.getImpl(var0, "MessageDigest", var1);
         if (var2[0] instanceof MessageDigest) {
            MessageDigest var4 = (MessageDigest)var2[0];
            var4.provider = (Provider)var2[1];
            return var4;
         } else {
            MessageDigest.Delegate var3 = new MessageDigest.Delegate((MessageDigestSpi)var2[0], var0);
            var3.provider = (Provider)var2[1];
            return var3;
         }
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static MessageDigest getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Object[] var2 = Security.getImpl(var0, "MessageDigest", var1);
         if (var2[0] instanceof MessageDigest) {
            MessageDigest var4 = (MessageDigest)var2[0];
            var4.provider = (Provider)var2[1];
            return var4;
         } else {
            MessageDigest.Delegate var3 = new MessageDigest.Delegate((MessageDigestSpi)var2[0], var0);
            var3.provider = (Provider)var2[1];
            return var3;
         }
      }
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public void update(byte var1) {
      this.engineUpdate(var1);
      this.state = 1;
   }

   public void update(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("No input buffer given");
      } else if (var1.length - var2 < var3) {
         throw new IllegalArgumentException("Input buffer too short");
      } else {
         this.engineUpdate(var1, var2, var3);
         this.state = 1;
      }
   }

   public void update(byte[] var1) {
      this.engineUpdate(var1, 0, var1.length);
      this.state = 1;
   }

   public final void update(ByteBuffer var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.engineUpdate(var1);
         this.state = 1;
      }
   }

   public byte[] digest() {
      byte[] var1 = this.engineDigest();
      this.state = 0;
      return var1;
   }

   public int digest(byte[] var1, int var2, int var3) throws DigestException {
      if (var1 == null) {
         throw new IllegalArgumentException("No output buffer given");
      } else if (var1.length - var2 < var3) {
         throw new IllegalArgumentException("Output buffer too small for specified offset and length");
      } else {
         int var4 = this.engineDigest(var1, var2, var3);
         this.state = 0;
         return var4;
      }
   }

   public byte[] digest(byte[] var1) {
      this.update(var1);
      return this.digest();
   }

   public String toString() {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      PrintStream var2 = new PrintStream(var1);
      var2.print(this.algorithm + " Message Digest from " + this.provider.getName() + ", ");
      switch(this.state) {
      case 0:
         var2.print("<initialized>");
         break;
      case 1:
         var2.print("<in progress>");
      }

      var2.println();
      return var1.toString();
   }

   public static boolean isEqual(byte[] var0, byte[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         } else {
            int var2 = 0;

            for(int var3 = 0; var3 < var0.length; ++var3) {
               var2 |= var0[var3] ^ var1[var3];
            }

            return var2 == 0;
         }
      } else {
         return false;
      }
   }

   public void reset() {
      this.engineReset();
      this.state = 0;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public final int getDigestLength() {
      int var1 = this.engineGetDigestLength();
      if (var1 == 0) {
         try {
            MessageDigest var2 = (MessageDigest)this.clone();
            byte[] var3 = var2.digest();
            return var3.length;
         } catch (CloneNotSupportedException var4) {
            return var1;
         }
      } else {
         return var1;
      }
   }

   public Object clone() throws CloneNotSupportedException {
      if (this instanceof Cloneable) {
         return super.clone();
      } else {
         throw new CloneNotSupportedException();
      }
   }

   static class Delegate extends MessageDigest {
      private MessageDigestSpi digestSpi;

      public Delegate(MessageDigestSpi var1, String var2) {
         super(var2);
         this.digestSpi = var1;
      }

      public Object clone() throws CloneNotSupportedException {
         if (this.digestSpi instanceof Cloneable) {
            MessageDigestSpi var1 = (MessageDigestSpi)this.digestSpi.clone();
            MessageDigest.Delegate var2 = new MessageDigest.Delegate(var1, super.algorithm);
            var2.provider = super.provider;
            var2.state = super.state;
            return var2;
         } else {
            throw new CloneNotSupportedException();
         }
      }

      protected int engineGetDigestLength() {
         return this.digestSpi.engineGetDigestLength();
      }

      protected void engineUpdate(byte var1) {
         this.digestSpi.engineUpdate(var1);
      }

      protected void engineUpdate(byte[] var1, int var2, int var3) {
         this.digestSpi.engineUpdate(var1, var2, var3);
      }

      protected void engineUpdate(ByteBuffer var1) {
         this.digestSpi.engineUpdate(var1);
      }

      protected byte[] engineDigest() {
         return this.digestSpi.engineDigest();
      }

      protected int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
         return this.digestSpi.engineDigest(var1, var2, var3);
      }

      protected void engineReset() {
         this.digestSpi.engineReset();
      }
   }
}
