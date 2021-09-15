package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

public final class ContentType {
   private String primaryType;
   private String subType;
   private ParameterList list;

   public ContentType(String s) throws WebServiceException {
      HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
      HeaderTokenizer.Token tk = h.next();
      if (tk.getType() != -1) {
         throw new WebServiceException();
      } else {
         this.primaryType = tk.getValue();
         tk = h.next();
         if ((char)tk.getType() != '/') {
            throw new WebServiceException();
         } else {
            tk = h.next();
            if (tk.getType() != -1) {
               throw new WebServiceException();
            } else {
               this.subType = tk.getValue();
               String rem = h.getRemainder();
               if (rem != null) {
                  this.list = new ParameterList(rem);
               }

            }
         }
      }
   }

   public String getPrimaryType() {
      return this.primaryType;
   }

   public String getSubType() {
      return this.subType;
   }

   public String getBaseType() {
      return this.primaryType + '/' + this.subType;
   }

   public String getParameter(String name) {
      return this.list == null ? null : this.list.get(name);
   }

   public ParameterList getParameterList() {
      return this.list;
   }
}
