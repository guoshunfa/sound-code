package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;

public class HierarchyEvent extends AWTEvent {
   private static final long serialVersionUID = -5337576970038043990L;
   public static final int HIERARCHY_FIRST = 1400;
   public static final int HIERARCHY_CHANGED = 1400;
   public static final int ANCESTOR_MOVED = 1401;
   public static final int ANCESTOR_RESIZED = 1402;
   public static final int HIERARCHY_LAST = 1402;
   public static final int PARENT_CHANGED = 1;
   public static final int DISPLAYABILITY_CHANGED = 2;
   public static final int SHOWING_CHANGED = 4;
   Component changed;
   Container changedParent;
   long changeFlags;

   public HierarchyEvent(Component var1, int var2, Component var3, Container var4) {
      super(var1, var2);
      this.changed = var3;
      this.changedParent = var4;
   }

   public HierarchyEvent(Component var1, int var2, Component var3, Container var4, long var5) {
      super(var1, var2);
      this.changed = var3;
      this.changedParent = var4;
      this.changeFlags = var5;
   }

   public Component getComponent() {
      return this.source instanceof Component ? (Component)this.source : null;
   }

   public Component getChanged() {
      return this.changed;
   }

   public Container getChangedParent() {
      return this.changedParent;
   }

   public long getChangeFlags() {
      return this.changeFlags;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 1400:
         var1 = "HIERARCHY_CHANGED (";
         boolean var2 = true;
         if ((this.changeFlags & 1L) != 0L) {
            var2 = false;
            var1 = var1 + "PARENT_CHANGED";
         }

         if ((this.changeFlags & 2L) != 0L) {
            if (var2) {
               var2 = false;
            } else {
               var1 = var1 + ",";
            }

            var1 = var1 + "DISPLAYABILITY_CHANGED";
         }

         if ((this.changeFlags & 4L) != 0L) {
            if (var2) {
               var2 = false;
            } else {
               var1 = var1 + ",";
            }

            var1 = var1 + "SHOWING_CHANGED";
         }

         if (!var2) {
            var1 = var1 + ",";
         }

         var1 = var1 + this.changed + "," + this.changedParent + ")";
         break;
      case 1401:
         var1 = "ANCESTOR_MOVED (" + this.changed + "," + this.changedParent + ")";
         break;
      case 1402:
         var1 = "ANCESTOR_RESIZED (" + this.changed + "," + this.changedParent + ")";
         break;
      default:
         var1 = "unknown type";
      }

      return var1;
   }
}
