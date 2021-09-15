package sun.lwawt.macosx;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.peer.MenuItemPeer;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.SunToolkit;
import sun.lwawt.LWToolkit;

public class CMenuItem extends CMenuComponent implements MenuItemPeer {
   private final AtomicBoolean enabled = new AtomicBoolean(true);

   public CMenuItem(MenuItem var1) {
      super(var1);
      this.initialize(var1);
   }

   protected void initialize(MenuItem var1) {
      if (!this.isSeparator()) {
         this.setLabel(var1.getLabel());
         this.setEnabled(var1.isEnabled());
      }

   }

   private boolean isSeparator() {
      String var1 = ((MenuItem)this.getTarget()).getLabel();
      return var1 != null && var1.equals("-");
   }

   long createModel() {
      CMenuComponent var1 = (CMenuComponent)LWToolkit.targetToPeer(this.getTarget().getParent());
      return var1.executeGet((var1x) -> {
         return this.nativeCreate(var1x, this.isSeparator());
      });
   }

   public void setLabel(String var1, char var2, int var3, int var4) {
      int var5 = var4;
      if (var3 == 0) {
         MenuShortcut var6 = ((MenuItem)this.getTarget()).getShortcut();
         if (var6 != null) {
            var3 = var6.getKey();
            var5 = var4 | 4;
            if (var6.usesShiftModifier()) {
               var5 |= 1;
            }
         }
      }

      if (var1 == null) {
         var1 = "";
      }

      if (var2 == '\uffff') {
         var2 = 0;
      }

      this.execute((var5x) -> {
         this.nativeSetLabel(var5x, var1, var2, var3, var5);
      });
   }

   public void setLabel(String var1) {
      this.setLabel(var1, '\u0000', 0, 0);
   }

   public final void setImage(Image var1) {
      CImage var2 = CImage.getCreator().createFromImage(var1);
      this.execute((var2x) -> {
         if (var2 == null) {
            this.nativeSetImage(var2x, 0L);
         } else {
            var2.execute((var3) -> {
               this.nativeSetImage(var2x, var3);
            });
         }

      });
   }

   public final void setToolTipText(String var1) {
      this.execute((var2) -> {
         this.nativeSetTooltip(var2, var1);
      });
   }

   public void enable() {
      this.setEnabled(true);
   }

   public void disable() {
      this.setEnabled(false);
   }

   public final boolean isEnabled() {
      return this.enabled.get();
   }

   public void setEnabled(boolean var1) {
      Object var2 = LWToolkit.targetToPeer(this.getTarget().getParent());
      if (var2 instanceof CMenuItem) {
         var1 &= ((CMenuItem)var2).isEnabled();
      }

      if (this.enabled.compareAndSet(!var1, var1)) {
         this.execute((var2x) -> {
            this.nativeSetEnabled(var2x, var1);
         });
      }

   }

   private native long nativeCreate(long var1, boolean var3);

   private native void nativeSetLabel(long var1, String var3, char var4, int var5, int var6);

   private native void nativeSetImage(long var1, long var3);

   private native void nativeSetTooltip(long var1, String var3);

   private native void nativeSetEnabled(long var1, boolean var3);

   void handleAction(final long var1, final int var3) {
      assert CThreading.assertAppKit();

      SunToolkit.executeOnEventHandlerThread(this.getTarget(), new Runnable() {
         public void run() {
            String var1x = ((MenuItem)CMenuItem.this.getTarget()).getActionCommand();
            ActionEvent var2 = new ActionEvent(CMenuItem.this.getTarget(), 1001, var1x, var1, var3);
            SunToolkit.postEvent(SunToolkit.targetToAppContext(CMenuItem.this.getTarget()), var2);
         }
      });
   }
}
