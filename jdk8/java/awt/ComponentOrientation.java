package java.awt;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

public final class ComponentOrientation implements Serializable {
   private static final long serialVersionUID = -4113291392143563828L;
   private static final int UNK_BIT = 1;
   private static final int HORIZ_BIT = 2;
   private static final int LTR_BIT = 4;
   public static final ComponentOrientation LEFT_TO_RIGHT = new ComponentOrientation(6);
   public static final ComponentOrientation RIGHT_TO_LEFT = new ComponentOrientation(2);
   public static final ComponentOrientation UNKNOWN = new ComponentOrientation(7);
   private int orientation;

   public boolean isHorizontal() {
      return (this.orientation & 2) != 0;
   }

   public boolean isLeftToRight() {
      return (this.orientation & 4) != 0;
   }

   public static ComponentOrientation getOrientation(Locale var0) {
      String var1 = var0.getLanguage();
      return !"iw".equals(var1) && !"ar".equals(var1) && !"fa".equals(var1) && !"ur".equals(var1) ? LEFT_TO_RIGHT : RIGHT_TO_LEFT;
   }

   /** @deprecated */
   @Deprecated
   public static ComponentOrientation getOrientation(ResourceBundle var0) {
      ComponentOrientation var1 = null;

      try {
         var1 = (ComponentOrientation)var0.getObject("Orientation");
      } catch (Exception var3) {
      }

      if (var1 == null) {
         var1 = getOrientation(var0.getLocale());
      }

      if (var1 == null) {
         var1 = getOrientation(Locale.getDefault());
      }

      return var1;
   }

   private ComponentOrientation(int var1) {
      this.orientation = var1;
   }
}
