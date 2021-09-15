package javax.xml.ws.handler;

public interface Handler<C extends MessageContext> {
   boolean handleMessage(C var1);

   boolean handleFault(C var1);

   void close(MessageContext var1);
}
