package javax.xml.crypto.dsig;

import java.util.List;
import javax.xml.crypto.XMLStructure;

public interface SignatureProperty extends XMLStructure {
   String getTarget();

   String getId();

   List getContent();
}
