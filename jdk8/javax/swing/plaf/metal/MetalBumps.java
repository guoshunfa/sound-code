package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import sun.awt.AppContext;

class MetalBumps implements Icon {
   static final Color ALPHA = new Color(0, 0, 0, 0);
   protected int xBumps;
   protected int yBumps;
   protected Color topColor;
   protected Color shadowColor;
   protected Color backColor;
   private static final Object METAL_BUMPS = new Object();
   protected BumpBuffer buffer;

   public MetalBumps(int var1, int var2, Color var3, Color var4, Color var5) {
      this.setBumpArea(var1, var2);
      this.setBumpColors(var3, var4, var5);
   }

   private static BumpBuffer createBuffer(GraphicsConfiguration var0, Color var1, Color var2, Color var3) {
      AppContext var4 = AppContext.getAppContext();
      Object var5 = (List)var4.get(METAL_BUMPS);
      if (var5 == null) {
         var5 = new ArrayList();
         var4.put(METAL_BUMPS, var5);
      }

      Iterator var6 = ((List)var5).iterator();

      BumpBuffer var7;
      do {
         if (!var6.hasNext()) {
            BumpBuffer var8 = new BumpBuffer(var0, var1, var2, var3);
            ((List)var5).add(var8);
            return var8;
         }

         var7 = (BumpBuffer)var6.next();
      } while(!var7.hasSameConfiguration(var0, var1, var2, var3));

      return var7;
   }

   public void setBumpArea(Dimension var1) {
      this.setBumpArea(var1.width, var1.height);
   }

   public void setBumpArea(int var1, int var2) {
      this.xBumps = var1 / 2;
      this.yBumps = var2 / 2;
   }

   public void setBumpColors(Color var1, Color var2, Color var3) {
      this.topColor = var1;
      this.shadowColor = var2;
      if (var3 == null) {
         this.backColor = ALPHA;
      } else {
         this.backColor = var3;
      }

   }

   public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
      GraphicsConfiguration var5 = var2 instanceof Graphics2D ? ((Graphics2D)var2).getDeviceConfiguration() : null;
      if (this.buffer == null || !this.buffer.hasSameConfiguration(var5, this.topColor, this.shadowColor, this.backColor)) {
         this.buffer = createBuffer(var5, this.topColor, this.shadowColor, this.backColor);
      }

      byte var6 = 64;
      byte var7 = 64;
      int var8 = this.getIconWidth();
      int var9 = this.getIconHeight();
      int var10 = var3 + var8;
      int var11 = var4 + var9;

      for(int var12 = var3; var4 < var11; var4 += var7) {
         int var13 = Math.min(var11 - var4, var7);

         for(var3 = var12; var3 < var10; var3 += var6) {
            int var14 = Math.min(var10 - var3, var6);
            var2.drawImage(this.buffer.getImage(), var3, var4, var3 + var14, var4 + var13, 0, 0, var14, var13, (ImageObserver)null);
         }
      }

   }

   public int getIconWidth() {
      return this.xBumps * 2;
   }

   public int getIconHeight() {
      return this.yBumps * 2;
   }
}
