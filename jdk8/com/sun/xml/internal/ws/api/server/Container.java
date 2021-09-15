package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Container implements ComponentRegistry, ComponentEx {
   private final Set<Component> components = new CopyOnWriteArraySet();
   public static final Container NONE = new Container.NoneContainer();

   protected Container() {
   }

   public <S> S getSPI(Class<S> spiType) {
      if (this.components == null) {
         return null;
      } else {
         Iterator var2 = this.components.iterator();

         Object s;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            Component c = (Component)var2.next();
            s = c.getSPI(spiType);
         } while(s == null);

         return s;
      }
   }

   public Set<Component> getComponents() {
      return this.components;
   }

   @NotNull
   public <E> Iterable<E> getIterableSPI(Class<E> spiType) {
      E item = this.getSPI(spiType);
      if (item != null) {
         Collection<E> c = Collections.singletonList(item);
         return c;
      } else {
         return Collections.emptySet();
      }
   }

   private static final class NoneContainer extends Container {
      private NoneContainer() {
      }

      // $FF: synthetic method
      NoneContainer(Object x0) {
         this();
      }
   }
}
