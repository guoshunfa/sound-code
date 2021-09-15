package javax.management.remote.rmi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.MarshalledObject;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
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
import javax.security.auth.Subject;

public final class RMIConnectionImpl_Stub extends RemoteStub implements RMIConnection {
   private static final long serialVersionUID = 2L;
   private static Method $method_addNotificationListener_0;
   private static Method $method_addNotificationListeners_1;
   private static Method $method_close_2;
   private static Method $method_createMBean_3;
   private static Method $method_createMBean_4;
   private static Method $method_createMBean_5;
   private static Method $method_createMBean_6;
   private static Method $method_fetchNotifications_7;
   private static Method $method_getAttribute_8;
   private static Method $method_getAttributes_9;
   private static Method $method_getConnectionId_10;
   private static Method $method_getDefaultDomain_11;
   private static Method $method_getDomains_12;
   private static Method $method_getMBeanCount_13;
   private static Method $method_getMBeanInfo_14;
   private static Method $method_getObjectInstance_15;
   private static Method $method_invoke_16;
   private static Method $method_isInstanceOf_17;
   private static Method $method_isRegistered_18;
   private static Method $method_queryMBeans_19;
   private static Method $method_queryNames_20;
   private static Method $method_removeNotificationListener_21;
   private static Method $method_removeNotificationListener_22;
   private static Method $method_removeNotificationListeners_23;
   private static Method $method_setAttribute_24;
   private static Method $method_setAttributes_25;
   private static Method $method_unregisterMBean_26;
   // $FF: synthetic field
   static Class class$javax$management$remote$rmi$RMIConnection;
   // $FF: synthetic field
   static Class class$javax$management$ObjectName;
   // $FF: synthetic field
   static Class class$java$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class class$javax$security$auth$Subject;
   // $FF: synthetic field
   static Class array$Ljavax$management$ObjectName;
   // $FF: synthetic field
   static Class array$Ljava$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class array$Ljavax$security$auth$Subject;
   // $FF: synthetic field
   static Class class$java$lang$AutoCloseable;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class array$Ljava$lang$String;
   // $FF: synthetic field
   static Class array$Ljava$lang$Integer;

   static {
      try {
         $method_addNotificationListener_0 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("addNotificationListener", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_addNotificationListeners_1 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("addNotificationListeners", array$Ljavax$management$ObjectName != null ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")), array$Ljava$rmi$MarshalledObject != null ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")), array$Ljavax$security$auth$Subject != null ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
         $method_close_2 = (class$java$lang$AutoCloseable != null ? class$java$lang$AutoCloseable : (class$java$lang$AutoCloseable = class$("java.lang.AutoCloseable"))).getMethod("close");
         $method_createMBean_3 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_createMBean_4 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_createMBean_5 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_createMBean_6 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("createMBean", class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_fetchNotifications_7 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("fetchNotifications", Long.TYPE, Integer.TYPE, Long.TYPE);
         $method_getAttribute_8 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getAttribute", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getAttributes_9 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getAttributes", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getConnectionId_10 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getConnectionId");
         $method_getDefaultDomain_11 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getDefaultDomain", class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getDomains_12 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getDomains", class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getMBeanCount_13 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getMBeanCount", class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getMBeanInfo_14 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getMBeanInfo", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_getObjectInstance_15 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("getObjectInstance", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_invoke_16 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("invoke", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_isInstanceOf_17 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("isInstanceOf", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_isRegistered_18 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("isRegistered", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_queryMBeans_19 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("queryMBeans", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_queryNames_20 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("queryNames", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_removeNotificationListener_21 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListener", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_removeNotificationListener_22 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListener", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_removeNotificationListeners_23 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("removeNotificationListeners", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), array$Ljava$lang$Integer != null ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_setAttribute_24 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("setAttribute", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_setAttributes_25 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("setAttributes", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
         $method_unregisterMBean_26 = (class$javax$management$remote$rmi$RMIConnection != null ? class$javax$management$remote$rmi$RMIConnection : (class$javax$management$remote$rmi$RMIConnection = class$("javax.management.remote.rmi.RMIConnection"))).getMethod("unregisterMBean", class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")), class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public RMIConnectionImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   public void addNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws IOException, InstanceNotFoundException {
      try {
         super.ref.invoke(this, $method_addNotificationListener_0, new Object[]{var1, var2, var3, var4, var5}, -8578317696269497109L);
      } catch (RuntimeException var7) {
         throw var7;
      } catch (IOException var8) {
         throw var8;
      } catch (InstanceNotFoundException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new UnexpectedException("undeclared checked exception", var10);
      }
   }

   public Integer[] addNotificationListeners(ObjectName[] var1, MarshalledObject[] var2, Subject[] var3) throws IOException, InstanceNotFoundException {
      try {
         Object var4 = super.ref.invoke(this, $method_addNotificationListeners_1, new Object[]{var1, var2, var3}, -5321691879380783377L);
         return (Integer[])var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new UnexpectedException("undeclared checked exception", var8);
      }
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
      try {
         super.ref.invoke(this, $method_close_2, (Object[])null, -4742752445160157748L);
      } catch (RuntimeException var2) {
         throw var2;
      } catch (IOException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, MarshalledObject var3, String[] var4, Subject var5) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
      try {
         Object var6 = super.ref.invoke(this, $method_createMBean_3, new Object[]{var1, var2, var3, var4, var5}, 4867822117947806114L);
         return (ObjectInstance)var6;
      } catch (RuntimeException var7) {
         throw var7;
      } catch (IOException var8) {
         throw var8;
      } catch (InstanceAlreadyExistsException var9) {
         throw var9;
      } catch (MBeanException var10) {
         throw var10;
      } catch (NotCompliantMBeanException var11) {
         throw var11;
      } catch (ReflectionException var12) {
         throw var12;
      } catch (Exception var13) {
         throw new UnexpectedException("undeclared checked exception", var13);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, MarshalledObject var4, String[] var5, Subject var6) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
      try {
         Object var7 = super.ref.invoke(this, $method_createMBean_4, new Object[]{var1, var2, var3, var4, var5, var6}, -6604955182088909937L);
         return (ObjectInstance)var7;
      } catch (RuntimeException var8) {
         throw var8;
      } catch (IOException var9) {
         throw var9;
      } catch (InstanceAlreadyExistsException var10) {
         throw var10;
      } catch (InstanceNotFoundException var11) {
         throw var11;
      } catch (MBeanException var12) {
         throw var12;
      } catch (NotCompliantMBeanException var13) {
         throw var13;
      } catch (ReflectionException var14) {
         throw var14;
      } catch (Exception var15) {
         throw new UnexpectedException("undeclared checked exception", var15);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, ObjectName var3, Subject var4) throws IOException, InstanceAlreadyExistsException, InstanceNotFoundException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
      try {
         Object var5 = super.ref.invoke(this, $method_createMBean_5, new Object[]{var1, var2, var3, var4}, -8679469989872508324L);
         return (ObjectInstance)var5;
      } catch (RuntimeException var6) {
         throw var6;
      } catch (IOException var7) {
         throw var7;
      } catch (InstanceAlreadyExistsException var8) {
         throw var8;
      } catch (InstanceNotFoundException var9) {
         throw var9;
      } catch (MBeanException var10) {
         throw var10;
      } catch (NotCompliantMBeanException var11) {
         throw var11;
      } catch (ReflectionException var12) {
         throw var12;
      } catch (Exception var13) {
         throw new UnexpectedException("undeclared checked exception", var13);
      }
   }

   public ObjectInstance createMBean(String var1, ObjectName var2, Subject var3) throws IOException, InstanceAlreadyExistsException, MBeanException, MBeanRegistrationException, NotCompliantMBeanException, ReflectionException {
      try {
         Object var4 = super.ref.invoke(this, $method_createMBean_6, new Object[]{var1, var2, var3}, 2510753813974665446L);
         return (ObjectInstance)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceAlreadyExistsException var7) {
         throw var7;
      } catch (MBeanException var8) {
         throw var8;
      } catch (NotCompliantMBeanException var9) {
         throw var9;
      } catch (ReflectionException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new UnexpectedException("undeclared checked exception", var11);
      }
   }

   public NotificationResult fetchNotifications(long var1, int var3, long var4) throws IOException {
      try {
         Object var6 = super.ref.invoke(this, $method_fetchNotifications_7, new Object[]{new Long(var1), new Integer(var3), new Long(var4)}, -5037523307973544478L);
         return (NotificationResult)var6;
      } catch (RuntimeException var7) {
         throw var7;
      } catch (IOException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public Object getAttribute(ObjectName var1, String var2, Subject var3) throws IOException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
      try {
         Object var4 = super.ref.invoke(this, $method_getAttribute_8, new Object[]{var1, var2, var3}, -1089783104982388203L);
         return var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (AttributeNotFoundException var7) {
         throw var7;
      } catch (InstanceNotFoundException var8) {
         throw var8;
      } catch (MBeanException var9) {
         throw var9;
      } catch (ReflectionException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new UnexpectedException("undeclared checked exception", var11);
      }
   }

   public AttributeList getAttributes(ObjectName var1, String[] var2, Subject var3) throws IOException, InstanceNotFoundException, ReflectionException {
      try {
         Object var4 = super.ref.invoke(this, $method_getAttributes_9, new Object[]{var1, var2, var3}, 6285293806596348999L);
         return (AttributeList)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (ReflectionException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public String getConnectionId() throws IOException {
      try {
         Object var1 = super.ref.invoke(this, $method_getConnectionId_10, (Object[])null, -67907180346059933L);
         return (String)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (IOException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public String getDefaultDomain(Subject var1) throws IOException {
      try {
         Object var2 = super.ref.invoke(this, $method_getDefaultDomain_11, new Object[]{var1}, 6047668923998658472L);
         return (String)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (IOException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public String[] getDomains(Subject var1) throws IOException {
      try {
         Object var2 = super.ref.invoke(this, $method_getDomains_12, new Object[]{var1}, -6662314179953625551L);
         return (String[])var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (IOException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public Integer getMBeanCount(Subject var1) throws IOException {
      try {
         Object var2 = super.ref.invoke(this, $method_getMBeanCount_13, new Object[]{var1}, -2042362057335820635L);
         return (Integer)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (IOException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }

   public MBeanInfo getMBeanInfo(ObjectName var1, Subject var2) throws IOException, InstanceNotFoundException, IntrospectionException, ReflectionException {
      try {
         Object var3 = super.ref.invoke(this, $method_getMBeanInfo_14, new Object[]{var1, var2}, -7404813916326233354L);
         return (MBeanInfo)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (IOException var5) {
         throw var5;
      } catch (InstanceNotFoundException var6) {
         throw var6;
      } catch (IntrospectionException var7) {
         throw var7;
      } catch (ReflectionException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public ObjectInstance getObjectInstance(ObjectName var1, Subject var2) throws IOException, InstanceNotFoundException {
      try {
         Object var3 = super.ref.invoke(this, $method_getObjectInstance_15, new Object[]{var1, var2}, 6950095694996159938L);
         return (ObjectInstance)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (IOException var5) {
         throw var5;
      } catch (InstanceNotFoundException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public Object invoke(ObjectName var1, String var2, MarshalledObject var3, String[] var4, Subject var5) throws IOException, InstanceNotFoundException, MBeanException, ReflectionException {
      try {
         Object var6 = super.ref.invoke(this, $method_invoke_16, new Object[]{var1, var2, var3, var4, var5}, 1434350937885235744L);
         return var6;
      } catch (RuntimeException var7) {
         throw var7;
      } catch (IOException var8) {
         throw var8;
      } catch (InstanceNotFoundException var9) {
         throw var9;
      } catch (MBeanException var10) {
         throw var10;
      } catch (ReflectionException var11) {
         throw var11;
      } catch (Exception var12) {
         throw new UnexpectedException("undeclared checked exception", var12);
      }
   }

   public boolean isInstanceOf(ObjectName var1, String var2, Subject var3) throws IOException, InstanceNotFoundException {
      try {
         Object var4 = super.ref.invoke(this, $method_isInstanceOf_17, new Object[]{var1, var2, var3}, -2147516868461740814L);
         return (Boolean)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new UnexpectedException("undeclared checked exception", var8);
      }
   }

   public boolean isRegistered(ObjectName var1, Subject var2) throws IOException {
      try {
         Object var3 = super.ref.invoke(this, $method_isRegistered_18, new Object[]{var1, var2}, 8325683335228268564L);
         return (Boolean)var3;
      } catch (RuntimeException var4) {
         throw var4;
      } catch (IOException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new UnexpectedException("undeclared checked exception", var6);
      }
   }

   public Set queryMBeans(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      try {
         Object var4 = super.ref.invoke(this, $method_queryMBeans_19, new Object[]{var1, var2, var3}, 2915881009400597976L);
         return (Set)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public Set queryNames(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException {
      try {
         Object var4 = super.ref.invoke(this, $method_queryNames_20, new Object[]{var1, var2, var3}, 9152567528369059802L);
         return (Set)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new UnexpectedException("undeclared checked exception", var7);
      }
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, MarshalledObject var3, MarshalledObject var4, Subject var5) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
      try {
         super.ref.invoke(this, $method_removeNotificationListener_21, new Object[]{var1, var2, var3, var4, var5}, 2578029900065214857L);
      } catch (RuntimeException var7) {
         throw var7;
      } catch (IOException var8) {
         throw var8;
      } catch (InstanceNotFoundException var9) {
         throw var9;
      } catch (ListenerNotFoundException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new UnexpectedException("undeclared checked exception", var11);
      }
   }

   public void removeNotificationListener(ObjectName var1, ObjectName var2, Subject var3) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
      try {
         super.ref.invoke(this, $method_removeNotificationListener_22, new Object[]{var1, var2, var3}, 6604721169198089513L);
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (ListenerNotFoundException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public void removeNotificationListeners(ObjectName var1, Integer[] var2, Subject var3) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
      try {
         super.ref.invoke(this, $method_removeNotificationListeners_23, new Object[]{var1, var2, var3}, 2549120024456183446L);
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (ListenerNotFoundException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public void setAttribute(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException, AttributeNotFoundException, InstanceNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      try {
         super.ref.invoke(this, $method_setAttribute_24, new Object[]{var1, var2, var3}, 6738606893952597516L);
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (AttributeNotFoundException var7) {
         throw var7;
      } catch (InstanceNotFoundException var8) {
         throw var8;
      } catch (InvalidAttributeValueException var9) {
         throw var9;
      } catch (MBeanException var10) {
         throw var10;
      } catch (ReflectionException var11) {
         throw var11;
      } catch (Exception var12) {
         throw new UnexpectedException("undeclared checked exception", var12);
      }
   }

   public AttributeList setAttributes(ObjectName var1, MarshalledObject var2, Subject var3) throws IOException, InstanceNotFoundException, ReflectionException {
      try {
         Object var4 = super.ref.invoke(this, $method_setAttributes_25, new Object[]{var1, var2, var3}, -230470228399681820L);
         return (AttributeList)var4;
      } catch (RuntimeException var5) {
         throw var5;
      } catch (IOException var6) {
         throw var6;
      } catch (InstanceNotFoundException var7) {
         throw var7;
      } catch (ReflectionException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public void unregisterMBean(ObjectName var1, Subject var2) throws IOException, InstanceNotFoundException, MBeanRegistrationException {
      try {
         super.ref.invoke(this, $method_unregisterMBean_26, new Object[]{var1, var2}, -159498580868721452L);
      } catch (RuntimeException var4) {
         throw var4;
      } catch (IOException var5) {
         throw var5;
      } catch (InstanceNotFoundException var6) {
         throw var6;
      } catch (MBeanRegistrationException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new UnexpectedException("undeclared checked exception", var8);
      }
   }
}
