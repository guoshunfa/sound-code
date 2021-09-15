package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationYearMonthImpl extends DurationImpl {
   public DurationYearMonthImpl(boolean isPositive, BigInteger years, BigInteger months) {
      super(isPositive, years, months, (BigInteger)null, (BigInteger)null, (BigInteger)null, (BigDecimal)null);
      this.convertToCanonicalYearMonth();
   }

   protected DurationYearMonthImpl(boolean isPositive, int years, int months) {
      this(isPositive, wrap(years), wrap(months));
   }

   protected DurationYearMonthImpl(long durationInMilliseconds) {
      super(durationInMilliseconds);
      this.convertToCanonicalYearMonth();
      this.days = null;
      this.hours = null;
      this.minutes = null;
      this.seconds = null;
      this.signum = this.calcSignum(this.signum >= 0);
   }

   protected DurationYearMonthImpl(String lexicalRepresentation) {
      super(lexicalRepresentation);
      if (this.getDays() <= 0 && this.getHours() <= 0 && this.getMinutes() <= 0 && this.getSeconds() <= 0) {
         this.convertToCanonicalYearMonth();
      } else {
         throw new IllegalArgumentException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"" + lexicalRepresentation + "\", data model requires PnYnM.");
      }
   }

   public int getValue() {
      return this.getYears() * 12 + this.getMonths();
   }

   private void convertToCanonicalYearMonth() {
      while(this.getMonths() >= 12) {
         this.months = this.months.subtract(BigInteger.valueOf(12L));
         this.years = BigInteger.valueOf((long)this.getYears()).add(BigInteger.ONE);
      }

   }
}
