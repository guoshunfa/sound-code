package org.omg.PortableInterceptor;

public interface ClientRequestInterceptorOperations extends InterceptorOperations {
   void send_request(ClientRequestInfo var1) throws ForwardRequest;

   void send_poll(ClientRequestInfo var1);

   void receive_reply(ClientRequestInfo var1);

   void receive_exception(ClientRequestInfo var1) throws ForwardRequest;

   void receive_other(ClientRequestInfo var1) throws ForwardRequest;
}
