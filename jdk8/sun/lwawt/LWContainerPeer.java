package sun.lwawt;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.peer.ContainerPeer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import sun.awt.SunGraphicsCallback;
import sun.java2d.pipe.Region;

abstract class LWContainerPeer<T extends Container, D extends JComponent> extends LWCanvasPeer<T, D> implements ContainerPeer {
   private final List<LWComponentPeer<?, ?>> childPeers = new LinkedList();

   LWContainerPeer(T var1, PlatformComponent var2) {
      super(var1, var2);
   }

   final void addChildPeer(LWComponentPeer<?, ?> var1) {
      synchronized(getPeerTreeLock()) {
         this.childPeers.add(this.childPeers.size(), var1);
      }
   }

   final void removeChildPeer(LWComponentPeer<?, ?> var1) {
      synchronized(getPeerTreeLock()) {
         this.childPeers.remove(var1);
      }
   }

   final void setChildPeerZOrder(LWComponentPeer<?, ?> var1, LWComponentPeer<?, ?> var2) {
      synchronized(getPeerTreeLock()) {
         this.childPeers.remove(var1);
         int var4 = var2 != null ? this.childPeers.indexOf(var2) : this.childPeers.size();
         if (var4 >= 0) {
            this.childPeers.add(var4, var1);
         }

      }
   }

   public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
   }

   public final void beginValidate() {
   }

   public final void endValidate() {
   }

   public final void beginLayout() {
      this.setLayouting(true);
   }

   public final void endLayout() {
      this.setLayouting(false);
      this.postPaintEvent(0, 0, 0, 0);
   }

   final List<LWComponentPeer<?, ?>> getChildren() {
      synchronized(getPeerTreeLock()) {
         Object var2 = ((LinkedList)this.childPeers).clone();
         return (List)var2;
      }
   }

   final Region getVisibleRegion() {
      return this.cutChildren(super.getVisibleRegion(), (LWComponentPeer)null);
   }

   final Region cutChildren(Region var1, LWComponentPeer<?, ?> var2) {
      boolean var3 = var2 == null;
      Iterator var4 = this.getChildren().iterator();

      while(true) {
         while(var4.hasNext()) {
            LWComponentPeer var5 = (LWComponentPeer)var4.next();
            if (!var3 && var5 == var2) {
               var3 = true;
            } else if (var3 && var5.isVisible()) {
               Rectangle var6 = var5.getBounds();
               Region var7 = var5.getRegion();
               Region var8 = var7.getTranslatedRegion(var6.x, var6.y);
               var1 = var1.getDifference(var8.getIntersection(this.getContentSize()));
            }
         }

         return var1;
      }
   }

   final LWComponentPeer<?, ?> findPeerAt(int var1, int var2) {
      LWComponentPeer var3 = super.findPeerAt(var1, var2);
      Rectangle var4 = this.getBounds();
      var1 -= var4.x;
      var2 -= var4.y;
      if (var3 != null && this.getContentSize().contains(var1, var2)) {
         synchronized(getPeerTreeLock()) {
            for(int var6 = this.childPeers.size() - 1; var6 >= 0; --var6) {
               LWComponentPeer var7 = ((LWComponentPeer)this.childPeers.get(var6)).findPeerAt(var1, var2);
               if (var7 != null) {
                  var3 = var7;
                  break;
               }
            }
         }
      }

      return var3;
   }

   final void repaintPeer(Rectangle var1) {
      Rectangle var2 = this.getSize().intersection(var1);
      if (this.isShowing() && !var2.isEmpty()) {
         super.repaintPeer(var2);
         this.repaintChildren(var2);
      }
   }

   private void repaintChildren(Rectangle var1) {
      Rectangle var2 = this.getContentSize();
      Iterator var3 = this.getChildren().iterator();

      while(var3.hasNext()) {
         LWComponentPeer var4 = (LWComponentPeer)var3.next();
         Rectangle var5 = var4.getBounds();
         Rectangle var6 = var1.intersection(var5);
         var6 = var6.intersection(var2);
         var6.translate(-var5.x, -var5.y);
         var4.repaintPeer(var6);
      }

   }

   Rectangle getContentSize() {
      return this.getSize();
   }

   public void setEnabled(boolean var1) {
      super.setEnabled(var1);
      Iterator var2 = this.getChildren().iterator();

      while(var2.hasNext()) {
         LWComponentPeer var3 = (LWComponentPeer)var2.next();
         var3.setEnabled(var1 && var3.getTarget().isEnabled());
      }

   }

   public void setBackground(Color var1) {
      Iterator var2 = this.getChildren().iterator();

      while(var2.hasNext()) {
         LWComponentPeer var3 = (LWComponentPeer)var2.next();
         if (!var3.getTarget().isBackgroundSet()) {
            var3.setBackground(var1);
         }
      }

      super.setBackground(var1);
   }

   public void setForeground(Color var1) {
      Iterator var2 = this.getChildren().iterator();

      while(var2.hasNext()) {
         LWComponentPeer var3 = (LWComponentPeer)var2.next();
         if (!var3.getTarget().isForegroundSet()) {
            var3.setForeground(var1);
         }
      }

      super.setForeground(var1);
   }

   public void setFont(Font var1) {
      Iterator var2 = this.getChildren().iterator();

      while(var2.hasNext()) {
         LWComponentPeer var3 = (LWComponentPeer)var2.next();
         if (!var3.getTarget().isFontSet()) {
            var3.setFont(var1);
         }
      }

      super.setFont(var1);
   }

   public final void paint(Graphics var1) {
      super.paint(var1);
      SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.getTarget()).getComponents(), var1, 3);
   }

   public final void print(Graphics var1) {
      super.print(var1);
      SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.getTarget()).getComponents(), var1, 3);
   }
}
