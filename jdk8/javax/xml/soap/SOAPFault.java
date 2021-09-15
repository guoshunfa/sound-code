package javax.xml.soap;

import java.util.Iterator;
import java.util.Locale;
import javax.xml.namespace.QName;

public interface SOAPFault extends SOAPBodyElement {
   void setFaultCode(Name var1) throws SOAPException;

   void setFaultCode(QName var1) throws SOAPException;

   void setFaultCode(String var1) throws SOAPException;

   Name getFaultCodeAsName();

   QName getFaultCodeAsQName();

   Iterator getFaultSubcodes();

   void removeAllFaultSubcodes();

   void appendFaultSubcode(QName var1) throws SOAPException;

   String getFaultCode();

   void setFaultActor(String var1) throws SOAPException;

   String getFaultActor();

   void setFaultString(String var1) throws SOAPException;

   void setFaultString(String var1, Locale var2) throws SOAPException;

   String getFaultString();

   Locale getFaultStringLocale();

   boolean hasDetail();

   Detail getDetail();

   Detail addDetail() throws SOAPException;

   Iterator getFaultReasonLocales() throws SOAPException;

   Iterator getFaultReasonTexts() throws SOAPException;

   String getFaultReasonText(Locale var1) throws SOAPException;

   void addFaultReasonText(String var1, Locale var2) throws SOAPException;

   String getFaultNode();

   void setFaultNode(String var1) throws SOAPException;

   String getFaultRole();

   void setFaultRole(String var1) throws SOAPException;
}
