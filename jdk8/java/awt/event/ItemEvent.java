package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ItemSelectable;

public class ItemEvent extends AWTEvent {
   public static final int ITEM_FIRST = 701;
   public static final int ITEM_LAST = 701;
   public static final int ITEM_STATE_CHANGED = 701;
   public static final int SELECTED = 1;
   public static final int DESELECTED = 2;
   Object item;
   int stateChange;
   private static final long serialVersionUID = -608708132447206933L;

   public ItemEvent(ItemSelectable var1, int var2, Object var3, int var4) {
      super(var1, var2);
      this.item = var3;
      this.stateChange = var4;
   }

   public ItemSelectable getItemSelectable() {
      return (ItemSelectable)this.source;
   }

   public Object getItem() {
      return this.item;
   }

   public int getStateChange() {
      return this.stateChange;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 701:
         var1 = "ITEM_STATE_CHANGED";
         break;
      default:
         var1 = "unknown type";
      }

      String var2;
      switch(this.stateChange) {
      case 1:
         var2 = "SELECTED";
         break;
      case 2:
         var2 = "DESELECTED";
         break;
      default:
         var2 = "unknown type";
      }

      return var1 + ",item=" + this.item + ",stateChange=" + var2;
   }
}
