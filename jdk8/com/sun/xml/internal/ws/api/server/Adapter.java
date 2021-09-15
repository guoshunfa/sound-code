package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.config.management.Reconfigurable;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;

public abstract class Adapter<TK extends Adapter.Toolkit> implements Reconfigurable, Component {
   protected final WSEndpoint<?> endpoint;
   protected volatile Pool<TK> pool = new Pool<TK>() {
      protected TK create() {
         return Adapter.this.createToolkit();
      }
   };

   protected Adapter(WSEndpoint endpoint) {
      assert endpoint != null;

      this.endpoint = endpoint;
      endpoint.getComponents().add(this.getEndpointComponent());
   }

   protected Component getEndpointComponent() {
      return new Component() {
         public <S> S getSPI(Class<S> spiType) {
            return spiType.isAssignableFrom(Reconfigurable.class) ? spiType.cast(Adapter.this) : null;
         }
      };
   }

   public void reconfigure() {
      this.pool = new Pool<TK>() {
         protected TK create() {
            return Adapter.this.createToolkit();
         }
      };
   }

   public <S> S getSPI(Class<S> spiType) {
      if (spiType.isAssignableFrom(Reconfigurable.class)) {
         return spiType.cast(this);
      } else {
         return this.endpoint != null ? this.endpoint.getSPI(spiType) : null;
      }
   }

   public WSEndpoint<?> getEndpoint() {
      return this.endpoint;
   }

   protected Pool<TK> getPool() {
      return this.pool;
   }

   protected abstract TK createToolkit();

   public class Toolkit {
      public final Codec codec;
      public final WSEndpoint.PipeHead head;

      public Toolkit() {
         this.codec = Adapter.this.endpoint.createCodec();
         this.head = Adapter.this.endpoint.createPipeHead();
      }
   }
}
