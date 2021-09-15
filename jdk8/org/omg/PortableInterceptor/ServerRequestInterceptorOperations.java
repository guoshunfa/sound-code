package org.omg.PortableInterceptor;

public interface ServerRequestInterceptorOperations extends InterceptorOperations {
   void receive_request_service_contexts(ServerRequestInfo var1) throws ForwardRequest;

   void receive_request(ServerRequestInfo var1) throws ForwardRequest;

   void send_reply(ServerRequestInfo var1);

   void send_exception(ServerRequestInfo var1) throws ForwardRequest;

   void send_other(ServerRequestInfo var1) throws ForwardRequest;
}
