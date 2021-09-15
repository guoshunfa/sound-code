package javax.swing;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class SpinnerDateModel extends AbstractSpinnerModel implements Serializable {
   private Comparable start;
   private Comparable end;
   private Calendar value;
   private int calendarField;

   private boolean calendarFieldOK(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
         return true;
      default:
         return false;
      }
   }

   public SpinnerDateModel(Date var1, Comparable var2, Comparable var3, int var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("value is null");
      } else if (!this.calendarFieldOK(var4)) {
         throw new IllegalArgumentException("invalid calendarField");
      } else if ((var2 == null || var2.compareTo(var1) <= 0) && (var3 == null || var3.compareTo(var1) >= 0)) {
         this.value = Calendar.getInstance();
         this.start = var2;
         this.end = var3;
         this.calendarField = var4;
         this.value.setTime(var1);
      } else {
         throw new IllegalArgumentException("(start <= value <= end) is false");
      }
   }

   public SpinnerDateModel() {
      this(new Date(), (Comparable)null, (Comparable)null, 5);
   }

   public void setStart(Comparable var1) {
      if (var1 == null) {
         if (this.start == null) {
            return;
         }
      } else if (var1.equals(this.start)) {
         return;
      }

      this.start = var1;
      this.fireStateChanged();
   }

   public Comparable getStart() {
      return this.start;
   }

   public void setEnd(Comparable var1) {
      if (var1 == null) {
         if (this.end == null) {
            return;
         }
      } else if (var1.equals(this.end)) {
         return;
      }

      this.end = var1;
      this.fireStateChanged();
   }

   public Comparable getEnd() {
      return this.end;
   }

   public void setCalendarField(int var1) {
      if (!this.calendarFieldOK(var1)) {
         throw new IllegalArgumentException("invalid calendarField");
      } else {
         if (var1 != this.calendarField) {
            this.calendarField = var1;
            this.fireStateChanged();
         }

      }
   }

   public int getCalendarField() {
      return this.calendarField;
   }

   public Object getNextValue() {
      Calendar var1 = Calendar.getInstance();
      var1.setTime(this.value.getTime());
      var1.add(this.calendarField, 1);
      Date var2 = var1.getTime();
      return this.end != null && this.end.compareTo(var2) < 0 ? null : var2;
   }

   public Object getPreviousValue() {
      Calendar var1 = Calendar.getInstance();
      var1.setTime(this.value.getTime());
      var1.add(this.calendarField, -1);
      Date var2 = var1.getTime();
      return this.start != null && this.start.compareTo(var2) > 0 ? null : var2;
   }

   public Date getDate() {
      return this.value.getTime();
   }

   public Object getValue() {
      return this.value.getTime();
   }

   public void setValue(Object var1) {
      if (var1 != null && var1 instanceof Date) {
         if (!var1.equals(this.value.getTime())) {
            this.value.setTime((Date)var1);
            this.fireStateChanged();
         }

      } else {
         throw new IllegalArgumentException("illegal value");
      }
   }
}
