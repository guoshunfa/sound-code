package sun.security.krb5.internal.ccache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.LoginOptions;

public class FileCredentialsCache extends CredentialsCache implements FileCCacheConstants {
   public int version;
   public Tag tag;
   public PrincipalName primaryPrincipal;
   private Vector<Credentials> credentialsList;
   private static String dir;
   private static boolean DEBUG;

   public static synchronized FileCredentialsCache acquireInstance(PrincipalName var0, String var1) {
      try {
         FileCredentialsCache var2 = new FileCredentialsCache();
         if (var1 == null) {
            cacheName = getDefaultCacheName();
         } else {
            cacheName = checkValidation(var1);
         }

         if (cacheName != null && (new File(cacheName)).exists()) {
            if (var0 != null) {
               var2.primaryPrincipal = var0;
            }

            var2.load(cacheName);
            return var2;
         }

         return null;
      } catch (IOException var3) {
         if (DEBUG) {
            var3.printStackTrace();
         }
      } catch (KrbException var4) {
         if (DEBUG) {
            var4.printStackTrace();
         }
      }

      return null;
   }

   public static FileCredentialsCache acquireInstance() {
      return acquireInstance((PrincipalName)null, (String)null);
   }

   static synchronized FileCredentialsCache New(PrincipalName var0, String var1) {
      try {
         FileCredentialsCache var2 = new FileCredentialsCache();
         cacheName = checkValidation(var1);
         if (cacheName == null) {
            return null;
         }

         var2.init(var0, cacheName);
         return var2;
      } catch (IOException var3) {
      } catch (KrbException var4) {
      }

      return null;
   }

   static synchronized FileCredentialsCache New(PrincipalName var0) {
      try {
         FileCredentialsCache var1 = new FileCredentialsCache();
         cacheName = getDefaultCacheName();
         var1.init(var0, cacheName);
         return var1;
      } catch (IOException var2) {
         if (DEBUG) {
            var2.printStackTrace();
         }
      } catch (KrbException var3) {
         if (DEBUG) {
            var3.printStackTrace();
         }
      }

      return null;
   }

   private FileCredentialsCache() {
   }

   boolean exists(String var1) {
      File var2 = new File(var1);
      return var2.exists();
   }

   synchronized void init(PrincipalName var1, String var2) throws IOException, KrbException {
      this.primaryPrincipal = var1;
      FileOutputStream var3 = new FileOutputStream(var2);
      Throwable var4 = null;

      try {
         CCacheOutputStream var5 = new CCacheOutputStream(var3);
         Throwable var6 = null;

         try {
            this.version = 1283;
            var5.writeHeader(this.primaryPrincipal, this.version);
         } catch (Throwable var29) {
            var6 = var29;
            throw var29;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var28) {
                     var6.addSuppressed(var28);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var31) {
         var4 = var31;
         throw var31;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var27) {
                  var4.addSuppressed(var27);
               }
            } else {
               var3.close();
            }
         }

      }

      this.load(var2);
   }

   synchronized void load(String var1) throws IOException, KrbException {
      FileInputStream var3 = new FileInputStream(var1);
      Throwable var4 = null;

      try {
         CCacheInputStream var5 = new CCacheInputStream(var3);
         Throwable var6 = null;

         try {
            this.version = var5.readVersion();
            if (this.version == 1284) {
               this.tag = var5.readTag();
            } else {
               this.tag = null;
               if (this.version == 1281 || this.version == 1282) {
                  var5.setNativeByteOrder();
               }
            }

            PrincipalName var2 = var5.readPrincipal(this.version);
            if (this.primaryPrincipal != null) {
               if (!this.primaryPrincipal.match(var2)) {
                  throw new IOException("Primary principals don't match.");
               }
            } else {
               this.primaryPrincipal = var2;
            }

            this.credentialsList = new Vector();

            while(var5.available() > 0) {
               Credentials var7 = var5.readCred(this.version);
               if (var7 != null) {
                  this.credentialsList.addElement(var7);
               }
            }
         } catch (Throwable var29) {
            var6 = var29;
            throw var29;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var28) {
                     var6.addSuppressed(var28);
                  }
               } else {
                  var5.close();
               }
            }

         }
      } catch (Throwable var31) {
         var4 = var31;
         throw var31;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var27) {
                  var4.addSuppressed(var27);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   public synchronized void update(Credentials var1) {
      if (this.credentialsList != null) {
         if (this.credentialsList.isEmpty()) {
            this.credentialsList.addElement(var1);
         } else {
            Credentials var2 = null;
            boolean var3 = false;

            for(int var4 = 0; var4 < this.credentialsList.size(); ++var4) {
               var2 = (Credentials)this.credentialsList.elementAt(var4);
               if (this.match(var1.sname.getNameStrings(), var2.sname.getNameStrings()) && var1.sname.getRealmString().equalsIgnoreCase(var2.sname.getRealmString())) {
                  var3 = true;
                  if (var1.endtime.getTime() >= var2.endtime.getTime()) {
                     if (DEBUG) {
                        System.out.println(" >>> FileCredentialsCache Ticket matched, overwrite the old one.");
                     }

                     this.credentialsList.removeElementAt(var4);
                     this.credentialsList.addElement(var1);
                  }
               }
            }

            if (!var3) {
               if (DEBUG) {
                  System.out.println(" >>> FileCredentialsCache Ticket not exactly matched, add new one into cache.");
               }

               this.credentialsList.addElement(var1);
            }
         }
      }

   }

   public synchronized PrincipalName getPrimaryPrincipal() {
      return this.primaryPrincipal;
   }

   public synchronized void save() throws IOException, Asn1Exception {
      FileOutputStream var1 = new FileOutputStream(cacheName);
      Throwable var2 = null;

      try {
         CCacheOutputStream var3 = new CCacheOutputStream(var1);
         Throwable var4 = null;

         try {
            var3.writeHeader(this.primaryPrincipal, this.version);
            Credentials[] var5 = null;
            if ((var5 = this.getCredsList()) != null) {
               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var3.addCreds(var5[var6]);
               }
            }
         } catch (Throwable var28) {
            var4 = var28;
            throw var28;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var27) {
                     var4.addSuppressed(var27);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Throwable var30) {
         var2 = var30;
         throw var30;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var26) {
                  var2.addSuppressed(var26);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   boolean match(String[] var1, String[] var2) {
      if (var1.length != var2.length) {
         return false;
      } else {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (!var1[var3].equalsIgnoreCase(var2[var3])) {
               return false;
            }
         }

         return true;
      }
   }

   public synchronized Credentials[] getCredsList() {
      if (this.credentialsList != null && !this.credentialsList.isEmpty()) {
         Credentials[] var1 = new Credentials[this.credentialsList.size()];

         for(int var2 = 0; var2 < this.credentialsList.size(); ++var2) {
            var1[var2] = (Credentials)this.credentialsList.elementAt(var2);
         }

         return var1;
      } else {
         return null;
      }
   }

   public Credentials getCreds(LoginOptions var1, PrincipalName var2) {
      if (var1 == null) {
         return this.getCreds(var2);
      } else {
         Credentials[] var3 = this.getCredsList();
         if (var3 == null) {
            return null;
         } else {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (var2.match(var3[var4].sname) && var3[var4].flags.match(var1)) {
                  return var3[var4];
               }
            }

            return null;
         }
      }
   }

   public Credentials getCreds(PrincipalName var1) {
      Credentials[] var2 = this.getCredsList();
      if (var2 == null) {
         return null;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.match(var2[var3].sname)) {
               return var2[var3];
            }
         }

         return null;
      }
   }

   public Credentials getDefaultCreds() {
      Credentials[] var1 = this.getCredsList();
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = var1.length - 1; var2 >= 0; --var2) {
            if (var1[var2].sname.toString().startsWith("krbtgt")) {
               String[] var3 = var1[var2].sname.getNameStrings();
               if (var3[1].equals(var1[var2].sname.getRealm().toString())) {
                  return var1[var2];
               }
            }
         }

         return null;
      }
   }

   public static String getDefaultCacheName() {
      String var0 = "krb5cc";
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            String var1 = System.getenv("KRB5CCNAME");
            if (var1 != null && var1.length() >= 5 && var1.regionMatches(true, 0, "FILE:", 0, 5)) {
               var1 = var1.substring(5);
            }

            return var1;
         }
      });
      if (var1 != null) {
         if (DEBUG) {
            System.out.println(">>>KinitOptions cache name is " + var1);
         }

         return var1;
      } else {
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
         String var3;
         String var4;
         if (var2 != null) {
            var3 = null;
            var4 = null;
            long var5 = 0L;
            if (!var2.startsWith("Windows")) {
               try {
                  Class var7 = Class.forName("com.sun.security.auth.module.UnixSystem");
                  Constructor var8 = var7.getConstructor();
                  Object var9 = var8.newInstance();
                  Method var10 = var7.getMethod("getUid");
                  var5 = (Long)var10.invoke(var9);
                  var1 = File.separator + "tmp" + File.separator + var0 + "_" + var5;
                  if (DEBUG) {
                     System.out.println(">>>KinitOptions cache name is " + var1);
                  }

                  return var1;
               } catch (Exception var11) {
                  if (DEBUG) {
                     System.out.println("Exception in obtaining uid for Unix platforms Using user's home directory");
                     var11.printStackTrace();
                  }
               }
            }
         }

         var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.name")));
         var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.home")));
         if (var4 == null) {
            var4 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("user.dir")));
         }

         if (var3 != null) {
            var1 = var4 + File.separator + var0 + "_" + var3;
         } else {
            var1 = var4 + File.separator + var0;
         }

         if (DEBUG) {
            System.out.println(">>>KinitOptions cache name is " + var1);
         }

         return var1;
      }
   }

   public static String checkValidation(String var0) {
      String var1 = null;
      if (var0 == null) {
         return null;
      } else {
         try {
            var1 = (new File(var0)).getCanonicalPath();
            File var2 = new File(var1);
            if (!var2.exists()) {
               File var3 = new File(var2.getParent());
               if (!var3.isDirectory()) {
                  var1 = null;
               }

               var3 = null;
            }

            var2 = null;
         } catch (IOException var4) {
            var1 = null;
         }

         return var1;
      }
   }

   private static String exec(String var0) {
      StringTokenizer var1 = new StringTokenizer(var0);
      Vector var2 = new Vector();

      while(var1.hasMoreTokens()) {
         var2.addElement(var1.nextToken());
      }

      final String[] var3 = new String[var2.size()];
      var2.copyInto(var3);

      try {
         Process var4 = (Process)AccessController.doPrivileged(new PrivilegedAction<Process>() {
            public Process run() {
               try {
                  return Runtime.getRuntime().exec(var3);
               } catch (IOException var2) {
                  if (FileCredentialsCache.DEBUG) {
                     var2.printStackTrace();
                  }

                  return null;
               }
            }
         });
         if (var4 == null) {
            return null;
         } else {
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var4.getInputStream(), "8859_1"));
            String var6 = null;
            if (var3.length == 1 && var3[0].equals("/usr/bin/env")) {
               while((var6 = var5.readLine()) != null) {
                  if (var6.length() >= 11 && var6.substring(0, 11).equalsIgnoreCase("KRB5CCNAME=")) {
                     var6 = var6.substring(11);
                     break;
                  }
               }
            } else {
               var6 = var5.readLine();
            }

            var5.close();
            return var6;
         }
      } catch (Exception var7) {
         if (DEBUG) {
            var7.printStackTrace();
         }

         return null;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
