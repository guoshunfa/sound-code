package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.font.TextHitInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

public class InputMethodEvent extends AWTEvent {
   private static final long serialVersionUID = 4727190874778922661L;
   public static final int INPUT_METHOD_FIRST = 1100;
   public static final int INPUT_METHOD_TEXT_CHANGED = 1100;
   public static final int CARET_POSITION_CHANGED = 1101;
   public static final int INPUT_METHOD_LAST = 1101;
   long when;
   private transient AttributedCharacterIterator text;
   private transient int committedCharacterCount;
   private transient TextHitInfo caret;
   private transient TextHitInfo visiblePosition;

   public InputMethodEvent(Component var1, int var2, long var3, AttributedCharacterIterator var5, int var6, TextHitInfo var7, TextHitInfo var8) {
      super(var1, var2);
      if (var2 >= 1100 && var2 <= 1101) {
         if (var2 == 1101 && var5 != null) {
            throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED");
         } else {
            this.when = var3;
            this.text = var5;
            int var9 = 0;
            if (var5 != null) {
               var9 = var5.getEndIndex() - var5.getBeginIndex();
            }

            if (var6 >= 0 && var6 <= var9) {
               this.committedCharacterCount = var6;
               this.caret = var7;
               this.visiblePosition = var8;
            } else {
               throw new IllegalArgumentException("committedCharacterCount outside of valid range");
            }
         }
      } else {
         throw new IllegalArgumentException("id outside of valid range");
      }
   }

   public InputMethodEvent(Component var1, int var2, AttributedCharacterIterator var3, int var4, TextHitInfo var5, TextHitInfo var6) {
      this(var1, var2, getMostRecentEventTimeForSource(var1), var3, var4, var5, var6);
   }

   public InputMethodEvent(Component var1, int var2, TextHitInfo var3, TextHitInfo var4) {
      this(var1, var2, getMostRecentEventTimeForSource(var1), (AttributedCharacterIterator)null, 0, var3, var4);
   }

   public AttributedCharacterIterator getText() {
      return this.text;
   }

   public int getCommittedCharacterCount() {
      return this.committedCharacterCount;
   }

   public TextHitInfo getCaret() {
      return this.caret;
   }

   public TextHitInfo getVisiblePosition() {
      return this.visiblePosition;
   }

   public void consume() {
      this.consumed = true;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public long getWhen() {
      return this.when;
   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 1100:
         var1 = "INPUT_METHOD_TEXT_CHANGED";
         break;
      case 1101:
         var1 = "CARET_POSITION_CHANGED";
         break;
      default:
         var1 = "unknown type";
      }

      String var2;
      if (this.text == null) {
         var2 = "no text";
      } else {
         StringBuilder var3 = new StringBuilder("\"");
         int var4 = this.committedCharacterCount;

         char var5;
         for(var5 = this.text.first(); var4-- > 0; var5 = this.text.next()) {
            var3.append(var5);
         }

         var3.append("\" + \"");

         while(var5 != '\uffff') {
            var3.append(var5);
            var5 = this.text.next();
         }

         var3.append("\"");
         var2 = var3.toString();
      }

      String var6 = this.committedCharacterCount + " characters committed";
      String var7;
      if (this.caret == null) {
         var7 = "no caret";
      } else {
         var7 = "caret: " + this.caret.toString();
      }

      String var8;
      if (this.visiblePosition == null) {
         var8 = "no visible position";
      } else {
         var8 = "visible position: " + this.visiblePosition.toString();
      }

      return var1 + ", " + var2 + ", " + var6 + ", " + var7 + ", " + var8;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      if (this.when == 0L) {
         this.when = EventQueue.getMostRecentEventTime();
      }

   }

   private static long getMostRecentEventTimeForSource(Object var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("null source");
      } else {
         AppContext var1 = SunToolkit.targetToAppContext(var0);
         EventQueue var2 = SunToolkit.getSystemEventQueueImplPP(var1);
         return AWTAccessor.getEventQueueAccessor().getMostRecentEventTime(var2);
      }
   }
}
