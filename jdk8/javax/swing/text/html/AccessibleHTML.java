package javax.swing.text.html;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleText;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

class AccessibleHTML implements Accessible {
   private JEditorPane editor;
   private Document model;
   private DocumentListener docListener;
   private PropertyChangeListener propChangeListener;
   private AccessibleHTML.ElementInfo rootElementInfo;
   private AccessibleHTML.RootHTMLAccessibleContext rootHTMLAccessibleContext;

   public AccessibleHTML(JEditorPane var1) {
      this.editor = var1;
      this.propChangeListener = new AccessibleHTML.PropertyChangeHandler();
      this.setDocument(this.editor.getDocument());
      this.docListener = new AccessibleHTML.DocumentHandler();
   }

   private void setDocument(Document var1) {
      if (this.model != null) {
         this.model.removeDocumentListener(this.docListener);
      }

      if (this.editor != null) {
         this.editor.removePropertyChangeListener(this.propChangeListener);
      }

      this.model = var1;
      if (this.model != null) {
         if (this.rootElementInfo != null) {
            this.rootElementInfo.invalidate(false);
         }

         this.buildInfo();
         this.model.addDocumentListener(this.docListener);
      } else {
         this.rootElementInfo = null;
      }

      if (this.editor != null) {
         this.editor.addPropertyChangeListener(this.propChangeListener);
      }

   }

   private Document getDocument() {
      return this.model;
   }

   private JEditorPane getTextComponent() {
      return this.editor;
   }

   private AccessibleHTML.ElementInfo getRootInfo() {
      return this.rootElementInfo;
   }

   private View getRootView() {
      return this.getTextComponent().getUI().getRootView(this.getTextComponent());
   }

   private Rectangle getRootEditorRect() {
      Rectangle var1 = this.getTextComponent().getBounds();
      if (var1.width > 0 && var1.height > 0) {
         var1.x = var1.y = 0;
         Insets var2 = this.editor.getInsets();
         var1.x += var2.left;
         var1.y += var2.top;
         var1.width -= var2.left + var2.right;
         var1.height -= var2.top + var2.bottom;
         return var1;
      } else {
         return null;
      }
   }

   private Object lock() {
      Document var1 = this.getDocument();
      if (var1 instanceof AbstractDocument) {
         ((AbstractDocument)var1).readLock();
         return var1;
      } else {
         return null;
      }
   }

   private void unlock(Object var1) {
      if (var1 != null) {
         ((AbstractDocument)var1).readUnlock();
      }

   }

   private void buildInfo() {
      Object var1 = this.lock();

      try {
         Document var2 = this.getDocument();
         Element var3 = var2.getDefaultRootElement();
         this.rootElementInfo = new AccessibleHTML.ElementInfo(var3);
         this.rootElementInfo.validate();
      } finally {
         this.unlock(var1);
      }

   }

   AccessibleHTML.ElementInfo createElementInfo(Element var1, AccessibleHTML.ElementInfo var2) {
      AttributeSet var3 = var1.getAttributes();
      if (var3 != null) {
         Object var4 = var3.getAttribute(StyleConstants.NameAttribute);
         if (var4 == HTML.Tag.IMG) {
            return new AccessibleHTML.IconElementInfo(var1, var2);
         }

         if (var4 == HTML.Tag.CONTENT || var4 == HTML.Tag.CAPTION) {
            return new AccessibleHTML.TextElementInfo(var1, var2);
         }

         if (var4 == HTML.Tag.TABLE) {
            return new AccessibleHTML.TableElementInfo(var1, var2);
         }
      }

      return null;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.rootHTMLAccessibleContext == null) {
         this.rootHTMLAccessibleContext = new AccessibleHTML.RootHTMLAccessibleContext(this.rootElementInfo);
      }

      return this.rootHTMLAccessibleContext;
   }

   private class PropertyChangeHandler implements PropertyChangeListener {
      private PropertyChangeHandler() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getPropertyName().equals("document")) {
            AccessibleHTML.this.setDocument(AccessibleHTML.this.editor.getDocument());
         }

      }

      // $FF: synthetic method
      PropertyChangeHandler(Object var2) {
         this();
      }
   }

   private class DocumentHandler implements DocumentListener {
      private DocumentHandler() {
      }

      public void insertUpdate(DocumentEvent var1) {
         AccessibleHTML.this.getRootInfo().update(var1);
      }

      public void removeUpdate(DocumentEvent var1) {
         AccessibleHTML.this.getRootInfo().update(var1);
      }

      public void changedUpdate(DocumentEvent var1) {
         AccessibleHTML.this.getRootInfo().update(var1);
      }

      // $FF: synthetic method
      DocumentHandler(Object var2) {
         this();
      }
   }

   private class ElementInfo {
      private ArrayList<AccessibleHTML.ElementInfo> children;
      private Element element;
      private AccessibleHTML.ElementInfo parent;
      private boolean isValid;
      private boolean canBeValid;

      ElementInfo(Element var2) {
         this(var2, (AccessibleHTML.ElementInfo)null);
      }

      ElementInfo(Element var2, AccessibleHTML.ElementInfo var3) {
         this.element = var2;
         this.parent = var3;
         this.isValid = false;
         this.canBeValid = true;
      }

      protected void validate() {
         this.isValid = true;
         this.loadChildren(this.getElement());
      }

      protected void loadChildren(Element var1) {
         if (!var1.isLeaf()) {
            int var2 = 0;

            for(int var3 = var1.getElementCount(); var2 < var3; ++var2) {
               Element var4 = var1.getElement(var2);
               AccessibleHTML.ElementInfo var5 = AccessibleHTML.this.createElementInfo(var4, this);
               if (var5 != null) {
                  this.addChild(var5);
               } else {
                  this.loadChildren(var4);
               }
            }
         }

      }

      public int getIndexInParent() {
         return this.parent != null && this.parent.isValid() ? this.parent.indexOf(this) : -1;
      }

      public Element getElement() {
         return this.element;
      }

      public AccessibleHTML.ElementInfo getParent() {
         return this.parent;
      }

      public int indexOf(AccessibleHTML.ElementInfo var1) {
         ArrayList var2 = this.children;
         return var2 != null ? var2.indexOf(var1) : -1;
      }

      public AccessibleHTML.ElementInfo getChild(int var1) {
         if (this.validateIfNecessary()) {
            ArrayList var2 = this.children;
            if (var2 != null && var1 >= 0 && var1 < var2.size()) {
               return (AccessibleHTML.ElementInfo)var2.get(var1);
            }
         }

         return null;
      }

      public int getChildCount() {
         this.validateIfNecessary();
         return this.children == null ? 0 : this.children.size();
      }

      protected void addChild(AccessibleHTML.ElementInfo var1) {
         if (this.children == null) {
            this.children = new ArrayList();
         }

         this.children.add(var1);
      }

      protected View getView() {
         if (!this.validateIfNecessary()) {
            return null;
         } else {
            Object var1 = AccessibleHTML.this.lock();

            View var5;
            try {
               View var2 = AccessibleHTML.this.getRootView();
               Element var3 = this.getElement();
               int var4 = var3.getStartOffset();
               if (var2 != null) {
                  var5 = this.getView(var2, var3, var4);
                  return var5;
               }

               var5 = null;
            } finally {
               AccessibleHTML.this.unlock(var1);
            }

            return var5;
         }
      }

      public Rectangle getBounds() {
         if (!this.validateIfNecessary()) {
            return null;
         } else {
            Object var1 = AccessibleHTML.this.lock();

            Rectangle var5;
            try {
               Rectangle var2 = AccessibleHTML.this.getRootEditorRect();
               View var3 = AccessibleHTML.this.getRootView();
               Element var4 = this.getElement();
               if (var2 == null || var3 == null) {
                  return null;
               }

               try {
                  var5 = var3.modelToView(var4.getStartOffset(), Position.Bias.Forward, var4.getEndOffset(), Position.Bias.Backward, var2).getBounds();
               } catch (BadLocationException var9) {
                  return null;
               }
            } finally {
               AccessibleHTML.this.unlock(var1);
            }

            return var5;
         }
      }

      protected boolean isValid() {
         return this.isValid;
      }

      protected AttributeSet getAttributes() {
         return this.validateIfNecessary() ? this.getElement().getAttributes() : null;
      }

      protected AttributeSet getViewAttributes() {
         if (this.validateIfNecessary()) {
            View var1 = this.getView();
            return var1 != null ? var1.getElement().getAttributes() : this.getElement().getAttributes();
         } else {
            return null;
         }
      }

      protected int getIntAttr(AttributeSet var1, Object var2, int var3) {
         if (var1 != null && var1.isDefined(var2)) {
            String var5 = (String)var1.getAttribute(var2);
            int var4;
            if (var5 == null) {
               var4 = var3;
            } else {
               try {
                  var4 = Math.max(0, Integer.parseInt(var5));
               } catch (NumberFormatException var7) {
                  var4 = var3;
               }
            }

            return var4;
         } else {
            return var3;
         }
      }

      protected boolean validateIfNecessary() {
         if (!this.isValid() && this.canBeValid) {
            this.children = null;
            Object var1 = AccessibleHTML.this.lock();

            try {
               this.validate();
            } finally {
               AccessibleHTML.this.unlock(var1);
            }
         }

         return this.isValid();
      }

      protected void invalidate(boolean var1) {
         if (!this.isValid()) {
            if (this.canBeValid && !var1) {
               this.canBeValid = false;
            }

         } else {
            this.isValid = false;
            this.canBeValid = var1;
            if (this.children != null) {
               Iterator var2 = this.children.iterator();

               while(var2.hasNext()) {
                  AccessibleHTML.ElementInfo var3 = (AccessibleHTML.ElementInfo)var2.next();
                  var3.invalidate(false);
               }

               this.children = null;
            }

         }
      }

      private View getView(View var1, Element var2, int var3) {
         if (var1.getElement() == var2) {
            return var1;
         } else {
            int var4 = var1.getViewIndex(var3, Position.Bias.Forward);
            return var4 != -1 && var4 < var1.getViewCount() ? this.getView(var1.getView(var4), var2, var3) : null;
         }
      }

      private int getClosestInfoIndex(int var1) {
         for(int var2 = 0; var2 < this.getChildCount(); ++var2) {
            AccessibleHTML.ElementInfo var3 = this.getChild(var2);
            if (var1 < var3.getElement().getEndOffset() || var1 == var3.getElement().getStartOffset()) {
               return var2;
            }
         }

         return -1;
      }

      private void update(DocumentEvent var1) {
         if (this.isValid()) {
            AccessibleHTML.ElementInfo var2 = this.getParent();
            Element var3 = this.getElement();

            do {
               DocumentEvent.ElementChange var4 = var1.getChange(var3);
               if (var4 != null) {
                  if (var3 == this.getElement()) {
                     this.invalidate(true);
                  } else if (var2 != null) {
                     var2.invalidate(var2 == AccessibleHTML.this.getRootInfo());
                  }

                  return;
               }

               var3 = var3.getParentElement();
            } while(var2 != null && var3 != null && var3 != var2.getElement());

            if (this.getChildCount() > 0) {
               Element var10 = this.getElement();
               int var5 = var1.getOffset();
               int var6 = this.getClosestInfoIndex(var5);
               if (var6 == -1 && var1.getType() == DocumentEvent.EventType.REMOVE && var5 >= var10.getEndOffset()) {
                  var6 = this.getChildCount() - 1;
               }

               AccessibleHTML.ElementInfo var7 = var6 >= 0 ? this.getChild(var6) : null;
               if (var7 != null && var7.getElement().getStartOffset() == var5 && var5 > 0) {
                  var6 = Math.max(var6 - 1, 0);
               }

               int var8;
               if (var1.getType() != DocumentEvent.EventType.REMOVE) {
                  var8 = this.getClosestInfoIndex(var5 + var1.getLength());
                  if (var8 < 0) {
                     var8 = this.getChildCount() - 1;
                  }
               } else {
                  for(var8 = var6; var8 + 1 < this.getChildCount() && this.getChild(var8 + 1).getElement().getEndOffset() == this.getChild(var8 + 1).getElement().getStartOffset(); ++var8) {
                  }
               }

               var6 = Math.max(var6, 0);

               for(int var9 = var6; var9 <= var8 && this.isValid(); ++var9) {
                  this.getChild(var9).update(var1);
               }
            }

         }
      }
   }

   private class TableElementInfo extends AccessibleHTML.ElementInfo implements Accessible {
      protected AccessibleHTML.ElementInfo caption;
      private AccessibleHTML.TableElementInfo.TableCellElementInfo[][] grid;
      private AccessibleContext accessibleContext;

      TableElementInfo(Element var2, AccessibleHTML.ElementInfo var3) {
         super(var2, var3);
      }

      public AccessibleHTML.ElementInfo getCaptionInfo() {
         return this.caption;
      }

      protected void validate() {
         super.validate();
         this.updateGrid();
      }

      protected void loadChildren(Element var1) {
         for(int var2 = 0; var2 < var1.getElementCount(); ++var2) {
            Element var3 = var1.getElement(var2);
            AttributeSet var4 = var3.getAttributes();
            if (var4.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TR) {
               this.addChild(new AccessibleHTML.TableElementInfo.TableRowElementInfo(var3, this, var2));
            } else if (var4.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CAPTION) {
               this.caption = AccessibleHTML.this.createElementInfo(var3, this);
            }
         }

      }

      private void updateGrid() {
         int var1 = 0;
         int var2 = 0;

         int var4;
         for(var4 = 0; var4 < this.getChildCount(); ++var4) {
            AccessibleHTML.TableElementInfo.TableRowElementInfo var5 = this.getRow(var4);
            int var6 = 0;

            for(int var7 = 0; var7 < var1; ++var7) {
               var6 = Math.max(var6, this.getRow(var4 - var7 - 1).getColumnCount(var7 + 2));
            }

            var1 = Math.max(var5.getRowCount(), var1);
            --var1;
            var2 = Math.max(var2, var5.getColumnCount() + var6);
         }

         int var3 = this.getChildCount() + var1;
         this.grid = new AccessibleHTML.TableElementInfo.TableCellElementInfo[var3][];

         for(var4 = 0; var4 < var3; ++var4) {
            this.grid[var4] = new AccessibleHTML.TableElementInfo.TableCellElementInfo[var2];
         }

         for(var4 = 0; var4 < var3; ++var4) {
            this.getRow(var4).updateGrid(var4);
         }

      }

      public AccessibleHTML.TableElementInfo.TableRowElementInfo getRow(int var1) {
         return (AccessibleHTML.TableElementInfo.TableRowElementInfo)this.getChild(var1);
      }

      public AccessibleHTML.TableElementInfo.TableCellElementInfo getCell(int var1, int var2) {
         return this.validateIfNecessary() && var1 < this.grid.length && var2 < this.grid[0].length ? this.grid[var1][var2] : null;
      }

      public int getRowExtentAt(int var1, int var2) {
         AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = this.getCell(var1, var2);
         if (var3 == null) {
            return 0;
         } else {
            int var4 = var3.getRowCount();

            int var5;
            for(var5 = 1; var1 - var5 >= 0 && this.grid[var1 - var5][var2] == var3; ++var5) {
            }

            return var4 - var5 + 1;
         }
      }

      public int getColumnExtentAt(int var1, int var2) {
         AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = this.getCell(var1, var2);
         if (var3 == null) {
            return 0;
         } else {
            int var4 = var3.getColumnCount();

            int var5;
            for(var5 = 1; var2 - var5 >= 0 && this.grid[var1][var2 - var5] == var3; ++var5) {
            }

            return var4 - var5 + 1;
         }
      }

      public int getRowCount() {
         return this.validateIfNecessary() ? this.grid.length : 0;
      }

      public int getColumnCount() {
         return this.validateIfNecessary() && this.grid.length > 0 ? this.grid[0].length : 0;
      }

      public AccessibleContext getAccessibleContext() {
         if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleHTML.TableElementInfo.TableAccessibleContext(this);
         }

         return this.accessibleContext;
      }

      private class TableCellElementInfo extends AccessibleHTML.ElementInfo {
         private Accessible accessible;
         private boolean isHeaderCell;

         TableCellElementInfo(Element var2, AccessibleHTML.ElementInfo var3) {
            super(var2, var3);
            this.isHeaderCell = false;
         }

         TableCellElementInfo(Element var2, AccessibleHTML.ElementInfo var3, boolean var4) {
            super(var2, var3);
            this.isHeaderCell = var4;
         }

         public boolean isHeaderCell() {
            return this.isHeaderCell;
         }

         public Accessible getAccessible() {
            this.accessible = null;
            this.getAccessible(this);
            return this.accessible;
         }

         private void getAccessible(AccessibleHTML.ElementInfo var1) {
            if (var1 instanceof Accessible) {
               this.accessible = (Accessible)var1;
            } else {
               for(int var2 = 0; var2 < var1.getChildCount(); ++var2) {
                  this.getAccessible(var1.getChild(var2));
               }
            }

         }

         public int getRowCount() {
            return this.validateIfNecessary() ? Math.max(1, this.getIntAttr(this.getAttributes(), HTML.Attribute.ROWSPAN, 1)) : 0;
         }

         public int getColumnCount() {
            return this.validateIfNecessary() ? Math.max(1, this.getIntAttr(this.getAttributes(), HTML.Attribute.COLSPAN, 1)) : 0;
         }

         protected void invalidate(boolean var1) {
            super.invalidate(var1);
            this.getParent().invalidate(true);
         }
      }

      private class TableRowElementInfo extends AccessibleHTML.ElementInfo {
         private AccessibleHTML.TableElementInfo parent;
         private int rowNumber;

         TableRowElementInfo(Element var2, AccessibleHTML.TableElementInfo var3, int var4) {
            super(var2, var3);
            this.parent = var3;
            this.rowNumber = var4;
         }

         protected void loadChildren(Element var1) {
            for(int var2 = 0; var2 < var1.getElementCount(); ++var2) {
               AttributeSet var3 = var1.getElement(var2).getAttributes();
               if (var3.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TH) {
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var4 = TableElementInfo.this.new TableCellElementInfo(var1.getElement(var2), this, true);
                  this.addChild(var4);
                  AccessibleTable var5 = this.parent.getAccessibleContext().getAccessibleTable();
                  AccessibleHTML.TableElementInfo.TableAccessibleContext var6 = (AccessibleHTML.TableElementInfo.TableAccessibleContext)var5;
                  var6.addRowHeader(var4, this.rowNumber);
               } else if (var3.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TD) {
                  this.addChild(TableElementInfo.this.new TableCellElementInfo(var1.getElement(var2), this, false));
               }
            }

         }

         public int getRowCount() {
            int var1 = 1;
            if (this.validateIfNecessary()) {
               for(int var2 = 0; var2 < this.getChildCount(); ++var2) {
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = (AccessibleHTML.TableElementInfo.TableCellElementInfo)this.getChild(var2);
                  if (var3.validateIfNecessary()) {
                     var1 = Math.max(var1, var3.getRowCount());
                  }
               }
            }

            return var1;
         }

         public int getColumnCount() {
            int var1 = 0;
            if (this.validateIfNecessary()) {
               for(int var2 = 0; var2 < this.getChildCount(); ++var2) {
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = (AccessibleHTML.TableElementInfo.TableCellElementInfo)this.getChild(var2);
                  if (var3.validateIfNecessary()) {
                     var1 += var3.getColumnCount();
                  }
               }
            }

            return var1;
         }

         protected void invalidate(boolean var1) {
            super.invalidate(var1);
            this.getParent().invalidate(true);
         }

         private void updateGrid(int var1) {
            if (this.validateIfNecessary()) {
               boolean var2 = false;

               int var3;
               while(!var2) {
                  for(var3 = 0; var3 < TableElementInfo.this.grid[var1].length; ++var3) {
                     if (TableElementInfo.this.grid[var1][var3] == null) {
                        var2 = true;
                        break;
                     }
                  }

                  if (!var2) {
                     ++var1;
                  }
               }

               var3 = 0;

               for(int var4 = 0; var4 < this.getChildCount(); ++var4) {
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var5;
                  for(var5 = (AccessibleHTML.TableElementInfo.TableCellElementInfo)this.getChild(var4); TableElementInfo.this.grid[var1][var3] != null; ++var3) {
                  }

                  for(int var6 = var5.getRowCount() - 1; var6 >= 0; --var6) {
                     for(int var7 = var5.getColumnCount() - 1; var7 >= 0; --var7) {
                        TableElementInfo.this.grid[var1 + var6][var3 + var7] = var5;
                     }
                  }

                  var3 += var5.getColumnCount();
               }
            }

         }

         private int getColumnCount(int var1) {
            if (this.validateIfNecessary()) {
               int var2 = 0;

               for(int var3 = 0; var3 < this.getChildCount(); ++var3) {
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var4 = (AccessibleHTML.TableElementInfo.TableCellElementInfo)this.getChild(var3);
                  if (var4.getRowCount() >= var1) {
                     var2 += var4.getColumnCount();
                  }
               }

               return var2;
            } else {
               return 0;
            }
         }
      }

      public class TableAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleTable {
         private AccessibleHTML.TableElementInfo.TableAccessibleContext.AccessibleHeadersTable rowHeadersTable;

         public TableAccessibleContext(AccessibleHTML.ElementInfo var2) {
            super(var2);
         }

         public String getAccessibleName() {
            return this.getAccessibleRole().toString();
         }

         public String getAccessibleDescription() {
            return AccessibleHTML.this.editor.getContentType();
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TABLE;
         }

         public int getAccessibleIndexInParent() {
            return this.elementInfo.getIndexInParent();
         }

         public int getAccessibleChildrenCount() {
            return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount() * ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount();
         }

         public Accessible getAccessibleChild(int var1) {
            int var2 = ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount();
            int var3 = ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount();
            int var4 = var1 / var2;
            int var5 = var1 % var3;
            return var4 >= 0 && var4 < var2 && var5 >= 0 && var5 < var3 ? this.getAccessibleAt(var4, var5) : null;
         }

         public AccessibleTable getAccessibleTable() {
            return this;
         }

         public Accessible getAccessibleCaption() {
            AccessibleHTML.ElementInfo var1 = TableElementInfo.this.getCaptionInfo();
            return var1 instanceof Accessible ? (Accessible)TableElementInfo.this.caption : null;
         }

         public void setAccessibleCaption(Accessible var1) {
         }

         public Accessible getAccessibleSummary() {
            return null;
         }

         public void setAccessibleSummary(Accessible var1) {
         }

         public int getAccessibleRowCount() {
            return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowCount();
         }

         public int getAccessibleColumnCount() {
            return ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnCount();
         }

         public Accessible getAccessibleAt(int var1, int var2) {
            AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = TableElementInfo.this.getCell(var1, var2);
            return var3 != null ? var3.getAccessible() : null;
         }

         public int getAccessibleRowExtentAt(int var1, int var2) {
            return ((AccessibleHTML.TableElementInfo)this.elementInfo).getRowExtentAt(var1, var2);
         }

         public int getAccessibleColumnExtentAt(int var1, int var2) {
            return ((AccessibleHTML.TableElementInfo)this.elementInfo).getColumnExtentAt(var1, var2);
         }

         public AccessibleTable getAccessibleRowHeader() {
            return this.rowHeadersTable;
         }

         public void setAccessibleRowHeader(AccessibleTable var1) {
         }

         public AccessibleTable getAccessibleColumnHeader() {
            return null;
         }

         public void setAccessibleColumnHeader(AccessibleTable var1) {
         }

         public Accessible getAccessibleRowDescription(int var1) {
            return null;
         }

         public void setAccessibleRowDescription(int var1, Accessible var2) {
         }

         public Accessible getAccessibleColumnDescription(int var1) {
            return null;
         }

         public void setAccessibleColumnDescription(int var1, Accessible var2) {
         }

         public boolean isAccessibleSelected(int var1, int var2) {
            if (TableElementInfo.this.validateIfNecessary()) {
               if (var1 < 0 || var1 >= this.getAccessibleRowCount() || var2 < 0 || var2 >= this.getAccessibleColumnCount()) {
                  return false;
               }

               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = TableElementInfo.this.getCell(var1, var2);
               if (var3 != null) {
                  Element var4 = var3.getElement();
                  int var5 = var4.getStartOffset();
                  int var6 = var4.getEndOffset();
                  return var5 >= AccessibleHTML.this.editor.getSelectionStart() && var6 <= AccessibleHTML.this.editor.getSelectionEnd();
               }
            }

            return false;
         }

         public boolean isAccessibleRowSelected(int var1) {
            if (!TableElementInfo.this.validateIfNecessary()) {
               return false;
            } else if (var1 >= 0 && var1 < this.getAccessibleRowCount()) {
               int var2 = this.getAccessibleColumnCount();
               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = TableElementInfo.this.getCell(var1, 0);
               if (var3 == null) {
                  return false;
               } else {
                  int var4 = var3.getElement().getStartOffset();
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var5 = TableElementInfo.this.getCell(var1, var2 - 1);
                  if (var5 == null) {
                     return false;
                  } else {
                     int var6 = var5.getElement().getEndOffset();
                     return var4 >= AccessibleHTML.this.editor.getSelectionStart() && var6 <= AccessibleHTML.this.editor.getSelectionEnd();
                  }
               }
            } else {
               return false;
            }
         }

         public boolean isAccessibleColumnSelected(int var1) {
            if (!TableElementInfo.this.validateIfNecessary()) {
               return false;
            } else if (var1 >= 0 && var1 < this.getAccessibleColumnCount()) {
               int var2 = this.getAccessibleRowCount();
               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = TableElementInfo.this.getCell(0, var1);
               if (var3 == null) {
                  return false;
               } else {
                  int var4 = var3.getElement().getStartOffset();
                  AccessibleHTML.TableElementInfo.TableCellElementInfo var5 = TableElementInfo.this.getCell(var2 - 1, var1);
                  if (var5 == null) {
                     return false;
                  } else {
                     int var6 = var5.getElement().getEndOffset();
                     return var4 >= AccessibleHTML.this.editor.getSelectionStart() && var6 <= AccessibleHTML.this.editor.getSelectionEnd();
                  }
               }
            } else {
               return false;
            }
         }

         public int[] getSelectedAccessibleRows() {
            if (!TableElementInfo.this.validateIfNecessary()) {
               return new int[0];
            } else {
               int var1 = this.getAccessibleRowCount();
               Vector var2 = new Vector();

               for(int var3 = 0; var3 < var1; ++var3) {
                  if (this.isAccessibleRowSelected(var3)) {
                     var2.addElement(var3);
                  }
               }

               int[] var5 = new int[var2.size()];

               for(int var4 = 0; var4 < var5.length; ++var4) {
                  var5[var4] = (Integer)var2.elementAt(var4);
               }

               return var5;
            }
         }

         public int[] getSelectedAccessibleColumns() {
            if (!TableElementInfo.this.validateIfNecessary()) {
               return new int[0];
            } else {
               int var1 = this.getAccessibleRowCount();
               Vector var2 = new Vector();

               for(int var3 = 0; var3 < var1; ++var3) {
                  if (this.isAccessibleColumnSelected(var3)) {
                     var2.addElement(var3);
                  }
               }

               int[] var5 = new int[var2.size()];

               for(int var4 = 0; var4 < var5.length; ++var4) {
                  var5[var4] = (Integer)var2.elementAt(var4);
               }

               return var5;
            }
         }

         public int getAccessibleRow(int var1) {
            if (TableElementInfo.this.validateIfNecessary()) {
               int var2 = this.getAccessibleColumnCount() * this.getAccessibleRowCount();
               return var1 >= var2 ? -1 : var1 / this.getAccessibleColumnCount();
            } else {
               return -1;
            }
         }

         public int getAccessibleColumn(int var1) {
            if (TableElementInfo.this.validateIfNecessary()) {
               int var2 = this.getAccessibleColumnCount() * this.getAccessibleRowCount();
               return var1 >= var2 ? -1 : var1 % this.getAccessibleColumnCount();
            } else {
               return -1;
            }
         }

         public int getAccessibleIndex(int var1, int var2) {
            if (TableElementInfo.this.validateIfNecessary()) {
               return var1 < this.getAccessibleRowCount() && var2 < this.getAccessibleColumnCount() ? var1 * this.getAccessibleColumnCount() + var2 : -1;
            } else {
               return -1;
            }
         }

         public String getAccessibleRowHeader(int var1) {
            if (TableElementInfo.this.validateIfNecessary()) {
               AccessibleHTML.TableElementInfo.TableCellElementInfo var2 = TableElementInfo.this.getCell(var1, 0);
               if (var2.isHeaderCell()) {
                  View var3 = var2.getView();
                  if (var3 != null && AccessibleHTML.this.model != null) {
                     try {
                        return AccessibleHTML.this.model.getText(var3.getStartOffset(), var3.getEndOffset() - var3.getStartOffset());
                     } catch (BadLocationException var5) {
                        return null;
                     }
                  }
               }
            }

            return null;
         }

         public String getAccessibleColumnHeader(int var1) {
            if (TableElementInfo.this.validateIfNecessary()) {
               AccessibleHTML.TableElementInfo.TableCellElementInfo var2 = TableElementInfo.this.getCell(0, var1);
               if (var2.isHeaderCell()) {
                  View var3 = var2.getView();
                  if (var3 != null && AccessibleHTML.this.model != null) {
                     try {
                        return AccessibleHTML.this.model.getText(var3.getStartOffset(), var3.getEndOffset() - var3.getStartOffset());
                     } catch (BadLocationException var5) {
                        return null;
                     }
                  }
               }
            }

            return null;
         }

         public void addRowHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo var1, int var2) {
            if (this.rowHeadersTable == null) {
               this.rowHeadersTable = new AccessibleHTML.TableElementInfo.TableAccessibleContext.AccessibleHeadersTable();
            }

            this.rowHeadersTable.addHeader(var1, var2);
         }

         protected class AccessibleHeadersTable implements AccessibleTable {
            private Hashtable<Integer, ArrayList<AccessibleHTML.TableElementInfo.TableCellElementInfo>> headers = new Hashtable();
            private int rowCount = 0;
            private int columnCount = 0;

            public void addHeader(AccessibleHTML.TableElementInfo.TableCellElementInfo var1, int var2) {
               Integer var3 = var2;
               ArrayList var4 = (ArrayList)this.headers.get(var3);
               if (var4 == null) {
                  var4 = new ArrayList();
                  this.headers.put(var3, var4);
               }

               var4.add(var1);
            }

            public Accessible getAccessibleCaption() {
               return null;
            }

            public void setAccessibleCaption(Accessible var1) {
            }

            public Accessible getAccessibleSummary() {
               return null;
            }

            public void setAccessibleSummary(Accessible var1) {
            }

            public int getAccessibleRowCount() {
               return this.rowCount;
            }

            public int getAccessibleColumnCount() {
               return this.columnCount;
            }

            private AccessibleHTML.TableElementInfo.TableCellElementInfo getElementInfoAt(int var1, int var2) {
               ArrayList var3 = (ArrayList)this.headers.get(var1);
               return var3 != null ? (AccessibleHTML.TableElementInfo.TableCellElementInfo)var3.get(var2) : null;
            }

            public Accessible getAccessibleAt(int var1, int var2) {
               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = this.getElementInfoAt(var1, var2);
               return var3 instanceof Accessible ? (Accessible)var3 : null;
            }

            public int getAccessibleRowExtentAt(int var1, int var2) {
               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = this.getElementInfoAt(var1, var2);
               return var3 != null ? var3.getRowCount() : 0;
            }

            public int getAccessibleColumnExtentAt(int var1, int var2) {
               AccessibleHTML.TableElementInfo.TableCellElementInfo var3 = this.getElementInfoAt(var1, var2);
               return var3 != null ? var3.getRowCount() : 0;
            }

            public AccessibleTable getAccessibleRowHeader() {
               return null;
            }

            public void setAccessibleRowHeader(AccessibleTable var1) {
            }

            public AccessibleTable getAccessibleColumnHeader() {
               return null;
            }

            public void setAccessibleColumnHeader(AccessibleTable var1) {
            }

            public Accessible getAccessibleRowDescription(int var1) {
               return null;
            }

            public void setAccessibleRowDescription(int var1, Accessible var2) {
            }

            public Accessible getAccessibleColumnDescription(int var1) {
               return null;
            }

            public void setAccessibleColumnDescription(int var1, Accessible var2) {
            }

            public boolean isAccessibleSelected(int var1, int var2) {
               return false;
            }

            public boolean isAccessibleRowSelected(int var1) {
               return false;
            }

            public boolean isAccessibleColumnSelected(int var1) {
               return false;
            }

            public int[] getSelectedAccessibleRows() {
               return new int[0];
            }

            public int[] getSelectedAccessibleColumns() {
               return new int[0];
            }
         }
      }
   }

   private class IconElementInfo extends AccessibleHTML.ElementInfo implements Accessible {
      private int width = -1;
      private int height = -1;
      private AccessibleContext accessibleContext;

      IconElementInfo(Element var2, AccessibleHTML.ElementInfo var3) {
         super(var2, var3);
      }

      protected void invalidate(boolean var1) {
         super.invalidate(var1);
         this.width = this.height = -1;
      }

      private int getImageSize(Object var1) {
         if (this.validateIfNecessary()) {
            int var2 = this.getIntAttr(this.getAttributes(), var1, -1);
            if (var2 == -1) {
               View var3 = this.getView();
               var2 = 0;
               if (var3 instanceof ImageView) {
                  Image var4 = ((ImageView)var3).getImage();
                  if (var4 != null) {
                     if (var1 == HTML.Attribute.WIDTH) {
                        var2 = var4.getWidth((ImageObserver)null);
                     } else {
                        var2 = var4.getHeight((ImageObserver)null);
                     }
                  }
               }
            }

            return var2;
         } else {
            return 0;
         }
      }

      public AccessibleContext getAccessibleContext() {
         if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleHTML.IconElementInfo.IconAccessibleContext(this);
         }

         return this.accessibleContext;
      }

      protected class IconAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleIcon {
         public IconAccessibleContext(AccessibleHTML.ElementInfo var2) {
            super(var2);
         }

         public String getAccessibleName() {
            return this.getAccessibleIconDescription();
         }

         public String getAccessibleDescription() {
            return AccessibleHTML.this.editor.getContentType();
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.ICON;
         }

         public AccessibleIcon[] getAccessibleIcon() {
            AccessibleIcon[] var1 = new AccessibleIcon[]{this};
            return var1;
         }

         public String getAccessibleIconDescription() {
            return ((ImageView)IconElementInfo.this.getView()).getAltText();
         }

         public void setAccessibleIconDescription(String var1) {
         }

         public int getAccessibleIconWidth() {
            if (IconElementInfo.this.width == -1) {
               IconElementInfo.this.width = IconElementInfo.this.getImageSize(HTML.Attribute.WIDTH);
            }

            return IconElementInfo.this.width;
         }

         public int getAccessibleIconHeight() {
            if (IconElementInfo.this.height == -1) {
               IconElementInfo.this.height = IconElementInfo.this.getImageSize(HTML.Attribute.HEIGHT);
            }

            return IconElementInfo.this.height;
         }
      }
   }

   class TextElementInfo extends AccessibleHTML.ElementInfo implements Accessible {
      private AccessibleContext accessibleContext;

      TextElementInfo(Element var2, AccessibleHTML.ElementInfo var3) {
         super(var2, var3);
      }

      public AccessibleContext getAccessibleContext() {
         if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleHTML.TextElementInfo.TextAccessibleContext(this);
         }

         return this.accessibleContext;
      }

      public class TextAccessibleContext extends AccessibleHTML.HTMLAccessibleContext implements AccessibleText {
         public TextAccessibleContext(AccessibleHTML.ElementInfo var2) {
            super(var2);
         }

         public AccessibleText getAccessibleText() {
            return this;
         }

         public String getAccessibleName() {
            return AccessibleHTML.this.model != null ? (String)AccessibleHTML.this.model.getProperty("title") : null;
         }

         public String getAccessibleDescription() {
            return AccessibleHTML.this.editor.getContentType();
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TEXT;
         }

         public int getIndexAtPoint(Point var1) {
            View var2 = TextElementInfo.this.getView();
            return var2 != null ? var2.viewToModel((float)var1.x, (float)var1.y, this.getBounds()) : -1;
         }

         public Rectangle getCharacterBounds(int var1) {
            try {
               return AccessibleHTML.this.editor.getUI().modelToView(AccessibleHTML.this.editor, var1);
            } catch (BadLocationException var3) {
               return null;
            }
         }

         public int getCharCount() {
            if (TextElementInfo.this.validateIfNecessary()) {
               Element var1 = this.elementInfo.getElement();
               return var1.getEndOffset() - var1.getStartOffset();
            } else {
               return 0;
            }
         }

         public int getCaretPosition() {
            View var1 = TextElementInfo.this.getView();
            if (var1 == null) {
               return -1;
            } else {
               Container var2 = var1.getContainer();
               if (var2 == null) {
                  return -1;
               } else {
                  return var2 instanceof JTextComponent ? ((JTextComponent)var2).getCaretPosition() : -1;
               }
            }
         }

         public String getAtIndex(int var1, int var2) {
            return this.getAtIndex(var1, var2, 0);
         }

         public String getAfterIndex(int var1, int var2) {
            return this.getAtIndex(var1, var2, 1);
         }

         public String getBeforeIndex(int var1, int var2) {
            return this.getAtIndex(var1, var2, -1);
         }

         private String getAtIndex(int var1, int var2, int var3) {
            if (AccessibleHTML.this.model instanceof AbstractDocument) {
               ((AbstractDocument)AccessibleHTML.this.model).readLock();
            }

            try {
               try {
                  AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment var4;
                  if (var2 < 0 || var2 >= AccessibleHTML.this.model.getLength()) {
                     var4 = null;
                     return var4;
                  }

                  switch(var1) {
                  case 1:
                     if (var2 + var3 < AccessibleHTML.this.model.getLength() && var2 + var3 >= 0) {
                        String var11 = AccessibleHTML.this.model.getText(var2 + var3, 1);
                        return var11;
                     }

                     return null;
                  case 2:
                  case 3:
                     var4 = this.getSegmentAt(var1, var2);
                     if (var4 != null) {
                        if (var3 != 0) {
                           int var5;
                           if (var3 < 0) {
                              var5 = var4.modelOffset - 1;
                           } else {
                              var5 = var4.modelOffset + var3 * var4.count;
                           }

                           if (var5 >= 0 && var5 <= AccessibleHTML.this.model.getLength()) {
                              var4 = this.getSegmentAt(var1, var5);
                           } else {
                              var4 = null;
                           }
                        }

                        if (var4 != null) {
                           String var12 = new String(var4.array, var4.offset, var4.count);
                           return var12;
                        }
                     }
                  }
               } catch (BadLocationException var9) {
               }

               return null;
            } finally {
               if (AccessibleHTML.this.model instanceof AbstractDocument) {
                  ((AbstractDocument)AccessibleHTML.this.model).readUnlock();
               }

            }
         }

         private Element getParagraphElement(int var1) {
            if (AccessibleHTML.this.model instanceof PlainDocument) {
               PlainDocument var5 = (PlainDocument)AccessibleHTML.this.model;
               return var5.getParagraphElement(var1);
            } else if (AccessibleHTML.this.model instanceof StyledDocument) {
               StyledDocument var4 = (StyledDocument)AccessibleHTML.this.model;
               return var4.getParagraphElement(var1);
            } else {
               Element var2;
               int var3;
               for(var2 = AccessibleHTML.this.model.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var3)) {
                  var3 = var2.getElementIndex(var1);
               }

               return var2 == null ? null : var2.getParentElement();
            }
         }

         private AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment getParagraphElementText(int var1) throws BadLocationException {
            Element var2 = this.getParagraphElement(var1);
            if (var2 != null) {
               AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment var3 = new AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment();

               try {
                  int var4 = var2.getEndOffset() - var2.getStartOffset();
                  AccessibleHTML.this.model.getText(var2.getStartOffset(), var4, var3);
               } catch (BadLocationException var5) {
                  return null;
               }

               var3.modelOffset = var2.getStartOffset();
               return var3;
            } else {
               return null;
            }
         }

         private AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment getSegmentAt(int var1, int var2) throws BadLocationException {
            AccessibleHTML.TextElementInfo.TextAccessibleContext.IndexedSegment var3 = this.getParagraphElementText(var2);
            if (var3 == null) {
               return null;
            } else {
               BreakIterator var4;
               switch(var1) {
               case 2:
                  var4 = BreakIterator.getWordInstance(this.getLocale());
                  break;
               case 3:
                  var4 = BreakIterator.getSentenceInstance(this.getLocale());
                  break;
               default:
                  return null;
               }

               var3.first();
               var4.setText((CharacterIterator)var3);
               int var5 = var4.following(var2 - var3.modelOffset + var3.offset);
               if (var5 == -1) {
                  return null;
               } else if (var5 > var3.offset + var3.count) {
                  return null;
               } else {
                  int var6 = var4.previous();
                  if (var6 != -1 && var6 < var3.offset + var3.count) {
                     var3.modelOffset = var3.modelOffset + var6 - var3.offset;
                     var3.offset = var6;
                     var3.count = var5 - var6;
                     return var3;
                  } else {
                     return null;
                  }
               }
            }
         }

         public AttributeSet getCharacterAttribute(int var1) {
            if (AccessibleHTML.this.model instanceof StyledDocument) {
               StyledDocument var2 = (StyledDocument)AccessibleHTML.this.model;
               Element var3 = var2.getCharacterElement(var1);
               if (var3 != null) {
                  return var3.getAttributes();
               }
            }

            return null;
         }

         public int getSelectionStart() {
            return AccessibleHTML.this.editor.getSelectionStart();
         }

         public int getSelectionEnd() {
            return AccessibleHTML.this.editor.getSelectionEnd();
         }

         public String getSelectedText() {
            return AccessibleHTML.this.editor.getSelectedText();
         }

         private String getText(int var1, int var2) throws BadLocationException {
            if (AccessibleHTML.this.model != null && AccessibleHTML.this.model instanceof StyledDocument) {
               StyledDocument var3 = (StyledDocument)AccessibleHTML.this.model;
               return AccessibleHTML.this.model.getText(var1, var2);
            } else {
               return null;
            }
         }

         private class IndexedSegment extends Segment {
            public int modelOffset;

            private IndexedSegment() {
            }

            // $FF: synthetic method
            IndexedSegment(Object var2) {
               this();
            }
         }
      }
   }

   protected abstract class HTMLAccessibleContext extends AccessibleContext implements Accessible, AccessibleComponent {
      protected AccessibleHTML.ElementInfo elementInfo;

      public HTMLAccessibleContext(AccessibleHTML.ElementInfo var2) {
         this.elementInfo = var2;
      }

      public AccessibleContext getAccessibleContext() {
         return this;
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = new AccessibleStateSet();
         JEditorPane var2 = AccessibleHTML.this.getTextComponent();
         if (var2.isEnabled()) {
            var1.add(AccessibleState.ENABLED);
         }

         if (var2 instanceof JTextComponent && ((JTextComponent)var2).isEditable()) {
            var1.add(AccessibleState.EDITABLE);
            var1.add(AccessibleState.FOCUSABLE);
         }

         if (var2.isVisible()) {
            var1.add(AccessibleState.VISIBLE);
         }

         if (var2.isShowing()) {
            var1.add(AccessibleState.SHOWING);
         }

         return var1;
      }

      public int getAccessibleIndexInParent() {
         return this.elementInfo.getIndexInParent();
      }

      public int getAccessibleChildrenCount() {
         return this.elementInfo.getChildCount();
      }

      public Accessible getAccessibleChild(int var1) {
         AccessibleHTML.ElementInfo var2 = this.elementInfo.getChild(var1);
         return var2 != null && var2 instanceof Accessible ? (Accessible)var2 : null;
      }

      public Locale getLocale() throws IllegalComponentStateException {
         return AccessibleHTML.this.editor.getLocale();
      }

      public AccessibleComponent getAccessibleComponent() {
         return this;
      }

      public Color getBackground() {
         return AccessibleHTML.this.getTextComponent().getBackground();
      }

      public void setBackground(Color var1) {
         AccessibleHTML.this.getTextComponent().setBackground(var1);
      }

      public Color getForeground() {
         return AccessibleHTML.this.getTextComponent().getForeground();
      }

      public void setForeground(Color var1) {
         AccessibleHTML.this.getTextComponent().setForeground(var1);
      }

      public Cursor getCursor() {
         return AccessibleHTML.this.getTextComponent().getCursor();
      }

      public void setCursor(Cursor var1) {
         AccessibleHTML.this.getTextComponent().setCursor(var1);
      }

      public Font getFont() {
         return AccessibleHTML.this.getTextComponent().getFont();
      }

      public void setFont(Font var1) {
         AccessibleHTML.this.getTextComponent().setFont(var1);
      }

      public FontMetrics getFontMetrics(Font var1) {
         return AccessibleHTML.this.getTextComponent().getFontMetrics(var1);
      }

      public boolean isEnabled() {
         return AccessibleHTML.this.getTextComponent().isEnabled();
      }

      public void setEnabled(boolean var1) {
         AccessibleHTML.this.getTextComponent().setEnabled(var1);
      }

      public boolean isVisible() {
         return AccessibleHTML.this.getTextComponent().isVisible();
      }

      public void setVisible(boolean var1) {
         AccessibleHTML.this.getTextComponent().setVisible(var1);
      }

      public boolean isShowing() {
         return AccessibleHTML.this.getTextComponent().isShowing();
      }

      public boolean contains(Point var1) {
         Rectangle var2 = this.getBounds();
         return var2 != null ? var2.contains(var1.x, var1.y) : false;
      }

      public Point getLocationOnScreen() {
         Point var1 = AccessibleHTML.this.getTextComponent().getLocationOnScreen();
         Rectangle var2 = this.getBounds();
         return var2 != null ? new Point(var1.x + var2.x, var1.y + var2.y) : null;
      }

      public Point getLocation() {
         Rectangle var1 = this.getBounds();
         return var1 != null ? new Point(var1.x, var1.y) : null;
      }

      public void setLocation(Point var1) {
      }

      public Rectangle getBounds() {
         return this.elementInfo.getBounds();
      }

      public void setBounds(Rectangle var1) {
      }

      public Dimension getSize() {
         Rectangle var1 = this.getBounds();
         return var1 != null ? new Dimension(var1.width, var1.height) : null;
      }

      public void setSize(Dimension var1) {
         JEditorPane var2 = AccessibleHTML.this.getTextComponent();
         var2.setSize(var1);
      }

      public Accessible getAccessibleAt(Point var1) {
         AccessibleHTML.ElementInfo var2 = this.getElementInfoAt(AccessibleHTML.this.rootElementInfo, var1);
         return var2 instanceof Accessible ? (Accessible)var2 : null;
      }

      private AccessibleHTML.ElementInfo getElementInfoAt(AccessibleHTML.ElementInfo var1, Point var2) {
         if (var1.getBounds() == null) {
            return null;
         } else if (var1.getChildCount() == 0 && var1.getBounds().contains(var2)) {
            return var1;
         } else {
            if (var1 instanceof AccessibleHTML.TableElementInfo) {
               AccessibleHTML.ElementInfo var3 = ((AccessibleHTML.TableElementInfo)var1).getCaptionInfo();
               if (var3 != null) {
                  Rectangle var4 = var3.getBounds();
                  if (var4 != null && var4.contains(var2)) {
                     return var3;
                  }
               }
            }

            for(int var6 = 0; var6 < var1.getChildCount(); ++var6) {
               AccessibleHTML.ElementInfo var7 = var1.getChild(var6);
               AccessibleHTML.ElementInfo var5 = this.getElementInfoAt(var7, var2);
               if (var5 != null) {
                  return var5;
               }
            }

            return null;
         }
      }

      public boolean isFocusTraversable() {
         JEditorPane var1 = AccessibleHTML.this.getTextComponent();
         return var1 instanceof JTextComponent && ((JTextComponent)var1).isEditable();
      }

      public void requestFocus() {
         if (this.isFocusTraversable()) {
            JEditorPane var1 = AccessibleHTML.this.getTextComponent();
            if (var1 instanceof JTextComponent) {
               var1.requestFocusInWindow();

               try {
                  if (this.elementInfo.validateIfNecessary()) {
                     Element var2 = this.elementInfo.getElement();
                     ((JTextComponent)var1).setCaretPosition(var2.getStartOffset());
                     AccessibleContext var3 = AccessibleHTML.this.editor.getAccessibleContext();
                     PropertyChangeEvent var4 = new PropertyChangeEvent(this, "AccessibleState", (Object)null, AccessibleState.FOCUSED);
                     var3.firePropertyChange("AccessibleState", (Object)null, var4);
                  }
               } catch (IllegalArgumentException var5) {
               }
            }

         }
      }

      public void addFocusListener(FocusListener var1) {
         AccessibleHTML.this.getTextComponent().addFocusListener(var1);
      }

      public void removeFocusListener(FocusListener var1) {
         AccessibleHTML.this.getTextComponent().removeFocusListener(var1);
      }
   }

   private class RootHTMLAccessibleContext extends AccessibleHTML.HTMLAccessibleContext {
      public RootHTMLAccessibleContext(AccessibleHTML.ElementInfo var2) {
         super(var2);
      }

      public String getAccessibleName() {
         return AccessibleHTML.this.model != null ? (String)AccessibleHTML.this.model.getProperty("title") : null;
      }

      public String getAccessibleDescription() {
         return AccessibleHTML.this.editor.getContentType();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TEXT;
      }
   }
}
