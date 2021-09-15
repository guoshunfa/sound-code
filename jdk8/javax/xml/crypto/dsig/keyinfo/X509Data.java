package javax.xml.crypto.dsig.keyinfo;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface X509Data extends XMLStructure {
   String TYPE = "http://www.w3.org/2000/09/xmldsig#X509Data";
   String RAW_X509_CERTIFICATE_TYPE = "http://www.w3.org/2000/09/xmldsig#rawX509Certificate";

   List getContent();
}
