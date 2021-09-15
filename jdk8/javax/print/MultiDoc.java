package javax.print;

import java.io.IOException;

public interface MultiDoc {
   Doc getDoc() throws IOException;

   MultiDoc next() throws IOException;
}
