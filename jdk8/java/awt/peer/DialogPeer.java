package java.awt.peer;

import java.awt.Window;
import java.util.List;

public interface DialogPeer extends WindowPeer {
   void setTitle(String var1);

   void setResizable(boolean var1);

   void blockWindows(List<Window> var1);
}
