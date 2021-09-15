package sun.security.krb5;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import sun.security.action.GetBooleanAction;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Realm implements Cloneable {
   public static final boolean AUTODEDUCEREALM = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.security.krb5.autodeducerealm")));
   private final String realm;

   public Realm(String var1) throws RealmException {
      this.realm = parseRealm(var1);
   }

   public static Realm getDefault() throws RealmException {
      try {
         return new Realm(Config.getInstance().getDefaultRealm());
      } catch (RealmException var1) {
         throw var1;
      } catch (KrbException var2) {
         throw new RealmException(var2);
      }
   }

   public Object clone() {
      return this;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Realm)) {
         return false;
      } else {
         Realm var2 = (Realm)var1;
         return this.realm.equals(var2.realm);
      }
   }

   public int hashCode() {
      return this.realm.hashCode();
   }

   public Realm(DerValue var1) throws Asn1Exception, RealmException, IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("encoding can not be null");
      } else {
         this.realm = (new KerberosString(var1)).toString();
         if (this.realm != null && this.realm.length() != 0) {
            if (!isValidRealmString(this.realm)) {
               throw new RealmException(600);
            }
         } else {
            throw new RealmException(601);
         }
      }
   }

   public String toString() {
      return this.realm;
   }

   public static String parseRealmAtSeparator(String var0) throws RealmException {
      if (var0 == null) {
         throw new IllegalArgumentException("null input name is not allowed");
      } else {
         String var1 = new String(var0);
         String var2 = null;

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            if (var1.charAt(var3) == '@' && (var3 == 0 || var1.charAt(var3 - 1) != '\\')) {
               if (var3 + 1 >= var1.length()) {
                  throw new IllegalArgumentException("empty realm part not allowed");
               }

               var2 = var1.substring(var3 + 1, var1.length());
               break;
            }
         }

         if (var2 != null) {
            if (var2.length() == 0) {
               throw new RealmException(601);
            }

            if (!isValidRealmString(var2)) {
               throw new RealmException(600);
            }
         }

         return var2;
      }
   }

   public static String parseRealmComponent(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("null input name is not allowed");
      } else {
         String var1 = new String(var0);
         String var2 = null;

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            if (var1.charAt(var3) == '.' && (var3 == 0 || var1.charAt(var3 - 1) != '\\')) {
               if (var3 + 1 < var1.length()) {
                  var2 = var1.substring(var3 + 1, var1.length());
               }
               break;
            }
         }

         return var2;
      }
   }

   protected static String parseRealm(String var0) throws RealmException {
      String var1 = parseRealmAtSeparator(var0);
      if (var1 == null) {
         var1 = var0;
      }

      if (var1 != null && var1.length() != 0) {
         if (!isValidRealmString(var1)) {
            throw new RealmException(600);
         } else {
            return var1;
         }
      } else {
         throw new RealmException(601);
      }
   }

   protected static boolean isValidRealmString(String var0) {
      if (var0 == null) {
         return false;
      } else if (var0.length() == 0) {
         return false;
      } else {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            if (var0.charAt(var1) == '/' || var0.charAt(var1) == ':' || var0.charAt(var1) == 0) {
               return false;
            }
         }

         return true;
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putDerValue((new KerberosString(this.realm)).toDerValue());
      return var1.toByteArray();
   }

   public static Realm parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException, RealmException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new Realm(var4);
         }
      }
   }

   public static String[] getRealmsList(String var0, String var1) {
      try {
         return parseCapaths(var0, var1);
      } catch (KrbException var3) {
         return parseHierarchy(var0, var1);
      }
   }

   private static String[] parseCapaths(String var0, String var1) throws KrbException {
      Config var2 = Config.getInstance();
      if (!var2.exists("capaths", var0, var1)) {
         throw new KrbException("No conf");
      } else {
         LinkedList var3 = new LinkedList();
         String var4 = var1;

         while(true) {
            String var5 = var2.getAll("capaths", var0, var4);
            if (var5 == null) {
               break;
            }

            String[] var6 = var5.split("\\s+");
            boolean var7 = false;

            for(int var8 = var6.length - 1; var8 >= 0; --var8) {
               if (!var3.contains(var6[var8]) && !var6[var8].equals(".") && !var6[var8].equals(var0) && !var6[var8].equals(var1) && !var6[var8].equals(var4)) {
                  var7 = true;
                  var3.addFirst(var6[var8]);
               }
            }

            if (!var7) {
               break;
            }

            var4 = (String)var3.getFirst();
         }

         var3.addFirst(var0);
         return (String[])var3.toArray(new String[var3.size()]);
      }
   }

   private static String[] parseHierarchy(String var0, String var1) {
      String[] var2 = var0.split("\\.");
      String[] var3 = var1.split("\\.");
      int var4 = var2.length;
      int var5 = var3.length;
      boolean var6 = false;
      --var5;
      --var4;

      while(var5 >= 0 && var4 >= 0 && var3[var5].equals(var2[var4])) {
         var6 = true;
         --var5;
         --var4;
      }

      LinkedList var7 = new LinkedList();

      int var8;
      for(var8 = 0; var8 <= var4; ++var8) {
         var7.addLast(subStringFrom(var2, var8));
      }

      if (var6) {
         var7.addLast(subStringFrom(var2, var4 + 1));
      }

      for(var8 = var5; var8 >= 0; --var8) {
         var7.addLast(subStringFrom(var3, var8));
      }

      var7.removeLast();
      return (String[])var7.toArray(new String[var7.size()]);
   }

   private static String subStringFrom(String[] var0, int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = var1; var3 < var0.length; ++var3) {
         if (var2.length() != 0) {
            var2.append('.');
         }

         var2.append(var0[var3]);
      }

      return var2.toString();
   }
}
