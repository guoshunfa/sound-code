package java.awt;

public final class JobAttributes implements Cloneable {
   private int copies;
   private JobAttributes.DefaultSelectionType defaultSelection;
   private JobAttributes.DestinationType destination;
   private JobAttributes.DialogType dialog;
   private String fileName;
   private int fromPage;
   private int maxPage;
   private int minPage;
   private JobAttributes.MultipleDocumentHandlingType multipleDocumentHandling;
   private int[][] pageRanges;
   private int prFirst;
   private int prLast;
   private String printer;
   private JobAttributes.SidesType sides;
   private int toPage;

   public JobAttributes() {
      this.setCopiesToDefault();
      this.setDefaultSelection(JobAttributes.DefaultSelectionType.ALL);
      this.setDestination(JobAttributes.DestinationType.PRINTER);
      this.setDialog(JobAttributes.DialogType.NATIVE);
      this.setMaxPage(Integer.MAX_VALUE);
      this.setMinPage(1);
      this.setMultipleDocumentHandlingToDefault();
      this.setSidesToDefault();
   }

   public JobAttributes(JobAttributes var1) {
      this.set(var1);
   }

   public JobAttributes(int var1, JobAttributes.DefaultSelectionType var2, JobAttributes.DestinationType var3, JobAttributes.DialogType var4, String var5, int var6, int var7, JobAttributes.MultipleDocumentHandlingType var8, int[][] var9, String var10, JobAttributes.SidesType var11) {
      this.setCopies(var1);
      this.setDefaultSelection(var2);
      this.setDestination(var3);
      this.setDialog(var4);
      this.setFileName(var5);
      this.setMaxPage(var6);
      this.setMinPage(var7);
      this.setMultipleDocumentHandling(var8);
      this.setPageRanges(var9);
      this.setPrinter(var10);
      this.setSides(var11);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public void set(JobAttributes var1) {
      this.copies = var1.copies;
      this.defaultSelection = var1.defaultSelection;
      this.destination = var1.destination;
      this.dialog = var1.dialog;
      this.fileName = var1.fileName;
      this.fromPage = var1.fromPage;
      this.maxPage = var1.maxPage;
      this.minPage = var1.minPage;
      this.multipleDocumentHandling = var1.multipleDocumentHandling;
      this.pageRanges = var1.pageRanges;
      this.prFirst = var1.prFirst;
      this.prLast = var1.prLast;
      this.printer = var1.printer;
      this.sides = var1.sides;
      this.toPage = var1.toPage;
   }

   public int getCopies() {
      return this.copies;
   }

   public void setCopies(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Invalid value for attribute copies");
      } else {
         this.copies = var1;
      }
   }

   public void setCopiesToDefault() {
      this.setCopies(1);
   }

   public JobAttributes.DefaultSelectionType getDefaultSelection() {
      return this.defaultSelection;
   }

   public void setDefaultSelection(JobAttributes.DefaultSelectionType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute defaultSelection");
      } else {
         this.defaultSelection = var1;
      }
   }

   public JobAttributes.DestinationType getDestination() {
      return this.destination;
   }

   public void setDestination(JobAttributes.DestinationType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute destination");
      } else {
         this.destination = var1;
      }
   }

   public JobAttributes.DialogType getDialog() {
      return this.dialog;
   }

   public void setDialog(JobAttributes.DialogType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute dialog");
      } else {
         this.dialog = var1;
      }
   }

   public String getFileName() {
      return this.fileName;
   }

   public void setFileName(String var1) {
      this.fileName = var1;
   }

   public int getFromPage() {
      if (this.fromPage != 0) {
         return this.fromPage;
      } else if (this.toPage != 0) {
         return this.getMinPage();
      } else {
         return this.pageRanges != null ? this.prFirst : this.getMinPage();
      }
   }

   public void setFromPage(int var1) {
      if (var1 > 0 && (this.toPage == 0 || var1 <= this.toPage) && var1 >= this.minPage && var1 <= this.maxPage) {
         this.fromPage = var1;
      } else {
         throw new IllegalArgumentException("Invalid value for attribute fromPage");
      }
   }

   public int getMaxPage() {
      return this.maxPage;
   }

   public void setMaxPage(int var1) {
      if (var1 > 0 && var1 >= this.minPage) {
         this.maxPage = var1;
      } else {
         throw new IllegalArgumentException("Invalid value for attribute maxPage");
      }
   }

   public int getMinPage() {
      return this.minPage;
   }

   public void setMinPage(int var1) {
      if (var1 > 0 && var1 <= this.maxPage) {
         this.minPage = var1;
      } else {
         throw new IllegalArgumentException("Invalid value for attribute minPage");
      }
   }

   public JobAttributes.MultipleDocumentHandlingType getMultipleDocumentHandling() {
      return this.multipleDocumentHandling;
   }

   public void setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute multipleDocumentHandling");
      } else {
         this.multipleDocumentHandling = var1;
      }
   }

   public void setMultipleDocumentHandlingToDefault() {
      this.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
   }

   public int[][] getPageRanges() {
      int var2;
      if (this.pageRanges == null) {
         int var3;
         if (this.fromPage == 0 && this.toPage == 0) {
            var3 = this.getMinPage();
            return new int[][]{{var3, var3}};
         } else {
            var3 = this.getFromPage();
            var2 = this.getToPage();
            return new int[][]{{var3, var2}};
         }
      } else {
         int[][] var1 = new int[this.pageRanges.length][2];

         for(var2 = 0; var2 < this.pageRanges.length; ++var2) {
            var1[var2][0] = this.pageRanges[var2][0];
            var1[var2][1] = this.pageRanges[var2][1];
         }

         return var1;
      }
   }

   public void setPageRanges(int[][] var1) {
      String var2 = "Invalid value for attribute pageRanges";
      int var3 = 0;
      int var4 = 0;
      if (var1 == null) {
         throw new IllegalArgumentException(var2);
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            if (var1[var5] == null || var1[var5].length != 2 || var1[var5][0] <= var4 || var1[var5][1] < var1[var5][0]) {
               throw new IllegalArgumentException(var2);
            }

            var4 = var1[var5][1];
            if (var3 == 0) {
               var3 = var1[var5][0];
            }
         }

         if (var3 >= this.minPage && var4 <= this.maxPage) {
            int[][] var7 = new int[var1.length][2];

            for(int var6 = 0; var6 < var1.length; ++var6) {
               var7[var6][0] = var1[var6][0];
               var7[var6][1] = var1[var6][1];
            }

            this.pageRanges = var7;
            this.prFirst = var3;
            this.prLast = var4;
         } else {
            throw new IllegalArgumentException(var2);
         }
      }
   }

   public String getPrinter() {
      return this.printer;
   }

   public void setPrinter(String var1) {
      this.printer = var1;
   }

   public JobAttributes.SidesType getSides() {
      return this.sides;
   }

   public void setSides(JobAttributes.SidesType var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid value for attribute sides");
      } else {
         this.sides = var1;
      }
   }

   public void setSidesToDefault() {
      this.setSides(JobAttributes.SidesType.ONE_SIDED);
   }

   public int getToPage() {
      if (this.toPage != 0) {
         return this.toPage;
      } else if (this.fromPage != 0) {
         return this.fromPage;
      } else {
         return this.pageRanges != null ? this.prLast : this.getMinPage();
      }
   }

   public void setToPage(int var1) {
      if (var1 > 0 && (this.fromPage == 0 || var1 >= this.fromPage) && var1 >= this.minPage && var1 <= this.maxPage) {
         this.toPage = var1;
      } else {
         throw new IllegalArgumentException("Invalid value for attribute toPage");
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof JobAttributes)) {
         return false;
      } else {
         JobAttributes var2 = (JobAttributes)var1;
         if (this.fileName == null) {
            if (var2.fileName != null) {
               return false;
            }
         } else if (!this.fileName.equals(var2.fileName)) {
            return false;
         }

         if (this.pageRanges == null) {
            if (var2.pageRanges != null) {
               return false;
            }
         } else {
            label92: {
               if (var2.pageRanges != null && this.pageRanges.length == var2.pageRanges.length) {
                  int var3 = 0;

                  while(true) {
                     if (var3 >= this.pageRanges.length) {
                        break label92;
                     }

                     if (this.pageRanges[var3][0] != var2.pageRanges[var3][0] || this.pageRanges[var3][1] != var2.pageRanges[var3][1]) {
                        return false;
                     }

                     ++var3;
                  }
               }

               return false;
            }
         }

         if (this.printer == null) {
            if (var2.printer != null) {
               return false;
            }
         } else if (!this.printer.equals(var2.printer)) {
            return false;
         }

         return this.copies == var2.copies && this.defaultSelection == var2.defaultSelection && this.destination == var2.destination && this.dialog == var2.dialog && this.fromPage == var2.fromPage && this.maxPage == var2.maxPage && this.minPage == var2.minPage && this.multipleDocumentHandling == var2.multipleDocumentHandling && this.prFirst == var2.prFirst && this.prLast == var2.prLast && this.sides == var2.sides && this.toPage == var2.toPage;
      }
   }

   public int hashCode() {
      int var1 = (this.copies + this.fromPage + this.maxPage + this.minPage + this.prFirst + this.prLast + this.toPage) * 31 << 21;
      if (this.pageRanges != null) {
         int var2 = 0;

         for(int var3 = 0; var3 < this.pageRanges.length; ++var3) {
            var2 += this.pageRanges[var3][0] + this.pageRanges[var3][1];
         }

         var1 ^= var2 * 31 << 11;
      }

      if (this.fileName != null) {
         var1 ^= this.fileName.hashCode();
      }

      if (this.printer != null) {
         var1 ^= this.printer.hashCode();
      }

      return this.defaultSelection.hashCode() << 6 ^ this.destination.hashCode() << 5 ^ this.dialog.hashCode() << 3 ^ this.multipleDocumentHandling.hashCode() << 2 ^ this.sides.hashCode() ^ var1;
   }

   public String toString() {
      int[][] var1 = this.getPageRanges();
      String var2 = "[";
      boolean var3 = true;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var3) {
            var3 = false;
         } else {
            var2 = var2 + ",";
         }

         var2 = var2 + var1[var4][0] + ":" + var1[var4][1];
      }

      var2 = var2 + "]";
      return "copies=" + this.getCopies() + ",defaultSelection=" + this.getDefaultSelection() + ",destination=" + this.getDestination() + ",dialog=" + this.getDialog() + ",fileName=" + this.getFileName() + ",fromPage=" + this.getFromPage() + ",maxPage=" + this.getMaxPage() + ",minPage=" + this.getMinPage() + ",multiple-document-handling=" + this.getMultipleDocumentHandling() + ",page-ranges=" + var2 + ",printer=" + this.getPrinter() + ",sides=" + this.getSides() + ",toPage=" + this.getToPage();
   }

   public static final class SidesType extends AttributeValue {
      private static final int I_ONE_SIDED = 0;
      private static final int I_TWO_SIDED_LONG_EDGE = 1;
      private static final int I_TWO_SIDED_SHORT_EDGE = 2;
      private static final String[] NAMES = new String[]{"one-sided", "two-sided-long-edge", "two-sided-short-edge"};
      public static final JobAttributes.SidesType ONE_SIDED = new JobAttributes.SidesType(0);
      public static final JobAttributes.SidesType TWO_SIDED_LONG_EDGE = new JobAttributes.SidesType(1);
      public static final JobAttributes.SidesType TWO_SIDED_SHORT_EDGE = new JobAttributes.SidesType(2);

      private SidesType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class MultipleDocumentHandlingType extends AttributeValue {
      private static final int I_SEPARATE_DOCUMENTS_COLLATED_COPIES = 0;
      private static final int I_SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = 1;
      private static final String[] NAMES = new String[]{"separate-documents-collated-copies", "separate-documents-uncollated-copies"};
      public static final JobAttributes.MultipleDocumentHandlingType SEPARATE_DOCUMENTS_COLLATED_COPIES = new JobAttributes.MultipleDocumentHandlingType(0);
      public static final JobAttributes.MultipleDocumentHandlingType SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new JobAttributes.MultipleDocumentHandlingType(1);

      private MultipleDocumentHandlingType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class DialogType extends AttributeValue {
      private static final int I_COMMON = 0;
      private static final int I_NATIVE = 1;
      private static final int I_NONE = 2;
      private static final String[] NAMES = new String[]{"common", "native", "none"};
      public static final JobAttributes.DialogType COMMON = new JobAttributes.DialogType(0);
      public static final JobAttributes.DialogType NATIVE = new JobAttributes.DialogType(1);
      public static final JobAttributes.DialogType NONE = new JobAttributes.DialogType(2);

      private DialogType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class DestinationType extends AttributeValue {
      private static final int I_FILE = 0;
      private static final int I_PRINTER = 1;
      private static final String[] NAMES = new String[]{"file", "printer"};
      public static final JobAttributes.DestinationType FILE = new JobAttributes.DestinationType(0);
      public static final JobAttributes.DestinationType PRINTER = new JobAttributes.DestinationType(1);

      private DestinationType(int var1) {
         super(var1, NAMES);
      }
   }

   public static final class DefaultSelectionType extends AttributeValue {
      private static final int I_ALL = 0;
      private static final int I_RANGE = 1;
      private static final int I_SELECTION = 2;
      private static final String[] NAMES = new String[]{"all", "range", "selection"};
      public static final JobAttributes.DefaultSelectionType ALL = new JobAttributes.DefaultSelectionType(0);
      public static final JobAttributes.DefaultSelectionType RANGE = new JobAttributes.DefaultSelectionType(1);
      public static final JobAttributes.DefaultSelectionType SELECTION = new JobAttributes.DefaultSelectionType(2);

      private DefaultSelectionType(int var1) {
         super(var1, NAMES);
      }
   }
}
