package javax.print.event;

import javax.print.DocPrintJob;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintJobAttributeSet;

public class PrintJobAttributeEvent extends PrintEvent {
   private static final long serialVersionUID = -6534469883874742101L;
   private PrintJobAttributeSet attributes;

   public PrintJobAttributeEvent(DocPrintJob var1, PrintJobAttributeSet var2) {
      super(var1);
      this.attributes = AttributeSetUtilities.unmodifiableView(var2);
   }

   public DocPrintJob getPrintJob() {
      return (DocPrintJob)this.getSource();
   }

   public PrintJobAttributeSet getAttributes() {
      return this.attributes;
   }
}
