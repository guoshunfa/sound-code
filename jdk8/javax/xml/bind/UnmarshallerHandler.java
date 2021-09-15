package javax.xml.bind;

import org.xml.sax.ContentHandler;

public interface UnmarshallerHandler extends ContentHandler {
   Object getResult() throws JAXBException, IllegalStateException;
}
