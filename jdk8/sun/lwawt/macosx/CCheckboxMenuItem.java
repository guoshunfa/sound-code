package sun.lwawt.macosx;

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxMenuItemPeer;
import sun.awt.SunToolkit;

public class CCheckboxMenuItem extends CMenuItem implements CheckboxMenuItemPeer {
   volatile boolean fAutoToggle = true;
   volatile boolean fIsIndeterminate = false;

   private native void nativeSetState(long var1, boolean var3);

   private native void nativeSetIsCheckbox(long var1);

   CCheckboxMenuItem(CheckboxMenuItem var1) {
      super(var1);
      this.execute(this::nativeSetIsCheckbox);
      this.setState(var1.getState());
   }

   public void setState(boolean var1) {
      this.execute((var2) -> {
         this.nativeSetState(var2, var1);
      });
   }

   public void handleAction(final boolean var1) {
      final CheckboxMenuItem var2 = (CheckboxMenuItem)this.getTarget();
      SunToolkit.executeOnEventHandlerThread(var2, new Runnable() {
         public void run() {
            var2.setState(var1);
         }
      });
      ItemEvent var3 = new ItemEvent(var2, 701, var2.getLabel(), var1 ? 1 : 2);
      SunToolkit.postEvent(SunToolkit.targetToAppContext(this.getTarget()), var3);
   }

   public void setIsIndeterminate(boolean var1) {
      this.fIsIndeterminate = var1;
   }

   private boolean isAutoToggle() {
      return this.fAutoToggle;
   }

   public void setAutoToggle(boolean var1) {
      this.fAutoToggle = var1;
   }
}
