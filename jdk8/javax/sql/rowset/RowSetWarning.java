package javax.sql.rowset;

import java.sql.SQLException;

public class RowSetWarning extends SQLException {
   static final long serialVersionUID = 6678332766434564774L;

   public RowSetWarning(String var1) {
      super(var1);
   }

   public RowSetWarning() {
   }

   public RowSetWarning(String var1, String var2) {
      super(var1, var2);
   }

   public RowSetWarning(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public RowSetWarning getNextWarning() {
      SQLException var1 = this.getNextException();
      if (var1 != null && !(var1 instanceof RowSetWarning)) {
         throw new Error("RowSetWarning chain holds value that is not a RowSetWarning: ");
      } else {
         return (RowSetWarning)var1;
      }
   }

   public void setNextWarning(RowSetWarning var1) {
      this.setNextException(var1);
   }
}
