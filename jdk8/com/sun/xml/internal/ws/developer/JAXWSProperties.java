package com.sun.xml.internal.ws.developer;

public interface JAXWSProperties {
   /** @deprecated */
   @Deprecated
   String CONTENT_NEGOTIATION_PROPERTY = "com.sun.xml.ws.client.ContentNegotiation";
   String MTOM_THRESHOLOD_VALUE = "com.sun.xml.internal.ws.common.MtomThresholdValue";
   String HTTP_EXCHANGE = "com.sun.xml.internal.ws.http.exchange";
   String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";
   String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
   String HTTP_CLIENT_STREAMING_CHUNK_SIZE = "com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size";
   String HOSTNAME_VERIFIER = "com.sun.xml.internal.ws.transport.https.client.hostname.verifier";
   String SSL_SOCKET_FACTORY = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";
   String INBOUND_HEADER_LIST_PROPERTY = "com.sun.xml.internal.ws.api.message.HeaderList";
   String WSENDPOINT = "com.sun.xml.internal.ws.api.server.WSEndpoint";
   String ADDRESSING_TO = "com.sun.xml.internal.ws.api.addressing.to";
   String ADDRESSING_FROM = "com.sun.xml.internal.ws.api.addressing.from";
   String ADDRESSING_ACTION = "com.sun.xml.internal.ws.api.addressing.action";
   String ADDRESSING_MESSAGEID = "com.sun.xml.internal.ws.api.addressing.messageId";
   String HTTP_REQUEST_URL = "com.sun.xml.internal.ws.transport.http.servlet.requestURL";
   String REST_BINDING = "http://jax-ws.dev.java.net/rest";
}
