package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;

public class DurationDV extends AbstractDateTimeDV {
   public static final int DURATION_TYPE = 0;
   public static final int YEARMONTHDURATION_TYPE = 1;
   public static final int DAYTIMEDURATION_TYPE = 2;
   private static final AbstractDateTimeDV.DateTimeData[] DATETIMES = new AbstractDateTimeDV.DateTimeData[]{new AbstractDateTimeDV.DateTimeData(1696, 9, 1, 0, 0, 0.0D, 90, (String)null, true, (AbstractDateTimeDV)null), new AbstractDateTimeDV.DateTimeData(1697, 2, 1, 0, 0, 0.0D, 90, (String)null, true, (AbstractDateTimeDV)null), new AbstractDateTimeDV.DateTimeData(1903, 3, 1, 0, 0, 0.0D, 90, (String)null, true, (AbstractDateTimeDV)null), new AbstractDateTimeDV.DateTimeData(1903, 7, 1, 0, 0, 0.0D, 90, (String)null, true, (AbstractDateTimeDV)null)};

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         return this.parse(content, 0);
      } catch (Exception var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "duration"});
      }
   }

   protected AbstractDateTimeDV.DateTimeData parse(String str, int durationType) throws SchemaDateTimeException {
      int len = str.length();
      AbstractDateTimeDV.DateTimeData date = new AbstractDateTimeDV.DateTimeData(str, this);
      int start = 0;
      int start = start + 1;
      char c = str.charAt(start);
      if (c != 'P' && c != '-') {
         throw new SchemaDateTimeException();
      } else {
         date.utc = c == '-' ? 45 : 0;
         if (c == '-' && str.charAt(start++) != 'P') {
            throw new SchemaDateTimeException();
         } else {
            int negate = 1;
            if (date.utc == 45) {
               negate = -1;
            }

            boolean designator = false;
            int endDate = this.indexOf(str, start, len, 'T');
            if (endDate == -1) {
               endDate = len;
            } else if (durationType == 1) {
               throw new SchemaDateTimeException();
            }

            int end = this.indexOf(str, start, endDate, 'Y');
            if (end != -1) {
               if (durationType == 2) {
                  throw new SchemaDateTimeException();
               }

               date.year = negate * this.parseInt(str, start, end);
               start = end + 1;
               designator = true;
            }

            end = this.indexOf(str, start, endDate, 'M');
            if (end != -1) {
               if (durationType == 2) {
                  throw new SchemaDateTimeException();
               }

               date.month = negate * this.parseInt(str, start, end);
               start = end + 1;
               designator = true;
            }

            end = this.indexOf(str, start, endDate, 'D');
            if (end != -1) {
               if (durationType == 1) {
                  throw new SchemaDateTimeException();
               }

               date.day = negate * this.parseInt(str, start, end);
               start = end + 1;
               designator = true;
            }

            if (len == endDate && start != len) {
               throw new SchemaDateTimeException();
            } else {
               if (len != endDate) {
                  label95: {
                     ++start;
                     end = this.indexOf(str, start, len, 'H');
                     if (end != -1) {
                        date.hour = negate * this.parseInt(str, start, end);
                        start = end + 1;
                        designator = true;
                     }

                     end = this.indexOf(str, start, len, 'M');
                     if (end != -1) {
                        date.minute = negate * this.parseInt(str, start, end);
                        start = end + 1;
                        designator = true;
                     }

                     end = this.indexOf(str, start, len, 'S');
                     if (end != -1) {
                        date.second = (double)negate * this.parseSecond(str, start, end);
                        start = end + 1;
                        designator = true;
                     }

                     if (start == len) {
                        --start;
                        if (str.charAt(start) != 'T') {
                           break label95;
                        }
                     }

                     throw new SchemaDateTimeException();
                  }
               }

               if (!designator) {
                  throw new SchemaDateTimeException();
               } else {
                  return date;
               }
            }
         }
      }
   }

   protected short compareDates(AbstractDateTimeDV.DateTimeData date1, AbstractDateTimeDV.DateTimeData date2, boolean strict) {
      short resultB = true;
      short resultA = this.compareOrder(date1, date2);
      if (resultA == 0) {
         return 0;
      } else {
         AbstractDateTimeDV.DateTimeData[] result = new AbstractDateTimeDV.DateTimeData[]{new AbstractDateTimeDV.DateTimeData((String)null, this), new AbstractDateTimeDV.DateTimeData((String)null, this)};
         AbstractDateTimeDV.DateTimeData tempA = this.addDuration(date1, DATETIMES[0], result[0]);
         AbstractDateTimeDV.DateTimeData tempB = this.addDuration(date2, DATETIMES[0], result[1]);
         resultA = this.compareOrder(tempA, tempB);
         if (resultA == 2) {
            return 2;
         } else {
            tempA = this.addDuration(date1, DATETIMES[1], result[0]);
            tempB = this.addDuration(date2, DATETIMES[1], result[1]);
            short resultB = this.compareOrder(tempA, tempB);
            resultA = this.compareResults(resultA, resultB, strict);
            if (resultA == 2) {
               return 2;
            } else {
               tempA = this.addDuration(date1, DATETIMES[2], result[0]);
               tempB = this.addDuration(date2, DATETIMES[2], result[1]);
               resultB = this.compareOrder(tempA, tempB);
               resultA = this.compareResults(resultA, resultB, strict);
               if (resultA == 2) {
                  return 2;
               } else {
                  tempA = this.addDuration(date1, DATETIMES[3], result[0]);
                  tempB = this.addDuration(date2, DATETIMES[3], result[1]);
                  resultB = this.compareOrder(tempA, tempB);
                  resultA = this.compareResults(resultA, resultB, strict);
                  return resultA;
               }
            }
         }
      }
   }

   private short compareResults(short resultA, short resultB, boolean strict) {
      if (resultB == 2) {
         return 2;
      } else if (resultA != resultB && strict) {
         return 2;
      } else if (resultA != resultB && !strict) {
         if (resultA != 0 && resultB != 0) {
            return 2;
         } else {
            return resultA != 0 ? resultA : resultB;
         }
      } else {
         return resultA;
      }
   }

   private AbstractDateTimeDV.DateTimeData addDuration(AbstractDateTimeDV.DateTimeData date, AbstractDateTimeDV.DateTimeData addto, AbstractDateTimeDV.DateTimeData duration) {
      this.resetDateObj(duration);
      int temp = addto.month + date.month;
      duration.month = this.modulo(temp, 1, 13);
      int carry = this.fQuotient(temp, 1, 13);
      duration.year = addto.year + date.year + carry;
      double dtemp = addto.second + date.second;
      carry = (int)Math.floor(dtemp / 60.0D);
      duration.second = dtemp - (double)(carry * 60);
      temp = addto.minute + date.minute + carry;
      carry = this.fQuotient(temp, 60);
      duration.minute = this.mod(temp, 60, carry);
      temp = addto.hour + date.hour + carry;
      carry = this.fQuotient(temp, 24);
      duration.hour = this.mod(temp, 24, carry);
      duration.day = addto.day + date.day + carry;

      while(true) {
         temp = this.maxDayInMonthFor(duration.year, duration.month);
         byte carry;
         if (duration.day < 1) {
            duration.day += this.maxDayInMonthFor(duration.year, duration.month - 1);
            carry = -1;
         } else {
            if (duration.day <= temp) {
               duration.utc = 90;
               return duration;
            }

            duration.day -= temp;
            carry = 1;
         }

         temp = duration.month + carry;
         duration.month = this.modulo(temp, 1, 13);
         duration.year += this.fQuotient(temp, 1, 13);
      }
   }

   protected double parseSecond(String buffer, int start, int end) throws NumberFormatException {
      int dot = -1;

      for(int i = start; i < end; ++i) {
         char ch = buffer.charAt(i);
         if (ch == '.') {
            dot = i;
         } else if (ch > '9' || ch < '0') {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         }
      }

      if (dot + 1 == end) {
         throw new NumberFormatException("'" + buffer + "' has wrong format");
      } else {
         double value = Double.parseDouble(buffer.substring(start, end));
         if (value == Double.POSITIVE_INFINITY) {
            throw new NumberFormatException("'" + buffer + "' has wrong format");
         } else {
            return value;
         }
      }
   }

   protected String dateToString(AbstractDateTimeDV.DateTimeData date) {
      StringBuffer message = new StringBuffer(30);
      if (date.year < 0 || date.month < 0 || date.day < 0 || date.hour < 0 || date.minute < 0 || date.second < 0.0D) {
         message.append('-');
      }

      message.append('P');
      message.append((date.year < 0 ? -1 : 1) * date.year);
      message.append('Y');
      message.append((date.month < 0 ? -1 : 1) * date.month);
      message.append('M');
      message.append((date.day < 0 ? -1 : 1) * date.day);
      message.append('D');
      message.append('T');
      message.append((date.hour < 0 ? -1 : 1) * date.hour);
      message.append('H');
      message.append((date.minute < 0 ? -1 : 1) * date.minute);
      message.append('M');
      this.append2(message, (double)(date.second < 0.0D ? -1 : 1) * date.second);
      message.append('S');
      return message.toString();
   }

   protected Duration getDuration(AbstractDateTimeDV.DateTimeData date) {
      int sign = 1;
      if (date.year < 0 || date.month < 0 || date.day < 0 || date.hour < 0 || date.minute < 0 || date.second < 0.0D) {
         sign = -1;
      }

      return datatypeFactory.newDuration(sign == 1, date.year != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.year)) : null, date.month != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.month)) : null, date.day != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.day)) : null, date.hour != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.hour)) : null, date.minute != Integer.MIN_VALUE ? BigInteger.valueOf((long)(sign * date.minute)) : null, date.second != -2.147483648E9D ? new BigDecimal(String.valueOf((double)sign * date.second)) : null);
   }
}
