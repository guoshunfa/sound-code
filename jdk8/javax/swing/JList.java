package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import javax.swing.text.Position;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class JList<E> extends JComponent implements Scrollable, Accessible {
   private static final String uiClassID = "ListUI";
   public static final int VERTICAL = 0;
   public static final int VERTICAL_WRAP = 1;
   public static final int HORIZONTAL_WRAP = 2;
   private int fixedCellWidth;
   private int fixedCellHeight;
   private int horizontalScrollIncrement;
   private E prototypeCellValue;
   private int visibleRowCount;
   private Color selectionForeground;
   private Color selectionBackground;
   private boolean dragEnabled;
   private ListSelectionModel selectionModel;
   private ListModel<E> dataModel;
   private ListCellRenderer<? super E> cellRenderer;
   private ListSelectionListener selectionListener;
   private int layoutOrientation;
   private DropMode dropMode;
   private transient JList.DropLocation dropLocation;

   public JList(ListModel<E> var1) {
      this.fixedCellWidth = -1;
      this.fixedCellHeight = -1;
      this.horizontalScrollIncrement = -1;
      this.visibleRowCount = 8;
      this.dropMode = DropMode.USE_SELECTION;
      if (var1 == null) {
         throw new IllegalArgumentException("dataModel must be non null");
      } else {
         ToolTipManager var2 = ToolTipManager.sharedInstance();
         var2.registerComponent(this);
         this.layoutOrientation = 0;
         this.dataModel = var1;
         this.selectionModel = this.createSelectionModel();
         this.setAutoscrolls(true);
         this.setOpaque(true);
         this.updateUI();
      }
   }

   public JList(final E[] var1) {
      this((ListModel)(new AbstractListModel<E>() {
         public int getSize() {
            return var1.length;
         }

         public E getElementAt(int var1x) {
            return var1[var1x];
         }
      }));
   }

   public JList(final Vector<? extends E> var1) {
      this((ListModel)(new AbstractListModel<E>() {
         public int getSize() {
            return var1.size();
         }

         public E getElementAt(int var1x) {
            return var1.elementAt(var1x);
         }
      }));
   }

   public JList() {
      this((ListModel)(new AbstractListModel<E>() {
         public int getSize() {
            return 0;
         }

         public E getElementAt(int var1) {
            throw new IndexOutOfBoundsException("No Data Model");
         }
      }));
   }

   public ListUI getUI() {
      return (ListUI)this.ui;
   }

   public void setUI(ListUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((ListUI)UIManager.getUI(this));
      ListCellRenderer var1 = this.getCellRenderer();
      if (var1 instanceof Component) {
         SwingUtilities.updateComponentTreeUI((Component)var1);
      }

   }

   public String getUIClassID() {
      return "ListUI";
   }

   private void updateFixedCellSize() {
      ListCellRenderer var1 = this.getCellRenderer();
      Object var2 = this.getPrototypeCellValue();
      if (var1 != null && var2 != null) {
         Component var3 = var1.getListCellRendererComponent(this, var2, 0, false, false);
         Font var4 = var3.getFont();
         var3.setFont(this.getFont());
         Dimension var5 = var3.getPreferredSize();
         this.fixedCellWidth = var5.width;
         this.fixedCellHeight = var5.height;
         var3.setFont(var4);
      }

   }

   public E getPrototypeCellValue() {
      return this.prototypeCellValue;
   }

   public void setPrototypeCellValue(E var1) {
      Object var2 = this.prototypeCellValue;
      this.prototypeCellValue = var1;
      if (var1 != null && !var1.equals(var2)) {
         this.updateFixedCellSize();
      }

      this.firePropertyChange("prototypeCellValue", var2, var1);
   }

   public int getFixedCellWidth() {
      return this.fixedCellWidth;
   }

   public void setFixedCellWidth(int var1) {
      int var2 = this.fixedCellWidth;
      this.fixedCellWidth = var1;
      this.firePropertyChange("fixedCellWidth", var2, this.fixedCellWidth);
   }

   public int getFixedCellHeight() {
      return this.fixedCellHeight;
   }

   public void setFixedCellHeight(int var1) {
      int var2 = this.fixedCellHeight;
      this.fixedCellHeight = var1;
      this.firePropertyChange("fixedCellHeight", var2, this.fixedCellHeight);
   }

   @Transient
   public ListCellRenderer<? super E> getCellRenderer() {
      return this.cellRenderer;
   }

   public void setCellRenderer(ListCellRenderer<? super E> var1) {
      ListCellRenderer var2 = this.cellRenderer;
      this.cellRenderer = var1;
      if (var1 != null && !var1.equals(var2)) {
         this.updateFixedCellSize();
      }

      this.firePropertyChange("cellRenderer", var2, var1);
   }

   public Color getSelectionForeground() {
      return this.selectionForeground;
   }

   public void setSelectionForeground(Color var1) {
      Color var2 = this.selectionForeground;
      this.selectionForeground = var1;
      this.firePropertyChange("selectionForeground", var2, var1);
   }

   public Color getSelectionBackground() {
      return this.selectionBackground;
   }

   public void setSelectionBackground(Color var1) {
      Color var2 = this.selectionBackground;
      this.selectionBackground = var1;
      this.firePropertyChange("selectionBackground", var2, var1);
   }

   public int getVisibleRowCount() {
      return this.visibleRowCount;
   }

   public void setVisibleRowCount(int var1) {
      int var2 = this.visibleRowCount;
      this.visibleRowCount = Math.max(0, var1);
      this.firePropertyChange("visibleRowCount", var2, var1);
   }

   public int getLayoutOrientation() {
      return this.layoutOrientation;
   }

   public void setLayoutOrientation(int var1) {
      int var2 = this.layoutOrientation;
      switch(var1) {
      case 0:
      case 1:
      case 2:
         this.layoutOrientation = var1;
         this.firePropertyChange("layoutOrientation", var2, var1);
         return;
      default:
         throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
      }
   }

   public int getFirstVisibleIndex() {
      Rectangle var1 = this.getVisibleRect();
      int var2;
      if (this.getComponentOrientation().isLeftToRight()) {
         var2 = this.locationToIndex(var1.getLocation());
      } else {
         var2 = this.locationToIndex(new Point(var1.x + var1.width - 1, var1.y));
      }

      if (var2 != -1) {
         Rectangle var3 = this.getCellBounds(var2, var2);
         if (var3 != null) {
            SwingUtilities.computeIntersection(var1.x, var1.y, var1.width, var1.height, var3);
            if (var3.width == 0 || var3.height == 0) {
               var2 = -1;
            }
         }
      }

      return var2;
   }

   public int getLastVisibleIndex() {
      boolean var1 = this.getComponentOrientation().isLeftToRight();
      Rectangle var2 = this.getVisibleRect();
      Point var3;
      if (var1) {
         var3 = new Point(var2.x + var2.width - 1, var2.y + var2.height - 1);
      } else {
         var3 = new Point(var2.x, var2.y + var2.height - 1);
      }

      int var4 = this.locationToIndex(var3);
      if (var4 != -1) {
         Rectangle var5 = this.getCellBounds(var4, var4);
         if (var5 != null) {
            SwingUtilities.computeIntersection(var2.x, var2.y, var2.width, var2.height, var5);
            if (var5.width == 0 || var5.height == 0) {
               boolean var6 = this.getLayoutOrientation() == 2;
               Point var7 = var6 ? new Point(var3.x, var2.y) : new Point(var2.x, var3.y);
               int var9 = -1;
               int var10 = var4;
               var4 = -1;

               int var8;
               do {
                  var8 = var9;
                  var9 = this.locationToIndex(var7);
                  if (var9 != -1) {
                     var5 = this.getCellBounds(var9, var9);
                     if (var9 != var10 && var5 != null && var5.contains(var7)) {
                        var4 = var9;
                        if (var6) {
                           var7.y = var5.y + var5.height;
                           if (var7.y >= var3.y) {
                              var8 = var9;
                           }
                        } else {
                           var7.x = var5.x + var5.width;
                           if (var7.x >= var3.x) {
                              var8 = var9;
                           }
                        }
                     } else {
                        var8 = var9;
                     }
                  }
               } while(var9 != -1 && var8 != var9);
            }
         }
      }

      return var4;
   }

   public void ensureIndexIsVisible(int var1) {
      Rectangle var2 = this.getCellBounds(var1, var1);
      if (var2 != null) {
         this.scrollRectToVisible(var2);
      }

   }

   public void setDragEnabled(boolean var1) {
      if (var1 && GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         this.dragEnabled = var1;
      }
   }

   public boolean getDragEnabled() {
      return this.dragEnabled;
   }

   public final void setDropMode(DropMode var1) {
      if (var1 != null) {
         switch(var1) {
         case USE_SELECTION:
         case ON:
         case INSERT:
         case ON_OR_INSERT:
            this.dropMode = var1;
            return;
         }
      }

      throw new IllegalArgumentException(var1 + ": Unsupported drop mode for list");
   }

   public final DropMode getDropMode() {
      return this.dropMode;
   }

   JList.DropLocation dropLocationForPoint(Point var1) {
      JList.DropLocation var2 = null;
      Rectangle var3 = null;
      int var4 = this.locationToIndex(var1);
      if (var4 != -1) {
         var3 = this.getCellBounds(var4, var4);
      }

      boolean var5;
      switch(this.dropMode) {
      case USE_SELECTION:
      case ON:
         var2 = new JList.DropLocation(var1, var3 != null && var3.contains(var1) ? var4 : -1, false);
         break;
      case INSERT:
         if (var4 == -1) {
            var2 = new JList.DropLocation(var1, this.getModel().getSize(), true);
         } else {
            if (this.layoutOrientation == 2) {
               var5 = this.getComponentOrientation().isLeftToRight();
               if (SwingUtilities2.liesInHorizontal(var3, var1, var5, false) == SwingUtilities2.Section.TRAILING) {
                  ++var4;
               } else if (var4 == this.getModel().getSize() - 1 && var1.y >= var3.y + var3.height) {
                  ++var4;
               }
            } else if (SwingUtilities2.liesInVertical(var3, var1, false) == SwingUtilities2.Section.TRAILING) {
               ++var4;
            }

            var2 = new JList.DropLocation(var1, var4, true);
         }
         break;
      case ON_OR_INSERT:
         if (var4 == -1) {
            var2 = new JList.DropLocation(var1, this.getModel().getSize(), true);
         } else {
            var5 = false;
            if (this.layoutOrientation == 2) {
               boolean var8 = this.getComponentOrientation().isLeftToRight();
               SwingUtilities2.Section var7 = SwingUtilities2.liesInHorizontal(var3, var1, var8, true);
               if (var7 == SwingUtilities2.Section.TRAILING) {
                  ++var4;
                  var5 = true;
               } else if (var4 == this.getModel().getSize() - 1 && var1.y >= var3.y + var3.height) {
                  ++var4;
                  var5 = true;
               } else if (var7 == SwingUtilities2.Section.LEADING) {
                  var5 = true;
               }
            } else {
               SwingUtilities2.Section var6 = SwingUtilities2.liesInVertical(var3, var1, true);
               if (var6 == SwingUtilities2.Section.LEADING) {
                  var5 = true;
               } else if (var6 == SwingUtilities2.Section.TRAILING) {
                  ++var4;
                  var5 = true;
               }
            }

            var2 = new JList.DropLocation(var1, var4, var5);
         }
         break;
      default:
         assert false : "Unexpected drop mode";
      }

      return var2;
   }

   Object setDropLocation(TransferHandler.DropLocation var1, Object var2, boolean var3) {
      Object var4 = null;
      JList.DropLocation var5 = (JList.DropLocation)var1;
      if (this.dropMode == DropMode.USE_SELECTION) {
         int var6;
         if (var5 == null) {
            if (!var3 && var2 != null) {
               this.setSelectedIndices(((int[][])((int[][])var2))[0]);
               var6 = ((int[][])((int[][])var2))[1][0];
               int var7 = ((int[][])((int[][])var2))[1][1];
               SwingUtilities2.setLeadAnchorWithoutSelection(this.getSelectionModel(), var7, var6);
            }
         } else {
            if (this.dropLocation == null) {
               int[] var8 = this.getSelectedIndices();
               var4 = new int[][]{var8, {this.getAnchorSelectionIndex(), this.getLeadSelectionIndex()}};
            } else {
               var4 = var2;
            }

            var6 = var5.getIndex();
            if (var6 == -1) {
               this.clearSelection();
               this.getSelectionModel().setAnchorSelectionIndex(-1);
               this.getSelectionModel().setLeadSelectionIndex(-1);
            } else {
               this.setSelectionInterval(var6, var6);
            }
         }
      }

      JList.DropLocation var9 = this.dropLocation;
      this.dropLocation = var5;
      this.firePropertyChange("dropLocation", var9, this.dropLocation);
      return var4;
   }

   public final JList.DropLocation getDropLocation() {
      return this.dropLocation;
   }

   public int getNextMatch(String var1, int var2, Position.Bias var3) {
      ListModel var4 = this.getModel();
      int var5 = var4.getSize();
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else if (var2 >= 0 && var2 < var5) {
         var1 = var1.toUpperCase();
         int var6 = var3 == Position.Bias.Forward ? 1 : -1;
         int var7 = var2;

         do {
            Object var8 = var4.getElementAt(var7);
            if (var8 != null) {
               String var9;
               if (var8 instanceof String) {
                  var9 = ((String)var8).toUpperCase();
               } else {
                  var9 = var8.toString();
                  if (var9 != null) {
                     var9 = var9.toUpperCase();
                  }
               }

               if (var9 != null && var9.startsWith(var1)) {
                  return var7;
               }
            }

            var7 = (var7 + var6 + var5) % var5;
         } while(var7 != var2);

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getToolTipText(MouseEvent var1) {
      if (var1 != null) {
         Point var2 = var1.getPoint();
         int var3 = this.locationToIndex(var2);
         ListCellRenderer var4 = this.getCellRenderer();
         Rectangle var5;
         if (var3 != -1 && var4 != null && (var5 = this.getCellBounds(var3, var3)) != null && var5.contains(var2.x, var2.y)) {
            ListSelectionModel var6 = this.getSelectionModel();
            Component var7 = var4.getListCellRendererComponent(this, this.getModel().getElementAt(var3), var3, var6.isSelectedIndex(var3), this.hasFocus() && var6.getLeadSelectionIndex() == var3);
            if (var7 instanceof JComponent) {
               var2.translate(-var5.x, -var5.y);
               MouseEvent var8 = new MouseEvent(var7, var1.getID(), var1.getWhen(), var1.getModifiers(), var2.x, var2.y, var1.getXOnScreen(), var1.getYOnScreen(), var1.getClickCount(), var1.isPopupTrigger(), 0);
               AWTAccessor.MouseEventAccessor var9 = AWTAccessor.getMouseEventAccessor();
               var9.setCausedByTouchEvent(var8, var9.isCausedByTouchEvent(var1));
               String var10 = ((JComponent)var7).getToolTipText(var8);
               if (var10 != null) {
                  return var10;
               }
            }
         }
      }

      return super.getToolTipText();
   }

   public int locationToIndex(Point var1) {
      ListUI var2 = this.getUI();
      return var2 != null ? var2.locationToIndex(this, var1) : -1;
   }

   public Point indexToLocation(int var1) {
      ListUI var2 = this.getUI();
      return var2 != null ? var2.indexToLocation(this, var1) : null;
   }

   public Rectangle getCellBounds(int var1, int var2) {
      ListUI var3 = this.getUI();
      return var3 != null ? var3.getCellBounds(this, var1, var2) : null;
   }

   public ListModel<E> getModel() {
      return this.dataModel;
   }

   public void setModel(ListModel<E> var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("model must be non null");
      } else {
         ListModel var2 = this.dataModel;
         this.dataModel = var1;
         this.firePropertyChange("model", var2, this.dataModel);
         this.clearSelection();
      }
   }

   public void setListData(final E[] var1) {
      this.setModel(new AbstractListModel<E>() {
         public int getSize() {
            return var1.length;
         }

         public E getElementAt(int var1x) {
            return var1[var1x];
         }
      });
   }

   public void setListData(final Vector<? extends E> var1) {
      this.setModel(new AbstractListModel<E>() {
         public int getSize() {
            return var1.size();
         }

         public E getElementAt(int var1x) {
            return var1.elementAt(var1x);
         }
      });
   }

   protected ListSelectionModel createSelectionModel() {
      return new DefaultListSelectionModel();
   }

   public ListSelectionModel getSelectionModel() {
      return this.selectionModel;
   }

   protected void fireSelectionValueChanged(int var1, int var2, boolean var3) {
      Object[] var4 = this.listenerList.getListenerList();
      ListSelectionEvent var5 = null;

      for(int var6 = var4.length - 2; var6 >= 0; var6 -= 2) {
         if (var4[var6] == ListSelectionListener.class) {
            if (var5 == null) {
               var5 = new ListSelectionEvent(this, var1, var2, var3);
            }

            ((ListSelectionListener)var4[var6 + 1]).valueChanged(var5);
         }
      }

   }

   public void addListSelectionListener(ListSelectionListener var1) {
      if (this.selectionListener == null) {
         this.selectionListener = new JList.ListSelectionHandler();
         this.getSelectionModel().addListSelectionListener(this.selectionListener);
      }

      this.listenerList.add(ListSelectionListener.class, var1);
   }

   public void removeListSelectionListener(ListSelectionListener var1) {
      this.listenerList.remove(ListSelectionListener.class, var1);
   }

   public ListSelectionListener[] getListSelectionListeners() {
      return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class);
   }

   public void setSelectionModel(ListSelectionModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("selectionModel must be non null");
      } else {
         if (this.selectionListener != null) {
            this.selectionModel.removeListSelectionListener(this.selectionListener);
            var1.addListSelectionListener(this.selectionListener);
         }

         ListSelectionModel var2 = this.selectionModel;
         this.selectionModel = var1;
         this.firePropertyChange("selectionModel", var2, var1);
      }
   }

   public void setSelectionMode(int var1) {
      this.getSelectionModel().setSelectionMode(var1);
   }

   public int getSelectionMode() {
      return this.getSelectionModel().getSelectionMode();
   }

   public int getAnchorSelectionIndex() {
      return this.getSelectionModel().getAnchorSelectionIndex();
   }

   public int getLeadSelectionIndex() {
      return this.getSelectionModel().getLeadSelectionIndex();
   }

   public int getMinSelectionIndex() {
      return this.getSelectionModel().getMinSelectionIndex();
   }

   public int getMaxSelectionIndex() {
      return this.getSelectionModel().getMaxSelectionIndex();
   }

   public boolean isSelectedIndex(int var1) {
      return this.getSelectionModel().isSelectedIndex(var1);
   }

   public boolean isSelectionEmpty() {
      return this.getSelectionModel().isSelectionEmpty();
   }

   public void clearSelection() {
      this.getSelectionModel().clearSelection();
   }

   public void setSelectionInterval(int var1, int var2) {
      this.getSelectionModel().setSelectionInterval(var1, var2);
   }

   public void addSelectionInterval(int var1, int var2) {
      this.getSelectionModel().addSelectionInterval(var1, var2);
   }

   public void removeSelectionInterval(int var1, int var2) {
      this.getSelectionModel().removeSelectionInterval(var1, var2);
   }

   public void setValueIsAdjusting(boolean var1) {
      this.getSelectionModel().setValueIsAdjusting(var1);
   }

   public boolean getValueIsAdjusting() {
      return this.getSelectionModel().getValueIsAdjusting();
   }

   @Transient
   public int[] getSelectedIndices() {
      ListSelectionModel var1 = this.getSelectionModel();
      int var2 = var1.getMinSelectionIndex();
      int var3 = var1.getMaxSelectionIndex();
      if (var2 >= 0 && var3 >= 0) {
         int[] var4 = new int[1 + (var3 - var2)];
         int var5 = 0;

         for(int var6 = var2; var6 <= var3; ++var6) {
            if (var1.isSelectedIndex(var6)) {
               var4[var5++] = var6;
            }
         }

         int[] var7 = new int[var5];
         System.arraycopy(var4, 0, var7, 0, var5);
         return var7;
      } else {
         return new int[0];
      }
   }

   public void setSelectedIndex(int var1) {
      if (var1 < this.getModel().getSize()) {
         this.getSelectionModel().setSelectionInterval(var1, var1);
      }
   }

   public void setSelectedIndices(int[] var1) {
      ListSelectionModel var2 = this.getSelectionModel();
      var2.clearSelection();
      int var3 = this.getModel().getSize();
      int[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4[var6];
         if (var7 < var3) {
            var2.addSelectionInterval(var7, var7);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public Object[] getSelectedValues() {
      ListSelectionModel var1 = this.getSelectionModel();
      ListModel var2 = this.getModel();
      int var3 = var1.getMinSelectionIndex();
      int var4 = var1.getMaxSelectionIndex();
      if (var3 >= 0 && var4 >= 0) {
         Object[] var5 = new Object[1 + (var4 - var3)];
         int var6 = 0;

         for(int var7 = var3; var7 <= var4; ++var7) {
            if (var1.isSelectedIndex(var7)) {
               var5[var6++] = var2.getElementAt(var7);
            }
         }

         Object[] var8 = new Object[var6];
         System.arraycopy(var5, 0, var8, 0, var6);
         return var8;
      } else {
         return new Object[0];
      }
   }

   public List<E> getSelectedValuesList() {
      ListSelectionModel var1 = this.getSelectionModel();
      ListModel var2 = this.getModel();
      int var3 = var1.getMinSelectionIndex();
      int var4 = var1.getMaxSelectionIndex();
      if (var3 >= 0 && var4 >= 0) {
         ArrayList var5 = new ArrayList();

         for(int var6 = var3; var6 <= var4; ++var6) {
            if (var1.isSelectedIndex(var6)) {
               var5.add(var2.getElementAt(var6));
            }
         }

         return var5;
      } else {
         return Collections.emptyList();
      }
   }

   public int getSelectedIndex() {
      return this.getMinSelectionIndex();
   }

   public E getSelectedValue() {
      int var1 = this.getMinSelectionIndex();
      return var1 == -1 ? null : this.getModel().getElementAt(var1);
   }

   public void setSelectedValue(Object var1, boolean var2) {
      if (var1 == null) {
         this.setSelectedIndex(-1);
      } else if (!var1.equals(this.getSelectedValue())) {
         ListModel var5 = this.getModel();
         int var3 = 0;

         for(int var4 = var5.getSize(); var3 < var4; ++var3) {
            if (var1.equals(var5.getElementAt(var3))) {
               this.setSelectedIndex(var3);
               if (var2) {
                  this.ensureIndexIsVisible(var3);
               }

               this.repaint();
               return;
            }
         }

         this.setSelectedIndex(-1);
      }

      this.repaint();
   }

   private void checkScrollableParameters(Rectangle var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("visibleRect must be non-null");
      } else {
         switch(var2) {
         case 0:
         case 1:
            return;
         default:
            throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
         }
      }
   }

   public Dimension getPreferredScrollableViewportSize() {
      if (this.getLayoutOrientation() != 0) {
         return this.getPreferredSize();
      } else {
         Insets var1 = this.getInsets();
         int var2 = var1.left + var1.right;
         int var3 = var1.top + var1.bottom;
         int var4 = this.getVisibleRowCount();
         int var5 = this.getFixedCellWidth();
         int var6 = this.getFixedCellHeight();
         int var7;
         int var8;
         if (var5 > 0 && var6 > 0) {
            var7 = var5 + var2;
            var8 = var4 * var6 + var3;
            return new Dimension(var7, var8);
         } else if (this.getModel().getSize() > 0) {
            var7 = this.getPreferredSize().width;
            Rectangle var9 = this.getCellBounds(0, 0);
            if (var9 != null) {
               var8 = var4 * var9.height + var3;
            } else {
               var8 = 1;
            }

            return new Dimension(var7, var8);
         } else {
            var5 = var5 > 0 ? var5 : 256;
            var6 = var6 > 0 ? var6 : 16;
            return new Dimension(var5, var6 * var4);
         }
      }
   }

   public int getScrollableUnitIncrement(Rectangle var1, int var2, int var3) {
      this.checkScrollableParameters(var1, var2);
      Point var6;
      if (var2 == 1) {
         int var11 = this.locationToIndex(var1.getLocation());
         if (var11 == -1) {
            return 0;
         } else {
            Rectangle var12;
            if (var3 > 0) {
               var12 = this.getCellBounds(var11, var11);
               return var12 == null ? 0 : var12.height - (var1.y - var12.y);
            } else {
               var12 = this.getCellBounds(var11, var11);
               if (var12.y == var1.y && var11 == 0) {
                  return 0;
               } else if (var12.y == var1.y) {
                  var6 = var12.getLocation();
                  --var6.y;
                  int var13 = this.locationToIndex(var6);
                  Rectangle var14 = this.getCellBounds(var13, var13);
                  return var14 != null && var14.y < var12.y ? var14.height : 0;
               } else {
                  return var1.y - var12.y;
               }
            }
         }
      } else {
         if (var2 == 0 && this.getLayoutOrientation() != 0) {
            boolean var4 = this.getComponentOrientation().isLeftToRight();
            if (var4) {
               var6 = var1.getLocation();
            } else {
               var6 = new Point(var1.x + var1.width - 1, var1.y);
            }

            int var5 = this.locationToIndex(var6);
            if (var5 != -1) {
               Rectangle var7 = this.getCellBounds(var5, var5);
               if (var7 != null && var7.contains(var6)) {
                  int var8;
                  int var9;
                  if (var4) {
                     var8 = var1.x;
                     var9 = var7.x;
                  } else {
                     var8 = var1.x + var1.width;
                     var9 = var7.x + var7.width;
                  }

                  if (var9 != var8) {
                     if (var3 < 0) {
                        return Math.abs(var8 - var9);
                     }

                     if (var4) {
                        return var9 + var7.width - var8;
                     }

                     return var8 - var7.x;
                  }

                  return var7.width;
               }
            }
         }

         Font var10 = this.getFont();
         return var10 != null ? var10.getSize() : 1;
      }
   }

   public int getScrollableBlockIncrement(Rectangle var1, int var2, int var3) {
      this.checkScrollableParameters(var1, var2);
      int var5;
      int var6;
      Rectangle var8;
      if (var2 == 1) {
         int var11 = var1.height;
         if (var3 > 0) {
            var5 = this.locationToIndex(new Point(var1.x, var1.y + var1.height - 1));
            if (var5 != -1) {
               Rectangle var12 = this.getCellBounds(var5, var5);
               if (var12 != null) {
                  var11 = var12.y - var1.y;
                  if (var11 == 0 && var5 < this.getModel().getSize() - 1) {
                     var11 = var12.height;
                  }
               }
            }
         } else {
            var5 = this.locationToIndex(new Point(var1.x, var1.y - var1.height));
            var6 = this.getFirstVisibleIndex();
            if (var5 != -1) {
               if (var6 == -1) {
                  var6 = this.locationToIndex(var1.getLocation());
               }

               Rectangle var13 = this.getCellBounds(var5, var5);
               var8 = this.getCellBounds(var6, var6);
               if (var13 != null && var8 != null) {
                  while(var13.y + var1.height < var8.y + var8.height && var13.y < var8.y) {
                     ++var5;
                     var13 = this.getCellBounds(var5, var5);
                  }

                  var11 = var1.y - var13.y;
                  if (var11 <= 0 && var13.y > 0) {
                     --var5;
                     var13 = this.getCellBounds(var5, var5);
                     if (var13 != null) {
                        var11 = var1.y - var13.y;
                     }
                  }
               }
            }
         }

         return var11;
      } else if (var2 == 0 && this.getLayoutOrientation() != 0) {
         boolean var4 = this.getComponentOrientation().isLeftToRight();
         var5 = var1.width;
         int var7;
         if (var3 > 0) {
            var6 = var1.x + (var4 ? var1.width - 1 : 0);
            var7 = this.locationToIndex(new Point(var6, var1.y));
            if (var7 != -1) {
               var8 = this.getCellBounds(var7, var7);
               if (var8 != null) {
                  if (var4) {
                     var5 = var8.x - var1.x;
                  } else {
                     var5 = var1.x + var1.width - (var8.x + var8.width);
                  }

                  if (var5 < 0) {
                     var5 += var8.width;
                  } else if (var5 == 0 && var7 < this.getModel().getSize() - 1) {
                     var5 = var8.width;
                  }
               }
            }
         } else {
            var6 = var1.x + (var4 ? -var1.width : var1.width - 1 + var1.width);
            var7 = this.locationToIndex(new Point(var6, var1.y));
            if (var7 != -1) {
               var8 = this.getCellBounds(var7, var7);
               if (var8 != null) {
                  int var9 = var8.x + var8.width;
                  if (var4) {
                     if (var8.x < var1.x - var1.width && var9 < var1.x) {
                        var5 = var1.x - var9;
                     } else {
                        var5 = var1.x - var8.x;
                     }
                  } else {
                     int var10 = var1.x + var1.width;
                     if (var9 > var10 + var1.width && var8.x > var10) {
                        var5 = var8.x - var10;
                     } else {
                        var5 = var9 - var10;
                     }
                  }
               }
            }
         }

         return var5;
      } else {
         return var1.width;
      }
   }

   public boolean getScrollableTracksViewportWidth() {
      if (this.getLayoutOrientation() == 2 && this.getVisibleRowCount() <= 0) {
         return true;
      } else {
         Container var1 = SwingUtilities.getUnwrappedParent(this);
         if (var1 instanceof JViewport) {
            return var1.getWidth() > this.getPreferredSize().width;
         } else {
            return false;
         }
      }
   }

   public boolean getScrollableTracksViewportHeight() {
      if (this.getLayoutOrientation() == 1 && this.getVisibleRowCount() <= 0) {
         return true;
      } else {
         Container var1 = SwingUtilities.getUnwrappedParent(this);
         if (var1 instanceof JViewport) {
            return var1.getHeight() > this.getPreferredSize().height;
         } else {
            return false;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ListUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.selectionForeground != null ? this.selectionForeground.toString() : "";
      String var2 = this.selectionBackground != null ? this.selectionBackground.toString() : "";
      return super.paramString() + ",fixedCellHeight=" + this.fixedCellHeight + ",fixedCellWidth=" + this.fixedCellWidth + ",horizontalScrollIncrement=" + this.horizontalScrollIncrement + ",selectionBackground=" + var2 + ",selectionForeground=" + var1 + ",visibleRowCount=" + this.visibleRowCount + ",layoutOrientation=" + this.layoutOrientation;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JList.AccessibleJList();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJList extends JComponent.AccessibleJComponent implements AccessibleSelection, PropertyChangeListener, ListSelectionListener, ListDataListener {
      int leadSelectionIndex;

      public AccessibleJList() {
         super();
         JList.this.addPropertyChangeListener(this);
         JList.this.getSelectionModel().addListSelectionListener(this);
         JList.this.getModel().addListDataListener(this);
         this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         Object var3 = var1.getOldValue();
         Object var4 = var1.getNewValue();
         if (var2.compareTo("model") == 0) {
            if (var3 != null && var3 instanceof ListModel) {
               ((ListModel)var3).removeListDataListener(this);
            }

            if (var4 != null && var4 instanceof ListModel) {
               ((ListModel)var4).addListDataListener(this);
            }
         } else if (var2.compareTo("selectionModel") == 0) {
            if (var3 != null && var3 instanceof ListSelectionModel) {
               ((ListSelectionModel)var3).removeListSelectionListener(this);
            }

            if (var4 != null && var4 instanceof ListSelectionModel) {
               ((ListSelectionModel)var4).addListSelectionListener(this);
            }

            this.firePropertyChange("AccessibleSelection", false, true);
         }

      }

      public void valueChanged(ListSelectionEvent var1) {
         int var2 = this.leadSelectionIndex;
         this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
         if (var2 != this.leadSelectionIndex) {
            Accessible var3 = var2 >= 0 ? this.getAccessibleChild(var2) : null;
            Accessible var4 = this.leadSelectionIndex >= 0 ? this.getAccessibleChild(this.leadSelectionIndex) : null;
            this.firePropertyChange("AccessibleActiveDescendant", var3, var4);
         }

         this.firePropertyChange("AccessibleVisibleData", false, true);
         this.firePropertyChange("AccessibleSelection", false, true);
         AccessibleStateSet var5 = this.getAccessibleStateSet();
         ListSelectionModel var6 = JList.this.getSelectionModel();
         if (var6.getSelectionMode() != 0) {
            if (!var5.contains(AccessibleState.MULTISELECTABLE)) {
               var5.add(AccessibleState.MULTISELECTABLE);
               this.firePropertyChange("AccessibleState", (Object)null, AccessibleState.MULTISELECTABLE);
            }
         } else if (var5.contains(AccessibleState.MULTISELECTABLE)) {
            var5.remove(AccessibleState.MULTISELECTABLE);
            this.firePropertyChange("AccessibleState", AccessibleState.MULTISELECTABLE, (Object)null);
         }

      }

      public void intervalAdded(ListDataEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", false, true);
      }

      public void intervalRemoved(ListDataEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", false, true);
      }

      public void contentsChanged(ListDataEvent var1) {
         this.firePropertyChange("AccessibleVisibleData", false, true);
      }

      public AccessibleStateSet getAccessibleStateSet() {
         AccessibleStateSet var1 = super.getAccessibleStateSet();
         if (JList.this.selectionModel.getSelectionMode() != 0) {
            var1.add(AccessibleState.MULTISELECTABLE);
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.LIST;
      }

      public Accessible getAccessibleAt(Point var1) {
         int var2 = JList.this.locationToIndex(var1);
         return var2 >= 0 ? new JList.AccessibleJList.ActionableAccessibleJListChild(JList.this, var2) : null;
      }

      public int getAccessibleChildrenCount() {
         return JList.this.getModel().getSize();
      }

      public Accessible getAccessibleChild(int var1) {
         return var1 >= JList.this.getModel().getSize() ? null : new JList.AccessibleJList.ActionableAccessibleJListChild(JList.this, var1);
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         return JList.this.getSelectedIndices().length;
      }

      public Accessible getAccessibleSelection(int var1) {
         int var2 = this.getAccessibleSelectionCount();
         return var1 >= 0 && var1 < var2 ? this.getAccessibleChild(JList.this.getSelectedIndices()[var1]) : null;
      }

      public boolean isAccessibleChildSelected(int var1) {
         return JList.this.isSelectedIndex(var1);
      }

      public void addAccessibleSelection(int var1) {
         JList.this.addSelectionInterval(var1, var1);
      }

      public void removeAccessibleSelection(int var1) {
         JList.this.removeSelectionInterval(var1, var1);
      }

      public void clearAccessibleSelection() {
         JList.this.clearSelection();
      }

      public void selectAllAccessibleSelection() {
         JList.this.addSelectionInterval(0, this.getAccessibleChildrenCount() - 1);
      }

      private class ActionableAccessibleJListChild extends JList<E>.AccessibleJList.AccessibleJListChild implements AccessibleAction {
         ActionableAccessibleJListChild(JList<E> var2, int var3) {
            super(var2, var3);
         }

         public AccessibleAction getAccessibleAction() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            if (var1 == null) {
               return null;
            } else {
               AccessibleAction var2 = var1.getAccessibleAction();
               return (AccessibleAction)(var2 != null ? var2 : this);
            }
         }

         public boolean doAccessibleAction(int var1) {
            if (var1 == 0) {
               JList.this.setSelectedIndex(this.indexInParent);
               return true;
            } else {
               return false;
            }
         }

         public String getAccessibleActionDescription(int var1) {
            return var1 == 0 ? UIManager.getString("AbstractButton.clickText") : null;
         }

         public int getAccessibleActionCount() {
            return 1;
         }
      }

      protected class AccessibleJListChild extends AccessibleContext implements Accessible, AccessibleComponent {
         private JList<E> parent = null;
         int indexInParent;
         private Component component = null;
         private AccessibleContext accessibleContext = null;
         private ListModel<E> listModel;
         private ListCellRenderer<? super E> cellRenderer = null;

         public AccessibleJListChild(JList<E> var2, int var3) {
            this.parent = var2;
            this.setAccessibleParent(var2);
            this.indexInParent = var3;
            if (var2 != null) {
               this.listModel = var2.getModel();
               this.cellRenderer = var2.getCellRenderer();
            }

         }

         private Component getCurrentComponent() {
            return this.getComponentAtIndex(this.indexInParent);
         }

         AccessibleContext getCurrentAccessibleContext() {
            Component var1 = this.getComponentAtIndex(this.indexInParent);
            return var1 instanceof Accessible ? var1.getAccessibleContext() : null;
         }

         private Component getComponentAtIndex(int var1) {
            if (var1 >= 0 && var1 < this.listModel.getSize()) {
               if (this.parent != null && this.listModel != null && this.cellRenderer != null) {
                  Object var2 = this.listModel.getElementAt(var1);
                  boolean var3 = this.parent.isSelectedIndex(var1);
                  boolean var4 = this.parent.isFocusOwner() && var1 == this.parent.getLeadSelectionIndex();
                  return this.cellRenderer.getListCellRendererComponent(this.parent, var2, var1, var3, var4);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }

         public AccessibleContext getAccessibleContext() {
            return this;
         }

         public String getAccessibleName() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleName() : null;
         }

         public void setAccessibleName(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleName(var1);
            }

         }

         public String getAccessibleDescription() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleDescription() : null;
         }

         public void setAccessibleDescription(String var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.setAccessibleDescription(var1);
            }

         }

         public AccessibleRole getAccessibleRole() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleRole() : null;
         }

         public AccessibleStateSet getAccessibleStateSet() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            AccessibleStateSet var2;
            if (var1 != null) {
               var2 = var1.getAccessibleStateSet();
            } else {
               var2 = new AccessibleStateSet();
            }

            var2.add(AccessibleState.SELECTABLE);
            if (this.parent.isFocusOwner() && this.indexInParent == this.parent.getLeadSelectionIndex()) {
               var2.add(AccessibleState.ACTIVE);
            }

            if (this.parent.isSelectedIndex(this.indexInParent)) {
               var2.add(AccessibleState.SELECTED);
            }

            if (this.isShowing()) {
               var2.add(AccessibleState.SHOWING);
            } else if (var2.contains(AccessibleState.SHOWING)) {
               var2.remove(AccessibleState.SHOWING);
            }

            if (this.isVisible()) {
               var2.add(AccessibleState.VISIBLE);
            } else if (var2.contains(AccessibleState.VISIBLE)) {
               var2.remove(AccessibleState.VISIBLE);
            }

            var2.add(AccessibleState.TRANSIENT);
            return var2;
         }

         public int getAccessibleIndexInParent() {
            return this.indexInParent;
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
            }

         }

         public void removePropertyChangeListener(PropertyChangeListener var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 != null) {
               var2.removePropertyChangeListener(var1);
            }

         }

         public AccessibleComponent getAccessibleComponent() {
            return this;
         }

         public AccessibleSelection getAccessibleSelection() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleSelection() : null;
         }

         public AccessibleText getAccessibleText() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleText() : null;
         }

         public AccessibleValue getAccessibleValue() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleValue() : null;
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
            int var1 = this.parent.getFirstVisibleIndex();
            int var2 = this.parent.getLastVisibleIndex();
            if (var2 == -1) {
               var2 = this.parent.getModel().getSize() - 1;
            }

            return this.indexInParent >= var1 && this.indexInParent <= var2;
         }

         public void setVisible(boolean var1) {
         }

         public boolean isShowing() {
            return this.parent.isShowing() && this.isVisible();
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
               Point var1;
               try {
                  var1 = this.parent.getLocationOnScreen();
               } catch (IllegalComponentStateException var3) {
                  return null;
               }

               Point var2 = this.parent.indexToLocation(this.indexInParent);
               if (var2 != null) {
                  var2.translate(var1.x, var1.y);
                  return var2;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }

         public Point getLocation() {
            return this.parent != null ? this.parent.indexToLocation(this.indexInParent) : null;
         }

         public void setLocation(Point var1) {
            if (this.parent != null && this.parent.contains(var1)) {
               JList.this.ensureIndexIsVisible(this.indexInParent);
            }

         }

         public Rectangle getBounds() {
            return this.parent != null ? this.parent.getCellBounds(this.indexInParent, this.indexInParent) : null;
         }

         public void setBounds(Rectangle var1) {
            AccessibleContext var2 = this.getCurrentAccessibleContext();
            if (var2 instanceof AccessibleComponent) {
               ((AccessibleComponent)var2).setBounds(var1);
            }

         }

         public Dimension getSize() {
            Rectangle var1 = this.getBounds();
            return var1 != null ? var1.getSize() : null;
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

         public AccessibleIcon[] getAccessibleIcon() {
            AccessibleContext var1 = this.getCurrentAccessibleContext();
            return var1 != null ? var1.getAccessibleIcon() : null;
         }
      }
   }

   private class ListSelectionHandler implements ListSelectionListener, Serializable {
      private ListSelectionHandler() {
      }

      public void valueChanged(ListSelectionEvent var1) {
         JList.this.fireSelectionValueChanged(var1.getFirstIndex(), var1.getLastIndex(), var1.getValueIsAdjusting());
      }

      // $FF: synthetic method
      ListSelectionHandler(Object var2) {
         this();
      }
   }

   public static final class DropLocation extends TransferHandler.DropLocation {
      private final int index;
      private final boolean isInsert;

      private DropLocation(Point var1, int var2, boolean var3) {
         super(var1);
         this.index = var2;
         this.isInsert = var3;
      }

      public int getIndex() {
         return this.index;
      }

      public boolean isInsert() {
         return this.isInsert;
      }

      public String toString() {
         return this.getClass().getName() + "[dropPoint=" + this.getDropPoint() + ",index=" + this.index + ",insert=" + this.isInsert + "]";
      }

      // $FF: synthetic method
      DropLocation(Point var1, int var2, boolean var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
