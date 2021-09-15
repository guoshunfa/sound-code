package sun.rmi.transport.proxy;

interface CGICommandHandler {
   String getName();

   void execute(String var1) throws CGIClientException, CGIServerException;
}
