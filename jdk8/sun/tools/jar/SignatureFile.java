package sun.tools.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import sun.net.www.MessageHeader;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;

public class SignatureFile {
   static final boolean debug = false;
   private Vector<MessageHeader> entries;
   static final String[] hashes = new String[]{"SHA"};
   private Manifest manifest;
   private String rawName;
   private PKCS7 signatureBlock;
   private Hashtable<String, MessageDigest> digests;

   static final void debug(String var0) {
   }

   private SignatureFile(String var1) throws JarException {
      this.entries = new Vector();
      this.digests = new Hashtable();
      this.entries = new Vector();
      if (var1 != null) {
         if (var1.length() > 8 || var1.indexOf(46) != -1) {
            throw new JarException("invalid file name");
         }

         this.rawName = var1.toUpperCase(Locale.ENGLISH);
      }

   }

   private SignatureFile(String var1, boolean var2) throws JarException {
      this(var1);
      if (var2) {
         MessageHeader var3 = new MessageHeader();
         var3.set("Signature-Version", "1.0");
         this.entries.addElement(var3);
      }

   }

   public SignatureFile(Manifest var1, String var2) throws JarException {
      this(var2, true);
      this.manifest = var1;
      Enumeration var3 = var1.entries();

      while(var3.hasMoreElements()) {
         MessageHeader var4 = (MessageHeader)var3.nextElement();
         String var5 = var4.findValue("Name");
         if (var5 != null) {
            this.add(var5);
         }
      }

   }

   public SignatureFile(Manifest var1, String[] var2, String var3) throws JarException {
      this(var3, true);
      this.manifest = var1;
      this.add(var2);
   }

   public SignatureFile(InputStream var1, String var2) throws IOException {
      this(var2);

      while(var1.available() > 0) {
         MessageHeader var3 = new MessageHeader(var1);
         this.entries.addElement(var3);
      }

   }

   public SignatureFile(InputStream var1) throws IOException {
      this((InputStream)var1, (String)null);
   }

   public SignatureFile(byte[] var1) throws IOException {
      this((InputStream)(new ByteArrayInputStream(var1)));
   }

   public String getName() {
      return "META-INF/" + this.rawName + ".SF";
   }

   public String getBlockName() {
      String var1 = "DSA";
      if (this.signatureBlock != null) {
         SignerInfo var2 = this.signatureBlock.getSignerInfos()[0];
         var1 = var2.getDigestEncryptionAlgorithmId().getName();
         String var3 = AlgorithmId.getEncAlgFromSigAlg(var1);
         if (var3 != null) {
            var1 = var3;
         }
      }

      return "META-INF/" + this.rawName + "." + var1;
   }

   public PKCS7 getBlock() {
      return this.signatureBlock;
   }

   public void setBlock(PKCS7 var1) {
      this.signatureBlock = var1;
   }

   public void add(String[] var1) throws JarException {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.add(var1[var2]);
      }

   }

   public void add(String var1) throws JarException {
      MessageHeader var2 = this.manifest.getEntry(var1);
      if (var2 == null) {
         throw new JarException("entry " + var1 + " not in manifest");
      } else {
         MessageHeader var3;
         try {
            var3 = this.computeEntry(var2);
         } catch (IOException var5) {
            throw new JarException(var5.getMessage());
         }

         this.entries.addElement(var3);
      }
   }

   public MessageHeader getEntry(String var1) {
      Enumeration var2 = this.entries();

      MessageHeader var3;
      do {
         if (!var2.hasMoreElements()) {
            return null;
         }

         var3 = (MessageHeader)var2.nextElement();
      } while(!var1.equals(var3.findValue("Name")));

      return var3;
   }

   public MessageHeader entryAt(int var1) {
      return (MessageHeader)this.entries.elementAt(var1);
   }

   public Enumeration<MessageHeader> entries() {
      return this.entries.elements();
   }

   private MessageHeader computeEntry(MessageHeader var1) throws IOException {
      MessageHeader var2 = new MessageHeader();
      String var3 = var1.findValue("Name");
      if (var3 == null) {
         return null;
      } else {
         var2.set("Name", var3);

         try {
            for(int var4 = 0; var4 < hashes.length; ++var4) {
               MessageDigest var5 = this.getDigest(hashes[var4]);
               ByteArrayOutputStream var6 = new ByteArrayOutputStream();
               PrintStream var7 = new PrintStream(var6);
               var1.print(var7);
               byte[] var8 = var6.toByteArray();
               byte[] var9 = var5.digest(var8);
               var2.set(hashes[var4] + "-Digest", Base64.getMimeEncoder().encodeToString(var9));
            }

            return var2;
         } catch (NoSuchAlgorithmException var10) {
            throw new JarException(var10.getMessage());
         }
      }
   }

   private MessageDigest getDigest(String var1) throws NoSuchAlgorithmException {
      MessageDigest var2 = (MessageDigest)this.digests.get(var1);
      if (var2 == null) {
         var2 = MessageDigest.getInstance(var1);
         this.digests.put(var1, var2);
      }

      var2.reset();
      return var2;
   }

   public void stream(OutputStream var1) throws IOException {
      MessageHeader var2 = (MessageHeader)this.entries.elementAt(0);
      if (var2.findValue("Signature-Version") == null) {
         throw new JarException("Signature file requires Signature-Version: 1.0 in 1st header");
      } else {
         PrintStream var3 = new PrintStream(var1);
         var2.print(var3);

         for(int var4 = 1; var4 < this.entries.size(); ++var4) {
            MessageHeader var5 = (MessageHeader)this.entries.elementAt(var4);
            var5.print(var3);
         }

      }
   }
}
