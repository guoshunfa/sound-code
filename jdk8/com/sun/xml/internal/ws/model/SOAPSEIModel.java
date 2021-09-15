package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public class SOAPSEIModel extends AbstractSEIModelImpl {
   public SOAPSEIModel(WebServiceFeatureList features) {
      super(features);
   }

   protected void populateMaps() {
      int emptyBodyCount = 0;
      Iterator var2 = this.getJavaMethods().iterator();

      while(var2.hasNext()) {
         JavaMethodImpl jm = (JavaMethodImpl)var2.next();
         this.put(jm.getMethod(), jm);
         boolean bodyFound = false;
         Iterator var5 = jm.getRequestParameters().iterator();

         while(var5.hasNext()) {
            ParameterImpl p = (ParameterImpl)var5.next();
            ParameterBinding binding = p.getBinding();
            if (binding.isBody()) {
               this.put(p.getName(), jm);
               bodyFound = true;
            }
         }

         if (!bodyFound) {
            this.put(this.emptyBodyName, jm);
            ++emptyBodyCount;
         }
      }

      if (emptyBodyCount > 1) {
      }

   }

   public Set<QName> getKnownHeaders() {
      Set<QName> headers = new HashSet();
      Iterator var2 = this.getJavaMethods().iterator();

      while(var2.hasNext()) {
         JavaMethodImpl method = (JavaMethodImpl)var2.next();
         Iterator<ParameterImpl> params = method.getRequestParameters().iterator();
         this.fillHeaders(params, headers, WebParam.Mode.IN);
         params = method.getResponseParameters().iterator();
         this.fillHeaders(params, headers, WebParam.Mode.OUT);
      }

      return headers;
   }

   private void fillHeaders(Iterator<ParameterImpl> params, Set<QName> headers, WebParam.Mode mode) {
      while(params.hasNext()) {
         ParameterImpl param = (ParameterImpl)params.next();
         ParameterBinding binding = mode == WebParam.Mode.IN ? param.getInBinding() : param.getOutBinding();
         QName name = param.getName();
         if (binding.isHeader() && !headers.contains(name)) {
            headers.add(name);
         }
      }

   }
}
