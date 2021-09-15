package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.parsers.DOMParserImpl;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.parsers.XIncludeAwareParserConfiguration;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class CoreDOMImplementationImpl implements DOMImplementation, DOMImplementationLS {
   private static final int SIZE = 2;
   private RevalidationHandler[] validators = new RevalidationHandler[2];
   private RevalidationHandler[] dtdValidators = new RevalidationHandler[2];
   private int freeValidatorIndex = -1;
   private int freeDTDValidatorIndex = -1;
   private int currentSize = 2;
   private int docAndDoctypeCounter = 0;
   static CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();

   public static DOMImplementation getDOMImplementation() {
      return singleton;
   }

   public boolean hasFeature(String feature, String version) {
      boolean anyVersion = version == null || version.length() == 0;
      if (feature.equalsIgnoreCase("+XPath") && (anyVersion || version.equals("3.0"))) {
         try {
            Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
            Class[] interfaces = xpathClass.getInterfaces();

            for(int i = 0; i < interfaces.length; ++i) {
               if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                  return true;
               }
            }

            return true;
         } catch (Exception var7) {
            return false;
         }
      } else {
         if (feature.startsWith("+")) {
            feature = feature.substring(1);
         }

         return feature.equalsIgnoreCase("Core") && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0")) || feature.equalsIgnoreCase("XML") && (anyVersion || version.equals("1.0") || version.equals("2.0") || version.equals("3.0")) || feature.equalsIgnoreCase("LS") && (anyVersion || version.equals("3.0"));
      }
   }

   public DocumentType createDocumentType(String qualifiedName, String publicID, String systemID) {
      this.checkQName(qualifiedName);
      return new DocumentTypeImpl((CoreDocumentImpl)null, qualifiedName, publicID, systemID);
   }

   final void checkQName(String qname) {
      int index = qname.indexOf(58);
      int lastIndex = qname.lastIndexOf(58);
      int length = qname.length();
      if (index != 0 && index != length - 1 && lastIndex == index) {
         int start = 0;
         int i;
         String msg;
         String msg;
         if (index > 0) {
            if (!XMLChar.isNCNameStart(qname.charAt(start))) {
               msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
               throw new DOMException((short)5, msg);
            }

            for(i = 1; i < index; ++i) {
               if (!XMLChar.isNCName(qname.charAt(i))) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
                  throw new DOMException((short)5, msg);
               }
            }

            start = index + 1;
         }

         if (!XMLChar.isNCNameStart(qname.charAt(start))) {
            msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
            throw new DOMException((short)5, msg);
         } else {
            for(i = start + 1; i < length; ++i) {
               if (!XMLChar.isNCName(qname.charAt(i))) {
                  msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", (Object[])null);
                  throw new DOMException((short)5, msg);
               }
            }

         }
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", (Object[])null);
         throw new DOMException((short)14, msg);
      }
   }

   public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
      if (doctype != null && doctype.getOwnerDocument() != null) {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", (Object[])null);
         throw new DOMException((short)4, msg);
      } else {
         CoreDocumentImpl doc = new CoreDocumentImpl(doctype);
         Element e = doc.createElementNS(namespaceURI, qualifiedName);
         doc.appendChild(e);
         return doc;
      }
   }

   public Object getFeature(String feature, String version) {
      if (singleton.hasFeature(feature, version)) {
         if (!feature.equalsIgnoreCase("+XPath")) {
            return singleton;
         }

         try {
            Class xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
            Class[] interfaces = xpathClass.getInterfaces();

            for(int i = 0; i < interfaces.length; ++i) {
               if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
                  return xpathClass.newInstance();
               }
            }
         } catch (Exception var6) {
            return null;
         }
      }

      return null;
   }

   public LSParser createLSParser(short mode, String schemaType) throws DOMException {
      if (mode == 1 && (schemaType == null || "http://www.w3.org/2001/XMLSchema".equals(schemaType) || "http://www.w3.org/TR/REC-xml".equals(schemaType))) {
         return schemaType != null && schemaType.equals("http://www.w3.org/TR/REC-xml") ? new DOMParserImpl(new DTDConfiguration(), schemaType) : new DOMParserImpl(new XIncludeAwareParserConfiguration(), schemaType);
      } else {
         String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", (Object[])null);
         throw new DOMException((short)9, msg);
      }
   }

   public LSSerializer createLSSerializer() {
      return new DOMSerializerImpl();
   }

   public LSInput createLSInput() {
      return new DOMInputImpl();
   }

   synchronized RevalidationHandler getValidator(String schemaType) {
      RevalidationHandler val;
      if (schemaType == "http://www.w3.org/2001/XMLSchema") {
         if (this.freeValidatorIndex < 0) {
            return (RevalidationHandler)((RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true));
         } else {
            val = this.validators[this.freeValidatorIndex];
            this.validators[this.freeValidatorIndex--] = null;
            return val;
         }
      } else if (schemaType == "http://www.w3.org/TR/REC-xml") {
         if (this.freeDTDValidatorIndex < 0) {
            return (RevalidationHandler)((RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true));
         } else {
            val = this.dtdValidators[this.freeDTDValidatorIndex];
            this.dtdValidators[this.freeDTDValidatorIndex--] = null;
            return val;
         }
      } else {
         return null;
      }
   }

   synchronized void releaseValidator(String schemaType, RevalidationHandler validator) {
      RevalidationHandler[] newarray;
      if (schemaType == "http://www.w3.org/2001/XMLSchema") {
         ++this.freeValidatorIndex;
         if (this.validators.length == this.freeValidatorIndex) {
            this.currentSize += 2;
            newarray = new RevalidationHandler[this.currentSize];
            System.arraycopy(this.validators, 0, newarray, 0, this.validators.length);
            this.validators = newarray;
         }

         this.validators[this.freeValidatorIndex] = validator;
      } else if (schemaType == "http://www.w3.org/TR/REC-xml") {
         ++this.freeDTDValidatorIndex;
         if (this.dtdValidators.length == this.freeDTDValidatorIndex) {
            this.currentSize += 2;
            newarray = new RevalidationHandler[this.currentSize];
            System.arraycopy(this.dtdValidators, 0, newarray, 0, this.dtdValidators.length);
            this.dtdValidators = newarray;
         }

         this.dtdValidators[this.freeDTDValidatorIndex] = validator;
      }

   }

   protected synchronized int assignDocumentNumber() {
      return ++this.docAndDoctypeCounter;
   }

   protected synchronized int assignDocTypeNumber() {
      return ++this.docAndDoctypeCounter;
   }

   public LSOutput createLSOutput() {
      return new DOMOutputImpl();
   }
}
