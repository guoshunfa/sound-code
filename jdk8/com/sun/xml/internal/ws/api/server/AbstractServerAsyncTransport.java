package com.sun.xml.internal.ws.api.server;

import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;
import java.io.IOException;

public abstract class AbstractServerAsyncTransport<T> {
   private final WSEndpoint endpoint;
   private final AbstractServerAsyncTransport.CodecPool codecPool;

   public AbstractServerAsyncTransport(WSEndpoint endpoint) {
      this.endpoint = endpoint;
      this.codecPool = new AbstractServerAsyncTransport.CodecPool(endpoint);
   }

   protected Packet decodePacket(T connection, @NotNull Codec codec) throws IOException {
      Packet packet = new Packet();
      packet.acceptableMimeTypes = this.getAcceptableMimeTypes(connection);
      packet.addSatellite(this.getPropertySet(connection));
      packet.transportBackChannel = this.getTransportBackChannel(connection);
      return packet;
   }

   protected abstract void encodePacket(T var1, @NotNull Packet var2, @NotNull Codec var3) throws IOException;

   @Nullable
   protected abstract String getAcceptableMimeTypes(T var1);

   @Nullable
   protected abstract TransportBackChannel getTransportBackChannel(T var1);

   @NotNull
   protected abstract PropertySet getPropertySet(T var1);

   @NotNull
   protected abstract WebServiceContextDelegate getWebServiceContextDelegate(T var1);

   protected void handle(final T connection) throws IOException {
      final Codec codec = (Codec)this.codecPool.take();
      Packet request = this.decodePacket(connection, codec);
      if (!request.getMessage().isFault()) {
         this.endpoint.schedule(request, new WSEndpoint.CompletionCallback() {
            public void onCompletion(@NotNull Packet response) {
               try {
                  AbstractServerAsyncTransport.this.encodePacket(connection, response, codec);
               } catch (IOException var3) {
                  var3.printStackTrace();
               }

               AbstractServerAsyncTransport.this.codecPool.recycle(codec);
            }
         });
      }

   }

   private static final class CodecPool extends Pool<Codec> {
      WSEndpoint endpoint;

      CodecPool(WSEndpoint endpoint) {
         this.endpoint = endpoint;
      }

      protected Codec create() {
         return this.endpoint.createCodec();
      }
   }
}
