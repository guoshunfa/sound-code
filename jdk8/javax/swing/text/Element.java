package javax.swing.text;

public interface Element {
   Document getDocument();

   Element getParentElement();

   String getName();

   AttributeSet getAttributes();

   int getStartOffset();

   int getEndOffset();

   int getElementIndex(int var1);

   int getElementCount();

   Element getElement(int var1);

   boolean isLeaf();
}
