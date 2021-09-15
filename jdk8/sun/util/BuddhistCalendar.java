package sun.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import sun.util.locale.provider.CalendarDataUtility;

public class BuddhistCalendar extends GregorianCalendar {
   private static final long serialVersionUID = -8527488697350388578L;
   private static final int BUDDHIST_YEAR_OFFSET = 543;
   private transient int yearOffset = 543;

   public BuddhistCalendar() {
   }

   public BuddhistCalendar(TimeZone var1) {
      super(var1);
   }

   public BuddhistCalendar(Locale var1) {
      super(var1);
   }

   public BuddhistCalendar(TimeZone var1, Locale var2) {
      super(var1, var2);
   }

   public String getCalendarType() {
      return "buddhist";
   }

   public boolean equals(Object var1) {
      return var1 instanceof BuddhistCalendar && super.equals(var1);
   }

   public int hashCode() {
      return super.hashCode() ^ 543;
   }

   public int get(int var1) {
      return var1 == 1 ? super.get(var1) + this.yearOffset : super.get(var1);
   }

   public void set(int var1, int var2) {
      if (var1 == 1) {
         super.set(var1, var2 - this.yearOffset);
      } else {
         super.set(var1, var2);
      }

   }

   public void add(int var1, int var2) {
      int var3 = this.yearOffset;
      this.yearOffset = 0;

      try {
         super.add(var1, var2);
      } finally {
         this.yearOffset = var3;
      }

   }

   public void roll(int var1, int var2) {
      int var3 = this.yearOffset;
      this.yearOffset = 0;

      try {
         super.roll(var1, var2);
      } finally {
         this.yearOffset = var3;
      }

   }

   public String getDisplayName(int var1, int var2, Locale var3) {
      return var1 != 0 ? super.getDisplayName(var1, var2, var3) : CalendarDataUtility.retrieveFieldValueName("buddhist", var1, this.get(var1), var2, var3);
   }

   public Map<String, Integer> getDisplayNames(int var1, int var2, Locale var3) {
      return var1 != 0 ? super.getDisplayNames(var1, var2, var3) : CalendarDataUtility.retrieveFieldValueNames("buddhist", var1, var2, var3);
   }

   public int getActualMaximum(int var1) {
      int var2 = this.yearOffset;
      this.yearOffset = 0;

      int var3;
      try {
         var3 = super.getActualMaximum(var1);
      } finally {
         this.yearOffset = var2;
      }

      return var3;
   }

   public String toString() {
      String var1 = super.toString();
      if (!this.isSet(1)) {
         return var1;
      } else {
         int var3 = var1.indexOf("YEAR=");
         if (var3 == -1) {
            return var1;
         } else {
            var3 += "YEAR=".length();
            StringBuilder var4 = new StringBuilder(var1.substring(0, var3));

            while(Character.isDigit(var1.charAt(var3++))) {
            }

            int var5 = this.internalGet(1) + 543;
            var4.append(var5).append(var1.substring(var3 - 1));
            return var4.toString();
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.yearOffset = 543;
   }
}
