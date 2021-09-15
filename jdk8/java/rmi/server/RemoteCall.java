package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

/** @deprecated */
@Deprecated
public interface RemoteCall {
   /** @deprecated */
   @Deprecated
   ObjectOutput getOutputStream() throws IOException;

   /** @deprecated */
   @Deprecated
   void releaseOutputStream() throws IOException;

   /** @deprecated */
   @Deprecated
   ObjectInput getInputStream() throws IOException;

   /** @deprecated */
   @Deprecated
   void releaseInputStream() throws IOException;

   /** @deprecated */
   @Deprecated
   ObjectOutput getResultStream(boolean var1) throws IOException, StreamCorruptedException;

   /** @deprecated */
   @Deprecated
   void executeCall() throws Exception;

   /** @deprecated */
   @Deprecated
   void done() throws IOException;
}
