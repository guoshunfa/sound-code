package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface WSDLModel extends WSDLExtensible {
   WSDLPortType getPortType(@NotNull QName var1);

   WSDLBoundPortType getBinding(@NotNull QName var1);

   WSDLBoundPortType getBinding(@NotNull QName var1, @NotNull QName var2);

   WSDLService getService(@NotNull QName var1);

   @NotNull
   Map<QName, ? extends WSDLPortType> getPortTypes();

   @NotNull
   Map<QName, ? extends WSDLBoundPortType> getBindings();

   @NotNull
   Map<QName, ? extends WSDLService> getServices();

   QName getFirstServiceName();

   WSDLMessage getMessage(QName var1);

   @NotNull
   Map<QName, ? extends WSDLMessage> getMessages();

   /** @deprecated */
   PolicyMap getPolicyMap();

   public static class WSDLParser {
      @NotNull
      public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
         return parse(wsdlEntityParser, resolver, isClientSide, Container.NONE, extensions);
      }

      @NotNull
      public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
         return parse(wsdlEntityParser, resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
      }

      @NotNull
      public static WSDLModel parse(XMLEntityResolver.Parser wsdlEntityParser, XMLEntityResolver resolver, boolean isClientSide, @NotNull Container container, PolicyResolver policyResolver, WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
         return RuntimeWSDLParser.parse(wsdlEntityParser, resolver, isClientSide, container, policyResolver, extensions);
      }
   }
}
