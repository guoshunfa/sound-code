package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_PH extends ParallelListResourceBundle {
   protected final Object[][] getContents() {
      return new Object[][]{{"NumberPatterns", new String[]{"#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%"}}, {"NumberElements", new String[]{".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN"}}, {"TimePatterns", new String[]{"h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE, MMMM d, yyyy", "MMMM d, yyyy", "MM d, yy", "M/d/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
