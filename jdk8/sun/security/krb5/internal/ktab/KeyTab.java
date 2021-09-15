package sun.security.krb5.internal.ktab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.Config;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class KeyTab implements KeyTabConstants {
   private static final boolean DEBUG;
   private static String defaultTabName;
   private static Map<String, KeyTab> map;
   private boolean isMissing = false;
   private boolean isValid = true;
   private final String tabName;
   private long lastModified;
   private int kt_vno = 1282;
   private Vector<KeyTabEntry> entries = new Vector();

   private KeyTab(String var1) {
      this.tabName = var1;

      try {
         this.lastModified = (new File(this.tabName)).lastModified();
         KeyTabInputStream var2 = new KeyTabInputStream(new FileInputStream(var1));
         Throwable var3 = null;

         try {
            this.load(var2);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (FileNotFoundException var16) {
         this.entries.clear();
         this.isMissing = true;
      } catch (Exception var17) {
         this.entries.clear();
         this.isValid = false;
      }

   }

   private static synchronized KeyTab getInstance0(String var0) {
      long var1 = (new File(var0)).lastModified();
      KeyTab var3 = (KeyTab)map.get(var0);
      if (var3 != null && var3.isValid() && var3.lastModified == var1) {
         return var3;
      } else {
         KeyTab var4 = new KeyTab(var0);
         if (var4.isValid()) {
            map.put(var0, var4);
            return var4;
         } else {
            return var3 != null ? var3 : var4;
         }
      }
   }

   public static KeyTab getInstance(String var0) {
      return var0 == null ? getInstance() : getInstance0(normalize(var0));
   }

   public static KeyTab getInstance(File var0) {
      return var0 == null ? getInstance() : getInstance0(var0.getPath());
   }

   public static KeyTab getInstance() {
      return getInstance(getDefaultTabName());
   }

   public boolean isMissing() {
      return this.isMissing;
   }

   public boolean isValid() {
      return this.isValid;
   }

   private static String getDefaultTabName() {
      if (defaultTabName != null) {
         return defaultTabName;
      } else {
         String var0 = null;

         String var1;
         try {
            var1 = Config.getInstance().get("libdefaults", "default_keytab_name");
            if (var1 != null) {
               StringTokenizer var2 = new StringTokenizer(var1, " ");

               while(var2.hasMoreTokens()) {
                  var0 = normalize(var2.nextToken());
                  if ((new File(var0)).exists()) {
                     break;
                  }
               }
            }
         } catch (KrbException var3) {
            var0 = null;
         }

         if (var0 == null) {
            var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.home")));
            if (var1 == null) {
               var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.dir")));
            }

            var0 = var1 + File.separator + "krb5.keytab";
         }

         defaultTabName = var0;
         return var0;
      }
   }

   public static String normalize(String var0) {
      String var1;
      if (var0.length() >= 5 && var0.substring(0, 5).equalsIgnoreCase("FILE:")) {
         var1 = var0.substring(5);
      } else if (var0.length() >= 9 && var0.substring(0, 9).equalsIgnoreCase("ANY:FILE:")) {
         var1 = var0.substring(9);
      } else if (var0.length() >= 7 && var0.substring(0, 7).equalsIgnoreCase("SRVTAB:")) {
         var1 = var0.substring(7);
      } else {
         var1 = var0;
      }

      return var1;
   }

   private void load(KeyTabInputStream var1) throws IOException, RealmException {
      this.entries.clear();
      this.kt_vno = var1.readVersion();
      if (this.kt_vno == 1281) {
         var1.setNativeByteOrder();
      }

      boolean var2 = false;

      while(var1.available() > 0) {
         int var4 = var1.readEntryLength();
         KeyTabEntry var3 = var1.readEntry(var4, this.kt_vno);
         if (DEBUG) {
            System.out.println(">>> KeyTab: load() entry length: " + var4 + "; type: " + (var3 != null ? var3.keyType : 0));
         }

         if (var3 != null) {
            this.entries.addElement(var3);
         }
      }

   }

   public PrincipalName getOneName() {
      int var1 = this.entries.size();
      return var1 > 0 ? ((KeyTabEntry)this.entries.elementAt(var1 - 1)).service : null;
   }

   public EncryptionKey[] readServiceKeys(PrincipalName var1) {
      int var4 = this.entries.size();
      ArrayList var5 = new ArrayList(var4);
      if (DEBUG) {
         System.out.println("Looking for keys for: " + var1);
      }

      for(int var6 = var4 - 1; var6 >= 0; --var6) {
         KeyTabEntry var2 = (KeyTabEntry)this.entries.elementAt(var6);
         if (var2.service.match(var1)) {
            if (EType.isSupported(var2.keyType)) {
               EncryptionKey var3 = new EncryptionKey(var2.keyblock, var2.keyType, new Integer(var2.keyVersion));
               var5.add(var3);
               if (DEBUG) {
                  System.out.println("Added key: " + var2.keyType + "version: " + var2.keyVersion);
               }
            } else if (DEBUG) {
               System.out.println("Found unsupported keytype (" + var2.keyType + ") for " + var1);
            }
         }
      }

      var4 = var5.size();
      EncryptionKey[] var7 = (EncryptionKey[])var5.toArray(new EncryptionKey[var4]);
      Arrays.sort(var7, new Comparator<EncryptionKey>() {
         public int compare(EncryptionKey var1, EncryptionKey var2) {
            return var2.getKeyVersionNumber() - var1.getKeyVersionNumber();
         }
      });
      return var7;
   }

   public boolean findServiceEntry(PrincipalName var1) {
      for(int var3 = 0; var3 < this.entries.size(); ++var3) {
         KeyTabEntry var2 = (KeyTabEntry)this.entries.elementAt(var3);
         if (var2.service.match(var1)) {
            if (EType.isSupported(var2.keyType)) {
               return true;
            }

            if (DEBUG) {
               System.out.println("Found unsupported keytype (" + var2.keyType + ") for " + var1);
            }
         }
      }

      return false;
   }

   public String tabName() {
      return this.tabName;
   }

   public void addEntry(PrincipalName var1, char[] var2, int var3, boolean var4) throws KrbException {
      this.addEntry(var1, var1.getSalt(), var2, var3, var4);
   }

   public void addEntry(PrincipalName var1, String var2, char[] var3, int var4, boolean var5) throws KrbException {
      EncryptionKey[] var6 = EncryptionKey.acquireSecretKeys(var3, var2);
      int var7 = 0;

      int var8;
      for(var8 = this.entries.size() - 1; var8 >= 0; --var8) {
         KeyTabEntry var9 = (KeyTabEntry)this.entries.get(var8);
         if (var9.service.match(var1)) {
            if (var9.keyVersion > var7) {
               var7 = var9.keyVersion;
            }

            if (!var5 || var9.keyVersion == var4) {
               this.entries.removeElementAt(var8);
            }
         }
      }

      if (var4 == -1) {
         var4 = var7 + 1;
      }

      for(var8 = 0; var6 != null && var8 < var6.length; ++var8) {
         int var12 = var6[var8].getEType();
         byte[] var10 = var6[var8].getBytes();
         KeyTabEntry var11 = new KeyTabEntry(var1, var1.getRealm(), new KerberosTime(System.currentTimeMillis()), var4, var12, var10);
         this.entries.addElement(var11);
      }

   }

   public KeyTabEntry[] getEntries() {
      KeyTabEntry[] var1 = new KeyTabEntry[this.entries.size()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = (KeyTabEntry)this.entries.elementAt(var2);
      }

      return var1;
   }

   public static synchronized KeyTab create() throws IOException, RealmException {
      String var0 = getDefaultTabName();
      return create(var0);
   }

   public static synchronized KeyTab create(String var0) throws IOException, RealmException {
      KeyTabOutputStream var1 = new KeyTabOutputStream(new FileOutputStream(var0));
      Throwable var2 = null;

      try {
         var1.writeVersion(1282);
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               var1.close();
            }
         }

      }

      return new KeyTab(var0);
   }

   public synchronized void save() throws IOException {
      KeyTabOutputStream var1 = new KeyTabOutputStream(new FileOutputStream(this.tabName));
      Throwable var2 = null;

      try {
         var1.writeVersion(this.kt_vno);

         for(int var3 = 0; var3 < this.entries.size(); ++var3) {
            var1.writeEntry((KeyTabEntry)this.entries.elementAt(var3));
         }
      } catch (Throwable var11) {
         var2 = var11;
         throw var11;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var10) {
                  var2.addSuppressed(var10);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public int deleteEntries(PrincipalName var1, int var2, int var3) {
      int var4 = 0;
      HashMap var5 = new HashMap();

      int var6;
      KeyTabEntry var7;
      int var8;
      for(var6 = this.entries.size() - 1; var6 >= 0; --var6) {
         var7 = (KeyTabEntry)this.entries.get(var6);
         if (var1.match(var7.getService()) && (var2 == -1 || var7.keyType == var2)) {
            if (var3 == -2) {
               if (var5.containsKey(var7.keyType)) {
                  var8 = (Integer)var5.get(var7.keyType);
                  if (var7.keyVersion > var8) {
                     var5.put(var7.keyType, var7.keyVersion);
                  }
               } else {
                  var5.put(var7.keyType, var7.keyVersion);
               }
            } else if (var3 == -1 || var7.keyVersion == var3) {
               this.entries.removeElementAt(var6);
               ++var4;
            }
         }
      }

      if (var3 == -2) {
         for(var6 = this.entries.size() - 1; var6 >= 0; --var6) {
            var7 = (KeyTabEntry)this.entries.get(var6);
            if (var1.match(var7.getService()) && (var2 == -1 || var7.keyType == var2)) {
               var8 = (Integer)var5.get(var7.keyType);
               if (var7.keyVersion != var8) {
                  this.entries.removeElementAt(var6);
                  ++var4;
               }
            }
         }
      }

      return var4;
   }

   public synchronized void createVersion(File var1) throws IOException {
      KeyTabOutputStream var2 = new KeyTabOutputStream(new FileOutputStream(var1));
      Throwable var3 = null;

      try {
         var2.write16(1282);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   static {
      DEBUG = Krb5.DEBUG;
      defaultTabName = null;
      map = new HashMap();
   }
}
