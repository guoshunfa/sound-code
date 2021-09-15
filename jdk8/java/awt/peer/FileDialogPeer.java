package java.awt.peer;

import java.io.FilenameFilter;

public interface FileDialogPeer extends DialogPeer {
   void setFile(String var1);

   void setDirectory(String var1);

   void setFilenameFilter(FilenameFilter var1);
}
