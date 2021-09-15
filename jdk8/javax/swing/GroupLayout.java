package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupLayout implements LayoutManager2 {
   private static final int MIN_SIZE = 0;
   private static final int PREF_SIZE = 1;
   private static final int MAX_SIZE = 2;
   private static final int SPECIFIC_SIZE = 3;
   private static final int UNSET = Integer.MIN_VALUE;
   public static final int DEFAULT_SIZE = -1;
   public static final int PREFERRED_SIZE = -2;
   private boolean autocreatePadding;
   private boolean autocreateContainerPadding;
   private GroupLayout.Group horizontalGroup;
   private GroupLayout.Group verticalGroup;
   private Map<Component, GroupLayout.ComponentInfo> componentInfos;
   private Container host;
   private Set<GroupLayout.Spring> tmpParallelSet;
   private boolean springsChanged;
   private boolean isValid;
   private boolean hasPreferredPaddingSprings;
   private LayoutStyle layoutStyle;
   private boolean honorsVisibility;

   private static void checkSize(int var0, int var1, int var2, boolean var3) {
      checkResizeType(var0, var3);
      if (!var3 && var1 < 0) {
         throw new IllegalArgumentException("Pref must be >= 0");
      } else {
         if (var3) {
            checkResizeType(var1, true);
         }

         checkResizeType(var2, var3);
         checkLessThan(var0, var1);
         checkLessThan(var1, var2);
      }
   }

   private static void checkResizeType(int var0, boolean var1) {
      if (var0 < 0 && (var1 && var0 != -1 && var0 != -2 || !var1 && var0 != -2)) {
         throw new IllegalArgumentException("Invalid size");
      }
   }

   private static void checkLessThan(int var0, int var1) {
      if (var0 >= 0 && var1 >= 0 && var0 > var1) {
         throw new IllegalArgumentException("Following is not met: min<=pref<=max");
      }
   }

   public GroupLayout(Container var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Container must be non-null");
      } else {
         this.honorsVisibility = true;
         this.host = var1;
         this.setHorizontalGroup(this.createParallelGroup(GroupLayout.Alignment.LEADING, true));
         this.setVerticalGroup(this.createParallelGroup(GroupLayout.Alignment.LEADING, true));
         this.componentInfos = new HashMap();
         this.tmpParallelSet = new HashSet();
      }
   }

   public void setHonorsVisibility(boolean var1) {
      if (this.honorsVisibility != var1) {
         this.honorsVisibility = var1;
         this.springsChanged = true;
         this.isValid = false;
         this.invalidateHost();
      }

   }

   public boolean getHonorsVisibility() {
      return this.honorsVisibility;
   }

   public void setHonorsVisibility(Component var1, Boolean var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Component must be non-null");
      } else {
         this.getComponentInfo(var1).setHonorsVisibility(var2);
         this.springsChanged = true;
         this.isValid = false;
         this.invalidateHost();
      }
   }

   public void setAutoCreateGaps(boolean var1) {
      if (this.autocreatePadding != var1) {
         this.autocreatePadding = var1;
         this.invalidateHost();
      }

   }

   public boolean getAutoCreateGaps() {
      return this.autocreatePadding;
   }

   public void setAutoCreateContainerGaps(boolean var1) {
      if (this.autocreateContainerPadding != var1) {
         this.autocreateContainerPadding = var1;
         this.horizontalGroup = this.createTopLevelGroup(this.getHorizontalGroup());
         this.verticalGroup = this.createTopLevelGroup(this.getVerticalGroup());
         this.invalidateHost();
      }

   }

   public boolean getAutoCreateContainerGaps() {
      return this.autocreateContainerPadding;
   }

   public void setHorizontalGroup(GroupLayout.Group var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Group must be non-null");
      } else {
         this.horizontalGroup = this.createTopLevelGroup(var1);
         this.invalidateHost();
      }
   }

   private GroupLayout.Group getHorizontalGroup() {
      byte var1 = 0;
      if (this.horizontalGroup.springs.size() > 1) {
         var1 = 1;
      }

      return (GroupLayout.Group)this.horizontalGroup.springs.get(var1);
   }

   public void setVerticalGroup(GroupLayout.Group var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Group must be non-null");
      } else {
         this.verticalGroup = this.createTopLevelGroup(var1);
         this.invalidateHost();
      }
   }

   private GroupLayout.Group getVerticalGroup() {
      byte var1 = 0;
      if (this.verticalGroup.springs.size() > 1) {
         var1 = 1;
      }

      return (GroupLayout.Group)this.verticalGroup.springs.get(var1);
   }

   private GroupLayout.Group createTopLevelGroup(GroupLayout.Group var1) {
      GroupLayout.SequentialGroup var2 = this.createSequentialGroup();
      if (this.getAutoCreateContainerGaps()) {
         var2.addSpring(new GroupLayout.ContainerAutoPreferredGapSpring());
         var2.addGroup(var1);
         var2.addSpring(new GroupLayout.ContainerAutoPreferredGapSpring());
      } else {
         var2.addGroup(var1);
      }

      return var2;
   }

   public GroupLayout.SequentialGroup createSequentialGroup() {
      return new GroupLayout.SequentialGroup();
   }

   public GroupLayout.ParallelGroup createParallelGroup() {
      return this.createParallelGroup(GroupLayout.Alignment.LEADING);
   }

   public GroupLayout.ParallelGroup createParallelGroup(GroupLayout.Alignment var1) {
      return this.createParallelGroup(var1, true);
   }

   public GroupLayout.ParallelGroup createParallelGroup(GroupLayout.Alignment var1, boolean var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("alignment must be non null");
      } else {
         return (GroupLayout.ParallelGroup)(var1 == GroupLayout.Alignment.BASELINE ? new GroupLayout.BaselineGroup(var2) : new GroupLayout.ParallelGroup(var1, var2));
      }
   }

   public GroupLayout.ParallelGroup createBaselineGroup(boolean var1, boolean var2) {
      return new GroupLayout.BaselineGroup(var1, var2);
   }

   public void linkSize(Component... var1) {
      this.linkSize(0, var1);
      this.linkSize(1, var1);
   }

   public void linkSize(int var1, Component... var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("Components must be non-null");
      } else {
         for(int var3 = var2.length - 1; var3 >= 0; --var3) {
            Component var4 = var2[var3];
            if (var2[var3] == null) {
               throw new IllegalArgumentException("Components must be non-null");
            }

            this.getComponentInfo(var4);
         }

         byte var6;
         if (var1 == 0) {
            var6 = 0;
         } else {
            if (var1 != 1) {
               throw new IllegalArgumentException("Axis must be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL");
            }

            var6 = 1;
         }

         GroupLayout.LinkInfo var7 = this.getComponentInfo(var2[var2.length - 1]).getLinkInfo(var6);

         for(int var5 = var2.length - 2; var5 >= 0; --var5) {
            var7.add(this.getComponentInfo(var2[var5]));
         }

         this.invalidateHost();
      }
   }

   public void replace(Component var1, Component var2) {
      if (var1 != null && var2 != null) {
         if (this.springsChanged) {
            this.registerComponents(this.horizontalGroup, 0);
            this.registerComponents(this.verticalGroup, 1);
         }

         GroupLayout.ComponentInfo var3 = (GroupLayout.ComponentInfo)this.componentInfos.remove(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Component must already exist");
         } else {
            this.host.remove(var1);
            if (var2.getParent() != this.host) {
               this.host.add(var2);
            }

            var3.setComponent(var2);
            this.componentInfos.put(var2, var3);
            this.invalidateHost();
         }
      } else {
         throw new IllegalArgumentException("Components must be non-null");
      }
   }

   public void setLayoutStyle(LayoutStyle var1) {
      this.layoutStyle = var1;
      this.invalidateHost();
   }

   public LayoutStyle getLayoutStyle() {
      return this.layoutStyle;
   }

   private LayoutStyle getLayoutStyle0() {
      LayoutStyle var1 = this.getLayoutStyle();
      if (var1 == null) {
         var1 = LayoutStyle.getInstance();
      }

      return var1;
   }

   private void invalidateHost() {
      if (this.host instanceof JComponent) {
         ((JComponent)this.host).revalidate();
      } else {
         this.host.invalidate();
      }

      this.host.repaint();
   }

   public void addLayoutComponent(String var1, Component var2) {
   }

   public void removeLayoutComponent(Component var1) {
      GroupLayout.ComponentInfo var2 = (GroupLayout.ComponentInfo)this.componentInfos.remove(var1);
      if (var2 != null) {
         var2.dispose();
         this.springsChanged = true;
         this.isValid = false;
      }

   }

   public Dimension preferredLayoutSize(Container var1) {
      this.checkParent(var1);
      this.prepare(1);
      return this.adjustSize(this.horizontalGroup.getPreferredSize(0), this.verticalGroup.getPreferredSize(1));
   }

   public Dimension minimumLayoutSize(Container var1) {
      this.checkParent(var1);
      this.prepare(0);
      return this.adjustSize(this.horizontalGroup.getMinimumSize(0), this.verticalGroup.getMinimumSize(1));
   }

   public void layoutContainer(Container var1) {
      this.prepare(3);
      Insets var2 = var1.getInsets();
      int var3 = var1.getWidth() - var2.left - var2.right;
      int var4 = var1.getHeight() - var2.top - var2.bottom;
      boolean var5 = this.isLeftToRight();
      if (this.getAutoCreateGaps() || this.getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings) {
         this.calculateAutopadding(this.horizontalGroup, 0, 3, 0, var3);
         this.calculateAutopadding(this.verticalGroup, 1, 3, 0, var4);
      }

      this.horizontalGroup.setSize(0, 0, var3);
      this.verticalGroup.setSize(1, 0, var4);
      Iterator var6 = this.componentInfos.values().iterator();

      while(var6.hasNext()) {
         GroupLayout.ComponentInfo var7 = (GroupLayout.ComponentInfo)var6.next();
         var7.setBounds(var2, var3, var5);
      }

   }

   public void addLayoutComponent(Component var1, Object var2) {
   }

   public Dimension maximumLayoutSize(Container var1) {
      this.checkParent(var1);
      this.prepare(2);
      return this.adjustSize(this.horizontalGroup.getMaximumSize(0), this.verticalGroup.getMaximumSize(1));
   }

   public float getLayoutAlignmentX(Container var1) {
      this.checkParent(var1);
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container var1) {
      this.checkParent(var1);
      return 0.5F;
   }

   public void invalidateLayout(Container var1) {
      this.checkParent(var1);
      synchronized(var1.getTreeLock()) {
         this.isValid = false;
      }
   }

   private void prepare(int var1) {
      boolean var2 = false;
      if (!this.isValid) {
         this.isValid = true;
         this.horizontalGroup.setSize(0, Integer.MIN_VALUE, Integer.MIN_VALUE);
         this.verticalGroup.setSize(1, Integer.MIN_VALUE, Integer.MIN_VALUE);

         GroupLayout.ComponentInfo var4;
         for(Iterator var3 = this.componentInfos.values().iterator(); var3.hasNext(); var4.clearCachedSize()) {
            var4 = (GroupLayout.ComponentInfo)var3.next();
            if (var4.updateVisibility()) {
               var2 = true;
            }
         }
      }

      if (this.springsChanged) {
         this.registerComponents(this.horizontalGroup, 0);
         this.registerComponents(this.verticalGroup, 1);
      }

      if (this.springsChanged || var2) {
         this.checkComponents();
         this.horizontalGroup.removeAutopadding();
         this.verticalGroup.removeAutopadding();
         if (this.getAutoCreateGaps()) {
            this.insertAutopadding(true);
         } else if (this.hasPreferredPaddingSprings || this.getAutoCreateContainerGaps()) {
            this.insertAutopadding(false);
         }

         this.springsChanged = false;
      }

      if (var1 != 3 && (this.getAutoCreateGaps() || this.getAutoCreateContainerGaps() || this.hasPreferredPaddingSprings)) {
         this.calculateAutopadding(this.horizontalGroup, 0, var1, 0, 0);
         this.calculateAutopadding(this.verticalGroup, 1, var1, 0, 0);
      }

   }

   private void calculateAutopadding(GroupLayout.Group var1, int var2, int var3, int var4, int var5) {
      var1.unsetAutopadding();
      switch(var3) {
      case 0:
         var5 = var1.getMinimumSize(var2);
         break;
      case 1:
         var5 = var1.getPreferredSize(var2);
         break;
      case 2:
         var5 = var1.getMaximumSize(var2);
      }

      var1.setSize(var2, var4, var5);
      var1.calculateAutopadding(var2);
   }

   private void checkComponents() {
      Iterator var1 = this.componentInfos.values().iterator();

      GroupLayout.ComponentInfo var2;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = (GroupLayout.ComponentInfo)var1.next();
         if (var2.horizontalSpring == null) {
            throw new IllegalStateException(var2.component + " is not attached to a horizontal group");
         }
      } while(var2.verticalSpring != null);

      throw new IllegalStateException(var2.component + " is not attached to a vertical group");
   }

   private void registerComponents(GroupLayout.Group var1, int var2) {
      List var3 = var1.springs;

      for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
         GroupLayout.Spring var5 = (GroupLayout.Spring)var3.get(var4);
         if (var5 instanceof GroupLayout.ComponentSpring) {
            ((GroupLayout.ComponentSpring)var5).installIfNecessary(var2);
         } else if (var5 instanceof GroupLayout.Group) {
            this.registerComponents((GroupLayout.Group)var5, var2);
         }
      }

   }

   private Dimension adjustSize(int var1, int var2) {
      Insets var3 = this.host.getInsets();
      return new Dimension(var1 + var3.left + var3.right, var2 + var3.top + var3.bottom);
   }

   private void checkParent(Container var1) {
      if (var1 != this.host) {
         throw new IllegalArgumentException("GroupLayout can only be used with one Container at a time");
      }
   }

   private GroupLayout.ComponentInfo getComponentInfo(Component var1) {
      GroupLayout.ComponentInfo var2 = (GroupLayout.ComponentInfo)this.componentInfos.get(var1);
      if (var2 == null) {
         var2 = new GroupLayout.ComponentInfo(var1);
         this.componentInfos.put(var1, var2);
         if (var1.getParent() != this.host) {
            this.host.add(var1);
         }
      }

      return var2;
   }

   private void insertAutopadding(boolean var1) {
      this.horizontalGroup.insertAutopadding(0, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), var1);
      this.verticalGroup.insertAutopadding(1, new ArrayList(1), new ArrayList(1), new ArrayList(1), new ArrayList(1), var1);
   }

   private boolean areParallelSiblings(Component var1, Component var2, int var3) {
      GroupLayout.ComponentInfo var4 = this.getComponentInfo(var1);
      GroupLayout.ComponentInfo var5 = this.getComponentInfo(var2);
      GroupLayout.ComponentSpring var6;
      GroupLayout.ComponentSpring var7;
      if (var3 == 0) {
         var6 = var4.horizontalSpring;
         var7 = var5.horizontalSpring;
      } else {
         var6 = var4.verticalSpring;
         var7 = var5.verticalSpring;
      }

      Set var8 = this.tmpParallelSet;
      var8.clear();

      GroupLayout.Spring var9;
      for(var9 = var6.getParent(); var9 != null; var9 = var9.getParent()) {
         var8.add(var9);
      }

      for(var9 = var7.getParent(); var9 != null; var9 = var9.getParent()) {
         if (var8.contains(var9)) {
            var8.clear();

            while(var9 != null) {
               if (var9 instanceof GroupLayout.ParallelGroup) {
                  return true;
               }

               var9 = var9.getParent();
            }

            return false;
         }
      }

      var8.clear();
      return false;
   }

   private boolean isLeftToRight() {
      return this.host.getComponentOrientation().isLeftToRight();
   }

   public String toString() {
      if (this.springsChanged) {
         this.registerComponents(this.horizontalGroup, 0);
         this.registerComponents(this.verticalGroup, 1);
      }

      StringBuffer var1 = new StringBuffer();
      var1.append("HORIZONTAL\n");
      this.createSpringDescription(var1, this.horizontalGroup, "  ", 0);
      var1.append("\nVERTICAL\n");
      this.createSpringDescription(var1, this.verticalGroup, "  ", 1);
      return var1.toString();
   }

   private void createSpringDescription(StringBuffer var1, GroupLayout.Spring var2, String var3, int var4) {
      String var5 = "";
      String var6 = "";
      if (var2 instanceof GroupLayout.ComponentSpring) {
         GroupLayout.ComponentSpring var7 = (GroupLayout.ComponentSpring)var2;
         var5 = Integer.toString(var7.getOrigin()) + " ";
         String var8 = var7.getComponent().getName();
         if (var8 != null) {
            var5 = "name=" + var8 + ", ";
         }
      }

      if (var2 instanceof GroupLayout.AutoPreferredGapSpring) {
         GroupLayout.AutoPreferredGapSpring var9 = (GroupLayout.AutoPreferredGapSpring)var2;
         var6 = ", userCreated=" + var9.getUserCreated() + ", matches=" + var9.getMatchDescription();
      }

      var1.append(var3 + var2.getClass().getName() + " " + Integer.toHexString(var2.hashCode()) + " " + var5 + ", size=" + var2.getSize() + ", alignment=" + var2.getAlignment() + " prefs=[" + var2.getMinimumSize(var4) + " " + var2.getPreferredSize(var4) + " " + var2.getMaximumSize(var4) + var6 + "]\n");
      if (var2 instanceof GroupLayout.Group) {
         List var10 = ((GroupLayout.Group)var2).springs;
         var3 = var3 + "  ";

         for(int var11 = 0; var11 < var10.size(); ++var11) {
            this.createSpringDescription(var1, (GroupLayout.Spring)var10.get(var11), var3, var4);
         }
      }

   }

   private class ComponentInfo {
      private Component component;
      GroupLayout.ComponentSpring horizontalSpring;
      GroupLayout.ComponentSpring verticalSpring;
      private GroupLayout.LinkInfo horizontalMaster;
      private GroupLayout.LinkInfo verticalMaster;
      private boolean visible;
      private Boolean honorsVisibility;

      ComponentInfo(Component var2) {
         this.component = var2;
         this.updateVisibility();
      }

      public void dispose() {
         this.removeSpring(this.horizontalSpring);
         this.horizontalSpring = null;
         this.removeSpring(this.verticalSpring);
         this.verticalSpring = null;
         if (this.horizontalMaster != null) {
            this.horizontalMaster.remove(this);
         }

         if (this.verticalMaster != null) {
            this.verticalMaster.remove(this);
         }

      }

      void setHonorsVisibility(Boolean var1) {
         this.honorsVisibility = var1;
      }

      private void removeSpring(GroupLayout.Spring var1) {
         if (var1 != null) {
            ((GroupLayout.Group)var1.getParent()).springs.remove(var1);
         }

      }

      public boolean isVisible() {
         return this.visible;
      }

      boolean updateVisibility() {
         boolean var1;
         if (this.honorsVisibility == null) {
            var1 = GroupLayout.this.getHonorsVisibility();
         } else {
            var1 = this.honorsVisibility;
         }

         boolean var2 = var1 ? this.component.isVisible() : true;
         if (this.visible != var2) {
            this.visible = var2;
            return true;
         } else {
            return false;
         }
      }

      public void setBounds(Insets var1, int var2, boolean var3) {
         int var4 = this.horizontalSpring.getOrigin();
         int var5 = this.horizontalSpring.getSize();
         int var6 = this.verticalSpring.getOrigin();
         int var7 = this.verticalSpring.getSize();
         if (!var3) {
            var4 = var2 - var4 - var5;
         }

         this.component.setBounds(var4 + var1.left, var6 + var1.top, var5, var7);
      }

      public void setComponent(Component var1) {
         this.component = var1;
         if (this.horizontalSpring != null) {
            this.horizontalSpring.setComponent(var1);
         }

         if (this.verticalSpring != null) {
            this.verticalSpring.setComponent(var1);
         }

      }

      public Component getComponent() {
         return this.component;
      }

      public boolean isLinked(int var1) {
         if (var1 == 0) {
            return this.horizontalMaster != null;
         } else {
            assert var1 == 1;

            return this.verticalMaster != null;
         }
      }

      private void setLinkInfo(int var1, GroupLayout.LinkInfo var2) {
         if (var1 == 0) {
            this.horizontalMaster = var2;
         } else {
            assert var1 == 1;

            this.verticalMaster = var2;
         }

      }

      public GroupLayout.LinkInfo getLinkInfo(int var1) {
         return this.getLinkInfo(var1, true);
      }

      private GroupLayout.LinkInfo getLinkInfo(int var1, boolean var2) {
         if (var1 == 0) {
            if (this.horizontalMaster == null && var2) {
               (new GroupLayout.LinkInfo(0)).add(this);
            }

            return this.horizontalMaster;
         } else {
            assert var1 == 1;

            if (this.verticalMaster == null && var2) {
               (new GroupLayout.LinkInfo(1)).add(this);
            }

            return this.verticalMaster;
         }
      }

      public void clearCachedSize() {
         if (this.horizontalMaster != null) {
            this.horizontalMaster.clearCachedSize();
         }

         if (this.verticalMaster != null) {
            this.verticalMaster.clearCachedSize();
         }

      }

      int getLinkSize(int var1, int var2) {
         if (var1 == 0) {
            return this.horizontalMaster.getSize(var1);
         } else {
            assert var1 == 1;

            return this.verticalMaster.getSize(var1);
         }
      }
   }

   private static class LinkInfo {
      private final int axis;
      private final List<GroupLayout.ComponentInfo> linked = new ArrayList();
      private int size = Integer.MIN_VALUE;

      LinkInfo(int var1) {
         this.axis = var1;
      }

      public void add(GroupLayout.ComponentInfo var1) {
         GroupLayout.LinkInfo var2 = var1.getLinkInfo(this.axis, false);
         if (var2 == null) {
            this.linked.add(var1);
            var1.setLinkInfo(this.axis, this);
         } else if (var2 != this) {
            this.linked.addAll(var2.linked);
            Iterator var3 = var2.linked.iterator();

            while(var3.hasNext()) {
               GroupLayout.ComponentInfo var4 = (GroupLayout.ComponentInfo)var3.next();
               var4.setLinkInfo(this.axis, this);
            }
         }

         this.clearCachedSize();
      }

      public void remove(GroupLayout.ComponentInfo var1) {
         this.linked.remove(var1);
         var1.setLinkInfo(this.axis, (GroupLayout.LinkInfo)null);
         if (this.linked.size() == 1) {
            ((GroupLayout.ComponentInfo)this.linked.get(0)).setLinkInfo(this.axis, (GroupLayout.LinkInfo)null);
         }

         this.clearCachedSize();
      }

      public void clearCachedSize() {
         this.size = Integer.MIN_VALUE;
      }

      public int getSize(int var1) {
         if (this.size == Integer.MIN_VALUE) {
            this.size = this.calculateLinkedSize(var1);
         }

         return this.size;
      }

      private int calculateLinkedSize(int var1) {
         int var2 = 0;

         GroupLayout.ComponentSpring var5;
         for(Iterator var3 = this.linked.iterator(); var3.hasNext(); var2 = Math.max(var2, var5.calculateNonlinkedPreferredSize(var1))) {
            GroupLayout.ComponentInfo var4 = (GroupLayout.ComponentInfo)var3.next();
            if (var1 == 0) {
               var5 = var4.horizontalSpring;
            } else {
               assert var1 == 1;

               var5 = var4.verticalSpring;
            }
         }

         return var2;
      }
   }

   private class ContainerAutoPreferredGapSpring extends GroupLayout.AutoPreferredGapSpring {
      private List<GroupLayout.ComponentSpring> targets;

      ContainerAutoPreferredGapSpring() {
         super(null);
         this.setUserCreated(true);
      }

      ContainerAutoPreferredGapSpring(int var2, int var3) {
         super(var2, var3);
         this.setUserCreated(true);
      }

      public void addTarget(GroupLayout.ComponentSpring var1, int var2) {
         if (this.targets == null) {
            this.targets = new ArrayList(1);
         }

         this.targets.add(var1);
      }

      public void calculatePadding(int var1) {
         LayoutStyle var2 = GroupLayout.this.getLayoutStyle0();
         int var3 = 0;
         this.size = 0;
         byte var4;
         int var5;
         GroupLayout.ComponentSpring var6;
         if (this.targets != null) {
            if (var1 == 0) {
               if (GroupLayout.this.isLeftToRight()) {
                  var4 = 7;
               } else {
                  var4 = 3;
               }
            } else {
               var4 = 5;
            }

            for(var5 = this.targets.size() - 1; var5 >= 0; --var5) {
               var6 = (GroupLayout.ComponentSpring)this.targets.get(var5);
               int var7 = 10;
               if (var6.getComponent() instanceof JComponent) {
                  var7 = var2.getContainerGap((JComponent)var6.getComponent(), var4, GroupLayout.this.host);
                  var3 = Math.max(var7, var3);
                  var7 -= var6.getOrigin();
               } else {
                  var3 = Math.max(var7, var3);
               }

               this.size = Math.max(this.size, var7);
            }
         } else {
            if (var1 == 0) {
               if (GroupLayout.this.isLeftToRight()) {
                  var4 = 3;
               } else {
                  var4 = 7;
               }
            } else {
               var4 = 5;
            }

            if (this.sources != null) {
               for(var5 = this.sources.size() - 1; var5 >= 0; --var5) {
                  var6 = (GroupLayout.ComponentSpring)this.sources.get(var5);
                  var3 = Math.max(var3, this.updateSize(var2, var6, var4));
               }
            } else if (this.source != null) {
               var3 = this.updateSize(var2, this.source, var4);
            }
         }

         if (this.lastSize != Integer.MIN_VALUE) {
            this.size += Math.min(var3, this.lastSize);
         }

      }

      private int updateSize(LayoutStyle var1, GroupLayout.ComponentSpring var2, int var3) {
         int var4 = 10;
         if (var2.getComponent() instanceof JComponent) {
            var4 = var1.getContainerGap((JComponent)var2.getComponent(), var3, GroupLayout.this.host);
         }

         int var5 = Math.max(0, this.getParent().getSize() - var2.getSize() - var2.getOrigin());
         this.size = Math.max(this.size, var4 - var5);
         return var4;
      }

      String getMatchDescription() {
         if (this.targets != null) {
            return "leading: " + this.targets.toString();
         } else {
            return this.sources != null ? "trailing: " + this.sources.toString() : "--";
         }
      }
   }

   private static final class AutoPreferredGapMatch {
      public final GroupLayout.ComponentSpring source;
      public final GroupLayout.ComponentSpring target;

      AutoPreferredGapMatch(GroupLayout.ComponentSpring var1, GroupLayout.ComponentSpring var2) {
         this.source = var1;
         this.target = var2;
      }

      private String toString(GroupLayout.ComponentSpring var1) {
         return var1.getComponent().getName();
      }

      public String toString() {
         return "[" + this.toString(this.source) + "-" + this.toString(this.target) + "]";
      }
   }

   private class AutoPreferredGapSpring extends GroupLayout.Spring {
      List<GroupLayout.ComponentSpring> sources;
      GroupLayout.ComponentSpring source;
      private List<GroupLayout.AutoPreferredGapMatch> matches;
      int size;
      int lastSize;
      private final int pref;
      private final int max;
      private LayoutStyle.ComponentPlacement type;
      private boolean userCreated;

      private AutoPreferredGapSpring() {
         super();
         this.pref = -2;
         this.max = -2;
         this.type = LayoutStyle.ComponentPlacement.RELATED;
      }

      AutoPreferredGapSpring(int var2, int var3) {
         super();
         this.pref = var2;
         this.max = var3;
      }

      AutoPreferredGapSpring(LayoutStyle.ComponentPlacement var2, int var3, int var4) {
         super();
         this.type = var2;
         this.pref = var3;
         this.max = var4;
         this.userCreated = true;
      }

      public void setSource(GroupLayout.ComponentSpring var1) {
         this.source = var1;
      }

      public void setSources(List<GroupLayout.ComponentSpring> var1) {
         this.sources = new ArrayList(var1);
      }

      public void setUserCreated(boolean var1) {
         this.userCreated = var1;
      }

      public boolean getUserCreated() {
         return this.userCreated;
      }

      void unset() {
         this.lastSize = this.getSize();
         super.unset();
         this.size = 0;
      }

      public void reset() {
         this.size = 0;
         this.sources = null;
         this.source = null;
         this.matches = null;
      }

      public void calculatePadding(int var1) {
         this.size = Integer.MIN_VALUE;
         int var2 = Integer.MIN_VALUE;
         if (this.matches != null) {
            LayoutStyle var3 = GroupLayout.this.getLayoutStyle0();
            byte var4;
            if (var1 == 0) {
               if (GroupLayout.this.isLeftToRight()) {
                  var4 = 3;
               } else {
                  var4 = 7;
               }
            } else {
               var4 = 5;
            }

            for(int var5 = this.matches.size() - 1; var5 >= 0; --var5) {
               GroupLayout.AutoPreferredGapMatch var6 = (GroupLayout.AutoPreferredGapMatch)this.matches.get(var5);
               var2 = Math.max(var2, this.calculatePadding(var3, var4, var6.source, var6.target));
            }
         }

         if (this.size == Integer.MIN_VALUE) {
            this.size = 0;
         }

         if (var2 == Integer.MIN_VALUE) {
            var2 = 0;
         }

         if (this.lastSize != Integer.MIN_VALUE) {
            this.size += Math.min(var2, this.lastSize);
         }

      }

      private int calculatePadding(LayoutStyle var1, int var2, GroupLayout.ComponentSpring var3, GroupLayout.ComponentSpring var4) {
         int var5 = var4.getOrigin() - (var3.getOrigin() + var3.getSize());
         if (var5 < 0) {
            return 0;
         } else {
            int var6;
            if (var3.getComponent() instanceof JComponent && var4.getComponent() instanceof JComponent) {
               var6 = var1.getPreferredGap((JComponent)var3.getComponent(), (JComponent)var4.getComponent(), this.type, var2, GroupLayout.this.host);
            } else {
               var6 = 10;
            }

            if (var6 > var5) {
               this.size = Math.max(this.size, var6 - var5);
            }

            return var6;
         }
      }

      public void addTarget(GroupLayout.ComponentSpring var1, int var2) {
         int var3 = var2 == 0 ? 1 : 0;
         if (this.source != null) {
            if (GroupLayout.this.areParallelSiblings(this.source.getComponent(), var1.getComponent(), var3)) {
               this.addValidTarget(this.source, var1);
            }
         } else {
            Component var4 = var1.getComponent();

            for(int var5 = this.sources.size() - 1; var5 >= 0; --var5) {
               GroupLayout.ComponentSpring var6 = (GroupLayout.ComponentSpring)this.sources.get(var5);
               if (GroupLayout.this.areParallelSiblings(var6.getComponent(), var4, var3)) {
                  this.addValidTarget(var6, var1);
               }
            }
         }

      }

      private void addValidTarget(GroupLayout.ComponentSpring var1, GroupLayout.ComponentSpring var2) {
         if (this.matches == null) {
            this.matches = new ArrayList(1);
         }

         this.matches.add(new GroupLayout.AutoPreferredGapMatch(var1, var2));
      }

      int calculateMinimumSize(int var1) {
         return this.size;
      }

      int calculatePreferredSize(int var1) {
         return this.pref != -2 && this.pref != -1 ? Math.max(this.size, this.pref) : this.size;
      }

      int calculateMaximumSize(int var1) {
         return this.max >= 0 ? Math.max(this.getPreferredSize(var1), this.max) : this.size;
      }

      String getMatchDescription() {
         return this.matches == null ? "" : this.matches.toString();
      }

      public String toString() {
         return super.toString() + this.getMatchDescription();
      }

      boolean willHaveZeroSize(boolean var1) {
         return var1;
      }

      // $FF: synthetic method
      AutoPreferredGapSpring(Object var2) {
         this();
      }
   }

   private class GapSpring extends GroupLayout.Spring {
      private final int min;
      private final int pref;
      private final int max;

      GapSpring(int var2, int var3, int var4) {
         super();
         GroupLayout.checkSize(var2, var3, var4, false);
         this.min = var2;
         this.pref = var3;
         this.max = var4;
      }

      int calculateMinimumSize(int var1) {
         return this.min == -2 ? this.getPreferredSize(var1) : this.min;
      }

      int calculatePreferredSize(int var1) {
         return this.pref;
      }

      int calculateMaximumSize(int var1) {
         return this.max == -2 ? this.getPreferredSize(var1) : this.max;
      }

      boolean willHaveZeroSize(boolean var1) {
         return false;
      }
   }

   private class PreferredGapSpring extends GroupLayout.Spring {
      private final JComponent source;
      private final JComponent target;
      private final LayoutStyle.ComponentPlacement type;
      private final int pref;
      private final int max;

      PreferredGapSpring(JComponent var2, JComponent var3, LayoutStyle.ComponentPlacement var4, int var5, int var6) {
         super();
         this.source = var2;
         this.target = var3;
         this.type = var4;
         this.pref = var5;
         this.max = var6;
      }

      int calculateMinimumSize(int var1) {
         return this.getPadding(var1);
      }

      int calculatePreferredSize(int var1) {
         if (this.pref != -1 && this.pref != -2) {
            int var2 = this.getMinimumSize(var1);
            int var3 = this.getMaximumSize(var1);
            return Math.min(var3, Math.max(var2, this.pref));
         } else {
            return this.getMinimumSize(var1);
         }
      }

      int calculateMaximumSize(int var1) {
         return this.max != -2 && this.max != -1 ? Math.max(this.getMinimumSize(var1), this.max) : this.getPadding(var1);
      }

      private int getPadding(int var1) {
         byte var2;
         if (var1 == 0) {
            var2 = 3;
         } else {
            var2 = 5;
         }

         return GroupLayout.this.getLayoutStyle0().getPreferredGap(this.source, this.target, this.type, var2, GroupLayout.this.host);
      }

      boolean willHaveZeroSize(boolean var1) {
         return false;
      }
   }

   private final class ComponentSpring extends GroupLayout.Spring {
      private Component component;
      private int origin;
      private final int min;
      private final int pref;
      private final int max;
      private int baseline;
      private boolean installed;

      private ComponentSpring(Component var2, int var3, int var4, int var5) {
         super();
         this.baseline = -1;
         this.component = var2;
         if (var2 == null) {
            throw new IllegalArgumentException("Component must be non-null");
         } else {
            GroupLayout.checkSize(var3, var4, var5, true);
            this.min = var3;
            this.max = var5;
            this.pref = var4;
            GroupLayout.this.getComponentInfo(var2);
         }
      }

      int calculateMinimumSize(int var1) {
         return this.isLinked(var1) ? this.getLinkSize(var1, 0) : this.calculateNonlinkedMinimumSize(var1);
      }

      int calculatePreferredSize(int var1) {
         if (this.isLinked(var1)) {
            return this.getLinkSize(var1, 1);
         } else {
            int var2 = this.getMinimumSize(var1);
            int var3 = this.calculateNonlinkedPreferredSize(var1);
            int var4 = this.getMaximumSize(var1);
            return Math.min(var4, Math.max(var2, var3));
         }
      }

      int calculateMaximumSize(int var1) {
         return this.isLinked(var1) ? this.getLinkSize(var1, 2) : Math.max(this.getMinimumSize(var1), this.calculateNonlinkedMaximumSize(var1));
      }

      boolean isVisible() {
         return GroupLayout.this.getComponentInfo(this.getComponent()).isVisible();
      }

      int calculateNonlinkedMinimumSize(int var1) {
         if (!this.isVisible()) {
            return 0;
         } else if (this.min >= 0) {
            return this.min;
         } else if (this.min == -2) {
            return this.calculateNonlinkedPreferredSize(var1);
         } else {
            assert this.min == -1;

            return this.getSizeAlongAxis(var1, this.component.getMinimumSize());
         }
      }

      int calculateNonlinkedPreferredSize(int var1) {
         if (!this.isVisible()) {
            return 0;
         } else if (this.pref >= 0) {
            return this.pref;
         } else {
            assert this.pref == -1 || this.pref == -2;

            return this.getSizeAlongAxis(var1, this.component.getPreferredSize());
         }
      }

      int calculateNonlinkedMaximumSize(int var1) {
         if (!this.isVisible()) {
            return 0;
         } else if (this.max >= 0) {
            return this.max;
         } else if (this.max == -2) {
            return this.calculateNonlinkedPreferredSize(var1);
         } else {
            assert this.max == -1;

            return this.getSizeAlongAxis(var1, this.component.getMaximumSize());
         }
      }

      private int getSizeAlongAxis(int var1, Dimension var2) {
         return var1 == 0 ? var2.width : var2.height;
      }

      private int getLinkSize(int var1, int var2) {
         if (!this.isVisible()) {
            return 0;
         } else {
            GroupLayout.ComponentInfo var3 = GroupLayout.this.getComponentInfo(this.component);
            return var3.getLinkSize(var1, var2);
         }
      }

      void setSize(int var1, int var2, int var3) {
         super.setSize(var1, var2, var3);
         this.origin = var2;
         if (var3 == Integer.MIN_VALUE) {
            this.baseline = -1;
         }

      }

      int getOrigin() {
         return this.origin;
      }

      void setComponent(Component var1) {
         this.component = var1;
      }

      Component getComponent() {
         return this.component;
      }

      int getBaseline() {
         if (this.baseline == -1) {
            GroupLayout.ComponentSpring var1 = GroupLayout.this.getComponentInfo(this.component).horizontalSpring;
            int var2 = var1.getPreferredSize(0);
            int var3 = this.getPreferredSize(1);
            if (var2 > 0 && var3 > 0) {
               this.baseline = this.component.getBaseline(var2, var3);
            }
         }

         return this.baseline;
      }

      Component.BaselineResizeBehavior getBaselineResizeBehavior() {
         return this.getComponent().getBaselineResizeBehavior();
      }

      private boolean isLinked(int var1) {
         return GroupLayout.this.getComponentInfo(this.component).isLinked(var1);
      }

      void installIfNecessary(int var1) {
         if (!this.installed) {
            this.installed = true;
            if (var1 == 0) {
               GroupLayout.this.getComponentInfo(this.component).horizontalSpring = this;
            } else {
               GroupLayout.this.getComponentInfo(this.component).verticalSpring = this;
            }
         }

      }

      boolean willHaveZeroSize(boolean var1) {
         return !this.isVisible();
      }

      // $FF: synthetic method
      ComponentSpring(Component var2, int var3, int var4, int var5, Object var6) {
         this(var2, var3, var4, var5);
      }
   }

   private class BaselineGroup extends GroupLayout.ParallelGroup {
      private boolean allSpringsHaveBaseline;
      private int prefAscent;
      private int prefDescent;
      private boolean baselineAnchorSet;
      private boolean baselineAnchoredToTop;
      private boolean calcedBaseline;

      BaselineGroup(boolean var2) {
         super(GroupLayout.Alignment.LEADING, var2);
         this.prefAscent = this.prefDescent = -1;
         this.calcedBaseline = false;
      }

      BaselineGroup(boolean var2, boolean var3) {
         this(var2);
         this.baselineAnchoredToTop = var3;
         this.baselineAnchorSet = true;
      }

      void unset() {
         super.unset();
         this.prefAscent = this.prefDescent = -1;
         this.calcedBaseline = false;
      }

      void setValidSize(int var1, int var2, int var3) {
         this.checkAxis(var1);
         if (this.prefAscent == -1) {
            super.setValidSize(var1, var2, var3);
         } else {
            this.baselineLayout(var2, var3);
         }

      }

      int calculateSize(int var1, int var2) {
         this.checkAxis(var1);
         if (!this.calcedBaseline) {
            this.calculateBaselineAndResizeBehavior();
         }

         if (var2 == 0) {
            return this.calculateMinSize();
         } else if (var2 == 2) {
            return this.calculateMaxSize();
         } else {
            return this.allSpringsHaveBaseline ? this.prefAscent + this.prefDescent : Math.max(this.prefAscent + this.prefDescent, super.calculateSize(var1, var2));
         }
      }

      private void calculateBaselineAndResizeBehavior() {
         this.prefAscent = 0;
         this.prefDescent = 0;
         int var1 = 0;
         Component.BaselineResizeBehavior var2 = null;
         Iterator var3 = this.springs.iterator();

         while(true) {
            GroupLayout.Spring var4;
            do {
               if (!var3.hasNext()) {
                  if (!this.baselineAnchorSet) {
                     if (var2 == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                        this.baselineAnchoredToTop = false;
                     } else {
                        this.baselineAnchoredToTop = true;
                     }
                  }

                  this.allSpringsHaveBaseline = var1 == this.springs.size();
                  this.calcedBaseline = true;
                  return;
               }

               var4 = (GroupLayout.Spring)var3.next();
            } while(var4.getAlignment() != null && var4.getAlignment() != GroupLayout.Alignment.BASELINE);

            int var5 = var4.getBaseline();
            if (var5 >= 0) {
               if (var4.isResizable(1)) {
                  Component.BaselineResizeBehavior var6 = var4.getBaselineResizeBehavior();
                  if (var2 == null) {
                     var2 = var6;
                  } else if (var6 != var2) {
                     var2 = Component.BaselineResizeBehavior.CONSTANT_ASCENT;
                  }
               }

               this.prefAscent = Math.max(this.prefAscent, var5);
               this.prefDescent = Math.max(this.prefDescent, var4.getPreferredSize(1) - var5);
               ++var1;
            }
         }
      }

      private int calculateMaxSize() {
         int var1 = this.prefAscent;
         int var2 = this.prefDescent;
         int var3 = 0;
         Iterator var4 = this.springs.iterator();

         while(true) {
            while(var4.hasNext()) {
               GroupLayout.Spring var5 = (GroupLayout.Spring)var4.next();
               int var7 = var5.getMaximumSize(1);
               int var6;
               if ((var5.getAlignment() == null || var5.getAlignment() == GroupLayout.Alignment.BASELINE) && (var6 = var5.getBaseline()) >= 0) {
                  int var8 = var5.getPreferredSize(1);
                  if (var8 != var7) {
                     switch(var5.getBaselineResizeBehavior()) {
                     case CONSTANT_ASCENT:
                        if (this.baselineAnchoredToTop) {
                           var2 = Math.max(var2, var7 - var6);
                        }
                        break;
                     case CONSTANT_DESCENT:
                        if (!this.baselineAnchoredToTop) {
                           var1 = Math.max(var1, var7 - var8 + var6);
                        }
                     }
                  }
               } else {
                  var3 = Math.max(var3, var7);
               }
            }

            return Math.max(var3, var1 + var2);
         }
      }

      private int calculateMinSize() {
         int var1 = 0;
         int var2 = 0;
         int var3 = 0;
         if (this.baselineAnchoredToTop) {
            var1 = this.prefAscent;
         } else {
            var2 = this.prefDescent;
         }

         Iterator var4 = this.springs.iterator();

         while(true) {
            while(var4.hasNext()) {
               GroupLayout.Spring var5 = (GroupLayout.Spring)var4.next();
               int var6 = var5.getMinimumSize(1);
               int var7;
               if ((var5.getAlignment() == null || var5.getAlignment() == GroupLayout.Alignment.BASELINE) && (var7 = var5.getBaseline()) >= 0) {
                  int var8 = var5.getPreferredSize(1);
                  Component.BaselineResizeBehavior var9 = var5.getBaselineResizeBehavior();
                  switch(var9) {
                  case CONSTANT_ASCENT:
                     if (this.baselineAnchoredToTop) {
                        var2 = Math.max(var6 - var7, var2);
                     } else {
                        var1 = Math.max(var7, var1);
                     }
                     break;
                  case CONSTANT_DESCENT:
                     if (!this.baselineAnchoredToTop) {
                        var1 = Math.max(var7 - (var8 - var6), var1);
                     } else {
                        var2 = Math.max(var8 - var7, var2);
                     }
                     break;
                  default:
                     var1 = Math.max(var7, var1);
                     var2 = Math.max(var8 - var7, var2);
                  }
               } else {
                  var3 = Math.max(var3, var6);
               }
            }

            return Math.max(var3, var1 + var2);
         }
      }

      private void baselineLayout(int var1, int var2) {
         int var3;
         int var4;
         if (this.baselineAnchoredToTop) {
            var3 = this.prefAscent;
            var4 = var2 - var3;
         } else {
            var3 = var2 - this.prefDescent;
            var4 = this.prefDescent;
         }

         Iterator var5 = this.springs.iterator();

         while(true) {
            while(var5.hasNext()) {
               GroupLayout.Spring var6 = (GroupLayout.Spring)var5.next();
               GroupLayout.Alignment var7 = var6.getAlignment();
               if (var7 != null && var7 != GroupLayout.Alignment.BASELINE) {
                  this.setChildSize(var6, 1, var1, var2);
               } else {
                  int var8 = var6.getBaseline();
                  if (var8 >= 0) {
                     int var9 = var6.getMaximumSize(1);
                     int var10 = var6.getPreferredSize(1);
                     int var11 = var10;
                     int var12;
                     switch(var6.getBaselineResizeBehavior()) {
                     case CONSTANT_ASCENT:
                        var12 = var1 + var3 - var8;
                        var11 = Math.min(var4, var9 - var8) + var8;
                        break;
                     case CONSTANT_DESCENT:
                        var11 = Math.min(var3, var9 - var10 + var8) + (var10 - var8);
                        var12 = var1 + var3 + (var10 - var8) - var11;
                        break;
                     default:
                        var12 = var1 + var3 - var8;
                     }

                     var6.setSize(1, var12, var11);
                  } else {
                     this.setChildSize(var6, 1, var1, var2);
                  }
               }
            }

            return;
         }
      }

      int getBaseline() {
         if (this.springs.size() > 1) {
            this.getPreferredSize(1);
            return this.prefAscent;
         } else {
            return this.springs.size() == 1 ? ((GroupLayout.Spring)this.springs.get(0)).getBaseline() : -1;
         }
      }

      Component.BaselineResizeBehavior getBaselineResizeBehavior() {
         if (this.springs.size() == 1) {
            return ((GroupLayout.Spring)this.springs.get(0)).getBaselineResizeBehavior();
         } else {
            return this.baselineAnchoredToTop ? Component.BaselineResizeBehavior.CONSTANT_ASCENT : Component.BaselineResizeBehavior.CONSTANT_DESCENT;
         }
      }

      private void checkAxis(int var1) {
         if (var1 == 0) {
            throw new IllegalStateException("Baseline must be used along vertical axis");
         }
      }
   }

   public class ParallelGroup extends GroupLayout.Group {
      private final GroupLayout.Alignment childAlignment;
      private final boolean resizable;

      ParallelGroup(GroupLayout.Alignment var2, boolean var3) {
         super();
         this.childAlignment = var2;
         this.resizable = var3;
      }

      public GroupLayout.ParallelGroup addGroup(GroupLayout.Group var1) {
         return (GroupLayout.ParallelGroup)super.addGroup(var1);
      }

      public GroupLayout.ParallelGroup addComponent(Component var1) {
         return (GroupLayout.ParallelGroup)super.addComponent(var1);
      }

      public GroupLayout.ParallelGroup addComponent(Component var1, int var2, int var3, int var4) {
         return (GroupLayout.ParallelGroup)super.addComponent(var1, var2, var3, var4);
      }

      public GroupLayout.ParallelGroup addGap(int var1) {
         return (GroupLayout.ParallelGroup)super.addGap(var1);
      }

      public GroupLayout.ParallelGroup addGap(int var1, int var2, int var3) {
         return (GroupLayout.ParallelGroup)super.addGap(var1, var2, var3);
      }

      public GroupLayout.ParallelGroup addGroup(GroupLayout.Alignment var1, GroupLayout.Group var2) {
         this.checkChildAlignment(var1);
         var2.setAlignment(var1);
         return (GroupLayout.ParallelGroup)this.addSpring(var2);
      }

      public GroupLayout.ParallelGroup addComponent(Component var1, GroupLayout.Alignment var2) {
         return this.addComponent(var1, var2, -1, -1, -1);
      }

      public GroupLayout.ParallelGroup addComponent(Component var1, GroupLayout.Alignment var2, int var3, int var4, int var5) {
         this.checkChildAlignment(var2);
         GroupLayout.ComponentSpring var6 = GroupLayout.this.new ComponentSpring(var1, var3, var4, var5);
         var6.setAlignment(var2);
         return (GroupLayout.ParallelGroup)this.addSpring(var6);
      }

      boolean isResizable() {
         return this.resizable;
      }

      int operator(int var1, int var2) {
         return Math.max(var1, var2);
      }

      int calculateMinimumSize(int var1) {
         return !this.isResizable() ? this.getPreferredSize(var1) : super.calculateMinimumSize(var1);
      }

      int calculateMaximumSize(int var1) {
         return !this.isResizable() ? this.getPreferredSize(var1) : super.calculateMaximumSize(var1);
      }

      void setValidSize(int var1, int var2, int var3) {
         Iterator var4 = this.springs.iterator();

         while(var4.hasNext()) {
            GroupLayout.Spring var5 = (GroupLayout.Spring)var4.next();
            this.setChildSize(var5, var1, var2, var3);
         }

      }

      void setChildSize(GroupLayout.Spring var1, int var2, int var3, int var4) {
         GroupLayout.Alignment var5 = var1.getAlignment();
         int var6 = Math.min(Math.max(var1.getMinimumSize(var2), var4), var1.getMaximumSize(var2));
         if (var5 == null) {
            var5 = this.childAlignment;
         }

         switch(var5) {
         case TRAILING:
            var1.setSize(var2, var3 + var4 - var6, var6);
            break;
         case CENTER:
            var1.setSize(var2, var3 + (var4 - var6) / 2, var6);
            break;
         default:
            var1.setSize(var2, var3, var6);
         }

      }

      void insertAutopadding(int var1, List<GroupLayout.AutoPreferredGapSpring> var2, List<GroupLayout.AutoPreferredGapSpring> var3, List<GroupLayout.ComponentSpring> var4, List<GroupLayout.ComponentSpring> var5, boolean var6) {
         Iterator var7 = this.springs.iterator();

         while(true) {
            GroupLayout.Spring var8;
            label29:
            do {
               while(var7.hasNext()) {
                  var8 = (GroupLayout.Spring)var7.next();
                  if (var8 instanceof GroupLayout.ComponentSpring) {
                     continue label29;
                  }

                  if (var8 instanceof GroupLayout.Group) {
                     ((GroupLayout.Group)var8).insertAutopadding(var1, var2, var3, var4, var5, var6);
                  } else if (var8 instanceof GroupLayout.AutoPreferredGapSpring) {
                     ((GroupLayout.AutoPreferredGapSpring)var8).setSources(var4);
                     var3.add((GroupLayout.AutoPreferredGapSpring)var8);
                  }
               }

               return;
            } while(!((GroupLayout.ComponentSpring)var8).isVisible());

            Iterator var9 = var2.iterator();

            while(var9.hasNext()) {
               GroupLayout.AutoPreferredGapSpring var10 = (GroupLayout.AutoPreferredGapSpring)var9.next();
               var10.addTarget((GroupLayout.ComponentSpring)var8, var1);
            }

            var5.add((GroupLayout.ComponentSpring)var8);
         }
      }

      private void checkChildAlignment(GroupLayout.Alignment var1) {
         this.checkChildAlignment(var1, this instanceof GroupLayout.BaselineGroup);
      }

      private void checkChildAlignment(GroupLayout.Alignment var1, boolean var2) {
         if (var1 == null) {
            throw new IllegalArgumentException("Alignment must be non-null");
         } else if (!var2 && var1 == GroupLayout.Alignment.BASELINE) {
            throw new IllegalArgumentException("Alignment must be one of:LEADING, TRAILING or CENTER");
         }
      }
   }

   private static final class SpringDelta implements Comparable<GroupLayout.SpringDelta> {
      public final int index;
      public int delta;

      public SpringDelta(int var1, int var2) {
         this.index = var1;
         this.delta = var2;
      }

      public int compareTo(GroupLayout.SpringDelta var1) {
         return this.delta - var1.delta;
      }

      public String toString() {
         return super.toString() + "[index=" + this.index + ", delta=" + this.delta + "]";
      }
   }

   public class SequentialGroup extends GroupLayout.Group {
      private GroupLayout.Spring baselineSpring;

      SequentialGroup() {
         super();
      }

      public GroupLayout.SequentialGroup addGroup(GroupLayout.Group var1) {
         return (GroupLayout.SequentialGroup)super.addGroup(var1);
      }

      public GroupLayout.SequentialGroup addGroup(boolean var1, GroupLayout.Group var2) {
         super.addGroup(var2);
         if (var1) {
            this.baselineSpring = var2;
         }

         return this;
      }

      public GroupLayout.SequentialGroup addComponent(Component var1) {
         return (GroupLayout.SequentialGroup)super.addComponent(var1);
      }

      public GroupLayout.SequentialGroup addComponent(boolean var1, Component var2) {
         super.addComponent(var2);
         if (var1) {
            this.baselineSpring = (GroupLayout.Spring)this.springs.get(this.springs.size() - 1);
         }

         return this;
      }

      public GroupLayout.SequentialGroup addComponent(Component var1, int var2, int var3, int var4) {
         return (GroupLayout.SequentialGroup)super.addComponent(var1, var2, var3, var4);
      }

      public GroupLayout.SequentialGroup addComponent(boolean var1, Component var2, int var3, int var4, int var5) {
         super.addComponent(var2, var3, var4, var5);
         if (var1) {
            this.baselineSpring = (GroupLayout.Spring)this.springs.get(this.springs.size() - 1);
         }

         return this;
      }

      public GroupLayout.SequentialGroup addGap(int var1) {
         return (GroupLayout.SequentialGroup)super.addGap(var1);
      }

      public GroupLayout.SequentialGroup addGap(int var1, int var2, int var3) {
         return (GroupLayout.SequentialGroup)super.addGap(var1, var2, var3);
      }

      public GroupLayout.SequentialGroup addPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3) {
         return this.addPreferredGap(var1, var2, var3, -1, -2);
      }

      public GroupLayout.SequentialGroup addPreferredGap(JComponent var1, JComponent var2, LayoutStyle.ComponentPlacement var3, int var4, int var5) {
         if (var3 == null) {
            throw new IllegalArgumentException("Type must be non-null");
         } else if (var1 != null && var2 != null) {
            this.checkPreferredGapValues(var4, var5);
            return (GroupLayout.SequentialGroup)this.addSpring(GroupLayout.this.new PreferredGapSpring(var1, var2, var3, var4, var5));
         } else {
            throw new IllegalArgumentException("Components must be non-null");
         }
      }

      public GroupLayout.SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement var1) {
         return this.addPreferredGap(var1, -1, -1);
      }

      public GroupLayout.SequentialGroup addPreferredGap(LayoutStyle.ComponentPlacement var1, int var2, int var3) {
         if (var1 != LayoutStyle.ComponentPlacement.RELATED && var1 != LayoutStyle.ComponentPlacement.UNRELATED) {
            throw new IllegalArgumentException("Type must be one of LayoutStyle.ComponentPlacement.RELATED or LayoutStyle.ComponentPlacement.UNRELATED");
         } else {
            this.checkPreferredGapValues(var2, var3);
            GroupLayout.this.hasPreferredPaddingSprings = true;
            return (GroupLayout.SequentialGroup)this.addSpring(GroupLayout.this.new AutoPreferredGapSpring(var1, var2, var3));
         }
      }

      public GroupLayout.SequentialGroup addContainerGap() {
         return this.addContainerGap(-1, -1);
      }

      public GroupLayout.SequentialGroup addContainerGap(int var1, int var2) {
         if ((var1 >= 0 || var1 == -1) && (var2 >= 0 || var2 == -1 || var2 == -2) && (var1 < 0 || var2 < 0 || var1 <= var2)) {
            GroupLayout.this.hasPreferredPaddingSprings = true;
            return (GroupLayout.SequentialGroup)this.addSpring(GroupLayout.this.new ContainerAutoPreferredGapSpring(var1, var2));
         } else {
            throw new IllegalArgumentException("Pref and max must be either DEFAULT_VALUE or >= 0 and pref <= max");
         }
      }

      int operator(int var1, int var2) {
         return this.constrain(var1) + this.constrain(var2);
      }

      void setValidSize(int var1, int var2, int var3) {
         int var4 = this.getPreferredSize(var1);
         int var7;
         if (var3 == var4) {
            for(Iterator var5 = this.springs.iterator(); var5.hasNext(); var2 += var7) {
               GroupLayout.Spring var6 = (GroupLayout.Spring)var5.next();
               var7 = var6.getPreferredSize(var1);
               var6.setSize(var1, var2, var7);
            }
         } else if (this.springs.size() == 1) {
            GroupLayout.Spring var8 = this.getSpring(0);
            var8.setSize(var1, var2, Math.min(Math.max(var3, var8.getMinimumSize(var1)), var8.getMaximumSize(var1)));
         } else if (this.springs.size() > 1) {
            this.setValidSizeNotPreferred(var1, var2, var3);
         }

      }

      private void setValidSizeNotPreferred(int var1, int var2, int var3) {
         int var4 = var3 - this.getPreferredSize(var1);

         assert var4 != 0;

         boolean var5 = var4 < 0;
         int var6 = this.springs.size();
         if (var5) {
            var4 *= -1;
         }

         List var7 = this.buildResizableList(var1, var5);
         int var8 = var7.size();
         int var9;
         if (var8 > 0) {
            var9 = var4 / var8;
            int var10 = var4 - var9 * var8;
            int[] var11 = new int[var6];
            int var12 = var5 ? -1 : 1;

            int var13;
            for(var13 = 0; var13 < var8; ++var13) {
               GroupLayout.SpringDelta var14 = (GroupLayout.SpringDelta)var7.get(var13);
               if (var13 + 1 == var8) {
                  var9 += var10;
               }

               var14.delta = Math.min(var9, var14.delta);
               var4 -= var14.delta;
               if (var14.delta != var9 && var13 + 1 < var8) {
                  var9 = var4 / (var8 - var13 - 1);
                  var10 = var4 - var9 * (var8 - var13 - 1);
               }

               var11[var14.index] = var12 * var14.delta;
            }

            for(var13 = 0; var13 < var6; ++var13) {
               GroupLayout.Spring var18 = this.getSpring(var13);
               int var15 = var18.getPreferredSize(var1) + var11[var13];
               var18.setSize(var1, var2, var15);
               var2 += var15;
            }
         } else {
            for(var9 = 0; var9 < var6; ++var9) {
               GroupLayout.Spring var16 = this.getSpring(var9);
               int var17;
               if (var5) {
                  var17 = var16.getMinimumSize(var1);
               } else {
                  var17 = var16.getMaximumSize(var1);
               }

               var16.setSize(var1, var2, var17);
               var2 += var17;
            }
         }

      }

      private List<GroupLayout.SpringDelta> buildResizableList(int var1, boolean var2) {
         int var3 = this.springs.size();
         ArrayList var4 = new ArrayList(var3);

         for(int var5 = 0; var5 < var3; ++var5) {
            GroupLayout.Spring var6 = this.getSpring(var5);
            int var7;
            if (var2) {
               var7 = var6.getPreferredSize(var1) - var6.getMinimumSize(var1);
            } else {
               var7 = var6.getMaximumSize(var1) - var6.getPreferredSize(var1);
            }

            if (var7 > 0) {
               var4.add(new GroupLayout.SpringDelta(var5, var7));
            }
         }

         Collections.sort(var4);
         return var4;
      }

      private int indexOfNextNonZeroSpring(int var1, boolean var2) {
         while(var1 < this.springs.size()) {
            GroupLayout.Spring var3 = (GroupLayout.Spring)this.springs.get(var1);
            if (!var3.willHaveZeroSize(var2)) {
               return var1;
            }

            ++var1;
         }

         return var1;
      }

      void insertAutopadding(int var1, List<GroupLayout.AutoPreferredGapSpring> var2, List<GroupLayout.AutoPreferredGapSpring> var3, List<GroupLayout.ComponentSpring> var4, List<GroupLayout.ComponentSpring> var5, boolean var6) {
         ArrayList var7 = new ArrayList(var2);
         ArrayList var8 = new ArrayList(1);
         ArrayList var9 = new ArrayList(var4);
         ArrayList var10 = null;
         int var11 = 0;

         while(true) {
            while(var11 < this.springs.size()) {
               GroupLayout.Spring var12 = this.getSpring(var11);
               GroupLayout.AutoPreferredGapSpring var16;
               if (var12 instanceof GroupLayout.AutoPreferredGapSpring) {
                  if (var7.size() == 0) {
                     var16 = (GroupLayout.AutoPreferredGapSpring)var12;
                     var16.setSources(var9);
                     var9.clear();
                     var11 = this.indexOfNextNonZeroSpring(var11 + 1, true);
                     if (var11 == this.springs.size()) {
                        if (!(var16 instanceof GroupLayout.ContainerAutoPreferredGapSpring)) {
                           var3.add(var16);
                        }
                     } else {
                        var7.clear();
                        var7.add(var16);
                     }
                  } else {
                     var11 = this.indexOfNextNonZeroSpring(var11 + 1, true);
                  }
               } else if (var9.size() > 0 && var6) {
                  var16 = GroupLayout.this.new AutoPreferredGapSpring();
                  this.springs.add(var11, var16);
               } else if (!(var12 instanceof GroupLayout.ComponentSpring)) {
                  if (var12 instanceof GroupLayout.Group) {
                     if (var10 == null) {
                        var10 = new ArrayList(1);
                     } else {
                        var10.clear();
                     }

                     var8.clear();
                     ((GroupLayout.Group)var12).insertAutopadding(var1, var7, var8, var9, var10, var6);
                     var9.clear();
                     var7.clear();
                     var11 = this.indexOfNextNonZeroSpring(var11 + 1, var10.size() == 0);
                     if (var11 == this.springs.size()) {
                        var5.addAll(var10);
                        var3.addAll(var8);
                     } else {
                        var9.addAll(var10);
                        var7.addAll(var8);
                     }
                  } else {
                     var7.clear();
                     var9.clear();
                     ++var11;
                  }
               } else {
                  GroupLayout.ComponentSpring var13 = (GroupLayout.ComponentSpring)var12;
                  if (!var13.isVisible()) {
                     ++var11;
                  } else {
                     Iterator var14 = var7.iterator();

                     while(var14.hasNext()) {
                        GroupLayout.AutoPreferredGapSpring var15 = (GroupLayout.AutoPreferredGapSpring)var14.next();
                        var15.addTarget(var13, var1);
                     }

                     var9.clear();
                     var7.clear();
                     var11 = this.indexOfNextNonZeroSpring(var11 + 1, false);
                     if (var11 == this.springs.size()) {
                        var5.add(var13);
                     } else {
                        var9.add(var13);
                     }
                  }
               }
            }

            return;
         }
      }

      int getBaseline() {
         if (this.baselineSpring != null) {
            int var1 = this.baselineSpring.getBaseline();
            if (var1 >= 0) {
               int var2 = 0;

               GroupLayout.Spring var4;
               for(Iterator var3 = this.springs.iterator(); var3.hasNext(); var2 += var4.getPreferredSize(1)) {
                  var4 = (GroupLayout.Spring)var3.next();
                  if (var4 == this.baselineSpring) {
                     return var2 + var1;
                  }
               }
            }
         }

         return -1;
      }

      Component.BaselineResizeBehavior getBaselineResizeBehavior() {
         if (!this.isResizable(1)) {
            return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
         } else {
            Iterator var2;
            GroupLayout.Spring var3;
            if (!this.baselineSpring.isResizable(1)) {
               boolean var1 = false;
               var2 = this.springs.iterator();

               while(var2.hasNext()) {
                  var3 = (GroupLayout.Spring)var2.next();
                  if (var3 == this.baselineSpring) {
                     break;
                  }

                  if (var3.isResizable(1)) {
                     var1 = true;
                     break;
                  }
               }

               boolean var6 = false;

               for(int var7 = this.springs.size() - 1; var7 >= 0; --var7) {
                  GroupLayout.Spring var4 = (GroupLayout.Spring)this.springs.get(var7);
                  if (var4 == this.baselineSpring) {
                     break;
                  }

                  if (var4.isResizable(1)) {
                     var6 = true;
                     break;
                  }
               }

               if (var1 && !var6) {
                  return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
               }

               if (!var1 && var6) {
                  return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
               }
            } else {
               Component.BaselineResizeBehavior var5 = this.baselineSpring.getBaselineResizeBehavior();
               if (var5 == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
                  var2 = this.springs.iterator();

                  while(var2.hasNext()) {
                     var3 = (GroupLayout.Spring)var2.next();
                     if (var3 == this.baselineSpring) {
                        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
                     }

                     if (var3.isResizable(1)) {
                        return Component.BaselineResizeBehavior.OTHER;
                     }
                  }
               } else if (var5 == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                  for(int var8 = this.springs.size() - 1; var8 >= 0; --var8) {
                     var3 = (GroupLayout.Spring)this.springs.get(var8);
                     if (var3 == this.baselineSpring) {
                        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
                     }

                     if (var3.isResizable(1)) {
                        return Component.BaselineResizeBehavior.OTHER;
                     }
                  }
               }
            }

            return Component.BaselineResizeBehavior.OTHER;
         }
      }

      private void checkPreferredGapValues(int var1, int var2) {
         if (var1 < 0 && var1 != -1 && var1 != -2 || var2 < 0 && var2 != -1 && var2 != -2 || var1 >= 0 && var2 >= 0 && var1 > var2) {
            throw new IllegalArgumentException("Pref and max must be either DEFAULT_SIZE, PREFERRED_SIZE, or >= 0 and pref <= max");
         }
      }
   }

   public abstract class Group extends GroupLayout.Spring {
      List<GroupLayout.Spring> springs = new ArrayList();

      Group() {
         super();
      }

      public GroupLayout.Group addGroup(GroupLayout.Group var1) {
         return this.addSpring(var1);
      }

      public GroupLayout.Group addComponent(Component var1) {
         return this.addComponent(var1, -1, -1, -1);
      }

      public GroupLayout.Group addComponent(Component var1, int var2, int var3, int var4) {
         return this.addSpring(GroupLayout.this.new ComponentSpring(var1, var2, var3, var4));
      }

      public GroupLayout.Group addGap(int var1) {
         return this.addGap(var1, var1, var1);
      }

      public GroupLayout.Group addGap(int var1, int var2, int var3) {
         return this.addSpring(GroupLayout.this.new GapSpring(var1, var2, var3));
      }

      GroupLayout.Spring getSpring(int var1) {
         return (GroupLayout.Spring)this.springs.get(var1);
      }

      int indexOf(GroupLayout.Spring var1) {
         return this.springs.indexOf(var1);
      }

      GroupLayout.Group addSpring(GroupLayout.Spring var1) {
         this.springs.add(var1);
         var1.setParent(this);
         if (!(var1 instanceof GroupLayout.AutoPreferredGapSpring) || !((GroupLayout.AutoPreferredGapSpring)var1).getUserCreated()) {
            GroupLayout.this.springsChanged = true;
         }

         return this;
      }

      void setSize(int var1, int var2, int var3) {
         super.setSize(var1, var2, var3);
         if (var3 == Integer.MIN_VALUE) {
            for(int var4 = this.springs.size() - 1; var4 >= 0; --var4) {
               this.getSpring(var4).setSize(var1, var2, var3);
            }
         } else {
            this.setValidSize(var1, var2, var3);
         }

      }

      abstract void setValidSize(int var1, int var2, int var3);

      int calculateMinimumSize(int var1) {
         return this.calculateSize(var1, 0);
      }

      int calculatePreferredSize(int var1) {
         return this.calculateSize(var1, 1);
      }

      int calculateMaximumSize(int var1) {
         return this.calculateSize(var1, 2);
      }

      int calculateSize(int var1, int var2) {
         int var3 = this.springs.size();
         if (var3 == 0) {
            return 0;
         } else if (var3 == 1) {
            return this.getSpringSize(this.getSpring(0), var1, var2);
         } else {
            int var4 = this.constrain(this.operator(this.getSpringSize(this.getSpring(0), var1, var2), this.getSpringSize(this.getSpring(1), var1, var2)));

            for(int var5 = 2; var5 < var3; ++var5) {
               var4 = this.constrain(this.operator(var4, this.getSpringSize(this.getSpring(var5), var1, var2)));
            }

            return var4;
         }
      }

      int getSpringSize(GroupLayout.Spring var1, int var2, int var3) {
         switch(var3) {
         case 0:
            return var1.getMinimumSize(var2);
         case 1:
            return var1.getPreferredSize(var2);
         case 2:
            return var1.getMaximumSize(var2);
         default:
            assert false;

            return 0;
         }
      }

      abstract int operator(int var1, int var2);

      abstract void insertAutopadding(int var1, List<GroupLayout.AutoPreferredGapSpring> var2, List<GroupLayout.AutoPreferredGapSpring> var3, List<GroupLayout.ComponentSpring> var4, List<GroupLayout.ComponentSpring> var5, boolean var6);

      void removeAutopadding() {
         this.unset();

         for(int var1 = this.springs.size() - 1; var1 >= 0; --var1) {
            GroupLayout.Spring var2 = (GroupLayout.Spring)this.springs.get(var1);
            if (var2 instanceof GroupLayout.AutoPreferredGapSpring) {
               if (((GroupLayout.AutoPreferredGapSpring)var2).getUserCreated()) {
                  ((GroupLayout.AutoPreferredGapSpring)var2).reset();
               } else {
                  this.springs.remove(var1);
               }
            } else if (var2 instanceof GroupLayout.Group) {
               ((GroupLayout.Group)var2).removeAutopadding();
            }
         }

      }

      void unsetAutopadding() {
         this.unset();

         for(int var1 = this.springs.size() - 1; var1 >= 0; --var1) {
            GroupLayout.Spring var2 = (GroupLayout.Spring)this.springs.get(var1);
            if (var2 instanceof GroupLayout.AutoPreferredGapSpring) {
               var2.unset();
            } else if (var2 instanceof GroupLayout.Group) {
               ((GroupLayout.Group)var2).unsetAutopadding();
            }
         }

      }

      void calculateAutopadding(int var1) {
         for(int var2 = this.springs.size() - 1; var2 >= 0; --var2) {
            GroupLayout.Spring var3 = (GroupLayout.Spring)this.springs.get(var2);
            if (var3 instanceof GroupLayout.AutoPreferredGapSpring) {
               var3.unset();
               ((GroupLayout.AutoPreferredGapSpring)var3).calculatePadding(var1);
            } else if (var3 instanceof GroupLayout.Group) {
               ((GroupLayout.Group)var3).calculateAutopadding(var1);
            }
         }

         this.unset();
      }

      boolean willHaveZeroSize(boolean var1) {
         for(int var2 = this.springs.size() - 1; var2 >= 0; --var2) {
            GroupLayout.Spring var3 = (GroupLayout.Spring)this.springs.get(var2);
            if (!var3.willHaveZeroSize(var1)) {
               return false;
            }
         }

         return true;
      }
   }

   private abstract class Spring {
      private int size;
      private int min;
      private int max;
      private int pref;
      private GroupLayout.Spring parent;
      private GroupLayout.Alignment alignment;

      Spring() {
         this.min = this.pref = this.max = Integer.MIN_VALUE;
      }

      abstract int calculateMinimumSize(int var1);

      abstract int calculatePreferredSize(int var1);

      abstract int calculateMaximumSize(int var1);

      void setParent(GroupLayout.Spring var1) {
         this.parent = var1;
      }

      GroupLayout.Spring getParent() {
         return this.parent;
      }

      void setAlignment(GroupLayout.Alignment var1) {
         this.alignment = var1;
      }

      GroupLayout.Alignment getAlignment() {
         return this.alignment;
      }

      final int getMinimumSize(int var1) {
         if (this.min == Integer.MIN_VALUE) {
            this.min = this.constrain(this.calculateMinimumSize(var1));
         }

         return this.min;
      }

      final int getPreferredSize(int var1) {
         if (this.pref == Integer.MIN_VALUE) {
            this.pref = this.constrain(this.calculatePreferredSize(var1));
         }

         return this.pref;
      }

      final int getMaximumSize(int var1) {
         if (this.max == Integer.MIN_VALUE) {
            this.max = this.constrain(this.calculateMaximumSize(var1));
         }

         return this.max;
      }

      void setSize(int var1, int var2, int var3) {
         this.size = var3;
         if (var3 == Integer.MIN_VALUE) {
            this.unset();
         }

      }

      void unset() {
         this.size = this.min = this.pref = this.max = Integer.MIN_VALUE;
      }

      int getSize() {
         return this.size;
      }

      int constrain(int var1) {
         return Math.min(var1, 32767);
      }

      int getBaseline() {
         return -1;
      }

      Component.BaselineResizeBehavior getBaselineResizeBehavior() {
         return Component.BaselineResizeBehavior.OTHER;
      }

      final boolean isResizable(int var1) {
         int var2 = this.getMinimumSize(var1);
         int var3 = this.getPreferredSize(var1);
         return var2 != var3 || var3 != this.getMaximumSize(var1);
      }

      abstract boolean willHaveZeroSize(boolean var1);
   }

   public static enum Alignment {
      LEADING,
      TRAILING,
      CENTER,
      BASELINE;
   }
}
