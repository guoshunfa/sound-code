package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_NZ extends ParallelListResourceBundle {
   protected final Object[][] getContents() {
      return new Object[][]{{"TimePatterns", new String[]{"h:mm:ss a z", "h:mm:ss a", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE, d MMMM yyyy", "d MMMM yyyy", "d/MM/yyyy", "d/MM/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
