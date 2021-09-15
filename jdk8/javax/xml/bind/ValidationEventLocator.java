package javax.xml.bind;

import java.net.URL;
import org.w3c.dom.Node;

public interface ValidationEventLocator {
   URL getURL();

   int getOffset();

   int getLineNumber();

   int getColumnNumber();

   Object getObject();

   Node getNode();
}
