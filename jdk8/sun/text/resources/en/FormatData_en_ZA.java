package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_ZA extends ParallelListResourceBundle {
   protected final Object[][] getContents() {
      return new Object[][]{{"NumberPatterns", new String[]{"#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%"}}, {"TimePatterns", new String[]{"h:mm:ss a", "h:mm:ss a", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE dd MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "yyyy/MM/dd"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
