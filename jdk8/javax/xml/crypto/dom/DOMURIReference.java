package javax.xml.crypto.dom;

import javax.xml.crypto.URIReference;
import org.w3c.dom.Node;

public interface DOMURIReference extends URIReference {
   Node getHere();
}
