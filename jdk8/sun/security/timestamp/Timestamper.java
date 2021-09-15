package sun.security.timestamp;

import java.io.IOException;

public interface Timestamper {
   TSResponse generateTimestamp(TSRequest var1) throws IOException;
}
