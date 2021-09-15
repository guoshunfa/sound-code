package sun.lwawt.macosx;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import sun.lwawt.LWWindowPeer;
import sun.lwawt.PlatformComponent;
import sun.lwawt.PlatformWindow;

public class CPrinterDialogPeer extends LWWindowPeer {
   Component fTarget;

   public CPrinterDialogPeer(CPrinterDialog var1, PlatformComponent var2, PlatformWindow var3) {
      super(var1, var2, var3, LWWindowPeer.PeerType.DIALOG);
      this.fTarget = var1;
      super.initialize();
   }

   protected void disposeImpl() {
      LWCToolkit.targetDisposedPeer(this.fTarget, this);
   }

   public void setVisible(boolean var1) {
      if (var1) {
         (new Thread(new Runnable() {
            public void run() {
               CPrinterDialog var1 = (CPrinterDialog)CPrinterDialogPeer.this.fTarget;
               var1.setRetVal(var1.showDialog());
               var1.setVisible(false);
            }
         })).start();
      }

   }

   public void toFront() {
   }

   public void toBack() {
   }

   public void setResizable(boolean var1) {
   }

   public void setEnabled(boolean var1) {
   }

   public void setBounds(int var1, int var2, int var3, int var4) {
   }

   public boolean handleEvent(Event var1) {
      return false;
   }

   public void setForeground(Color var1) {
   }

   public void setBackground(Color var1) {
   }

   public void setFont(Font var1) {
   }

   public boolean requestFocus(boolean var1, boolean var2) {
      return false;
   }

   void start() {
   }

   void invalidate(int var1, int var2, int var3, int var4) {
   }

   public void addDropTarget(DropTarget var1) {
   }

   public void removeDropTarget(DropTarget var1) {
   }

   public boolean isRestackSupported() {
      return false;
   }

   public void updateAlwaysOnTopState() {
   }

   public void updateMinimumSize() {
   }

   public void setModalBlocked(Dialog var1, boolean var2) {
   }

   public void updateFocusableWindowState() {
   }

   static {
      Toolkit.getDefaultToolkit();
   }
}
