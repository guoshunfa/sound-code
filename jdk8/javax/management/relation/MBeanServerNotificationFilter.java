package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;

public class MBeanServerNotificationFilter extends NotificationFilterSupport {
   private static final long oldSerialVersionUID = 6001782699077323605L;
   private static final long newSerialVersionUID = 2605900539589789736L;
   private static final ObjectStreamField[] oldSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("mySelectObjNameList", Vector.class), new ObjectStreamField("myDeselectObjNameList", Vector.class)};
   private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("selectedNames", List.class), new ObjectStreamField("deselectedNames", List.class)};
   private static final long serialVersionUID;
   private static final ObjectStreamField[] serialPersistentFields;
   private static boolean compat = false;
   private List<ObjectName> selectedNames = new Vector();
   private List<ObjectName> deselectedNames = null;

   public MBeanServerNotificationFilter() {
      JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
      this.enableType("JMX.mbean.registered");
      this.enableType("JMX.mbean.unregistered");
      JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
   }

   public synchronized void disableAllObjectNames() {
      JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
      this.selectedNames = new Vector();
      this.deselectedNames = null;
      JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
   }

   public synchronized void disableObjectName(ObjectName var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableObjectName", (Object)var1);
         if (this.selectedNames != null && this.selectedNames.size() != 0) {
            this.selectedNames.remove(var1);
         }

         if (this.deselectedNames != null && !this.deselectedNames.contains(var1)) {
            this.deselectedNames.add(var1);
         }

         JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableObjectName");
      }
   }

   public synchronized void enableAllObjectNames() {
      JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
      this.selectedNames = null;
      this.deselectedNames = new Vector();
      JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
   }

   public synchronized void enableObjectName(ObjectName var1) throws IllegalArgumentException {
      if (var1 == null) {
         String var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableObjectName", (Object)var1);
         if (this.deselectedNames != null && this.deselectedNames.size() != 0) {
            this.deselectedNames.remove(var1);
         }

         if (this.selectedNames != null && !this.selectedNames.contains(var1)) {
            this.selectedNames.add(var1);
         }

         JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableObjectName");
      }
   }

   public synchronized Vector<ObjectName> getEnabledObjectNames() {
      return this.selectedNames != null ? new Vector(this.selectedNames) : null;
   }

   public synchronized Vector<ObjectName> getDisabledObjectNames() {
      return this.deselectedNames != null ? new Vector(this.deselectedNames) : null;
   }

   public synchronized boolean isNotificationEnabled(Notification var1) throws IllegalArgumentException {
      String var2;
      if (var1 == null) {
         var2 = "Invalid parameter.";
         throw new IllegalArgumentException(var2);
      } else {
         JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", (Object)var1);
         var2 = var1.getType();
         Vector var3 = this.getEnabledTypes();
         if (!var3.contains(var2)) {
            JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "Type not selected, exiting");
            return false;
         } else {
            MBeanServerNotification var4 = (MBeanServerNotification)var1;
            ObjectName var5 = var4.getMBeanName();
            boolean var6 = false;
            if (this.selectedNames != null) {
               if (this.selectedNames.size() == 0) {
                  JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "No ObjectNames selected, exiting");
                  return false;
               }

               var6 = this.selectedNames.contains(var5);
               if (!var6) {
                  JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not in selected list, exiting");
                  return false;
               }
            }

            if (!var6) {
               if (this.deselectedNames == null) {
                  JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not selected, and all names deselected, exiting");
                  return false;
               }

               if (this.deselectedNames.contains(var5)) {
                  JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName explicitly not selected, exiting");
                  return false;
               }
            }

            JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName selected, exiting");
            return true;
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (compat) {
         ObjectInputStream.GetField var2 = var1.readFields();
         this.selectedNames = (List)Util.cast(var2.get("mySelectObjNameList", (Object)null));
         if (var2.defaulted("mySelectObjNameList")) {
            throw new NullPointerException("mySelectObjNameList");
         }

         this.deselectedNames = (List)Util.cast(var2.get("myDeselectObjNameList", (Object)null));
         if (var2.defaulted("myDeselectObjNameList")) {
            throw new NullPointerException("myDeselectObjNameList");
         }
      } else {
         var1.defaultReadObject();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (compat) {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("mySelectObjNameList", this.selectedNames);
         var2.put("myDeselectObjNameList", this.deselectedNames);
         var1.writeFields();
      } else {
         var1.defaultWriteObject();
      }

   }

   static {
      try {
         GetPropertyAction var0 = new GetPropertyAction("jmx.serial.form");
         String var1 = (String)AccessController.doPrivileged((PrivilegedAction)var0);
         compat = var1 != null && var1.equals("1.0");
      } catch (Exception var2) {
      }

      if (compat) {
         serialPersistentFields = oldSerialPersistentFields;
         serialVersionUID = 6001782699077323605L;
      } else {
         serialPersistentFields = newSerialPersistentFields;
         serialVersionUID = 2605900539589789736L;
      }

   }
}
