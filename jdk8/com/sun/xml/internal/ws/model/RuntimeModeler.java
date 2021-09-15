package com.sun.xml.internal.ws.model;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingType;
import javax.xml.ws.FaultAction;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;

public class RuntimeModeler {
   private final WebServiceFeatureList features;
   private BindingID bindingId;
   private WSBinding wsBinding;
   private final Class portClass;
   private AbstractSEIModelImpl model;
   private SOAPBindingImpl defaultBinding;
   private String packageName;
   private String targetNamespace;
   private boolean isWrapped = true;
   private ClassLoader classLoader;
   private final WSDLPort binding;
   private QName serviceName;
   private QName portName;
   private Set<Class> classUsesWebMethod;
   private DatabindingConfig config;
   private MetadataReader metadataReader;
   public static final String PD_JAXWS_PACKAGE_PD = ".jaxws.";
   public static final String JAXWS_PACKAGE_PD = "jaxws.";
   public static final String RESPONSE = "Response";
   public static final String RETURN = "return";
   public static final String BEAN = "Bean";
   public static final String SERVICE = "Service";
   public static final String PORT = "Port";
   public static final Class HOLDER_CLASS = Holder.class;
   public static final Class<RemoteException> REMOTE_EXCEPTION_CLASS = RemoteException.class;
   public static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
   public static final Class<Exception> EXCEPTION_CLASS = Exception.class;
   public static final String DecapitalizeExceptionBeanProperties = "com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties";
   public static final String SuppressDocLitWrapperGeneration = "com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration";
   public static final String DocWrappeeNamespapceQualified = "com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified";
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");

   public RuntimeModeler(@NotNull DatabindingConfig config) {
      this.portClass = config.getEndpointClass() != null ? config.getEndpointClass() : config.getContractClass();
      this.serviceName = config.getMappingInfo().getServiceName();
      this.binding = config.getWsdlPort();
      this.classLoader = config.getClassLoader();
      this.portName = config.getMappingInfo().getPortName();
      this.config = config;
      this.wsBinding = config.getWSBinding();
      this.metadataReader = config.getMetadataReader();
      this.targetNamespace = config.getMappingInfo().getTargetNamespace();
      if (this.metadataReader == null) {
         this.metadataReader = new ReflectAnnotationReader();
      }

      if (this.wsBinding != null) {
         this.bindingId = this.wsBinding.getBindingId();
         if (config.getFeatures() != null) {
            this.wsBinding.getFeatures().mergeFeatures(config.getFeatures(), false);
         }

         if (this.binding != null) {
            this.wsBinding.getFeatures().mergeFeatures((Iterable)this.binding.getFeatures(), false);
         }

         this.features = WebServiceFeatureList.toList(this.wsBinding.getFeatures());
      } else {
         this.bindingId = config.getMappingInfo().getBindingID();
         this.features = WebServiceFeatureList.toList(config.getFeatures());
         if (this.binding != null) {
            this.bindingId = this.binding.getBinding().getBindingId();
         }

         if (this.bindingId == null) {
            this.bindingId = this.getDefaultBindingID();
         }

         if (!this.features.contains(MTOMFeature.class)) {
            MTOM mtomAn = (MTOM)this.getAnnotation(this.portClass, MTOM.class);
            if (mtomAn != null) {
               this.features.add(WebServiceFeatureList.getFeature(mtomAn));
            }
         }

         if (!this.features.contains(EnvelopeStyleFeature.class)) {
            EnvelopeStyle es = (EnvelopeStyle)this.getAnnotation(this.portClass, EnvelopeStyle.class);
            if (es != null) {
               this.features.add(WebServiceFeatureList.getFeature(es));
            }
         }

         this.wsBinding = this.bindingId.createBinding((WSFeatureList)this.features);
      }

   }

   private BindingID getDefaultBindingID() {
      BindingType bt = (BindingType)this.getAnnotation(this.portClass, BindingType.class);
      if (bt != null) {
         return BindingID.parse(bt.value());
      } else {
         SOAPVersion ver = WebServiceFeatureList.getSoapVersion(this.features);
         boolean mtomEnabled = this.features.isEnabled(MTOMFeature.class);
         if (SOAPVersion.SOAP_12.equals(ver)) {
            return mtomEnabled ? BindingID.SOAP12_HTTP_MTOM : BindingID.SOAP12_HTTP;
         } else {
            return mtomEnabled ? BindingID.SOAP11_HTTP_MTOM : BindingID.SOAP11_HTTP;
         }
      }
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public void setPortName(QName portName) {
      this.portName = portName;
   }

   private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> T) {
      return this.metadataReader.getAnnotation(T, clazz);
   }

   private <T extends Annotation> T getAnnotation(Method method, Class<T> T) {
      return this.metadataReader.getAnnotation(T, method);
   }

   private Annotation[] getAnnotations(Method method) {
      return this.metadataReader.getAnnotations(method);
   }

   private Annotation[] getAnnotations(Class<?> c) {
      return this.metadataReader.getAnnotations(c);
   }

   private Annotation[][] getParamAnnotations(Method method) {
      return this.metadataReader.getParameterAnnotations(method);
   }

   public AbstractSEIModelImpl buildRuntimeModel() {
      this.model = new SOAPSEIModel(this.features);
      this.model.contractClass = this.config.getContractClass();
      this.model.endpointClass = this.config.getEndpointClass();
      this.model.classLoader = this.classLoader;
      this.model.wsBinding = this.wsBinding;
      this.model.databindingInfo.setWsdlURL(this.config.getWsdlURL());
      this.model.databindingInfo.properties().putAll(this.config.properties());
      if (this.model.contractClass == null) {
         this.model.contractClass = this.portClass;
      }

      if (this.model.endpointClass == null && !this.portClass.isInterface()) {
         this.model.endpointClass = this.portClass;
      }

      Class<?> seiClass = this.portClass;
      this.metadataReader.getProperties(this.model.databindingInfo.properties(), this.portClass);
      WebService webService = (WebService)this.getAnnotation(this.portClass, WebService.class);
      if (webService == null) {
         throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[]{this.portClass.getCanonicalName()});
      } else {
         Class<?> seiFromConfig = this.configEndpointInterface();
         if (webService.endpointInterface().length() > 0 || seiFromConfig != null) {
            if (seiFromConfig != null) {
               seiClass = seiFromConfig;
            } else {
               seiClass = this.getClass(webService.endpointInterface(), ModelerMessages.localizableRUNTIME_MODELER_CLASS_NOT_FOUND(webService.endpointInterface()));
            }

            this.model.contractClass = seiClass;
            this.model.endpointClass = this.portClass;
            WebService seiService = (WebService)this.getAnnotation(seiClass, WebService.class);
            if (seiService == null) {
               throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[]{webService.endpointInterface()});
            }

            SOAPBinding sbPortClass = (SOAPBinding)this.getAnnotation(this.portClass, SOAPBinding.class);
            SOAPBinding sbSei = (SOAPBinding)this.getAnnotation(seiClass, SOAPBinding.class);
            if (sbPortClass != null && (sbSei == null || sbSei.style() != sbPortClass.style() || sbSei.use() != sbPortClass.use())) {
               logger.warning(ServerMessages.RUNTIMEMODELER_INVALIDANNOTATION_ON_IMPL("@SOAPBinding", this.portClass.getName(), seiClass.getName()));
            }
         }

         if (this.serviceName == null) {
            this.serviceName = getServiceName(this.portClass, this.metadataReader);
         }

         this.model.setServiceQName(this.serviceName);
         if (this.portName == null) {
            this.portName = getPortName(this.portClass, this.metadataReader, this.serviceName.getNamespaceURI());
         }

         this.model.setPortName(this.portName);
         DatabindingMode dbm2 = (DatabindingMode)this.getAnnotation(this.portClass, DatabindingMode.class);
         if (dbm2 != null) {
            this.model.databindingInfo.setDatabindingMode(dbm2.value());
         }

         this.processClass(seiClass);
         if (this.model.getJavaMethods().size() == 0) {
            throw new RuntimeModelerException("runtime.modeler.no.operations", new Object[]{this.portClass.getName()});
         } else {
            this.model.postProcess();
            this.config.properties().put(BindingContext.class.getName(), this.model.bindingContext);
            if (this.binding != null) {
               this.model.freeze(this.binding);
            }

            return this.model;
         }
      }
   }

   private Class configEndpointInterface() {
      return this.config.getEndpointClass() != null && !this.config.getEndpointClass().isInterface() ? this.config.getContractClass() : null;
   }

   private Class getClass(String className, Localizable errorMessage) {
      try {
         return this.classLoader == null ? Thread.currentThread().getContextClassLoader().loadClass(className) : this.classLoader.loadClass(className);
      } catch (ClassNotFoundException var4) {
         throw new RuntimeModelerException(errorMessage);
      }
   }

   private boolean noWrapperGen() {
      Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.SuppressDocLitWrapperGeneration");
      return o != null && o instanceof Boolean ? (Boolean)o : false;
   }

   private Class getRequestWrapperClass(String className, Method method, QName reqElemName) {
      ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;

      try {
         return loader.loadClass(className);
      } catch (ClassNotFoundException var6) {
         if (this.noWrapperGen()) {
            return WrapperComposite.class;
         } else {
            logger.fine("Dynamically creating request wrapper Class " + className);
            return WrapperBeanGenerator.createRequestWrapperBean(className, method, reqElemName, loader);
         }
      }
   }

   private Class getResponseWrapperClass(String className, Method method, QName resElemName) {
      ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;

      try {
         return loader.loadClass(className);
      } catch (ClassNotFoundException var6) {
         if (this.noWrapperGen()) {
            return WrapperComposite.class;
         } else {
            logger.fine("Dynamically creating response wrapper bean Class " + className);
            return WrapperBeanGenerator.createResponseWrapperBean(className, method, resElemName, loader);
         }
      }
   }

   private Class getExceptionBeanClass(String className, Class exception, String name, String namespace) {
      boolean decapitalizeExceptionBeanProperties = true;
      Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.DecapitalizeExceptionBeanProperties");
      if (o != null && o instanceof Boolean) {
         decapitalizeExceptionBeanProperties = (Boolean)o;
      }

      ClassLoader loader = this.classLoader == null ? Thread.currentThread().getContextClassLoader() : this.classLoader;

      try {
         return loader.loadClass(className);
      } catch (ClassNotFoundException var9) {
         logger.fine("Dynamically creating exception bean Class " + className);
         return WrapperBeanGenerator.createExceptionBean(className, exception, this.targetNamespace, name, namespace, loader, decapitalizeExceptionBeanProperties);
      }
   }

   protected void determineWebMethodUse(Class clazz) {
      if (clazz != null) {
         if (!clazz.isInterface()) {
            if (clazz == Object.class) {
               return;
            }

            Method[] var3 = clazz.getMethods();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Method method = var3[var5];
               if (method.getDeclaringClass() == clazz) {
                  WebMethod webMethod = (WebMethod)this.getAnnotation(method, WebMethod.class);
                  if (webMethod != null && !webMethod.exclude()) {
                     this.classUsesWebMethod.add(clazz);
                     break;
                  }
               }
            }
         }

         this.determineWebMethodUse(clazz.getSuperclass());
      }
   }

   void processClass(Class clazz) {
      this.classUsesWebMethod = new HashSet();
      this.determineWebMethodUse(clazz);
      WebService webService = (WebService)this.getAnnotation(clazz, WebService.class);
      QName portTypeName = getPortTypeName(clazz, this.targetNamespace, this.metadataReader);
      this.packageName = "";
      if (clazz.getPackage() != null) {
         this.packageName = clazz.getPackage().getName();
      }

      this.targetNamespace = portTypeName.getNamespaceURI();
      this.model.setPortTypeName(portTypeName);
      this.model.setTargetNamespace(this.targetNamespace);
      this.model.defaultSchemaNamespaceSuffix = this.config.getMappingInfo().getDefaultSchemaNamespaceSuffix();
      this.model.setWSDLLocation(webService.wsdlLocation());
      SOAPBinding soapBinding = (SOAPBinding)this.getAnnotation(clazz, SOAPBinding.class);
      if (soapBinding != null) {
         if (soapBinding.style() == SOAPBinding.Style.RPC && soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
            throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[]{soapBinding, clazz});
         }

         this.isWrapped = soapBinding.parameterStyle() == SOAPBinding.ParameterStyle.WRAPPED;
      }

      this.defaultBinding = this.createBinding(soapBinding);
      Method[] var5 = clazz.getMethods();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Method method = var5[var7];
         if (!clazz.isInterface()) {
            if (method.getDeclaringClass() == Object.class) {
               continue;
            }

            if (!getBooleanSystemProperty("com.sun.xml.internal.ws.legacyWebMethod")) {
               if (!this.isWebMethodBySpec(method, clazz)) {
                  continue;
               }
            } else if (!this.isWebMethod(method)) {
               continue;
            }
         }

         this.processMethod(method);
      }

      XmlSeeAlso xmlSeeAlso = (XmlSeeAlso)this.getAnnotation(clazz, XmlSeeAlso.class);
      if (xmlSeeAlso != null) {
         this.model.addAdditionalClasses(xmlSeeAlso.value());
      }

   }

   private boolean isWebMethodBySpec(Method method, Class clazz) {
      int modifiers = method.getModifiers();
      boolean staticFinal = Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);

      assert Modifier.isPublic(modifiers);

      assert !clazz.isInterface();

      WebMethod webMethod = (WebMethod)this.getAnnotation(method, WebMethod.class);
      if (webMethod != null) {
         if (webMethod.exclude()) {
            return false;
         } else if (staticFinal) {
            throw new RuntimeModelerException(ModelerMessages.localizableRUNTIME_MODELER_WEBMETHOD_MUST_BE_NONSTATICFINAL(method));
         } else {
            return true;
         }
      } else if (staticFinal) {
         return false;
      } else {
         Class declClass = method.getDeclaringClass();
         return this.getAnnotation(declClass, WebService.class) != null;
      }
   }

   private boolean isWebMethod(Method method) {
      int modifiers = method.getModifiers();
      if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
         Class clazz = method.getDeclaringClass();
         boolean declHasWebService = this.getAnnotation(clazz, WebService.class) != null;
         WebMethod webMethod = (WebMethod)this.getAnnotation(method, WebMethod.class);
         if (webMethod != null && !webMethod.exclude() && declHasWebService) {
            return true;
         } else {
            return declHasWebService && !this.classUsesWebMethod.contains(clazz);
         }
      } else {
         return false;
      }
   }

   protected SOAPBindingImpl createBinding(SOAPBinding soapBinding) {
      SOAPBindingImpl rtSOAPBinding = new SOAPBindingImpl();
      SOAPBinding.Style style = soapBinding != null ? soapBinding.style() : SOAPBinding.Style.DOCUMENT;
      rtSOAPBinding.setStyle(style);

      assert this.bindingId != null;

      this.model.bindingId = this.bindingId;
      SOAPVersion soapVersion = this.bindingId.getSOAPVersion();
      rtSOAPBinding.setSOAPVersion(soapVersion);
      return rtSOAPBinding;
   }

   public static String getNamespace(@NotNull String packageName) {
      if (packageName.length() == 0) {
         return null;
      } else {
         StringTokenizer tokenizer = new StringTokenizer(packageName, ".");
         String[] tokens;
         if (tokenizer.countTokens() == 0) {
            tokens = new String[0];
         } else {
            tokens = new String[tokenizer.countTokens()];

            for(int i = tokenizer.countTokens() - 1; i >= 0; --i) {
               tokens[i] = tokenizer.nextToken();
            }
         }

         StringBuilder namespace = new StringBuilder("http://");

         for(int i = 0; i < tokens.length; ++i) {
            if (i != 0) {
               namespace.append('.');
            }

            namespace.append(tokens[i]);
         }

         namespace.append('/');
         return namespace.toString();
      }
   }

   private boolean isServiceException(Class<?> exception) {
      return EXCEPTION_CLASS.isAssignableFrom(exception) && !RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception) && !REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception);
   }

   private void processMethod(Method method) {
      WebMethod webMethod = (WebMethod)this.getAnnotation(method, WebMethod.class);
      if (webMethod == null || !webMethod.exclude()) {
         String methodName = method.getName();
         boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
         if (isOneway) {
            Class[] var5 = method.getExceptionTypes();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Class<?> exception = var5[var7];
               if (this.isServiceException(exception)) {
                  throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.checked.exceptions", new Object[]{this.portClass.getCanonicalName(), methodName, exception.getName()});
               }
            }
         }

         JavaMethodImpl javaMethod;
         if (method.getDeclaringClass() == this.portClass) {
            javaMethod = new JavaMethodImpl(this.model, method, method, this.metadataReader);
         } else {
            try {
               Method tmpMethod = this.portClass.getMethod(method.getName(), method.getParameterTypes());
               javaMethod = new JavaMethodImpl(this.model, tmpMethod, method, this.metadataReader);
            } catch (NoSuchMethodException var14) {
               throw new RuntimeModelerException("runtime.modeler.method.not.found", new Object[]{method.getName(), this.portClass.getName()});
            }
         }

         MEP mep = this.getMEP(method);
         javaMethod.setMEP(mep);
         String action = null;
         String operationName = method.getName();
         if (webMethod != null) {
            action = webMethod.action();
            operationName = webMethod.operationName().length() > 0 ? webMethod.operationName() : operationName;
         }

         if (this.binding != null) {
            WSDLBoundOperation bo = this.binding.getBinding().get(new QName(this.targetNamespace, operationName));
            if (bo != null) {
               WSDLInput wsdlInput = bo.getOperation().getInput();
               String wsaAction = wsdlInput.getAction();
               if (wsaAction != null && !wsdlInput.isDefaultAction()) {
                  action = wsaAction;
               } else {
                  action = bo.getSOAPAction();
               }
            }
         }

         javaMethod.setOperationQName(new QName(this.targetNamespace, operationName));
         SOAPBinding methodBinding = (SOAPBinding)this.getAnnotation(method, SOAPBinding.class);
         if (methodBinding != null && methodBinding.style() == SOAPBinding.Style.RPC) {
            logger.warning(ModelerMessages.RUNTIMEMODELER_INVALID_SOAPBINDING_ON_METHOD(methodBinding, method.getName(), method.getDeclaringClass().getName()));
         } else if (methodBinding == null && !method.getDeclaringClass().equals(this.portClass)) {
            methodBinding = (SOAPBinding)this.getAnnotation(method.getDeclaringClass(), SOAPBinding.class);
            if (methodBinding != null && methodBinding.style() == SOAPBinding.Style.RPC && methodBinding.parameterStyle() == SOAPBinding.ParameterStyle.BARE) {
               throw new RuntimeModelerException("runtime.modeler.invalid.soapbinding.parameterstyle", new Object[]{methodBinding, method.getDeclaringClass()});
            }
         }

         if (methodBinding != null && this.defaultBinding.getStyle() != methodBinding.style()) {
            throw new RuntimeModelerException("runtime.modeler.soapbinding.conflict", new Object[]{methodBinding.style(), method.getName(), this.defaultBinding.getStyle()});
         } else {
            boolean methodIsWrapped = this.isWrapped;
            SOAPBinding.Style style = this.defaultBinding.getStyle();
            SOAPBindingImpl sb;
            if (methodBinding != null) {
               sb = this.createBinding(methodBinding);
               style = sb.getStyle();
               if (action != null) {
                  sb.setSOAPAction(action);
               }

               methodIsWrapped = methodBinding.parameterStyle().equals(SOAPBinding.ParameterStyle.WRAPPED);
               javaMethod.setBinding(sb);
            } else {
               sb = new SOAPBindingImpl(this.defaultBinding);
               if (action != null) {
                  sb.setSOAPAction(action);
               } else {
                  String defaults = SOAPVersion.SOAP_11 == sb.getSOAPVersion() ? "" : null;
                  sb.setSOAPAction(defaults);
               }

               javaMethod.setBinding(sb);
            }

            if (!methodIsWrapped) {
               this.processDocBareMethod(javaMethod, operationName, method);
            } else if (style.equals(SOAPBinding.Style.DOCUMENT)) {
               this.processDocWrappedMethod(javaMethod, methodName, operationName, method);
            } else {
               this.processRpcMethod(javaMethod, methodName, operationName, method);
            }

            this.model.addJavaMethod(javaMethod);
         }
      }
   }

   private MEP getMEP(Method m) {
      if (this.getAnnotation(m, Oneway.class) != null) {
         return MEP.ONE_WAY;
      } else if (Response.class.isAssignableFrom(m.getReturnType())) {
         return MEP.ASYNC_POLL;
      } else {
         return Future.class.isAssignableFrom(m.getReturnType()) ? MEP.ASYNC_CALLBACK : MEP.REQUEST_RESPONSE;
      }
   }

   protected void processDocWrappedMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method) {
      boolean methodHasHeaderParams = false;
      boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
      RequestWrapper reqWrapper = (RequestWrapper)this.getAnnotation(method, RequestWrapper.class);
      ResponseWrapper resWrapper = (ResponseWrapper)this.getAnnotation(method, ResponseWrapper.class);
      String beanPackage = this.packageName + ".jaxws.";
      if (this.packageName == null || this.packageName.length() == 0) {
         beanPackage = "jaxws.";
      }

      String requestClassName;
      if (reqWrapper != null && reqWrapper.className().length() > 0) {
         requestClassName = reqWrapper.className();
      } else {
         requestClassName = beanPackage + capitalize(method.getName());
      }

      String responseClassName;
      if (resWrapper != null && resWrapper.className().length() > 0) {
         responseClassName = resWrapper.className();
      } else {
         responseClassName = beanPackage + capitalize(method.getName()) + "Response";
      }

      String reqName = operationName;
      String reqNamespace = this.targetNamespace;
      String reqPartName = "parameters";
      if (reqWrapper != null) {
         if (reqWrapper.targetNamespace().length() > 0) {
            reqNamespace = reqWrapper.targetNamespace();
         }

         if (reqWrapper.localName().length() > 0) {
            reqName = reqWrapper.localName();
         }

         try {
            if (reqWrapper.partName().length() > 0) {
               reqPartName = reqWrapper.partName();
            }
         } catch (LinkageError var49) {
         }
      }

      QName reqElementName = new QName(reqNamespace, reqName);
      javaMethod.setRequestPayloadName(reqElementName);
      Class requestClass = this.getRequestWrapperClass(requestClassName, method, reqElementName);
      Class responseClass = null;
      String resName = operationName + "Response";
      String resNamespace = this.targetNamespace;
      QName resElementName = null;
      String resPartName = "parameters";
      if (!isOneway) {
         if (resWrapper != null) {
            if (resWrapper.targetNamespace().length() > 0) {
               resNamespace = resWrapper.targetNamespace();
            }

            if (resWrapper.localName().length() > 0) {
               resName = resWrapper.localName();
            }

            try {
               if (resWrapper.partName().length() > 0) {
                  resPartName = resWrapper.partName();
               }
            } catch (LinkageError var48) {
            }
         }

         resElementName = new QName(resNamespace, resName);
         responseClass = this.getResponseWrapperClass(responseClassName, method, resElementName);
      }

      TypeInfo typeRef = new TypeInfo(reqElementName, requestClass, new Annotation[0]);
      typeRef.setNillable(false);
      WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
      requestWrapper.setPartName(reqPartName);
      requestWrapper.setBinding(ParameterBinding.BODY);
      javaMethod.addParameter(requestWrapper);
      WrapperParameter responseWrapper = null;
      if (!isOneway) {
         typeRef = new TypeInfo(resElementName, responseClass, new Annotation[0]);
         typeRef.setNillable(false);
         responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
         javaMethod.addParameter(responseWrapper);
         responseWrapper.setBinding(ParameterBinding.BODY);
      }

      WebResult webResult = (WebResult)this.getAnnotation(method, WebResult.class);
      XmlElement xmlElem = (XmlElement)this.getAnnotation(method, XmlElement.class);
      QName resultQName = getReturnQName(method, webResult, xmlElem);
      Class returnType = method.getReturnType();
      boolean isResultHeader = false;
      if (webResult != null) {
         isResultHeader = webResult.header();
         methodHasHeaderParams = isResultHeader || methodHasHeaderParams;
         if (isResultHeader && xmlElem != null) {
            throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " as the return value is bound to header", new Object[0]);
         }

         if (resultQName.getNamespaceURI().length() == 0 && webResult.header()) {
            resultQName = new QName(this.targetNamespace, resultQName.getLocalPart());
         }
      }

      if (javaMethod.isAsync()) {
         returnType = this.getAsyncReturnType(method, returnType);
         resultQName = new QName("return");
      }

      resultQName = this.qualifyWrappeeIfNeeded(resultQName, resNamespace);
      if (!isOneway && returnType != null && !returnType.getName().equals("void")) {
         Annotation[] rann = this.getAnnotations(method);
         if (resultQName.getLocalPart() != null) {
            TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
            this.metadataReader.getProperties(rTypeReference.properties(), method);
            rTypeReference.setGenericType(method.getGenericReturnType());
            ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
            if (isResultHeader) {
               returnParameter.setBinding(ParameterBinding.HEADER);
               javaMethod.addParameter(returnParameter);
            } else {
               returnParameter.setBinding(ParameterBinding.BODY);
               responseWrapper.addWrapperChild(returnParameter);
            }
         }
      }

      Class<?>[] parameterTypes = method.getParameterTypes();
      Type[] genericParameterTypes = method.getGenericParameterTypes();
      Annotation[][] pannotations = this.getParamAnnotations(method);
      int pos = 0;
      Class[] var34 = parameterTypes;
      int var35 = parameterTypes.length;

      for(int var36 = 0; var36 < var35; ++var36) {
         Class clazzType = var34[var36];
         String partName = null;
         String paramName = "arg" + pos;
         boolean isHeader = false;
         if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
            if (isHolder && clazzType == Holder.class) {
               clazzType = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }

            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            WebParam webParam = null;
            xmlElem = null;
            Annotation[] var44 = pannotations[pos];
            int var45 = var44.length;

            for(int var46 = 0; var46 < var45; ++var46) {
               Annotation annotation = var44[var46];
               if (annotation.annotationType() == WebParam.class) {
                  webParam = (WebParam)annotation;
               } else if (annotation.annotationType() == XmlElement.class) {
                  xmlElem = (XmlElement)annotation;
               }
            }

            QName paramQName = getParameterQName(method, webParam, xmlElem, paramName);
            if (webParam != null) {
               isHeader = webParam.header();
               methodHasHeaderParams = isHeader || methodHasHeaderParams;
               if (isHeader && xmlElem != null) {
                  throw new RuntimeModelerException("@XmlElement cannot be specified on method " + method + " parameter that is bound to header", new Object[0]);
               }

               if (webParam.partName().length() > 0) {
                  partName = webParam.partName();
               } else {
                  partName = paramQName.getLocalPart();
               }

               if (isHeader && paramQName.getNamespaceURI().equals("")) {
                  paramQName = new QName(this.targetNamespace, paramQName.getLocalPart());
               }

               paramMode = webParam.mode();
               if (isHolder && paramMode == WebParam.Mode.IN) {
                  paramMode = WebParam.Mode.INOUT;
               }
            }

            paramQName = this.qualifyWrappeeIfNeeded(paramQName, reqNamespace);
            typeRef = new TypeInfo(paramQName, clazzType, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            if (isHeader) {
               param.setBinding(ParameterBinding.HEADER);
               javaMethod.addParameter(param);
               param.setPartName(partName);
            } else {
               param.setBinding(ParameterBinding.BODY);
               if (paramMode != WebParam.Mode.OUT) {
                  requestWrapper.addWrapperChild(param);
               }

               if (paramMode != WebParam.Mode.IN) {
                  if (isOneway) {
                     throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[]{this.portClass.getCanonicalName(), methodName});
                  }

                  responseWrapper.addWrapperChild(param);
               }
            }
         }
      }

      if (methodHasHeaderParams) {
         resPartName = "result";
      }

      if (responseWrapper != null) {
         responseWrapper.setPartName(resPartName);
      }

      this.processExceptions(javaMethod, method);
   }

   private QName qualifyWrappeeIfNeeded(QName resultQName, String ns) {
      Object o = this.config.properties().get("com.sun.xml.internal.ws.api.model.DocWrappeeNamespapceQualified");
      boolean qualified = o != null && o instanceof Boolean ? (Boolean)o : false;
      return !qualified || resultQName.getNamespaceURI() != null && !"".equals(resultQName.getNamespaceURI()) ? resultQName : new QName(ns, resultQName.getLocalPart());
   }

   protected void processRpcMethod(JavaMethodImpl javaMethod, String methodName, String operationName, Method method) {
      boolean isOneway = this.getAnnotation(method, Oneway.class) != null;
      Map<Integer, ParameterImpl> resRpcParams = new TreeMap();
      Map<Integer, ParameterImpl> reqRpcParams = new TreeMap();
      String reqNamespace = this.targetNamespace;
      String respNamespace = this.targetNamespace;
      QName reqElementName;
      if (this.binding != null && SOAPBinding.Style.RPC.equals(this.binding.getBinding().getStyle())) {
         reqElementName = new QName(this.binding.getBinding().getPortTypeName().getNamespaceURI(), operationName);
         WSDLBoundOperation op = this.binding.getBinding().get(reqElementName);
         if (op != null) {
            if (op.getRequestNamespace() != null) {
               reqNamespace = op.getRequestNamespace();
            }

            if (op.getResponseNamespace() != null) {
               respNamespace = op.getResponseNamespace();
            }
         }
      }

      reqElementName = new QName(reqNamespace, operationName);
      javaMethod.setRequestPayloadName(reqElementName);
      QName resElementName = null;
      if (!isOneway) {
         resElementName = new QName(respNamespace, operationName + "Response");
      }

      Class wrapperType = WrapperComposite.class;
      TypeInfo typeRef = new TypeInfo(reqElementName, wrapperType, new Annotation[0]);
      WrapperParameter requestWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.IN, 0);
      requestWrapper.setInBinding(ParameterBinding.BODY);
      javaMethod.addParameter(requestWrapper);
      WrapperParameter responseWrapper = null;
      if (!isOneway) {
         typeRef = new TypeInfo(resElementName, wrapperType, new Annotation[0]);
         responseWrapper = new WrapperParameter(javaMethod, typeRef, WebParam.Mode.OUT, -1);
         responseWrapper.setOutBinding(ParameterBinding.BODY);
         javaMethod.addParameter(responseWrapper);
      }

      Class returnType = method.getReturnType();
      String resultName = "return";
      String resultTNS = this.targetNamespace;
      String resultPartName = resultName;
      boolean isResultHeader = false;
      WebResult webResult = (WebResult)this.getAnnotation(method, WebResult.class);
      if (webResult != null) {
         isResultHeader = webResult.header();
         if (webResult.name().length() > 0) {
            resultName = webResult.name();
         }

         if (webResult.partName().length() > 0) {
            resultPartName = webResult.partName();
            if (!isResultHeader) {
               resultName = resultPartName;
            }
         } else {
            resultPartName = resultName;
         }

         if (webResult.targetNamespace().length() > 0) {
            resultTNS = webResult.targetNamespace();
         }

         isResultHeader = webResult.header();
      }

      QName resultQName;
      if (isResultHeader) {
         resultQName = new QName(resultTNS, resultName);
      } else {
         resultQName = new QName(resultName);
      }

      if (javaMethod.isAsync()) {
         returnType = this.getAsyncReturnType(method, returnType);
      }

      if (!isOneway && returnType != null && returnType != Void.TYPE) {
         Annotation[] rann = this.getAnnotations(method);
         TypeInfo rTypeReference = new TypeInfo(resultQName, returnType, rann);
         this.metadataReader.getProperties(rTypeReference.properties(), method);
         rTypeReference.setGenericType(method.getGenericReturnType());
         ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
         returnParameter.setPartName(resultPartName);
         if (isResultHeader) {
            returnParameter.setBinding(ParameterBinding.HEADER);
            javaMethod.addParameter(returnParameter);
            rTypeReference.setGlobalElement(true);
         } else {
            ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
            returnParameter.setBinding(rb);
            if (rb.isBody()) {
               rTypeReference.setGlobalElement(false);
               WSDLPart p = this.getPart(new QName(this.targetNamespace, operationName), resultPartName, WebParam.Mode.OUT);
               if (p == null) {
                  resRpcParams.put(resRpcParams.size() + 10000, returnParameter);
               } else {
                  resRpcParams.put(p.getIndex(), returnParameter);
               }
            } else {
               javaMethod.addParameter(returnParameter);
            }
         }
      }

      Class<?>[] parameterTypes = method.getParameterTypes();
      Type[] genericParameterTypes = method.getGenericParameterTypes();
      Annotation[][] pannotations = this.getParamAnnotations(method);
      int pos = 0;
      Class[] var48 = parameterTypes;
      int var28 = parameterTypes.length;

      for(int var29 = 0; var29 < var28; ++var29) {
         Class clazzType = var48[var29];
         String paramName = "";
         String paramNamespace = "";
         String partName = "";
         boolean isHeader = false;
         if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
            if (isHolder && clazzType == Holder.class) {
               clazzType = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }

            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            Annotation[] var37 = pannotations[pos];
            int var38 = var37.length;

            for(int var39 = 0; var39 < var38; ++var39) {
               Annotation annotation = var37[var39];
               if (annotation.annotationType() == WebParam.class) {
                  WebParam webParam = (WebParam)annotation;
                  paramName = webParam.name();
                  partName = webParam.partName();
                  isHeader = webParam.header();
                  WebParam.Mode mode = webParam.mode();
                  paramNamespace = webParam.targetNamespace();
                  if (isHolder && mode == WebParam.Mode.IN) {
                     mode = WebParam.Mode.INOUT;
                  }

                  paramMode = mode;
                  break;
               }
            }

            if (paramName.length() == 0) {
               paramName = "arg" + pos;
            }

            if (partName.length() == 0) {
               partName = paramName;
            } else if (!isHeader) {
               paramName = partName;
            }

            if (partName.length() == 0) {
               partName = paramName;
            }

            QName paramQName;
            if (!isHeader) {
               paramQName = new QName("", paramName);
            } else {
               if (paramNamespace.length() == 0) {
                  paramNamespace = this.targetNamespace;
               }

               paramQName = new QName(paramNamespace, paramName);
            }

            typeRef = new TypeInfo(paramQName, clazzType, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            param.setPartName(partName);
            ParameterBinding pb;
            if (paramMode == WebParam.Mode.INOUT) {
               pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
               param.setInBinding(pb);
               pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
               param.setOutBinding(pb);
            } else if (isHeader) {
               typeRef.setGlobalElement(true);
               param.setBinding(ParameterBinding.HEADER);
            } else {
               pb = this.getBinding(operationName, partName, false, paramMode);
               param.setBinding(pb);
            }

            if (param.getInBinding().isBody()) {
               typeRef.setGlobalElement(false);
               WSDLPart p;
               if (!param.isOUT()) {
                  p = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.IN);
                  if (p == null) {
                     reqRpcParams.put(reqRpcParams.size() + 10000, param);
                  } else {
                     reqRpcParams.put(p.getIndex(), param);
                  }
               }

               if (!param.isIN()) {
                  if (isOneway) {
                     throw new RuntimeModelerException("runtime.modeler.oneway.operation.no.out.parameters", new Object[]{this.portClass.getCanonicalName(), methodName});
                  }

                  p = this.getPart(new QName(this.targetNamespace, operationName), partName, WebParam.Mode.OUT);
                  if (p == null) {
                     resRpcParams.put(resRpcParams.size() + 10000, param);
                  } else {
                     resRpcParams.put(p.getIndex(), param);
                  }
               }
            } else {
               javaMethod.addParameter(param);
            }
         }
      }

      Iterator var49 = reqRpcParams.values().iterator();

      ParameterImpl p;
      while(var49.hasNext()) {
         p = (ParameterImpl)var49.next();
         requestWrapper.addWrapperChild(p);
      }

      var49 = resRpcParams.values().iterator();

      while(var49.hasNext()) {
         p = (ParameterImpl)var49.next();
         responseWrapper.addWrapperChild(p);
      }

      this.processExceptions(javaMethod, method);
   }

   protected void processExceptions(JavaMethodImpl javaMethod, Method method) {
      Action actionAnn = (Action)this.getAnnotation(method, Action.class);
      FaultAction[] faultActions = new FaultAction[0];
      if (actionAnn != null) {
         faultActions = actionAnn.fault();
      }

      Class[] var5 = method.getExceptionTypes();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Class<?> exception = var5[var7];
         if (EXCEPTION_CLASS.isAssignableFrom(exception) && !RUNTIME_EXCEPTION_CLASS.isAssignableFrom(exception) && !REMOTE_EXCEPTION_CLASS.isAssignableFrom(exception)) {
            WebFault webFault = (WebFault)this.getAnnotation(exception, WebFault.class);
            Method faultInfoMethod = this.getWSDLExceptionFaultInfo(exception);
            ExceptionType exceptionType = ExceptionType.WSDLException;
            String namespace = this.targetNamespace;
            String name = exception.getSimpleName();
            String beanPackage = this.packageName + ".jaxws.";
            if (this.packageName.length() == 0) {
               beanPackage = "jaxws.";
            }

            String className = beanPackage + name + "Bean";
            String messageName = exception.getSimpleName();
            if (webFault != null) {
               if (webFault.faultBean().length() > 0) {
                  className = webFault.faultBean();
               }

               if (webFault.name().length() > 0) {
                  name = webFault.name();
               }

               if (webFault.targetNamespace().length() > 0) {
                  namespace = webFault.targetNamespace();
               }

               if (webFault.messageName().length() > 0) {
                  messageName = webFault.messageName();
               }
            }

            Class exceptionBean;
            Annotation[] anns;
            if (faultInfoMethod == null) {
               exceptionBean = this.getExceptionBeanClass(className, exception, name, namespace);
               exceptionType = ExceptionType.UserDefined;
               anns = this.getAnnotations(exceptionBean);
            } else {
               exceptionBean = faultInfoMethod.getReturnType();
               anns = this.getAnnotations(faultInfoMethod);
            }

            QName faultName = new QName(namespace, name);
            TypeInfo typeRef = new TypeInfo(faultName, exceptionBean, anns);
            CheckedExceptionImpl checkedException = new CheckedExceptionImpl(javaMethod, exception, typeRef, exceptionType);
            checkedException.setMessageName(messageName);
            FaultAction[] var22 = faultActions;
            int var23 = faultActions.length;

            for(int var24 = 0; var24 < var23; ++var24) {
               FaultAction fa = var22[var24];
               if (fa.className().equals(exception) && !fa.value().equals("")) {
                  checkedException.setFaultAction(fa.value());
                  break;
               }
            }

            javaMethod.addException(checkedException);
         }
      }

   }

   protected Method getWSDLExceptionFaultInfo(Class exception) {
      if (this.getAnnotation(exception, WebFault.class) == null) {
         return null;
      } else {
         try {
            return exception.getMethod("getFaultInfo");
         } catch (NoSuchMethodException var3) {
            return null;
         }
      }
   }

   protected void processDocBareMethod(JavaMethodImpl javaMethod, String operationName, Method method) {
      String resultName = operationName + "Response";
      String resultTNS = this.targetNamespace;
      String resultPartName = null;
      boolean isResultHeader = false;
      WebResult webResult = (WebResult)this.getAnnotation(method, WebResult.class);
      if (webResult != null) {
         if (webResult.name().length() > 0) {
            resultName = webResult.name();
         }

         if (webResult.targetNamespace().length() > 0) {
            resultTNS = webResult.targetNamespace();
         }

         resultPartName = webResult.partName();
         isResultHeader = webResult.header();
      }

      Class returnType = method.getReturnType();
      Type gReturnType = method.getGenericReturnType();
      if (javaMethod.isAsync()) {
         returnType = this.getAsyncReturnType(method, returnType);
      }

      if (returnType != null && !returnType.getName().equals("void")) {
         Annotation[] rann = this.getAnnotations(method);
         if (resultName != null) {
            QName responseQName = new QName(resultTNS, resultName);
            TypeInfo rTypeReference = new TypeInfo(responseQName, returnType, rann);
            rTypeReference.setGenericType(gReturnType);
            this.metadataReader.getProperties(rTypeReference.properties(), method);
            ParameterImpl returnParameter = new ParameterImpl(javaMethod, rTypeReference, WebParam.Mode.OUT, -1);
            if (resultPartName == null || resultPartName.length() == 0) {
               resultPartName = resultName;
            }

            returnParameter.setPartName(resultPartName);
            if (isResultHeader) {
               returnParameter.setBinding(ParameterBinding.HEADER);
            } else {
               ParameterBinding rb = this.getBinding(operationName, resultPartName, false, WebParam.Mode.OUT);
               returnParameter.setBinding(rb);
            }

            javaMethod.addParameter(returnParameter);
         }
      }

      Class<?>[] parameterTypes = method.getParameterTypes();
      Type[] genericParameterTypes = method.getGenericParameterTypes();
      Annotation[][] pannotations = this.getParamAnnotations(method);
      int pos = 0;
      Class[] var34 = parameterTypes;
      int var16 = parameterTypes.length;

      for(int var17 = 0; var17 < var16; ++var17) {
         Class clazzType = var34[var17];
         String paramName = operationName;
         String partName = null;
         String requestNamespace = this.targetNamespace;
         boolean isHeader = false;
         if (!javaMethod.isAsync() || !AsyncHandler.class.isAssignableFrom(clazzType)) {
            boolean isHolder = HOLDER_CLASS.isAssignableFrom(clazzType);
            if (isHolder && clazzType == Holder.class) {
               clazzType = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0]);
            }

            WebParam.Mode paramMode = isHolder ? WebParam.Mode.INOUT : WebParam.Mode.IN;
            Annotation[] var25 = pannotations[pos];
            int var26 = var25.length;

            for(int var27 = 0; var27 < var26; ++var27) {
               Annotation annotation = var25[var27];
               if (annotation.annotationType() == WebParam.class) {
                  WebParam webParam = (WebParam)annotation;
                  paramMode = webParam.mode();
                  if (isHolder && paramMode == WebParam.Mode.IN) {
                     paramMode = WebParam.Mode.INOUT;
                  }

                  isHeader = webParam.header();
                  if (isHeader) {
                     paramName = "arg" + pos;
                  }

                  if (paramMode == WebParam.Mode.OUT && !isHeader) {
                     paramName = operationName + "Response";
                  }

                  if (webParam.name().length() > 0) {
                     paramName = webParam.name();
                  }

                  partName = webParam.partName();
                  if (!webParam.targetNamespace().equals("")) {
                     requestNamespace = webParam.targetNamespace();
                  }
                  break;
               }
            }

            QName requestQName = new QName(requestNamespace, paramName);
            if (!isHeader && paramMode != WebParam.Mode.OUT) {
               javaMethod.setRequestPayloadName(requestQName);
            }

            TypeInfo typeRef = new TypeInfo(requestQName, clazzType, pannotations[pos]);
            this.metadataReader.getProperties(typeRef.properties(), method, pos);
            typeRef.setGenericType(genericParameterTypes[pos]);
            ParameterImpl param = new ParameterImpl(javaMethod, typeRef, paramMode, pos++);
            if (partName == null || partName.length() == 0) {
               partName = paramName;
            }

            param.setPartName(partName);
            ParameterBinding pb;
            if (paramMode == WebParam.Mode.INOUT) {
               pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.IN);
               param.setInBinding(pb);
               pb = this.getBinding(operationName, partName, isHeader, WebParam.Mode.OUT);
               param.setOutBinding(pb);
            } else if (isHeader) {
               param.setBinding(ParameterBinding.HEADER);
            } else {
               pb = this.getBinding(operationName, partName, false, paramMode);
               param.setBinding(pb);
            }

            javaMethod.addParameter(param);
         }
      }

      this.validateDocBare(javaMethod);
      this.processExceptions(javaMethod, method);
   }

   private void validateDocBare(JavaMethodImpl javaMethod) {
      int numInBodyBindings = 0;
      Iterator var3 = javaMethod.getRequestParameters().iterator();

      do {
         if (!var3.hasNext()) {
            int numOutBodyBindings = 0;
            Iterator var7 = javaMethod.getResponseParameters().iterator();

            do {
               if (!var7.hasNext()) {
                  return;
               }

               Parameter param = (Parameter)var7.next();
               if (param.getBinding().equals(ParameterBinding.BODY) && param.isOUT()) {
                  ++numOutBodyBindings;
               }
            } while(numOutBodyBindings <= 1);

            throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
         }

         Parameter param = (Parameter)var3.next();
         if (param.getBinding().equals(ParameterBinding.BODY) && param.isIN()) {
            ++numInBodyBindings;
         }
      } while(numInBodyBindings <= 1);

      throw new RuntimeModelerException(ModelerMessages.localizableNOT_A_VALID_BARE_METHOD(this.portClass.getName(), javaMethod.getMethod().getName()));
   }

   private Class getAsyncReturnType(Method method, Class returnType) {
      if (Response.class.isAssignableFrom(returnType)) {
         Type ret = method.getGenericReturnType();
         return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)ret).getActualTypeArguments()[0]);
      } else {
         Type[] types = method.getGenericParameterTypes();
         Class[] params = method.getParameterTypes();
         int i = 0;
         Class[] var6 = params;
         int var7 = params.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Class cls = var6[var8];
            if (AsyncHandler.class.isAssignableFrom(cls)) {
               return (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)types[i]).getActualTypeArguments()[0]);
            }

            ++i;
         }

         return returnType;
      }
   }

   public static String capitalize(String name) {
      if (name != null && name.length() != 0) {
         char[] chars = name.toCharArray();
         chars[0] = Character.toUpperCase(chars[0]);
         return new String(chars);
      } else {
         return name;
      }
   }

   public static QName getServiceName(Class<?> implClass) {
      return getServiceName(implClass, (MetadataReader)null);
   }

   public static QName getServiceName(Class<?> implClass, boolean isStandard) {
      return getServiceName(implClass, (MetadataReader)null, isStandard);
   }

   public static QName getServiceName(Class<?> implClass, MetadataReader reader) {
      return getServiceName(implClass, reader, true);
   }

   public static QName getServiceName(Class<?> implClass, MetadataReader reader, boolean isStandard) {
      if (implClass.isInterface()) {
         throw new RuntimeModelerException("runtime.modeler.cannot.get.serviceName.from.interface", new Object[]{implClass.getCanonicalName()});
      } else {
         String name = implClass.getSimpleName() + "Service";
         String packageName = "";
         if (implClass.getPackage() != null) {
            packageName = implClass.getPackage().getName();
         }

         WebService webService = (WebService)getAnnotation(WebService.class, implClass, reader);
         if (isStandard && webService == null) {
            throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[]{implClass.getCanonicalName()});
         } else {
            if (webService != null && webService.serviceName().length() > 0) {
               name = webService.serviceName();
            }

            String targetNamespace = getNamespace(packageName);
            if (webService != null && webService.targetNamespace().length() > 0) {
               targetNamespace = webService.targetNamespace();
            } else if (targetNamespace == null) {
               throw new RuntimeModelerException("runtime.modeler.no.package", new Object[]{implClass.getName()});
            }

            return new QName(targetNamespace, name);
         }
      }
   }

   public static QName getPortName(Class<?> implClass, String targetNamespace) {
      return getPortName(implClass, (MetadataReader)null, targetNamespace);
   }

   public static QName getPortName(Class<?> implClass, String targetNamespace, boolean isStandard) {
      return getPortName(implClass, (MetadataReader)null, targetNamespace, isStandard);
   }

   public static QName getPortName(Class<?> implClass, MetadataReader reader, String targetNamespace) {
      return getPortName(implClass, reader, targetNamespace, true);
   }

   public static QName getPortName(Class<?> implClass, MetadataReader reader, String targetNamespace, boolean isStandard) {
      WebService webService = (WebService)getAnnotation(WebService.class, implClass, reader);
      if (isStandard && webService == null) {
         throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[]{implClass.getCanonicalName()});
      } else {
         String name;
         if (webService != null && webService.portName().length() > 0) {
            name = webService.portName();
         } else if (webService != null && webService.name().length() > 0) {
            name = webService.name() + "Port";
         } else {
            name = implClass.getSimpleName() + "Port";
         }

         if (targetNamespace == null) {
            if (webService != null && webService.targetNamespace().length() > 0) {
               targetNamespace = webService.targetNamespace();
            } else {
               String packageName = null;
               if (implClass.getPackage() != null) {
                  packageName = implClass.getPackage().getName();
               }

               if (packageName != null) {
                  targetNamespace = getNamespace(packageName);
               }

               if (targetNamespace == null) {
                  throw new RuntimeModelerException("runtime.modeler.no.package", new Object[]{implClass.getName()});
               }
            }
         }

         return new QName(targetNamespace, name);
      }
   }

   static <A extends Annotation> A getAnnotation(Class<A> t, Class<?> cls, MetadataReader reader) {
      return reader == null ? cls.getAnnotation(t) : reader.getAnnotation(t, cls);
   }

   public static QName getPortTypeName(Class<?> implOrSeiClass) {
      return getPortTypeName(implOrSeiClass, (String)null, (MetadataReader)null);
   }

   public static QName getPortTypeName(Class<?> implOrSeiClass, MetadataReader metadataReader) {
      return getPortTypeName(implOrSeiClass, (String)null, metadataReader);
   }

   public static QName getPortTypeName(Class<?> implOrSeiClass, String tns, MetadataReader reader) {
      assert implOrSeiClass != null;

      WebService webService = (WebService)getAnnotation(WebService.class, implOrSeiClass, reader);
      Class<?> clazz = implOrSeiClass;
      if (webService == null) {
         throw new RuntimeModelerException("runtime.modeler.no.webservice.annotation", new Object[]{implOrSeiClass.getCanonicalName()});
      } else {
         String name;
         if (!implOrSeiClass.isInterface()) {
            name = webService.endpointInterface();
            if (name.length() > 0) {
               try {
                  clazz = Thread.currentThread().getContextClassLoader().loadClass(name);
               } catch (ClassNotFoundException var7) {
                  throw new RuntimeModelerException("runtime.modeler.class.not.found", new Object[]{name});
               }

               WebService ws = (WebService)getAnnotation(WebService.class, clazz, reader);
               if (ws == null) {
                  throw new RuntimeModelerException("runtime.modeler.endpoint.interface.no.webservice", new Object[]{webService.endpointInterface()});
               }
            }
         }

         webService = (WebService)getAnnotation(WebService.class, clazz, reader);
         name = webService.name();
         if (name.length() == 0) {
            name = clazz.getSimpleName();
         }

         if (tns == null || "".equals(tns.trim())) {
            tns = webService.targetNamespace();
         }

         if (tns.length() == 0) {
            tns = getNamespace(clazz.getPackage().getName());
         }

         if (tns == null) {
            throw new RuntimeModelerException("runtime.modeler.no.package", new Object[]{clazz.getName()});
         } else {
            return new QName(tns, name);
         }
      }
   }

   private ParameterBinding getBinding(String operation, String part, boolean isHeader, WebParam.Mode mode) {
      if (this.binding == null) {
         return isHeader ? ParameterBinding.HEADER : ParameterBinding.BODY;
      } else {
         QName opName = new QName(this.binding.getBinding().getPortType().getName().getNamespaceURI(), operation);
         return this.binding.getBinding().getBinding(opName, part, mode);
      }
   }

   private WSDLPart getPart(QName opName, String partName, WebParam.Mode mode) {
      if (this.binding != null) {
         WSDLBoundOperation bo = this.binding.getBinding().get(opName);
         if (bo != null) {
            return bo.getPart(partName, mode);
         }
      }

      return null;
   }

   private static Boolean getBooleanSystemProperty(final String prop) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String value = System.getProperty(prop);
            return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
         }
      });
   }

   private static QName getReturnQName(Method method, WebResult webResult, XmlElement xmlElem) {
      String webResultName = null;
      if (webResult != null && webResult.name().length() > 0) {
         webResultName = webResult.name();
      }

      String xmlElemName = null;
      if (xmlElem != null && !xmlElem.name().equals("##default")) {
         xmlElemName = xmlElem.name();
      }

      if (xmlElemName != null && webResultName != null && !xmlElemName.equals(webResultName)) {
         throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebResult(name)=" + webResultName + " are different for method " + method, new Object[0]);
      } else {
         String localPart = "return";
         if (webResultName != null) {
            localPart = webResultName;
         } else if (xmlElemName != null) {
            localPart = xmlElemName;
         }

         String webResultNS = null;
         if (webResult != null && webResult.targetNamespace().length() > 0) {
            webResultNS = webResult.targetNamespace();
         }

         String xmlElemNS = null;
         if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
         }

         if (xmlElemNS != null && webResultNS != null && !xmlElemNS.equals(webResultNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebResult(targetNamespace)=" + webResultNS + " are different for method " + method, new Object[0]);
         } else {
            String ns = "";
            if (webResultNS != null) {
               ns = webResultNS;
            } else if (xmlElemNS != null) {
               ns = xmlElemNS;
            }

            return new QName(ns, localPart);
         }
      }
   }

   private static QName getParameterQName(Method method, WebParam webParam, XmlElement xmlElem, String paramDefault) {
      String webParamName = null;
      if (webParam != null && webParam.name().length() > 0) {
         webParamName = webParam.name();
      }

      String xmlElemName = null;
      if (xmlElem != null && !xmlElem.name().equals("##default")) {
         xmlElemName = xmlElem.name();
      }

      if (xmlElemName != null && webParamName != null && !xmlElemName.equals(webParamName)) {
         throw new RuntimeModelerException("@XmlElement(name)=" + xmlElemName + " and @WebParam(name)=" + webParamName + " are different for method " + method, new Object[0]);
      } else {
         String localPart = paramDefault;
         if (webParamName != null) {
            localPart = webParamName;
         } else if (xmlElemName != null) {
            localPart = xmlElemName;
         }

         String webParamNS = null;
         if (webParam != null && webParam.targetNamespace().length() > 0) {
            webParamNS = webParam.targetNamespace();
         }

         String xmlElemNS = null;
         if (xmlElem != null && !xmlElem.namespace().equals("##default")) {
            xmlElemNS = xmlElem.namespace();
         }

         if (xmlElemNS != null && webParamNS != null && !xmlElemNS.equals(webParamNS)) {
            throw new RuntimeModelerException("@XmlElement(namespace)=" + xmlElemNS + " and @WebParam(targetNamespace)=" + webParamNS + " are different for method " + method, new Object[0]);
         } else {
            String ns = "";
            if (webParamNS != null) {
               ns = webParamNS;
            } else if (xmlElemNS != null) {
               ns = xmlElemNS;
            }

            return new QName(ns, localPart);
         }
      }
   }
}
