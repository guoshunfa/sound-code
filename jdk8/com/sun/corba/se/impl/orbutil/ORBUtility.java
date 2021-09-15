package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.SharedSecrets;

public final class ORBUtility {
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("util");
   private static OMGSystemException omgWrapper = OMGSystemException.get("util");
   private static StructMember[] members = null;
   private static final Hashtable exceptionClassNames = new Hashtable();
   private static final Hashtable exceptionRepositoryIds = new Hashtable();

   private ORBUtility() {
   }

   private static StructMember[] systemExceptionMembers(ORB var0) {
      if (members == null) {
         members = new StructMember[3];
         members[0] = new StructMember("id", var0.create_string_tc(0), (IDLType)null);
         members[1] = new StructMember("minor", var0.get_primitive_tc(TCKind.tk_long), (IDLType)null);
         members[2] = new StructMember("completed", var0.get_primitive_tc(TCKind.tk_long), (IDLType)null);
      }

      return members;
   }

   private static TypeCode getSystemExceptionTypeCode(ORB var0, String var1, String var2) {
      Class var3 = TypeCode.class;
      synchronized(TypeCode.class) {
         return var0.create_exception_tc(var1, var2, systemExceptionMembers(var0));
      }
   }

   private static boolean isSystemExceptionTypeCode(TypeCode var0, ORB var1) {
      StructMember[] var2 = systemExceptionMembers(var1);

      try {
         return var0.kind().value() == 22 && var0.member_count() == 3 && var0.member_type(0).equal(var2[0].type) && var0.member_type(1).equal(var2[1].type) && var0.member_type(2).equal(var2[2].type);
      } catch (BadKind var4) {
         return false;
      } catch (Bounds var5) {
         return false;
      }
   }

   public static void insertSystemException(SystemException var0, Any var1) {
      OutputStream var2 = var1.create_output_stream();
      ORB var3 = (ORB)((ORB)var2.orb());
      String var4 = var0.getClass().getName();
      String var5 = repositoryIdOf(var4);
      var2.write_string(var5);
      var2.write_long(var0.minor);
      var2.write_long(var0.completed.value());
      var1.read_value(var2.create_input_stream(), getSystemExceptionTypeCode(var3, var5, var4));
   }

   public static SystemException extractSystemException(Any var0) {
      InputStream var1 = var0.create_input_stream();
      ORB var2 = (ORB)((ORB)var1.orb());
      if (!isSystemExceptionTypeCode(var0.type(), var2)) {
         throw wrapper.unknownDsiSysex(CompletionStatus.COMPLETED_MAYBE);
      } else {
         return readSystemException(var1);
      }
   }

   public static ValueHandler createValueHandler() {
      try {
         ValueHandler var0 = (ValueHandler)AccessController.doPrivileged(new PrivilegedExceptionAction<ValueHandler>() {
            public ValueHandler run() throws Exception {
               return Util.createValueHandler();
            }
         });
         return var0;
      } catch (PrivilegedActionException var2) {
         throw new InternalError(var2.getMessage());
      }
   }

   public static boolean isForeignORB(ORB var0) {
      if (var0 == null) {
         return false;
      } else {
         try {
            return var0.getORBVersion().equals(ORBVersionFactory.getFOREIGN());
         } catch (SecurityException var2) {
            return false;
         }
      }
   }

   public static int bytesToInt(byte[] var0, int var1) {
      int var2 = var0[var1++] << 24 & -16777216;
      int var3 = var0[var1++] << 16 & 16711680;
      int var4 = var0[var1++] << 8 & '\uff00';
      int var5 = var0[var1++] << 0 & 255;
      return var2 | var3 | var4 | var5;
   }

   public static void intToBytes(int var0, byte[] var1, int var2) {
      var1[var2++] = (byte)(var0 >>> 24 & 255);
      var1[var2++] = (byte)(var0 >>> 16 & 255);
      var1[var2++] = (byte)(var0 >>> 8 & 255);
      var1[var2++] = (byte)(var0 >>> 0 & 255);
   }

   public static int hexOf(char var0) {
      int var1 = var0 - 48;
      if (var1 >= 0 && var1 <= 9) {
         return var1;
      } else {
         var1 = var0 - 97 + 10;
         if (var1 >= 10 && var1 <= 15) {
            return var1;
         } else {
            var1 = var0 - 65 + 10;
            if (var1 >= 10 && var1 <= 15) {
               return var1;
            } else {
               throw wrapper.badHexDigit();
            }
         }
      }
   }

   public static void writeSystemException(SystemException var0, OutputStream var1) {
      String var2 = repositoryIdOf(var0.getClass().getName());
      var1.write_string(var2);
      var1.write_long(var0.minor);
      var1.write_long(var0.completed.value());
   }

   public static SystemException readSystemException(InputStream var0) {
      try {
         String var1 = classNameOf(var0.read_string());
         SystemException var2 = (SystemException)SharedSecrets.getJavaCorbaAccess().loadClass(var1).newInstance();
         var2.minor = var0.read_long();
         var2.completed = CompletionStatus.from_int(var0.read_long());
         return var2;
      } catch (Exception var3) {
         throw wrapper.unknownSysex(CompletionStatus.COMPLETED_MAYBE, var3);
      }
   }

   public static String classNameOf(String var0) {
      String var1 = null;
      var1 = (String)exceptionClassNames.get(var0);
      if (var1 == null) {
         var1 = "org.omg.CORBA.UNKNOWN";
      }

      return var1;
   }

   public static boolean isSystemException(String var0) {
      String var1 = null;
      var1 = (String)exceptionClassNames.get(var0);
      return var1 != null;
   }

   public static byte getEncodingVersion(ORB var0, IOR var1) {
      if (var0.getORBData().isJavaSerializationEnabled()) {
         IIOPProfile var2 = var1.getProfile();
         IIOPProfileTemplate var3 = (IIOPProfileTemplate)var2.getTaggedProfileTemplate();
         Iterator var4 = var3.iteratorById(1398099458);
         if (var4.hasNext()) {
            JavaSerializationComponent var5 = (JavaSerializationComponent)var4.next();
            byte var6 = var5.javaSerializationVersion();
            if (var6 >= 1) {
               return 1;
            }

            if (var6 > 0) {
               return var5.javaSerializationVersion();
            }
         }
      }

      return 0;
   }

   public static String repositoryIdOf(String var0) {
      String var1 = (String)exceptionRepositoryIds.get(var0);
      if (var1 == null) {
         var1 = "IDL:omg.org/CORBA/UNKNOWN:1.0";
      }

      return var1;
   }

   public static int[] parseVersion(String var0) {
      if (var0 == null) {
         return new int[0];
      } else {
         char[] var1 = var0.toCharArray();

         int var2;
         for(var2 = 0; var2 < var1.length && (var1[var2] < '0' || var1[var2] > '9'); ++var2) {
            if (var2 == var1.length) {
               return new int[0];
            }
         }

         int var3 = var2 + 1;

         int var4;
         for(var4 = 1; var3 < var1.length; ++var3) {
            if (var1[var3] == '.') {
               ++var4;
            } else if (var1[var3] < '0' || var1[var3] > '9') {
               break;
            }
         }

         int[] var5 = new int[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            int var7 = var0.indexOf(46, var2);
            if (var7 == -1 || var7 > var3) {
               var7 = var3;
            }

            if (var2 >= var7) {
               var5[var6] = 0;
            } else {
               var5[var6] = Integer.parseInt(var0.substring(var2, var7));
            }

            var2 = var7 + 1;
         }

         return var5;
      }
   }

   public static int compareVersion(int[] var0, int[] var1) {
      if (var0 == null) {
         var0 = new int[0];
      }

      if (var1 == null) {
         var1 = new int[0];
      }

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var2 >= var1.length || var0[var2] > var1[var2]) {
            return 1;
         }

         if (var0[var2] < var1[var2]) {
            return -1;
         }
      }

      return var0.length == var1.length ? 0 : -1;
   }

   public static synchronized int compareVersion(String var0, String var1) {
      return compareVersion(parseVersion(var0), parseVersion(var1));
   }

   private static String compressClassName(String var0) {
      String var1 = "com.sun.corba.se.";
      return var0.startsWith(var1) ? "(ORB)." + var0.substring(var1.length()) : var0;
   }

   public static String getThreadName(Thread var0) {
      if (var0 == null) {
         return "null";
      } else {
         String var1 = var0.getName();
         StringTokenizer var2 = new StringTokenizer(var1);
         int var3 = var2.countTokens();
         if (var3 != 5) {
            return var1;
         } else {
            String[] var4 = new String[var3];

            for(int var5 = 0; var5 < var3; ++var5) {
               var4[var5] = var2.nextToken();
            }

            return !var4[0].equals("SelectReaderThread") ? var1 : "SelectReaderThread[" + var4[2] + ":" + var4[3] + "]";
         }
      }
   }

   private static String formatStackTraceElement(StackTraceElement var0) {
      return compressClassName(var0.getClassName()) + "." + var0.getMethodName() + (var0.isNativeMethod() ? "(Native Method)" : (var0.getFileName() != null && var0.getLineNumber() >= 0 ? "(" + var0.getFileName() + ":" + var0.getLineNumber() + ")" : (var0.getFileName() != null ? "(" + var0.getFileName() + ")" : "(Unknown Source)")));
   }

   private static void printStackTrace(StackTraceElement[] var0) {
      System.out.println("    Stack Trace:");

      for(int var1 = 1; var1 < var0.length; ++var1) {
         System.out.print("        >");
         System.out.println(formatStackTraceElement(var0[var1]));
      }

   }

   public static synchronized void dprint(Object var0, String var1) {
      System.out.println(compressClassName(var0.getClass().getName()) + "(" + getThreadName(Thread.currentThread()) + "): " + var1);
   }

   public static synchronized void dprint(String var0, String var1) {
      System.out.println(compressClassName(var0) + "(" + getThreadName(Thread.currentThread()) + "): " + var1);
   }

   public synchronized void dprint(String var1) {
      dprint((Object)this, var1);
   }

   public static synchronized void dprintTrace(Object var0, String var1) {
      dprint(var0, var1);
      Throwable var2 = new Throwable();
      printStackTrace(var2.getStackTrace());
   }

   public static synchronized void dprint(Object var0, String var1, Throwable var2) {
      System.out.println(compressClassName(var0.getClass().getName()) + '(' + Thread.currentThread() + "): " + var1);
      if (var2 != null) {
         printStackTrace(var2.getStackTrace());
      }

   }

   public static String[] concatenateStringArrays(String[] var0, String[] var1) {
      String[] var2 = new String[var0.length + var1.length];

      int var3;
      for(var3 = 0; var3 < var0.length; ++var3) {
         var2[var3] = var0[var3];
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         var2[var3 + var0.length] = var1[var3];
      }

      return var2;
   }

   public static void throwNotSerializableForCorba(String var0) {
      throw omgWrapper.notSerializable((CompletionStatus)CompletionStatus.COMPLETED_MAYBE, var0);
   }

   public static byte getMaxStreamFormatVersion() {
      ValueHandler var0;
      try {
         var0 = (ValueHandler)AccessController.doPrivileged(new PrivilegedExceptionAction<ValueHandler>() {
            public ValueHandler run() throws Exception {
               return Util.createValueHandler();
            }
         });
      } catch (PrivilegedActionException var2) {
         throw new InternalError(var2.getMessage());
      }

      return !(var0 instanceof ValueHandlerMultiFormat) ? 1 : ((ValueHandlerMultiFormat)var0).getMaximumStreamFormatVersion();
   }

   public static CorbaClientDelegate makeClientDelegate(IOR var0) {
      ORB var1 = var0.getORB();
      CorbaContactInfoList var2 = var1.getCorbaContactInfoListFactory().create(var0);
      CorbaClientDelegate var3 = var1.getClientDelegateFactory().create(var2);
      return var3;
   }

   public static org.omg.CORBA.Object makeObjectReference(IOR var0) {
      CorbaClientDelegate var1 = makeClientDelegate(var0);
      CORBAObjectImpl var2 = new CORBAObjectImpl();
      StubAdapter.setDelegate(var2, var1);
      return var2;
   }

   public static IOR getIOR(org.omg.CORBA.Object var0) {
      if (var0 == null) {
         throw wrapper.nullObjectReference();
      } else {
         IOR var1 = null;
         if (StubAdapter.isStub(var0)) {
            Delegate var2 = StubAdapter.getDelegate(var0);
            if (var2 instanceof CorbaClientDelegate) {
               CorbaClientDelegate var3 = (CorbaClientDelegate)var2;
               ContactInfoList var4 = var3.getContactInfoList();
               if (var4 instanceof CorbaContactInfoList) {
                  CorbaContactInfoList var5 = (CorbaContactInfoList)var4;
                  var1 = var5.getTargetIOR();
                  if (var1 == null) {
                     throw wrapper.nullIor();
                  } else {
                     return var1;
                  }
               } else {
                  throw new INTERNAL();
               }
            } else {
               throw wrapper.objrefFromForeignOrb();
            }
         } else {
            throw wrapper.localObjectNotAllowed();
         }
      }
   }

   public static IOR connectAndGetIOR(ORB var0, org.omg.CORBA.Object var1) {
      IOR var2;
      try {
         var2 = getIOR(var1);
      } catch (BAD_OPERATION var6) {
         if (StubAdapter.isStub(var1)) {
            try {
               StubAdapter.connect(var1, var0);
            } catch (RemoteException var5) {
               throw wrapper.connectingServant((Throwable)var5);
            }
         } else {
            var0.connect(var1);
         }

         var2 = getIOR(var1);
      }

      return var2;
   }

   public static String operationNameAndRequestId(CorbaMessageMediator var0) {
      return "op/" + var0.getOperationName() + " id/" + var0.getRequestId();
   }

   public static boolean isPrintable(char var0) {
      if (Character.isJavaIdentifierStart(var0)) {
         return true;
      } else if (Character.isDigit(var0)) {
         return true;
      } else {
         switch(Character.getType(var0)) {
         case 20:
            return true;
         case 21:
            return true;
         case 22:
            return true;
         case 23:
         case 26:
         default:
            return false;
         case 24:
            return true;
         case 25:
            return true;
         case 27:
            return true;
         }
      }
   }

   public static String getClassSecurityInfo(final Class var0) {
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            StringBuffer var1 = new StringBuffer(500);
            ProtectionDomain var2 = var0.getProtectionDomain();
            Policy var3 = Policy.getPolicy();
            PermissionCollection var4 = var3.getPermissions(var2);
            var1.append("\nPermissionCollection ");
            var1.append(var4.toString());
            var1.append(var2.toString());
            return var1.toString();
         }
      });
      return var1;
   }

   static {
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0", "org.omg.CORBA.BAD_CONTEXT");
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0", "org.omg.CORBA.BAD_INV_ORDER");
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0", "org.omg.CORBA.BAD_OPERATION");
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_PARAM:1.0", "org.omg.CORBA.BAD_PARAM");
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0", "org.omg.CORBA.BAD_TYPECODE");
      exceptionClassNames.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0", "org.omg.CORBA.COMM_FAILURE");
      exceptionClassNames.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0", "org.omg.CORBA.DATA_CONVERSION");
      exceptionClassNames.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0", "org.omg.CORBA.IMP_LIMIT");
      exceptionClassNames.put("IDL:omg.org/CORBA/INTF_REPOS:1.0", "org.omg.CORBA.INTF_REPOS");
      exceptionClassNames.put("IDL:omg.org/CORBA/INTERNAL:1.0", "org.omg.CORBA.INTERNAL");
      exceptionClassNames.put("IDL:omg.org/CORBA/INV_FLAG:1.0", "org.omg.CORBA.INV_FLAG");
      exceptionClassNames.put("IDL:omg.org/CORBA/INV_IDENT:1.0", "org.omg.CORBA.INV_IDENT");
      exceptionClassNames.put("IDL:omg.org/CORBA/INV_OBJREF:1.0", "org.omg.CORBA.INV_OBJREF");
      exceptionClassNames.put("IDL:omg.org/CORBA/MARSHAL:1.0", "org.omg.CORBA.MARSHAL");
      exceptionClassNames.put("IDL:omg.org/CORBA/NO_MEMORY:1.0", "org.omg.CORBA.NO_MEMORY");
      exceptionClassNames.put("IDL:omg.org/CORBA/FREE_MEM:1.0", "org.omg.CORBA.FREE_MEM");
      exceptionClassNames.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0", "org.omg.CORBA.NO_IMPLEMENT");
      exceptionClassNames.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0", "org.omg.CORBA.NO_PERMISSION");
      exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0", "org.omg.CORBA.NO_RESOURCES");
      exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0", "org.omg.CORBA.NO_RESPONSE");
      exceptionClassNames.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0", "org.omg.CORBA.OBJ_ADAPTER");
      exceptionClassNames.put("IDL:omg.org/CORBA/INITIALIZE:1.0", "org.omg.CORBA.INITIALIZE");
      exceptionClassNames.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0", "org.omg.CORBA.PERSIST_STORE");
      exceptionClassNames.put("IDL:omg.org/CORBA/TRANSIENT:1.0", "org.omg.CORBA.TRANSIENT");
      exceptionClassNames.put("IDL:omg.org/CORBA/UNKNOWN:1.0", "org.omg.CORBA.UNKNOWN");
      exceptionClassNames.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0", "org.omg.CORBA.OBJECT_NOT_EXIST");
      exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0", "org.omg.CORBA.INVALID_TRANSACTION");
      exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0", "org.omg.CORBA.TRANSACTION_REQUIRED");
      exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0", "org.omg.CORBA.TRANSACTION_ROLLEDBACK");
      exceptionClassNames.put("IDL:omg.org/CORBA/INV_POLICY:1.0", "org.omg.CORBA.INV_POLICY");
      exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0", "org.omg.CORBA.TRANSACTION_UNAVAILABLE");
      exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0", "org.omg.CORBA.TRANSACTION_MODE");
      exceptionClassNames.put("IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0", "org.omg.CORBA.CODESET_INCOMPATIBLE");
      exceptionClassNames.put("IDL:omg.org/CORBA/REBIND:1.0", "org.omg.CORBA.REBIND");
      exceptionClassNames.put("IDL:omg.org/CORBA/TIMEOUT:1.0", "org.omg.CORBA.TIMEOUT");
      exceptionClassNames.put("IDL:omg.org/CORBA/BAD_QOS:1.0", "org.omg.CORBA.BAD_QOS");
      exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_ACTIVITY:1.0", "org.omg.CORBA.INVALID_ACTIVITY");
      exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_COMPLETED:1.0", "org.omg.CORBA.ACTIVITY_COMPLETED");
      exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_REQUIRED:1.0", "org.omg.CORBA.ACTIVITY_REQUIRED");
      Enumeration var0 = exceptionClassNames.keys();

      try {
         while(var0.hasMoreElements()) {
            Object var1 = var0.nextElement();
            String var2 = (String)var1;
            String var3 = (String)exceptionClassNames.get(var2);
            exceptionRepositoryIds.put(var3, var2);
         }
      } catch (NoSuchElementException var5) {
      }

   }
}
