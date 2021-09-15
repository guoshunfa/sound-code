package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;

public class WSEndpointMOMProxy extends WSEndpoint implements ManagedObjectManager {
   @NotNull
   private final WSEndpointImpl wsEndpoint;
   private ManagedObjectManager managedObjectManager;

   WSEndpointMOMProxy(@NotNull WSEndpointImpl wsEndpoint) {
      this.wsEndpoint = wsEndpoint;
   }

   public ManagedObjectManager getManagedObjectManager() {
      if (this.managedObjectManager == null) {
         this.managedObjectManager = this.wsEndpoint.obtainManagedObjectManager();
      }

      return this.managedObjectManager;
   }

   void setManagedObjectManager(ManagedObjectManager managedObjectManager) {
      this.managedObjectManager = managedObjectManager;
   }

   public boolean isInitialized() {
      return this.managedObjectManager != null;
   }

   public WSEndpointImpl getWsEndpoint() {
      return this.wsEndpoint;
   }

   public void suspendJMXRegistration() {
      this.getManagedObjectManager().suspendJMXRegistration();
   }

   public void resumeJMXRegistration() {
      this.getManagedObjectManager().resumeJMXRegistration();
   }

   public boolean isManagedObject(Object obj) {
      return this.getManagedObjectManager().isManagedObject(obj);
   }

   public GmbalMBean createRoot() {
      return this.getManagedObjectManager().createRoot();
   }

   public GmbalMBean createRoot(Object root) {
      return this.getManagedObjectManager().createRoot(root);
   }

   public GmbalMBean createRoot(Object root, String name) {
      return this.getManagedObjectManager().createRoot(root, name);
   }

   public Object getRoot() {
      return this.getManagedObjectManager().getRoot();
   }

   public GmbalMBean register(Object parent, Object obj, String name) {
      return this.getManagedObjectManager().register(parent, obj, name);
   }

   public GmbalMBean register(Object parent, Object obj) {
      return this.getManagedObjectManager().register(parent, obj);
   }

   public GmbalMBean registerAtRoot(Object obj, String name) {
      return this.getManagedObjectManager().registerAtRoot(obj, name);
   }

   public GmbalMBean registerAtRoot(Object obj) {
      return this.getManagedObjectManager().registerAtRoot(obj);
   }

   public void unregister(Object obj) {
      this.getManagedObjectManager().unregister(obj);
   }

   public ObjectName getObjectName(Object obj) {
      return this.getManagedObjectManager().getObjectName(obj);
   }

   public AMXClient getAMXClient(Object obj) {
      return this.getManagedObjectManager().getAMXClient(obj);
   }

   public Object getObject(ObjectName oname) {
      return this.getManagedObjectManager().getObject(oname);
   }

   public void stripPrefix(String... str) {
      this.getManagedObjectManager().stripPrefix(str);
   }

   public void stripPackagePrefix() {
      this.getManagedObjectManager().stripPackagePrefix();
   }

   public String getDomain() {
      return this.getManagedObjectManager().getDomain();
   }

   public void setMBeanServer(MBeanServer server) {
      this.getManagedObjectManager().setMBeanServer(server);
   }

   public MBeanServer getMBeanServer() {
      return this.getManagedObjectManager().getMBeanServer();
   }

   public void setResourceBundle(ResourceBundle rb) {
      this.getManagedObjectManager().setResourceBundle(rb);
   }

   public ResourceBundle getResourceBundle() {
      return this.getManagedObjectManager().getResourceBundle();
   }

   public void addAnnotation(AnnotatedElement element, Annotation annotation) {
      this.getManagedObjectManager().addAnnotation(element, annotation);
   }

   public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel level) {
      this.getManagedObjectManager().setRegistrationDebug(level);
   }

   public void setRuntimeDebug(boolean flag) {
      this.getManagedObjectManager().setRuntimeDebug(flag);
   }

   public void setTypelibDebug(int level) {
      this.getManagedObjectManager().setTypelibDebug(level);
   }

   public void setJMXRegistrationDebug(boolean flag) {
      this.getManagedObjectManager().setJMXRegistrationDebug(flag);
   }

   public String dumpSkeleton(Object obj) {
      return this.getManagedObjectManager().dumpSkeleton(obj);
   }

   public void suppressDuplicateRootReport(boolean suppressReport) {
      this.getManagedObjectManager().suppressDuplicateRootReport(suppressReport);
   }

   public void close() throws IOException {
      this.getManagedObjectManager().close();
   }

   public boolean equalsProxiedInstance(WSEndpoint endpoint) {
      if (this.wsEndpoint == null) {
         return endpoint == null;
      } else {
         return this.wsEndpoint.equals(endpoint);
      }
   }

   public Codec createCodec() {
      return this.wsEndpoint.createCodec();
   }

   public QName getServiceName() {
      return this.wsEndpoint.getServiceName();
   }

   public QName getPortName() {
      return this.wsEndpoint.getPortName();
   }

   public Class getImplementationClass() {
      return this.wsEndpoint.getImplementationClass();
   }

   public WSBinding getBinding() {
      return this.wsEndpoint.getBinding();
   }

   public Container getContainer() {
      return this.wsEndpoint.getContainer();
   }

   public WSDLPort getPort() {
      return this.wsEndpoint.getPort();
   }

   public void setExecutor(Executor exec) {
      this.wsEndpoint.setExecutor(exec);
   }

   public void schedule(Packet request, WSEndpoint.CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
      this.wsEndpoint.schedule(request, callback, interceptor);
   }

   public WSEndpoint.PipeHead createPipeHead() {
      return this.wsEndpoint.createPipeHead();
   }

   public void dispose() {
      if (this.wsEndpoint != null) {
         this.wsEndpoint.dispose();
      }

   }

   public ServiceDefinition getServiceDefinition() {
      return this.wsEndpoint.getServiceDefinition();
   }

   public Set getComponentRegistry() {
      return this.wsEndpoint.getComponentRegistry();
   }

   public SEIModel getSEIModel() {
      return this.wsEndpoint.getSEIModel();
   }

   public PolicyMap getPolicyMap() {
      return this.wsEndpoint.getPolicyMap();
   }

   public void closeManagedObjectManager() {
      this.wsEndpoint.closeManagedObjectManager();
   }

   public ServerTubeAssemblerContext getAssemblerContext() {
      return this.wsEndpoint.getAssemblerContext();
   }

   public EndpointReference getEndpointReference(Class clazz, String address, String wsdlAddress, Element... referenceParameters) {
      return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, referenceParameters);
   }

   public EndpointReference getEndpointReference(Class clazz, String address, String wsdlAddress, List metadata, List referenceParameters) {
      return this.wsEndpoint.getEndpointReference(clazz, address, wsdlAddress, metadata, referenceParameters);
   }

   public OperationDispatcher getOperationDispatcher() {
      return this.wsEndpoint.getOperationDispatcher();
   }

   public Packet createServiceResponseForException(ThrowableContainerPropertySet tc, Packet responsePacket, SOAPVersion soapVersion, WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
      return this.wsEndpoint.createServiceResponseForException(tc, responsePacket, soapVersion, wsdlPort, seiModel, binding);
   }
}
