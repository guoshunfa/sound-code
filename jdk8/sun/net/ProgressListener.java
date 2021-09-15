package sun.net;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
   void progressStart(ProgressEvent var1);

   void progressUpdate(ProgressEvent var1);

   void progressFinish(ProgressEvent var1);
}
