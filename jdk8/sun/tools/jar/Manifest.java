package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.net.www.MessageHeader;

public class Manifest {
   private Vector<MessageHeader> entries;
   private byte[] tmpbuf;
   private Hashtable<String, MessageHeader> tableEntries;
   static final String[] hashes = new String[]{"SHA"};
   static final byte[] EOL = new byte[]{13, 10};
   static final boolean debug = false;
   static final String VERSION = "1.0";

   static final void debug(String var0) {
   }

   public Manifest() {
      this.entries = new Vector();
      this.tmpbuf = new byte[512];
      this.tableEntries = new Hashtable();
   }

   public Manifest(byte[] var1) throws IOException {
      this(new ByteArrayInputStream(var1), false);
   }

   public Manifest(InputStream var1) throws IOException {
      this(var1, true);
   }

   public Manifest(InputStream var1, boolean var2) throws IOException {
      this.entries = new Vector();
      this.tmpbuf = new byte[512];
      this.tableEntries = new Hashtable();
      if (!((InputStream)var1).markSupported()) {
         var1 = new BufferedInputStream((InputStream)var1);
      }

      while(true) {
         ((InputStream)var1).mark(1);
         if (((InputStream)var1).read() == -1) {
            return;
         }

         ((InputStream)var1).reset();
         MessageHeader var3 = new MessageHeader((InputStream)var1);
         if (var2) {
            this.doHashes(var3);
         }

         this.addEntry(var3);
      }
   }

   public Manifest(String[] var1) throws IOException {
      this.entries = new Vector();
      this.tmpbuf = new byte[512];
      this.tableEntries = new Hashtable();
      MessageHeader var2 = new MessageHeader();
      var2.add("Manifest-Version", "1.0");
      String var3 = System.getProperty("java.version");
      var2.add("Created-By", "Manifest JDK " + var3);
      this.addEntry(var2);
      this.addFiles((File)null, var1);
   }

   public void addEntry(MessageHeader var1) {
      this.entries.addElement(var1);
      String var2 = var1.findValue("Name");
      debug("addEntry for name: " + var2);
      if (var2 != null) {
         this.tableEntries.put(var2, var1);
      }

   }

   public MessageHeader getEntry(String var1) {
      return (MessageHeader)this.tableEntries.get(var1);
   }

   public MessageHeader entryAt(int var1) {
      return (MessageHeader)this.entries.elementAt(var1);
   }

   public Enumeration<MessageHeader> entries() {
      return this.entries.elements();
   }

   public void addFiles(File var1, String[] var2) throws IOException {
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            File var4;
            if (var1 == null) {
               var4 = new File(var2[var3]);
            } else {
               var4 = new File(var1, var2[var3]);
            }

            if (var4.isDirectory()) {
               this.addFiles(var4, var4.list());
            } else {
               this.addFile(var4);
            }
         }

      }
   }

   private final String stdToLocal(String var1) {
      return var1.replace('/', File.separatorChar);
   }

   private final String localToStd(String var1) {
      var1 = var1.replace(File.separatorChar, '/');
      if (var1.startsWith("./")) {
         var1 = var1.substring(2);
      } else if (var1.startsWith("/")) {
         var1 = var1.substring(1);
      }

      return var1;
   }

   public void addFile(File var1) throws IOException {
      String var2 = this.localToStd(var1.getPath());
      if (this.tableEntries.get(var2) == null) {
         MessageHeader var3 = new MessageHeader();
         var3.add("Name", var2);
         this.addEntry(var3);
      }

   }

   public void doHashes(MessageHeader var1) throws IOException {
      String var2 = var1.findValue("Name");
      if (var2 != null && !var2.endsWith("/")) {
         for(int var3 = 0; var3 < hashes.length; ++var3) {
            FileInputStream var4 = new FileInputStream(this.stdToLocal(var2));

            try {
               MessageDigest var5 = MessageDigest.getInstance(hashes[var3]);

               int var6;
               while((var6 = var4.read(this.tmpbuf, 0, this.tmpbuf.length)) != -1) {
                  var5.update(this.tmpbuf, 0, var6);
               }

               var1.set(hashes[var3] + "-Digest", Base64.getMimeEncoder().encodeToString(var5.digest()));
            } catch (NoSuchAlgorithmException var10) {
               throw new JarException("Digest algorithm " + hashes[var3] + " not available.");
            } finally {
               var4.close();
            }
         }

      }
   }

   public void stream(OutputStream var1) throws IOException {
      PrintStream var2;
      if (var1 instanceof PrintStream) {
         var2 = (PrintStream)var1;
      } else {
         var2 = new PrintStream(var1);
      }

      MessageHeader var3 = (MessageHeader)this.entries.elementAt(0);
      if (var3.findValue("Manifest-Version") == null) {
         String var4 = System.getProperty("java.version");
         if (var3.findValue("Name") == null) {
            var3.prepend("Manifest-Version", "1.0");
            var3.add("Created-By", "Manifest JDK " + var4);
         } else {
            var2.print("Manifest-Version: 1.0\r\nCreated-By: " + var4 + "\r\n\r\n");
         }

         var2.flush();
      }

      var3.print(var2);

      for(int var6 = 1; var6 < this.entries.size(); ++var6) {
         MessageHeader var5 = (MessageHeader)this.entries.elementAt(var6);
         var5.print(var2);
      }

   }

   public static boolean isManifestName(String var0) {
      if (var0.charAt(0) == '/') {
         var0 = var0.substring(1, var0.length());
      }

      var0 = var0.toUpperCase();
      return var0.equals("META-INF/MANIFEST.MF");
   }
}
