package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSQName;

public class QNameDV extends TypeValidator {
   private static final String EMPTY_STRING = "".intern();

   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      int colonptr = content.indexOf(":");
      String prefix;
      String localpart;
      if (colonptr > 0) {
         prefix = context.getSymbol(content.substring(0, colonptr));
         localpart = content.substring(colonptr + 1);
      } else {
         prefix = EMPTY_STRING;
         localpart = content;
      }

      if (prefix.length() > 0 && !XMLChar.isValidNCName(prefix)) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "QName"});
      } else if (!XMLChar.isValidNCName(localpart)) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "QName"});
      } else {
         String uri = context.getURI(prefix);
         if (prefix.length() > 0 && uri == null) {
            throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[]{content, prefix});
         } else {
            return new QNameDV.XQName(prefix, context.getSymbol(localpart), context.getSymbol(content), uri);
         }
      }
   }

   public int getDataLength(Object value) {
      return ((QNameDV.XQName)value).rawname.length();
   }

   private static final class XQName extends QName implements XSQName {
      public XQName(String prefix, String localpart, String rawname, String uri) {
         this.setValues(prefix, localpart, rawname, uri);
      }

      public boolean equals(Object object) {
         if (!(object instanceof QName)) {
            return false;
         } else {
            QName qname = (QName)object;
            return this.uri == qname.uri && this.localpart == qname.localpart;
         }
      }

      public synchronized String toString() {
         return this.rawname;
      }

      public javax.xml.namespace.QName getJAXPQName() {
         return new javax.xml.namespace.QName(this.uri, this.localpart, this.prefix);
      }

      public QName getXNIQName() {
         return this;
      }
   }
}
