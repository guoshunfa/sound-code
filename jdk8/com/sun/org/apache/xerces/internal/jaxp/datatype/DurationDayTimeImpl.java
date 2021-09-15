package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationDayTimeImpl extends DurationImpl {
   public DurationDayTimeImpl(boolean isPositive, BigInteger days, BigInteger hours, BigInteger minutes, BigDecimal seconds) {
      super(isPositive, (BigInteger)null, (BigInteger)null, days, hours, minutes, seconds);
      this.convertToCanonicalDayTime();
   }

   public DurationDayTimeImpl(boolean isPositive, int days, int hours, int minutes, int seconds) {
      this(isPositive, wrap(days), wrap(hours), wrap(minutes), seconds != Integer.MIN_VALUE ? new BigDecimal(String.valueOf(seconds)) : null);
   }

   protected DurationDayTimeImpl(String lexicalRepresentation) {
      super(lexicalRepresentation);
      if (this.getYears() <= 0 && this.getMonths() <= 0) {
         this.convertToCanonicalDayTime();
      } else {
         throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires a format PnDTnHnMnS.");
      }
   }

   protected DurationDayTimeImpl(long durationInMilliseconds) {
      super(durationInMilliseconds);
      this.convertToCanonicalDayTime();
      this.years = null;
      this.months = null;
   }

   public float getValue() {
      float sec = this.seconds == null ? 0.0F : this.seconds.floatValue();
      return (float)(((this.getDays() * 24 + this.getHours()) * 60 + this.getMinutes()) * 60) + sec;
   }

   private void convertToCanonicalDayTime() {
      while(this.getSeconds() >= 60) {
         this.seconds = this.seconds.subtract(BigDecimal.valueOf(60L));
         this.minutes = BigInteger.valueOf((long)this.getMinutes()).add(BigInteger.ONE);
      }

      while(this.getMinutes() >= 60) {
         this.minutes = this.minutes.subtract(BigInteger.valueOf(60L));
         this.hours = BigInteger.valueOf((long)this.getHours()).add(BigInteger.ONE);
      }

      while(this.getHours() >= 24) {
         this.hours = this.hours.subtract(BigInteger.valueOf(24L));
         this.days = BigInteger.valueOf((long)this.getDays()).add(BigInteger.ONE);
      }

   }
}
