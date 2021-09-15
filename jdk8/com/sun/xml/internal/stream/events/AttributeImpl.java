package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeImpl extends DummyEvent implements Attribute {
   private String fValue;
   private String fNonNormalizedvalue;
   private QName fQName;
   private String fAttributeType;
   private boolean fIsSpecified;

   public AttributeImpl() {
      this.fAttributeType = "CDATA";
      this.init();
   }

   public AttributeImpl(String name, String value) {
      this.fAttributeType = "CDATA";
      this.init();
      this.fQName = new QName(name);
      this.fValue = value;
   }

   public AttributeImpl(String prefix, String name, String value) {
      this(prefix, (String)null, name, value, (String)null, (String)null, false);
   }

   public AttributeImpl(String prefix, String uri, String localPart, String value, String type) {
      this(prefix, uri, localPart, value, (String)null, type, false);
   }

   public AttributeImpl(String prefix, String uri, String localPart, String value, String nonNormalizedvalue, String type, boolean isSpecified) {
      this(new QName(uri, localPart, prefix), value, nonNormalizedvalue, type, isSpecified);
   }

   public AttributeImpl(QName qname, String value, String nonNormalizedvalue, String type, boolean isSpecified) {
      this.fAttributeType = "CDATA";
      this.init();
      this.fQName = qname;
      this.fValue = value;
      if (type != null && !type.equals("")) {
         this.fAttributeType = type;
      }

      this.fNonNormalizedvalue = nonNormalizedvalue;
      this.fIsSpecified = isSpecified;
   }

   public String toString() {
      return this.fQName.getPrefix() != null && this.fQName.getPrefix().length() > 0 ? this.fQName.getPrefix() + ":" + this.fQName.getLocalPart() + "='" + this.fValue + "'" : this.fQName.getLocalPart() + "='" + this.fValue + "'";
   }

   public void setName(QName name) {
      this.fQName = name;
   }

   public QName getName() {
      return this.fQName;
   }

   public void setValue(String value) {
      this.fValue = value;
   }

   public String getValue() {
      return this.fValue;
   }

   public void setNonNormalizedValue(String nonNormalizedvalue) {
      this.fNonNormalizedvalue = nonNormalizedvalue;
   }

   public String getNonNormalizedValue() {
      return this.fNonNormalizedvalue;
   }

   public void setAttributeType(String attributeType) {
      this.fAttributeType = attributeType;
   }

   public String getDTDType() {
      return this.fAttributeType;
   }

   public void setSpecified(boolean isSpecified) {
      this.fIsSpecified = isSpecified;
   }

   public boolean isSpecified() {
      return this.fIsSpecified;
   }

   protected void writeAsEncodedUnicodeEx(Writer writer) throws IOException {
      writer.write(this.toString());
   }

   protected void init() {
      this.setEventType(10);
   }
}
