package javax.accessibility;

import java.awt.datatransfer.DataFlavor;
import java.io.InputStream;

public interface AccessibleStreamable {
   DataFlavor[] getMimeTypes();

   InputStream getStream(DataFlavor var1);
}
