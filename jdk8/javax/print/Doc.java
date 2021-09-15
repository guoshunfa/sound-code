package javax.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.print.attribute.DocAttributeSet;

public interface Doc {
   DocFlavor getDocFlavor();

   Object getPrintData() throws IOException;

   DocAttributeSet getAttributes();

   Reader getReaderForText() throws IOException;

   InputStream getStreamForBytes() throws IOException;
}
