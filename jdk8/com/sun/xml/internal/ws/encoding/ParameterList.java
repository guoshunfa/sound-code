package com.sun.xml.internal.ws.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

final class ParameterList {
   private final Map<String, String> list;

   ParameterList(String s) {
      HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
      this.list = new HashMap();

      while(true) {
         HeaderTokenizer.Token tk = h.next();
         int type = tk.getType();
         if (type == -4) {
            return;
         }

         if ((char)type != ';') {
            throw new WebServiceException();
         }

         tk = h.next();
         if (tk.getType() == -4) {
            return;
         }

         if (tk.getType() != -1) {
            throw new WebServiceException();
         }

         String name = tk.getValue().toLowerCase();
         tk = h.next();
         if ((char)tk.getType() != '=') {
            throw new WebServiceException();
         }

         tk = h.next();
         type = tk.getType();
         if (type != -1 && type != -2) {
            throw new WebServiceException();
         }

         this.list.put(name, tk.getValue());
      }
   }

   int size() {
      return this.list.size();
   }

   String get(String name) {
      return (String)this.list.get(name.trim().toLowerCase());
   }

   Iterator<String> getNames() {
      return this.list.keySet().iterator();
   }
}
