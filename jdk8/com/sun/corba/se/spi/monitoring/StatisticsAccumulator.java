package com.sun.corba.se.spi.monitoring;

public class StatisticsAccumulator {
   protected double max = Double.MIN_VALUE;
   protected double min = Double.MAX_VALUE;
   private double sampleSum;
   private double sampleSquareSum;
   private long sampleCount;
   protected String unit;

   public void sample(double var1) {
      ++this.sampleCount;
      if (var1 < this.min) {
         this.min = var1;
      }

      if (var1 > this.max) {
         this.max = var1;
      }

      this.sampleSum += var1;
      this.sampleSquareSum += var1 * var1;
   }

   public String getValue() {
      return this.toString();
   }

   public String toString() {
      return "Minimum Value = " + this.min + " " + this.unit + " Maximum Value = " + this.max + " " + this.unit + " Average Value = " + this.computeAverage() + " " + this.unit + " Standard Deviation = " + this.computeStandardDeviation() + " " + this.unit + " Samples Collected = " + this.sampleCount;
   }

   protected double computeAverage() {
      return this.sampleSum / (double)this.sampleCount;
   }

   protected double computeStandardDeviation() {
      double var1 = this.sampleSum * this.sampleSum;
      return Math.sqrt((this.sampleSquareSum - var1 / (double)this.sampleCount) / (double)(this.sampleCount - 1L));
   }

   public StatisticsAccumulator(String var1) {
      this.unit = var1;
      this.sampleCount = 0L;
      this.sampleSum = 0.0D;
      this.sampleSquareSum = 0.0D;
   }

   void clearState() {
      this.min = Double.MAX_VALUE;
      this.max = Double.MIN_VALUE;
      this.sampleCount = 0L;
      this.sampleSum = 0.0D;
      this.sampleSquareSum = 0.0D;
   }

   public void unitTestValidate(String var1, double var2, double var4, long var6, double var8, double var10) {
      if (!var1.equals(this.unit)) {
         throw new RuntimeException("Unit is not same as expected Unit\nUnit = " + this.unit + "ExpectedUnit = " + var1);
      } else if (this.min != var2) {
         throw new RuntimeException("Minimum value is not same as expected minimum value\nMin Value = " + this.min + "Expected Min Value = " + var2);
      } else if (this.max != var4) {
         throw new RuntimeException("Maximum value is not same as expected maximum value\nMax Value = " + this.max + "Expected Max Value = " + var4);
      } else if (this.sampleCount != var6) {
         throw new RuntimeException("Sample count is not same as expected Sample Count\nSampleCount = " + this.sampleCount + "Expected Sample Count = " + var6);
      } else if (this.computeAverage() != var8) {
         throw new RuntimeException("Average is not same as expected Average\nAverage = " + this.computeAverage() + "Expected Average = " + var8);
      } else {
         double var12 = Math.abs(this.computeStandardDeviation() - var10);
         if (var12 > 1.0D) {
            throw new RuntimeException("Standard Deviation is not same as expected Std Deviation\nStandard Dev = " + this.computeStandardDeviation() + "Expected Standard Dev = " + var10);
         }
      }
   }
}
