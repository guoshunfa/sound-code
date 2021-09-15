package javax.print.event;

import javax.print.PrintService;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintServiceAttributeSet;

public class PrintServiceAttributeEvent extends PrintEvent {
   private static final long serialVersionUID = -7565987018140326600L;
   private PrintServiceAttributeSet attributes;

   public PrintServiceAttributeEvent(PrintService var1, PrintServiceAttributeSet var2) {
      super(var1);
      this.attributes = AttributeSetUtilities.unmodifiableView(var2);
   }

   public PrintService getPrintService() {
      return (PrintService)this.getSource();
   }

   public PrintServiceAttributeSet getAttributes() {
      return this.attributes;
   }
}
