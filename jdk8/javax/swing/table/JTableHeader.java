package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.TableHeaderUI;
import sun.awt.AWTAccessor;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class JTableHeader extends JComponent implements TableColumnModelListener, Accessible {
   private static final String uiClassID = "TableHeaderUI";
   protected JTable table;
   protected TableColumnModel columnModel;
   protected boolean reorderingAllowed;
   protected boolean resizingAllowed;
   protected boolean updateTableInRealTime;
   protected transient TableColumn resizingColumn;
   protected transient TableColumn draggedColumn;
   protected transient int draggedDistance;
   private TableCellRenderer defaultRenderer;

   public JTableHeader() {
      this((TableColumnModel)null);
   }

   public JTableHeader(TableColumnModel var1) {
      if (var1 == null) {
         var1 = this.createDefaultColumnModel();
      }

      this.setColumnModel(var1);
      this.initializeLocalVars();
      this.updateUI();
   }

   public void setTable(JTable var1) {
      JTable var2 = this.table;
      this.table = var1;
      this.firePropertyChange("table", var2, var1);
   }

   public JTable getTable() {
      return this.table;
   }

   public void setReorderingAllowed(boolean var1) {
      boolean var2 = this.reorderingAllowed;
      this.reorderingAllowed = var1;
      this.firePropertyChange("reorderingAllowed", var2, var1);
   }

   public boolean getReorderingAllowed() {
      return this.reorderingAllowed;
   }

   public void setResizingAllowed(boolean var1) {
      boolean var2 = this.resizingAllowed;
      this.resizingAllowed = var1;
      this.firePropertyChange("resizingAllowed", var2, var1);
   }

   public boolean getResizingAllowed() {
      return this.resizingAllowed;
   }

   public TableColumn getDraggedColumn() {
      return this.draggedColumn;
   }

   public int getDraggedDistance() {
      return this.draggedDistance;
   }

   public TableColumn getResizingColumn() {
      return this.resizingColumn;
   }

   public void setUpdateTableInRealTime(boolean var1) {
      this.updateTableInRealTime = var1;
   }

   public boolean getUpdateTableInRealTime() {
      return this.updateTableInRealTime;
   }

   public void setDefaultRenderer(TableCellRenderer var1) {
      this.defaultRenderer = var1;
   }

   @Transient
   public TableCellRenderer getDefaultRenderer() {
      return this.defaultRenderer;
   }

   public int columnAtPoint(Point var1) {
      int var2 = var1.x;
      if (!this.getComponentOrientation().isLeftToRight()) {
         var2 = this.getWidthInRightToLeft() - var2 - 1;
      }

      return this.getColumnModel().getColumnIndexAtX(var2);
   }

   public Rectangle getHeaderRect(int var1) {
      Rectangle var2 = new Rectangle();
      TableColumnModel var3 = this.getColumnModel();
      var2.height = this.getHeight();
      if (var1 < 0) {
         if (!this.getComponentOrientation().isLeftToRight()) {
            var2.x = this.getWidthInRightToLeft();
         }
      } else if (var1 >= var3.getColumnCount()) {
         if (this.getComponentOrientation().isLeftToRight()) {
            var2.x = this.getWidth();
         }
      } else {
         for(int var4 = 0; var4 < var1; ++var4) {
            var2.x += var3.getColumn(var4).getWidth();
         }

         if (!this.getComponentOrientation().isLeftToRight()) {
            var2.x = this.getWidthInRightToLeft() - var2.x - var3.getColumn(var1).getWidth();
         }

         var2.width = var3.getColumn(var1).getWidth();
      }

      return var2;
   }

   public String getToolTipText(MouseEvent var1) {
      String var2 = null;
      Point var3 = var1.getPoint();
      int var4;
      if ((var4 = this.columnAtPoint(var3)) != -1) {
         TableColumn var5 = this.columnModel.getColumn(var4);
         TableCellRenderer var6 = var5.getHeaderRenderer();
         if (var6 == null) {
            var6 = this.defaultRenderer;
         }

         Component var7 = var6.getTableCellRendererComponent(this.getTable(), var5.getHeaderValue(), false, false, -1, var4);
         if (var7 instanceof JComponent) {
            Rectangle var9 = this.getHeaderRect(var4);
            var3.translate(-var9.x, -var9.y);
            MouseEvent var8 = new MouseEvent(var7, var1.getID(), var1.getWhen(), var1.getModifiers(), var3.x, var3.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
            AWTAccessor.MouseEventAccessor var10 = AWTAccessor.getMouseEventAccessor();
            var10.setCausedByTouchEvent(var8, var10.isCausedByTouchEvent(var1));
            var2 = ((JComponent)var7).getToolTipText(var8);
         }
      }

      if (var2 == null) {
         var2 = this.getToolTipText();
      }

      return var2;
   }

   public TableHeaderUI getUI() {
      return (TableHeaderUI)this.ui;
   }

   public void setUI(TableHeaderUI var1) {
      if (this.ui != var1) {
         super.setUI(var1);
         this.repaint();
      }

   }

   public void updateUI() {
      this.setUI((TableHeaderUI)UIManager.getUI(this));
      TableCellRenderer var1 = this.getDefaultRenderer();
      if (var1 instanceof Component) {
         SwingUtilities.updateComponentTreeUI((Component)var1);
      }

   }

   public String getUIClassID() {
      return "TableHeaderUI";
   }

   public void setColumnModel(TableColumnModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Cannot set a null ColumnModel");
      } else {
         TableColumnModel var2 = this.columnModel;
         if (var1 != var2) {
            if (var2 != null) {
               var2.removeColumnModelListener(this);
            }

            this.columnModel = var1;
            var1.addColumnModelListener(this);
            this.firePropertyChange("columnModel", var2, var1);
            this.resizeAndRepaint();
         }

      }
   }

   public TableColumnModel getColumnModel() {
      return this.columnModel;
   }

   public void columnAdded(TableColumnModelEvent var1) {
      this.resizeAndRepaint();
   }

   public void columnRemoved(TableColumnModelEvent var1) {
      this.resizeAndRepaint();
   }

   public void columnMoved(TableColumnModelEvent var1) {
      this.repaint();
   }

   public void columnMarginChanged(ChangeEvent var1) {
      this.resizeAndRepaint();
   }

   public void columnSelectionChanged(ListSelectionEvent var1) {
   }

   protected TableColumnModel createDefaultColumnModel() {
      return new DefaultTableColumnModel();
   }

   protected TableCellRenderer createDefaultRenderer() {
      return new DefaultTableCellHeaderRenderer();
   }

   protected void initializeLocalVars() {
      this.setOpaque(true);
      this.table = null;
      this.reorderingAllowed = true;
      this.resizingAllowed = true;
      this.draggedColumn = null;
      this.draggedDistance = 0;
      this.resizingColumn = null;
      this.updateTableInRealTime = true;
      ToolTipManager var1 = ToolTipManager.sharedInstance();
      var1.registerComponent(this);
      this.setDefaultRenderer(this.createDefaultRenderer());
   }

   public void resizeAndRepaint() {
      this.revalidate();
      this.repaint();
   }

   public void setDraggedColumn(TableColumn var1) {
      this.draggedColumn = var1;
   }

   public void setDraggedDistance(int var1) {
      this.draggedDistance = var1;
   }

   public void setResizingColumn(TableColumn var1) {
      this.resizingColumn = var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.ui != null && this.getUIClassID().equals("TableHeaderUI")) {
         this.ui.installUI(this);
      }

   }

   private int getWidthInRightToLeft() {
      return this.table != null && this.table.getAutoResizeMode() != 0 ? this.table.getWidth() : super.getWidth();
   }

   protected String paramString() {
      String var1 = this.reorderingAllowed ? "true" : "false";
      String var2 = this.resizingAllowed ? "true" : "false";
      String var3 = this.updateTableInRealTime ? "true" : "false";
      return super.paramString() + ",draggedDistance=" + this.draggedDistance + ",reorderingAllowed=" + var1 + ",resizingAllowed=" + var2 + ",updateTableInRealTime=" + var3;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JTableHeader.AccessibleJTableHeader();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJTableHeader extends JComponent.AccessibleJComponent {
      protected AccessibleJTableHeader() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PANEL;
      }

      public Accessible getAccessibleAt(Point var1) {
         int var2;
         if ((var2 = JTableHeader.this.columnAtPoint(var1)) != -1) {
            TableColumn var3 = JTableHeader.this.columnModel.getColumn(var2);
            TableCellRenderer var4 = var3.getHeaderRenderer();
            if (var4 == null) {
               if (JTableHeader.this.defaultRenderer == null) {
                  return null;
               }

               var4 = JTableHeader.this.defaultRenderer;
            }

            var4.getTableCellRendererComponent(JTableHeader.this.getTable(), var3.getHeaderValue(), false, false, -1, var2);
            return new JTableHeader.AccessibleJTableHeader.AccessibleJTableHeaderEntry(var2, JTableHeader.this, JTableHeader.this.table);
         } else {
            return null;
         }
      }

      public int getAccessibleChildrenCount() {
         return JTableHeader.this.columnModel.getColumnCount();
      }

      public Accessible getAccessibleChild(int var1) {
         if (var1 >= 0 && var1 < this.getAccessibleChildrenCount()) {
            TableColumn var2 = JTableHeader.this.columnModel.getColumn(var1);
            TableCellRenderer var3 = var2.getHeaderRenderer();
            if (var3 == null) {
               if (JTableHeader.this.defaultRenderer == null) {
                  return null;
               }

               var3 = JTableHeader.this.defaultRenderer;
            }

            var3.getTableCellRendererComponent(JTableHeader.this.getTable(), var2.getHeaderValue(), false, false, -1, var1);
            return new JTableHeader.AccessibleJTableHeader.AccessibleJTableHeaderEntry(var1, JTableHeader.this, JTableHeader.this.table);
         } else {
            return null;
         }
      }

      protected class AccessibleJTableHeaderEntry extends AccessibleContext implements Accessible, AccessibleComponent {
         private JTableHeader parent;
         private int column;
         private JTable table;

         public AccessibleJTableHeaderEntry(int var2, JTableHeader var3, JTable var4) {
            this.parent = var3;
            this.column = var2;
            this.table = var4;
            this.setAccessibleParent(this.parent);
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         private AccessibleContext getCurrentAccessibleContext() {
            TableColumnModel var1 = this.table.getColumnModel();
            if (var1 != null) {
               if (this.column < 0 || this.column >= var1.getColumnCount()) {
                  return null;
               }

               TableColumn var2 = var1.getColumn(this.column);
               TableCellRenderer var3 = var2.getHeaderRenderer();
               if (var3 == null) {
                  if (JTableHeader.this.defaultRenderer == null) {
                     return null;
                  }

                  var3 = JTableHeader.this.defaultRenderer;
               }

               Component var4 = var3.getTableCellRendererComponent(JTableHeader.this.getTable(), var2.getHeaderValue(), false, false, -1, this.column);
               if (var4 instanceof Accessible) {
                  return ((Accessible)var4).getAccessibleContext();
               }
            }

            return null;
         }

         private Component getCurrentComponent() {
            TableColumnModel var1 = this.table.getColumnModel();
            if (var1 != null) {
               if (this.column >= 0 && this.column < var1.getColumnCount()) {
                  TableColumn var2 = var1.getColumn(this.column);
                  TableCellRenderer var3 = var2.getHeaderRenderer();
                  if (var3 == null) {
                     if (JTableHeader.this.defaultRenderer == null) {
                        return null;
                     }

                     var3 = JTableHeader.this.defaultRenderer;
                  }

                  return var3.getTableCellRendererComponent(JTableHeader.this.getTable(), var2.getHeaderValue(), false, false, -1, this.column);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }

         public String getAccessibleName() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            String var2;
            if (var1 != null) {
               var2 = var1.getAccessibleName();
               if (var2 != null && var2 != "") {
                  return var2;
               }
            }

            if (this.accessibleName != null && this.accessibleName != "") {
               return this.accessibleName;
            } else {
               var2 = (String)JTableHeader.this.getClientProperty("AccessibleName");
               return var2 != null ? var2 : this.table.getColumnName(this.column);
            }
         }

         public void setAccessibleName(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleName(var1);
            } else {
               super.setAccessibleName(var1);
            }

         }

         public String getAccessibleDescription() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleDescription() : super.getAccessibleDescription();
         }

         public void setAccessibleDescription(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleDescription(var1);
            } else {
               super.setAccessibleDescription(var1);
            }

         }

         public AccessibleRole getAccessibleRole() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleRole() : AccessibleRole.COLUMN_HEADER;
         }

         public AccessibleStateSet getAccessibleStateSet() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 != null) {
               AccessibleStateSet var2 = var1.getAccessibleStateSet();
               if (this.isShowing()) {
                  var2.add(AccessibleState.SHOWING);
               }

               return var2;
            } else {
               return new AccessibleStateSet();
            }
         }

         public int getAccessibleIndexInParent() {
            return this.column;
         }

         public int getAccessibleChildrenCount() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleChildrenCount() : 0;
         }

         public Accessible getAccessibleChild(int var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               Accessible var3 = var2.getAccessibleChild(var1);
               var2.setAccessibleParent(this);
               return var3;
            } else {
               return null;
            }
         }

         public Locale getLocale() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getLocale() : null;
         }

         public void addPropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.addPropertyChangeListener(var1);
            } else {
               super.addPropertyChangeListener(var1);
            }

         }

         public void removePropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.removePropertyChangeListener(var1);
            } else {
               super.removePropertyChangeListener(var1);
            }

         }

         public AccessibleAction getAccessibleAction() {
            return this.getCurrentAccessibleContext().getAccessibleAction();
         }

         public AccessibleComponent getAccessibleComponent() {
            return this;
         }

         public AccessibleSelection getAccessibleSelection() {
            return this.getCurrentAccessibleContext().getAccessibleSelection();
         }

         public AccessibleText getAccessibleText() {
            return this.getCurrentAccessibleContext().getAccessibleText();
         }

         public AccessibleValue getAccessibleValue() {
            return this.getCurrentAccessibleContext().getAccessibleValue();
         }

         public Color getBackground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getBackground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getBackground() : null;
            }
         }

         public void setBackground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBackground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBackground(var1);
               }
            }

         }

         public Color getForeground() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getForeground();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getForeground() : null;
            }
         }

         public void setForeground(Color var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setForeground(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setForeground(var1);
               }
            }

         }

         public Cursor getCursor() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getCursor();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  return var2.getCursor();
               } else {
                  Accessible var3 = this.getAccessibleParent();
                  return var3 instanceof AccessibleComponent ? ((AccessibleComponent)var3).getCursor() : null;
               }
            }
         }

         public void setCursor(Cursor var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setCursor(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setCursor(var1);
               }
            }

         }

         public Font getFont() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).getFont();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.getFont() : null;
            }
         }

         public void setFont(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setFont(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setFont(var1);
               }
            }

         }

         public FontMetrics getFontMetrics(Font var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var2).getFontMetrics(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               return var3 != null ? var3.getFontMetrics(var1) : null;
            }
         }

         public boolean isEnabled() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isEnabled();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isEnabled() : false;
            }
         }

         public void setEnabled(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setEnabled(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setEnabled(var1);
               }
            }

         }

         public boolean isVisible() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isVisible();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isVisible() : false;
            }
         }

         public void setVisible(boolean var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setVisible(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setVisible(var1);
               }
            }

         }

         public boolean isShowing() {
            return this.isVisible() && JTableHeader.this.isShowing();
         }

         public boolean contains(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               Rectangle var5 = ((AccessibleComponent)var2).getBounds();
               return var5.contains(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  Rectangle var4 = var3.getBounds();
                  return var4.contains(var1);
               } else {
                  return this.getBounds().contains(var1);
               }
            }
         }

         public Point getLocationOnScreen() {
            if (this.parent != null) {
               Point var1 = this.parent.getLocationOnScreen();
               Point var2 = this.getLocation();
               var2.translate(var1.x, var1.y);
               return var2;
            } else {
               return null;
            }
         }

         public Point getLocation() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               Rectangle var4 = ((AccessibleComponent)var1).getBounds();
               return var4.getLocation();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  Rectangle var3 = var2.getBounds();
                  return var3.getLocation();
               } else {
                  return this.getBounds().getLocation();
               }
            }
         }

         public void setLocation(Point var1) {
         }

         public Rectangle getBounds() {
            Rectangle var1 = this.table.getCellRect(-1, this.column, false);
            var1.y = 0;
            return var1;
         }

         public void setBounds(Rectangle var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBounds(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setBounds(var1);
               }
            }

         }

         public Dimension getSize() {
            return this.getBounds().getSize();
         }

         public void setSize(Dimension var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setSize(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.setSize(var1);
               }
            }

         }

         public Accessible getAccessibleAt(Point var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            return var2 instanceof AccessibleComponent ? ((AccessibleComponent)var2).getAccessibleAt(var1) : null;
         }

         public boolean isFocusTraversable() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               return ((AccessibleComponent)var1).isFocusTraversable();
            } else {
               Component var2 = this.getCurrentComponent();
               return var2 != null ? var2.isFocusTraversable() : false;
            }
         }

         public void requestFocus() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 instanceof AccessibleComponent) {
               ((AccessibleComponent)var1).requestFocus();
            } else {
               Component var2 = this.getCurrentComponent();
               if (var2 != null) {
                  var2.requestFocus();
               }
            }

         }

         public void addFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).addFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.addFocusListener(var1);
               }
            }

         }

         public void removeFocusListener(FocusListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).removeFocusListener(var1);
            } else {
               Component var3 = this.getCurrentComponent();
               if (var3 != null) {
                  var3.removeFocusListener(var1);
               }
            }

         }
      }
   }
}
