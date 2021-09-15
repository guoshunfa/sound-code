package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_MT extends ParallelListResourceBundle {
   protected final Object[][] getContents() {
      return new Object[][]{{"NumberPatterns", new String[]{"#,##0.###", "¤#,##0.00", "#,##0%"}}, {"NumberElements", new String[]{".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN"}}, {"TimePatterns", new String[]{"HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm"}}, {"DatePatterns", new String[]{"EEEE, d MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "dd/MM/yyyy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
