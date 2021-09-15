package javax.swing.text;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DateFormatter extends InternationalFormatter {
   public DateFormatter() {
      this(DateFormat.getDateInstance());
   }

   public DateFormatter(DateFormat var1) {
      super(var1);
      this.setFormat(var1);
   }

   public void setFormat(DateFormat var1) {
      super.setFormat(var1);
   }

   private Calendar getCalendar() {
      Format var1 = this.getFormat();
      return var1 instanceof DateFormat ? ((DateFormat)var1).getCalendar() : Calendar.getInstance();
   }

   boolean getSupportsIncrement() {
      return true;
   }

   Object getAdjustField(int var1, Map var2) {
      Iterator var3 = var2.keySet().iterator();

      Object var4;
      do {
         do {
            if (!var3.hasNext()) {
               return null;
            }

            var4 = var3.next();
         } while(!(var4 instanceof DateFormat.Field));
      } while(var4 != DateFormat.Field.HOUR1 && ((DateFormat.Field)var4).getCalendarField() == -1);

      return var4;
   }

   Object adjustValue(Object var1, Map var2, Object var3, int var4) throws BadLocationException, ParseException {
      if (var3 != null) {
         if (var3 == DateFormat.Field.HOUR1) {
            var3 = DateFormat.Field.HOUR0;
         }

         int var5 = ((DateFormat.Field)var3).getCalendarField();
         Calendar var6 = this.getCalendar();
         if (var6 != null) {
            var6.setTime((Date)var1);
            var6.get(var5);

            Date var10;
            try {
               var6.add(var5, var4);
               var10 = var6.getTime();
            } catch (Throwable var9) {
               var10 = null;
            }

            return var10;
         }
      }

      return null;
   }
}
