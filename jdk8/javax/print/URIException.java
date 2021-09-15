package javax.print;

import java.net.URI;

public interface URIException {
   int URIInaccessible = 1;
   int URISchemeNotSupported = 2;
   int URIOtherProblem = -1;

   URI getUnsupportedURI();

   int getReason();
}
