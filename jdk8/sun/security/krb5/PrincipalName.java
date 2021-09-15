package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import sun.misc.Unsafe;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PrincipalName implements Cloneable {
   public static final int KRB_NT_UNKNOWN = 0;
   public static final int KRB_NT_PRINCIPAL = 1;
   public static final int KRB_NT_SRV_INST = 2;
   public static final int KRB_NT_SRV_HST = 3;
   public static final int KRB_NT_SRV_XHST = 4;
   public static final int KRB_NT_UID = 5;
   public static final String TGS_DEFAULT_SRV_NAME = "krbtgt";
   public static final int TGS_DEFAULT_NT = 2;
   public static final char NAME_COMPONENT_SEPARATOR = '/';
   public static final char NAME_REALM_SEPARATOR = '@';
   public static final char REALM_COMPONENT_SEPARATOR = '.';
   public static final String NAME_COMPONENT_SEPARATOR_STR = "/";
   public static final String NAME_REALM_SEPARATOR_STR = "@";
   public static final String REALM_COMPONENT_SEPARATOR_STR = ".";
   private final int nameType;
   private final String[] nameStrings;
   private final Realm nameRealm;
   private final boolean realmDeduced;
   private transient String salt;
   private static final long NAME_STRINGS_OFFSET;
   private static final Unsafe UNSAFE;

   public PrincipalName(int var1, String[] var2, Realm var3) {
      this.salt = null;
      if (var3 == null) {
         throw new IllegalArgumentException("Null realm not allowed");
      } else {
         validateNameStrings(var2);
         this.nameType = var1;
         this.nameStrings = (String[])var2.clone();
         this.nameRealm = var3;
         this.realmDeduced = false;
      }
   }

   public PrincipalName(String[] var1, String var2) throws RealmException {
      this(0, var1, new Realm(var2));
   }

   private static void validateNameStrings(String[] var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Null nameStrings not allowed");
      } else if (var0.length == 0) {
         throw new IllegalArgumentException("Empty nameStrings not allowed");
      } else {
         String[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1[var3];
            if (var4 == null) {
               throw new IllegalArgumentException("Null nameString not allowed");
            }

            if (var4.isEmpty()) {
               throw new IllegalArgumentException("Empty nameString not allowed");
            }
         }

      }
   }

   public Object clone() {
      try {
         PrincipalName var1 = (PrincipalName)super.clone();
         UNSAFE.putObject(this, NAME_STRINGS_OFFSET, this.nameStrings.clone());
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError("Should never happen");
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PrincipalName)) {
         return false;
      } else {
         PrincipalName var2 = (PrincipalName)var1;
         return this.nameRealm.equals(var2.nameRealm) && Arrays.equals((Object[])this.nameStrings, (Object[])var2.nameStrings);
      }
   }

   public PrincipalName(DerValue var1, Realm var2) throws Asn1Exception, IOException {
      this.salt = null;
      if (var2 == null) {
         throw new IllegalArgumentException("Null realm not allowed");
      } else {
         this.realmDeduced = false;
         this.nameRealm = var2;
         if (var1 == null) {
            throw new IllegalArgumentException("Null encoding not allowed");
         } else if (var1.getTag() != 48) {
            throw new Asn1Exception(906);
         } else {
            DerValue var3 = var1.getData().getDerValue();
            if ((var3.getTag() & 31) != 0) {
               throw new Asn1Exception(906);
            } else {
               BigInteger var4 = var3.getData().getBigInteger();
               this.nameType = var4.intValue();
               var3 = var1.getData().getDerValue();
               if ((var3.getTag() & 31) != 1) {
                  throw new Asn1Exception(906);
               } else {
                  DerValue var8 = var3.getData().getDerValue();
                  if (var8.getTag() != 48) {
                     throw new Asn1Exception(906);
                  } else {
                     Vector var5 = new Vector();

                     while(var8.getData().available() > 0) {
                        DerValue var6 = var8.getData().getDerValue();
                        String var7 = (new KerberosString(var6)).toString();
                        var5.addElement(var7);
                     }

                     this.nameStrings = new String[var5.size()];
                     var5.copyInto(this.nameStrings);
                     validateNameStrings(this.nameStrings);
                  }
               }
            }
         }
      }
   }

   public static PrincipalName parse(DerInputStream var0, byte var1, boolean var2, Realm var3) throws Asn1Exception, IOException, RealmException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var4 = var0.getDerValue();
         if (var1 != (var4.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var5 = var4.getData().getDerValue();
            if (var3 == null) {
               var3 = Realm.getDefault();
            }

            return new PrincipalName(var5, var3);
         }
      }
   }

   private static String[] parseName(String var0) {
      Vector var1 = new Vector();
      String var2 = var0;
      int var3 = 0;
      int var4 = 0;

      String var5;
      while(var3 < var2.length()) {
         if (var2.charAt(var3) == '/') {
            if (var3 > 0 && var2.charAt(var3 - 1) == '\\') {
               var2 = var2.substring(0, var3 - 1) + var2.substring(var3, var2.length());
               continue;
            }

            if (var4 <= var3) {
               var5 = var2.substring(var4, var3);
               var1.addElement(var5);
            }

            var4 = var3 + 1;
         } else if (var2.charAt(var3) == '@') {
            if (var3 > 0 && var2.charAt(var3 - 1) == '\\') {
               var2 = var2.substring(0, var3 - 1) + var2.substring(var3, var2.length());
               continue;
            }

            if (var4 < var3) {
               var5 = var2.substring(var4, var3);
               var1.addElement(var5);
            }

            var4 = var3 + 1;
            break;
         }

         ++var3;
      }

      if (var3 == var2.length()) {
         var5 = var2.substring(var4, var3);
         var1.addElement(var5);
      }

      String[] var6 = new String[var1.size()];
      var1.copyInto(var6);
      return var6;
   }

   public PrincipalName(String var1, int var2, String var3) throws RealmException {
      this.salt = null;
      if (var1 == null) {
         throw new IllegalArgumentException("Null name not allowed");
      } else {
         String[] var4 = parseName(var1);
         validateNameStrings(var4);
         if (var3 == null) {
            var3 = Realm.parseRealmAtSeparator(var1);
         }

         this.realmDeduced = var3 == null;
         switch(var2) {
         case 0:
         case 1:
         case 2:
         case 4:
         case 5:
            this.nameStrings = var4;
            this.nameType = var2;
            if (var3 != null) {
               this.nameRealm = new Realm(var3);
            } else {
               this.nameRealm = Realm.getDefault();
            }
            break;
         case 3:
            String var5;
            if (var4.length >= 2) {
               var5 = var4[1];

               try {
                  String var6 = InetAddress.getByName(var5).getCanonicalHostName();
                  if (var6.toLowerCase(Locale.ENGLISH).startsWith(var5.toLowerCase(Locale.ENGLISH) + ".")) {
                     var5 = var6;
                  }
               } catch (SecurityException | UnknownHostException var7) {
               }

               if (var5.endsWith(".")) {
                  var5 = var5.substring(0, var5.length() - 1);
               }

               var4[1] = var5.toLowerCase(Locale.ENGLISH);
            }

            this.nameStrings = var4;
            this.nameType = var2;
            if (var3 != null) {
               this.nameRealm = new Realm(var3);
            } else {
               var5 = mapHostToRealm(var4[1]);
               if (var5 != null) {
                  this.nameRealm = new Realm(var5);
               } else {
                  this.nameRealm = Realm.getDefault();
               }
            }
            break;
         default:
            throw new IllegalArgumentException("Illegal name type");
         }

      }
   }

   public PrincipalName(String var1, int var2) throws RealmException {
      this(var1, var2, (String)null);
   }

   public PrincipalName(String var1) throws RealmException {
      this(var1, 0);
   }

   public PrincipalName(String var1, String var2) throws RealmException {
      this(var1, 0, var2);
   }

   public static PrincipalName tgsService(String var0, String var1) throws KrbException {
      return new PrincipalName(2, new String[]{"krbtgt", var0}, new Realm(var1));
   }

   public String getRealmAsString() {
      return this.getRealmString();
   }

   public String getPrincipalNameAsString() {
      StringBuffer var1 = new StringBuffer(this.nameStrings[0]);

      for(int var2 = 1; var2 < this.nameStrings.length; ++var2) {
         var1.append(this.nameStrings[var2]);
      }

      return var1.toString();
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   public String getName() {
      return this.toString();
   }

   public int getNameType() {
      return this.nameType;
   }

   public String[] getNameStrings() {
      return (String[])this.nameStrings.clone();
   }

   public byte[][] toByteArray() {
      byte[][] var1 = new byte[this.nameStrings.length][];

      for(int var2 = 0; var2 < this.nameStrings.length; ++var2) {
         var1[var2] = new byte[this.nameStrings[var2].length()];
         var1[var2] = this.nameStrings[var2].getBytes();
      }

      return var1;
   }

   public String getRealmString() {
      return this.nameRealm.toString();
   }

   public Realm getRealm() {
      return this.nameRealm;
   }

   public String getSalt() {
      if (this.salt != null) {
         return this.salt;
      } else {
         StringBuffer var1 = new StringBuffer();
         var1.append(this.nameRealm.toString());

         for(int var2 = 0; var2 < this.nameStrings.length; ++var2) {
            var1.append(this.nameStrings[var2]);
         }

         return var1.toString();
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < this.nameStrings.length; ++var2) {
         if (var2 > 0) {
            var1.append("/");
         }

         var1.append(this.nameStrings[var2]);
      }

      var1.append("@");
      var1.append(this.nameRealm.toString());
      return var1.toString();
   }

   public String getNameString() {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < this.nameStrings.length; ++var2) {
         if (var2 > 0) {
            var1.append("/");
         }

         var1.append(this.nameStrings[var2]);
      }

      return var1.toString();
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      BigInteger var3 = BigInteger.valueOf((long)this.nameType);
      var2.putInteger(var3);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      DerValue[] var4 = new DerValue[this.nameStrings.length];

      for(int var5 = 0; var5 < this.nameStrings.length; ++var5) {
         var4[var5] = (new KerberosString(this.nameStrings[var5])).toDerValue();
      }

      var2.putSequence(var4);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public boolean match(PrincipalName var1) {
      boolean var2 = true;
      if (this.nameRealm != null && var1.nameRealm != null && !this.nameRealm.toString().equalsIgnoreCase(var1.nameRealm.toString())) {
         var2 = false;
      }

      if (this.nameStrings.length != var1.nameStrings.length) {
         var2 = false;
      } else {
         for(int var3 = 0; var3 < this.nameStrings.length; ++var3) {
            if (!this.nameStrings[var3].equalsIgnoreCase(var1.nameStrings[var3])) {
               var2 = false;
            }
         }
      }

      return var2;
   }

   public void writePrincipal(CCacheOutputStream var1) throws IOException {
      var1.write32(this.nameType);
      var1.write32(this.nameStrings.length);
      Object var2 = null;
      byte[] var5 = this.nameRealm.toString().getBytes();
      var1.write32(var5.length);
      var1.write(var5, 0, var5.length);
      Object var3 = null;

      for(int var4 = 0; var4 < this.nameStrings.length; ++var4) {
         byte[] var6 = this.nameStrings[var4].getBytes();
         var1.write32(var6.length);
         var1.write(var6, 0, var6.length);
      }

   }

   public String getInstanceComponent() {
      return this.nameStrings != null && this.nameStrings.length >= 2 ? new String(this.nameStrings[1]) : null;
   }

   static String mapHostToRealm(String var0) {
      String var1 = null;

      try {
         String var2 = null;
         Config var3 = Config.getInstance();
         if ((var1 = var3.get("domain_realm", var0)) != null) {
            return var1;
         }

         for(int var4 = 1; var4 < var0.length(); ++var4) {
            if (var0.charAt(var4) == '.' && var4 != var0.length() - 1) {
               var2 = var0.substring(var4);
               var1 = var3.get("domain_realm", var2);
               if (var1 != null) {
                  break;
               }

               var2 = var0.substring(var4 + 1);
               var1 = var3.get("domain_realm", var2);
               if (var1 != null) {
                  break;
               }
            }
         }
      } catch (KrbException var5) {
      }

      return var1;
   }

   public boolean isRealmDeduced() {
      return this.realmDeduced;
   }

   static {
      try {
         Unsafe var0 = Unsafe.getUnsafe();
         NAME_STRINGS_OFFSET = var0.objectFieldOffset(PrincipalName.class.getDeclaredField("nameStrings"));
         UNSAFE = var0;
      } catch (ReflectiveOperationException var1) {
         throw new Error(var1);
      }
   }
}
