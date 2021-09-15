package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.external.amx.AMXGlassfish;
import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.InheritedAttributes;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.org.glassfish.gmbal.ManagedObjectManagerFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.Stub;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.ws.WebServiceFeature;

public abstract class MonitorBase {
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
   private static ManagementAssertion.Setting clientMonitoring;
   private static ManagementAssertion.Setting endpointMonitoring;
   private static int typelibDebug;
   private static String registrationDebug;
   private static boolean runtimeDebug;
   private static int maxUniqueEndpointRootNameRetries;
   private static final String monitorProperty = "com.sun.xml.internal.ws.monitoring.";

   @NotNull
   public ManagedObjectManager createManagedObjectManager(WSEndpoint endpoint) {
      String rootName = endpoint.getServiceName().getLocalPart() + "-" + endpoint.getPortName().getLocalPart();
      if (rootName.equals("-")) {
         rootName = "provider";
      }

      String contextPath = this.getContextPath(endpoint);
      if (contextPath != null) {
         rootName = contextPath + "-" + rootName;
      }

      ManagedServiceAssertion assertion = ManagedServiceAssertion.getAssertion(endpoint);
      if (assertion != null) {
         String id = assertion.getId();
         if (id != null) {
            rootName = id;
         }

         if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
            return this.disabled("This endpoint", rootName);
         }
      }

      return endpointMonitoring.equals(ManagementAssertion.Setting.OFF) ? this.disabled("Global endpoint", rootName) : this.createMOMLoop(rootName, 0);
   }

   private String getContextPath(WSEndpoint endpoint) {
      try {
         Container container = endpoint.getContainer();
         Method getSPI = container.getClass().getDeclaredMethod("getSPI", Class.class);
         getSPI.setAccessible(true);
         Class servletContextClass = Class.forName("javax.servlet.ServletContext");
         Object servletContext = getSPI.invoke(container, servletContextClass);
         if (servletContext != null) {
            Method getContextPath = servletContextClass.getDeclaredMethod("getContextPath");
            getContextPath.setAccessible(true);
            return (String)getContextPath.invoke(servletContext);
         } else {
            return null;
         }
      } catch (Throwable var7) {
         logger.log(Level.FINEST, "getContextPath", var7);
         return null;
      }
   }

   @NotNull
   public ManagedObjectManager createManagedObjectManager(Stub stub) {
      EndpointAddress ea = stub.requestContext.getEndpointAddress();
      if (ea == null) {
         return ManagedObjectManagerFactory.createNOOP();
      } else {
         String rootName = ea.toString();
         ManagedClientAssertion assertion = ManagedClientAssertion.getAssertion(stub.getPortInfo());
         if (assertion != null) {
            String id = assertion.getId();
            if (id != null) {
               rootName = id;
            }

            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
               return this.disabled("This client", rootName);
            }

            if (assertion.monitoringAttribute() == ManagementAssertion.Setting.ON && clientMonitoring != ManagementAssertion.Setting.OFF) {
               return this.createMOMLoop(rootName, 0);
            }
         }

         return clientMonitoring != ManagementAssertion.Setting.NOT_SET && clientMonitoring != ManagementAssertion.Setting.OFF ? this.createMOMLoop(rootName, 0) : this.disabled("Global client", rootName);
      }
   }

   @NotNull
   private ManagedObjectManager disabled(String x, String rootName) {
      String msg = x + " monitoring disabled. " + rootName + " will not be monitored";
      logger.log(Level.CONFIG, msg);
      return ManagedObjectManagerFactory.createNOOP();
   }

   @NotNull
   private ManagedObjectManager createMOMLoop(String rootName, int unique) {
      boolean isFederated = AMXGlassfish.getGlassfishVersion() != null;
      ManagedObjectManager mom = this.createMOM(isFederated);
      mom = this.initMOM(mom);
      mom = this.createRoot(mom, rootName, unique);
      return mom;
   }

   @NotNull
   private ManagedObjectManager createMOM(boolean isFederated) {
      try {
         return new RewritingMOM(isFederated ? ManagedObjectManagerFactory.createFederated(AMXGlassfish.DEFAULT.serverMon(AMXGlassfish.DEFAULT.dasName())) : ManagedObjectManagerFactory.createStandalone("com.sun.metro"));
      } catch (Throwable var3) {
         if (isFederated) {
            logger.log(Level.CONFIG, "Problem while attempting to federate with GlassFish AMX monitoring.  Trying standalone.", var3);
            return this.createMOM(false);
         } else {
            logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", var3);
            return ManagedObjectManagerFactory.createNOOP();
         }
      }
   }

   @NotNull
   private ManagedObjectManager initMOM(ManagedObjectManager mom) {
      try {
         if (typelibDebug != -1) {
            mom.setTypelibDebug(typelibDebug);
         }

         if (registrationDebug.equals("FINE")) {
            mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.FINE);
         } else if (registrationDebug.equals("NORMAL")) {
            mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NORMAL);
         } else {
            mom.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
         }

         mom.setRuntimeDebug(runtimeDebug);
         mom.suppressDuplicateRootReport(true);
         mom.stripPrefix("com.sun.xml.internal.ws.server", "com.sun.xml.internal.ws.rx.rm.runtime.sequence");
         mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(ManagedData.class));
         mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(Description.class));
         mom.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(InheritedAttributes.class));
         mom.suspendJMXRegistration();
         return mom;
      } catch (Throwable var5) {
         try {
            mom.close();
         } catch (IOException var4) {
            logger.log(Level.CONFIG, (String)"Ignoring exception caught when closing unused ManagedObjectManager", (Throwable)var4);
         }

         logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", var5);
         return ManagedObjectManagerFactory.createNOOP();
      }
   }

   private ManagedObjectManager createRoot(ManagedObjectManager mom, String rootName, int unique) {
      String name = rootName + (unique == 0 ? "" : "-" + String.valueOf(unique));

      try {
         Object ignored = mom.createRoot(this, name);
         if (ignored != null) {
            ObjectName ignoredName = mom.getObjectName(mom.getRoot());
            if (ignoredName != null) {
               logger.log(Level.INFO, (String)"Metro monitoring rootname successfully set to: {0}", (Object)ignoredName);
            }

            return mom;
         } else {
            try {
               mom.close();
            } catch (IOException var8) {
               logger.log(Level.CONFIG, (String)"Ignoring exception caught when closing unused ManagedObjectManager", (Throwable)var8);
            }

            String basemsg = "Duplicate Metro monitoring rootname: " + name + " : ";
            String msg;
            if (unique > maxUniqueEndpointRootNameRetries) {
               msg = basemsg + "Giving up.";
               logger.log(Level.INFO, msg);
               return ManagedObjectManagerFactory.createNOOP();
            } else {
               msg = basemsg + "Will try to make unique";
               logger.log(Level.CONFIG, msg);
               ++unique;
               return this.createMOMLoop(rootName, unique);
            }
         }
      } catch (Throwable var9) {
         logger.log(Level.WARNING, "Error while creating monitoring root with name: " + rootName, var9);
         return ManagedObjectManagerFactory.createNOOP();
      }
   }

   private static ManagementAssertion.Setting propertyToSetting(String propName) {
      String s = System.getProperty(propName);
      if (s == null) {
         return ManagementAssertion.Setting.NOT_SET;
      } else {
         s = s.toLowerCase();
         if (!s.equals("false") && !s.equals("off")) {
            return !s.equals("true") && !s.equals("on") ? ManagementAssertion.Setting.NOT_SET : ManagementAssertion.Setting.ON;
         } else {
            return ManagementAssertion.Setting.OFF;
         }
      }
   }

   static {
      clientMonitoring = ManagementAssertion.Setting.NOT_SET;
      endpointMonitoring = ManagementAssertion.Setting.NOT_SET;
      typelibDebug = -1;
      registrationDebug = "NONE";
      runtimeDebug = false;
      maxUniqueEndpointRootNameRetries = 100;

      try {
         endpointMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.endpoint");
         clientMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.client");
         Integer i = Integer.getInteger("com.sun.xml.internal.ws.monitoring.typelibDebug");
         if (i != null) {
            typelibDebug = i;
         }

         String s = System.getProperty("com.sun.xml.internal.ws.monitoring.registrationDebug");
         if (s != null) {
            registrationDebug = s.toUpperCase();
         }

         s = System.getProperty("com.sun.xml.internal.ws.monitoring.runtimeDebug");
         if (s != null && s.toLowerCase().equals("true")) {
            runtimeDebug = true;
         }

         i = Integer.getInteger("com.sun.xml.internal.ws.monitoring.maxUniqueEndpointRootNameRetries");
         if (i != null) {
            maxUniqueEndpointRootNameRetries = i;
         }
      } catch (Exception var2) {
         logger.log(Level.WARNING, (String)"Error while reading monitoring properties", (Throwable)var2);
      }

   }
}
