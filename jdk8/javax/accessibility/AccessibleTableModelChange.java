package javax.accessibility;

public interface AccessibleTableModelChange {
   int INSERT = 1;
   int UPDATE = 0;
   int DELETE = -1;

   int getType();

   int getFirstRow();

   int getLastRow();

   int getFirstColumn();

   int getLastColumn();
}
