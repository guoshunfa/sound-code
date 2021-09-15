package javax.swing.text;

import java.io.Serializable;

public class TabStop implements Serializable {
   public static final int ALIGN_LEFT = 0;
   public static final int ALIGN_RIGHT = 1;
   public static final int ALIGN_CENTER = 2;
   public static final int ALIGN_DECIMAL = 4;
   public static final int ALIGN_BAR = 5;
   public static final int LEAD_NONE = 0;
   public static final int LEAD_DOTS = 1;
   public static final int LEAD_HYPHENS = 2;
   public static final int LEAD_UNDERLINE = 3;
   public static final int LEAD_THICKLINE = 4;
   public static final int LEAD_EQUALS = 5;
   private int alignment;
   private float position;
   private int leader;

   public TabStop(float var1) {
      this(var1, 0, 0);
   }

   public TabStop(float var1, int var2, int var3) {
      this.alignment = var2;
      this.leader = var3;
      this.position = var1;
   }

   public float getPosition() {
      return this.position;
   }

   public int getAlignment() {
      return this.alignment;
   }

   public int getLeader() {
      return this.leader;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabStop)) {
         return false;
      } else {
         TabStop var2 = (TabStop)var1;
         return this.alignment == var2.alignment && this.leader == var2.leader && this.position == var2.position;
      }
   }

   public int hashCode() {
      return this.alignment ^ this.leader ^ Math.round(this.position);
   }

   public String toString() {
      String var1;
      switch(this.alignment) {
      case 0:
      case 3:
      default:
         var1 = "";
         break;
      case 1:
         var1 = "right ";
         break;
      case 2:
         var1 = "center ";
         break;
      case 4:
         var1 = "decimal ";
         break;
      case 5:
         var1 = "bar ";
      }

      var1 = var1 + "tab @" + this.position;
      if (this.leader != 0) {
         var1 = var1 + " (w/leaders)";
      }

      return var1;
   }
}
