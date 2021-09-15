package java.awt.peer;

public interface ScrollbarPeer extends ComponentPeer {
   void setValues(int var1, int var2, int var3, int var4);

   void setLineIncrement(int var1);

   void setPageIncrement(int var1);
}
