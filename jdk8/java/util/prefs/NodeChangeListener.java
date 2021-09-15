package java.util.prefs;

import java.util.EventListener;

public interface NodeChangeListener extends EventListener {
   void childAdded(NodeChangeEvent var1);

   void childRemoved(NodeChangeEvent var1);
}
