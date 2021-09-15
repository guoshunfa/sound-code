package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface SignatureProperties extends XMLStructure {
   String TYPE = "http://www.w3.org/2000/09/xmldsig#SignatureProperties";

   String getId();

   List getProperties();
}
