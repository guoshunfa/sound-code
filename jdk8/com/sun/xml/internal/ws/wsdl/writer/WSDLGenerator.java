package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.bind.v2.schemagen.Util;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Element;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.txw2.TXW;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.output.ResultFactory;
import com.sun.xml.internal.txw2.output.TXWResult;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLGeneratorExtension;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.writer.document.Binding;
import com.sun.xml.internal.ws.wsdl.writer.document.BindingOperationType;
import com.sun.xml.internal.ws.wsdl.writer.document.Definitions;
import com.sun.xml.internal.ws.wsdl.writer.document.Fault;
import com.sun.xml.internal.ws.wsdl.writer.document.FaultType;
import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import com.sun.xml.internal.ws.wsdl.writer.document.Message;
import com.sun.xml.internal.ws.wsdl.writer.document.Operation;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import com.sun.xml.internal.ws.wsdl.writer.document.Port;
import com.sun.xml.internal.ws.wsdl.writer.document.PortType;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.sun.xml.internal.ws.wsdl.writer.document.Types;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Body;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.BodyType;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Header;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPFault;
import com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPAddress;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;

public class WSDLGenerator {
   private WSDLGenerator.JAXWSOutputSchemaResolver resolver;
   private com.oracle.webservices.internal.api.databinding.WSDLResolver wsdlResolver;
   private AbstractSEIModelImpl model;
   private Definitions serviceDefinitions;
   private Definitions portDefinitions;
   private Types types;
   private static final String DOT_WSDL = ".wsdl";
   private static final String RESPONSE = "Response";
   private static final String PARAMETERS = "parameters";
   private static final String RESULT = "parameters";
   private static final String UNWRAPPABLE_RESULT = "result";
   private static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
   private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
   private static final String XSD_PREFIX = "xsd";
   private static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap/";
   private static final String SOAP12_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/soap12/";
   private static final String SOAP_PREFIX = "soap";
   private static final String SOAP12_PREFIX = "soap12";
   private static final String TNS_PREFIX = "tns";
   private static final String DOCUMENT = "document";
   private static final String RPC = "rpc";
   private static final String LITERAL = "literal";
   private static final String REPLACE_WITH_ACTUAL_URL = "REPLACE_WITH_ACTUAL_URL";
   private Set<QName> processedExceptions;
   private WSBinding binding;
   private String wsdlLocation;
   private String portWSDLID;
   private String schemaPrefix;
   private WSDLGeneratorExtension extension;
   List<WSDLGeneratorExtension> extensionHandlers;
   private String endpointAddress;
   private Container container;
   private final Class implType;
   private boolean inlineSchemas;
   private final boolean disableXmlSecurity;

   public WSDLGenerator(AbstractSEIModelImpl model, com.oracle.webservices.internal.api.databinding.WSDLResolver wsdlResolver, WSBinding binding, Container container, Class implType, boolean inlineSchemas, WSDLGeneratorExtension... extensions) {
      this(model, wsdlResolver, binding, container, implType, inlineSchemas, false, extensions);
   }

   public WSDLGenerator(AbstractSEIModelImpl model, com.oracle.webservices.internal.api.databinding.WSDLResolver wsdlResolver, WSBinding binding, Container container, Class implType, boolean inlineSchemas, boolean disableXmlSecurity, WSDLGeneratorExtension... extensions) {
      this.wsdlResolver = null;
      this.processedExceptions = new HashSet();
      this.endpointAddress = "REPLACE_WITH_ACTUAL_URL";
      this.model = model;
      this.resolver = new WSDLGenerator.JAXWSOutputSchemaResolver();
      this.wsdlResolver = wsdlResolver;
      this.binding = binding;
      this.container = container;
      this.implType = implType;
      this.extensionHandlers = new ArrayList();
      this.inlineSchemas = inlineSchemas;
      this.disableXmlSecurity = disableXmlSecurity;
      this.register(new W3CAddressingWSDLGeneratorExtension());
      this.register(new W3CAddressingMetadataWSDLGeneratorExtension());
      this.register(new PolicyWSDLGeneratorExtension());
      WSDLGeneratorExtension[] wsdlGeneratorExtensions;
      int var11;
      if (container != null) {
         wsdlGeneratorExtensions = (WSDLGeneratorExtension[])container.getSPI(WSDLGeneratorExtension[].class);
         if (wsdlGeneratorExtensions != null) {
            WSDLGeneratorExtension[] var10 = wsdlGeneratorExtensions;
            var11 = wsdlGeneratorExtensions.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               WSDLGeneratorExtension wsdlGeneratorExtension = var10[var12];
               this.register(wsdlGeneratorExtension);
            }
         }
      }

      wsdlGeneratorExtensions = extensions;
      int var14 = extensions.length;

      for(var11 = 0; var11 < var14; ++var11) {
         WSDLGeneratorExtension w = wsdlGeneratorExtensions[var11];
         this.register(w);
      }

      this.extension = new WSDLGeneratorExtensionFacade((WSDLGeneratorExtension[])this.extensionHandlers.toArray(new WSDLGeneratorExtension[0]));
   }

   public void setEndpointAddress(String address) {
      this.endpointAddress = address;
   }

   protected String mangleName(String name) {
      return BindingHelper.mangleNameToClassName(name);
   }

   public void doGeneration() {
      XmlSerializer portWriter = null;
      String fileName = this.mangleName(this.model.getServiceQName().getLocalPart());
      Result result = this.wsdlResolver.getWSDL(fileName + ".wsdl");
      this.wsdlLocation = result.getSystemId();
      XmlSerializer serviceWriter = new WSDLGenerator.CommentFilter(ResultFactory.createSerializer(result));
      if (this.model.getServiceQName().getNamespaceURI().equals(this.model.getTargetNamespace())) {
         portWriter = serviceWriter;
         this.schemaPrefix = fileName + "_";
      } else {
         String wsdlName = this.mangleName(this.model.getPortTypeName().getLocalPart());
         if (wsdlName.equals(fileName)) {
            wsdlName = wsdlName + "PortType";
         }

         Holder<String> absWSDLName = new Holder();
         absWSDLName.value = wsdlName + ".wsdl";
         result = this.wsdlResolver.getAbstractWSDL(absWSDLName);
         if (result != null) {
            this.portWSDLID = result.getSystemId();
            if (this.portWSDLID.equals(this.wsdlLocation)) {
               portWriter = serviceWriter;
            } else {
               portWriter = new WSDLGenerator.CommentFilter(ResultFactory.createSerializer(result));
            }
         } else {
            this.portWSDLID = (String)absWSDLName.value;
         }

         this.schemaPrefix = (new File(this.portWSDLID)).getName();
         int idx = this.schemaPrefix.lastIndexOf(46);
         if (idx > 0) {
            this.schemaPrefix = this.schemaPrefix.substring(0, idx);
         }

         this.schemaPrefix = this.mangleName(this.schemaPrefix) + "_";
      }

      this.generateDocument(serviceWriter, portWriter);
   }

   private void generateDocument(XmlSerializer serviceStream, XmlSerializer portStream) {
      this.serviceDefinitions = (Definitions)TXW.create(Definitions.class, serviceStream);
      this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
      this.serviceDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
      this.serviceDefinitions.targetNamespace(this.model.getServiceQName().getNamespaceURI());
      this.serviceDefinitions._namespace(this.model.getServiceQName().getNamespaceURI(), "tns");
      if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
         this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap12/", "soap12");
      } else {
         this.serviceDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/soap/", "soap");
      }

      this.serviceDefinitions.name(this.model.getServiceQName().getLocalPart());
      WSDLGenExtnContext serviceCtx = new WSDLGenExtnContext(this.serviceDefinitions, this.model, this.binding, this.container, this.implType);
      this.extension.start(serviceCtx);
      String schemaLoc;
      Import _import;
      if (serviceStream != portStream && portStream != null) {
         this.portDefinitions = (Definitions)TXW.create(Definitions.class, portStream);
         this.portDefinitions._namespace("http://schemas.xmlsoap.org/wsdl/", "");
         this.portDefinitions._namespace("http://www.w3.org/2001/XMLSchema", "xsd");
         if (this.model.getTargetNamespace() != null) {
            this.portDefinitions.targetNamespace(this.model.getTargetNamespace());
            this.portDefinitions._namespace(this.model.getTargetNamespace(), "tns");
         }

         schemaLoc = relativize(this.portWSDLID, this.wsdlLocation);
         _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
         _import.location(schemaLoc);
      } else if (portStream != null) {
         this.portDefinitions = this.serviceDefinitions;
      } else {
         schemaLoc = relativize(this.portWSDLID, this.wsdlLocation);
         _import = this.serviceDefinitions._import().namespace(this.model.getTargetNamespace());
         _import.location(schemaLoc);
      }

      this.extension.addDefinitionsExtension(this.serviceDefinitions);
      if (this.portDefinitions != null) {
         this.generateTypes();
         this.generateMessages();
         this.generatePortType();
      }

      this.generateBinding();
      this.generateService();
      this.extension.end(serviceCtx);
      this.serviceDefinitions.commit();
      if (this.portDefinitions != null && this.portDefinitions != this.serviceDefinitions) {
         this.portDefinitions.commit();
      }

   }

   protected void generateTypes() {
      this.types = this.portDefinitions.types();
      if (this.model.getBindingContext() != null) {
         if (this.inlineSchemas && this.model.getBindingContext().getClass().getName().indexOf("glassfish") == -1) {
            this.resolver.nonGlassfishSchemas = new ArrayList();
         }

         try {
            this.model.getBindingContext().generateSchema(this.resolver);
         } catch (IOException var7) {
            throw new WebServiceException(var7.getMessage());
         }
      }

      if (this.resolver.nonGlassfishSchemas != null) {
         TransformerFactory tf = XmlUtil.newTransformerFactory(!this.disableXmlSecurity);

         try {
            Transformer t = tf.newTransformer();
            Iterator var3 = this.resolver.nonGlassfishSchemas.iterator();

            while(var3.hasNext()) {
               DOMResult xsd = (DOMResult)var3.next();
               Document doc = (Document)xsd.getNode();
               SAXResult sax = new SAXResult(new TXWContentHandler(this.types));
               t.transform(new DOMSource(doc.getDocumentElement()), sax);
            }
         } catch (TransformerConfigurationException var8) {
            throw new WebServiceException(var8.getMessage(), var8);
         } catch (TransformerException var9) {
            throw new WebServiceException(var9.getMessage(), var9);
         }
      }

      this.generateWrappers();
   }

   void generateWrappers() {
      List<WrapperParameter> wrappers = new ArrayList();
      Iterator var2 = this.model.getJavaMethods().iterator();

      while(true) {
         JavaMethodImpl method;
         do {
            if (!var2.hasNext()) {
               if (wrappers.isEmpty()) {
                  return;
               }

               HashMap<String, Schema> xsds = new HashMap();
               Iterator var17 = wrappers.iterator();

               while(var17.hasNext()) {
                  WrapperParameter wp = (WrapperParameter)var17.next();
                  String tns = wp.getName().getNamespaceURI();
                  Schema xsd = (Schema)xsds.get(tns);
                  if (xsd == null) {
                     xsd = this.types.schema();
                     xsd.targetNamespace(tns);
                     xsds.put(tns, xsd);
                  }

                  Element e = (Element)xsd._element(Element.class);
                  e._attribute("name", wp.getName().getLocalPart());
                  e.type(wp.getName());
                  ComplexType ct = (ComplexType)xsd._element(ComplexType.class);
                  ct._attribute("name", wp.getName().getLocalPart());
                  ExplicitGroup sq = ct.sequence();
                  Iterator var10 = wp.getWrapperChildren().iterator();

                  while(var10.hasNext()) {
                     ParameterImpl p = (ParameterImpl)var10.next();
                     if (p.getBinding().isBody()) {
                        LocalElement le = sq.element();
                        le._attribute("name", p.getName().getLocalPart());
                        TypeInfo typeInfo = p.getItemType();
                        boolean repeatedElement = false;
                        if (typeInfo == null) {
                           typeInfo = p.getTypeInfo();
                        } else {
                           repeatedElement = true;
                        }

                        QName type = this.model.getBindingContext().getTypeName(typeInfo);
                        le.type(type);
                        if (repeatedElement) {
                           le.minOccurs(0);
                           le.maxOccurs("unbounded");
                        }
                     }
                  }
               }

               return;
            }

            method = (JavaMethodImpl)var2.next();
         } while(method.getBinding().isRpcLit());

         Iterator var4 = method.getRequestParameters().iterator();

         ParameterImpl p;
         while(var4.hasNext()) {
            p = (ParameterImpl)var4.next();
            if (p instanceof WrapperParameter && WrapperComposite.class.equals(((WrapperParameter)p).getTypeInfo().type)) {
               wrappers.add((WrapperParameter)p);
            }
         }

         var4 = method.getResponseParameters().iterator();

         while(var4.hasNext()) {
            p = (ParameterImpl)var4.next();
            if (p instanceof WrapperParameter && WrapperComposite.class.equals(((WrapperParameter)p).getTypeInfo().type)) {
               wrappers.add((WrapperParameter)p);
            }
         }
      }
   }

   protected void generateMessages() {
      Iterator var1 = this.model.getJavaMethods().iterator();

      while(var1.hasNext()) {
         JavaMethodImpl method = (JavaMethodImpl)var1.next();
         this.generateSOAPMessages(method, method.getBinding());
      }

   }

   protected void generateSOAPMessages(JavaMethodImpl method, SOAPBinding binding) {
      boolean isDoclit = binding.isDocLit();
      Message message = this.portDefinitions.message().name(method.getRequestMessageName());
      this.extension.addInputMessageExtension(message, method);
      BindingContext jaxbContext = this.model.getBindingContext();
      boolean unwrappable = true;
      Iterator var8 = method.getRequestParameters().iterator();

      while(true) {
         Part part;
         ParameterImpl param;
         Iterator var10;
         ParameterImpl childParam;
         while(var8.hasNext()) {
            param = (ParameterImpl)var8.next();
            if (isDoclit) {
               if (this.isHeaderParameter(param)) {
                  unwrappable = false;
               }

               part = message.part().name(param.getPartName());
               part.element(param.getName());
            } else if (param.isWrapperStyle()) {
               var10 = ((WrapperParameter)param).getWrapperChildren().iterator();

               while(var10.hasNext()) {
                  childParam = (ParameterImpl)var10.next();
                  part = message.part().name(childParam.getPartName());
                  part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
               }
            } else {
               part = message.part().name(param.getPartName());
               part.element(param.getName());
            }
         }

         if (method.getMEP() != MEP.ONE_WAY) {
            message = this.portDefinitions.message().name(method.getResponseMessageName());
            this.extension.addOutputMessageExtension(message, method);
            var8 = method.getResponseParameters().iterator();

            label56:
            while(true) {
               while(true) {
                  if (!var8.hasNext()) {
                     break label56;
                  }

                  param = (ParameterImpl)var8.next();
                  if (isDoclit) {
                     part = message.part().name(param.getPartName());
                     part.element(param.getName());
                  } else if (param.isWrapperStyle()) {
                     var10 = ((WrapperParameter)param).getWrapperChildren().iterator();

                     while(var10.hasNext()) {
                        childParam = (ParameterImpl)var10.next();
                        part = message.part().name(childParam.getPartName());
                        part.type(jaxbContext.getTypeName(childParam.getXMLBridge().getTypeInfo()));
                     }
                  } else {
                     part = message.part().name(param.getPartName());
                     part.element(param.getName());
                  }
               }
            }
         }

         var8 = method.getCheckedExceptions().iterator();

         while(var8.hasNext()) {
            CheckedExceptionImpl exception = (CheckedExceptionImpl)var8.next();
            QName tagName = exception.getDetailType().tagName;
            String messageName = exception.getMessageName();
            QName messageQName = new QName(this.model.getTargetNamespace(), messageName);
            if (!this.processedExceptions.contains(messageQName)) {
               message = this.portDefinitions.message().name(messageName);
               this.extension.addFaultMessageExtension(message, method, exception);
               part = message.part().name("fault");
               part.element(tagName);
               this.processedExceptions.add(messageQName);
            }
         }

         return;
      }
   }

   protected void generatePortType() {
      PortType portType = this.portDefinitions.portType().name(this.model.getPortTypeName().getLocalPart());
      this.extension.addPortTypeExtension(portType);
      Iterator var2 = this.model.getJavaMethods().iterator();

      while(var2.hasNext()) {
         JavaMethodImpl method = (JavaMethodImpl)var2.next();
         Operation operation = portType.operation().name(method.getOperationName());
         this.generateParameterOrder(operation, method);
         this.extension.addOperationExtension(operation, method);
         switch(method.getMEP()) {
         case REQUEST_RESPONSE:
            this.generateInputMessage(operation, method);
            this.generateOutputMessage(operation, method);
            break;
         case ONE_WAY:
            this.generateInputMessage(operation, method);
         }

         Iterator var5 = method.getCheckedExceptions().iterator();

         while(var5.hasNext()) {
            CheckedExceptionImpl exception = (CheckedExceptionImpl)var5.next();
            QName messageName = new QName(this.model.getTargetNamespace(), exception.getMessageName());
            FaultType paramType = operation.fault().message(messageName).name(exception.getMessageName());
            this.extension.addOperationFaultExtension(paramType, method, exception);
         }
      }

   }

   protected boolean isWrapperStyle(JavaMethodImpl method) {
      if (method.getRequestParameters().size() > 0) {
         ParameterImpl param = (ParameterImpl)method.getRequestParameters().iterator().next();
         return param.isWrapperStyle();
      } else {
         return false;
      }
   }

   protected boolean isRpcLit(JavaMethodImpl method) {
      return method.getBinding().getStyle() == javax.jws.soap.SOAPBinding.Style.RPC;
   }

   protected void generateParameterOrder(Operation operation, JavaMethodImpl method) {
      if (method.getMEP() != MEP.ONE_WAY) {
         if (this.isRpcLit(method)) {
            this.generateRpcParameterOrder(operation, method);
         } else {
            this.generateDocumentParameterOrder(operation, method);
         }

      }
   }

   protected void generateRpcParameterOrder(Operation operation, JavaMethodImpl method) {
      StringBuilder paramOrder = new StringBuilder();
      Set<String> partNames = new HashSet();
      List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
      int i = 0;
      Iterator var8 = sortedParams.iterator();

      while(var8.hasNext()) {
         ParameterImpl parameter = (ParameterImpl)var8.next();
         if (parameter.getIndex() >= 0) {
            String partName = parameter.getPartName();
            if (!partNames.contains(partName)) {
               if (i++ > 0) {
                  paramOrder.append(' ');
               }

               paramOrder.append(partName);
               partNames.add(partName);
            }
         }
      }

      if (i > 1) {
         operation.parameterOrder(paramOrder.toString());
      }

   }

   protected void generateDocumentParameterOrder(Operation operation, JavaMethodImpl method) {
      StringBuilder paramOrder = new StringBuilder();
      Set<String> partNames = new HashSet();
      List<ParameterImpl> sortedParams = this.sortMethodParameters(method);
      int i = 0;
      Iterator var8 = sortedParams.iterator();

      while(var8.hasNext()) {
         ParameterImpl parameter = (ParameterImpl)var8.next();
         if (parameter.getIndex() >= 0) {
            String partName = parameter.getPartName();
            if (!partNames.contains(partName)) {
               if (i++ > 0) {
                  paramOrder.append(' ');
               }

               paramOrder.append(partName);
               partNames.add(partName);
            }
         }
      }

      if (i > 1) {
         operation.parameterOrder(paramOrder.toString());
      }

   }

   protected List<ParameterImpl> sortMethodParameters(JavaMethodImpl method) {
      Set<ParameterImpl> paramSet = new HashSet();
      List<ParameterImpl> sortedParams = new ArrayList();
      Iterator params;
      ParameterImpl param;
      if (this.isRpcLit(method)) {
         params = method.getRequestParameters().iterator();

         while(params.hasNext()) {
            param = (ParameterImpl)params.next();
            if (param instanceof WrapperParameter) {
               paramSet.addAll(((WrapperParameter)param).getWrapperChildren());
            } else {
               paramSet.add(param);
            }
         }

         params = method.getResponseParameters().iterator();

         while(params.hasNext()) {
            param = (ParameterImpl)params.next();
            if (param instanceof WrapperParameter) {
               paramSet.addAll(((WrapperParameter)param).getWrapperChildren());
            } else {
               paramSet.add(param);
            }
         }
      } else {
         paramSet.addAll(method.getRequestParameters());
         paramSet.addAll(method.getResponseParameters());
      }

      params = paramSet.iterator();
      if (paramSet.isEmpty()) {
         return sortedParams;
      } else {
         param = (ParameterImpl)params.next();
         sortedParams.add(param);

         for(int i = 1; i < paramSet.size(); ++i) {
            param = (ParameterImpl)params.next();

            int pos;
            for(pos = 0; pos < i; ++pos) {
               ParameterImpl sortedParam = (ParameterImpl)sortedParams.get(pos);
               if (param.getIndex() == sortedParam.getIndex() && param instanceof WrapperParameter || param.getIndex() < sortedParam.getIndex()) {
                  break;
               }
            }

            sortedParams.add(pos, param);
         }

         return sortedParams;
      }
   }

   protected boolean isBodyParameter(ParameterImpl parameter) {
      ParameterBinding paramBinding = parameter.getBinding();
      return paramBinding.isBody();
   }

   protected boolean isHeaderParameter(ParameterImpl parameter) {
      ParameterBinding paramBinding = parameter.getBinding();
      return paramBinding.isHeader();
   }

   protected boolean isAttachmentParameter(ParameterImpl parameter) {
      ParameterBinding paramBinding = parameter.getBinding();
      return paramBinding.isAttachment();
   }

   protected void generateBinding() {
      Binding newBinding = this.serviceDefinitions.binding().name(this.model.getBoundPortTypeName().getLocalPart());
      this.extension.addBindingExtension(newBinding);
      newBinding.type(this.model.getPortTypeName());
      boolean first = true;
      Iterator var3 = this.model.getJavaMethods().iterator();

      while(var3.hasNext()) {
         JavaMethodImpl method = (JavaMethodImpl)var3.next();
         if (first) {
            SOAPBinding sBinding = method.getBinding();
            SOAPVersion soapVersion = sBinding.getSOAPVersion();
            if (soapVersion == SOAPVersion.SOAP_12) {
               com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding soapBinding = newBinding.soap12Binding();
               soapBinding.transport(this.binding.getBindingId().getTransport());
               if (sBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.DOCUMENT)) {
                  soapBinding.style("document");
               } else {
                  soapBinding.style("rpc");
               }
            } else {
               com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding soapBinding = newBinding.soapBinding();
               soapBinding.transport(this.binding.getBindingId().getTransport());
               if (sBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.DOCUMENT)) {
                  soapBinding.style("document");
               } else {
                  soapBinding.style("rpc");
               }
            }

            first = false;
         }

         if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
            this.generateSOAP12BindingOperation(method, newBinding);
         } else {
            this.generateBindingOperation(method, newBinding);
         }
      }

   }

   protected void generateBindingOperation(JavaMethodImpl method, Binding binding) {
      BindingOperationType operation = binding.operation().name(method.getOperationName());
      this.extension.addBindingOperationExtension(operation, method);
      String targetNamespace = this.model.getTargetNamespace();
      QName requestMessage = new QName(targetNamespace, method.getOperationName());
      List<ParameterImpl> bodyParams = new ArrayList();
      List<ParameterImpl> headerParams = new ArrayList();
      this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
      SOAPBinding soapBinding = method.getBinding();
      operation.soapOperation().soapAction(soapBinding.getSOAPAction());
      TypedXmlWriter input = operation.input();
      this.extension.addBindingOperationInputExtension(input, method);
      BodyType body = (BodyType)input._element(Body.class);
      boolean isRpc = soapBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.RPC);
      if (soapBinding.getUse() != javax.jws.soap.SOAPBinding.Use.LITERAL) {
         throw new WebServiceException("encoded use is not supported");
      } else {
         body.use("literal");
         StringBuilder parts;
         if (headerParams.size() > 0) {
            if (bodyParams.size() <= 0) {
               body.parts("");
            } else {
               ParameterImpl param = (ParameterImpl)bodyParams.iterator().next();
               if (!isRpc) {
                  body.parts(param.getPartName());
               } else {
                  parts = new StringBuilder();
                  int i = 0;

                  ParameterImpl parameter;
                  for(Iterator var15 = ((WrapperParameter)param).getWrapperChildren().iterator(); var15.hasNext(); parts.append(parameter.getPartName())) {
                     parameter = (ParameterImpl)var15.next();
                     if (i++ > 0) {
                        parts.append(' ');
                     }
                  }

                  body.parts(parts.toString());
               }
            }

            this.generateSOAPHeaders(input, headerParams, requestMessage);
         }

         if (isRpc) {
            body.namespace(((ParameterImpl)method.getRequestParameters().iterator().next()).getName().getNamespaceURI());
         }

         if (method.getMEP() != MEP.ONE_WAY) {
            bodyParams.clear();
            headerParams.clear();
            this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
            TypedXmlWriter output = operation.output();
            this.extension.addBindingOperationOutputExtension(output, method);
            body = (BodyType)output._element(Body.class);
            body.use("literal");
            if (headerParams.size() > 0) {
               parts = new StringBuilder();
               if (bodyParams.size() > 0) {
                  ParameterImpl param = bodyParams.iterator().hasNext() ? (ParameterImpl)bodyParams.iterator().next() : null;
                  if (param != null) {
                     if (isRpc) {
                        int i = 0;

                        ParameterImpl parameter;
                        for(Iterator var26 = ((WrapperParameter)param).getWrapperChildren().iterator(); var26.hasNext(); parts.append(parameter.getPartName())) {
                           parameter = (ParameterImpl)var26.next();
                           if (i++ > 0) {
                              parts.append(" ");
                           }
                        }
                     } else {
                        parts = new StringBuilder(param.getPartName());
                     }
                  }
               }

               body.parts(parts.toString());
               QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
               this.generateSOAPHeaders(output, headerParams, responseMessage);
            }

            if (isRpc) {
               body.namespace(((ParameterImpl)method.getRequestParameters().iterator().next()).getName().getNamespaceURI());
            }
         }

         Iterator var19 = method.getCheckedExceptions().iterator();

         while(var19.hasNext()) {
            CheckedExceptionImpl exception = (CheckedExceptionImpl)var19.next();
            Fault fault = operation.fault().name(exception.getMessageName());
            this.extension.addBindingOperationFaultExtension(fault, method, exception);
            SOAPFault soapFault = ((SOAPFault)fault._element(SOAPFault.class)).name(exception.getMessageName());
            soapFault.use("literal");
         }

      }
   }

   protected void generateSOAP12BindingOperation(JavaMethodImpl method, Binding binding) {
      BindingOperationType operation = binding.operation().name(method.getOperationName());
      this.extension.addBindingOperationExtension(operation, method);
      String targetNamespace = this.model.getTargetNamespace();
      QName requestMessage = new QName(targetNamespace, method.getOperationName());
      ArrayList<ParameterImpl> bodyParams = new ArrayList();
      ArrayList<ParameterImpl> headerParams = new ArrayList();
      this.splitParameters(bodyParams, headerParams, method.getRequestParameters());
      SOAPBinding soapBinding = method.getBinding();
      String soapAction = soapBinding.getSOAPAction();
      if (soapAction != null) {
         operation.soap12Operation().soapAction(soapAction);
      }

      TypedXmlWriter input = operation.input();
      this.extension.addBindingOperationInputExtension(input, method);
      com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType body = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType)input._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
      boolean isRpc = soapBinding.getStyle().equals(javax.jws.soap.SOAPBinding.Style.RPC);
      if (!soapBinding.getUse().equals(javax.jws.soap.SOAPBinding.Use.LITERAL)) {
         throw new WebServiceException("encoded use is not supported");
      } else {
         body.use("literal");
         if (headerParams.size() > 0) {
            if (bodyParams.size() <= 0) {
               body.parts("");
            } else {
               ParameterImpl param = (ParameterImpl)bodyParams.iterator().next();
               if (!isRpc) {
                  body.parts(param.getPartName());
               } else {
                  StringBuilder parts = new StringBuilder();
                  int i = 0;

                  ParameterImpl parameter;
                  for(Iterator var16 = ((WrapperParameter)param).getWrapperChildren().iterator(); var16.hasNext(); parts.append(parameter.getPartName())) {
                     parameter = (ParameterImpl)var16.next();
                     if (i++ > 0) {
                        parts.append(' ');
                     }
                  }

                  body.parts(parts.toString());
               }
            }

            this.generateSOAP12Headers(input, headerParams, requestMessage);
         }

         if (isRpc) {
            body.namespace(((ParameterImpl)method.getRequestParameters().iterator().next()).getName().getNamespaceURI());
         }

         if (method.getMEP() != MEP.ONE_WAY) {
            bodyParams.clear();
            headerParams.clear();
            this.splitParameters(bodyParams, headerParams, method.getResponseParameters());
            TypedXmlWriter output = operation.output();
            this.extension.addBindingOperationOutputExtension(output, method);
            body = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.BodyType)output._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Body.class);
            body.use("literal");
            if (headerParams.size() > 0) {
               if (bodyParams.size() > 0) {
                  ParameterImpl param = (ParameterImpl)bodyParams.iterator().next();
                  if (isRpc) {
                     StringBuilder parts = new StringBuilder();
                     int i = 0;

                     ParameterImpl parameter;
                     for(Iterator var28 = ((WrapperParameter)param).getWrapperChildren().iterator(); var28.hasNext(); parts.append(parameter.getPartName())) {
                        parameter = (ParameterImpl)var28.next();
                        if (i++ > 0) {
                           parts.append(" ");
                        }
                     }

                     body.parts(parts.toString());
                  } else {
                     body.parts(param.getPartName());
                  }
               } else {
                  body.parts("");
               }

               QName responseMessage = new QName(targetNamespace, method.getResponseMessageName());
               this.generateSOAP12Headers(output, headerParams, responseMessage);
            }

            if (isRpc) {
               body.namespace(((ParameterImpl)method.getRequestParameters().iterator().next()).getName().getNamespaceURI());
            }
         }

         Iterator var20 = method.getCheckedExceptions().iterator();

         while(var20.hasNext()) {
            CheckedExceptionImpl exception = (CheckedExceptionImpl)var20.next();
            Fault fault = operation.fault().name(exception.getMessageName());
            this.extension.addBindingOperationFaultExtension(fault, method, exception);
            com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault soapFault = ((com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault)fault._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPFault.class)).name(exception.getMessageName());
            soapFault.use("literal");
         }

      }
   }

   protected void splitParameters(List<ParameterImpl> bodyParams, List<ParameterImpl> headerParams, List<ParameterImpl> params) {
      Iterator var4 = params.iterator();

      while(var4.hasNext()) {
         ParameterImpl parameter = (ParameterImpl)var4.next();
         if (this.isBodyParameter(parameter)) {
            bodyParams.add(parameter);
         } else {
            headerParams.add(parameter);
         }
      }

   }

   protected void generateSOAPHeaders(TypedXmlWriter writer, List<ParameterImpl> parameters, QName message) {
      Iterator var4 = parameters.iterator();

      while(var4.hasNext()) {
         ParameterImpl headerParam = (ParameterImpl)var4.next();
         Header header = (Header)writer._element(Header.class);
         header.message(message);
         header.part(headerParam.getPartName());
         header.use("literal");
      }

   }

   protected void generateSOAP12Headers(TypedXmlWriter writer, List<ParameterImpl> parameters, QName message) {
      Iterator var4 = parameters.iterator();

      while(var4.hasNext()) {
         ParameterImpl headerParam = (ParameterImpl)var4.next();
         com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header header = (com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header)writer._element(com.sun.xml.internal.ws.wsdl.writer.document.soap12.Header.class);
         header.message(message);
         header.part(headerParam.getPartName());
         header.use("literal");
      }

   }

   protected void generateService() {
      QName portQName = this.model.getPortName();
      QName serviceQName = this.model.getServiceQName();
      Service service = this.serviceDefinitions.service().name(serviceQName.getLocalPart());
      this.extension.addServiceExtension(service);
      Port port = service.port().name(portQName.getLocalPart());
      port.binding(this.model.getBoundPortTypeName());
      this.extension.addPortExtension(port);
      if (!this.model.getJavaMethods().isEmpty()) {
         if (this.binding.getBindingId().getSOAPVersion() == SOAPVersion.SOAP_12) {
            SOAPAddress address = (SOAPAddress)port._element(SOAPAddress.class);
            address.location(this.endpointAddress);
         } else {
            com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress address = (com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress)port._element(com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPAddress.class);
            address.location(this.endpointAddress);
         }

      }
   }

   protected void generateInputMessage(Operation operation, JavaMethodImpl method) {
      ParamType paramType = operation.input();
      this.extension.addOperationInputExtension(paramType, method);
      paramType.message(new QName(this.model.getTargetNamespace(), method.getRequestMessageName()));
   }

   protected void generateOutputMessage(Operation operation, JavaMethodImpl method) {
      ParamType paramType = operation.output();
      this.extension.addOperationOutputExtension(paramType, method);
      paramType.message(new QName(this.model.getTargetNamespace(), method.getResponseMessageName()));
   }

   public Result createOutputFile(String namespaceUri, String suggestedFileName) throws IOException {
      if (namespaceUri == null) {
         return null;
      } else {
         Holder<String> fileNameHolder = new Holder();
         fileNameHolder.value = this.schemaPrefix + suggestedFileName;
         Result result = this.wsdlResolver.getSchemaOutput(namespaceUri, fileNameHolder);
         String schemaLoc;
         if (result == null) {
            schemaLoc = (String)fileNameHolder.value;
         } else {
            schemaLoc = relativize(result.getSystemId(), this.wsdlLocation);
         }

         boolean isEmptyNs = namespaceUri.trim().equals("");
         if (!isEmptyNs) {
            com.sun.xml.internal.ws.wsdl.writer.document.xsd.Import _import = this.types.schema()._import();
            _import.namespace(namespaceUri);
            _import.schemaLocation(schemaLoc);
         }

         return result;
      }
   }

   private Result createInlineSchema(String namespaceUri, String suggestedFileName) throws IOException {
      if (namespaceUri.equals("")) {
         return null;
      } else {
         Result result = new TXWResult(this.types);
         result.setSystemId("");
         return result;
      }
   }

   protected static String relativize(String uri, String baseUri) {
      try {
         assert uri != null;

         if (baseUri == null) {
            return uri;
         } else {
            URI theUri = new URI(Util.escapeURI(uri));
            URI theBaseUri = new URI(Util.escapeURI(baseUri));
            if (!theUri.isOpaque() && !theBaseUri.isOpaque()) {
               if (Util.equalsIgnoreCase(theUri.getScheme(), theBaseUri.getScheme()) && Util.equal(theUri.getAuthority(), theBaseUri.getAuthority())) {
                  String uriPath = theUri.getPath();
                  String basePath = theBaseUri.getPath();
                  if (!basePath.endsWith("/")) {
                     basePath = Util.normalizeUriPath(basePath);
                  }

                  if (uriPath.equals(basePath)) {
                     return ".";
                  } else {
                     String relPath = calculateRelativePath(uriPath, basePath);
                     if (relPath == null) {
                        return uri;
                     } else {
                        StringBuilder relUri = new StringBuilder();
                        relUri.append(relPath);
                        if (theUri.getQuery() != null) {
                           relUri.append('?').append(theUri.getQuery());
                        }

                        if (theUri.getFragment() != null) {
                           relUri.append('#').append(theUri.getFragment());
                        }

                        return relUri.toString();
                     }
                  }
               } else {
                  return uri;
               }
            } else {
               return uri;
            }
         }
      } catch (URISyntaxException var8) {
         throw new InternalError("Error escaping one of these uris:\n\t" + uri + "\n\t" + baseUri);
      }
   }

   private static String calculateRelativePath(String uri, String base) {
      if (base == null) {
         return null;
      } else {
         return uri.startsWith(base) ? uri.substring(base.length()) : "../" + calculateRelativePath(uri, Util.getParentUriPath(base));
      }
   }

   private void register(WSDLGeneratorExtension h) {
      this.extensionHandlers.add(h);
   }

   protected class JAXWSOutputSchemaResolver extends SchemaOutputResolver {
      ArrayList<DOMResult> nonGlassfishSchemas = null;

      public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
         return WSDLGenerator.this.inlineSchemas ? (this.nonGlassfishSchemas != null ? this.nonGlassfishSchemaResult(namespaceUri, suggestedFileName) : WSDLGenerator.this.createInlineSchema(namespaceUri, suggestedFileName)) : WSDLGenerator.this.createOutputFile(namespaceUri, suggestedFileName);
      }

      private Result nonGlassfishSchemaResult(String namespaceUri, String suggestedFileName) throws IOException {
         DOMResult result = new DOMResult();
         result.setSystemId("");
         this.nonGlassfishSchemas.add(result);
         return result;
      }
   }

   private static class CommentFilter implements XmlSerializer {
      final XmlSerializer serializer;
      private static final String VERSION_COMMENT;

      CommentFilter(XmlSerializer serializer) {
         this.serializer = serializer;
      }

      public void startDocument() {
         this.serializer.startDocument();
         this.comment(new StringBuilder(VERSION_COMMENT));
         this.text(new StringBuilder("\n"));
      }

      public void beginStartTag(String uri, String localName, String prefix) {
         this.serializer.beginStartTag(uri, localName, prefix);
      }

      public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
         this.serializer.writeAttribute(uri, localName, prefix, value);
      }

      public void writeXmlns(String prefix, String uri) {
         this.serializer.writeXmlns(prefix, uri);
      }

      public void endStartTag(String uri, String localName, String prefix) {
         this.serializer.endStartTag(uri, localName, prefix);
      }

      public void endTag() {
         this.serializer.endTag();
      }

      public void text(StringBuilder text) {
         this.serializer.text(text);
      }

      public void cdata(StringBuilder text) {
         this.serializer.cdata(text);
      }

      public void comment(StringBuilder comment) {
         this.serializer.comment(comment);
      }

      public void endDocument() {
         this.serializer.endDocument();
      }

      public void flush() {
         this.serializer.flush();
      }

      static {
         VERSION_COMMENT = " Generated by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
      }
   }
}
