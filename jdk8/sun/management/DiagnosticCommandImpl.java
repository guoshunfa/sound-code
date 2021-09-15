package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.Permission;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

class DiagnosticCommandImpl extends NotificationEmitterSupport implements DiagnosticCommandMBean {
   private final VMManagement jvm;
   private volatile Map<String, DiagnosticCommandImpl.Wrapper> wrappers = null;
   private static final String strClassName = "".getClass().getName();
   private static final String strArrayClassName = String[].class.getName();
   private final boolean isSupported;
   private static final String notifName = "javax.management.Notification";
   private static final String[] diagFramNotifTypes = new String[]{"jmx.mbean.info.changed"};
   private MBeanNotificationInfo[] notifInfo = null;
   private static long seqNumber = 0L;

   public Object getAttribute(String var1) throws AttributeNotFoundException, MBeanException, ReflectionException {
      throw new AttributeNotFoundException(var1);
   }

   public void setAttribute(Attribute var1) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
      throw new AttributeNotFoundException(var1.getName());
   }

   public AttributeList getAttributes(String[] var1) {
      return new AttributeList();
   }

   public AttributeList setAttributes(AttributeList var1) {
      return new AttributeList();
   }

   DiagnosticCommandImpl(VMManagement var1) {
      this.jvm = var1;
      this.isSupported = var1.isRemoteDiagnosticCommandsSupported();
   }

   public MBeanInfo getMBeanInfo() {
      TreeSet var1 = new TreeSet(new DiagnosticCommandImpl.OperationInfoComparator());
      Object var2;
      if (!this.isSupported) {
         var2 = Collections.EMPTY_MAP;
      } else {
         try {
            String[] var3 = this.getDiagnosticCommands();
            DiagnosticCommandInfo[] var4 = this.getDiagnosticCommandInfo(var3);
            MBeanParameterInfo[] var5 = new MBeanParameterInfo[]{new MBeanParameterInfo("arguments", strArrayClassName, "Array of Diagnostic Commands Arguments and Options")};
            var2 = new HashMap();

            for(int var6 = 0; var6 < var3.length; ++var6) {
               String var7 = transform(var3[var6]);

               try {
                  DiagnosticCommandImpl.Wrapper var8 = new DiagnosticCommandImpl.Wrapper(var7, var3[var6], var4[var6]);
                  ((Map)var2).put(var7, var8);
                  var1.add(new MBeanOperationInfo(var8.name, var8.info.getDescription(), var8.info.getArgumentsInfo() != null && !var8.info.getArgumentsInfo().isEmpty() ? var5 : null, strClassName, 2, this.commandDescriptor(var8)));
               } catch (InstantiationException var9) {
               }
            }
         } catch (UnsupportedOperationException | IllegalArgumentException var10) {
            var2 = Collections.EMPTY_MAP;
         }
      }

      this.wrappers = Collections.unmodifiableMap((Map)var2);
      HashMap var11 = new HashMap();
      var11.put("immutableInfo", "false");
      var11.put("interfaceClassName", "com.sun.management.DiagnosticCommandMBean");
      var11.put("mxbean", "false");
      ImmutableDescriptor var12 = new ImmutableDescriptor(var11);
      return new MBeanInfo(this.getClass().getName(), "Diagnostic Commands", (MBeanAttributeInfo[])null, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])var1.toArray(new MBeanOperationInfo[var1.size()]), this.getNotificationInfo(), var12);
   }

   public Object invoke(String var1, Object[] var2, String[] var3) throws MBeanException, ReflectionException {
      if (!this.isSupported) {
         throw new UnsupportedOperationException();
      } else {
         if (this.wrappers == null) {
            this.getMBeanInfo();
         }

         DiagnosticCommandImpl.Wrapper var4 = (DiagnosticCommandImpl.Wrapper)this.wrappers.get(var1);
         if (var4 != null) {
            if (var4.info.getArgumentsInfo().isEmpty() && (var2 == null || var2.length == 0) && (var3 == null || var3.length == 0)) {
               return var4.execute((String[])null);
            }

            if (var2 != null && var2.length == 1 && var3 != null && var3.length == 1 && var3[0] != null && var3[0].compareTo(strArrayClassName) == 0) {
               return var4.execute((String[])((String[])var2[0]));
            }
         }

         throw new ReflectionException(new NoSuchMethodException(var1));
      }
   }

   private static String transform(String var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = true;
      boolean var3 = false;

      for(int var4 = 0; var4 < var0.length(); ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 != '.' && var5 != '_') {
            if (var3) {
               var3 = false;
               var1.append(Character.toUpperCase(var5));
            } else if (var2) {
               var1.append(Character.toLowerCase(var5));
            } else {
               var1.append(var5);
            }
         } else {
            var2 = false;
            var3 = true;
         }
      }

      return var1.toString();
   }

   private Descriptor commandDescriptor(DiagnosticCommandImpl.Wrapper var1) throws IllegalArgumentException {
      HashMap var2 = new HashMap();
      var2.put("dcmd.name", var1.info.getName());
      var2.put("dcmd.description", var1.info.getDescription());
      var2.put("dcmd.vmImpact", var1.info.getImpact());
      var2.put("dcmd.permissionClass", var1.info.getPermissionClass());
      var2.put("dcmd.permissionName", var1.info.getPermissionName());
      var2.put("dcmd.permissionAction", var1.info.getPermissionAction());
      var2.put("dcmd.enabled", var1.info.isEnabled());
      StringBuilder var3 = new StringBuilder();
      var3.append("help ");
      var3.append(var1.info.getName());
      var2.put("dcmd.help", this.executeDiagnosticCommand(var3.toString()));
      if (var1.info.getArgumentsInfo() != null && !var1.info.getArgumentsInfo().isEmpty()) {
         HashMap var4 = new HashMap();

         DiagnosticCommandArgumentInfo var6;
         HashMap var7;
         for(Iterator var5 = var1.info.getArgumentsInfo().iterator(); var5.hasNext(); var4.put(var6.getName(), new ImmutableDescriptor(var7))) {
            var6 = (DiagnosticCommandArgumentInfo)var5.next();
            var7 = new HashMap();
            var7.put("dcmd.arg.name", var6.getName());
            var7.put("dcmd.arg.type", var6.getType());
            var7.put("dcmd.arg.description", var6.getDescription());
            var7.put("dcmd.arg.isMandatory", var6.isMandatory());
            var7.put("dcmd.arg.isMultiple", var6.isMultiple());
            boolean var8 = var6.isOption();
            var7.put("dcmd.arg.isOption", var8);
            if (!var8) {
               var7.put("dcmd.arg.position", var6.getPosition());
            } else {
               var7.put("dcmd.arg.position", -1);
            }
         }

         var2.put("dcmd.arguments", new ImmutableDescriptor(var4));
      }

      return new ImmutableDescriptor(var2);
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      synchronized(this) {
         if (this.notifInfo == null) {
            this.notifInfo = new MBeanNotificationInfo[1];
            this.notifInfo[0] = new MBeanNotificationInfo(diagFramNotifTypes, "javax.management.Notification", "Diagnostic Framework Notification");
         }
      }

      return (MBeanNotificationInfo[])this.notifInfo.clone();
   }

   private static long getNextSeqNumber() {
      return ++seqNumber;
   }

   private void createDiagnosticFrameworkNotification() {
      if (this.hasListeners()) {
         ObjectName var1 = null;

         try {
            var1 = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
         } catch (MalformedObjectNameException var3) {
         }

         Notification var2 = new Notification("jmx.mbean.info.changed", var1, getNextSeqNumber());
         var2.setUserData(this.getMBeanInfo());
         this.sendNotification(var2);
      }
   }

   public synchronized void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      boolean var4 = this.hasListeners();
      super.addNotificationListener(var1, var2, var3);
      boolean var5 = this.hasListeners();
      if (!var4 && var5) {
         this.setNotificationEnabled(true);
      }

   }

   public synchronized void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      boolean var2 = this.hasListeners();
      super.removeNotificationListener(var1);
      boolean var3 = this.hasListeners();
      if (var2 && !var3) {
         this.setNotificationEnabled(false);
      }

   }

   public synchronized void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      boolean var4 = this.hasListeners();
      super.removeNotificationListener(var1, var2, var3);
      boolean var5 = this.hasListeners();
      if (var4 && !var5) {
         this.setNotificationEnabled(false);
      }

   }

   private native void setNotificationEnabled(boolean var1);

   private native String[] getDiagnosticCommands();

   private native DiagnosticCommandInfo[] getDiagnosticCommandInfo(String[] var1);

   private native String executeDiagnosticCommand(String var1);

   private static class OperationInfoComparator implements Comparator<MBeanOperationInfo> {
      private OperationInfoComparator() {
      }

      public int compare(MBeanOperationInfo var1, MBeanOperationInfo var2) {
         return var1.getName().compareTo(var2.getName());
      }

      // $FF: synthetic method
      OperationInfoComparator(Object var1) {
         this();
      }
   }

   private class Wrapper {
      String name;
      String cmd;
      DiagnosticCommandInfo info;
      Permission permission;

      Wrapper(String var2, String var3, DiagnosticCommandInfo var4) throws InstantiationException {
         this.name = var2;
         this.cmd = var3;
         this.info = var4;
         this.permission = null;
         InstantiationException var5 = null;
         if (var4.getPermissionClass() != null) {
            try {
               Class var6 = Class.forName(var4.getPermissionClass());
               Constructor var7;
               if (var4.getPermissionAction() == null) {
                  try {
                     var7 = var6.getConstructor(String.class);
                     this.permission = (Permission)var7.newInstance(var4.getPermissionName());
                  } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var9) {
                     var5 = var9;
                  }
               }

               if (this.permission == null) {
                  try {
                     var7 = var6.getConstructor(String.class, String.class);
                     this.permission = (Permission)var7.newInstance(var4.getPermissionName(), var4.getPermissionAction());
                  } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var8) {
                     var5 = var8;
                  }
               }
            } catch (ClassNotFoundException var10) {
            }

            if (this.permission == null) {
               InstantiationException var11 = new InstantiationException("Unable to instantiate required permission");
               var11.initCause(var5);
            }
         }

      }

      public String execute(String[] var1) {
         if (this.permission != null) {
            SecurityManager var2 = System.getSecurityManager();
            if (var2 != null) {
               var2.checkPermission(this.permission);
            }
         }

         if (var1 == null) {
            return DiagnosticCommandImpl.this.executeDiagnosticCommand(this.cmd);
         } else {
            StringBuilder var4 = new StringBuilder();
            var4.append(this.cmd);

            for(int var3 = 0; var3 < var1.length; ++var3) {
               if (var1[var3] == null) {
                  throw new IllegalArgumentException("Invalid null argument");
               }

               var4.append(" ");
               var4.append(var1[var3]);
            }

            return DiagnosticCommandImpl.this.executeDiagnosticCommand(var4.toString());
         }
      }
   }
}
