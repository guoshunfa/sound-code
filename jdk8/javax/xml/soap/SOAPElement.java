package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public interface SOAPElement extends Node, Element {
   SOAPElement addChildElement(Name var1) throws SOAPException;

   SOAPElement addChildElement(QName var1) throws SOAPException;

   SOAPElement addChildElement(String var1) throws SOAPException;

   SOAPElement addChildElement(String var1, String var2) throws SOAPException;

   SOAPElement addChildElement(String var1, String var2, String var3) throws SOAPException;

   SOAPElement addChildElement(SOAPElement var1) throws SOAPException;

   void removeContents();

   SOAPElement addTextNode(String var1) throws SOAPException;

   SOAPElement addAttribute(Name var1, String var2) throws SOAPException;

   SOAPElement addAttribute(QName var1, String var2) throws SOAPException;

   SOAPElement addNamespaceDeclaration(String var1, String var2) throws SOAPException;

   String getAttributeValue(Name var1);

   String getAttributeValue(QName var1);

   Iterator getAllAttributes();

   Iterator getAllAttributesAsQNames();

   String getNamespaceURI(String var1);

   Iterator getNamespacePrefixes();

   Iterator getVisibleNamespacePrefixes();

   QName createQName(String var1, String var2) throws SOAPException;

   Name getElementName();

   QName getElementQName();

   SOAPElement setElementQName(QName var1) throws SOAPException;

   boolean removeAttribute(Name var1);

   boolean removeAttribute(QName var1);

   boolean removeNamespaceDeclaration(String var1);

   Iterator getChildElements();

   Iterator getChildElements(Name var1);

   Iterator getChildElements(QName var1);

   void setEncodingStyle(String var1) throws SOAPException;

   String getEncodingStyle();
}
