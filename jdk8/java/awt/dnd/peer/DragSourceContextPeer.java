package java.awt.dnd.peer;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;

public interface DragSourceContextPeer {
   void startDrag(DragSourceContext var1, Cursor var2, Image var3, Point var4) throws InvalidDnDOperationException;

   Cursor getCursor();

   void setCursor(Cursor var1) throws InvalidDnDOperationException;

   void transferablesFlavorsChanged();
}
