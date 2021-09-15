package com.sun.xml.internal.ws.encoding.xml;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet;

public class XMLPropertyBag extends BasePropertySet {
   private String contentType;
   private static final BasePropertySet.PropertyMap model = parse(XMLPropertyBag.class);

   protected BasePropertySet.PropertyMap getPropertyMap() {
      return model;
   }

   @PropertySet.Property({"com.sun.jaxws.rest.contenttype"})
   public String getXMLContentType() {
      return this.contentType;
   }

   public void setXMLContentType(String content) {
      this.contentType = content;
   }
}
