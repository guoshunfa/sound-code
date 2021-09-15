package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.output.TXWSerializer;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import javax.xml.namespace.QName;

public abstract class TXW {
   private TXW() {
   }

   static QName getTagName(Class<?> c) {
      String localName = "";
      String nsUri = "##default";
      XmlElement xe = (XmlElement)c.getAnnotation(XmlElement.class);
      if (xe != null) {
         localName = xe.value();
         nsUri = xe.ns();
      }

      if (localName.length() == 0) {
         localName = c.getName();
         int idx = localName.lastIndexOf(46);
         if (idx >= 0) {
            localName = localName.substring(idx + 1);
         }

         localName = Character.toLowerCase(localName.charAt(0)) + localName.substring(1);
      }

      if (nsUri.equals("##default")) {
         Package pkg = c.getPackage();
         if (pkg != null) {
            XmlNamespace xn = (XmlNamespace)pkg.getAnnotation(XmlNamespace.class);
            if (xn != null) {
               nsUri = xn.value();
            }
         }
      }

      if (nsUri.equals("##default")) {
         nsUri = "";
      }

      return new QName(nsUri, localName);
   }

   public static <T extends TypedXmlWriter> T create(Class<T> rootElement, XmlSerializer out) {
      if (out instanceof TXWSerializer) {
         TXWSerializer txws = (TXWSerializer)out;
         return txws.txw._element(rootElement);
      } else {
         Document doc = new Document(out);
         QName n = getTagName(rootElement);
         return (new ContainerElement(doc, (ContainerElement)null, n.getNamespaceURI(), n.getLocalPart()))._cast(rootElement);
      }
   }

   public static <T extends TypedXmlWriter> T create(QName tagName, Class<T> rootElement, XmlSerializer out) {
      if (out instanceof TXWSerializer) {
         TXWSerializer txws = (TXWSerializer)out;
         return txws.txw._element(tagName, rootElement);
      } else {
         return (new ContainerElement(new Document(out), (ContainerElement)null, tagName.getNamespaceURI(), tagName.getLocalPart()))._cast(rootElement);
      }
   }
}
