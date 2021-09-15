package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import java.util.Hashtable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class JVM_MANAGEMENT_MIB extends SnmpMib implements Serializable {
   static final long serialVersionUID = 6895037919735816732L;
   private boolean isInitialized = false;
   protected SnmpStandardObjectServer objectserver;
   protected final Hashtable<String, SnmpMibTable> metadatas = new Hashtable();

   public JVM_MANAGEMENT_MIB() {
      this.mibName = "JVM_MANAGEMENT_MIB";
   }

   public void init() throws IllegalAccessException {
      if (!this.isInitialized) {
         try {
            this.populate((MBeanServer)null, (ObjectName)null);
         } catch (IllegalAccessException var2) {
            throw var2;
         } catch (RuntimeException var3) {
            throw var3;
         } catch (Exception var4) {
            throw new Error(var4.getMessage());
         }

         this.isInitialized = true;
      }
   }

   public ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception {
      if (this.isInitialized) {
         throw new InstanceAlreadyExistsException();
      } else {
         this.server = var1;
         this.populate(var1, var2);
         this.isInitialized = true;
         return var2;
      }
   }

   public void populate(MBeanServer var1, ObjectName var2) throws Exception {
      if (!this.isInitialized) {
         if (this.objectserver == null) {
            this.objectserver = new SnmpStandardObjectServer();
         }

         this.initJvmOS(var1);
         this.initJvmCompilation(var1);
         this.initJvmRuntime(var1);
         this.initJvmThreading(var1);
         this.initJvmMemory(var1);
         this.initJvmClassLoading(var1);
         this.isInitialized = true;
      }
   }

   protected void initJvmOS(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmOS", "1.3.6.1.4.1.42.2.145.3.163.1.1.6");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmOS", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmOS");
      }

      JvmOSMeta var4 = this.createJvmOSMetaNode("JvmOS", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmOSMBean var5 = (JvmOSMBean)this.createJvmOSMBean("JvmOS", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmOS", var2, var3, var4, var5, var1);
      }

   }

   protected JvmOSMeta createJvmOSMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmOSMeta(this, this.objectserver);
   }

   protected abstract Object createJvmOSMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   protected void initJvmCompilation(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmCompilation", "1.3.6.1.4.1.42.2.145.3.163.1.1.5");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmCompilation", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmCompilation");
      }

      JvmCompilationMeta var4 = this.createJvmCompilationMetaNode("JvmCompilation", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmCompilationMBean var5 = (JvmCompilationMBean)this.createJvmCompilationMBean("JvmCompilation", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmCompilation", var2, var3, var4, var5, var1);
      }

   }

   protected JvmCompilationMeta createJvmCompilationMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmCompilationMeta(this, this.objectserver);
   }

   protected abstract Object createJvmCompilationMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   protected void initJvmRuntime(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmRuntime", "1.3.6.1.4.1.42.2.145.3.163.1.1.4");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmRuntime", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmRuntime");
      }

      JvmRuntimeMeta var4 = this.createJvmRuntimeMetaNode("JvmRuntime", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmRuntimeMBean var5 = (JvmRuntimeMBean)this.createJvmRuntimeMBean("JvmRuntime", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmRuntime", var2, var3, var4, var5, var1);
      }

   }

   protected JvmRuntimeMeta createJvmRuntimeMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmRuntimeMeta(this, this.objectserver);
   }

   protected abstract Object createJvmRuntimeMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   protected void initJvmThreading(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmThreading", "1.3.6.1.4.1.42.2.145.3.163.1.1.3");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmThreading", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmThreading");
      }

      JvmThreadingMeta var4 = this.createJvmThreadingMetaNode("JvmThreading", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmThreadingMBean var5 = (JvmThreadingMBean)this.createJvmThreadingMBean("JvmThreading", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmThreading", var2, var3, var4, var5, var1);
      }

   }

   protected JvmThreadingMeta createJvmThreadingMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmThreadingMeta(this, this.objectserver);
   }

   protected abstract Object createJvmThreadingMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   protected void initJvmMemory(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmMemory", "1.3.6.1.4.1.42.2.145.3.163.1.1.2");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmMemory", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmMemory");
      }

      JvmMemoryMeta var4 = this.createJvmMemoryMetaNode("JvmMemory", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmMemoryMBean var5 = (JvmMemoryMBean)this.createJvmMemoryMBean("JvmMemory", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmMemory", var2, var3, var4, var5, var1);
      }

   }

   protected JvmMemoryMeta createJvmMemoryMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmMemoryMeta(this, this.objectserver);
   }

   protected abstract Object createJvmMemoryMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   protected void initJvmClassLoading(MBeanServer var1) throws Exception {
      String var2 = this.getGroupOid("JvmClassLoading", "1.3.6.1.4.1.42.2.145.3.163.1.1.1");
      ObjectName var3 = null;
      if (var1 != null) {
         var3 = this.getGroupObjectName("JvmClassLoading", var2, this.mibName + ":name=sun.management.snmp.jvmmib.JvmClassLoading");
      }

      JvmClassLoadingMeta var4 = this.createJvmClassLoadingMetaNode("JvmClassLoading", var2, var3, var1);
      if (var4 != null) {
         var4.registerTableNodes(this, var1);
         JvmClassLoadingMBean var5 = (JvmClassLoadingMBean)this.createJvmClassLoadingMBean("JvmClassLoading", var2, var3, var1);
         var4.setInstance(var5);
         this.registerGroupNode("JvmClassLoading", var2, var3, var4, var5, var1);
      }

   }

   protected JvmClassLoadingMeta createJvmClassLoadingMetaNode(String var1, String var2, ObjectName var3, MBeanServer var4) {
      return new JvmClassLoadingMeta(this, this.objectserver);
   }

   protected abstract Object createJvmClassLoadingMBean(String var1, String var2, ObjectName var3, MBeanServer var4);

   public void registerTableMeta(String var1, SnmpMibTable var2) {
      if (this.metadatas != null) {
         if (var1 != null) {
            this.metadatas.put(var1, var2);
         }
      }
   }

   public SnmpMibTable getRegisteredTableMeta(String var1) {
      if (this.metadatas == null) {
         return null;
      } else {
         return var1 == null ? null : (SnmpMibTable)this.metadatas.get(var1);
      }
   }

   public SnmpStandardObjectServer getStandardObjectServer() {
      if (this.objectserver == null) {
         this.objectserver = new SnmpStandardObjectServer();
      }

      return this.objectserver;
   }
}
