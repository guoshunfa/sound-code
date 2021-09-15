package javax.swing.table;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

public class TableColumn implements Serializable {
   public static final String COLUMN_WIDTH_PROPERTY = "columWidth";
   public static final String HEADER_VALUE_PROPERTY = "headerValue";
   public static final String HEADER_RENDERER_PROPERTY = "headerRenderer";
   public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
   protected int modelIndex;
   protected Object identifier;
   protected int width;
   protected int minWidth;
   private int preferredWidth;
   protected int maxWidth;
   protected TableCellRenderer headerRenderer;
   protected Object headerValue;
   protected TableCellRenderer cellRenderer;
   protected TableCellEditor cellEditor;
   protected boolean isResizable;
   /** @deprecated */
   @Deprecated
   protected transient int resizedPostingDisableCount;
   private SwingPropertyChangeSupport changeSupport;

   public TableColumn() {
      this(0);
   }

   public TableColumn(int var1) {
      this(var1, 75, (TableCellRenderer)null, (TableCellEditor)null);
   }

   public TableColumn(int var1, int var2) {
      this(var1, var2, (TableCellRenderer)null, (TableCellEditor)null);
   }

   public TableColumn(int var1, int var2, TableCellRenderer var3, TableCellEditor var4) {
      this.modelIndex = var1;
      this.preferredWidth = this.width = Math.max(var2, 0);
      this.cellRenderer = var3;
      this.cellEditor = var4;
      this.minWidth = Math.min(15, this.width);
      this.maxWidth = Integer.MAX_VALUE;
      this.isResizable = true;
      this.resizedPostingDisableCount = 0;
      this.headerValue = null;
   }

   private void firePropertyChange(String var1, Object var2, Object var3) {
      if (this.changeSupport != null) {
         this.changeSupport.firePropertyChange(var1, var2, var3);
      }

   }

   private void firePropertyChange(String var1, int var2, int var3) {
      if (var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }

   }

   private void firePropertyChange(String var1, boolean var2, boolean var3) {
      if (var2 != var3) {
         this.firePropertyChange(var1, var2, var3);
      }

   }

   public void setModelIndex(int var1) {
      int var2 = this.modelIndex;
      this.modelIndex = var1;
      this.firePropertyChange("modelIndex", var2, var1);
   }

   public int getModelIndex() {
      return this.modelIndex;
   }

   public void setIdentifier(Object var1) {
      Object var2 = this.identifier;
      this.identifier = var1;
      this.firePropertyChange("identifier", var2, var1);
   }

   public Object getIdentifier() {
      return this.identifier != null ? this.identifier : this.getHeaderValue();
   }

   public void setHeaderValue(Object var1) {
      Object var2 = this.headerValue;
      this.headerValue = var1;
      this.firePropertyChange("headerValue", var2, var1);
   }

   public Object getHeaderValue() {
      return this.headerValue;
   }

   public void setHeaderRenderer(TableCellRenderer var1) {
      TableCellRenderer var2 = this.headerRenderer;
      this.headerRenderer = var1;
      this.firePropertyChange("headerRenderer", var2, var1);
   }

   public TableCellRenderer getHeaderRenderer() {
      return this.headerRenderer;
   }

   public void setCellRenderer(TableCellRenderer var1) {
      TableCellRenderer var2 = this.cellRenderer;
      this.cellRenderer = var1;
      this.firePropertyChange("cellRenderer", var2, var1);
   }

   public TableCellRenderer getCellRenderer() {
      return this.cellRenderer;
   }

   public void setCellEditor(TableCellEditor var1) {
      TableCellEditor var2 = this.cellEditor;
      this.cellEditor = var1;
      this.firePropertyChange("cellEditor", var2, var1);
   }

   public TableCellEditor getCellEditor() {
      return this.cellEditor;
   }

   public void setWidth(int var1) {
      int var2 = this.width;
      this.width = Math.min(Math.max(var1, this.minWidth), this.maxWidth);
      this.firePropertyChange("width", var2, this.width);
   }

   public int getWidth() {
      return this.width;
   }

   public void setPreferredWidth(int var1) {
      int var2 = this.preferredWidth;
      this.preferredWidth = Math.min(Math.max(var1, this.minWidth), this.maxWidth);
      this.firePropertyChange("preferredWidth", var2, this.preferredWidth);
   }

   public int getPreferredWidth() {
      return this.preferredWidth;
   }

   public void setMinWidth(int var1) {
      int var2 = this.minWidth;
      this.minWidth = Math.max(Math.min(var1, this.maxWidth), 0);
      if (this.width < this.minWidth) {
         this.setWidth(this.minWidth);
      }

      if (this.preferredWidth < this.minWidth) {
         this.setPreferredWidth(this.minWidth);
      }

      this.firePropertyChange("minWidth", var2, this.minWidth);
   }

   public int getMinWidth() {
      return this.minWidth;
   }

   public void setMaxWidth(int var1) {
      int var2 = this.maxWidth;
      this.maxWidth = Math.max(this.minWidth, var1);
      if (this.width > this.maxWidth) {
         this.setWidth(this.maxWidth);
      }

      if (this.preferredWidth > this.maxWidth) {
         this.setPreferredWidth(this.maxWidth);
      }

      this.firePropertyChange("maxWidth", var2, this.maxWidth);
   }

   public int getMaxWidth() {
      return this.maxWidth;
   }

   public void setResizable(boolean var1) {
      boolean var2 = this.isResizable;
      this.isResizable = var1;
      this.firePropertyChange("isResizable", var2, this.isResizable);
   }

   public boolean getResizable() {
      return this.isResizable;
   }

   public void sizeWidthToFit() {
      if (this.headerRenderer != null) {
         Component var1 = this.headerRenderer.getTableCellRendererComponent((JTable)null, this.getHeaderValue(), false, false, 0, 0);
         this.setMinWidth(var1.getMinimumSize().width);
         this.setMaxWidth(var1.getMaximumSize().width);
         this.setPreferredWidth(var1.getPreferredSize().width);
         this.setWidth(this.getPreferredWidth());
      }
   }

   /** @deprecated */
   @Deprecated
   public void disableResizedPosting() {
      ++this.resizedPostingDisableCount;
   }

   /** @deprecated */
   @Deprecated
   public void enableResizedPosting() {
      --this.resizedPostingDisableCount;
   }

   public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new SwingPropertyChangeSupport(this);
      }

      this.changeSupport.addPropertyChangeListener(var1);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1);
      }

   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   protected TableCellRenderer createDefaultHeaderRenderer() {
      DefaultTableCellRenderer var1 = new DefaultTableCellRenderer() {
         public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
            if (var1 != null) {
               JTableHeader var7 = var1.getTableHeader();
               if (var7 != null) {
                  this.setForeground(var7.getForeground());
                  this.setBackground(var7.getBackground());
                  this.setFont(var7.getFont());
               }
            }

            this.setText(var2 == null ? "" : var2.toString());
            this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return this;
         }
      };
      var1.setHorizontalAlignment(0);
      return var1;
   }
}
