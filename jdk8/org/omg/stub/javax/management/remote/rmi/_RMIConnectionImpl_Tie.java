package org.omg.stub.javax.management.remote.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
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
import javax.management.remote.rmi.RMIConnectionImpl;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.security.auth.Subject;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class _RMIConnectionImpl_Tie extends ObjectImpl implements Tie {
   private volatile RMIConnectionImpl target = null;
   private static final String[] _type_ids = new String[]{"RMI:javax.management.remote.rmi.RMIConnection:0000000000000000"};
   // $FF: synthetic field
   static Class class$javax$management$ObjectName;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class class$javax$security$auth$Subject;
   // $FF: synthetic field
   static Class class$javax$management$MBeanException;
   // $FF: synthetic field
   static Class class$javax$management$AttributeNotFoundException;
   // $FF: synthetic field
   static Class class$javax$management$InstanceNotFoundException;
   // $FF: synthetic field
   static Class class$javax$management$ReflectionException;
   // $FF: synthetic field
   static Class class$java$io$IOException;
   // $FF: synthetic field
   static Class array$Ljava$lang$String;
   // $FF: synthetic field
   static Class class$javax$management$AttributeList;
   // $FF: synthetic field
   static Class class$java$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class class$javax$management$InvalidAttributeValueException;
   // $FF: synthetic field
   static Class class$java$lang$Integer;
   // $FF: synthetic field
   static Class class$javax$management$IntrospectionException;
   // $FF: synthetic field
   static Class class$javax$management$MBeanInfo;
   // $FF: synthetic field
   static Class array$Ljavax$management$ObjectName;
   // $FF: synthetic field
   static Class array$Ljava$rmi$MarshalledObject;
   // $FF: synthetic field
   static Class array$Ljavax$security$auth$Subject;
   // $FF: synthetic field
   static Class array$Ljava$lang$Integer;
   // $FF: synthetic field
   static Class class$javax$management$ObjectInstance;
   // $FF: synthetic field
   static Class class$javax$management$InstanceAlreadyExistsException;
   // $FF: synthetic field
   static Class class$javax$management$NotCompliantMBeanException;
   // $FF: synthetic field
   static Class class$javax$management$remote$NotificationResult;
   // $FF: synthetic field
   static Class class$javax$management$MBeanRegistrationException;
   // $FF: synthetic field
   static Class class$javax$management$ListenerNotFoundException;
   // $FF: synthetic field
   static Class class$java$util$Set;

   public String[] _ids() {
      return (String[])_type_ids.clone();
   }

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) throws SystemException {
      try {
         RMIConnectionImpl var4 = this.target;
         if (var4 == null) {
            throw new IOException();
         } else {
            org.omg.CORBA_2_3.portable.InputStream var5 = (org.omg.CORBA_2_3.portable.InputStream)var2;
            String var7;
            org.omg.CORBA_2_3.portable.OutputStream var8;
            org.omg.CORBA_2_3.portable.OutputStream var10;
            String var11;
            org.omg.CORBA_2_3.portable.OutputStream var12;
            org.omg.CORBA_2_3.portable.OutputStream var13;
            org.omg.CORBA_2_3.portable.OutputStream var14;
            ObjectName var94;
            MarshalledObject var95;
            Subject var97;
            ObjectName var98;
            MarshalledObject var100;
            Subject var101;
            String var102;
            OutputStream var103;
            MarshalledObject var104;
            String[] var105;
            String var107;
            String var112;
            Subject var113;
            org.omg.CORBA_2_3.portable.OutputStream var115;
            Subject var117;
            String var119;
            OutputStream var120;
            String[] var123;
            OutputStream var125;
            String var131;
            org.omg.CORBA_2_3.portable.OutputStream var133;
            switch(var1.charAt(3)) {
            case 'A':
               if (var1.equals("getAttribute")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var7 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  Object var144;
                  try {
                     var144 = var4.getAttribute(var94, var7, var97);
                  } catch (MBeanException var45) {
                     var11 = "IDL:javax/management/MBeanEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var45, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                     return var12;
                  } catch (AttributeNotFoundException var46) {
                     var11 = "IDL:javax/management/AttributeNotFoundEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var46, (Class)(class$javax$management$AttributeNotFoundException != null ? class$javax$management$AttributeNotFoundException : (class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException"))));
                     return var12;
                  } catch (InstanceNotFoundException var47) {
                     var11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var47, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var12;
                  } catch (ReflectionException var48) {
                     var11 = "IDL:javax/management/ReflectionEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var48, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                     return var12;
                  } catch (IOException var49) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var49, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var125 = var3.createReply();
                  Util.writeAny(var125, var144);
                  return var125;
               } else {
                  AttributeList var143;
                  if (var1.equals("getAttributes")) {
                     var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var123 = (String[])var5.read_value(array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                     var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     try {
                        var143 = var4.getAttributes(var94, var123, var97);
                     } catch (InstanceNotFoundException var56) {
                        var11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var56, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                        return var12;
                     } catch (ReflectionException var57) {
                        var11 = "IDL:javax/management/ReflectionEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var57, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var12;
                     } catch (IOException var58) {
                        var11 = "IDL:java/io/IOEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var58, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var12;
                     }

                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                     var10.write_value(var143, (Class)(class$javax$management$AttributeList != null ? class$javax$management$AttributeList : (class$javax$management$AttributeList = class$("javax.management.AttributeList"))));
                     return var10;
                  } else if (var1.equals("setAttribute")) {
                     var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var95 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                     var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     try {
                        var4.setAttribute(var94, var95, var97);
                     } catch (InstanceNotFoundException var74) {
                        var107 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var74, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                        return var115;
                     } catch (AttributeNotFoundException var75) {
                        var107 = "IDL:javax/management/AttributeNotFoundEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var75, (Class)(class$javax$management$AttributeNotFoundException != null ? class$javax$management$AttributeNotFoundException : (class$javax$management$AttributeNotFoundException = class$("javax.management.AttributeNotFoundException"))));
                        return var115;
                     } catch (InvalidAttributeValueException var76) {
                        var107 = "IDL:javax/management/InvalidAttributeValueEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var76, (Class)(class$javax$management$InvalidAttributeValueException != null ? class$javax$management$InvalidAttributeValueException : (class$javax$management$InvalidAttributeValueException = class$("javax.management.InvalidAttributeValueException"))));
                        return var115;
                     } catch (MBeanException var77) {
                        var107 = "IDL:javax/management/MBeanEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var77, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                        return var115;
                     } catch (ReflectionException var78) {
                        var107 = "IDL:javax/management/ReflectionEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var78, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var115;
                     } catch (IOException var79) {
                        var107 = "IDL:java/io/IOEx:1.0";
                        var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var115.write_string(var107);
                        var115.write_value(var79, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var115;
                     }

                     var103 = var3.createReply();
                     return var103;
                  } else if (var1.equals("setAttributes")) {
                     var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var95 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                     var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     try {
                        var143 = var4.setAttributes(var94, var95, var97);
                     } catch (InstanceNotFoundException var80) {
                        var11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var80, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                        return var12;
                     } catch (ReflectionException var81) {
                        var11 = "IDL:javax/management/ReflectionEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var81, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var12;
                     } catch (IOException var82) {
                        var11 = "IDL:java/io/IOEx:1.0";
                        var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var12.write_string(var11);
                        var12.write_value(var82, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var12;
                     }

                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                     var10.write_value(var143, (Class)(class$javax$management$AttributeList != null ? class$javax$management$AttributeList : (class$javax$management$AttributeList = class$("javax.management.AttributeList"))));
                     return var10;
                  }
               }
            case 'C':
               if (var1.equals("getConnectionId")) {
                  try {
                     var102 = var4.getConnectionId();
                  } catch (IOException var63) {
                     String var138 = "IDL:java/io/IOEx:1.0";
                     var133 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var133.write_string(var138);
                     var133.write_value(var63, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var133;
                  }

                  org.omg.CORBA_2_3.portable.OutputStream var129 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var129.write_value(var102, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  return var129;
               }
            case 'D':
               if (var1.equals("getDefaultDomain")) {
                  var117 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var7 = var4.getDefaultDomain(var117);
                  } catch (IOException var19) {
                     var112 = "IDL:java/io/IOEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var19, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var10;
                  }

                  var8 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var8.write_value(var7, (Class)(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String"))));
                  return var8;
               } else if (var1.equals("getDomains")) {
                  var117 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var123 = var4.getDomains(var117);
                  } catch (IOException var20) {
                     var112 = "IDL:java/io/IOEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var20, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var10;
                  }

                  var8 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var8.write_value(this.cast_array(var123), array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  return var8;
               }
            case 'M':
               if (var1.equals("getMBeanCount")) {
                  var117 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  Integer var121;
                  try {
                     var121 = var4.getMBeanCount(var117);
                  } catch (IOException var83) {
                     var112 = "IDL:java/io/IOEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var83, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var10;
                  }

                  var8 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var8.write_value(var121, (Class)(class$java$lang$Integer != null ? class$java$lang$Integer : (class$java$lang$Integer = class$("java.lang.Integer"))));
                  return var8;
               } else if (var1.equals("getMBeanInfo")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var101 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  MBeanInfo var132;
                  try {
                     var132 = var4.getMBeanInfo(var94, var101);
                  } catch (InstanceNotFoundException var59) {
                     var107 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var59, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var115;
                  } catch (IntrospectionException var60) {
                     var107 = "IDL:javax/management/IntrospectionEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var60, (Class)(class$javax$management$IntrospectionException != null ? class$javax$management$IntrospectionException : (class$javax$management$IntrospectionException = class$("javax.management.IntrospectionException"))));
                     return var115;
                  } catch (ReflectionException var61) {
                     var107 = "IDL:javax/management/ReflectionEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var61, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                     return var115;
                  } catch (IOException var62) {
                     var107 = "IDL:java/io/IOEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var62, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var115;
                  }

                  var133 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var133.write_value(var132, (Class)(class$javax$management$MBeanInfo != null ? class$javax$management$MBeanInfo : (class$javax$management$MBeanInfo = class$("javax.management.MBeanInfo"))));
                  return var133;
               }
            case 'N':
               if (var1.equals("addNotificationListener")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var100 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var104 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var113 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var4.addNotificationListener(var94, var98, var100, var104, var113);
                  } catch (InstanceNotFoundException var21) {
                     var119 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var13.write_string(var119);
                     var13.write_value(var21, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var13;
                  } catch (IOException var22) {
                     var119 = "IDL:java/io/IOEx:1.0";
                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var13.write_string(var119);
                     var13.write_value(var22, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var13;
                  }

                  var120 = var3.createReply();
                  return var120;
               } else if (var1.equals("addNotificationListeners")) {
                  ObjectName[] var111 = (ObjectName[])var5.read_value(array$Ljavax$management$ObjectName != null ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
                  MarshalledObject[] var116 = (MarshalledObject[])var5.read_value(array$Ljava$rmi$MarshalledObject != null ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
                  Subject[] var124 = (Subject[])var5.read_value(array$Ljavax$security$auth$Subject != null ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));

                  Integer[] var134;
                  try {
                     var134 = var4.addNotificationListeners(var111, var116, var124);
                  } catch (InstanceNotFoundException var84) {
                     var11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var84, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var12;
                  } catch (IOException var85) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var85, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var10.write_value(this.cast_array(var134), array$Ljava$lang$Integer != null ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                  return var10;
               }
            case 'O':
               if (var1.equals("getObjectInstance")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var101 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  ObjectInstance var122;
                  try {
                     var122 = var4.getObjectInstance(var94, var101);
                  } catch (InstanceNotFoundException var37) {
                     var107 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var37, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var115;
                  } catch (IOException var38) {
                     var107 = "IDL:java/io/IOEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var38, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var115;
                  }

                  var133 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var133.write_value(var122, (Class)(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance"))));
                  return var133;
               }
            case 'a':
               if (var1.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject")) {
                  var102 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  ObjectInstance var128;
                  try {
                     var128 = var4.createMBean(var102, var98, var97);
                  } catch (ReflectionException var64) {
                     var11 = "IDL:javax/management/ReflectionEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var64, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                     return var12;
                  } catch (InstanceAlreadyExistsException var65) {
                     var11 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var65, (Class)(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException"))));
                     return var12;
                  } catch (MBeanException var66) {
                     var11 = "IDL:javax/management/MBeanEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var66, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                     return var12;
                  } catch (NotCompliantMBeanException var67) {
                     var11 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var67, (Class)(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException"))));
                     return var12;
                  } catch (IOException var68) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var68, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var10.write_value(var128, (Class)(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance"))));
                  return var10;
               } else {
                  ObjectName var114;
                  if (var1.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
                     var102 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                     var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var114 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     Subject var126 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     ObjectInstance var139;
                     try {
                        var139 = var4.createMBean(var102, var98, var114, var126);
                     } catch (ReflectionException var39) {
                        var119 = "IDL:javax/management/ReflectionEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var39, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var13;
                     } catch (InstanceAlreadyExistsException var40) {
                        var119 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var40, (Class)(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException"))));
                        return var13;
                     } catch (MBeanException var41) {
                        var119 = "IDL:javax/management/MBeanEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var41, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                        return var13;
                     } catch (NotCompliantMBeanException var42) {
                        var119 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var42, (Class)(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException"))));
                        return var13;
                     } catch (InstanceNotFoundException var43) {
                        var119 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var43, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                        return var13;
                     } catch (IOException var44) {
                        var119 = "IDL:java/io/IOEx:1.0";
                        var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var13.write_string(var119);
                        var13.write_value(var44, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var13;
                     }

                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                     var115.write_value(var139, (Class)(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance"))));
                     return var115;
                  } else if (var1.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
                     var102 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                     var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var100 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                     var105 = (String[])var5.read_value(array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                     var113 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     ObjectInstance var142;
                     try {
                        var142 = var4.createMBean(var102, var98, var100, var105, var113);
                     } catch (ReflectionException var69) {
                        var131 = "IDL:javax/management/ReflectionEx:1.0";
                        var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var14.write_string(var131);
                        var14.write_value(var69, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var14;
                     } catch (InstanceAlreadyExistsException var70) {
                        var131 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                        var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var14.write_string(var131);
                        var14.write_value(var70, (Class)(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException"))));
                        return var14;
                     } catch (MBeanException var71) {
                        var131 = "IDL:javax/management/MBeanEx:1.0";
                        var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var14.write_string(var131);
                        var14.write_value(var71, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                        return var14;
                     } catch (NotCompliantMBeanException var72) {
                        var131 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                        var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var14.write_string(var131);
                        var14.write_value(var72, (Class)(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException"))));
                        return var14;
                     } catch (IOException var73) {
                        var131 = "IDL:java/io/IOEx:1.0";
                        var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var14.write_string(var131);
                        var14.write_value(var73, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var14;
                     }

                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                     var12.write_value(var142, (Class)(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance"))));
                     return var12;
                  } else if (var1.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
                     var102 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                     var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var114 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                     var104 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                     String[] var136 = (String[])var5.read_value(array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                     Subject var140 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                     org.omg.CORBA_2_3.portable.OutputStream var15;
                     ObjectInstance var135;
                     String var141;
                     try {
                        var135 = var4.createMBean(var102, var98, var114, var104, var136, var140);
                     } catch (ReflectionException var50) {
                        var141 = "IDL:javax/management/ReflectionEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var50, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                        return var15;
                     } catch (InstanceAlreadyExistsException var51) {
                        var141 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var51, (Class)(class$javax$management$InstanceAlreadyExistsException != null ? class$javax$management$InstanceAlreadyExistsException : (class$javax$management$InstanceAlreadyExistsException = class$("javax.management.InstanceAlreadyExistsException"))));
                        return var15;
                     } catch (MBeanException var52) {
                        var141 = "IDL:javax/management/MBeanEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var52, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                        return var15;
                     } catch (NotCompliantMBeanException var53) {
                        var141 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var53, (Class)(class$javax$management$NotCompliantMBeanException != null ? class$javax$management$NotCompliantMBeanException : (class$javax$management$NotCompliantMBeanException = class$("javax.management.NotCompliantMBeanException"))));
                        return var15;
                     } catch (InstanceNotFoundException var54) {
                        var141 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var54, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                        return var15;
                     } catch (IOException var55) {
                        var141 = "IDL:java/io/IOEx:1.0";
                        var15 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                        var15.write_string(var141);
                        var15.write_value(var55, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                        return var15;
                     }

                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                     var13.write_value(var135, (Class)(class$javax$management$ObjectInstance != null ? class$javax$management$ObjectInstance : (class$javax$management$ObjectInstance = class$("javax.management.ObjectInstance"))));
                     return var13;
                  }
               }
            case 'c':
               if (var1.equals("fetchNotifications")) {
                  long var99 = var5.read_longlong();
                  int var110 = var5.read_long();
                  long var118 = var5.read_longlong();

                  NotificationResult var137;
                  try {
                     var137 = var4.fetchNotifications(var99, var110, var118);
                  } catch (IOException var18) {
                     var131 = "IDL:java/io/IOEx:1.0";
                     var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var14.write_string(var131);
                     var14.write_value(var18, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var14;
                  }

                  var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var12.write_value(var137, (Class)(class$javax$management$remote$NotificationResult != null ? class$javax$management$remote$NotificationResult : (class$javax$management$remote$NotificationResult = class$("javax.management.remote.NotificationResult"))));
                  return var12;
               }
            case 'e':
               if (var1.equals("unregisterMBean")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var101 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var4.unregisterMBean(var94, var101);
                  } catch (InstanceNotFoundException var34) {
                     var112 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var34, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var10;
                  } catch (MBeanRegistrationException var35) {
                     var112 = "IDL:javax/management/MBeanRegistrationEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var35, (Class)(class$javax$management$MBeanRegistrationException != null ? class$javax$management$MBeanRegistrationException : (class$javax$management$MBeanRegistrationException = class$("javax.management.MBeanRegistrationException"))));
                     return var10;
                  } catch (IOException var36) {
                     var112 = "IDL:java/io/IOEx:1.0";
                     var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var10.write_string(var112);
                     var10.write_value(var36, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var10;
                  }

                  OutputStream var109 = var3.createReply();
                  return var109;
               } else if (var1.equals("isRegistered")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var101 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  boolean var106;
                  try {
                     var106 = var4.isRegistered(var94, var101);
                  } catch (IOException var29) {
                     var107 = "IDL:java/io/IOEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var29, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var115;
                  }

                  var103 = var3.createReply();
                  var103.write_boolean(var106);
                  return var103;
               }
            case 'n':
               if (var1.equals("isInstanceOf")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var7 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  boolean var108;
                  try {
                     var108 = var4.isInstanceOf(var94, var7, var97);
                  } catch (InstanceNotFoundException var26) {
                     var11 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var26, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var12;
                  } catch (IOException var27) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var27, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var125 = var3.createReply();
                  var125.write_boolean(var108);
                  return var125;
               }
            case 'o':
               if (var1.equals("invoke")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var7 = (String)var5.read_value(class$java$lang$String != null ? class$java$lang$String : (class$java$lang$String = class$("java.lang.String")));
                  var100 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var105 = (String[])var5.read_value(array$Ljava$lang$String != null ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
                  var113 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  Object var130;
                  try {
                     var130 = var4.invoke(var94, var7, var100, var105, var113);
                  } catch (InstanceNotFoundException var30) {
                     var131 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var14.write_string(var131);
                     var14.write_value(var30, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var14;
                  } catch (MBeanException var31) {
                     var131 = "IDL:javax/management/MBeanEx:1.0";
                     var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var14.write_string(var131);
                     var14.write_value(var31, (Class)(class$javax$management$MBeanException != null ? class$javax$management$MBeanException : (class$javax$management$MBeanException = class$("javax.management.MBeanException"))));
                     return var14;
                  } catch (ReflectionException var32) {
                     var131 = "IDL:javax/management/ReflectionEx:1.0";
                     var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var14.write_string(var131);
                     var14.write_value(var32, (Class)(class$javax$management$ReflectionException != null ? class$javax$management$ReflectionException : (class$javax$management$ReflectionException = class$("javax.management.ReflectionException"))));
                     return var14;
                  } catch (IOException var33) {
                     var131 = "IDL:java/io/IOEx:1.0";
                     var14 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var14.write_string(var131);
                     var14.write_value(var33, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var14;
                  }

                  OutputStream var127 = var3.createReply();
                  Util.writeAny(var127, var130);
                  return var127;
               } else if (var1.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var4.removeNotificationListener(var94, var98, var97);
                  } catch (InstanceNotFoundException var89) {
                     var107 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var89, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var115;
                  } catch (ListenerNotFoundException var90) {
                     var107 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var90, (Class)(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException"))));
                     return var115;
                  } catch (IOException var91) {
                     var107 = "IDL:java/io/IOEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var91, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var115;
                  }

                  var103 = var3.createReply();
                  return var103;
               } else if (var1.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var98 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var100 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var104 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var113 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var4.removeNotificationListener(var94, var98, var100, var104, var113);
                  } catch (InstanceNotFoundException var23) {
                     var119 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var13.write_string(var119);
                     var13.write_value(var23, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var13;
                  } catch (ListenerNotFoundException var24) {
                     var119 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var13.write_string(var119);
                     var13.write_value(var24, (Class)(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException"))));
                     return var13;
                  } catch (IOException var25) {
                     var119 = "IDL:java/io/IOEx:1.0";
                     var13 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var13.write_string(var119);
                     var13.write_value(var25, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var13;
                  }

                  var120 = var3.createReply();
                  return var120;
               } else if (var1.equals("removeNotificationListeners")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  Integer[] var96 = (Integer[])var5.read_value(array$Ljava$lang$Integer != null ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var4.removeNotificationListeners(var94, var96, var97);
                  } catch (InstanceNotFoundException var86) {
                     var107 = "IDL:javax/management/InstanceNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var86, (Class)(class$javax$management$InstanceNotFoundException != null ? class$javax$management$InstanceNotFoundException : (class$javax$management$InstanceNotFoundException = class$("javax.management.InstanceNotFoundException"))));
                     return var115;
                  } catch (ListenerNotFoundException var87) {
                     var107 = "IDL:javax/management/ListenerNotFoundEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var87, (Class)(class$javax$management$ListenerNotFoundException != null ? class$javax$management$ListenerNotFoundException : (class$javax$management$ListenerNotFoundException = class$("javax.management.ListenerNotFoundException"))));
                     return var115;
                  } catch (IOException var88) {
                     var107 = "IDL:java/io/IOEx:1.0";
                     var115 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var115.write_string(var107);
                     var115.write_value(var88, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var115;
                  }

                  var103 = var3.createReply();
                  return var103;
               }
            case 'r':
               Set var9;
               if (var1.equals("queryMBeans")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var95 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var9 = var4.queryMBeans(var94, var95, var97);
                  } catch (IOException var17) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var17, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var10.write_value((Serializable)var9, class$java$util$Set != null ? class$java$util$Set : (class$java$util$Set = class$("java.util.Set")));
                  return var10;
               } else if (var1.equals("queryNames")) {
                  var94 = (ObjectName)var5.read_value(class$javax$management$ObjectName != null ? class$javax$management$ObjectName : (class$javax$management$ObjectName = class$("javax.management.ObjectName")));
                  var95 = (MarshalledObject)var5.read_value(class$java$rmi$MarshalledObject != null ? class$java$rmi$MarshalledObject : (class$java$rmi$MarshalledObject = class$("java.rmi.MarshalledObject")));
                  var97 = (Subject)var5.read_value(class$javax$security$auth$Subject != null ? class$javax$security$auth$Subject : (class$javax$security$auth$Subject = class$("javax.security.auth.Subject")));

                  try {
                     var9 = var4.queryNames(var94, var95, var97);
                  } catch (IOException var28) {
                     var11 = "IDL:java/io/IOEx:1.0";
                     var12 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var12.write_string(var11);
                     var12.write_value(var28, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var12;
                  }

                  var10 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createReply();
                  var10.write_value((Serializable)var9, class$java$util$Set != null ? class$java$util$Set : (class$java$util$Set = class$("java.util.Set")));
                  return var10;
               }
            case 's':
               if (var1.equals("close")) {
                  try {
                     var4.close();
                  } catch (IOException var16) {
                     var7 = "IDL:java/io/IOEx:1.0";
                     var8 = (org.omg.CORBA_2_3.portable.OutputStream)var3.createExceptionReply();
                     var8.write_string(var7);
                     var8.write_value(var16, (Class)(class$java$io$IOException != null ? class$java$io$IOException : (class$java$io$IOException = class$("java.io.IOException"))));
                     return var8;
                  }

                  OutputStream var6 = var3.createReply();
                  return var6;
               }
            default:
               throw new BAD_OPERATION();
            }
         }
      } catch (SystemException var92) {
         throw var92;
      } catch (Throwable var93) {
         throw new UnknownException(var93);
      }
   }

   private Serializable cast_array(Object var1) {
      return (Serializable)var1;
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public void deactivate() {
      this._orb().disconnect(this);
      this._set_delegate((Delegate)null);
      this.target = null;
   }

   public Remote getTarget() {
      return this.target;
   }

   public ORB orb() {
      return this._orb();
   }

   public void orb(ORB var1) {
      var1.connect(this);
   }

   public void setTarget(Remote var1) {
      this.target = (RMIConnectionImpl)var1;
   }

   public org.omg.CORBA.Object thisObject() {
      return this;
   }
}
