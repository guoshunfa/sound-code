package sun.awt.shell;

import java.util.Comparator;
import javax.swing.SortOrder;

public class ShellFolderColumnInfo {
   private String title;
   private Integer width;
   private boolean visible;
   private Integer alignment;
   private SortOrder sortOrder;
   private Comparator comparator;
   private boolean compareByColumn;

   public ShellFolderColumnInfo(String var1, Integer var2, Integer var3, boolean var4, SortOrder var5, Comparator var6, boolean var7) {
      this.title = var1;
      this.width = var2;
      this.alignment = var3;
      this.visible = var4;
      this.sortOrder = var5;
      this.comparator = var6;
      this.compareByColumn = var7;
   }

   public ShellFolderColumnInfo(String var1, Integer var2, Integer var3, boolean var4, SortOrder var5, Comparator var6) {
      this(var1, var2, var3, var4, var5, var6, false);
   }

   public ShellFolderColumnInfo(String var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, (SortOrder)null, (Comparator)null);
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public Integer getWidth() {
      return this.width;
   }

   public void setWidth(Integer var1) {
      this.width = var1;
   }

   public Integer getAlignment() {
      return this.alignment;
   }

   public void setAlignment(Integer var1) {
      this.alignment = var1;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public SortOrder getSortOrder() {
      return this.sortOrder;
   }

   public void setSortOrder(SortOrder var1) {
      this.sortOrder = var1;
   }

   public Comparator getComparator() {
      return this.comparator;
   }

   public void setComparator(Comparator var1) {
      this.comparator = var1;
   }

   public boolean isCompareByColumn() {
      return this.compareByColumn;
   }

   public void setCompareByColumn(boolean var1) {
      this.compareByColumn = var1;
   }
}
