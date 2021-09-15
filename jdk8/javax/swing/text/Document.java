package javax.swing.text;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;

public interface Document {
   String StreamDescriptionProperty = "stream";
   String TitleProperty = "title";

   int getLength();

   void addDocumentListener(DocumentListener var1);

   void removeDocumentListener(DocumentListener var1);

   void addUndoableEditListener(UndoableEditListener var1);

   void removeUndoableEditListener(UndoableEditListener var1);

   Object getProperty(Object var1);

   void putProperty(Object var1, Object var2);

   void remove(int var1, int var2) throws BadLocationException;

   void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException;

   String getText(int var1, int var2) throws BadLocationException;

   void getText(int var1, int var2, Segment var3) throws BadLocationException;

   Position getStartPosition();

   Position getEndPosition();

   Position createPosition(int var1) throws BadLocationException;

   Element[] getRootElements();

   Element getDefaultRootElement();

   void render(Runnable var1);
}
