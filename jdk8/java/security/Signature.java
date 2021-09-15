package java.security;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.security.jca.GetInstance;
import sun.security.jca.ServiceId;
import sun.security.util.Debug;

public abstract class Signature extends SignatureSpi {
   private static final Debug debug = Debug.getInstance("jca", "Signature");
   private static final Debug pdebug = Debug.getInstance("provider", "Provider");
   private static final boolean skipDebug = Debug.isOn("engine=") && !Debug.isOn("signature");
   private String algorithm;
   Provider provider;
   protected static final int UNINITIALIZED = 0;
   protected static final int SIGN = 2;
   protected static final int VERIFY = 3;
   protected int state = 0;
   private static final String RSA_SIGNATURE = "NONEwithRSA";
   private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
   private static final List<ServiceId> rsaIds = Arrays.asList(new ServiceId("Signature", "NONEwithRSA"), new ServiceId("Cipher", "RSA/ECB/PKCS1Padding"), new ServiceId("Cipher", "RSA/ECB"), new ServiceId("Cipher", "RSA//PKCS1Padding"), new ServiceId("Cipher", "RSA"));
   private static final Map<String, Boolean> signatureInfo = new ConcurrentHashMap();

   protected Signature(String var1) {
      this.algorithm = var1;
   }

   public static Signature getInstance(String var0) throws NoSuchAlgorithmException {
      List var1;
      if (var0.equalsIgnoreCase("NONEwithRSA")) {
         var1 = GetInstance.getServices(rsaIds);
      } else {
         var1 = GetInstance.getServices("Signature", var0);
      }

      Iterator var2 = var1.iterator();
      if (!var2.hasNext()) {
         throw new NoSuchAlgorithmException(var0 + " Signature not available");
      } else {
         while(true) {
            Provider.Service var4 = (Provider.Service)var2.next();
            if (isSpi(var4)) {
               return new Signature.Delegate(var4, var2, var0);
            }

            try {
               GetInstance.Instance var5 = GetInstance.getInstance(var4, SignatureSpi.class);
               return getInstance(var5, var0);
            } catch (NoSuchAlgorithmException var6) {
               if (!var2.hasNext()) {
                  throw var6;
               }
            }
         }
      }
   }

   private static Signature getInstance(GetInstance.Instance var0, String var1) {
      Object var2;
      if (var0.impl instanceof Signature) {
         var2 = (Signature)var0.impl;
         ((Signature)var2).algorithm = var1;
      } else {
         SignatureSpi var3 = (SignatureSpi)var0.impl;
         var2 = new Signature.Delegate(var3, var1);
      }

      ((Signature)var2).provider = var0.provider;
      return (Signature)var2;
   }

   private static boolean isSpi(Provider.Service var0) {
      if (var0.getType().equals("Cipher")) {
         return true;
      } else {
         String var1 = var0.getClassName();
         Boolean var2 = (Boolean)signatureInfo.get(var1);
         if (var2 == null) {
            try {
               Object var3 = var0.newInstance((Object)null);
               boolean var4 = var3 instanceof SignatureSpi && !(var3 instanceof Signature);
               if (debug != null && !var4) {
                  debug.println("Not a SignatureSpi " + var1);
                  debug.println("Delayed provider selection may not be available for algorithm " + var0.getAlgorithm());
               }

               var2 = var4;
               signatureInfo.put(var1, var2);
            } catch (Exception var5) {
               return false;
            }
         }

         return var2;
      }
   }

   public static Signature getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var0.equalsIgnoreCase("NONEwithRSA")) {
         if (var1 != null && var1.length() != 0) {
            Provider var3 = Security.getProvider(var1);
            if (var3 == null) {
               throw new NoSuchProviderException("no such provider: " + var1);
            } else {
               return getInstanceRSA(var3);
            }
         } else {
            throw new IllegalArgumentException("missing provider");
         }
      } else {
         GetInstance.Instance var2 = GetInstance.getInstance("Signature", SignatureSpi.class, var0, var1);
         return getInstance(var2, var0);
      }
   }

   public static Signature getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var0.equalsIgnoreCase("NONEwithRSA")) {
         if (var1 == null) {
            throw new IllegalArgumentException("missing provider");
         } else {
            return getInstanceRSA(var1);
         }
      } else {
         GetInstance.Instance var2 = GetInstance.getInstance("Signature", SignatureSpi.class, var0, var1);
         return getInstance(var2, var0);
      }
   }

   private static Signature getInstanceRSA(Provider var0) throws NoSuchAlgorithmException {
      Provider.Service var1 = var0.getService("Signature", "NONEwithRSA");
      if (var1 != null) {
         GetInstance.Instance var4 = GetInstance.getInstance(var1, SignatureSpi.class);
         return getInstance(var4, "NONEwithRSA");
      } else {
         try {
            Cipher var2 = Cipher.getInstance("RSA/ECB/PKCS1Padding", var0);
            return new Signature.Delegate(new Signature.CipherAdapter(var2), "NONEwithRSA");
         } catch (GeneralSecurityException var3) {
            throw new NoSuchAlgorithmException("no such algorithm: NONEwithRSA for provider " + var0.getName(), var3);
         }
      }
   }

   public final Provider getProvider() {
      this.chooseFirstProvider();
      return this.provider;
   }

   private String getProviderName() {
      return this.provider == null ? "(no provider)" : this.provider.getName();
   }

   void chooseFirstProvider() {
   }

   public final void initVerify(PublicKey var1) throws InvalidKeyException {
      this.engineInitVerify(var1);
      this.state = 3;
      if (!skipDebug && pdebug != null) {
         pdebug.println("Signature." + this.algorithm + " verification algorithm from: " + this.getProviderName());
      }

   }

   public final void initVerify(java.security.cert.Certificate var1) throws InvalidKeyException {
      if (var1 instanceof X509Certificate) {
         X509Certificate var2 = (X509Certificate)var1;
         Set var3 = var2.getCriticalExtensionOIDs();
         if (var3 != null && !var3.isEmpty() && var3.contains("2.5.29.15")) {
            boolean[] var4 = var2.getKeyUsage();
            if (var4 != null && !var4[0]) {
               throw new InvalidKeyException("Wrong key usage");
            }
         }
      }

      PublicKey var5 = var1.getPublicKey();
      this.engineInitVerify(var5);
      this.state = 3;
      if (!skipDebug && pdebug != null) {
         pdebug.println("Signature." + this.algorithm + " verification algorithm from: " + this.getProviderName());
      }

   }

   public final void initSign(PrivateKey var1) throws InvalidKeyException {
      this.engineInitSign(var1);
      this.state = 2;
      if (!skipDebug && pdebug != null) {
         pdebug.println("Signature." + this.algorithm + " signing algorithm from: " + this.getProviderName());
      }

   }

   public final void initSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
      this.engineInitSign(var1, var2);
      this.state = 2;
      if (!skipDebug && pdebug != null) {
         pdebug.println("Signature." + this.algorithm + " signing algorithm from: " + this.getProviderName());
      }

   }

   public final byte[] sign() throws SignatureException {
      if (this.state == 2) {
         return this.engineSign();
      } else {
         throw new SignatureException("object not initialized for signing");
      }
   }

   public final int sign(byte[] var1, int var2, int var3) throws SignatureException {
      if (var1 == null) {
         throw new IllegalArgumentException("No output buffer given");
      } else if (var2 >= 0 && var3 >= 0) {
         if (var1.length - var2 < var3) {
            throw new IllegalArgumentException("Output buffer too small for specified offset and length");
         } else if (this.state != 2) {
            throw new SignatureException("object not initialized for signing");
         } else {
            return this.engineSign(var1, var2, var3);
         }
      } else {
         throw new IllegalArgumentException("offset or len is less than 0");
      }
   }

   public final boolean verify(byte[] var1) throws SignatureException {
      if (this.state == 3) {
         return this.engineVerify(var1);
      } else {
         throw new SignatureException("object not initialized for verification");
      }
   }

   public final boolean verify(byte[] var1, int var2, int var3) throws SignatureException {
      if (this.state == 3) {
         if (var1 == null) {
            throw new IllegalArgumentException("signature is null");
         } else if (var2 >= 0 && var3 >= 0) {
            if (var1.length - var2 < var3) {
               throw new IllegalArgumentException("signature too small for specified offset and length");
            } else {
               return this.engineVerify(var1, var2, var3);
            }
         } else {
            throw new IllegalArgumentException("offset or length is less than 0");
         }
      } else {
         throw new SignatureException("object not initialized for verification");
      }
   }

   public final void update(byte var1) throws SignatureException {
      if (this.state != 3 && this.state != 2) {
         throw new SignatureException("object not initialized for signature or verification");
      } else {
         this.engineUpdate(var1);
      }
   }

   public final void update(byte[] var1) throws SignatureException {
      this.update(var1, 0, var1.length);
   }

   public final void update(byte[] var1, int var2, int var3) throws SignatureException {
      if (this.state != 2 && this.state != 3) {
         throw new SignatureException("object not initialized for signature or verification");
      } else if (var1 == null) {
         throw new IllegalArgumentException("data is null");
      } else if (var2 >= 0 && var3 >= 0) {
         if (var1.length - var2 < var3) {
            throw new IllegalArgumentException("data too small for specified offset and length");
         } else {
            this.engineUpdate(var1, var2, var3);
         }
      } else {
         throw new IllegalArgumentException("off or len is less than 0");
      }
   }

   public final void update(ByteBuffer var1) throws SignatureException {
      if (this.state != 2 && this.state != 3) {
         throw new SignatureException("object not initialized for signature or verification");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.engineUpdate(var1);
      }
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public String toString() {
      String var1 = "";
      switch(this.state) {
      case 0:
         var1 = "<not initialized>";
      case 1:
      default:
         break;
      case 2:
         var1 = "<initialized for signing>";
         break;
      case 3:
         var1 = "<initialized for verifying>";
      }

      return "Signature object: " + this.getAlgorithm() + var1;
   }

   /** @deprecated */
   @Deprecated
   public final void setParameter(String var1, Object var2) throws InvalidParameterException {
      this.engineSetParameter(var1, var2);
   }

   public final void setParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      this.engineSetParameter(var1);
   }

   public final AlgorithmParameters getParameters() {
      return this.engineGetParameters();
   }

   /** @deprecated */
   @Deprecated
   public final Object getParameter(String var1) throws InvalidParameterException {
      return this.engineGetParameter(var1);
   }

   public Object clone() throws CloneNotSupportedException {
      if (this instanceof Cloneable) {
         return super.clone();
      } else {
         throw new CloneNotSupportedException();
      }
   }

   static {
      Boolean var0 = Boolean.TRUE;
      signatureInfo.put("sun.security.provider.DSA$RawDSA", var0);
      signatureInfo.put("sun.security.provider.DSA$SHA1withDSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$MD2withRSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$MD5withRSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$SHA1withRSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$SHA256withRSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$SHA384withRSA", var0);
      signatureInfo.put("sun.security.rsa.RSASignature$SHA512withRSA", var0);
      signatureInfo.put("com.sun.net.ssl.internal.ssl.RSASignature", var0);
      signatureInfo.put("sun.security.pkcs11.P11Signature", var0);
   }

   private static class CipherAdapter extends SignatureSpi {
      private final Cipher cipher;
      private ByteArrayOutputStream data;

      CipherAdapter(Cipher var1) {
         this.cipher = var1;
      }

      protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
         this.cipher.init(2, var1);
         if (this.data == null) {
            this.data = new ByteArrayOutputStream(128);
         } else {
            this.data.reset();
         }

      }

      protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
         this.cipher.init(1, var1);
         this.data = null;
      }

      protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
         this.cipher.init(1, var1, var2);
         this.data = null;
      }

      protected void engineUpdate(byte var1) throws SignatureException {
         this.engineUpdate(new byte[]{var1}, 0, 1);
      }

      protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
         if (this.data != null) {
            this.data.write(var1, var2, var3);
         } else {
            byte[] var4 = this.cipher.update(var1, var2, var3);
            if (var4 != null && var4.length != 0) {
               throw new SignatureException("Cipher unexpectedly returned data");
            }
         }
      }

      protected byte[] engineSign() throws SignatureException {
         try {
            return this.cipher.doFinal();
         } catch (IllegalBlockSizeException var2) {
            throw new SignatureException("doFinal() failed", var2);
         } catch (BadPaddingException var3) {
            throw new SignatureException("doFinal() failed", var3);
         }
      }

      protected boolean engineVerify(byte[] var1) throws SignatureException {
         try {
            byte[] var2 = this.cipher.doFinal(var1);
            byte[] var3 = this.data.toByteArray();
            this.data.reset();
            return MessageDigest.isEqual(var2, var3);
         } catch (BadPaddingException var4) {
            return false;
         } catch (IllegalBlockSizeException var5) {
            throw new SignatureException("doFinal() failed", var5);
         }
      }

      protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
         throw new InvalidParameterException("Parameters not supported");
      }

      protected Object engineGetParameter(String var1) throws InvalidParameterException {
         throw new InvalidParameterException("Parameters not supported");
      }
   }

   private static class Delegate extends Signature {
      private SignatureSpi sigSpi;
      private final Object lock;
      private Provider.Service firstService;
      private Iterator<Provider.Service> serviceIterator;
      private static int warnCount = 10;
      private static final int I_PUB = 1;
      private static final int I_PRIV = 2;
      private static final int I_PRIV_SR = 3;

      Delegate(SignatureSpi var1, String var2) {
         super(var2);
         this.sigSpi = var1;
         this.lock = null;
      }

      Delegate(Provider.Service var1, Iterator<Provider.Service> var2, String var3) {
         super(var3);
         this.firstService = var1;
         this.serviceIterator = var2;
         this.lock = new Object();
      }

      public Object clone() throws CloneNotSupportedException {
         this.chooseFirstProvider();
         if (this.sigSpi instanceof Cloneable) {
            SignatureSpi var1 = (SignatureSpi)this.sigSpi.clone();
            Signature.Delegate var2 = new Signature.Delegate(var1, super.algorithm);
            var2.provider = super.provider;
            return var2;
         } else {
            throw new CloneNotSupportedException();
         }
      }

      private static SignatureSpi newInstance(Provider.Service var0) throws NoSuchAlgorithmException {
         if (var0.getType().equals("Cipher")) {
            try {
               Cipher var3 = Cipher.getInstance("RSA/ECB/PKCS1Padding", var0.getProvider());
               return new Signature.CipherAdapter(var3);
            } catch (NoSuchPaddingException var2) {
               throw new NoSuchAlgorithmException(var2);
            }
         } else {
            Object var1 = var0.newInstance((Object)null);
            if (!(var1 instanceof SignatureSpi)) {
               throw new NoSuchAlgorithmException("Not a SignatureSpi: " + var1.getClass().getName());
            } else {
               return (SignatureSpi)var1;
            }
         }
      }

      void chooseFirstProvider() {
         if (this.sigSpi == null) {
            synchronized(this.lock) {
               if (this.sigSpi == null) {
                  if (Signature.debug != null) {
                     int var2 = --warnCount;
                     if (var2 >= 0) {
                        Signature.debug.println("Signature.init() not first method called, disabling delayed provider selection");
                        if (var2 == 0) {
                           Signature.debug.println("Further warnings of this type will be suppressed");
                        }

                        (new Exception("Call trace")).printStackTrace();
                     }
                  }

                  NoSuchAlgorithmException var8 = null;

                  while(true) {
                     Provider.Service var3;
                     do {
                        if (this.firstService == null && !this.serviceIterator.hasNext()) {
                           ProviderException var9 = new ProviderException("Could not construct SignatureSpi instance");
                           if (var8 != null) {
                              var9.initCause(var8);
                           }

                           throw var9;
                        }

                        if (this.firstService != null) {
                           var3 = this.firstService;
                           this.firstService = null;
                        } else {
                           var3 = (Provider.Service)this.serviceIterator.next();
                        }
                     } while(!Signature.isSpi(var3));

                     try {
                        this.sigSpi = newInstance(var3);
                        this.provider = var3.getProvider();
                        this.firstService = null;
                        this.serviceIterator = null;
                        return;
                     } catch (NoSuchAlgorithmException var6) {
                        var8 = var6;
                     }
                  }
               }
            }
         }
      }

      private void chooseProvider(int var1, Key var2, SecureRandom var3) throws InvalidKeyException {
         synchronized(this.lock) {
            if (this.sigSpi != null) {
               this.init(this.sigSpi, var1, var2, var3);
            } else {
               Exception var5 = null;

               while(true) {
                  Provider.Service var6;
                  do {
                     do {
                        if (this.firstService == null && !this.serviceIterator.hasNext()) {
                           if (var5 instanceof InvalidKeyException) {
                              throw (InvalidKeyException)var5;
                           }

                           if (var5 instanceof RuntimeException) {
                              throw (RuntimeException)var5;
                           }

                           String var11 = var2 != null ? var2.getClass().getName() : "(null)";
                           throw new InvalidKeyException("No installed provider supports this key: " + var11, var5);
                        }

                        if (this.firstService != null) {
                           var6 = this.firstService;
                           this.firstService = null;
                        } else {
                           var6 = (Provider.Service)this.serviceIterator.next();
                        }
                     } while(!var6.supportsParameter(var2));
                  } while(!Signature.isSpi(var6));

                  try {
                     SignatureSpi var7 = newInstance(var6);
                     this.init(var7, var1, var2, var3);
                     this.provider = var6.getProvider();
                     this.sigSpi = var7;
                     this.firstService = null;
                     this.serviceIterator = null;
                     return;
                  } catch (Exception var9) {
                     if (var5 == null) {
                        var5 = var9;
                     }
                  }
               }
            }
         }
      }

      private void init(SignatureSpi var1, int var2, Key var3, SecureRandom var4) throws InvalidKeyException {
         switch(var2) {
         case 1:
            var1.engineInitVerify((PublicKey)var3);
            break;
         case 2:
            var1.engineInitSign((PrivateKey)var3);
            break;
         case 3:
            var1.engineInitSign((PrivateKey)var3, var4);
            break;
         default:
            throw new AssertionError("Internal error: " + var2);
         }

      }

      protected void engineInitVerify(PublicKey var1) throws InvalidKeyException {
         if (this.sigSpi != null) {
            this.sigSpi.engineInitVerify(var1);
         } else {
            this.chooseProvider(1, var1, (SecureRandom)null);
         }

      }

      protected void engineInitSign(PrivateKey var1) throws InvalidKeyException {
         if (this.sigSpi != null) {
            this.sigSpi.engineInitSign(var1);
         } else {
            this.chooseProvider(2, var1, (SecureRandom)null);
         }

      }

      protected void engineInitSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException {
         if (this.sigSpi != null) {
            this.sigSpi.engineInitSign(var1, var2);
         } else {
            this.chooseProvider(3, var1, var2);
         }

      }

      protected void engineUpdate(byte var1) throws SignatureException {
         this.chooseFirstProvider();
         this.sigSpi.engineUpdate(var1);
      }

      protected void engineUpdate(byte[] var1, int var2, int var3) throws SignatureException {
         this.chooseFirstProvider();
         this.sigSpi.engineUpdate(var1, var2, var3);
      }

      protected void engineUpdate(ByteBuffer var1) {
         this.chooseFirstProvider();
         this.sigSpi.engineUpdate(var1);
      }

      protected byte[] engineSign() throws SignatureException {
         this.chooseFirstProvider();
         return this.sigSpi.engineSign();
      }

      protected int engineSign(byte[] var1, int var2, int var3) throws SignatureException {
         this.chooseFirstProvider();
         return this.sigSpi.engineSign(var1, var2, var3);
      }

      protected boolean engineVerify(byte[] var1) throws SignatureException {
         this.chooseFirstProvider();
         return this.sigSpi.engineVerify(var1);
      }

      protected boolean engineVerify(byte[] var1, int var2, int var3) throws SignatureException {
         this.chooseFirstProvider();
         return this.sigSpi.engineVerify(var1, var2, var3);
      }

      protected void engineSetParameter(String var1, Object var2) throws InvalidParameterException {
         this.chooseFirstProvider();
         this.sigSpi.engineSetParameter(var1, var2);
      }

      protected void engineSetParameter(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
         this.chooseFirstProvider();
         this.sigSpi.engineSetParameter(var1);
      }

      protected Object engineGetParameter(String var1) throws InvalidParameterException {
         this.chooseFirstProvider();
         return this.sigSpi.engineGetParameter(var1);
      }

      protected AlgorithmParameters engineGetParameters() {
         this.chooseFirstProvider();
         return this.sigSpi.engineGetParameters();
      }
   }
}
