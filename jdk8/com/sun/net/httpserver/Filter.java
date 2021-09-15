package com.sun.net.httpserver;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import jdk.Exported;

@Exported
public abstract class Filter {
   protected Filter() {
   }

   public abstract void doFilter(HttpExchange var1, Filter.Chain var2) throws IOException;

   public abstract String description();

   @Exported
   public static class Chain {
      private ListIterator<Filter> iter;
      private HttpHandler handler;

      public Chain(List<Filter> var1, HttpHandler var2) {
         this.iter = var1.listIterator();
         this.handler = var2;
      }

      public void doFilter(HttpExchange var1) throws IOException {
         if (!this.iter.hasNext()) {
            this.handler.handle(var1);
         } else {
            Filter var2 = (Filter)this.iter.next();
            var2.doFilter(var1, this);
         }

      }
   }
}
