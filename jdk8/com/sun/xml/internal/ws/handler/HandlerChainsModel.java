package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.UtilException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

public class HandlerChainsModel {
   private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.util");
   private Class annotatedClass;
   private List<HandlerChainsModel.HandlerChainType> handlerChains;
   private String id;
   public static final String PROTOCOL_SOAP11_TOKEN = "##SOAP11_HTTP";
   public static final String PROTOCOL_SOAP12_TOKEN = "##SOAP12_HTTP";
   public static final String PROTOCOL_XML_TOKEN = "##XML_HTTP";
   public static final String NS_109 = "http://java.sun.com/xml/ns/javaee";
   public static final QName QNAME_CHAIN_PORT_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "port-name-pattern");
   public static final QName QNAME_CHAIN_PROTOCOL_BINDING = new QName("http://java.sun.com/xml/ns/javaee", "protocol-bindings");
   public static final QName QNAME_CHAIN_SERVICE_PATTERN = new QName("http://java.sun.com/xml/ns/javaee", "service-name-pattern");
   public static final QName QNAME_HANDLER_CHAIN = new QName("http://java.sun.com/xml/ns/javaee", "handler-chain");
   public static final QName QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/xml/ns/javaee", "handler-chains");
   public static final QName QNAME_HANDLER = new QName("http://java.sun.com/xml/ns/javaee", "handler");
   public static final QName QNAME_HANDLER_NAME = new QName("http://java.sun.com/xml/ns/javaee", "handler-name");
   public static final QName QNAME_HANDLER_CLASS = new QName("http://java.sun.com/xml/ns/javaee", "handler-class");
   public static final QName QNAME_HANDLER_PARAM = new QName("http://java.sun.com/xml/ns/javaee", "init-param");
   public static final QName QNAME_HANDLER_PARAM_NAME = new QName("http://java.sun.com/xml/ns/javaee", "param-name");
   public static final QName QNAME_HANDLER_PARAM_VALUE = new QName("http://java.sun.com/xml/ns/javaee", "param-value");
   public static final QName QNAME_HANDLER_HEADER = new QName("http://java.sun.com/xml/ns/javaee", "soap-header");
   public static final QName QNAME_HANDLER_ROLE = new QName("http://java.sun.com/xml/ns/javaee", "soap-role");

   private HandlerChainsModel(Class annotatedClass) {
      this.annotatedClass = annotatedClass;
   }

   private List<HandlerChainsModel.HandlerChainType> getHandlerChain() {
      if (this.handlerChains == null) {
         this.handlerChains = new ArrayList();
      }

      return this.handlerChains;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String value) {
      this.id = value;
   }

   public static HandlerChainsModel parseHandlerConfigFile(Class annotatedClass, XMLStreamReader reader) {
      ensureProperName(reader, QNAME_HANDLER_CHAINS);
      HandlerChainsModel handlerModel = new HandlerChainsModel(annotatedClass);
      List<HandlerChainsModel.HandlerChainType> hChains = handlerModel.getHandlerChain();
      XMLStreamReaderUtil.nextElementContent(reader);

      while(reader.getName().equals(QNAME_HANDLER_CHAIN)) {
         HandlerChainsModel.HandlerChainType hChain = new HandlerChainsModel.HandlerChainType();
         XMLStreamReaderUtil.nextElementContent(reader);
         String handlerClass;
         QName serviceNamepattern;
         if (reader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
            serviceNamepattern = XMLStreamReaderUtil.getElementQName(reader);
            hChain.setPortNamePattern(serviceNamepattern);
            XMLStreamReaderUtil.nextElementContent(reader);
         } else if (!reader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
            if (reader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
               serviceNamepattern = XMLStreamReaderUtil.getElementQName(reader);
               hChain.setServiceNamePattern(serviceNamepattern);
               XMLStreamReaderUtil.nextElementContent(reader);
            }
         } else {
            String bindingList = XMLStreamReaderUtil.getElementText(reader);
            StringTokenizer stk = new StringTokenizer(bindingList);

            while(stk.hasMoreTokens()) {
               handlerClass = stk.nextToken();
               hChain.addProtocolBinding(handlerClass);
            }

            XMLStreamReaderUtil.nextElementContent(reader);
         }

         List handlers = hChain.getHandlers();

         while(reader.getName().equals(QNAME_HANDLER)) {
            HandlerChainsModel.HandlerType handler = new HandlerChainsModel.HandlerType();
            XMLStreamReaderUtil.nextContent(reader);
            if (reader.getName().equals(QNAME_HANDLER_NAME)) {
               handlerClass = XMLStreamReaderUtil.getElementText(reader).trim();
               handler.setHandlerName(handlerClass);
               XMLStreamReaderUtil.nextContent(reader);
            }

            ensureProperName(reader, QNAME_HANDLER_CLASS);
            handlerClass = XMLStreamReaderUtil.getElementText(reader).trim();
            handler.setHandlerClass(handlerClass);
            XMLStreamReaderUtil.nextContent(reader);

            while(reader.getName().equals(QNAME_HANDLER_PARAM)) {
               skipInitParamElement(reader);
            }

            while(reader.getName().equals(QNAME_HANDLER_HEADER)) {
               skipTextElement(reader);
            }

            while(reader.getName().equals(QNAME_HANDLER_ROLE)) {
               List<String> soapRoles = handler.getSoapRoles();
               soapRoles.add(XMLStreamReaderUtil.getElementText(reader));
               XMLStreamReaderUtil.nextContent(reader);
            }

            handlers.add(handler);
            ensureProperName(reader, QNAME_HANDLER);
            XMLStreamReaderUtil.nextContent(reader);
         }

         ensureProperName(reader, QNAME_HANDLER_CHAIN);
         hChains.add(hChain);
         XMLStreamReaderUtil.nextContent(reader);
      }

      return handlerModel;
   }

   public static HandlerAnnotationInfo parseHandlerFile(XMLStreamReader reader, ClassLoader classLoader, QName serviceName, QName portName, WSBinding wsbinding) {
      ensureProperName(reader, QNAME_HANDLER_CHAINS);
      String bindingId = wsbinding.getBindingId().toString();
      HandlerAnnotationInfo info = new HandlerAnnotationInfo();
      XMLStreamReaderUtil.nextElementContent(reader);
      List<Handler> handlerChain = new ArrayList();
      HashSet roles = new HashSet();

      while(true) {
         while(reader.getName().equals(QNAME_HANDLER_CHAIN)) {
            XMLStreamReaderUtil.nextElementContent(reader);
            boolean parseChain;
            if (reader.getName().equals(QNAME_CHAIN_PORT_PATTERN)) {
               if (portName == null) {
                  logger.warning("handler chain sepcified for port but port QName passed to parser is null");
               }

               parseChain = JAXWSUtils.matchQNames(portName, XMLStreamReaderUtil.getElementQName(reader));
               if (!parseChain) {
                  skipChain(reader);
                  continue;
               }

               XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_PROTOCOL_BINDING)) {
               if (bindingId == null) {
                  logger.warning("handler chain sepcified for bindingId but bindingId passed to parser is null");
               }

               String bindingConstraint = XMLStreamReaderUtil.getElementText(reader);
               boolean skipThisChain = true;
               StringTokenizer stk = new StringTokenizer(bindingConstraint);
               ArrayList bindingList = new ArrayList();

               while(stk.hasMoreTokens()) {
                  String tokenOrURI = stk.nextToken();
                  tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
                  String binding = BindingID.parse(tokenOrURI).toString();
                  bindingList.add(binding);
               }

               if (bindingList.contains(bindingId)) {
                  skipThisChain = false;
               }

               if (skipThisChain) {
                  skipChain(reader);
                  continue;
               }

               XMLStreamReaderUtil.nextElementContent(reader);
            } else if (reader.getName().equals(QNAME_CHAIN_SERVICE_PATTERN)) {
               if (serviceName == null) {
                  logger.warning("handler chain sepcified for service but service QName passed to parser is null");
               }

               parseChain = JAXWSUtils.matchQNames(serviceName, XMLStreamReaderUtil.getElementQName(reader));
               if (!parseChain) {
                  skipChain(reader);
                  continue;
               }

               XMLStreamReaderUtil.nextElementContent(reader);
            }

            while(reader.getName().equals(QNAME_HANDLER)) {
               XMLStreamReaderUtil.nextContent(reader);
               if (reader.getName().equals(QNAME_HANDLER_NAME)) {
                  skipTextElement(reader);
               }

               ensureProperName(reader, QNAME_HANDLER_CLASS);

               Handler handler;
               try {
                  handler = (Handler)loadClass(classLoader, XMLStreamReaderUtil.getElementText(reader).trim()).newInstance();
               } catch (InstantiationException var16) {
                  throw new RuntimeException(var16);
               } catch (IllegalAccessException var17) {
                  throw new RuntimeException(var17);
               }

               XMLStreamReaderUtil.nextContent(reader);

               while(reader.getName().equals(QNAME_HANDLER_PARAM)) {
                  skipInitParamElement(reader);
               }

               while(reader.getName().equals(QNAME_HANDLER_HEADER)) {
                  skipTextElement(reader);
               }

               while(reader.getName().equals(QNAME_HANDLER_ROLE)) {
                  roles.add(XMLStreamReaderUtil.getElementText(reader));
                  XMLStreamReaderUtil.nextContent(reader);
               }

               Method[] var20 = handler.getClass().getMethods();
               int var21 = var20.length;

               for(int var22 = 0; var22 < var21; ++var22) {
                  Method method = var20[var22];
                  if (method.getAnnotation(PostConstruct.class) != null) {
                     try {
                        method.invoke(handler);
                        break;
                     } catch (Exception var15) {
                        throw new RuntimeException(var15);
                     }
                  }
               }

               handlerChain.add(handler);
               ensureProperName(reader, QNAME_HANDLER);
               XMLStreamReaderUtil.nextContent(reader);
            }

            ensureProperName(reader, QNAME_HANDLER_CHAIN);
            XMLStreamReaderUtil.nextContent(reader);
         }

         info.setHandlers(handlerChain);
         info.setRoles(roles);
         return info;
      }
   }

   public HandlerAnnotationInfo getHandlersForPortInfo(PortInfo info) {
      HandlerAnnotationInfo handlerInfo = new HandlerAnnotationInfo();
      List<Handler> handlerClassList = new ArrayList();
      Set<String> roles = new HashSet();
      Iterator var5 = this.handlerChains.iterator();

      while(true) {
         HandlerChainsModel.HandlerChainType hchain;
         boolean hchainMatched;
         do {
            if (!var5.hasNext()) {
               handlerInfo.setHandlers(handlerClassList);
               handlerInfo.setRoles(roles);
               return handlerInfo;
            }

            hchain = (HandlerChainsModel.HandlerChainType)var5.next();
            hchainMatched = false;
            if (!hchain.isConstraintSet() || JAXWSUtils.matchQNames(info.getServiceName(), hchain.getServiceNamePattern()) || JAXWSUtils.matchQNames(info.getPortName(), hchain.getPortNamePattern()) || hchain.getProtocolBindings().contains(info.getBindingID())) {
               hchainMatched = true;
            }
         } while(!hchainMatched);

         HandlerChainsModel.HandlerType handler;
         for(Iterator var8 = hchain.getHandlers().iterator(); var8.hasNext(); roles.addAll(handler.getSoapRoles())) {
            handler = (HandlerChainsModel.HandlerType)var8.next();

            try {
               Handler handlerClass = (Handler)loadClass(this.annotatedClass.getClassLoader(), handler.getHandlerClass()).newInstance();
               callHandlerPostConstruct(handlerClass);
               handlerClassList.add(handlerClass);
            } catch (InstantiationException var11) {
               throw new RuntimeException(var11);
            } catch (IllegalAccessException var12) {
               throw new RuntimeException(var12);
            }
         }
      }
   }

   private static Class loadClass(ClassLoader loader, String name) {
      try {
         return Class.forName(name, true, loader);
      } catch (ClassNotFoundException var3) {
         throw new UtilException("util.handler.class.not.found", new Object[]{name});
      }
   }

   private static void callHandlerPostConstruct(Object handlerClass) {
      Method[] var1 = handlerClass.getClass().getMethods();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Method method = var1[var3];
         if (method.getAnnotation(PostConstruct.class) != null) {
            try {
               method.invoke(handlerClass);
               break;
            } catch (Exception var6) {
               throw new RuntimeException(var6);
            }
         }
      }

   }

   private static void skipChain(XMLStreamReader reader) {
      while(XMLStreamReaderUtil.nextContent(reader) != 2 || !reader.getName().equals(QNAME_HANDLER_CHAIN)) {
      }

      XMLStreamReaderUtil.nextElementContent(reader);
   }

   private static void skipTextElement(XMLStreamReader reader) {
      XMLStreamReaderUtil.nextContent(reader);
      XMLStreamReaderUtil.nextElementContent(reader);
      XMLStreamReaderUtil.nextElementContent(reader);
   }

   private static void skipInitParamElement(XMLStreamReader reader) {
      int state;
      do {
         state = XMLStreamReaderUtil.nextContent(reader);
      } while(state != 2 || !reader.getName().equals(QNAME_HANDLER_PARAM));

      XMLStreamReaderUtil.nextElementContent(reader);
   }

   private static void ensureProperName(XMLStreamReader reader, QName expectedName) {
      if (!reader.getName().equals(expectedName)) {
         failWithLocalName("util.parser.wrong.element", reader, expectedName.getLocalPart());
      }

   }

   static void ensureProperName(XMLStreamReader reader, String expectedName) {
      if (!reader.getLocalName().equals(expectedName)) {
         failWithLocalName("util.parser.wrong.element", reader, expectedName);
      }

   }

   private static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
      throw new UtilException(key, new Object[]{Integer.toString(reader.getLocation().getLineNumber()), reader.getLocalName(), arg});
   }

   static class HandlerType {
      String handlerName;
      String handlerClass;
      List<String> soapRoles;
      String id;

      public HandlerType() {
      }

      public String getHandlerName() {
         return this.handlerName;
      }

      public void setHandlerName(String value) {
         this.handlerName = value;
      }

      public String getHandlerClass() {
         return this.handlerClass;
      }

      public void setHandlerClass(String value) {
         this.handlerClass = value;
      }

      public String getId() {
         return this.id;
      }

      public void setId(String value) {
         this.id = value;
      }

      public List<String> getSoapRoles() {
         if (this.soapRoles == null) {
            this.soapRoles = new ArrayList();
         }

         return this.soapRoles;
      }
   }

   static class HandlerChainType {
      QName serviceNamePattern;
      QName portNamePattern;
      List<String> protocolBindings = new ArrayList();
      boolean constraintSet = false;
      List<HandlerChainsModel.HandlerType> handlers;
      String id;

      public HandlerChainType() {
      }

      public void setServiceNamePattern(QName value) {
         this.serviceNamePattern = value;
         this.constraintSet = true;
      }

      public QName getServiceNamePattern() {
         return this.serviceNamePattern;
      }

      public void setPortNamePattern(QName value) {
         this.portNamePattern = value;
         this.constraintSet = true;
      }

      public QName getPortNamePattern() {
         return this.portNamePattern;
      }

      public List<String> getProtocolBindings() {
         return this.protocolBindings;
      }

      public void addProtocolBinding(String tokenOrURI) {
         tokenOrURI = DeploymentDescriptorParser.getBindingIdForToken(tokenOrURI);
         String binding = BindingID.parse(tokenOrURI).toString();
         this.protocolBindings.add(binding);
         this.constraintSet = true;
      }

      public boolean isConstraintSet() {
         return this.constraintSet || !this.protocolBindings.isEmpty();
      }

      public String getId() {
         return this.id;
      }

      public void setId(String value) {
         this.id = value;
      }

      public List<HandlerChainsModel.HandlerType> getHandlers() {
         if (this.handlers == null) {
            this.handlers = new ArrayList();
         }

         return this.handlers;
      }
   }
}
