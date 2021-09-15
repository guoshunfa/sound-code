package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.Holder;

enum ValueGetter {
   PLAIN {
      Object get(Object parameter) {
         return parameter;
      }
   },
   HOLDER {
      Object get(Object parameter) {
         return parameter == null ? null : ((Holder)parameter).value;
      }
   };

   private ValueGetter() {
   }

   abstract Object get(Object var1);

   // $FF: synthetic method
   ValueGetter(Object x2) {
      this();
   }
}
