package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class MultipleDocumentHandling extends EnumSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 8098326460746413466L;
   public static final MultipleDocumentHandling SINGLE_DOCUMENT = new MultipleDocumentHandling(0);
   public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandling(1);
   public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandling(2);
   public static final MultipleDocumentHandling SINGLE_DOCUMENT_NEW_SHEET = new MultipleDocumentHandling(3);
   private static final String[] myStringTable = new String[]{"single-document", "separate-documents-uncollated-copies", "separate-documents-collated-copies", "single-document-new-sheet"};
   private static final MultipleDocumentHandling[] myEnumValueTable;

   protected MultipleDocumentHandling(int var1) {
      super(var1);
   }

   protected String[] getStringTable() {
      return (String[])((String[])myStringTable.clone());
   }

   protected EnumSyntax[] getEnumValueTable() {
      return (EnumSyntax[])((EnumSyntax[])myEnumValueTable.clone());
   }

   public final Class<? extends Attribute> getCategory() {
      return MultipleDocumentHandling.class;
   }

   public final String getName() {
      return "multiple-document-handling";
   }

   static {
      myEnumValueTable = new MultipleDocumentHandling[]{SINGLE_DOCUMENT, SEPARATE_DOCUMENTS_UNCOLLATED_COPIES, SEPARATE_DOCUMENTS_COLLATED_COPIES, SINGLE_DOCUMENT_NEW_SHEET};
   }
}
