package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer;
import com.sun.org.apache.xml.internal.security.c14n.InvalidCanonicalizerException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.transforms.InvalidTransformException;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLCipher {
   private static Logger log = Logger.getLogger(XMLCipher.class.getName());
   public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
   public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
   public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
   public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
   public static final String AES_128_GCM = "http://www.w3.org/2009/xmlenc11#aes128-gcm";
   public static final String AES_192_GCM = "http://www.w3.org/2009/xmlenc11#aes192-gcm";
   public static final String AES_256_GCM = "http://www.w3.org/2009/xmlenc11#aes256-gcm";
   public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
   public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
   public static final String RSA_OAEP_11 = "http://www.w3.org/2009/xmlenc11#rsa-oaep";
   public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
   public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
   public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
   public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
   public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
   public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
   public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
   public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
   public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
   public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
   public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
   public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
   public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
   public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
   public static final String PHYSICAL_XML_N14C = "http://santuario.apache.org/c14n/physical";
   public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
   public static final int ENCRYPT_MODE = 1;
   public static final int DECRYPT_MODE = 2;
   public static final int UNWRAP_MODE = 4;
   public static final int WRAP_MODE = 3;
   private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n";
   private Cipher contextCipher;
   private int cipherMode = Integer.MIN_VALUE;
   private String algorithm = null;
   private String requestedJCEProvider = null;
   private Canonicalizer canon;
   private Document contextDocument;
   private XMLCipher.Factory factory;
   private Serializer serializer;
   private Key key;
   private Key kek;
   private EncryptedKey ek;
   private EncryptedData ed;
   private SecureRandom random;
   private boolean secureValidation;
   private String digestAlg;
   private List<KeyResolverSpi> internalKeyResolvers;

   public void setSerializer(Serializer var1) {
      this.serializer = var1;
      var1.setCanonicalizer(this.canon);
   }

   public Serializer getSerializer() {
      return this.serializer;
   }

   private XMLCipher(String var1, String var2, String var3, String var4) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Constructing XMLCipher...");
      }

      this.factory = new XMLCipher.Factory();
      this.algorithm = var1;
      this.requestedJCEProvider = var2;
      this.digestAlg = var4;

      try {
         if (var3 == null) {
            this.canon = Canonicalizer.getInstance("http://santuario.apache.org/c14n/physical");
         } else {
            this.canon = Canonicalizer.getInstance(var3);
         }
      } catch (InvalidCanonicalizerException var6) {
         throw new XMLEncryptionException("empty", var6);
      }

      if (this.serializer == null) {
         this.serializer = new DocumentSerializer();
      }

      this.serializer.setCanonicalizer(this.canon);
      if (var1 != null) {
         this.contextCipher = this.constructCipher(var1, var4);
      }

   }

   private static boolean isValidEncryptionAlgorithm(String var0) {
      return var0.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc") || var0.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc") || var0.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc") || var0.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc") || var0.equals("http://www.w3.org/2009/xmlenc11#aes128-gcm") || var0.equals("http://www.w3.org/2009/xmlenc11#aes192-gcm") || var0.equals("http://www.w3.org/2009/xmlenc11#aes256-gcm") || var0.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5") || var0.equals("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p") || var0.equals("http://www.w3.org/2009/xmlenc11#rsa-oaep") || var0.equals("http://www.w3.org/2001/04/xmlenc#kw-tripledes") || var0.equals("http://www.w3.org/2001/04/xmlenc#kw-aes128") || var0.equals("http://www.w3.org/2001/04/xmlenc#kw-aes256") || var0.equals("http://www.w3.org/2001/04/xmlenc#kw-aes192");
   }

   private static void validateTransformation(String var0) {
      if (null == var0) {
         throw new NullPointerException("Transformation unexpectedly null...");
      } else {
         if (!isValidEncryptionAlgorithm(var0)) {
            log.log(Level.WARNING, "Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2009/xmlenc11#rsa-oaep\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\nhttp://www.w3.org/2009/xmlenc11#aes128-gcm\nhttp://www.w3.org/2009/xmlenc11#aes192-gcm\nhttp://www.w3.org/2009/xmlenc11#aes256-gcm\n");
         }

      }
   }

   public static XMLCipher getInstance(String var0) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation");
      }

      validateTransformation(var0);
      return new XMLCipher(var0, (String)null, (String)null, (String)null);
   }

   public static XMLCipher getInstance(String var0, String var1) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm");
      }

      validateTransformation(var0);
      return new XMLCipher(var0, (String)null, var1, (String)null);
   }

   public static XMLCipher getInstance(String var0, String var1, String var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation and c14n algorithm");
      }

      validateTransformation(var0);
      return new XMLCipher(var0, (String)null, var1, var2);
   }

   public static XMLCipher getProviderInstance(String var0, String var1) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation and provider");
      }

      if (null == var1) {
         throw new NullPointerException("Provider unexpectedly null..");
      } else {
         validateTransformation(var0);
         return new XMLCipher(var0, var1, (String)null, (String)null);
      }
   }

   public static XMLCipher getProviderInstance(String var0, String var1, String var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm");
      }

      if (null == var1) {
         throw new NullPointerException("Provider unexpectedly null..");
      } else {
         validateTransformation(var0);
         return new XMLCipher(var0, var1, var2, (String)null);
      }
   }

   public static XMLCipher getProviderInstance(String var0, String var1, String var2, String var3) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with transformation, provider and c14n algorithm");
      }

      if (null == var1) {
         throw new NullPointerException("Provider unexpectedly null..");
      } else {
         validateTransformation(var0);
         return new XMLCipher(var0, var1, var2, var3);
      }
   }

   public static XMLCipher getInstance() throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with no arguments");
      }

      return new XMLCipher((String)null, (String)null, (String)null, (String)null);
   }

   public static XMLCipher getProviderInstance(String var0) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Getting XMLCipher with provider");
      }

      return new XMLCipher((String)null, var0, (String)null, (String)null);
   }

   public void init(int var1, Key var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Initializing XMLCipher...");
      }

      this.ek = null;
      this.ed = null;
      switch(var1) {
      case 1:
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "opmode = ENCRYPT_MODE");
         }

         this.ed = this.createEncryptedData(1, "NO VALUE YET");
         break;
      case 2:
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "opmode = DECRYPT_MODE");
         }
         break;
      case 3:
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "opmode = WRAP_MODE");
         }

         this.ek = this.createEncryptedKey(1, "NO VALUE YET");
         break;
      case 4:
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "opmode = UNWRAP_MODE");
         }
         break;
      default:
         log.log(Level.SEVERE, "Mode unexpectedly invalid");
         throw new XMLEncryptionException("Invalid mode in init");
      }

      this.cipherMode = var1;
      this.key = var2;
   }

   public void setSecureValidation(boolean var1) {
      this.secureValidation = var1;
   }

   public void registerInternalKeyResolver(KeyResolverSpi var1) {
      if (this.internalKeyResolvers == null) {
         this.internalKeyResolvers = new ArrayList();
      }

      this.internalKeyResolvers.add(var1);
   }

   public EncryptedData getEncryptedData() {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Returning EncryptedData");
      }

      return this.ed;
   }

   public EncryptedKey getEncryptedKey() {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Returning EncryptedKey");
      }

      return this.ek;
   }

   public void setKEK(Key var1) {
      this.kek = var1;
   }

   public Element martial(EncryptedData var1) {
      return this.factory.toElement(var1);
   }

   public Element martial(Document var1, EncryptedData var2) {
      this.contextDocument = var1;
      return this.factory.toElement(var2);
   }

   public Element martial(EncryptedKey var1) {
      return this.factory.toElement(var1);
   }

   public Element martial(Document var1, EncryptedKey var2) {
      this.contextDocument = var1;
      return this.factory.toElement(var2);
   }

   public Element martial(ReferenceList var1) {
      return this.factory.toElement(var1);
   }

   public Element martial(Document var1, ReferenceList var2) {
      this.contextDocument = var1;
      return this.factory.toElement(var2);
   }

   private Document encryptElement(Element var1) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Encrypting element...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Element unexpectedly null...");
      }

      if (this.cipherMode != 1 && log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
      }

      if (this.algorithm == null) {
         throw new XMLEncryptionException("XMLCipher instance without transformation specified");
      } else {
         this.encryptData(this.contextDocument, var1, false);
         Element var2 = this.factory.toElement(this.ed);
         Node var3 = var1.getParentNode();
         var3.replaceChild(var2, var1);
         return this.contextDocument;
      }
   }

   private Document encryptElementContent(Element var1) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Encrypting element content...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Element unexpectedly null...");
      }

      if (this.cipherMode != 1 && log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
      }

      if (this.algorithm == null) {
         throw new XMLEncryptionException("XMLCipher instance without transformation specified");
      } else {
         this.encryptData(this.contextDocument, var1, true);
         Element var2 = this.factory.toElement(this.ed);
         removeContent(var1);
         var1.appendChild(var2);
         return this.contextDocument;
      }
   }

   public Document doFinal(Document var1, Document var2) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Processing source document...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Context document unexpectedly null...");
      }

      if (null == var2) {
         log.log(Level.SEVERE, "Source document unexpectedly null...");
      }

      this.contextDocument = var1;
      Document var3 = null;
      switch(this.cipherMode) {
      case 1:
         var3 = this.encryptElement(var2.getDocumentElement());
         break;
      case 2:
         var3 = this.decryptElement(var2.getDocumentElement());
      case 3:
      case 4:
         break;
      default:
         throw new XMLEncryptionException("empty", new IllegalStateException());
      }

      return var3;
   }

   public Document doFinal(Document var1, Element var2) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Processing source element...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Context document unexpectedly null...");
      }

      if (null == var2) {
         log.log(Level.SEVERE, "Source element unexpectedly null...");
      }

      this.contextDocument = var1;
      Document var3 = null;
      switch(this.cipherMode) {
      case 1:
         var3 = this.encryptElement(var2);
         break;
      case 2:
         var3 = this.decryptElement(var2);
      case 3:
      case 4:
         break;
      default:
         throw new XMLEncryptionException("empty", new IllegalStateException());
      }

      return var3;
   }

   public Document doFinal(Document var1, Element var2, boolean var3) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Processing source element...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Context document unexpectedly null...");
      }

      if (null == var2) {
         log.log(Level.SEVERE, "Source element unexpectedly null...");
      }

      this.contextDocument = var1;
      Document var4 = null;
      switch(this.cipherMode) {
      case 1:
         if (var3) {
            var4 = this.encryptElementContent(var2);
         } else {
            var4 = this.encryptElement(var2);
         }
         break;
      case 2:
         if (var3) {
            var4 = this.decryptElementContent(var2);
         } else {
            var4 = this.decryptElement(var2);
         }
      case 3:
      case 4:
         break;
      default:
         throw new XMLEncryptionException("empty", new IllegalStateException());
      }

      return var4;
   }

   public EncryptedData encryptData(Document var1, Element var2) throws Exception {
      return this.encryptData(var1, var2, false);
   }

   public EncryptedData encryptData(Document var1, String var2, InputStream var3) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Encrypting element...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Context document unexpectedly null...");
      }

      if (null == var3) {
         log.log(Level.SEVERE, "Serialized data unexpectedly null...");
      }

      if (this.cipherMode != 1 && log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
      }

      return this.encryptData(var1, (Element)null, var2, var3);
   }

   public EncryptedData encryptData(Document var1, Element var2, boolean var3) throws Exception {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Encrypting element...");
      }

      if (null == var1) {
         log.log(Level.SEVERE, "Context document unexpectedly null...");
      }

      if (null == var2) {
         log.log(Level.SEVERE, "Element unexpectedly null...");
      }

      if (this.cipherMode != 1 && log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in ENCRYPT_MODE...");
      }

      return var3 ? this.encryptData(var1, var2, "http://www.w3.org/2001/04/xmlenc#Content", (InputStream)null) : this.encryptData(var1, var2, "http://www.w3.org/2001/04/xmlenc#Element", (InputStream)null);
   }

   private EncryptedData encryptData(Document var1, Element var2, String var3, InputStream var4) throws Exception {
      this.contextDocument = var1;
      if (this.algorithm == null) {
         throw new XMLEncryptionException("XMLCipher instance without transformation specified");
      } else {
         byte[] var5 = null;
         NodeList var6;
         if (var4 == null) {
            if (var3.equals("http://www.w3.org/2001/04/xmlenc#Content")) {
               var6 = var2.getChildNodes();
               if (null == var6) {
                  Object[] var7 = new Object[]{"Element has no content."};
                  throw new XMLEncryptionException("empty", var7);
               }

               var5 = this.serializer.serializeToByteArray(var6);
            } else {
               var5 = this.serializer.serializeToByteArray(var2);
            }

            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Serialized octets:\n" + new String(var5, "UTF-8"));
            }
         }

         var6 = null;
         Cipher var22;
         if (this.contextCipher == null) {
            var22 = this.constructCipher(this.algorithm, (String)null);
         } else {
            var22 = this.contextCipher;
         }

         byte[] var8;
         try {
            if (!"http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(this.algorithm) && !"http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(this.algorithm) && !"http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(this.algorithm)) {
               var22.init(this.cipherMode, this.key);
            } else {
               if (this.random == null) {
                  this.random = SecureRandom.getInstance("SHA1PRNG");
               }

               var8 = new byte[12];
               this.random.nextBytes(var8);
               IvParameterSpec var9 = new IvParameterSpec(var8);
               var22.init(this.cipherMode, this.key, var9);
            }
         } catch (InvalidKeyException var19) {
            throw new XMLEncryptionException("empty", var19);
         } catch (NoSuchAlgorithmException var20) {
            throw new XMLEncryptionException("empty", var20);
         }

         byte[] var21;
         byte[] var24;
         try {
            if (var4 == null) {
               var21 = var22.doFinal(var5);
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, "Expected cipher.outputSize = " + Integer.toString(var22.getOutputSize(var5.length)));
               }
            } else {
               var24 = new byte[8192];
               ByteArrayOutputStream var10 = new ByteArrayOutputStream();

               int var23;
               while((var23 = var4.read(var24)) != -1) {
                  byte[] var11 = var22.update(var24, 0, var23);
                  var10.write(var11);
               }

               var10.write(var22.doFinal());
               var21 = var10.toByteArray();
            }

            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Actual cipher.outputSize = " + Integer.toString(var21.length));
            }
         } catch (IllegalStateException var15) {
            throw new XMLEncryptionException("empty", var15);
         } catch (IllegalBlockSizeException var16) {
            throw new XMLEncryptionException("empty", var16);
         } catch (BadPaddingException var17) {
            throw new XMLEncryptionException("empty", var17);
         } catch (UnsupportedEncodingException var18) {
            throw new XMLEncryptionException("empty", var18);
         }

         var8 = var22.getIV();
         var24 = new byte[var8.length + var21.length];
         System.arraycopy(var8, 0, var24, 0, var8.length);
         System.arraycopy(var21, 0, var24, var8.length, var21.length);
         String var25 = Base64.encode(var24);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Encrypted octets:\n" + var25);
            log.log(Level.FINE, "Encrypted octets length = " + var25.length());
         }

         try {
            CipherData var26 = this.ed.getCipherData();
            CipherValue var12 = var26.getCipherValue();
            var12.setValue(var25);
            if (var3 != null) {
               this.ed.setType((new URI(var3)).toString());
            }

            EncryptionMethod var13 = this.factory.newEncryptionMethod((new URI(this.algorithm)).toString());
            var13.setDigestAlgorithm(this.digestAlg);
            this.ed.setEncryptionMethod(var13);
         } catch (URISyntaxException var14) {
            throw new XMLEncryptionException("empty", var14);
         }

         return this.ed;
      }
   }

   public EncryptedData loadEncryptedData(Document var1, Element var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Loading encrypted element...");
      }

      if (null == var1) {
         throw new NullPointerException("Context document unexpectedly null...");
      } else if (null == var2) {
         throw new NullPointerException("Element unexpectedly null...");
      } else if (this.cipherMode != 2) {
         throw new XMLEncryptionException("XMLCipher unexpectedly not in DECRYPT_MODE...");
      } else {
         this.contextDocument = var1;
         this.ed = this.factory.newEncryptedData(var2);
         return this.ed;
      }
   }

   public EncryptedKey loadEncryptedKey(Document var1, Element var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Loading encrypted key...");
      }

      if (null == var1) {
         throw new NullPointerException("Context document unexpectedly null...");
      } else if (null == var2) {
         throw new NullPointerException("Element unexpectedly null...");
      } else if (this.cipherMode != 4 && this.cipherMode != 2) {
         throw new XMLEncryptionException("XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
      } else {
         this.contextDocument = var1;
         this.ek = this.factory.newEncryptedKey(var2);
         return this.ek;
      }
   }

   public EncryptedKey loadEncryptedKey(Element var1) throws XMLEncryptionException {
      return this.loadEncryptedKey(var1.getOwnerDocument(), var1);
   }

   public EncryptedKey encryptKey(Document var1, Key var2) throws XMLEncryptionException {
      return this.encryptKey(var1, var2, (String)null, (byte[])null);
   }

   public EncryptedKey encryptKey(Document var1, Key var2, String var3, byte[] var4) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Encrypting key ...");
      }

      if (null == var2) {
         log.log(Level.SEVERE, "Key unexpectedly null...");
      }

      if (this.cipherMode != 3) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in WRAP_MODE...");
      }

      if (this.algorithm == null) {
         throw new XMLEncryptionException("XMLCipher instance without transformation specified");
      } else {
         this.contextDocument = var1;
         Object var5 = null;
         Cipher var6;
         if (this.contextCipher == null) {
            var6 = this.constructCipher(this.algorithm, (String)null);
         } else {
            var6 = this.contextCipher;
         }

         byte[] var14;
         try {
            OAEPParameterSpec var7 = this.constructOAEPParameters(this.algorithm, this.digestAlg, var3, var4);
            if (var7 == null) {
               var6.init(3, this.key);
            } else {
               var6.init(3, this.key, var7);
            }

            var14 = var6.wrap(var2);
         } catch (InvalidKeyException var11) {
            throw new XMLEncryptionException("empty", var11);
         } catch (IllegalBlockSizeException var12) {
            throw new XMLEncryptionException("empty", var12);
         } catch (InvalidAlgorithmParameterException var13) {
            throw new XMLEncryptionException("empty", var13);
         }

         String var15 = Base64.encode(var14);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Encrypted key octets:\n" + var15);
            log.log(Level.FINE, "Encrypted key octets length = " + var15.length());
         }

         CipherValue var8 = this.ek.getCipherData().getCipherValue();
         var8.setValue(var15);

         try {
            EncryptionMethod var9 = this.factory.newEncryptionMethod((new URI(this.algorithm)).toString());
            var9.setDigestAlgorithm(this.digestAlg);
            var9.setMGFAlgorithm(var3);
            var9.setOAEPparams(var4);
            this.ek.setEncryptionMethod(var9);
         } catch (URISyntaxException var10) {
            throw new XMLEncryptionException("empty", var10);
         }

         return this.ek;
      }
   }

   public Key decryptKey(EncryptedKey var1, String var2) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Decrypting key from previously loaded EncryptedKey...");
      }

      if (this.cipherMode != 4 && log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "XMLCipher unexpectedly not in UNWRAP_MODE...");
      }

      if (var2 == null) {
         throw new XMLEncryptionException("Cannot decrypt a key without knowing the algorithm");
      } else {
         String var5;
         if (this.key == null) {
            if (log.isLoggable(Level.FINE)) {
               log.log(Level.FINE, "Trying to find a KEK via key resolvers");
            }

            KeyInfo var3 = var1.getKeyInfo();
            if (var3 != null) {
               var3.setSecureValidation(this.secureValidation);

               try {
                  String var4 = var1.getEncryptionMethod().getAlgorithm();
                  var5 = JCEMapper.getJCEKeyAlgorithmFromURI(var4);
                  if ("RSA".equals(var5)) {
                     this.key = var3.getPrivateKey();
                  } else {
                     this.key = var3.getSecretKey();
                  }
               } catch (Exception var13) {
                  if (log.isLoggable(Level.FINE)) {
                     log.log(Level.FINE, (String)var13.getMessage(), (Throwable)var13);
                  }
               }
            }

            if (this.key == null) {
               log.log(Level.SEVERE, "XMLCipher::decryptKey called without a KEK and cannot resolve");
               throw new XMLEncryptionException("Unable to decrypt without a KEK");
            }
         }

         XMLCipherInput var14 = new XMLCipherInput(var1);
         var14.setSecureValidation(this.secureValidation);
         byte[] var15 = var14.getBytes();
         var5 = JCEMapper.getJCEKeyAlgorithmFromURI(var2);
         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "JCE Key Algorithm: " + var5);
         }

         Cipher var6;
         if (this.contextCipher == null) {
            var6 = this.constructCipher(var1.getEncryptionMethod().getAlgorithm(), var1.getEncryptionMethod().getDigestAlgorithm());
         } else {
            var6 = this.contextCipher;
         }

         Key var7;
         try {
            EncryptionMethod var8 = var1.getEncryptionMethod();
            OAEPParameterSpec var9 = this.constructOAEPParameters(var8.getAlgorithm(), var8.getDigestAlgorithm(), var8.getMGFAlgorithm(), var8.getOAEPparams());
            if (var9 == null) {
               var6.init(4, this.key);
            } else {
               var6.init(4, this.key, var9);
            }

            var7 = var6.unwrap(var15, var5, 3);
         } catch (InvalidKeyException var10) {
            throw new XMLEncryptionException("empty", var10);
         } catch (NoSuchAlgorithmException var11) {
            throw new XMLEncryptionException("empty", var11);
         } catch (InvalidAlgorithmParameterException var12) {
            throw new XMLEncryptionException("empty", var12);
         }

         if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Decryption of key type " + var2 + " OK");
         }

         return var7;
      }
   }

   private OAEPParameterSpec constructOAEPParameters(String var1, String var2, String var3, byte[] var4) {
      if (!"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(var1) && !"http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(var1)) {
         return null;
      } else {
         String var5 = "SHA-1";
         if (var2 != null) {
            var5 = JCEMapper.translateURItoJCEID(var2);
         }

         PSpecified var6 = PSpecified.DEFAULT;
         if (var4 != null) {
            var6 = new PSpecified(var4);
         }

         MGF1ParameterSpec var7 = new MGF1ParameterSpec("SHA-1");
         if ("http://www.w3.org/2009/xmlenc11#rsa-oaep".equals(var1)) {
            if ("http://www.w3.org/2009/xmlenc11#mgf1sha256".equals(var3)) {
               var7 = new MGF1ParameterSpec("SHA-256");
            } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha384".equals(var3)) {
               var7 = new MGF1ParameterSpec("SHA-384");
            } else if ("http://www.w3.org/2009/xmlenc11#mgf1sha512".equals(var3)) {
               var7 = new MGF1ParameterSpec("SHA-512");
            }
         }

         return new OAEPParameterSpec(var5, "MGF1", var7, var6);
      }
   }

   private Cipher constructCipher(String var1, String var2) throws XMLEncryptionException {
      String var3 = JCEMapper.translateURItoJCEID(var1);
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "JCE Algorithm = " + var3);
      }

      Cipher var4;
      try {
         if (this.requestedJCEProvider == null) {
            var4 = Cipher.getInstance(var3);
         } else {
            var4 = Cipher.getInstance(var3, this.requestedJCEProvider);
         }
      } catch (NoSuchAlgorithmException var8) {
         if (!"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(var1) || var2 != null && !"http://www.w3.org/2000/09/xmldsig#sha1".equals(var2)) {
            throw new XMLEncryptionException("empty", var8);
         }

         try {
            if (this.requestedJCEProvider == null) {
               var4 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            } else {
               var4 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", this.requestedJCEProvider);
            }
         } catch (Exception var7) {
            throw new XMLEncryptionException("empty", var7);
         }
      } catch (NoSuchProviderException var9) {
         throw new XMLEncryptionException("empty", var9);
      } catch (NoSuchPaddingException var10) {
         throw new XMLEncryptionException("empty", var10);
      }

      return var4;
   }

   public Key decryptKey(EncryptedKey var1) throws XMLEncryptionException {
      return this.decryptKey(var1, this.ed.getEncryptionMethod().getAlgorithm());
   }

   private static void removeContent(Node var0) {
      while(var0.hasChildNodes()) {
         var0.removeChild(var0.getFirstChild());
      }

   }

   private Document decryptElement(Element var1) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Decrypting element...");
      }

      if (this.cipherMode != 2) {
         log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
      }

      byte[] var2 = this.decryptToByteArray(var1);
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Decrypted octets:\n" + new String(var2));
      }

      Node var3 = var1.getParentNode();
      Node var4 = this.serializer.deserialize(var2, var3);
      if (var3 != null && 9 == var3.getNodeType()) {
         this.contextDocument.removeChild(this.contextDocument.getDocumentElement());
         this.contextDocument.appendChild(var4);
      } else if (var3 != null) {
         var3.replaceChild(var4, var1);
      }

      return this.contextDocument;
   }

   private Document decryptElementContent(Element var1) throws XMLEncryptionException {
      Element var2 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
      if (null == var2) {
         throw new XMLEncryptionException("No EncryptedData child element.");
      } else {
         return this.decryptElement(var2);
      }
   }

   public byte[] decryptToByteArray(Element var1) throws XMLEncryptionException {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Decrypting to ByteArray...");
      }

      if (this.cipherMode != 2) {
         log.log(Level.SEVERE, "XMLCipher unexpectedly not in DECRYPT_MODE...");
      }

      EncryptedData var2 = this.factory.newEncryptedData(var1);
      int var7;
      if (this.key == null) {
         KeyInfo var3 = var2.getKeyInfo();
         if (var3 != null) {
            try {
               String var4 = var2.getEncryptionMethod().getAlgorithm();
               EncryptedKeyResolver var5 = new EncryptedKeyResolver(var4, this.kek);
               if (this.internalKeyResolvers != null) {
                  int var6 = this.internalKeyResolvers.size();

                  for(var7 = 0; var7 < var6; ++var7) {
                     var5.registerInternalKeyResolver((KeyResolverSpi)this.internalKeyResolvers.get(var7));
                  }
               }

               var3.registerInternalKeyResolver(var5);
               var3.setSecureValidation(this.secureValidation);
               this.key = var3.getSecretKey();
            } catch (KeyResolverException var19) {
               if (log.isLoggable(Level.FINE)) {
                  log.log(Level.FINE, (String)var19.getMessage(), (Throwable)var19);
               }
            }
         }

         if (this.key == null) {
            log.log(Level.SEVERE, "XMLCipher::decryptElement called without a key and unable to resolve");
            throw new XMLEncryptionException("encryption.nokey");
         }
      }

      XMLCipherInput var20 = new XMLCipherInput(var2);
      var20.setSecureValidation(this.secureValidation);
      byte[] var21 = var20.getBytes();
      String var22 = JCEMapper.translateURItoJCEID(var2.getEncryptionMethod().getAlgorithm());
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "JCE Algorithm = " + var22);
      }

      Cipher var23;
      try {
         if (this.requestedJCEProvider == null) {
            var23 = Cipher.getInstance(var22);
         } else {
            var23 = Cipher.getInstance(var22, this.requestedJCEProvider);
         }
      } catch (NoSuchAlgorithmException var16) {
         throw new XMLEncryptionException("empty", var16);
      } catch (NoSuchProviderException var17) {
         throw new XMLEncryptionException("empty", var17);
      } catch (NoSuchPaddingException var18) {
         throw new XMLEncryptionException("empty", var18);
      }

      var7 = var23.getBlockSize();
      String var8 = var2.getEncryptionMethod().getAlgorithm();
      if ("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(var8) || "http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(var8) || "http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(var8)) {
         var7 = 12;
      }

      byte[] var9 = new byte[var7];
      System.arraycopy(var21, 0, var9, 0, var7);
      IvParameterSpec var10 = new IvParameterSpec(var9);

      try {
         var23.init(this.cipherMode, this.key, var10);
      } catch (InvalidKeyException var14) {
         throw new XMLEncryptionException("empty", var14);
      } catch (InvalidAlgorithmParameterException var15) {
         throw new XMLEncryptionException("empty", var15);
      }

      try {
         return var23.doFinal(var21, var7, var21.length - var7);
      } catch (IllegalBlockSizeException var12) {
         throw new XMLEncryptionException("empty", var12);
      } catch (BadPaddingException var13) {
         throw new XMLEncryptionException("empty", var13);
      }
   }

   public EncryptedData createEncryptedData(int var1, String var2) throws XMLEncryptionException {
      EncryptedData var3 = null;
      CipherData var4 = null;
      switch(var1) {
      case 1:
         CipherValue var6 = this.factory.newCipherValue(var2);
         var4 = this.factory.newCipherData(var1);
         var4.setCipherValue(var6);
         var3 = this.factory.newEncryptedData(var4);
         break;
      case 2:
         CipherReference var5 = this.factory.newCipherReference(var2);
         var4 = this.factory.newCipherData(var1);
         var4.setCipherReference(var5);
         var3 = this.factory.newEncryptedData(var4);
      }

      return var3;
   }

   public EncryptedKey createEncryptedKey(int var1, String var2) throws XMLEncryptionException {
      EncryptedKey var3 = null;
      CipherData var4 = null;
      switch(var1) {
      case 1:
         CipherValue var6 = this.factory.newCipherValue(var2);
         var4 = this.factory.newCipherData(var1);
         var4.setCipherValue(var6);
         var3 = this.factory.newEncryptedKey(var4);
         break;
      case 2:
         CipherReference var5 = this.factory.newCipherReference(var2);
         var4 = this.factory.newCipherData(var1);
         var4.setCipherReference(var5);
         var3 = this.factory.newEncryptedKey(var4);
      }

      return var3;
   }

   public AgreementMethod createAgreementMethod(String var1) {
      return this.factory.newAgreementMethod(var1);
   }

   public CipherData createCipherData(int var1) {
      return this.factory.newCipherData(var1);
   }

   public CipherReference createCipherReference(String var1) {
      return this.factory.newCipherReference(var1);
   }

   public CipherValue createCipherValue(String var1) {
      return this.factory.newCipherValue(var1);
   }

   public EncryptionMethod createEncryptionMethod(String var1) {
      return this.factory.newEncryptionMethod(var1);
   }

   public EncryptionProperties createEncryptionProperties() {
      return this.factory.newEncryptionProperties();
   }

   public EncryptionProperty createEncryptionProperty() {
      return this.factory.newEncryptionProperty();
   }

   public ReferenceList createReferenceList(int var1) {
      return this.factory.newReferenceList(var1);
   }

   public Transforms createTransforms() {
      return this.factory.newTransforms();
   }

   public Transforms createTransforms(Document var1) {
      return this.factory.newTransforms(var1);
   }

   private class Factory {
      private Factory() {
      }

      AgreementMethod newAgreementMethod(String var1) {
         return new XMLCipher.Factory.AgreementMethodImpl(var1);
      }

      CipherData newCipherData(int var1) {
         return new XMLCipher.Factory.CipherDataImpl(var1);
      }

      CipherReference newCipherReference(String var1) {
         return new XMLCipher.Factory.CipherReferenceImpl(var1);
      }

      CipherValue newCipherValue(String var1) {
         return new XMLCipher.Factory.CipherValueImpl(var1);
      }

      EncryptedData newEncryptedData(CipherData var1) {
         return new XMLCipher.Factory.EncryptedDataImpl(var1);
      }

      EncryptedKey newEncryptedKey(CipherData var1) {
         return new XMLCipher.Factory.EncryptedKeyImpl(var1);
      }

      EncryptionMethod newEncryptionMethod(String var1) {
         return new XMLCipher.Factory.EncryptionMethodImpl(var1);
      }

      EncryptionProperties newEncryptionProperties() {
         return new XMLCipher.Factory.EncryptionPropertiesImpl();
      }

      EncryptionProperty newEncryptionProperty() {
         return new XMLCipher.Factory.EncryptionPropertyImpl();
      }

      ReferenceList newReferenceList(int var1) {
         return new XMLCipher.Factory.ReferenceListImpl(var1);
      }

      Transforms newTransforms() {
         return new XMLCipher.Factory.TransformsImpl();
      }

      Transforms newTransforms(Document var1) {
         return new XMLCipher.Factory.TransformsImpl(var1);
      }

      CipherData newCipherData(Element var1) throws XMLEncryptionException {
         if (null == var1) {
            throw new NullPointerException("element is null");
         } else {
            byte var2 = 0;
            Element var3 = null;
            if (var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0) {
               var2 = 1;
               var3 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
            } else if (var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0) {
               var2 = 2;
               var3 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
            }

            CipherData var4 = this.newCipherData(var2);
            if (var2 == 1) {
               var4.setCipherValue(this.newCipherValue(var3));
            } else if (var2 == 2) {
               var4.setCipherReference(this.newCipherReference(var3));
            }

            return var4;
         }
      }

      CipherReference newCipherReference(Element var1) throws XMLEncryptionException {
         Attr var2 = var1.getAttributeNodeNS((String)null, "URI");
         XMLCipher.Factory.CipherReferenceImpl var3 = new XMLCipher.Factory.CipherReferenceImpl(var2);
         NodeList var4 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms");
         Element var5 = (Element)var4.item(0);
         if (var5 != null) {
            if (XMLCipher.log.isLoggable(Level.FINE)) {
               XMLCipher.log.log(Level.FINE, "Creating a DSIG based Transforms element");
            }

            try {
               var3.setTransforms(new XMLCipher.Factory.TransformsImpl(var5));
            } catch (XMLSignatureException var7) {
               throw new XMLEncryptionException("empty", var7);
            } catch (InvalidTransformException var8) {
               throw new XMLEncryptionException("empty", var8);
            } catch (XMLSecurityException var9) {
               throw new XMLEncryptionException("empty", var9);
            }
         }

         return var3;
      }

      CipherValue newCipherValue(Element var1) {
         String var2 = XMLUtils.getFullTextChildrenFromElement(var1);
         return this.newCipherValue(var2);
      }

      EncryptedData newEncryptedData(Element var1) throws XMLEncryptionException {
         EncryptedData var2 = null;
         NodeList var3 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
         Element var4 = (Element)var3.item(var3.getLength() - 1);
         CipherData var5 = this.newCipherData(var4);
         var2 = this.newEncryptedData(var5);
         var2.setId(var1.getAttributeNS((String)null, "Id"));
         var2.setType(var1.getAttributeNS((String)null, "Type"));
         var2.setMimeType(var1.getAttributeNS((String)null, "MimeType"));
         var2.setEncoding(var1.getAttributeNS((String)null, "Encoding"));
         Element var6 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
         if (null != var6) {
            var2.setEncryptionMethod(this.newEncryptionMethod(var6));
         }

         Element var7 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
         if (null != var7) {
            KeyInfo var8 = this.newKeyInfo(var7);
            var2.setKeyInfo(var8);
         }

         Element var9 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
         if (null != var9) {
            var2.setEncryptionProperties(this.newEncryptionProperties(var9));
         }

         return var2;
      }

      EncryptedKey newEncryptedKey(Element var1) throws XMLEncryptionException {
         EncryptedKey var2 = null;
         NodeList var3 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
         Element var4 = (Element)var3.item(var3.getLength() - 1);
         CipherData var5 = this.newCipherData(var4);
         var2 = this.newEncryptedKey(var5);
         var2.setId(var1.getAttributeNS((String)null, "Id"));
         var2.setType(var1.getAttributeNS((String)null, "Type"));
         var2.setMimeType(var1.getAttributeNS((String)null, "MimeType"));
         var2.setEncoding(var1.getAttributeNS((String)null, "Encoding"));
         var2.setRecipient(var1.getAttributeNS((String)null, "Recipient"));
         Element var6 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
         if (null != var6) {
            var2.setEncryptionMethod(this.newEncryptionMethod(var6));
         }

         Element var7 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
         if (null != var7) {
            KeyInfo var8 = this.newKeyInfo(var7);
            var2.setKeyInfo(var8);
         }

         Element var11 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
         if (null != var11) {
            var2.setEncryptionProperties(this.newEncryptionProperties(var11));
         }

         Element var9 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0);
         if (null != var9) {
            var2.setReferenceList(this.newReferenceList(var9));
         }

         Element var10 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0);
         if (null != var10) {
            var2.setCarriedName(var10.getFirstChild().getNodeValue());
         }

         return var2;
      }

      KeyInfo newKeyInfo(Element var1) throws XMLEncryptionException {
         try {
            KeyInfo var2 = new KeyInfo(var1, (String)null);
            var2.setSecureValidation(XMLCipher.this.secureValidation);
            if (XMLCipher.this.internalKeyResolvers != null) {
               int var3 = XMLCipher.this.internalKeyResolvers.size();

               for(int var4 = 0; var4 < var3; ++var4) {
                  var2.registerInternalKeyResolver((KeyResolverSpi)XMLCipher.this.internalKeyResolvers.get(var4));
               }
            }

            return var2;
         } catch (XMLSecurityException var5) {
            throw new XMLEncryptionException("Error loading Key Info", var5);
         }
      }

      EncryptionMethod newEncryptionMethod(Element var1) {
         String var2 = var1.getAttributeNS((String)null, "Algorithm");
         EncryptionMethod var3 = this.newEncryptionMethod(var2);
         Element var4 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
         if (null != var4) {
            var3.setKeySize(Integer.valueOf(var4.getFirstChild().getNodeValue()));
         }

         Element var5 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0);
         if (null != var5) {
            try {
               String var6 = var5.getFirstChild().getNodeValue();
               var3.setOAEPparams(Base64.decode(var6.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException var9) {
               throw new RuntimeException("UTF-8 not supported", var9);
            } catch (Base64DecodingException var10) {
               throw new RuntimeException("BASE-64 decoding error", var10);
            }
         }

         Element var11 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "DigestMethod").item(0);
         if (var11 != null) {
            String var7 = var11.getAttributeNS((String)null, "Algorithm");
            var3.setDigestAlgorithm(var7);
         }

         Element var12 = (Element)var1.getElementsByTagNameNS("http://www.w3.org/2009/xmlenc11#", "MGF").item(0);
         if (var12 != null && !"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p".equals(XMLCipher.this.algorithm)) {
            String var8 = var12.getAttributeNS((String)null, "Algorithm");
            var3.setMGFAlgorithm(var8);
         }

         return var3;
      }

      EncryptionProperties newEncryptionProperties(Element var1) {
         EncryptionProperties var2 = this.newEncryptionProperties();
         var2.setId(var1.getAttributeNS((String)null, "Id"));
         NodeList var3 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");

         for(int var4 = 0; var4 < var3.getLength(); ++var4) {
            Node var5 = var3.item(var4);
            if (null != var5) {
               var2.addEncryptionProperty(this.newEncryptionProperty((Element)var5));
            }
         }

         return var2;
      }

      EncryptionProperty newEncryptionProperty(Element var1) {
         EncryptionProperty var2 = this.newEncryptionProperty();
         var2.setTarget(var1.getAttributeNS((String)null, "Target"));
         var2.setId(var1.getAttributeNS((String)null, "Id"));
         return var2;
      }

      ReferenceList newReferenceList(Element var1) {
         byte var2 = 0;
         if (null != var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(0)) {
            var2 = 1;
         } else if (null != var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(0)) {
            var2 = 2;
         }

         XMLCipher.Factory.ReferenceListImpl var3 = new XMLCipher.Factory.ReferenceListImpl(var2);
         NodeList var4 = null;
         int var5;
         String var6;
         switch(var2) {
         case 1:
            var4 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");

            for(var5 = 0; var5 < var4.getLength(); ++var5) {
               var6 = ((Element)var4.item(var5)).getAttribute("URI");
               var3.add(var3.newDataReference(var6));
            }

            return var3;
         case 2:
            var4 = var1.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");

            for(var5 = 0; var5 < var4.getLength(); ++var5) {
               var6 = ((Element)var4.item(var5)).getAttribute("URI");
               var3.add(var3.newKeyReference(var6));
            }
         }

         return var3;
      }

      Element toElement(EncryptedData var1) {
         return ((XMLCipher.Factory.EncryptedDataImpl)var1).toElement();
      }

      Element toElement(EncryptedKey var1) {
         return ((XMLCipher.Factory.EncryptedKeyImpl)var1).toElement();
      }

      Element toElement(ReferenceList var1) {
         return ((XMLCipher.Factory.ReferenceListImpl)var1).toElement();
      }

      // $FF: synthetic method
      Factory(Object var2) {
         this();
      }

      private class ReferenceListImpl implements ReferenceList {
         private Class<?> sentry;
         private List<Reference> references;

         public ReferenceListImpl(int var2) {
            if (var2 == 1) {
               this.sentry = XMLCipher.Factory.ReferenceListImpl.DataReference.class;
            } else {
               if (var2 != 2) {
                  throw new IllegalArgumentException();
               }

               this.sentry = XMLCipher.Factory.ReferenceListImpl.KeyReference.class;
            }

            this.references = new LinkedList();
         }

         public void add(Reference var1) {
            if (!var1.getClass().equals(this.sentry)) {
               throw new IllegalArgumentException();
            } else {
               this.references.add(var1);
            }
         }

         public void remove(Reference var1) {
            if (!var1.getClass().equals(this.sentry)) {
               throw new IllegalArgumentException();
            } else {
               this.references.remove(var1);
            }
         }

         public int size() {
            return this.references.size();
         }

         public boolean isEmpty() {
            return this.references.isEmpty();
         }

         public Iterator<Reference> getReferences() {
            return this.references.iterator();
         }

         Element toElement() {
            Element var1 = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
            Iterator var2 = this.references.iterator();

            while(var2.hasNext()) {
               Reference var3 = (Reference)var2.next();
               var1.appendChild(((XMLCipher.Factory.ReferenceListImpl.ReferenceImpl)var3).toElement());
            }

            return var1;
         }

         public Reference newDataReference(String var1) {
            return new XMLCipher.Factory.ReferenceListImpl.DataReference(var1);
         }

         public Reference newKeyReference(String var1) {
            return new XMLCipher.Factory.ReferenceListImpl.KeyReference(var1);
         }

         private class KeyReference extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl {
            KeyReference(String var2) {
               super(var2);
            }

            public String getType() {
               return "KeyReference";
            }
         }

         private class DataReference extends XMLCipher.Factory.ReferenceListImpl.ReferenceImpl {
            DataReference(String var2) {
               super(var2);
            }

            public String getType() {
               return "DataReference";
            }
         }

         private abstract class ReferenceImpl implements Reference {
            private String uri;
            private List<Element> referenceInformation;

            ReferenceImpl(String var2) {
               this.uri = var2;
               this.referenceInformation = new LinkedList();
            }

            public abstract String getType();

            public String getURI() {
               return this.uri;
            }

            public Iterator<Element> getElementRetrievalInformation() {
               return this.referenceInformation.iterator();
            }

            public void setURI(String var1) {
               this.uri = var1;
            }

            public void removeElementRetrievalInformation(Element var1) {
               this.referenceInformation.remove(var1);
            }

            public void addElementRetrievalInformation(Element var1) {
               this.referenceInformation.add(var1);
            }

            public Element toElement() {
               String var1 = this.getType();
               Element var2 = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", var1);
               var2.setAttribute("URI", this.uri);
               return var2;
            }
         }
      }

      private class TransformsImpl extends com.sun.org.apache.xml.internal.security.transforms.Transforms implements Transforms {
         public TransformsImpl() {
            super(XMLCipher.this.contextDocument);
         }

         public TransformsImpl(Document var2) {
            if (var2 == null) {
               throw new RuntimeException("Document is null");
            } else {
               this.doc = var2;
               this.constructionElement = this.createElementForFamilyLocal(this.doc, this.getBaseNamespace(), this.getBaseLocalName());
            }
         }

         public TransformsImpl(Element var2) throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException {
            super(var2, "");
         }

         public Element toElement() {
            if (this.doc == null) {
               this.doc = XMLCipher.this.contextDocument;
            }

            return this.getElement();
         }

         public com.sun.org.apache.xml.internal.security.transforms.Transforms getDSTransforms() {
            return this;
         }

         public String getBaseNamespace() {
            return "http://www.w3.org/2001/04/xmlenc#";
         }
      }

      private class EncryptionPropertyImpl implements EncryptionProperty {
         private String target = null;
         private String id = null;
         private Map<String, String> attributeMap = new HashMap();
         private List<Element> encryptionInformation = null;

         public EncryptionPropertyImpl() {
            this.encryptionInformation = new LinkedList();
         }

         public String getTarget() {
            return this.target;
         }

         public void setTarget(String var1) {
            if (var1 != null && var1.length() != 0) {
               if (var1.startsWith("#")) {
                  this.target = var1;
               } else {
                  URI var2 = null;

                  try {
                     var2 = new URI(var1);
                  } catch (URISyntaxException var4) {
                     throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var4);
                  }

                  this.target = var2.toString();
               }
            } else {
               this.target = null;
            }

         }

         public String getId() {
            return this.id;
         }

         public void setId(String var1) {
            this.id = var1;
         }

         public String getAttribute(String var1) {
            return (String)this.attributeMap.get(var1);
         }

         public void setAttribute(String var1, String var2) {
            this.attributeMap.put(var1, var2);
         }

         public Iterator<Element> getEncryptionInformation() {
            return this.encryptionInformation.iterator();
         }

         public void addEncryptionInformation(Element var1) {
            this.encryptionInformation.add(var1);
         }

         public void removeEncryptionInformation(Element var1) {
            this.encryptionInformation.remove(var1);
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionProperty");
            if (null != this.target) {
               var1.setAttributeNS((String)null, "Target", this.target);
            }

            if (null != this.id) {
               var1.setAttributeNS((String)null, "Id", this.id);
            }

            return var1;
         }
      }

      private class EncryptionPropertiesImpl implements EncryptionProperties {
         private String id = null;
         private List<EncryptionProperty> encryptionProperties = null;

         public EncryptionPropertiesImpl() {
            this.encryptionProperties = new LinkedList();
         }

         public String getId() {
            return this.id;
         }

         public void setId(String var1) {
            this.id = var1;
         }

         public Iterator<EncryptionProperty> getEncryptionProperties() {
            return this.encryptionProperties.iterator();
         }

         public void addEncryptionProperty(EncryptionProperty var1) {
            this.encryptionProperties.add(var1);
         }

         public void removeEncryptionProperty(EncryptionProperty var1) {
            this.encryptionProperties.remove(var1);
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionProperties");
            if (null != this.id) {
               var1.setAttributeNS((String)null, "Id", this.id);
            }

            Iterator var2 = this.getEncryptionProperties();

            while(var2.hasNext()) {
               var1.appendChild(((XMLCipher.Factory.EncryptionPropertyImpl)var2.next()).toElement());
            }

            return var1;
         }
      }

      private class EncryptionMethodImpl implements EncryptionMethod {
         private String algorithm = null;
         private int keySize = Integer.MIN_VALUE;
         private byte[] oaepParams = null;
         private List<Element> encryptionMethodInformation = null;
         private String digestAlgorithm = null;
         private String mgfAlgorithm = null;

         public EncryptionMethodImpl(String var2) {
            URI var3 = null;

            try {
               var3 = new URI(var2);
            } catch (URISyntaxException var5) {
               throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var5);
            }

            this.algorithm = var3.toString();
            this.encryptionMethodInformation = new LinkedList();
         }

         public String getAlgorithm() {
            return this.algorithm;
         }

         public int getKeySize() {
            return this.keySize;
         }

         public void setKeySize(int var1) {
            this.keySize = var1;
         }

         public byte[] getOAEPparams() {
            return this.oaepParams;
         }

         public void setOAEPparams(byte[] var1) {
            this.oaepParams = var1;
         }

         public void setDigestAlgorithm(String var1) {
            this.digestAlgorithm = var1;
         }

         public String getDigestAlgorithm() {
            return this.digestAlgorithm;
         }

         public void setMGFAlgorithm(String var1) {
            this.mgfAlgorithm = var1;
         }

         public String getMGFAlgorithm() {
            return this.mgfAlgorithm;
         }

         public Iterator<Element> getEncryptionMethodInformation() {
            return this.encryptionMethodInformation.iterator();
         }

         public void addEncryptionMethodInformation(Element var1) {
            this.encryptionMethodInformation.add(var1);
         }

         public void removeEncryptionMethodInformation(Element var1) {
            this.encryptionMethodInformation.remove(var1);
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "EncryptionMethod");
            var1.setAttributeNS((String)null, "Algorithm", this.algorithm);
            if (this.keySize > 0) {
               var1.appendChild(XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "KeySize").appendChild(XMLCipher.this.contextDocument.createTextNode(String.valueOf(this.keySize))));
            }

            Element var2;
            if (null != this.oaepParams) {
               var2 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "OAEPparams");
               var2.appendChild(XMLCipher.this.contextDocument.createTextNode(Base64.encode(this.oaepParams)));
               var1.appendChild(var2);
            }

            if (this.digestAlgorithm != null) {
               var2 = XMLUtils.createElementInSignatureSpace(XMLCipher.this.contextDocument, "DigestMethod");
               var2.setAttributeNS((String)null, "Algorithm", this.digestAlgorithm);
               var1.appendChild(var2);
            }

            if (this.mgfAlgorithm != null) {
               var2 = XMLUtils.createElementInEncryption11Space(XMLCipher.this.contextDocument, "MGF");
               var2.setAttributeNS((String)null, "Algorithm", this.mgfAlgorithm);
               var2.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + ElementProxy.getDefaultPrefix("http://www.w3.org/2009/xmlenc11#"), "http://www.w3.org/2009/xmlenc11#");
               var1.appendChild(var2);
            }

            Iterator var3 = this.encryptionMethodInformation.iterator();

            while(var3.hasNext()) {
               var1.appendChild((Node)var3.next());
            }

            return var1;
         }
      }

      private abstract class EncryptedTypeImpl {
         private String id = null;
         private String type = null;
         private String mimeType = null;
         private String encoding = null;
         private EncryptionMethod encryptionMethod = null;
         private KeyInfo keyInfo = null;
         private CipherData cipherData = null;
         private EncryptionProperties encryptionProperties = null;

         protected EncryptedTypeImpl(CipherData var2) {
            this.cipherData = var2;
         }

         public String getId() {
            return this.id;
         }

         public void setId(String var1) {
            this.id = var1;
         }

         public String getType() {
            return this.type;
         }

         public void setType(String var1) {
            if (var1 != null && var1.length() != 0) {
               URI var2 = null;

               try {
                  var2 = new URI(var1);
               } catch (URISyntaxException var4) {
                  throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var4);
               }

               this.type = var2.toString();
            } else {
               this.type = null;
            }

         }

         public String getMimeType() {
            return this.mimeType;
         }

         public void setMimeType(String var1) {
            this.mimeType = var1;
         }

         public String getEncoding() {
            return this.encoding;
         }

         public void setEncoding(String var1) {
            if (var1 != null && var1.length() != 0) {
               URI var2 = null;

               try {
                  var2 = new URI(var1);
               } catch (URISyntaxException var4) {
                  throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var4);
               }

               this.encoding = var2.toString();
            } else {
               this.encoding = null;
            }

         }

         public EncryptionMethod getEncryptionMethod() {
            return this.encryptionMethod;
         }

         public void setEncryptionMethod(EncryptionMethod var1) {
            this.encryptionMethod = var1;
         }

         public KeyInfo getKeyInfo() {
            return this.keyInfo;
         }

         public void setKeyInfo(KeyInfo var1) {
            this.keyInfo = var1;
         }

         public CipherData getCipherData() {
            return this.cipherData;
         }

         public EncryptionProperties getEncryptionProperties() {
            return this.encryptionProperties;
         }

         public void setEncryptionProperties(EncryptionProperties var1) {
            this.encryptionProperties = var1;
         }
      }

      private class EncryptedKeyImpl extends XMLCipher.Factory.EncryptedTypeImpl implements EncryptedKey {
         private String keyRecipient = null;
         private ReferenceList referenceList = null;
         private String carriedName = null;

         public EncryptedKeyImpl(CipherData var2) {
            super(var2);
         }

         public String getRecipient() {
            return this.keyRecipient;
         }

         public void setRecipient(String var1) {
            this.keyRecipient = var1;
         }

         public ReferenceList getReferenceList() {
            return this.referenceList;
         }

         public void setReferenceList(ReferenceList var1) {
            this.referenceList = var1;
         }

         public String getCarriedName() {
            return this.carriedName;
         }

         public void setCarriedName(String var1) {
            this.carriedName = var1;
         }

         Element toElement() {
            Element var1 = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
            if (null != super.getId()) {
               var1.setAttributeNS((String)null, "Id", super.getId());
            }

            if (null != super.getType()) {
               var1.setAttributeNS((String)null, "Type", super.getType());
            }

            if (null != super.getMimeType()) {
               var1.setAttributeNS((String)null, "MimeType", super.getMimeType());
            }

            if (null != super.getEncoding()) {
               var1.setAttributeNS((String)null, "Encoding", super.getEncoding());
            }

            if (null != this.getRecipient()) {
               var1.setAttributeNS((String)null, "Recipient", this.getRecipient());
            }

            if (null != super.getEncryptionMethod()) {
               var1.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
            }

            if (null != super.getKeyInfo()) {
               var1.appendChild(super.getKeyInfo().getElement().cloneNode(true));
            }

            var1.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
            if (null != super.getEncryptionProperties()) {
               var1.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
            }

            if (this.referenceList != null && !this.referenceList.isEmpty()) {
               var1.appendChild(((XMLCipher.Factory.ReferenceListImpl)this.getReferenceList()).toElement());
            }

            if (null != this.carriedName) {
               Element var2 = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
               Text var3 = XMLCipher.this.contextDocument.createTextNode(this.carriedName);
               var2.appendChild(var3);
               var1.appendChild(var2);
            }

            return var1;
         }
      }

      private class EncryptedDataImpl extends XMLCipher.Factory.EncryptedTypeImpl implements EncryptedData {
         public EncryptedDataImpl(CipherData var2) {
            super(var2);
         }

         Element toElement() {
            Element var1 = ElementProxy.createElementForFamily(XMLCipher.this.contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
            if (null != super.getId()) {
               var1.setAttributeNS((String)null, "Id", super.getId());
            }

            if (null != super.getType()) {
               var1.setAttributeNS((String)null, "Type", super.getType());
            }

            if (null != super.getMimeType()) {
               var1.setAttributeNS((String)null, "MimeType", super.getMimeType());
            }

            if (null != super.getEncoding()) {
               var1.setAttributeNS((String)null, "Encoding", super.getEncoding());
            }

            if (null != super.getEncryptionMethod()) {
               var1.appendChild(((XMLCipher.Factory.EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
            }

            if (null != super.getKeyInfo()) {
               var1.appendChild(super.getKeyInfo().getElement().cloneNode(true));
            }

            var1.appendChild(((XMLCipher.Factory.CipherDataImpl)super.getCipherData()).toElement());
            if (null != super.getEncryptionProperties()) {
               var1.appendChild(((XMLCipher.Factory.EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
            }

            return var1;
         }
      }

      private class CipherValueImpl implements CipherValue {
         private String cipherValue = null;

         public CipherValueImpl(String var2) {
            this.cipherValue = var2;
         }

         public String getValue() {
            return this.cipherValue;
         }

         public void setValue(String var1) {
            this.cipherValue = var1;
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherValue");
            var1.appendChild(XMLCipher.this.contextDocument.createTextNode(this.cipherValue));
            return var1;
         }
      }

      private class CipherReferenceImpl implements CipherReference {
         private String referenceURI = null;
         private Transforms referenceTransforms = null;
         private Attr referenceNode = null;

         public CipherReferenceImpl(String var2) {
            this.referenceURI = var2;
            this.referenceNode = null;
         }

         public CipherReferenceImpl(Attr var2) {
            this.referenceURI = var2.getNodeValue();
            this.referenceNode = var2;
         }

         public String getURI() {
            return this.referenceURI;
         }

         public Attr getURIAsAttr() {
            return this.referenceNode;
         }

         public Transforms getTransforms() {
            return this.referenceTransforms;
         }

         public void setTransforms(Transforms var1) {
            this.referenceTransforms = var1;
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherReference");
            var1.setAttributeNS((String)null, "URI", this.referenceURI);
            if (null != this.referenceTransforms) {
               var1.appendChild(((XMLCipher.Factory.TransformsImpl)this.referenceTransforms).toElement());
            }

            return var1;
         }
      }

      private class CipherDataImpl implements CipherData {
         private static final String valueMessage = "Data type is reference type.";
         private static final String referenceMessage = "Data type is value type.";
         private CipherValue cipherValue = null;
         private CipherReference cipherReference = null;
         private int cipherType = Integer.MIN_VALUE;

         public CipherDataImpl(int var2) {
            this.cipherType = var2;
         }

         public CipherValue getCipherValue() {
            return this.cipherValue;
         }

         public void setCipherValue(CipherValue var1) throws XMLEncryptionException {
            if (this.cipherType == 2) {
               throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is reference type."));
            } else {
               this.cipherValue = var1;
            }
         }

         public CipherReference getCipherReference() {
            return this.cipherReference;
         }

         public void setCipherReference(CipherReference var1) throws XMLEncryptionException {
            if (this.cipherType == 1) {
               throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is value type."));
            } else {
               this.cipherReference = var1;
            }
         }

         public int getDataType() {
            return this.cipherType;
         }

         Element toElement() {
            Element var1 = XMLUtils.createElementInEncryptionSpace(XMLCipher.this.contextDocument, "CipherData");
            if (this.cipherType == 1) {
               var1.appendChild(((XMLCipher.Factory.CipherValueImpl)this.cipherValue).toElement());
            } else if (this.cipherType == 2) {
               var1.appendChild(((XMLCipher.Factory.CipherReferenceImpl)this.cipherReference).toElement());
            }

            return var1;
         }
      }

      private class AgreementMethodImpl implements AgreementMethod {
         private byte[] kaNonce = null;
         private List<Element> agreementMethodInformation = null;
         private KeyInfo originatorKeyInfo = null;
         private KeyInfo recipientKeyInfo = null;
         private String algorithmURI = null;

         public AgreementMethodImpl(String var2) {
            this.agreementMethodInformation = new LinkedList();
            URI var3 = null;

            try {
               var3 = new URI(var2);
            } catch (URISyntaxException var5) {
               throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(var5);
            }

            this.algorithmURI = var3.toString();
         }

         public byte[] getKANonce() {
            return this.kaNonce;
         }

         public void setKANonce(byte[] var1) {
            this.kaNonce = var1;
         }

         public Iterator<Element> getAgreementMethodInformation() {
            return this.agreementMethodInformation.iterator();
         }

         public void addAgreementMethodInformation(Element var1) {
            this.agreementMethodInformation.add(var1);
         }

         public void revoveAgreementMethodInformation(Element var1) {
            this.agreementMethodInformation.remove(var1);
         }

         public KeyInfo getOriginatorKeyInfo() {
            return this.originatorKeyInfo;
         }

         public void setOriginatorKeyInfo(KeyInfo var1) {
            this.originatorKeyInfo = var1;
         }

         public KeyInfo getRecipientKeyInfo() {
            return this.recipientKeyInfo;
         }

         public void setRecipientKeyInfo(KeyInfo var1) {
            this.recipientKeyInfo = var1;
         }

         public String getAlgorithm() {
            return this.algorithmURI;
         }
      }
   }
}
