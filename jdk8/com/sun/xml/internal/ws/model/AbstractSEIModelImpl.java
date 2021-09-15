package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.developer.JAXBContextFactory;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.spi.db.BindingInfo;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.Pool;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class AbstractSEIModelImpl implements SEIModel {
   private List<Class> additionalClasses = new ArrayList();
   private Pool.Marshaller marshallers;
   /** @deprecated */
   protected JAXBRIContext jaxbContext;
   protected BindingContext bindingContext;
   private String wsdlLocation;
   private QName serviceName;
   private QName portName;
   private QName portTypeName;
   private Map<Method, JavaMethodImpl> methodToJM = new HashMap();
   private Map<QName, JavaMethodImpl> nameToJM = new HashMap();
   private Map<QName, JavaMethodImpl> wsdlOpToJM = new HashMap();
   private List<JavaMethodImpl> javaMethods = new ArrayList();
   private final Map<TypeReference, Bridge> bridgeMap = new HashMap();
   private final Map<TypeInfo, XMLBridge> xmlBridgeMap = new HashMap();
   protected final QName emptyBodyName = new QName("");
   private String targetNamespace = "";
   private List<String> knownNamespaceURIs = null;
   private WSDLPort port;
   private final WebServiceFeatureList features;
   private Databinding databinding;
   BindingID bindingId;
   protected Class contractClass;
   protected Class endpointClass;
   protected ClassLoader classLoader = null;
   protected WSBinding wsBinding;
   protected BindingInfo databindingInfo;
   protected String defaultSchemaNamespaceSuffix;
   private static final Logger LOGGER = Logger.getLogger(AbstractSEIModelImpl.class.getName());

   protected AbstractSEIModelImpl(WebServiceFeatureList features) {
      this.features = features;
      this.databindingInfo = new BindingInfo();
      this.databindingInfo.setSEIModel(this);
   }

   void postProcess() {
      if (this.jaxbContext == null) {
         this.populateMaps();
         this.createJAXBContext();
      }
   }

   public void freeze(WSDLPort port) {
      this.port = port;
      Iterator var2 = this.javaMethods.iterator();

      while(var2.hasNext()) {
         JavaMethodImpl m = (JavaMethodImpl)var2.next();
         m.freeze(port);
         this.putOp(m.getOperationQName(), m);
      }

      if (this.databinding != null) {
         ((DatabindingImpl)this.databinding).freeze(port);
      }

   }

   protected abstract void populateMaps();

   public Pool.Marshaller getMarshallerPool() {
      return this.marshallers;
   }

   /** @deprecated */
   public JAXBContext getJAXBContext() {
      JAXBContext jc = this.bindingContext.getJAXBContext();
      return (JAXBContext)(jc != null ? jc : this.jaxbContext);
   }

   public BindingContext getBindingContext() {
      return this.bindingContext;
   }

   public List<String> getKnownNamespaceURIs() {
      return this.knownNamespaceURIs;
   }

   /** @deprecated */
   public final Bridge getBridge(TypeReference type) {
      Bridge b = (Bridge)this.bridgeMap.get(type);

      assert b != null;

      return b;
   }

   public final XMLBridge getXMLBridge(TypeInfo type) {
      XMLBridge b = (XMLBridge)this.xmlBridgeMap.get(type);

      assert b != null;

      return b;
   }

   private void createJAXBContext() {
      final List<TypeInfo> types = this.getAllTypeInfos();
      final List<Class> cls = new ArrayList(types.size() + this.additionalClasses.size());
      cls.addAll(this.additionalClasses);
      Iterator var3 = types.iterator();

      while(var3.hasNext()) {
         TypeInfo type = (TypeInfo)var3.next();
         cls.add((Class)type.type);
      }

      try {
         this.bindingContext = (BindingContext)AccessController.doPrivileged(new PrivilegedExceptionAction<BindingContext>() {
            public BindingContext run() throws Exception {
               if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINEST)) {
                  AbstractSEIModelImpl.LOGGER.log(Level.FINEST, "Creating JAXBContext with classes={0} and types={1}", new Object[]{cls, types});
               }

               UsesJAXBContextFeature f = (UsesJAXBContextFeature)AbstractSEIModelImpl.this.features.get(UsesJAXBContextFeature.class);
               DatabindingModeFeature dmf = (DatabindingModeFeature)AbstractSEIModelImpl.this.features.get(DatabindingModeFeature.class);
               JAXBContextFactory factory = f != null ? f.getFactory() : null;
               if (factory == null) {
                  factory = JAXBContextFactory.DEFAULT;
               }

               AbstractSEIModelImpl.this.databindingInfo.properties().put(JAXBContextFactory.class.getName(), factory);
               if (dmf != null) {
                  if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
                     AbstractSEIModelImpl.LOGGER.log(Level.FINE, (String)"DatabindingModeFeature in SEI specifies mode: {0}", (Object)dmf.getMode());
                  }

                  AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode(dmf.getMode());
               }

               if (f != null) {
                  AbstractSEIModelImpl.this.databindingInfo.setDatabindingMode("glassfish.jaxb");
               }

               AbstractSEIModelImpl.this.databindingInfo.setClassLoader(AbstractSEIModelImpl.this.classLoader);
               AbstractSEIModelImpl.this.databindingInfo.contentClasses().addAll(cls);
               AbstractSEIModelImpl.this.databindingInfo.typeInfos().addAll(types);
               AbstractSEIModelImpl.this.databindingInfo.properties().put("c14nSupport", Boolean.FALSE);
               AbstractSEIModelImpl.this.databindingInfo.setDefaultNamespace(AbstractSEIModelImpl.this.getDefaultSchemaNamespace());
               BindingContext bc = BindingContextFactory.create(AbstractSEIModelImpl.this.databindingInfo);
               if (AbstractSEIModelImpl.LOGGER.isLoggable(Level.FINE)) {
                  AbstractSEIModelImpl.LOGGER.log(Level.FINE, "Created binding context: " + bc.getClass().getName());
               }

               return bc;
            }
         });
         this.createBondMap(types);
      } catch (PrivilegedActionException var5) {
         throw new WebServiceException(ModelerMessages.UNABLE_TO_CREATE_JAXB_CONTEXT(), var5);
      }

      this.knownNamespaceURIs = new ArrayList();
      var3 = this.bindingContext.getKnownNamespaceURIs().iterator();

      while(var3.hasNext()) {
         String namespace = (String)var3.next();
         if (namespace.length() > 0 && !namespace.equals("http://www.w3.org/2001/XMLSchema") && !namespace.equals("http://www.w3.org/XML/1998/namespace")) {
            this.knownNamespaceURIs.add(namespace);
         }
      }

      this.marshallers = new Pool.Marshaller(this.jaxbContext);
   }

   private List<TypeInfo> getAllTypeInfos() {
      List<TypeInfo> types = new ArrayList();
      Collection<JavaMethodImpl> methods = this.methodToJM.values();
      Iterator var3 = methods.iterator();

      while(var3.hasNext()) {
         JavaMethodImpl m = (JavaMethodImpl)var3.next();
         m.fillTypes(types);
      }

      return types;
   }

   private void createBridgeMap(List<TypeReference> types) {
      Iterator var2 = types.iterator();

      while(var2.hasNext()) {
         TypeReference type = (TypeReference)var2.next();
         Bridge bridge = this.jaxbContext.createBridge(type);
         this.bridgeMap.put(type, bridge);
      }

   }

   private void createBondMap(List<TypeInfo> types) {
      Iterator var2 = types.iterator();

      while(var2.hasNext()) {
         TypeInfo type = (TypeInfo)var2.next();
         XMLBridge binding = this.bindingContext.createBridge(type);
         this.xmlBridgeMap.put(type, binding);
      }

   }

   public boolean isKnownFault(QName name, Method method) {
      JavaMethodImpl m = this.getJavaMethod(method);
      Iterator var4 = m.getCheckedExceptions().iterator();

      CheckedExceptionImpl ce;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         ce = (CheckedExceptionImpl)var4.next();
      } while(!ce.getDetailType().tagName.equals(name));

      return true;
   }

   public boolean isCheckedException(Method m, Class ex) {
      JavaMethodImpl jm = this.getJavaMethod(m);
      Iterator var4 = jm.getCheckedExceptions().iterator();

      CheckedExceptionImpl ce;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         ce = (CheckedExceptionImpl)var4.next();
      } while(!ce.getExceptionClass().equals(ex));

      return true;
   }

   public JavaMethodImpl getJavaMethod(Method method) {
      return (JavaMethodImpl)this.methodToJM.get(method);
   }

   public JavaMethodImpl getJavaMethod(QName name) {
      return (JavaMethodImpl)this.nameToJM.get(name);
   }

   public JavaMethod getJavaMethodForWsdlOperation(QName operationName) {
      return (JavaMethod)this.wsdlOpToJM.get(operationName);
   }

   /** @deprecated */
   public QName getQNameForJM(JavaMethodImpl jm) {
      Iterator var2 = this.nameToJM.keySet().iterator();

      QName key;
      JavaMethodImpl jmethod;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         key = (QName)var2.next();
         jmethod = (JavaMethodImpl)this.nameToJM.get(key);
      } while(!jmethod.getOperationName().equals(jm.getOperationName()));

      return key;
   }

   public final Collection<JavaMethodImpl> getJavaMethods() {
      return Collections.unmodifiableList(this.javaMethods);
   }

   void addJavaMethod(JavaMethodImpl jm) {
      if (jm != null) {
         this.javaMethods.add(jm);
      }

   }

   private List<ParameterImpl> applyRpcLitParamBinding(JavaMethodImpl method, WrapperParameter wrapperParameter, WSDLBoundPortType boundPortType, WebParam.Mode mode) {
      QName opName = new QName(boundPortType.getPortTypeName().getNamespaceURI(), method.getOperationName());
      WSDLBoundOperation bo = boundPortType.get(opName);
      Map<Integer, ParameterImpl> bodyParams = new HashMap();
      List<ParameterImpl> unboundParams = new ArrayList();
      List<ParameterImpl> attachParams = new ArrayList();
      Iterator var10 = wrapperParameter.wrapperChildren.iterator();

      while(true) {
         ParameterImpl param;
         ParameterBinding paramBinding;
         do {
            String partName;
            do {
               if (!var10.hasNext()) {
                  wrapperParameter.clear();

                  for(int i = 0; i < bodyParams.size(); ++i) {
                     param = (ParameterImpl)bodyParams.get(i);
                     wrapperParameter.addWrapperChild(param);
                  }

                  var10 = unboundParams.iterator();

                  while(var10.hasNext()) {
                     param = (ParameterImpl)var10.next();
                     wrapperParameter.addWrapperChild(param);
                  }

                  return attachParams;
               }

               param = (ParameterImpl)var10.next();
               partName = param.getPartName();
            } while(partName == null);

            paramBinding = boundPortType.getBinding(opName, partName, mode);
         } while(paramBinding == null);

         if (mode == WebParam.Mode.IN) {
            param.setInBinding(paramBinding);
         } else if (mode == WebParam.Mode.OUT || mode == WebParam.Mode.INOUT) {
            param.setOutBinding(paramBinding);
         }

         if (paramBinding.isUnbound()) {
            unboundParams.add(param);
         } else if (paramBinding.isAttachment()) {
            attachParams.add(param);
         } else if (paramBinding.isBody()) {
            if (bo != null) {
               WSDLPart p = bo.getPart(param.getPartName(), mode);
               if (p != null) {
                  bodyParams.put(p.getIndex(), param);
               } else {
                  bodyParams.put(bodyParams.size(), param);
               }
            } else {
               bodyParams.put(bodyParams.size(), param);
            }
         }
      }
   }

   void put(QName name, JavaMethodImpl jm) {
      this.nameToJM.put(name, jm);
   }

   void put(Method method, JavaMethodImpl jm) {
      this.methodToJM.put(method, jm);
   }

   void putOp(QName opName, JavaMethodImpl jm) {
      this.wsdlOpToJM.put(opName, jm);
   }

   public String getWSDLLocation() {
      return this.wsdlLocation;
   }

   void setWSDLLocation(String location) {
      this.wsdlLocation = location;
   }

   public QName getServiceQName() {
      return this.serviceName;
   }

   public WSDLPort getPort() {
      return this.port;
   }

   public QName getPortName() {
      return this.portName;
   }

   public QName getPortTypeName() {
      return this.portTypeName;
   }

   void setServiceQName(QName name) {
      this.serviceName = name;
   }

   void setPortName(QName name) {
      this.portName = name;
   }

   void setPortTypeName(QName name) {
      this.portTypeName = name;
   }

   void setTargetNamespace(String namespace) {
      this.targetNamespace = namespace;
   }

   public String getTargetNamespace() {
      return this.targetNamespace;
   }

   String getDefaultSchemaNamespace() {
      String defaultNamespace = this.getTargetNamespace();
      if (this.defaultSchemaNamespaceSuffix == null) {
         return defaultNamespace;
      } else {
         if (!defaultNamespace.endsWith("/")) {
            defaultNamespace = defaultNamespace + "/";
         }

         return defaultNamespace + this.defaultSchemaNamespaceSuffix;
      }
   }

   @NotNull
   public QName getBoundPortTypeName() {
      assert this.portName != null;

      return new QName(this.portName.getNamespaceURI(), this.portName.getLocalPart() + "Binding");
   }

   public void addAdditionalClasses(Class... additionalClasses) {
      Class[] var2 = additionalClasses;
      int var3 = additionalClasses.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class cls = var2[var4];
         this.additionalClasses.add(cls);
      }

   }

   public Databinding getDatabinding() {
      return this.databinding;
   }

   public void setDatabinding(Databinding wsRuntime) {
      this.databinding = wsRuntime;
   }

   public WSBinding getWSBinding() {
      return this.wsBinding;
   }

   public Class getContractClass() {
      return this.contractClass;
   }

   public Class getEndpointClass() {
      return this.endpointClass;
   }
}
