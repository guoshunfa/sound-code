package sun.print;

import java.awt.Window;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;

public abstract class DocumentPropertiesUI {
   public static final int DOCUMENTPROPERTIES_ROLE = 199;
   public static final String DOCPROPERTIESCLASSNAME = DocumentPropertiesUI.class.getName();

   public abstract PrintRequestAttributeSet showDocumentProperties(PrinterJob var1, Window var2, PrintService var3, PrintRequestAttributeSet var4);
}
