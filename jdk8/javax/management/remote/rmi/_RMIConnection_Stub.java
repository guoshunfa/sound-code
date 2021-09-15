package javax.management.remote.rmi;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.SerializablePermission;
import java.rmi.MarshalledObject;
import java.rmi.UnexpectedException;
import java.util.Set;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.NotificationResult;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.security.auth.Subject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class _RMIConnection_Stub extends Stub implements RMIConnection {
   private static final String[] _type_ids = new String[]{"RMI:javax.management.remote.rmi.RMIConnection:0000000000000000"};
   private transient boolean _instantiated;
   // $FF: synthetic field
   static Class class$java$io$IOException;
   // $FF: synthetic field
   static Class class$javax$management$remote$rmi$RMIConnection;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class class$javax$management$ObjectName;
   // $FF: synthetic field
   static Class class$javax$security$auth$Subject;
   // $FF: synthetic field
   static Class class$javax$management$ObjectInstance;
   // $FF: synthetic field
   static Class class$javax$management$ReflectionException;
   // $FF: synthetic field
   static Class class$javax$management$InstanceAlreadyExistsException;
   // $FF: synthetic field
   static Class class$javax$management$MBeanRegistrationException;
   // $FF: synthetic field
   static Class class$javax$management$MBeanException;
   // $FF: synthetic field
   static Class class$javax$management$NotCompliantMBeanException;
   // $FF: synthetic field
   static Class class$javax$management$InstanceNotFoundException;
   // $FF: synthetic field
   static Class class$java$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class array$Ljava$lang$String;
   // $FF: synthetic field
   static Class class$java$util$Set;
   // $FF: synthetic field
   static Class class$java$lang$Integer;
   // $FF: synthetic field
   static Class class$javax$management$AttributeNotFoundException;
   // $FF: synthetic field
   static Class class$javax$management$AttributeList;
   // $FF: synthetic field
   static Class class$javax$management$InvalidAttributeValueException;
   // $FF: synthetic field
   static Class class$javax$management$MBeanInfo;
   // $FF: synthetic field
   static Class class$javax$management$IntrospectionException;
   // $FF: synthetic field
   static Class class$javax$management$ListenerNotFoundException;
   // $FF: synthetic field
   static Class array$Ljavax$management$ObjectName;
   // $FF: synthetic field
   static Class array$Ljava$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class array$Ljavax$security$auth$Subject;
   // $FF: synthetic field
   static Class array$Ljava$lang$Integer;
   // $FF: synthetic field
   static Class class$javax$management$remote$NotificationResult;

   public _RMIConnection_Stub() {
      this(checkPermission());
      this._instantiated = true;
   }

   private _RMIConnection_Stub(Void var1) {
      this._instantiated = false;
   }

   public String[] _ids() {
      return (String[])_type_ids.clone();
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var6 = null;

               try {
                  OutputStream var9 = (OutputStream)this._request("addNotificationListener", true);
                  var9.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var9.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var9.write_value(var3, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var9.write_value(var4, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var9.write_value(var5, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var9);
               } catch (ApplicationException var27) {
                  var6 = (InputStream)var27.getInputStream();
                  String var10 = var6.read_string();
                  if (var10.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var6.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var10.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var6.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var10);
               } catch (RemarshalException var28) {
                  this.addNotificationListener(var1, var2, var3, var4, var5);
               } finally {
                  this._releaseReply(var6);
               }
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var33 = this._servant_preinvoke("addNotificationListener", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var33 == null) {
               this.addNotificationListener(var1, var2, var3, var4, var5);
               return;
            }

            try {
               Object[] var34 = Util.copyObjects(new Object[]{var1, var2, var3, var4, var5}, this._orb());
               ObjectName var36 = (ObjectName)var34[0];
               ObjectName var11 = (ObjectName)var34[1];
               MarshalledObject var12 = (MarshalledObject)var34[2];
               MarshalledObject var13 = (MarshalledObject)var34[3];
               Subject var14 = (Subject)var34[4];
               ((RMIConnection)var33.servant).addNotificationListener(var36, var11, var12, var13, var14);
            } catch (Throwable var31) {
               Throwable var35 = (Throwable)Util.copyObject(var31, this._orb());
               if (var35 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var35;
               }

               if (var35 instanceof IOException) {
                  throw (IOException)var35;
               }

               throw Util.wrapException(var35);
            } finally {
               this._servant_postinvoke(var33);
            }
         }

      }
   }

   public Integer[] addNotificationListeners(ObjectName[] var1, MarshalledObject[] var2, Subject[] var3) throws InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Integer[] var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("addNotificationListeners", true);
                  var32.write_value(this.cast_array(var1), array$Ljavax$management$ObjectName != null ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
                  var32.write_value(this.cast_array(var2), array$Ljava$rmi$MarshalledObject != null ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
                  var32.write_value(this.cast_array(var3), array$Ljavax$security$auth$Subject != null ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (Integer[])var31.read_value(array$Ljava$lang$Integer != null ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var31.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.addNotificationListeners(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("addNotificationListeners", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.addNotificationListeners(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName[] var33 = (ObjectName[])var8[0];
                  MarshalledObject[] var10 = (MarshalledObject[])var8[1];
                  Subject[] var11 = (Subject[])var8[2];
                  Integer[] var12 = ((RMIConnection)var4.servant).addNotificationListeners(var33, var10, var11);
                  var5 = (Integer[])Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   private Serializable cast_array(Object var1) {
      return (Serializable)var1;
   }

   private static Void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new SerializablePermission("enableSubclassImplementation"));
      }

      return null;
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public void close() throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var1 = null;

               try {
                  org.omg.CORBA.portable.OutputStream var4 = this._request("close", true);
                  this._invoke(var4);
               } catch (ApplicationException var20) {
                  var1 = (InputStream)var20.getInputStream();
                  String var5 = var1.read_string();
                  if (var5.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var1.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var5);
               } catch (RemarshalException var21) {
                  this.close();
               } finally {
                  this._releaseReply(var1);
               }
            } catch (SystemException var23) {
               throw Util.mapSystemException(var23);
            }
         } else {
            ServantObject var24 = this._servant_preinvoke("close", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var24 == null) {
               this.close();
               return;
            }

            try {
               ((RMIConnection)var24.servant).close();
            } catch (Throwable var18) {
               Throwable var25 = (Throwable)Util.copyObject(var18, this._orb());
               if (var25 instanceof IOException) {
                  throw (IOException)var25;
               }

               throw Util.wrapException(var25);
            } finally {
               this._servant_postinvoke(var24);
            }
         }

      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, MarshalledObject var3, String[] var4, Subject var5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var37;
         ObjectInstance var7;
         if (!Util.isLocal(this)) {
            try {
               InputStream var35 = null;

               try {
                  OutputStream var36 = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
                  var36.write_value(var1, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var36.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var36.write_value(var3, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var36.write_value(this.cast_array(var4), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  var36.write_value(var5, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var35 = (InputStream)this._invoke(var36);
                  var7 = (ObjectInstance)var35.read_value(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                  return var7;
               } catch (ApplicationException var31) {
                  var35 = (InputStream)var31.getInputStream();
                  var37 = var35.read_string();
                  if (var37.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var35.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var37.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                     throw (InstanceAlreadyExistsException)var35.read_value(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                  }

                  if (var37.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                     throw (MBeanRegistrationException)var35.read_value(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                  }

                  if (var37.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var35.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var37.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                     throw (NotCompliantMBeanException)var35.read_value(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                  }

                  if (var37.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var35.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var37);
               } catch (RemarshalException var32) {
                  var7 = this.createMBean(var1, var2, var3, var4, var5);
               } finally {
                  this._releaseReply(var35);
               }

               return var7;
            } catch (SystemException var34) {
               throw Util.mapSystemException(var34);
            }
         } else {
            ServantObject var6 = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var6 == null) {
               return this.createMBean(var1, var2, var3, var4, var5);
            } else {
               try {
                  Object[] var10 = Util.copyObjects(new Object[]{var1, var2, var3, var4, var5}, this._orb());
                  var37 = (String)var10[0];
                  ObjectName var12 = (ObjectName)var10[1];
                  MarshalledObject var13 = (MarshalledObject)var10[2];
                  String[] var14 = (String[])var10[3];
                  Subject var15 = (Subject)var10[4];
                  ObjectInstance var16 = ((RMIConnection)var6.servant).createMBean(var37, var12, var13, var14, var15);
                  var7 = (ObjectInstance)Util.copyObject(var16, this._orb());
               } catch (Throwable var29) {
                  Throwable var11 = (Throwable)Util.copyObject(var29, this._orb());
                  if (var11 instanceof ReflectionException) {
                     throw (ReflectionException)var11;
                  }

                  if (var11 instanceof InstanceAlreadyExistsException) {
                     throw (InstanceAlreadyExistsException)var11;
                  }

                  if (var11 instanceof MBeanRegistrationException) {
                     throw (MBeanRegistrationException)var11;
                  }

                  if (var11 instanceof MBeanException) {
                     throw (MBeanException)var11;
                  }

                  if (var11 instanceof NotCompliantMBeanException) {
                     throw (NotCompliantMBeanException)var11;
                  }

                  if (var11 instanceof IOException) {
                     throw (IOException)var11;
                  }

                  throw Util.wrapException(var11);
               } finally {
                  this._servant_postinvoke(var6);
               }

               return var7;
            }
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, MarshalledObject var4, String[] var5, Subject var6) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var39;
         ObjectInstance var8;
         if (!Util.isLocal(this)) {
            try {
               InputStream var37 = null;

               try {
                  OutputStream var38 = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
                  var38.write_value(var1, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var38.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var38.write_value(var3, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var38.write_value(var4, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var38.write_value(this.cast_array(var5), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  var38.write_value(var6, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var37 = (InputStream)this._invoke(var38);
                  var8 = (ObjectInstance)var37.read_value(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                  return var8;
               } catch (ApplicationException var33) {
                  var37 = (InputStream)var33.getInputStream();
                  var39 = var37.read_string();
                  if (var39.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var37.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var39.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                     throw (InstanceAlreadyExistsException)var37.read_value(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                  }

                  if (var39.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                     throw (MBeanRegistrationException)var37.read_value(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                  }

                  if (var39.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var37.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var39.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                     throw (NotCompliantMBeanException)var37.read_value(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                  }

                  if (var39.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var37.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var39.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var37.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var39);
               } catch (RemarshalException var34) {
                  var8 = this.createMBean(var1, var2, var3, var4, var5, var6);
               } finally {
                  this._releaseReply(var37);
               }

               return var8;
            } catch (SystemException var36) {
               throw Util.mapSystemException(var36);
            }
         } else {
            ServantObject var7 = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var7 == null) {
               return this.createMBean(var1, var2, var3, var4, var5, var6);
            } else {
               try {
                  Object[] var11 = Util.copyObjects(new Object[]{var1, var2, var3, var4, var5, var6}, this._orb());
                  var39 = (String)var11[0];
                  ObjectName var13 = (ObjectName)var11[1];
                  ObjectName var14 = (ObjectName)var11[2];
                  MarshalledObject var15 = (MarshalledObject)var11[3];
                  String[] var16 = (String[])var11[4];
                  Subject var17 = (Subject)var11[5];
                  ObjectInstance var18 = ((RMIConnection)var7.servant).createMBean(var39, var13, var14, var15, var16, var17);
                  var8 = (ObjectInstance)Util.copyObject(var18, this._orb());
               } catch (Throwable var31) {
                  Throwable var12 = (Throwable)Util.copyObject(var31, this._orb());
                  if (var12 instanceof ReflectionException) {
                     throw (ReflectionException)var12;
                  }

                  if (var12 instanceof InstanceAlreadyExistsException) {
                     throw (InstanceAlreadyExistsException)var12;
                  }

                  if (var12 instanceof MBeanRegistrationException) {
                     throw (MBeanRegistrationException)var12;
                  }

                  if (var12 instanceof MBeanException) {
                     throw (MBeanException)var12;
                  }

                  if (var12 instanceof NotCompliantMBeanException) {
                     throw (NotCompliantMBeanException)var12;
                  }

                  if (var12 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var12;
                  }

                  if (var12 instanceof IOException) {
                     throw (IOException)var12;
                  }

                  throw Util.wrapException(var12);
               } finally {
                  this._servant_postinvoke(var7);
               }

               return var8;
            }
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Subject var4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var35;
         ObjectInstance var6;
         if (!Util.isLocal(this)) {
            try {
               InputStream var33 = null;

               try {
                  OutputStream var34 = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
                  var34.write_value(var1, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var34.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var34.write_value(var3, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var34.write_value(var4, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var33 = (InputStream)this._invoke(var34);
                  var6 = (ObjectInstance)var33.read_value(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                  return var6;
               } catch (ApplicationException var29) {
                  var33 = (InputStream)var29.getInputStream();
                  var35 = var33.read_string();
                  if (var35.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var33.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var35.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                     throw (InstanceAlreadyExistsException)var33.read_value(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                  }

                  if (var35.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                     throw (MBeanRegistrationException)var33.read_value(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                  }

                  if (var35.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var33.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var35.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                     throw (NotCompliantMBeanException)var33.read_value(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                  }

                  if (var35.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var33.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var35.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var33.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var35);
               } catch (RemarshalException var30) {
                  var6 = this.createMBean(var1, var2, var3, var4);
               } finally {
                  this._releaseReply(var33);
               }

               return var6;
            } catch (SystemException var32) {
               throw Util.mapSystemException(var32);
            }
         } else {
            ServantObject var5 = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var5 == null) {
               return this.createMBean(var1, var2, var3, var4);
            } else {
               try {
                  Object[] var9 = Util.copyObjects(new Object[]{var1, var2, var3, var4}, this._orb());
                  var35 = (String)var9[0];
                  ObjectName var11 = (ObjectName)var9[1];
                  ObjectName var12 = (ObjectName)var9[2];
                  Subject var13 = (Subject)var9[3];
                  ObjectInstance var14 = ((RMIConnection)var5.servant).createMBean(var35, var11, var12, var13);
                  var6 = (ObjectInstance)Util.copyObject(var14, this._orb());
               } catch (Throwable var27) {
                  Throwable var10 = (Throwable)Util.copyObject(var27, this._orb());
                  if (var10 instanceof ReflectionException) {
                     throw (ReflectionException)var10;
                  }

                  if (var10 instanceof InstanceAlreadyExistsException) {
                     throw (InstanceAlreadyExistsException)var10;
                  }

                  if (var10 instanceof MBeanRegistrationException) {
                     throw (MBeanRegistrationException)var10;
                  }

                  if (var10 instanceof MBeanException) {
                     throw (MBeanException)var10;
                  }

                  if (var10 instanceof NotCompliantMBeanException) {
                     throw (NotCompliantMBeanException)var10;
                  }

                  if (var10 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var10;
                  }

                  if (var10 instanceof IOException) {
                     throw (IOException)var10;
                  }

                  throw Util.wrapException(var10);
               } finally {
                  this._servant_postinvoke(var5);
               }

               return var6;
            }
         }
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Subject var3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var33;
         ObjectInstance var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", true);
                  var32.write_value(var1, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var32.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (ObjectInstance)var31.read_value(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  var33 = var31.read_string();
                  if (var33.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var31.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var33.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0")) {
                     throw (InstanceAlreadyExistsException)var31.read_value(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException")));
                  }

                  if (var33.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                     throw (MBeanRegistrationException)var31.read_value(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                  }

                  if (var33.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var31.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var33.equals("IDL:javax/management/NotCompliantMBeanEx:1.0")) {
                     throw (NotCompliantMBeanException)var31.read_value(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException")));
                  }

                  if (var33.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var33);
               } catch (RemarshalException var28) {
                  var5 = this.createMBean(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.createMBean(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  var33 = (String)var8[0];
                  ObjectName var10 = (ObjectName)var8[1];
                  Subject var11 = (Subject)var8[2];
                  ObjectInstance var12 = ((RMIConnection)var4.servant).createMBean(var33, var10, var11);
                  var5 = (ObjectInstance)Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof ReflectionException) {
                     throw (ReflectionException)var9;
                  }

                  if (var9 instanceof InstanceAlreadyExistsException) {
                     throw (InstanceAlreadyExistsException)var9;
                  }

                  if (var9 instanceof MBeanRegistrationException) {
                     throw (MBeanRegistrationException)var9;
                  }

                  if (var9 instanceof MBeanException) {
                     throw (MBeanException)var9;
                  }

                  if (var9 instanceof NotCompliantMBeanException) {
                     throw (NotCompliantMBeanException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public NotificationResult fetchNotifications(long var1, int var3, long var4) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         NotificationResult var7;
         if (!Util.isLocal(this)) {
            try {
               InputStream var32 = null;

               try {
                  org.omg.CORBA.portable.OutputStream var30 = this._request("fetchNotifications", true);
                  var30.write_longlong(var1);
                  var30.write_long(var3);
                  var30.write_longlong(var4);
                  var32 = (InputStream)this._invoke(var30);
                  var7 = (NotificationResult)var32.read_value(class$javax$management$remote$NotificationResult != null ? class$javax$management$remote$NotificationResult : (class$javax$management$remote$NotificationResult = class$("javax.management.remote.NotificationResult")));
                  return var7;
               } catch (ApplicationException var26) {
                  var32 = (InputStream)var26.getInputStream();
                  String var31 = var32.read_string();
                  if (var31.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var32.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var31);
               } catch (RemarshalException var27) {
                  var7 = this.fetchNotifications(var1, var3, var4);
               } finally {
                  this._releaseReply(var32);
               }

               return var7;
            } catch (SystemException var29) {
               throw Util.mapSystemException(var29);
            }
         } else {
            ServantObject var6 = this._servant_preinvoke("fetchNotifications", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var6 == null) {
               return this.fetchNotifications(var1, var3, var4);
            } else {
               try {
                  NotificationResult var10 = ((RMIConnection)var6.servant).fetchNotifications(var1, var3, var4);
                  var7 = (NotificationResult)Util.copyObject(var10, this._orb());
               } catch (Throwable var24) {
                  Throwable var11 = (Throwable)Util.copyObject(var24, this._orb());
                  if (var11 instanceof IOException) {
                     throw (IOException)var11;
                  }

                  throw Util.wrapException(var11);
               } finally {
                  this._servant_postinvoke(var6);
               }

               return var7;
            }
         }
      }
   }

   public Object getAttribute(ObjectName var1, String var2, Subject var3) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Object var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("getAttribute", true);
                  var32.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(var2, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = Util.readAny(var31);
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var31.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var34.equals("IDL:javax/management/AttributeNotFoundEx:1.0")) {
                     throw (AttributeNotFoundException)var31.read_value(class$javax$management$AttributeNotFoundException != null ? class$javax$management$AttributeNotFoundException : (class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                  }

                  if (var34.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var31.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var34.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var31.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.getAttribute(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("getAttribute", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.getAttribute(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var33 = (ObjectName)var8[0];
                  String var10 = (String)var8[1];
                  Subject var11 = (Subject)var8[2];
                  Object var12 = ((RMIConnection)var4.servant).getAttribute(var33, var10, var11);
                  var5 = Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof MBeanException) {
                     throw (MBeanException)var9;
                  }

                  if (var9 instanceof AttributeNotFoundException) {
                     throw (AttributeNotFoundException)var9;
                  }

                  if (var9 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var9;
                  }

                  if (var9 instanceof ReflectionException) {
                     throw (ReflectionException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         AttributeList var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("getAttributes", true);
                  var32.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(this.cast_array(var2), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (AttributeList)var31.read_value(class$javax$management$AttributeList != null ? class$javax$management$AttributeList : (class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var31.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var34.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var31.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.getAttributes(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("getAttributes", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.getAttributes(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var33 = (ObjectName)var8[0];
                  String[] var10 = (String[])var8[1];
                  Subject var11 = (Subject)var8[2];
                  AttributeList var12 = ((RMIConnection)var4.servant).getAttributes(var33, var10, var11);
                  var5 = (AttributeList)Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var9;
                  }

                  if (var9 instanceof ReflectionException) {
                     throw (ReflectionException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public String getConnectionId() throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var2;
         if (!Util.isLocal(this)) {
            try {
               InputStream var25 = null;

               try {
                  org.omg.CORBA.portable.OutputStream var5 = this._request("getConnectionId", true);
                  var25 = (InputStream)this._invoke(var5);
                  var2 = (String)var25.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  return var2;
               } catch (ApplicationException var21) {
                  var25 = (InputStream)var21.getInputStream();
                  String var26 = var25.read_string();
                  if (var26.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var25.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var26);
               } catch (RemarshalException var22) {
                  var2 = this.getConnectionId();
               } finally {
                  this._releaseReply(var25);
               }

               return var2;
            } catch (SystemException var24) {
               throw Util.mapSystemException(var24);
            }
         } else {
            ServantObject var1 = this._servant_preinvoke("getConnectionId", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var1 == null) {
               return this.getConnectionId();
            } else {
               try {
                  var2 = ((RMIConnection)var1.servant).getConnectionId();
               } catch (Throwable var19) {
                  Throwable var6 = (Throwable)Util.copyObject(var19, this._orb());
                  if (var6 instanceof IOException) {
                     throw (IOException)var6;
                  }

                  throw Util.wrapException(var6);
               } finally {
                  this._servant_postinvoke(var1);
               }

               return var2;
            }
         }
      }
   }

   public String getDefaultDomain(Subject var1) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String var3;
         if (!Util.isLocal(this)) {
            try {
               InputStream var26 = null;

               try {
                  OutputStream var27 = (OutputStream)this._request("getDefaultDomain", true);
                  var27.write_value(var1, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var26 = (InputStream)this._invoke(var27);
                  var3 = (String)var26.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  return var3;
               } catch (ApplicationException var22) {
                  var26 = (InputStream)var22.getInputStream();
                  String var28 = var26.read_string();
                  if (var28.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var26.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var28);
               } catch (RemarshalException var23) {
                  var3 = this.getDefaultDomain(var1);
               } finally {
                  this._releaseReply(var26);
               }

               return var3;
            } catch (SystemException var25) {
               throw Util.mapSystemException(var25);
            }
         } else {
            ServantObject var2 = this._servant_preinvoke("getDefaultDomain", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var2 == null) {
               return this.getDefaultDomain(var1);
            } else {
               try {
                  Subject var6 = (Subject)Util.copyObject(var1, this._orb());
                  var3 = ((RMIConnection)var2.servant).getDefaultDomain(var6);
               } catch (Throwable var20) {
                  Throwable var7 = (Throwable)Util.copyObject(var20, this._orb());
                  if (var7 instanceof IOException) {
                     throw (IOException)var7;
                  }

                  throw Util.wrapException(var7);
               } finally {
                  this._servant_postinvoke(var2);
               }

               return var3;
            }
         }
      }
   }

   public String[] getDomains(Subject var1) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         String[] var3;
         if (!Util.isLocal(this)) {
            try {
               InputStream var26 = null;

               try {
                  OutputStream var27 = (OutputStream)this._request("getDomains", true);
                  var27.write_value(var1, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var26 = (InputStream)this._invoke(var27);
                  var3 = (String[])var26.read_value(array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  return var3;
               } catch (ApplicationException var22) {
                  var26 = (InputStream)var22.getInputStream();
                  String var29 = var26.read_string();
                  if (var29.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var26.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var29);
               } catch (RemarshalException var23) {
                  var3 = this.getDomains(var1);
               } finally {
                  this._releaseReply(var26);
               }

               return var3;
            } catch (SystemException var25) {
               throw Util.mapSystemException(var25);
            }
         } else {
            ServantObject var2 = this._servant_preinvoke("getDomains", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var2 == null) {
               return this.getDomains(var1);
            } else {
               try {
                  Subject var6 = (Subject)Util.copyObject(var1, this._orb());
                  String[] var28 = ((RMIConnection)var2.servant).getDomains(var6);
                  var3 = (String[])Util.copyObject(var28, this._orb());
               } catch (Throwable var20) {
                  Throwable var7 = (Throwable)Util.copyObject(var20, this._orb());
                  if (var7 instanceof IOException) {
                     throw (IOException)var7;
                  }

                  throw Util.wrapException(var7);
               } finally {
                  this._servant_postinvoke(var2);
               }

               return var3;
            }
         }
      }
   }

   public Integer getMBeanCount(Subject var1) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Integer var3;
         if (!Util.isLocal(this)) {
            try {
               InputStream var26 = null;

               try {
                  OutputStream var27 = (OutputStream)this._request("getMBeanCount", true);
                  var27.write_value(var1, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var26 = (InputStream)this._invoke(var27);
                  var3 = (Integer)var26.read_value(class$java$lang$Integer != null ? class$java$lang$Integer : (class$java$lang$Integer = class$("java.lang.Integer")));
                  return var3;
               } catch (ApplicationException var22) {
                  var26 = (InputStream)var22.getInputStream();
                  String var29 = var26.read_string();
                  if (var29.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var26.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var29);
               } catch (RemarshalException var23) {
                  var3 = this.getMBeanCount(var1);
               } finally {
                  this._releaseReply(var26);
               }

               return var3;
            } catch (SystemException var25) {
               throw Util.mapSystemException(var25);
            }
         } else {
            ServantObject var2 = this._servant_preinvoke("getMBeanCount", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var2 == null) {
               return this.getMBeanCount(var1);
            } else {
               try {
                  Subject var6 = (Subject)Util.copyObject(var1, this._orb());
                  Integer var28 = ((RMIConnection)var2.servant).getMBeanCount(var6);
                  var3 = (Integer)Util.copyObject(var28, this._orb());
               } catch (Throwable var20) {
                  Throwable var7 = (Throwable)Util.copyObject(var20, this._orb());
                  if (var7 instanceof IOException) {
                     throw (IOException)var7;
                  }

                  throw Util.wrapException(var7);
               } finally {
                  this._servant_postinvoke(var2);
               }

               return var3;
            }
         }
      }
   }

   public MBeanInfo getMBeanInfo(ObjectName var1, Subject var2) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         MBeanInfo var4;
         if (!Util.isLocal(this)) {
            try {
               InputStream var29 = null;

               try {
                  OutputStream var30 = (OutputStream)this._request("getMBeanInfo", true);
                  var30.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var30.write_value(var2, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var29 = (InputStream)this._invoke(var30);
                  var4 = (MBeanInfo)var29.read_value(class$javax$management$MBeanInfo != null ? class$javax$management$MBeanInfo : (class$javax$management$MBeanInfo = class$("javax.management.MBeanInfo")));
                  return var4;
               } catch (ApplicationException var25) {
                  var29 = (InputStream)var25.getInputStream();
                  String var32 = var29.read_string();
                  if (var32.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var29.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var32.equals("IDL:javax/management/IntrospectionEx:1.0")) {
                     throw (IntrospectionException)var29.read_value(class$javax$management$IntrospectionException != null ? class$javax$management$IntrospectionException : (class$javax$management$IntrospectionException = class$("javax.management.IntrospectionException")));
                  }

                  if (var32.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var29.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var32.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var29.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var32);
               } catch (RemarshalException var26) {
                  var4 = this.getMBeanInfo(var1, var2);
               } finally {
                  this._releaseReply(var29);
               }

               return var4;
            } catch (SystemException var28) {
               throw Util.mapSystemException(var28);
            }
         } else {
            ServantObject var3 = this._servant_preinvoke("getMBeanInfo", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var3 == null) {
               return this.getMBeanInfo(var1, var2);
            } else {
               try {
                  Object[] var7 = Util.copyObjects(new Object[]{var1, var2}, this._orb());
                  ObjectName var31 = (ObjectName)var7[0];
                  Subject var9 = (Subject)var7[1];
                  MBeanInfo var10 = ((RMIConnection)var3.servant).getMBeanInfo(var31, var9);
                  var4 = (MBeanInfo)Util.copyObject(var10, this._orb());
               } catch (Throwable var23) {
                  Throwable var8 = (Throwable)Util.copyObject(var23, this._orb());
                  if (var8 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var8;
                  }

                  if (var8 instanceof IntrospectionException) {
                     throw (IntrospectionException)var8;
                  }

                  if (var8 instanceof ReflectionException) {
                     throw (ReflectionException)var8;
                  }

                  if (var8 instanceof IOException) {
                     throw (IOException)var8;
                  }

                  throw Util.wrapException(var8);
               } finally {
                  this._servant_postinvoke(var3);
               }

               return var4;
            }
         }
      }
   }

   public ObjectInstance getObjectInstance(ObjectName var1, Subject var2) throws InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         ObjectInstance var4;
         if (!Util.isLocal(this)) {
            try {
               InputStream var29 = null;

               try {
                  OutputStream var30 = (OutputStream)this._request("getObjectInstance", true);
                  var30.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var30.write_value(var2, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var29 = (InputStream)this._invoke(var30);
                  var4 = (ObjectInstance)var29.read_value(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance")));
                  return var4;
               } catch (ApplicationException var25) {
                  var29 = (InputStream)var25.getInputStream();
                  String var32 = var29.read_string();
                  if (var32.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var29.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var32.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var29.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var32);
               } catch (RemarshalException var26) {
                  var4 = this.getObjectInstance(var1, var2);
               } finally {
                  this._releaseReply(var29);
               }

               return var4;
            } catch (SystemException var28) {
               throw Util.mapSystemException(var28);
            }
         } else {
            ServantObject var3 = this._servant_preinvoke("getObjectInstance", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var3 == null) {
               return this.getObjectInstance(var1, var2);
            } else {
               try {
                  Object[] var7 = Util.copyObjects(new Object[]{var1, var2}, this._orb());
                  ObjectName var31 = (ObjectName)var7[0];
                  Subject var9 = (Subject)var7[1];
                  ObjectInstance var10 = ((RMIConnection)var3.servant).getObjectInstance(var31, var9);
                  var4 = (ObjectInstance)Util.copyObject(var10, this._orb());
               } catch (Throwable var23) {
                  Throwable var8 = (Throwable)Util.copyObject(var23, this._orb());
                  if (var8 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var8;
                  }

                  if (var8 instanceof IOException) {
                     throw (IOException)var8;
                  }

                  throw Util.wrapException(var8);
               } finally {
                  this._servant_postinvoke(var3);
               }

               return var4;
            }
         }
      }
   }

   public Object invoke(ObjectName var1, String var2, MarshalledObject var3, String[] var4, Subject var5) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Object var7;
         if (!Util.isLocal(this)) {
            try {
               InputStream var35 = null;

               try {
                  OutputStream var36 = (OutputStream)this._request("invoke", true);
                  var36.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var36.write_value(var2, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var36.write_value(var3, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var36.write_value(this.cast_array(var4), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  var36.write_value(var5, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var35 = (InputStream)this._invoke(var36);
                  var7 = Util.readAny(var35);
                  return var7;
               } catch (ApplicationException var31) {
                  var35 = (InputStream)var31.getInputStream();
                  String var38 = var35.read_string();
                  if (var38.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var35.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var38.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var35.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var38.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var35.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var38.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var35.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var38);
               } catch (RemarshalException var32) {
                  var7 = this.invoke(var1, var2, var3, var4, var5);
               } finally {
                  this._releaseReply(var35);
               }

               return var7;
            } catch (SystemException var34) {
               throw Util.mapSystemException(var34);
            }
         } else {
            ServantObject var6 = this._servant_preinvoke("invoke", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var6 == null) {
               return this.invoke(var1, var2, var3, var4, var5);
            } else {
               try {
                  Object[] var10 = Util.copyObjects(new Object[]{var1, var2, var3, var4, var5}, this._orb());
                  ObjectName var37 = (ObjectName)var10[0];
                  String var12 = (String)var10[1];
                  MarshalledObject var13 = (MarshalledObject)var10[2];
                  String[] var14 = (String[])var10[3];
                  Subject var15 = (Subject)var10[4];
                  Object var16 = ((RMIConnection)var6.servant).invoke(var37, var12, var13, var14, var15);
                  var7 = Util.copyObject(var16, this._orb());
               } catch (Throwable var29) {
                  Throwable var11 = (Throwable)Util.copyObject(var29, this._orb());
                  if (var11 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var11;
                  }

                  if (var11 instanceof MBeanException) {
                     throw (MBeanException)var11;
                  }

                  if (var11 instanceof ReflectionException) {
                     throw (ReflectionException)var11;
                  }

                  if (var11 instanceof IOException) {
                     throw (IOException)var11;
                  }

                  throw Util.wrapException(var11);
               } finally {
                  this._servant_postinvoke(var6);
               }

               return var7;
            }
         }
      }
   }

   public boolean isInstanceOf(ObjectName var1, String var2, Subject var3) throws InstanceNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         boolean var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var30 = null;

               try {
                  OutputStream var31 = (OutputStream)this._request("isInstanceOf", true);
                  var31.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var31.write_value(var2, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  var31.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var30 = (InputStream)this._invoke(var31);
                  var5 = var30.read_boolean();
                  return var5;
               } catch (ApplicationException var26) {
                  var30 = (InputStream)var26.getInputStream();
                  String var33 = var30.read_string();
                  if (var33.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var30.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var33.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var30.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var33);
               } catch (RemarshalException var27) {
                  var5 = this.isInstanceOf(var1, var2, var3);
               } finally {
                  this._releaseReply(var30);
               }

               return var5;
            } catch (SystemException var29) {
               throw Util.mapSystemException(var29);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("isInstanceOf", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.isInstanceOf(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var32 = (ObjectName)var8[0];
                  String var10 = (String)var8[1];
                  Subject var11 = (Subject)var8[2];
                  var5 = ((RMIConnection)var4.servant).isInstanceOf(var32, var10, var11);
               } catch (Throwable var24) {
                  Throwable var9 = (Throwable)Util.copyObject(var24, this._orb());
                  if (var9 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public boolean isRegistered(ObjectName var1, Subject var2) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         boolean var4;
         if (!Util.isLocal(this)) {
            try {
               InputStream var28 = null;

               try {
                  OutputStream var29 = (OutputStream)this._request("isRegistered", true);
                  var29.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var29.write_value(var2, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var28 = (InputStream)this._invoke(var29);
                  var4 = var28.read_boolean();
                  return var4;
               } catch (ApplicationException var24) {
                  var28 = (InputStream)var24.getInputStream();
                  String var31 = var28.read_string();
                  if (var31.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var28.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var31);
               } catch (RemarshalException var25) {
                  var4 = this.isRegistered(var1, var2);
               } finally {
                  this._releaseReply(var28);
               }

               return var4;
            } catch (SystemException var27) {
               throw Util.mapSystemException(var27);
            }
         } else {
            ServantObject var3 = this._servant_preinvoke("isRegistered", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var3 == null) {
               return this.isRegistered(var1, var2);
            } else {
               try {
                  Object[] var7 = Util.copyObjects(new Object[]{var1, var2}, this._orb());
                  ObjectName var30 = (ObjectName)var7[0];
                  Subject var9 = (Subject)var7[1];
                  var4 = ((RMIConnection)var3.servant).isRegistered(var30, var9);
               } catch (Throwable var22) {
                  Throwable var8 = (Throwable)Util.copyObject(var22, this._orb());
                  if (var8 instanceof IOException) {
                     throw (IOException)var8;
                  }

                  throw Util.wrapException(var8);
               } finally {
                  this._servant_postinvoke(var3);
               }

               return var4;
            }
         }
      }
   }

   public Set queryMBeans(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Set var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("queryMBeans", true);
                  var32.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(var2, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (Set)var31.read_value(class$java$util$Set != null ? class$java$util$Set : (class$java$util$Set = class$("java.util.Set")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.queryMBeans(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("queryMBeans", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.queryMBeans(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var33 = (ObjectName)var8[0];
                  MarshalledObject var10 = (MarshalledObject)var8[1];
                  Subject var11 = (Subject)var8[2];
                  Set var12 = ((RMIConnection)var4.servant).queryMBeans(var33, var10, var11);
                  var5 = (Set)Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public Set queryNames(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         Set var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("queryNames", true);
                  var32.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(var2, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (Set)var31.read_value(class$java$util$Set != null ? class$java$util$Set : (class$java$util$Set = class$("java.util.Set")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.queryNames(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("queryNames", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.queryNames(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var33 = (ObjectName)var8[0];
                  MarshalledObject var10 = (MarshalledObject)var8[1];
                  Subject var11 = (Subject)var8[2];
                  Set var12 = ((RMIConnection)var4.servant).queryNames(var33, var10, var11);
                  var5 = (Set)Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      checkPermission();
      var1.defaultReadObject();
      this._instantiated = true;
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var6 = null;

               try {
                  OutputStream var9 = (OutputStream)this._request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", true);
                  var9.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var9.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var9.write_value(var3, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var9.write_value(var4, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var9.write_value(var5, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var9);
               } catch (ApplicationException var27) {
                  var6 = (InputStream)var27.getInputStream();
                  String var10 = var6.read_string();
                  if (var10.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var6.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var10.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                     throw (ListenerNotFoundException)var6.read_value(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                  }

                  if (var10.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var6.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var10);
               } catch (RemarshalException var28) {
                  this.removeNotificationListener(var1, var2, var3, var4, var5);
               } finally {
                  this._releaseReply(var6);
               }
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var33 = this._servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var33 == null) {
               this.removeNotificationListener(var1, var2, var3, var4, var5);
               return;
            }

            try {
               Object[] var34 = Util.copyObjects(new Object[]{var1, var2, var3, var4, var5}, this._orb());
               ObjectName var36 = (ObjectName)var34[0];
               ObjectName var11 = (ObjectName)var34[1];
               MarshalledObject var12 = (MarshalledObject)var34[2];
               MarshalledObject var13 = (MarshalledObject)var34[3];
               Subject var14 = (Subject)var34[4];
               ((RMIConnection)var33.servant).removeNotificationListener(var36, var11, var12, var13, var14);
            } catch (Throwable var31) {
               Throwable var35 = (Throwable)Util.copyObject(var31, this._orb());
               if (var35 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var35;
               }

               if (var35 instanceof ListenerNotFoundException) {
                  throw (ListenerNotFoundException)var35;
               }

               if (var35 instanceof IOException) {
                  throw (IOException)var35;
               }

               throw Util.wrapException(var35);
            } finally {
               this._servant_postinvoke(var33);
            }
         }

      }
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var4 = null;

               try {
                  OutputStream var7 = (OutputStream)this._request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
                  var7.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var7.write_value(var2, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var7.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var7);
               } catch (ApplicationException var23) {
                  var4 = (InputStream)var23.getInputStream();
                  String var8 = var4.read_string();
                  if (var8.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var4.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var8.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                     throw (ListenerNotFoundException)var4.read_value(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                  }

                  if (var8.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var4.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var8);
               } catch (RemarshalException var24) {
                  this.removeNotificationListener(var1, var2, var3);
               } finally {
                  this._releaseReply(var4);
               }
            } catch (SystemException var26) {
               throw Util.mapSystemException(var26);
            }
         } else {
            ServantObject var29 = this._servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var29 == null) {
               this.removeNotificationListener(var1, var2, var3);
               return;
            }

            try {
               Object[] var30 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
               ObjectName var32 = (ObjectName)var30[0];
               ObjectName var9 = (ObjectName)var30[1];
               Subject var10 = (Subject)var30[2];
               ((RMIConnection)var29.servant).removeNotificationListener(var32, var9, var10);
            } catch (Throwable var27) {
               Throwable var31 = (Throwable)Util.copyObject(var27, this._orb());
               if (var31 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var31;
               }

               if (var31 instanceof ListenerNotFoundException) {
                  throw (ListenerNotFoundException)var31;
               }

               if (var31 instanceof IOException) {
                  throw (IOException)var31;
               }

               throw Util.wrapException(var31);
            } finally {
               this._servant_postinvoke(var29);
            }
         }

      }
   }

   public void removeNotificationListeners(ObjectName var1, Integer[] var2, Subject var3) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var4 = null;

               try {
                  OutputStream var7 = (OutputStream)this._request("removeNotificationListeners", true);
                  var7.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var7.write_value(this.cast_array(var2), array$Ljava$lang$Integer != null ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                  var7.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var7);
               } catch (ApplicationException var23) {
                  var4 = (InputStream)var23.getInputStream();
                  String var8 = var4.read_string();
                  if (var8.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var4.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var8.equals("IDL:javax/management/ListenerNotFoundEx:1.0")) {
                     throw (ListenerNotFoundException)var4.read_value(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException")));
                  }

                  if (var8.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var4.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var8);
               } catch (RemarshalException var24) {
                  this.removeNotificationListeners(var1, var2, var3);
               } finally {
                  this._releaseReply(var4);
               }
            } catch (SystemException var26) {
               throw Util.mapSystemException(var26);
            }
         } else {
            ServantObject var29 = this._servant_preinvoke("removeNotificationListeners", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var29 == null) {
               this.removeNotificationListeners(var1, var2, var3);
               return;
            }

            try {
               Object[] var30 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
               ObjectName var32 = (ObjectName)var30[0];
               Integer[] var9 = (Integer[])var30[1];
               Subject var10 = (Subject)var30[2];
               ((RMIConnection)var29.servant).removeNotificationListeners(var32, var9, var10);
            } catch (Throwable var27) {
               Throwable var31 = (Throwable)Util.copyObject(var27, this._orb());
               if (var31 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var31;
               }

               if (var31 instanceof ListenerNotFoundException) {
                  throw (ListenerNotFoundException)var31;
               }

               if (var31 instanceof IOException) {
                  throw (IOException)var31;
               }

               throw Util.wrapException(var31);
            } finally {
               this._servant_postinvoke(var29);
            }
         }

      }
   }

   public void setAttribute(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var4 = null;

               try {
                  OutputStream var7 = (OutputStream)this._request("setAttribute", true);
                  var7.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var7.write_value(var2, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var7.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var7);
               } catch (ApplicationException var23) {
                  var4 = (InputStream)var23.getInputStream();
                  String var8 = var4.read_string();
                  if (var8.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var4.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var8.equals("IDL:javax/management/AttributeNotFoundEx:1.0")) {
                     throw (AttributeNotFoundException)var4.read_value(class$javax$management$AttributeNotFoundException != null ? class$javax$management$AttributeNotFoundException : (class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException")));
                  }

                  if (var8.equals("IDL:javax/management/InvalidAttributeValueEx:1.0")) {
                     throw (InvalidAttributeValueException)var4.read_value(class$javax$management$InvalidAttributeValueException != null ? class$javax$management$InvalidAttributeValueException : (class$javax$management$InvalidAttributeValueException = class$("javax.management.InvalidAttributeValueException")));
                  }

                  if (var8.equals("IDL:javax/management/MBeanEx:1.0")) {
                     throw (MBeanException)var4.read_value(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException")));
                  }

                  if (var8.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var4.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var8.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var4.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var8);
               } catch (RemarshalException var24) {
                  this.setAttribute(var1, var2, var3);
               } finally {
                  this._releaseReply(var4);
               }
            } catch (SystemException var26) {
               throw Util.mapSystemException(var26);
            }
         } else {
            ServantObject var29 = this._servant_preinvoke("setAttribute", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var29 == null) {
               this.setAttribute(var1, var2, var3);
               return;
            }

            try {
               Object[] var30 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
               ObjectName var32 = (ObjectName)var30[0];
               MarshalledObject var9 = (MarshalledObject)var30[1];
               Subject var10 = (Subject)var30[2];
               ((RMIConnection)var29.servant).setAttribute(var32, var9, var10);
            } catch (Throwable var27) {
               Throwable var31 = (Throwable)Util.copyObject(var27, this._orb());
               if (var31 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var31;
               }

               if (var31 instanceof AttributeNotFoundException) {
                  throw (AttributeNotFoundException)var31;
               }

               if (var31 instanceof InvalidAttributeValueException) {
                  throw (InvalidAttributeValueException)var31;
               }

               if (var31 instanceof MBeanException) {
                  throw (MBeanException)var31;
               }

               if (var31 instanceof ReflectionException) {
                  throw (ReflectionException)var31;
               }

               if (var31 instanceof IOException) {
                  throw (IOException)var31;
               }

               throw Util.wrapException(var31);
            } finally {
               this._servant_postinvoke(var29);
            }
         }

      }
   }

   public AttributeList setAttributes(ObjectName var1, MarshalledObject var2, Subject var3) throws InstanceNotFoundException, ReflectionException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         AttributeList var5;
         if (!Util.isLocal(this)) {
            try {
               InputStream var31 = null;

               try {
                  OutputStream var32 = (OutputStream)this._request("setAttributes", true);
                  var32.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var32.write_value(var2, (Class)(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject"))));
                  var32.write_value(var3, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  var31 = (InputStream)this._invoke(var32);
                  var5 = (AttributeList)var31.read_value(class$javax$management$AttributeList != null ? class$javax$management$AttributeList : (class$javax$management$AttributeList = class$("javax.management.AttributeList")));
                  return var5;
               } catch (ApplicationException var27) {
                  var31 = (InputStream)var27.getInputStream();
                  String var34 = var31.read_string();
                  if (var34.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var31.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var34.equals("IDL:javax/management/ReflectionEx:1.0")) {
                     throw (ReflectionException)var31.read_value(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException")));
                  }

                  if (var34.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var31.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var34);
               } catch (RemarshalException var28) {
                  var5 = this.setAttributes(var1, var2, var3);
               } finally {
                  this._releaseReply(var31);
               }

               return var5;
            } catch (SystemException var30) {
               throw Util.mapSystemException(var30);
            }
         } else {
            ServantObject var4 = this._servant_preinvoke("setAttributes", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var4 == null) {
               return this.setAttributes(var1, var2, var3);
            } else {
               try {
                  Object[] var8 = Util.copyObjects(new Object[]{var1, var2, var3}, this._orb());
                  ObjectName var33 = (ObjectName)var8[0];
                  MarshalledObject var10 = (MarshalledObject)var8[1];
                  Subject var11 = (Subject)var8[2];
                  AttributeList var12 = ((RMIConnection)var4.servant).setAttributes(var33, var10, var11);
                  var5 = (AttributeList)Util.copyObject(var12, this._orb());
               } catch (Throwable var25) {
                  Throwable var9 = (Throwable)Util.copyObject(var25, this._orb());
                  if (var9 instanceof InstanceNotFoundException) {
                     throw (InstanceNotFoundException)var9;
                  }

                  if (var9 instanceof ReflectionException) {
                     throw (ReflectionException)var9;
                  }

                  if (var9 instanceof IOException) {
                     throw (IOException)var9;
                  }

                  throw Util.wrapException(var9);
               } finally {
                  this._servant_postinvoke(var4);
               }

               return var5;
            }
         }
      }
   }

   public void unregisterMBean(ObjectName var1, Subject var2) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
      if (System.getSecurityManager() != null && !this._instantiated) {
         throw new IOError(new IOException("InvalidObject "));
      } else {
         if (!Util.isLocal(this)) {
            try {
               InputStream var3 = null;

               try {
                  OutputStream var6 = (OutputStream)this._request("unregisterMBean", true);
                  var6.write_value(var1, (Class)(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName"))));
                  var6.write_value(var2, (Class)(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject"))));
                  this._invoke(var6);
               } catch (ApplicationException var23) {
                  var3 = (InputStream)var23.getInputStream();
                  String var7 = var3.read_string();
                  if (var7.equals("IDL:javax/management/InstanceNotFoundEx:1.0")) {
                     throw (InstanceNotFoundException)var3.read_value(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException")));
                  }

                  if (var7.equals("IDL:javax/management/MBeanRegistrationEx:1.0")) {
                     throw (MBeanRegistrationException)var3.read_value(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException")));
                  }

                  if (var7.equals("IDL:java/io/IOEx:1.0")) {
                     throw (IOException)var3.read_value(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException")));
                  }

                  throw new UnexpectedException(var7);
               } catch (RemarshalException var24) {
                  this.unregisterMBean(var1, var2);
               } finally {
                  this._releaseReply(var3);
               }
            } catch (SystemException var26) {
               throw Util.mapSystemException(var26);
            }
         } else {
            ServantObject var27 = this._servant_preinvoke("unregisterMBean", class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection")));
            if (var27 == null) {
               this.unregisterMBean(var1, var2);
               return;
            }

            try {
               Object[] var28 = Util.copyObjects(new Object[]{var1, var2}, this._orb());
               ObjectName var30 = (ObjectName)var28[0];
               Subject var8 = (Subject)var28[1];
               ((RMIConnection)var27.servant).unregisterMBean(var30, var8);
            } catch (Throwable var21) {
               Throwable var29 = (Throwable)Util.copyObject(var21, this._orb());
               if (var29 instanceof InstanceNotFoundException) {
                  throw (InstanceNotFoundException)var29;
               }

               if (var29 instanceof MBeanRegistrationException) {
                  throw (MBeanRegistrationException)var29;
               }

               if (var29 instanceof IOException) {
                  throw (IOException)var29;
               }

               throw Util.wrapException(var29);
            } finally {
               this._servant_postinvoke(var27);
            }
         }

      }
   }
}
