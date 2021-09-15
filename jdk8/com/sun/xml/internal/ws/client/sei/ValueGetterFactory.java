package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam;

abstract class ValueGetterFactory {
   static final ValueGetterFactory SYNC = new ValueGetterFactory() {
      ValueGetter get(ParameterImpl p) {
         return p.getMode() != WebParam.Mode.IN && p.getIndex() != -1 ? ValueGetter.HOLDER : ValueGetter.PLAIN;
      }
   };
   static final ValueGetterFactory ASYNC = new ValueGetterFactory() {
      ValueGetter get(ParameterImpl p) {
         return ValueGetter.PLAIN;
      }
   };

   abstract ValueGetter get(ParameterImpl var1);
}
