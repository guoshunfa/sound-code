package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam;
import javax.xml.ws.Holder;

public enum ValueGetter {
   PLAIN {
      public Object get(Object parameter) {
         return parameter;
      }
   },
   HOLDER {
      public Object get(Object parameter) {
         return parameter == null ? null : ((Holder)parameter).value;
      }
   };

   private ValueGetter() {
   }

   public abstract Object get(Object var1);

   public static ValueGetter get(ParameterImpl p) {
      return p.getMode() != WebParam.Mode.IN && p.getIndex() != -1 ? HOLDER : PLAIN;
   }

   // $FF: synthetic method
   ValueGetter(Object x2) {
      this();
   }
}
