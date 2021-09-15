package com.sun.corba.se.impl.util;

import com.sun.corba.se.impl.io.ObjectStreamClass;
import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Remote;
import java.util.Hashtable;
import javax.rmi.CORBA.ClassDesc;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ValueBase;

public class RepositoryId {
   private static final byte[] IDL_IDENTIFIER_CHARS = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1};
   private static final long serialVersionUID = 123456789L;
   private static String defaultServerURL = null;
   private static boolean useCodebaseOnly = false;
   private static IdentityHashtable classToRepStr;
   private static IdentityHashtable classIDLToRepStr;
   private static IdentityHashtable classSeqToRepStr;
   private static final IdentityHashtable repStrToByteArray;
   private static Hashtable repStrToClass;
   private String repId = null;
   private boolean isSupportedFormat = true;
   private String typeString = null;
   private String versionString = null;
   private boolean isSequence = false;
   private boolean isRMIValueType = false;
   private boolean isIDLType = false;
   private String completeClassName = null;
   private String unqualifiedName = null;
   private String definedInId = null;
   private Class clazz = null;
   private String suid = null;
   private String actualSuid = null;
   private long suidLong = -1L;
   private long actualSuidLong = -1L;
   private static final String kSequenceKeyword = "seq";
   private static final String kValuePrefix = "RMI:";
   private static final String kIDLPrefix = "IDL:";
   private static final String kIDLNamePrefix = "omg.org/";
   private static final String kIDLClassnamePrefix = "org.omg.";
   private static final String kSequencePrefix = "[";
   private static final String kCORBAPrefix = "CORBA/";
   private static final String kArrayPrefix = "RMI:[CORBA/";
   private static final int kValuePrefixLength;
   private static final int kIDLPrefixLength;
   private static final int kSequencePrefixLength;
   private static final String kInterfaceHashCode = ":0000000000000000";
   private static final String kInterfaceOnlyHashStr = "0000000000000000";
   private static final String kExternalizableHashStr = "0000000000000001";
   public static final int kInitialValueTag = 2147483392;
   public static final int kNoTypeInfo = 0;
   public static final int kSingleRepTypeInfo = 2;
   public static final int kPartialListTypeInfo = 6;
   public static final int kChunkedMask = 8;
   public static final int kPreComputed_StandardRMIUnchunked;
   public static final int kPreComputed_CodeBaseRMIUnchunked;
   public static final int kPreComputed_StandardRMIChunked;
   public static final int kPreComputed_CodeBaseRMIChunked;
   public static final int kPreComputed_StandardRMIUnchunked_NoRep;
   public static final int kPreComputed_CodeBaseRMIUnchunked_NoRep;
   public static final int kPreComputed_StandardRMIChunked_NoRep;
   public static final int kPreComputed_CodeBaseRMIChunked_NoRep;
   public static final String kWStringValueVersion = "1.0";
   public static final String kWStringValueHash = ":1.0";
   public static final String kWStringStubValue = "WStringValue";
   public static final String kWStringTypeStr = "omg.org/CORBA/WStringValue";
   public static final String kWStringValueRepID = "IDL:omg.org/CORBA/WStringValue:1.0";
   public static final String kAnyRepID = "IDL:omg.org/CORBA/Any";
   public static final String kClassDescValueHash;
   public static final String kClassDescStubValue = "ClassDesc";
   public static final String kClassDescTypeStr = "javax.rmi.CORBA.ClassDesc";
   public static final String kClassDescValueRepID;
   public static final String kObjectValueHash = ":1.0";
   public static final String kObjectStubValue = "Object";
   public static final String kSequenceValueHash = ":1.0";
   public static final String kPrimitiveSequenceValueHash = ":0000000000000000";
   public static final String kSerializableValueHash = ":1.0";
   public static final String kSerializableStubValue = "Serializable";
   public static final String kExternalizableValueHash = ":1.0";
   public static final String kExternalizableStubValue = "Externalizable";
   public static final String kRemoteValueHash = "";
   public static final String kRemoteStubValue = "";
   public static final String kRemoteTypeStr = "";
   public static final String kRemoteValueRepID = "";
   private static final Hashtable kSpecialArrayTypeStrings;
   private static final Hashtable kSpecialCasesRepIDs;
   private static final Hashtable kSpecialCasesStubValues;
   private static final Hashtable kSpecialCasesVersions;
   private static final Hashtable kSpecialCasesClasses;
   private static final Hashtable kSpecialCasesArrayPrefix;
   private static final Hashtable kSpecialPrimitives;
   private static final byte[] ASCII_HEX;
   public static final RepositoryIdCache cache;
   public static final String kjava_rmi_Remote;
   public static final String korg_omg_CORBA_Object;
   public static final Class[] kNoParamTypes;
   public static final Object[] kNoArgs;

   RepositoryId() {
   }

   RepositoryId(String var1) {
      this.init(var1);
   }

   RepositoryId init(String var1) {
      this.repId = var1;
      if (var1.length() == 0) {
         this.clazz = Remote.class;
         this.typeString = "";
         this.isRMIValueType = true;
         this.suid = "0000000000000000";
         return this;
      } else if (var1.equals("IDL:omg.org/CORBA/WStringValue:1.0")) {
         this.clazz = String.class;
         this.typeString = "omg.org/CORBA/WStringValue";
         this.isIDLType = true;
         this.completeClassName = "java.lang.String";
         this.versionString = "1.0";
         return this;
      } else {
         String var2 = convertFromISOLatin1(var1);
         int var3 = var2.indexOf(58);
         if (var3 == -1) {
            throw new IllegalArgumentException("RepsitoryId must have the form <type>:<body>");
         } else {
            int var4 = var2.indexOf(58, var3 + 1);
            if (var4 == -1) {
               this.versionString = "";
            } else {
               this.versionString = var2.substring(var4);
            }

            if (var2.startsWith("IDL:")) {
               this.typeString = var2.substring(kIDLPrefixLength, var2.indexOf(58, kIDLPrefixLength));
               this.isIDLType = true;
               if (this.typeString.startsWith("omg.org/")) {
                  this.completeClassName = "org.omg." + this.typeString.substring("omg.org/".length()).replace('/', '.');
               } else {
                  this.completeClassName = this.typeString.replace('/', '.');
               }
            } else if (var2.startsWith("RMI:")) {
               this.typeString = var2.substring(kValuePrefixLength, var2.indexOf(58, kValuePrefixLength));
               this.isRMIValueType = true;
               if (this.versionString.indexOf(46) == -1) {
                  this.actualSuid = this.versionString.substring(1);
                  this.suid = this.actualSuid;
                  if (this.actualSuid.indexOf(58) != -1) {
                     int var5 = this.actualSuid.indexOf(58) + 1;
                     this.suid = this.actualSuid.substring(var5);
                     this.actualSuid = this.actualSuid.substring(0, var5 - 1);
                  }
               }
            } else {
               this.isSupportedFormat = false;
               this.typeString = "";
            }

            if (this.typeString.startsWith("[")) {
               this.isSequence = true;
            }

            return this;
         }
      }
   }

   public final String getUnqualifiedName() {
      if (this.unqualifiedName == null) {
         String var1 = this.getClassName();
         int var2 = var1.lastIndexOf(46);
         if (var2 == -1) {
            this.unqualifiedName = var1;
            this.definedInId = "IDL::1.0";
         } else {
            this.unqualifiedName = var1.substring(var2);
            this.definedInId = "IDL:" + var1.substring(0, var2).replace('.', '/') + ":1.0";
         }
      }

      return this.unqualifiedName;
   }

   public final String getDefinedInId() {
      if (this.definedInId == null) {
         this.getUnqualifiedName();
      }

      return this.definedInId;
   }

   public final String getTypeString() {
      return this.typeString;
   }

   public final String getVersionString() {
      return this.versionString;
   }

   public final String getSerialVersionUID() {
      return this.suid;
   }

   public final String getActualSerialVersionUID() {
      return this.actualSuid;
   }

   public final long getSerialVersionUIDAsLong() {
      return this.suidLong;
   }

   public final long getActualSerialVersionUIDAsLong() {
      return this.actualSuidLong;
   }

   public final boolean isRMIValueType() {
      return this.isRMIValueType;
   }

   public final boolean isIDLType() {
      return this.isIDLType;
   }

   public final String getRepositoryId() {
      return this.repId;
   }

   public static byte[] getByteArray(String var0) {
      synchronized(repStrToByteArray) {
         return (byte[])((byte[])repStrToByteArray.get(var0));
      }
   }

   public static void setByteArray(String var0, byte[] var1) {
      synchronized(repStrToByteArray) {
         repStrToByteArray.put(var0, var1);
      }
   }

   public final boolean isSequence() {
      return this.isSequence;
   }

   public final boolean isSupportedFormat() {
      return this.isSupportedFormat;
   }

   public final String getClassName() {
      if (this.isRMIValueType) {
         return this.typeString;
      } else {
         return this.isIDLType ? this.completeClassName : null;
      }
   }

   public final Class getAnyClassFromType() throws ClassNotFoundException {
      try {
         return this.getClassFromType();
      } catch (ClassNotFoundException var3) {
         Class var2 = (Class)repStrToClass.get(this.repId);
         if (var2 != null) {
            return var2;
         } else {
            throw var3;
         }
      }
   }

   public final Class getClassFromType() throws ClassNotFoundException {
      if (this.clazz != null) {
         return this.clazz;
      } else {
         Class var1 = (Class)kSpecialCasesClasses.get(this.getClassName());
         if (var1 != null) {
            this.clazz = var1;
            return var1;
         } else {
            try {
               return Util.loadClass(this.getClassName(), (String)null, (ClassLoader)null);
            } catch (ClassNotFoundException var5) {
               if (defaultServerURL != null) {
                  try {
                     return this.getClassFromType(defaultServerURL);
                  } catch (MalformedURLException var4) {
                     throw var5;
                  }
               } else {
                  throw var5;
               }
            }
         }
      }
   }

   public final Class getClassFromType(Class var1, String var2) throws ClassNotFoundException {
      if (this.clazz != null) {
         return this.clazz;
      } else {
         Class var3 = (Class)kSpecialCasesClasses.get(this.getClassName());
         if (var3 != null) {
            this.clazz = var3;
            return var3;
         } else {
            ClassLoader var4 = var1 == null ? null : var1.getClassLoader();
            return Utility.loadClassOfType(this.getClassName(), var2, var4, var1, var4);
         }
      }
   }

   public final Class getClassFromType(String var1) throws ClassNotFoundException, MalformedURLException {
      return Util.loadClass(this.getClassName(), var1, (ClassLoader)null);
   }

   public final String toString() {
      return this.repId;
   }

   public static boolean useFullValueDescription(Class var0, String var1) throws IOException {
      String var2 = createForAnyType(var0);
      if (var2.equals(var1)) {
         return false;
      } else {
         RepositoryId var3;
         RepositoryId var4;
         synchronized(cache) {
            var3 = cache.getId(var1);
            var4 = cache.getId(var2);
         }

         if (var3.isRMIValueType() && var4.isRMIValueType()) {
            if (!var3.getSerialVersionUID().equals(var4.getSerialVersionUID())) {
               String var5 = "Mismatched serialization UIDs : Source (Rep. ID" + var4 + ") = " + var4.getSerialVersionUID() + " whereas Target (Rep. ID " + var1 + ") = " + var3.getSerialVersionUID();
               throw new IOException(var5);
            } else {
               return true;
            }
         } else {
            throw new IOException("The repository ID is not of an RMI value type (Expected ID = " + var2 + "; Received ID = " + var1 + ")");
         }
      }
   }

   private static String createHashString(Serializable var0) {
      return createHashString(var0.getClass());
   }

   private static String createHashString(Class var0) {
      if (!var0.isInterface() && Serializable.class.isAssignableFrom(var0)) {
         long var1 = ObjectStreamClass.getActualSerialVersionUID(var0);
         String var3 = null;
         if (var1 == 0L) {
            var3 = "0000000000000000";
         } else if (var1 == 1L) {
            var3 = "0000000000000001";
         } else {
            var3 = Long.toHexString(var1).toUpperCase();
         }

         while(var3.length() < 16) {
            var3 = "0" + var3;
         }

         long var4 = ObjectStreamClass.getSerialVersionUID(var0);
         String var6 = null;
         if (var4 == 0L) {
            var6 = "0000000000000000";
         } else if (var4 == 1L) {
            var6 = "0000000000000001";
         } else {
            var6 = Long.toHexString(var4).toUpperCase();
         }

         while(var6.length() < 16) {
            var6 = "0" + var6;
         }

         var3 = var3 + ":" + var6;
         return ":" + var3;
      } else {
         return ":0000000000000000";
      }
   }

   public static String createSequenceRepID(Object var0) {
      return createSequenceRepID(var0.getClass());
   }

   public static String createSequenceRepID(Class var0) {
      synchronized(classSeqToRepStr) {
         String var2 = (String)classSeqToRepStr.get(var0);
         if (var2 != null) {
            return var2;
         } else {
            Class var3 = var0;
            Class var4 = null;

            int var5;
            for(var5 = 0; (var4 = var0.getComponentType()) != null; var0 = var4) {
               ++var5;
            }

            if (var0.isPrimitive()) {
               var2 = "RMI:" + var3.getName() + ":0000000000000000";
            } else {
               StringBuffer var6 = new StringBuffer();
               var6.append("RMI:");

               while(var5-- > 0) {
                  var6.append("[");
               }

               var6.append("L");
               var6.append(convertToISOLatin1(var0.getName()));
               var6.append(";");
               var6.append(createHashString(var0));
               var2 = var6.toString();
            }

            classSeqToRepStr.put(var3, var2);
            return var2;
         }
      }
   }

   public static String createForSpecialCase(Class var0) {
      return var0.isArray() ? createSequenceRepID(var0) : (String)kSpecialCasesRepIDs.get(var0);
   }

   public static String createForSpecialCase(Serializable var0) {
      Class var1 = var0.getClass();
      return var1.isArray() ? createSequenceRepID((Object)var0) : createForSpecialCase(var1);
   }

   public static String createForJavaType(Serializable var0) throws TypeMismatchException {
      synchronized(classToRepStr) {
         String var2 = createForSpecialCase(var0);
         if (var2 != null) {
            return var2;
         } else {
            Class var3 = var0.getClass();
            var2 = (String)classToRepStr.get(var3);
            if (var2 != null) {
               return var2;
            } else {
               var2 = "RMI:" + convertToISOLatin1(var3.getName()) + createHashString(var3);
               classToRepStr.put(var3, var2);
               repStrToClass.put(var2, var3);
               return var2;
            }
         }
      }
   }

   public static String createForJavaType(Class var0) throws TypeMismatchException {
      synchronized(classToRepStr) {
         String var2 = createForSpecialCase(var0);
         if (var2 != null) {
            return var2;
         } else {
            var2 = (String)classToRepStr.get(var0);
            if (var2 != null) {
               return var2;
            } else {
               var2 = "RMI:" + convertToISOLatin1(var0.getName()) + createHashString(var0);
               classToRepStr.put(var0, var2);
               repStrToClass.put(var2, var0);
               return var2;
            }
         }
      }
   }

   public static String createForIDLType(Class var0, int var1, int var2) throws TypeMismatchException {
      synchronized(classIDLToRepStr) {
         String var4 = (String)classIDLToRepStr.get(var0);
         if (var4 != null) {
            return var4;
         } else {
            var4 = "IDL:" + convertToISOLatin1(var0.getName()).replace('.', '/') + ":" + var1 + "." + var2;
            classIDLToRepStr.put(var0, var4);
            return var4;
         }
      }
   }

   private static String getIdFromHelper(Class var0) {
      try {
         Class var1 = Utility.loadClassForClass(var0.getName() + "Helper", (String)null, var0.getClassLoader(), var0, var0.getClassLoader());
         Method var2 = var1.getDeclaredMethod("id", kNoParamTypes);
         return (String)var2.invoke((Object)null, kNoArgs);
      } catch (ClassNotFoundException var3) {
         throw new MARSHAL(var3.toString());
      } catch (NoSuchMethodException var4) {
         throw new MARSHAL(var4.toString());
      } catch (InvocationTargetException var5) {
         throw new MARSHAL(var5.toString());
      } catch (IllegalAccessException var6) {
         throw new MARSHAL(var6.toString());
      }
   }

   public static String createForAnyType(Class var0) {
      try {
         if (var0.isArray()) {
            return createSequenceRepID(var0);
         } else if (IDLEntity.class.isAssignableFrom(var0)) {
            try {
               return getIdFromHelper(var0);
            } catch (Throwable var2) {
               return createForIDLType(var0, 1, 0);
            }
         } else {
            return createForJavaType(var0);
         }
      } catch (TypeMismatchException var3) {
         return null;
      }
   }

   public static boolean isAbstractBase(Class var0) {
      return var0.isInterface() && IDLEntity.class.isAssignableFrom(var0) && !ValueBase.class.isAssignableFrom(var0) && !org.omg.CORBA.Object.class.isAssignableFrom(var0);
   }

   public static boolean isAnyRequired(Class var0) {
      return var0 == Object.class || var0 == Serializable.class || var0 == Externalizable.class;
   }

   public static long fromHex(String var0) {
      return var0.startsWith("0x") ? Long.valueOf(var0.substring(2), 16) : Long.valueOf(var0, 16);
   }

   public static String convertToISOLatin1(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return var0;
      } else {
         StringBuffer var2 = null;

         for(int var3 = 0; var3 < var1; ++var3) {
            char var4 = var0.charAt(var3);
            if (var4 <= 255 && IDL_IDENTIFIER_CHARS[var4] != 0) {
               if (var2 != null) {
                  var2.append(var4);
               }
            } else {
               if (var2 == null) {
                  var2 = new StringBuffer(var0.substring(0, var3));
               }

               var2.append("\\U" + (char)ASCII_HEX[(var4 & '\uf000') >>> 12] + (char)ASCII_HEX[(var4 & 3840) >>> 8] + (char)ASCII_HEX[(var4 & 240) >>> 4] + (char)ASCII_HEX[var4 & 15]);
            }
         }

         if (var2 != null) {
            var0 = var2.toString();
         }

         return var0;
      }
   }

   private static String convertFromISOLatin1(String var0) {
      boolean var1 = true;
      StringBuffer var2 = new StringBuffer(var0);

      int var7;
      while((var7 = var2.toString().indexOf("\\U")) != -1) {
         String var3 = "0000" + var2.toString().substring(var7 + 2, var7 + 6);
         byte[] var4 = new byte[(var3.length() - 4) / 2];
         int var5 = 4;

         for(int var6 = 0; var5 < var3.length(); ++var6) {
            var4[var6] = (byte)(Utility.hexOf(var3.charAt(var5)) << 4 & 240);
            var4[var6] |= (byte)(Utility.hexOf(var3.charAt(var5 + 1)) << 0 & 15);
            var5 += 2;
         }

         var2 = new StringBuffer(delete(var2.toString(), var7, var7 + 6));
         var2.insert(var7, (char)var4[1]);
      }

      return var2.toString();
   }

   private static String delete(String var0, int var1, int var2) {
      return var0.substring(0, var1) + var0.substring(var2, var0.length());
   }

   private static String replace(String var0, String var1, String var2) {
      boolean var3 = false;

      for(int var6 = var0.indexOf(var1); var6 != -1; var6 = var0.indexOf(var1)) {
         String var4 = var0.substring(0, var6);
         String var5 = var0.substring(var6 + var1.length());
         var0 = new String(var4 + var2 + var5);
      }

      return var0;
   }

   public static int computeValueTag(boolean var0, int var1, boolean var2) {
      int var3 = 2147483392;
      if (var0) {
         var3 |= 1;
      }

      var3 |= var1;
      if (var2) {
         var3 |= 8;
      }

      return var3;
   }

   public static boolean isCodeBasePresent(int var0) {
      return (var0 & 1) == 1;
   }

   public static int getTypeInfo(int var0) {
      return var0 & 6;
   }

   public static boolean isChunkedEncoding(int var0) {
      return (var0 & 8) != 0;
   }

   public static String getServerURL() {
      return defaultServerURL;
   }

   static {
      if (defaultServerURL == null) {
         defaultServerURL = JDKBridge.getLocalCodebase();
      }

      useCodebaseOnly = JDKBridge.useCodebaseOnly();
      classToRepStr = new IdentityHashtable();
      classIDLToRepStr = new IdentityHashtable();
      classSeqToRepStr = new IdentityHashtable();
      repStrToByteArray = new IdentityHashtable();
      repStrToClass = new Hashtable();
      kValuePrefixLength = "RMI:".length();
      kIDLPrefixLength = "IDL:".length();
      kSequencePrefixLength = "[".length();
      kPreComputed_StandardRMIUnchunked = computeValueTag(false, 2, false);
      kPreComputed_CodeBaseRMIUnchunked = computeValueTag(true, 2, false);
      kPreComputed_StandardRMIChunked = computeValueTag(false, 2, true);
      kPreComputed_CodeBaseRMIChunked = computeValueTag(true, 2, true);
      kPreComputed_StandardRMIUnchunked_NoRep = computeValueTag(false, 0, false);
      kPreComputed_CodeBaseRMIUnchunked_NoRep = computeValueTag(true, 0, false);
      kPreComputed_StandardRMIChunked_NoRep = computeValueTag(false, 0, true);
      kPreComputed_CodeBaseRMIChunked_NoRep = computeValueTag(true, 0, true);
      kClassDescValueHash = ":" + Long.toHexString(ObjectStreamClass.getActualSerialVersionUID(ClassDesc.class)).toUpperCase() + ":" + Long.toHexString(ObjectStreamClass.getSerialVersionUID(ClassDesc.class)).toUpperCase();
      kClassDescValueRepID = "RMI:javax.rmi.CORBA.ClassDesc" + kClassDescValueHash;
      kSpecialArrayTypeStrings = new Hashtable();
      kSpecialArrayTypeStrings.put("CORBA.WStringValue", new StringBuffer(String.class.getName()));
      kSpecialArrayTypeStrings.put("javax.rmi.CORBA.ClassDesc", new StringBuffer(Class.class.getName()));
      kSpecialArrayTypeStrings.put("CORBA.Object", new StringBuffer(Remote.class.getName()));
      kSpecialCasesRepIDs = new Hashtable();
      kSpecialCasesRepIDs.put(String.class, "IDL:omg.org/CORBA/WStringValue:1.0");
      kSpecialCasesRepIDs.put(Class.class, kClassDescValueRepID);
      kSpecialCasesRepIDs.put(Remote.class, "");
      kSpecialCasesStubValues = new Hashtable();
      kSpecialCasesStubValues.put(String.class, "WStringValue");
      kSpecialCasesStubValues.put(Class.class, "ClassDesc");
      kSpecialCasesStubValues.put(Object.class, "Object");
      kSpecialCasesStubValues.put(Serializable.class, "Serializable");
      kSpecialCasesStubValues.put(Externalizable.class, "Externalizable");
      kSpecialCasesStubValues.put(Remote.class, "");
      kSpecialCasesVersions = new Hashtable();
      kSpecialCasesVersions.put(String.class, ":1.0");
      kSpecialCasesVersions.put(Class.class, kClassDescValueHash);
      kSpecialCasesVersions.put(Object.class, ":1.0");
      kSpecialCasesVersions.put(Serializable.class, ":1.0");
      kSpecialCasesVersions.put(Externalizable.class, ":1.0");
      kSpecialCasesVersions.put(Remote.class, "");
      kSpecialCasesClasses = new Hashtable();
      kSpecialCasesClasses.put("omg.org/CORBA/WStringValue", String.class);
      kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
      kSpecialCasesClasses.put("", Remote.class);
      kSpecialCasesClasses.put("org.omg.CORBA.WStringValue", String.class);
      kSpecialCasesClasses.put("javax.rmi.CORBA.ClassDesc", Class.class);
      kSpecialCasesArrayPrefix = new Hashtable();
      kSpecialCasesArrayPrefix.put(String.class, "RMI:[CORBA/");
      kSpecialCasesArrayPrefix.put(Class.class, "RMI:[javax/rmi/CORBA/");
      kSpecialCasesArrayPrefix.put(Object.class, "RMI:[java/lang/");
      kSpecialCasesArrayPrefix.put(Serializable.class, "RMI:[java/io/");
      kSpecialCasesArrayPrefix.put(Externalizable.class, "RMI:[java/io/");
      kSpecialCasesArrayPrefix.put(Remote.class, "RMI:[CORBA/");
      kSpecialPrimitives = new Hashtable();
      kSpecialPrimitives.put("int", "long");
      kSpecialPrimitives.put("long", "longlong");
      kSpecialPrimitives.put("byte", "octet");
      ASCII_HEX = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
      cache = new RepositoryIdCache();
      kjava_rmi_Remote = createForAnyType(Remote.class);
      korg_omg_CORBA_Object = createForAnyType(org.omg.CORBA.Object.class);
      kNoParamTypes = new Class[0];
      kNoArgs = new Object[0];
   }
}
