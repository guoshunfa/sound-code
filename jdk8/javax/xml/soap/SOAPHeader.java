package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;

public interface SOAPHeader extends SOAPElement {
   SOAPHeaderElement addHeaderElement(Name var1) throws SOAPException;

   SOAPHeaderElement addHeaderElement(QName var1) throws SOAPException;

   Iterator examineMustUnderstandHeaderElements(String var1);

   Iterator examineHeaderElements(String var1);

   Iterator extractHeaderElements(String var1);

   SOAPHeaderElement addNotUnderstoodHeaderElement(QName var1) throws SOAPException;

   SOAPHeaderElement addUpgradeHeaderElement(Iterator var1) throws SOAPException;

   SOAPHeaderElement addUpgradeHeaderElement(String[] var1) throws SOAPException;

   SOAPHeaderElement addUpgradeHeaderElement(String var1) throws SOAPException;

   Iterator examineAllHeaderElements();

   Iterator extractAllHeaderElements();
}
