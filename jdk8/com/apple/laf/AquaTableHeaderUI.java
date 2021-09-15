package com.apple.laf;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class AquaTableHeaderUI extends BasicTableHeaderUI {
   private int originalHeaderAlignment;
   protected int sortColumn;
   protected int sortOrder;
   static final AquaUtils.RecyclableSingleton<ClientPropertyApplicator<JTableHeader, JTableHeader>> TABLE_HEADER_APPLICATORS = new AquaUtils.RecyclableSingleton<ClientPropertyApplicator<JTableHeader, JTableHeader>>() {
      protected ClientPropertyApplicator<JTableHeader, JTableHeader> getInstance() {
         return new ClientPropertyApplicator(new ClientPropertyApplicator.Property[]{new ClientPropertyApplicator.Property<JTableHeader>("JTableHeader.selectedColumn") {
            public void applyProperty(JTableHeader var1, Object var2) {
               AquaTableHeaderUI.tickle(var1, var2, var1.getClientProperty("JTableHeader.sortDirection"));
            }
         }, new ClientPropertyApplicator.Property<JTableHeader>("JTableHeader.sortDirection") {
            public void applyProperty(JTableHeader var1, Object var2) {
               AquaTableHeaderUI.tickle(var1, var1.getClientProperty("JTableHeader.selectedColumn"), var2);
            }
         }});
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTableHeaderUI();
   }

   public void installDefaults() {
      super.installDefaults();
      TableCellRenderer var1 = this.header.getDefaultRenderer();
      if (var1 instanceof UIResource && var1 instanceof DefaultTableCellRenderer) {
         DefaultTableCellRenderer var2 = (DefaultTableCellRenderer)var1;
         this.originalHeaderAlignment = var2.getHorizontalAlignment();
         var2.setHorizontalAlignment(10);
      }

   }

   public void uninstallDefaults() {
      TableCellRenderer var1 = this.header.getDefaultRenderer();
      if (var1 instanceof UIResource && var1 instanceof DefaultTableCellRenderer) {
         DefaultTableCellRenderer var2 = (DefaultTableCellRenderer)var1;
         var2.setHorizontalAlignment(this.originalHeaderAlignment);
      }

      super.uninstallDefaults();
   }

   static ClientPropertyApplicator<JTableHeader, JTableHeader> getTableHeaderApplicators() {
      return (ClientPropertyApplicator)TABLE_HEADER_APPLICATORS.get();
   }

   static void tickle(JTableHeader var0, Object var1, Object var2) {
      TableColumn var3 = getTableColumn(var0, var1);
      if (var3 != null) {
         byte var4 = 0;
         if ("ascending".equalsIgnoreCase(var2 + "")) {
            var4 = 1;
         } else if ("descending".equalsIgnoreCase(var2 + "")) {
            var4 = -1;
         } else if ("decending".equalsIgnoreCase(var2 + "")) {
            var4 = -1;
         }

         TableHeaderUI var5 = var0.getUI();
         if (var5 != null && var5 instanceof AquaTableHeaderUI) {
            AquaTableHeaderUI var6 = (AquaTableHeaderUI)var5;
            var6.sortColumn = var3.getModelIndex();
            var6.sortOrder = var4;
            AquaTableHeaderUI.AquaTableCellRenderer var7 = var6.new AquaTableCellRenderer();
            var3.setHeaderRenderer(var7);
         }
      }
   }

   protected static TableColumn getTableColumn(JTableHeader var0, Object var1) {
      if (var1 != null && var1 instanceof Integer) {
         int var2 = (Integer)var1;
         TableColumnModel var3 = var0.getColumnModel();
         return var2 >= 0 && var2 < var3.getColumnCount() ? var3.getColumn(var2) : null;
      } else {
         return null;
      }
   }

   protected static AquaTableHeaderBorder getAquaBorderFrom(JTableHeader var0, TableColumn var1) {
      TableCellRenderer var2 = var1.getHeaderRenderer();
      if (var2 == null) {
         return null;
      } else {
         Component var3 = var2.getTableCellRendererComponent(var0.getTable(), var1.getHeaderValue(), false, false, -1, var1.getModelIndex());
         if (!(var3 instanceof JComponent)) {
            return null;
         } else {
            Border var4 = ((JComponent)var3).getBorder();
            return !(var4 instanceof AquaTableHeaderBorder) ? null : (AquaTableHeaderBorder)var4;
         }
      }
   }

   protected void installListeners() {
      super.installListeners();
      getTableHeaderApplicators().attachAndApplyClientProperties(this.header);
   }

   protected void uninstallListeners() {
      getTableHeaderApplicators().removeFrom(this.header);
      super.uninstallListeners();
   }

   private int getHeaderHeightAqua() {
      int var1 = 0;
      boolean var2 = false;
      TableColumnModel var3 = this.header.getColumnModel();

      for(int var4 = 0; var4 < var3.getColumnCount(); ++var4) {
         TableColumn var5 = var3.getColumn(var4);
         if (var5.getHeaderRenderer() != null || !var2) {
            Component var6 = this.getHeaderRendererAqua(var4);
            int var7 = var6.getPreferredSize().height;
            var1 = Math.max(var1, var7);
            if (var7 > 4) {
               var2 = true;
            }
         }
      }

      return var1;
   }

   private Component getHeaderRendererAqua(int var1) {
      TableColumn var2 = this.header.getColumnModel().getColumn(var1);
      TableCellRenderer var3 = var2.getHeaderRenderer();
      if (var3 == null) {
         var3 = this.header.getDefaultRenderer();
      }

      return var3.getTableCellRendererComponent(this.header.getTable(), var2.getHeaderValue(), false, false, -1, var1);
   }

   private Dimension createHeaderSizeAqua(long var1) {
      if (var1 > 2147483647L) {
         var1 = 2147483647L;
      }

      return new Dimension((int)var1, this.getHeaderHeightAqua());
   }

   public Dimension getMinimumSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.header.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getMinWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createHeaderSizeAqua(var2);
   }

   public Dimension getPreferredSize(JComponent var1) {
      long var2 = 0L;

      TableColumn var5;
      for(Enumeration var4 = this.header.getColumnModel().getColumns(); var4.hasMoreElements(); var2 += (long)var5.getPreferredWidth()) {
         var5 = (TableColumn)var4.nextElement();
      }

      return this.createHeaderSizeAqua(var2);
   }

   class AquaTableCellRenderer extends DefaultTableCellRenderer implements UIResource {
      public Component getTableCellRendererComponent(JTable var1, Object var2, boolean var3, boolean var4, int var5, int var6) {
         if (var1 != null && AquaTableHeaderUI.this.header != null) {
            this.setForeground(AquaTableHeaderUI.this.header.getForeground());
            this.setBackground(AquaTableHeaderUI.this.header.getBackground());
            this.setFont(UIManager.getFont("TableHeader.font"));
         }

         this.setText(var2 == null ? "" : var2.toString());
         AquaTableHeaderBorder var7 = AquaTableHeaderBorder.getListHeaderBorder();
         boolean var8 = var1.getColumnModel().getColumn(var6).getModelIndex() == AquaTableHeaderUI.this.sortColumn;
         var7.setSelected(var8);
         if (var8) {
            var7.setSortOrder(AquaTableHeaderUI.this.sortOrder);
         } else {
            var7.setSortOrder(0);
         }

         this.setBorder(var7);
         return this;
      }
   }
}
