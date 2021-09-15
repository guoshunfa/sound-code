package javax.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class JTextArea extends JTextComponent {
   private static final String uiClassID = "TextAreaUI";
   private int rows;
   private int columns;
   private int columnWidth;
   private int rowHeight;
   private boolean wrap;
   private boolean word;

   public JTextArea() {
      this((Document)null, (String)null, 0, 0);
   }

   public JTextArea(String var1) {
      this((Document)null, var1, 0, 0);
   }

   public JTextArea(int var1, int var2) {
      this((Document)null, (String)null, var1, var2);
   }

   public JTextArea(String var1, int var2, int var3) {
      this((Document)null, var1, var2, var3);
   }

   public JTextArea(Document var1) {
      this(var1, (String)null, 0, 0);
   }

   public JTextArea(Document var1, String var2, int var3, int var4) {
      this.rows = var3;
      this.columns = var4;
      if (var1 == null) {
         var1 = this.createDefaultModel();
      }

      this.setDocument(var1);
      if (var2 != null) {
         this.setText(var2);
         this.select(0, 0);
      }

      if (var3 < 0) {
         throw new IllegalArgumentException("rows: " + var3);
      } else if (var4 < 0) {
         throw new IllegalArgumentException("columns: " + var4);
      } else {
         LookAndFeel.installProperty(this, "focusTraversalKeysForward", JComponent.getManagingFocusForwardTraversalKeys());
         LookAndFeel.installProperty(this, "focusTraversalKeysBackward", JComponent.getManagingFocusBackwardTraversalKeys());
      }
   }

   public String getUIClassID() {
      return "TextAreaUI";
   }

   protected Document createDefaultModel() {
      return new PlainDocument();
   }

   public void setTabSize(int var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         int var3 = this.getTabSize();
         var2.putProperty("tabSize", var1);
         this.firePropertyChange("tabSize", var3, var1);
      }

   }

   public int getTabSize() {
      int var1 = 8;
      Document var2 = this.getDocument();
      if (var2 != null) {
         Integer var3 = (Integer)var2.getProperty("tabSize");
         if (var3 != null) {
            var1 = var3;
         }
      }

      return var1;
   }

   public void setLineWrap(boolean var1) {
      boolean var2 = this.wrap;
      this.wrap = var1;
      this.firePropertyChange("lineWrap", var2, var1);
   }

   public boolean getLineWrap() {
      return this.wrap;
   }

   public void setWrapStyleWord(boolean var1) {
      boolean var2 = this.word;
      this.word = var1;
      this.firePropertyChange("wrapStyleWord", var2, var1);
   }

   public boolean getWrapStyleWord() {
      return this.word;
   }

   public int getLineOfOffset(int var1) throws BadLocationException {
      Document var2 = this.getDocument();
      if (var1 < 0) {
         throw new BadLocationException("Can't translate offset to line", -1);
      } else if (var1 > var2.getLength()) {
         throw new BadLocationException("Can't translate offset to line", var2.getLength() + 1);
      } else {
         Element var3 = this.getDocument().getDefaultRootElement();
         return var3.getElementIndex(var1);
      }
   }

   public int getLineCount() {
      Element var1 = this.getDocument().getDefaultRootElement();
      return var1.getElementCount();
   }

   public int getLineStartOffset(int var1) throws BadLocationException {
      int var2 = this.getLineCount();
      if (var1 < 0) {
         throw new BadLocationException("Negative line", -1);
      } else if (var1 >= var2) {
         throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
      } else {
         Element var3 = this.getDocument().getDefaultRootElement();
         Element var4 = var3.getElement(var1);
         return var4.getStartOffset();
      }
   }

   public int getLineEndOffset(int var1) throws BadLocationException {
      int var2 = this.getLineCount();
      if (var1 < 0) {
         throw new BadLocationException("Negative line", -1);
      } else if (var1 >= var2) {
         throw new BadLocationException("No such line", this.getDocument().getLength() + 1);
      } else {
         Element var3 = this.getDocument().getDefaultRootElement();
         Element var4 = var3.getElement(var1);
         int var5 = var4.getEndOffset();
         return var1 == var2 - 1 ? var5 - 1 : var5;
      }
   }

   public void insert(String var1, int var2) {
      Document var3 = this.getDocument();
      if (var3 != null) {
         try {
            var3.insertString(var2, var1, (AttributeSet)null);
         } catch (BadLocationException var5) {
            throw new IllegalArgumentException(var5.getMessage());
         }
      }

   }

   public void append(String var1) {
      Document var2 = this.getDocument();
      if (var2 != null) {
         try {
            var2.insertString(var2.getLength(), var1, (AttributeSet)null);
         } catch (BadLocationException var4) {
         }
      }

   }

   public void replaceRange(String var1, int var2, int var3) {
      if (var3 < var2) {
         throw new IllegalArgumentException("end before start");
      } else {
         Document var4 = this.getDocument();
         if (var4 != null) {
            try {
               if (var4 instanceof AbstractDocument) {
                  ((AbstractDocument)var4).replace(var2, var3 - var2, var1, (AttributeSet)null);
               } else {
                  var4.remove(var2, var3 - var2);
                  var4.insertString(var2, var1, (AttributeSet)null);
               }
            } catch (BadLocationException var6) {
               throw new IllegalArgumentException(var6.getMessage());
            }
         }

      }
   }

   public int getRows() {
      return this.rows;
   }

   public void setRows(int var1) {
      int var2 = this.rows;
      if (var1 < 0) {
         throw new IllegalArgumentException("rows less than zero.");
      } else {
         if (var1 != var2) {
            this.rows = var1;
            this.invalidate();
         }

      }
   }

   protected int getRowHeight() {
      if (this.rowHeight == 0) {
         FontMetrics var1 = this.getFontMetrics(this.getFont());
         this.rowHeight = var1.getHeight();
      }

      return this.rowHeight;
   }

   public int getColumns() {
      return this.columns;
   }

   public void setColumns(int var1) {
      int var2 = this.columns;
      if (var1 < 0) {
         throw new IllegalArgumentException("columns less than zero.");
      } else {
         if (var1 != var2) {
            this.columns = var1;
            this.invalidate();
         }

      }
   }

   protected int getColumnWidth() {
      if (this.columnWidth == 0) {
         FontMetrics var1 = this.getFontMetrics(this.getFont());
         this.columnWidth = var1.charWidth('m');
      }

      return this.columnWidth;
   }

   public Dimension getPreferredSize() {
      Dimension var1 = super.getPreferredSize();
      var1 = var1 == null ? new Dimension(400, 400) : var1;
      Insets var2 = this.getInsets();
      if (this.columns != 0) {
         var1.width = Math.max(var1.width, this.columns * this.getColumnWidth() + var2.left + var2.right);
      }

      if (this.rows != 0) {
         var1.height = Math.max(var1.height, this.rows * this.getRowHeight() + var2.top + var2.bottom);
      }

      return var1;
   }

   public void setFont(Font var1) {
      super.setFont(var1);
      this.rowHeight = 0;
      this.columnWidth = 0;
   }

   protected String paramString() {
      String var1 = this.wrap ? "true" : "false";
      String var2 = this.word ? "true" : "false";
      return super.paramString() + ",colums=" + this.columns + ",columWidth=" + this.columnWidth + ",rows=" + this.rows + ",rowHeight=" + this.rowHeight + ",word=" + var2 + ",wrap=" + var1;
   }

   public boolean getScrollableTracksViewportWidth() {
      return this.wrap ? true : super.getScrollableTracksViewportWidth();
   }

   public Dimension getPreferredScrollableViewportSize() {
      Dimension var1 = super.getPreferredScrollableViewportSize();
      var1 = var1 == null ? new Dimension(400, 400) : var1;
      Insets var2 = this.getInsets();
      var1.width = this.columns == 0 ? var1.width : this.columns * this.getColumnWidth() + var2.left + var2.right;
      var1.height = this.rows == 0 ? var1.height : this.rows * this.getRowHeight() + var2.top + var2.bottom;
      return var1;
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      switch(var2) {
      case 0:
         return this.getColumnWidth();
      case 1:
         return this.getRowHeight();
      default:
         throw new IllegalArgumentException("Invalid orientation: " + var2);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("TextAreaUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTextArea.AccessibleJTextArea();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJTextArea extends JTextComponent.AccessibleJTextComponent {
      protected AccessibleJTextArea() {
         super();
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         var1.add(AccessibleState.MULTI_LINE);
         return var1;
      }
   }
}
