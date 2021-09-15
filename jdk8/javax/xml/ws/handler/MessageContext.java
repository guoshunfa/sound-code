package javax.xml.ws.handler;

import java.util.Map;

public interface MessageContext extends Map<String, Object> {
   String MESSAGE_OUTBOUND_PROPERTY = "javax.xml.ws.handler.message.outbound";
   String INBOUND_MESSAGE_ATTACHMENTS = "javax.xml.ws.binding.attachments.inbound";
   String OUTBOUND_MESSAGE_ATTACHMENTS = "javax.xml.ws.binding.attachments.outbound";
   String WSDL_DESCRIPTION = "javax.xml.ws.wsdl.description";
   String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
   String WSDL_PORT = "javax.xml.ws.wsdl.port";
   String WSDL_INTERFACE = "javax.xml.ws.wsdl.interface";
   String WSDL_OPERATION = "javax.xml.ws.wsdl.operation";
   String HTTP_RESPONSE_CODE = "javax.xml.ws.http.response.code";
   String HTTP_REQUEST_HEADERS = "javax.xml.ws.http.request.headers";
   String HTTP_RESPONSE_HEADERS = "javax.xml.ws.http.response.headers";
   String HTTP_REQUEST_METHOD = "javax.xml.ws.http.request.method";
   String SERVLET_REQUEST = "javax.xml.ws.servlet.request";
   String SERVLET_RESPONSE = "javax.xml.ws.servlet.response";
   String SERVLET_CONTEXT = "javax.xml.ws.servlet.context";
   String QUERY_STRING = "javax.xml.ws.http.request.querystring";
   String PATH_INFO = "javax.xml.ws.http.request.pathinfo";
   String REFERENCE_PARAMETERS = "javax.xml.ws.reference.parameters";

   void setScope(String var1, MessageContext.Scope var2);

   MessageContext.Scope getScope(String var1);

   public static enum Scope {
      APPLICATION,
      HANDLER;
   }
}
