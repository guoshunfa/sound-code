package java.awt.peer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public interface DesktopPeer {
   boolean isSupported(Desktop.Action var1);

   void open(File var1) throws IOException;

   void edit(File var1) throws IOException;

   void print(File var1) throws IOException;

   void mail(URI var1) throws IOException;

   void browse(URI var1) throws IOException;
}
