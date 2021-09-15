package sun.lwawt.macosx;

import java.awt.Desktop;
import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class CDesktopPeer implements DesktopPeer {
   public boolean isSupported(Desktop.Action var1) {
      return true;
   }

   public void open(File var1) throws IOException {
      this.lsOpenFile(var1, false);
   }

   public void edit(File var1) throws IOException {
      this.lsOpenFile(var1, false);
   }

   public void print(File var1) throws IOException {
      this.lsOpenFile(var1, true);
   }

   public void mail(URI var1) throws IOException {
      this.lsOpen(var1);
   }

   public void browse(URI var1) throws IOException {
      this.lsOpen(var1);
   }

   private void lsOpen(URI var1) throws IOException {
      int var2 = _lsOpenURI(var1.toString());
      if (var2 != 0) {
         throw new IOException("Failed to mail or browse " + var1 + ". Error code: " + var2);
      }
   }

   private void lsOpenFile(File var1, boolean var2) throws IOException {
      int var3 = _lsOpenFile(var1.getCanonicalPath(), var2);
      if (var3 != 0) {
         throw new IOException("Failed to open, edit or print " + var1 + ". Error code: " + var3);
      }
   }

   private static native int _lsOpenURI(String var0);

   private static native int _lsOpenFile(String var0, boolean var1);
}
