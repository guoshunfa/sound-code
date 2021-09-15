package javax.print.attribute;

import java.io.Serializable;

public abstract class ResolutionSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = 2706743076526672017L;
   private int crossFeedResolution;
   private int feedResolution;
   public static final int DPI = 100;
   public static final int DPCM = 254;

   public ResolutionSyntax(int var1, int var2, int var3) {
      if (var1 < 1) {
         throw new IllegalArgumentException("crossFeedResolution is < 1");
      } else if (var2 < 1) {
         throw new IllegalArgumentException("feedResolution is < 1");
      } else if (var3 < 1) {
         throw new IllegalArgumentException("units is < 1");
      } else {
         this.crossFeedResolution = var1 * var3;
         this.feedResolution = var2 * var3;
      }
   }

   private static int convertFromDphi(int var0, int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException(": units is < 1");
      } else {
         int var2 = var1 / 2;
         return (var0 + var2) / var1;
      }
   }

   public int[] getResolution(int var1) {
      return new int[]{this.getCrossFeedResolution(var1), this.getFeedResolution(var1)};
   }

   public int getCrossFeedResolution(int var1) {
      return convertFromDphi(this.crossFeedResolution, var1);
   }

   public int getFeedResolution(int var1) {
      return convertFromDphi(this.feedResolution, var1);
   }

   public String toString(int var1, String var2) {
      StringBuffer var3 = new StringBuffer();
      var3.append(this.getCrossFeedResolution(var1));
      var3.append('x');
      var3.append(this.getFeedResolution(var1));
      if (var2 != null) {
         var3.append(' ');
         var3.append(var2);
      }

      return var3.toString();
   }

   public boolean lessThanOrEquals(ResolutionSyntax var1) {
      return this.crossFeedResolution <= var1.crossFeedResolution && this.feedResolution <= var1.feedResolution;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof ResolutionSyntax && this.crossFeedResolution == ((ResolutionSyntax)var1).crossFeedResolution && this.feedResolution == ((ResolutionSyntax)var1).feedResolution;
   }

   public int hashCode() {
      return this.crossFeedResolution & '\uffff' | (this.feedResolution & '\uffff') << 16;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append(this.crossFeedResolution);
      var1.append('x');
      var1.append(this.feedResolution);
      var1.append(" dphi");
      return var1.toString();
   }

   protected int getCrossFeedResolutionDphi() {
      return this.crossFeedResolution;
   }

   protected int getFeedResolutionDphi() {
      return this.feedResolution;
   }
}
