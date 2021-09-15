package javax.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public interface PrintService {
   String getName();

   DocPrintJob createPrintJob();

   void addPrintServiceAttributeListener(PrintServiceAttributeListener var1);

   void removePrintServiceAttributeListener(PrintServiceAttributeListener var1);

   PrintServiceAttributeSet getAttributes();

   <T extends PrintServiceAttribute> T getAttribute(Class<T> var1);

   DocFlavor[] getSupportedDocFlavors();

   boolean isDocFlavorSupported(DocFlavor var1);

   Class<?>[] getSupportedAttributeCategories();

   boolean isAttributeCategorySupported(Class<? extends Attribute> var1);

   Object getDefaultAttributeValue(Class<? extends Attribute> var1);

   Object getSupportedAttributeValues(Class<? extends Attribute> var1, DocFlavor var2, AttributeSet var3);

   boolean isAttributeValueSupported(Attribute var1, DocFlavor var2, AttributeSet var3);

   AttributeSet getUnsupportedAttributes(DocFlavor var1, AttributeSet var2);

   ServiceUIFactory getServiceUIFactory();

   boolean equals(Object var1);

   int hashCode();
}
