package javax.swing.text;

public class DocumentFilter {
   public void remove(DocumentFilter.FilterBypass var1, int var2, int var3) throws BadLocationException {
      var1.remove(var2, var3);
   }

   public void insertString(DocumentFilter.FilterBypass var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
      var1.insertString(var2, var3, var4);
   }

   public void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
      var1.replace(var2, var3, var4, var5);
   }

   public abstract static class FilterBypass {
      public abstract Document getDocument();

      public abstract void remove(int var1, int var2) throws BadLocationException;

      public abstract void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException;

      public abstract void replace(int var1, int var2, String var3, AttributeSet var4) throws BadLocationException;
   }
}
