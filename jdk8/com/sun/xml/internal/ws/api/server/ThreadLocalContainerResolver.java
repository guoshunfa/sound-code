package com.sun.xml.internal.ws.api.server;

import java.util.concurrent.Executor;

public class ThreadLocalContainerResolver extends ContainerResolver {
   private ThreadLocal<Container> containerThreadLocal = new ThreadLocal<Container>() {
      protected Container initialValue() {
         return Container.NONE;
      }
   };

   public Container getContainer() {
      return (Container)this.containerThreadLocal.get();
   }

   public Container enterContainer(Container container) {
      Container old = (Container)this.containerThreadLocal.get();
      this.containerThreadLocal.set(container);
      return old;
   }

   public void exitContainer(Container old) {
      this.containerThreadLocal.set(old);
   }

   public Executor wrapExecutor(final Container container, final Executor ex) {
      return ex == null ? null : new Executor() {
         public void execute(final Runnable command) {
            ex.execute(new Runnable() {
               public void run() {
                  Container old = ThreadLocalContainerResolver.this.enterContainer(container);

                  try {
                     command.run();
                  } finally {
                     ThreadLocalContainerResolver.this.exitContainer(old);
                  }

               }
            });
         }
      };
   }
}
