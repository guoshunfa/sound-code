package javax.swing.text;

public class NavigationFilter {
   public void setDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
      var1.setDot(var2, var3);
   }

   public void moveDot(NavigationFilter.FilterBypass var1, int var2, Position.Bias var3) {
      var1.moveDot(var2, var3);
   }

   public int getNextVisualPositionFrom(JTextComponent var1, int var2, Position.Bias var3, int var4, Position.Bias[] var5) throws BadLocationException {
      return var1.getUI().getNextVisualPositionFrom(var1, var2, var3, var4, var5);
   }

   public abstract static class FilterBypass {
      public abstract Caret getCaret();

      public abstract void setDot(int var1, Position.Bias var2);

      public abstract void moveDot(int var1, Position.Bias var2);
   }
}
