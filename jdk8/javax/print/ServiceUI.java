package javax.print;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import sun.print.ServiceDialog;
import sun.print.SunAlternateMedia;

public class ServiceUI {
   public static PrintService printDialog(GraphicsConfiguration var0, int var1, int var2, PrintService[] var3, PrintService var4, DocFlavor var5, PrintRequestAttributeSet var6) throws HeadlessException {
      int var7 = -1;
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (var3 != null && var3.length != 0) {
         if (var6 == null) {
            throw new IllegalArgumentException("attributes must be non-null");
         } else {
            if (var4 != null) {
               for(int var8 = 0; var8 < var3.length; ++var8) {
                  if (var3[var8].equals(var4)) {
                     var7 = var8;
                     break;
                  }
               }

               if (var7 < 0) {
                  throw new IllegalArgumentException("services must contain defaultService");
               }
            } else {
               var7 = 0;
            }

            Object var19 = null;
            Rectangle var9 = var0 == null ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds() : var0.getBounds();
            ServiceDialog var10;
            if (var19 instanceof Frame) {
               var10 = new ServiceDialog(var0, var1 + var9.x, var2 + var9.y, var3, var7, var5, var6, (Frame)var19);
            } else {
               var10 = new ServiceDialog(var0, var1 + var9.x, var2 + var9.y, var3, var7, var5, var6, (Dialog)var19);
            }

            Rectangle var11 = var10.getBounds();
            GraphicsEnvironment var12 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] var13 = var12.getScreenDevices();

            for(int var14 = 0; var14 < var13.length; ++var14) {
               var9 = var9.union(var13[var14].getDefaultConfiguration().getBounds());
            }

            if (!var9.contains(var11)) {
               var10.setLocationRelativeTo((Component)var19);
            }

            var10.show();
            if (var10.getStatus() == 1) {
               PrintRequestAttributeSet var20 = var10.getAttributes();
               Class var15 = Destination.class;
               Class var16 = SunAlternateMedia.class;
               Class var17 = Fidelity.class;
               if (var6.containsKey(var15) && !var20.containsKey(var15)) {
                  var6.remove(var15);
               }

               if (var6.containsKey(var16) && !var20.containsKey(var16)) {
                  var6.remove(var16);
               }

               var6.addAll(var20);
               Fidelity var18 = (Fidelity)var6.get(var17);
               if (var18 != null && var18 == Fidelity.FIDELITY_TRUE) {
                  removeUnsupportedAttributes(var10.getPrintService(), var5, var6);
               }
            }

            return var10.getPrintService();
         }
      } else {
         throw new IllegalArgumentException("services must be non-null and non-empty");
      }
   }

   private static void removeUnsupportedAttributes(PrintService var0, DocFlavor var1, AttributeSet var2) {
      AttributeSet var3 = var0.getUnsupportedAttributes(var1, var2);
      if (var3 != null) {
         Attribute[] var4 = var3.toArray();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            Class var6 = var4[var5].getCategory();
            if (var0.isAttributeCategorySupported(var6)) {
               Attribute var7 = (Attribute)var0.getDefaultAttributeValue(var6);
               if (var7 != null) {
                  var2.add(var7);
               } else {
                  var2.remove(var6);
               }
            } else {
               var2.remove(var6);
            }
         }
      }

   }
}
