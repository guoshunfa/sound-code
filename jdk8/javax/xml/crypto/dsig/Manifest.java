package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface Manifest extends XMLStructure {
   String TYPE = "http://www.w3.org/2000/09/xmldsig#Manifest";

   String getId();

   List getReferences();
}
