package java.awt.peer;

public interface TrayIconPeer {
   void dispose();

   void setToolTip(String var1);

   void updateImage();

   void displayMessage(String var1, String var2, String var3);

   void showPopupMenu(int var1, int var2);
}
