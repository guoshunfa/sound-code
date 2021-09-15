package sun.security.krb5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.dns.ResolverConfiguration;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class Config {
   private static Config singleton = null;
   private Hashtable<String, Object> stanzaTable = new Hashtable();
   private static boolean DEBUG;
   private static final int BASE16_0 = 1;
   private static final int BASE16_1 = 16;
   private static final int BASE16_2 = 256;
   private static final int BASE16_3 = 4096;
   private final String defaultRealm;
   private final String defaultKDC;

   private static native String getWindowsDirectory(boolean var0);

   public static synchronized Config getInstance() throws KrbException {
      if (singleton == null) {
         singleton = new Config();
      }

      return singleton;
   }

   public static synchronized void refresh() throws KrbException {
      singleton = new Config();
      KdcComm.initStatic();
      EType.initStatic();
      Checksum.initStatic();
   }

   private static boolean isMacosLionOrBetter() {
      String var0 = getProperty("os.name");
      if (!var0.contains("OS X")) {
         return false;
      } else {
         String var1 = getProperty("os.version");
         String[] var2 = var1.split("\\.");
         if (!var2[0].equals("10")) {
            return false;
         } else if (var2.length < 2) {
            return false;
         } else {
            try {
               int var3 = Integer.parseInt(var2[1]);
               if (var3 >= 7) {
                  return true;
               }
            } catch (NumberFormatException var4) {
            }

            return false;
         }
      }
   }

   private Config() throws KrbException {
      String var1 = getProperty("java.security.krb5.kdc");
      if (var1 != null) {
         this.defaultKDC = var1.replace(':', ' ');
      } else {
         this.defaultKDC = null;
      }

      this.defaultRealm = getProperty("java.security.krb5.realm");
      if (this.defaultKDC == null && this.defaultRealm != null || this.defaultRealm == null && this.defaultKDC != null) {
         throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set.");
      } else {
         try {
            String var3 = this.getJavaFileName();
            List var2;
            if (var3 != null) {
               var2 = this.loadConfigFile(var3);
               this.stanzaTable = this.parseStanzaTable(var2);
               if (DEBUG) {
                  System.out.println("Loaded from Java config");
               }
            } else {
               boolean var4 = false;
               if (isMacosLionOrBetter()) {
                  try {
                     this.stanzaTable = SCDynamicStoreConfig.getConfig();
                     if (DEBUG) {
                        System.out.println("Loaded from SCDynamicStoreConfig");
                     }

                     var4 = true;
                  } catch (IOException var6) {
                  }
               }

               if (!var4) {
                  var3 = this.getNativeFileName();
                  var2 = this.loadConfigFile(var3);
                  this.stanzaTable = this.parseStanzaTable(var2);
                  if (DEBUG) {
                     System.out.println("Loaded from native config");
                  }
               }
            }
         } catch (IOException var7) {
         }

      }
   }

   public String get(String... var1) {
      Vector var2 = this.getString0(var1);
      return var2 == null ? null : (String)var2.lastElement();
   }

   private Boolean getBooleanObject(String... var1) {
      String var2 = this.get(var1);
      if (var2 == null) {
         return null;
      } else {
         String var3 = var2.toLowerCase(Locale.US);
         byte var4 = -1;
         switch(var3.hashCode()) {
         case 3521:
            if (var3.equals("no")) {
               var4 = 2;
            }
            break;
         case 119527:
            if (var3.equals("yes")) {
               var4 = 0;
            }
            break;
         case 3569038:
            if (var3.equals("true")) {
               var4 = 1;
            }
            break;
         case 97196323:
            if (var3.equals("false")) {
               var4 = 3;
            }
         }

         switch(var4) {
         case 0:
         case 1:
            return Boolean.TRUE;
         case 2:
         case 3:
            return Boolean.FALSE;
         default:
            return null;
         }
      }
   }

   public String getAll(String... var1) {
      Vector var2 = this.getString0(var1);
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder();
         boolean var4 = true;
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (var4) {
               var3.append(var6);
               var4 = false;
            } else {
               var3.append(' ').append(var6);
            }
         }

         return var3.toString();
      }
   }

   public boolean exists(String... var1) {
      return this.get0(var1) != null;
   }

   private Vector<String> getString0(String... var1) {
      try {
         return (Vector)this.get0(var1);
      } catch (ClassCastException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   private Object get0(String... var1) {
      Object var2 = this.stanzaTable;

      try {
         String[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var2 = ((Hashtable)var2).get(var6);
            if (var2 == null) {
               return null;
            }
         }

         return var2;
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException(var7);
      }
   }

   public int getIntValue(String... var1) {
      String var2 = this.get(var1);
      int var3 = Integer.MIN_VALUE;
      if (var2 != null) {
         try {
            var3 = this.parseIntValue(var2);
         } catch (NumberFormatException var5) {
            if (DEBUG) {
               System.out.println("Exception in getting value of " + Arrays.toString((Object[])var1) + " " + var5.getMessage());
               System.out.println("Setting " + Arrays.toString((Object[])var1) + " to minimum value");
            }

            var3 = Integer.MIN_VALUE;
         }
      }

      return var3;
   }

   public boolean getBooleanValue(String... var1) {
      String var2 = this.get(var1);
      return var2 != null && var2.equalsIgnoreCase("true");
   }

   private int parseIntValue(String var1) throws NumberFormatException {
      int var2 = 0;
      String var3;
      if (var1.startsWith("+")) {
         var3 = var1.substring(1);
         return Integer.parseInt(var3);
      } else {
         if (var1.startsWith("0x")) {
            var3 = var1.substring(2);
            char[] var4 = var3.toCharArray();
            if (var4.length > 8) {
               throw new NumberFormatException();
            }

            for(int var5 = 0; var5 < var4.length; ++var5) {
               int var6 = var4.length - var5 - 1;
               switch(var4[var5]) {
               case '0':
                  var2 += 0;
                  break;
               case '1':
                  var2 += 1 * this.getBase(var6);
                  break;
               case '2':
                  var2 += 2 * this.getBase(var6);
                  break;
               case '3':
                  var2 += 3 * this.getBase(var6);
                  break;
               case '4':
                  var2 += 4 * this.getBase(var6);
                  break;
               case '5':
                  var2 += 5 * this.getBase(var6);
                  break;
               case '6':
                  var2 += 6 * this.getBase(var6);
                  break;
               case '7':
                  var2 += 7 * this.getBase(var6);
                  break;
               case '8':
                  var2 += 8 * this.getBase(var6);
                  break;
               case '9':
                  var2 += 9 * this.getBase(var6);
                  break;
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case 'G':
               case 'H':
               case 'I':
               case 'J':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'S':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               case 'Z':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '_':
               case '`':
               default:
                  throw new NumberFormatException("Invalid numerical format");
               case 'A':
               case 'a':
                  var2 += 10 * this.getBase(var6);
                  break;
               case 'B':
               case 'b':
                  var2 += 11 * this.getBase(var6);
                  break;
               case 'C':
               case 'c':
                  var2 += 12 * this.getBase(var6);
                  break;
               case 'D':
               case 'd':
                  var2 += 13 * this.getBase(var6);
                  break;
               case 'E':
               case 'e':
                  var2 += 14 * this.getBase(var6);
                  break;
               case 'F':
               case 'f':
                  var2 += 15 * this.getBase(var6);
               }
            }

            if (var2 < 0) {
               throw new NumberFormatException("Data overflow.");
            }
         } else {
            var2 = Integer.parseInt(var1);
         }

         return var2;
      }
   }

   private int getBase(int var1) {
      int var2 = 16;
      switch(var1) {
      case 0:
         var2 = 1;
         break;
      case 1:
         var2 = 16;
         break;
      case 2:
         var2 = 256;
         break;
      case 3:
         var2 = 4096;
         break;
      default:
         for(int var3 = 1; var3 < var1; ++var3) {
            var2 *= 16;
         }
      }

      return var2;
   }

   private List<String> loadConfigFile(final String var1) throws IOException, KrbException {
      try {
         ArrayList var2 = new ArrayList();
         BufferedReader var3 = new BufferedReader(new InputStreamReader((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
            public FileInputStream run() throws IOException {
               return new FileInputStream(var1);
            }
         })));
         Throwable var4 = null;

         try {
            String var6 = null;

            String var5;
            while((var5 = var3.readLine()) != null) {
               var5 = var5.trim();
               if (!var5.isEmpty() && !var5.startsWith("#") && !var5.startsWith(";")) {
                  if (var5.startsWith("[")) {
                     if (!var5.endsWith("]")) {
                        throw new KrbException("Illegal config content:" + var5);
                     }

                     if (var6 != null) {
                        var2.add(var6);
                        var2.add("}");
                     }

                     String var7 = var5.substring(1, var5.length() - 1).trim();
                     if (var7.isEmpty()) {
                        throw new KrbException("Illegal config content:" + var5);
                     }

                     var6 = var7 + " = {";
                  } else if (var5.startsWith("{")) {
                     if (var6 == null) {
                        throw new KrbException("Config file should not start with \"{\"");
                     }

                     var6 = var6 + " {";
                     if (var5.length() > 1) {
                        var2.add(var6);
                        var6 = var5.substring(1).trim();
                     }
                  } else if (var6 != null) {
                     var2.add(var6);
                     var6 = var5;
                  }
               }
            }

            if (var6 != null) {
               var2.add(var6);
               var2.add("}");
            }
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var15) {
                     var4.addSuppressed(var15);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return var2;
      } catch (PrivilegedActionException var18) {
         throw (IOException)var18.getException();
      }
   }

   private Hashtable<String, Object> parseStanzaTable(List<String> var1) throws KrbException {
      Hashtable var2 = this.stanzaTable;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (var4.equals("}")) {
            var2 = (Hashtable)var2.remove(" PARENT ");
            if (var2 == null) {
               throw new KrbException("Unmatched close brace");
            }
         } else {
            int var5 = var4.indexOf(61);
            if (var5 < 0) {
               throw new KrbException("Illegal config content:" + var4);
            }

            String var6 = var4.substring(0, var5).trim();
            String var7 = trimmed(var4.substring(var5 + 1));
            if (var7.equals("{")) {
               if (var2 == this.stanzaTable) {
                  var6 = var6.toLowerCase(Locale.US);
               }

               Hashtable var8 = new Hashtable();
               var2.put(var6, var8);
               var8.put(" PARENT ", var2);
               var2 = var8;
            } else {
               Vector var10;
               if (var2.containsKey(var6)) {
                  Object var9 = var2.get(var6);
                  if (!(var9 instanceof Vector)) {
                     throw new KrbException("Key " + var6 + "used for both value and section");
                  }

                  var10 = (Vector)var2.get(var6);
               } else {
                  var10 = new Vector();
                  var2.put(var6, var10);
               }

               var10.add(var7);
            }
         }
      }

      if (var2 != this.stanzaTable) {
         throw new KrbException("Not closed");
      } else {
         return var2;
      }
   }

   private String getJavaFileName() {
      String var1 = getProperty("java.security.krb5.conf");
      if (var1 == null) {
         var1 = getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
         if (!this.fileExists(var1)) {
            var1 = null;
         }
      }

      if (DEBUG) {
         System.out.println("Java config name: " + var1);
      }

      return var1;
   }

   private String getNativeFileName() {
      String var1 = null;
      String var2 = getProperty("os.name");
      if (var2.startsWith("Windows")) {
         try {
            Credentials.ensureLoaded();
         } catch (Exception var4) {
         }

         if (Credentials.alreadyLoaded) {
            String var3 = getWindowsDirectory(false);
            if (var3 != null) {
               if (var3.endsWith("\\")) {
                  var3 = var3 + "krb5.ini";
               } else {
                  var3 = var3 + "\\krb5.ini";
               }

               if (this.fileExists(var3)) {
                  var1 = var3;
               }
            }

            if (var1 == null) {
               var3 = getWindowsDirectory(true);
               if (var3 != null) {
                  if (var3.endsWith("\\")) {
                     var3 = var3 + "krb5.ini";
                  } else {
                     var3 = var3 + "\\krb5.ini";
                  }

                  var1 = var3;
               }
            }
         }

         if (var1 == null) {
            var1 = "c:\\winnt\\krb5.ini";
         }
      } else if (var2.startsWith("SunOS")) {
         var1 = "/etc/krb5/krb5.conf";
      } else if (var2.contains("OS X")) {
         var1 = this.findMacosConfigFile();
      } else {
         var1 = "/etc/krb5.conf";
      }

      if (DEBUG) {
         System.out.println("Native config name: " + var1);
      }

      return var1;
   }

   private static String getProperty(String var0) {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0)));
   }

   private String findMacosConfigFile() {
      String var1 = getProperty("user.home");
      String var3 = var1 + "/Library/Preferences/edu.mit.Kerberos";
      if (this.fileExists(var3)) {
         return var3;
      } else {
         return this.fileExists("/Library/Preferences/edu.mit.Kerberos") ? "/Library/Preferences/edu.mit.Kerberos" : "/etc/krb5.conf";
      }
   }

   private static String trimmed(String var0) {
      var0 = var0.trim();
      if (var0.length() >= 2 && (var0.charAt(0) == '"' && var0.charAt(var0.length() - 1) == '"' || var0.charAt(0) == '\'' && var0.charAt(var0.length() - 1) == '\'')) {
         var0 = var0.substring(1, var0.length() - 1).trim();
      }

      return var0;
   }

   public void listTable() {
      System.out.println((Object)this);
   }

   public int[] defaultEtype(String var1) throws KrbException {
      String var2 = this.get("libdefaults", var1);
      int[] var3;
      if (var2 == null) {
         if (DEBUG) {
            System.out.println("Using builtin default etypes for " + var1);
         }

         var3 = EType.getBuiltInDefaults();
      } else {
         String var4 = " ";

         int var6;
         for(var6 = 0; var6 < var2.length(); ++var6) {
            if (var2.substring(var6, var6 + 1).equals(",")) {
               var4 = ",";
               break;
            }
         }

         StringTokenizer var5 = new StringTokenizer(var2, var4);
         var6 = var5.countTokens();
         ArrayList var7 = new ArrayList(var6);

         int var9;
         for(var9 = 0; var9 < var6; ++var9) {
            int var8 = getType(var5.nextToken());
            if (var8 != -1 && EType.isSupported(var8)) {
               var7.add(var8);
            }
         }

         if (var7.isEmpty()) {
            throw new KrbException("no supported default etypes for " + var1);
         }

         var3 = new int[var7.size()];

         for(var9 = 0; var9 < var3.length; ++var9) {
            var3[var9] = (Integer)var7.get(var9);
         }
      }

      if (DEBUG) {
         System.out.print("default etypes for " + var1 + ":");

         for(int var10 = 0; var10 < var3.length; ++var10) {
            System.out.print(" " + var3[var10]);
         }

         System.out.println(".");
      }

      return var3;
   }

   public static int getType(String var0) {
      short var1 = -1;
      if (var0 == null) {
         return var1;
      } else {
         if (!var0.startsWith("d") && !var0.startsWith("D")) {
            if (!var0.startsWith("a") && !var0.startsWith("A")) {
               if (var0.equalsIgnoreCase("rc4-hmac")) {
                  var1 = 23;
               } else if (var0.equalsIgnoreCase("CRC32")) {
                  var1 = 1;
               } else if (!var0.startsWith("r") && !var0.startsWith("R")) {
                  if (var0.equalsIgnoreCase("hmac-sha1-des3-kd")) {
                     var1 = 12;
                  } else if (var0.equalsIgnoreCase("hmac-sha1-96-aes128")) {
                     var1 = 15;
                  } else if (var0.equalsIgnoreCase("hmac-sha1-96-aes256")) {
                     var1 = 16;
                  } else if (!var0.equalsIgnoreCase("hmac-md5-rc4") && !var0.equalsIgnoreCase("hmac-md5-arcfour") && !var0.equalsIgnoreCase("hmac-md5-enc")) {
                     if (var0.equalsIgnoreCase("NULL")) {
                        var1 = 0;
                     }
                  } else {
                     var1 = -138;
                  }
               } else if (var0.equalsIgnoreCase("rsa-md5")) {
                  var1 = 7;
               } else if (var0.equalsIgnoreCase("rsa-md5-des")) {
                  var1 = 8;
               }
            } else if (!var0.equalsIgnoreCase("aes128-cts") && !var0.equalsIgnoreCase("aes128-cts-hmac-sha1-96")) {
               if (!var0.equalsIgnoreCase("aes256-cts") && !var0.equalsIgnoreCase("aes256-cts-hmac-sha1-96")) {
                  if (var0.equalsIgnoreCase("arcfour-hmac") || var0.equalsIgnoreCase("arcfour-hmac-md5")) {
                     var1 = 23;
                  }
               } else {
                  var1 = 18;
               }
            } else {
               var1 = 17;
            }
         } else if (var0.equalsIgnoreCase("des-cbc-crc")) {
            var1 = 1;
         } else if (var0.equalsIgnoreCase("des-cbc-md5")) {
            var1 = 3;
         } else if (var0.equalsIgnoreCase("des-mac")) {
            var1 = 4;
         } else if (var0.equalsIgnoreCase("des-mac-k")) {
            var1 = 5;
         } else if (var0.equalsIgnoreCase("des-cbc-md4")) {
            var1 = 2;
         } else if (var0.equalsIgnoreCase("des3-cbc-sha1") || var0.equalsIgnoreCase("des3-hmac-sha1") || var0.equalsIgnoreCase("des3-cbc-sha1-kd") || var0.equalsIgnoreCase("des3-cbc-hmac-sha1-kd")) {
            var1 = 16;
         }

         return var1;
      }
   }

   public void resetDefaultRealm(String var1) {
      if (DEBUG) {
         System.out.println(">>> Config try resetting default kdc " + var1);
      }

   }

   public boolean useAddresses() {
      boolean var1 = false;
      String var2 = this.get("libdefaults", "no_addresses");
      var1 = var2 != null && var2.equalsIgnoreCase("false");
      if (!var1) {
         var2 = this.get("libdefaults", "noaddresses");
         var1 = var2 != null && var2.equalsIgnoreCase("false");
      }

      return var1;
   }

   private boolean useDNS(String var1, boolean var2) {
      Boolean var3 = this.getBooleanObject("libdefaults", var1);
      if (var3 != null) {
         return var3;
      } else {
         var3 = this.getBooleanObject("libdefaults", "dns_fallback");
         return var3 != null ? var3 : var2;
      }
   }

   private boolean useDNS_KDC() {
      return this.useDNS("dns_lookup_kdc", true);
   }

   private boolean useDNS_Realm() {
      return this.useDNS("dns_lookup_realm", false);
   }

   public String getDefaultRealm() throws KrbException {
      if (this.defaultRealm != null) {
         return this.defaultRealm;
      } else {
         KrbException var1 = null;
         String var2 = this.get("libdefaults", "default_realm");
         if (var2 == null && this.useDNS_Realm()) {
            try {
               var2 = this.getRealmFromDNS();
            } catch (KrbException var4) {
               var1 = var4;
            }
         }

         if (var2 == null) {
            var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  String var1 = System.getProperty("os.name");
                  return var1.startsWith("Windows") ? System.getenv("USERDNSDOMAIN") : null;
               }
            });
         }

         if (var2 == null) {
            KrbException var3 = new KrbException("Cannot locate default realm");
            if (var1 != null) {
               var3.initCause(var1);
            }

            throw var3;
         } else {
            return var2;
         }
      }
   }

   public String getKDCList(String var1) throws KrbException {
      if (var1 == null) {
         var1 = this.getDefaultRealm();
      }

      if (var1.equalsIgnoreCase(this.defaultRealm)) {
         return this.defaultKDC;
      } else {
         KrbException var2 = null;
         String var3 = this.getAll("realms", var1, "kdc");
         if (var3 == null && this.useDNS_KDC()) {
            try {
               var3 = this.getKDCFromDNS(var1);
            } catch (KrbException var5) {
               var2 = var5;
            }
         }

         if (var3 == null) {
            var3 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  String var1 = System.getProperty("os.name");
                  if (var1.startsWith("Windows")) {
                     String var2 = System.getenv("LOGONSERVER");
                     if (var2 != null && var2.startsWith("\\\\")) {
                        var2 = var2.substring(2);
                     }

                     return var2;
                  } else {
                     return null;
                  }
               }
            });
         }

         if (var3 == null) {
            if (this.defaultKDC != null) {
               return this.defaultKDC;
            } else {
               KrbException var4 = new KrbException("Cannot locate KDC");
               if (var2 != null) {
                  var4.initCause(var2);
               }

               throw var4;
            }
         } else {
            return var3;
         }
      }
   }

   private String getRealmFromDNS() throws KrbException {
      String var1 = null;
      String var2 = null;

      try {
         var2 = InetAddress.getLocalHost().getCanonicalHostName();
      } catch (UnknownHostException var7) {
         KrbException var4 = new KrbException(60, "Unable to locate Kerberos realm: " + var7.getMessage());
         var4.initCause(var7);
         throw var4;
      }

      String var3 = PrincipalName.mapHostToRealm(var2);
      if (var3 == null) {
         List var8 = ResolverConfiguration.open().searchlist();
         Iterator var5 = var8.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            var1 = checkRealm(var6);
            if (var1 != null) {
               break;
            }
         }
      } else {
         var1 = checkRealm(var3);
      }

      if (var1 == null) {
         throw new KrbException(60, "Unable to locate Kerberos realm");
      } else {
         return var1;
      }
   }

   private static String checkRealm(String var0) {
      if (DEBUG) {
         System.out.println("getRealmFromDNS: trying " + var0);
      }

      String[] var1 = null;

      for(String var2 = var0; var1 == null && var2 != null; var2 = Realm.parseRealmComponent(var2)) {
         var1 = KrbServiceLocator.getKerberosService(var2);
      }

      if (var1 != null) {
         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3].equalsIgnoreCase(var0)) {
               return var1[var3];
            }
         }
      }

      return null;
   }

   private String getKDCFromDNS(String var1) throws KrbException {
      String var2 = "";
      String[] var3 = null;
      if (DEBUG) {
         System.out.println("getKDCFromDNS using UDP");
      }

      var3 = KrbServiceLocator.getKerberosService(var1, "_udp");
      if (var3 == null) {
         if (DEBUG) {
            System.out.println("getKDCFromDNS using TCP");
         }

         var3 = KrbServiceLocator.getKerberosService(var1, "_tcp");
      }

      if (var3 == null) {
         throw new KrbException(60, "Unable to locate KDC for realm " + var1);
      } else if (var3.length == 0) {
         return null;
      } else {
         for(int var4 = 0; var4 < var3.length; ++var4) {
            var2 = var2 + var3[var4].trim() + " ";
         }

         var2 = var2.trim();
         return var2.equals("") ? null : var2;
      }
   }

   private boolean fileExists(String var1) {
      return (Boolean)AccessController.doPrivileged((PrivilegedAction)(new Config.FileExistsAction(var1)));
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      toStringInternal("", this.stanzaTable, var1);
      return var1.toString();
   }

   private static void toStringInternal(String var0, Object var1, StringBuffer var2) {
      if (var1 instanceof String) {
         var2.append(var1).append('\n');
      } else if (var1 instanceof Hashtable) {
         Hashtable var3 = (Hashtable)var1;
         var2.append("{\n");
         Iterator var4 = var3.keySet().iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var2.append(var0).append("    ").append(var5).append(" = ");
            toStringInternal(var0 + "    ", var3.get(var5), var2);
         }

         var2.append(var0).append("}\n");
      } else if (var1 instanceof Vector) {
         Vector var9 = (Vector)var1;
         var2.append("[");
         boolean var10 = true;
         Object[] var11 = var9.toArray();
         int var6 = var11.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Object var8 = var11[var7];
            if (!var10) {
               var2.append(",");
            }

            var2.append(var8);
            var10 = false;
         }

         var2.append("]\n");
      }

   }

   static {
      DEBUG = Krb5.DEBUG;
   }

   static class FileExistsAction implements PrivilegedAction<Boolean> {
      private String fileName;

      public FileExistsAction(String var1) {
         this.fileName = var1;
      }

      public Boolean run() {
         return (new File(this.fileName)).exists();
      }
   }
}
