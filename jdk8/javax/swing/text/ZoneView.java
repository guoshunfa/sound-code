package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.DocumentEvent;

public class ZoneView extends BoxView {
   int maxZoneSize = 8192;
   int maxZonesLoaded = 3;
   Vector<View> loadedZones = new Vector();

   public ZoneView(Element var1, int var2) {
      super(var1, var2);
   }

   public int getMaximumZoneSize() {
      return this.maxZoneSize;
   }

   public void setMaximumZoneSize(int var1) {
      this.maxZoneSize = var1;
   }

   public int getMaxZonesLoaded() {
      return this.maxZonesLoaded;
   }

   public void setMaxZonesLoaded(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("ZoneView.setMaxZonesLoaded must be greater than 0.");
      } else {
         this.maxZonesLoaded = var1;
         this.unloadOldZones();
      }
   }

   protected void zoneWasLoaded(View var1) {
      this.loadedZones.addElement(var1);
      this.unloadOldZones();
   }

   void unloadOldZones() {
      while(this.loadedZones.size() > this.getMaxZonesLoaded()) {
         View var1 = (View)this.loadedZones.elementAt(0);
         this.loadedZones.removeElementAt(0);
         this.unloadZone(var1);
      }

   }

   protected void unloadZone(View var1) {
      var1.removeAll();
   }

   protected boolean isZoneLoaded(View var1) {
      return var1.getViewCount() > 0;
   }

   protected View createZone(int var1, int var2) {
      Document var3 = this.getDocument();

      try {
         ZoneView.Zone var4 = new ZoneView.Zone(this.getElement(), var3.createPosition(var1), var3.createPosition(var2));
         return var4;
      } catch (BadLocationException var6) {
         throw new StateInvariantError(var6.getMessage());
      }
   }

   protected void loadChildren(ViewFactory var1) {
      Document var2 = this.getDocument();
      int var3 = this.getStartOffset();
      int var4 = this.getEndOffset();
      this.append(this.createZone(var3, var4));
      this.handleInsert(var3, var4 - var3);
   }

   protected int getViewIndexAtPosition(int var1) {
      int var2 = this.getViewCount();
      if (var1 == this.getEndOffset()) {
         return var2 - 1;
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            View var4 = this.getView(var3);
            if (var1 >= var4.getStartOffset() && var1 < var4.getEndOffset()) {
               return var3;
            }
         }

         return -1;
      }
   }

   void handleInsert(int var1, int var2) {
      int var3 = this.getViewIndex(var1, Position.Bias.Forward);
      View var4 = this.getView(var3);
      int var5 = var4.getStartOffset();
      int var6 = var4.getEndOffset();
      if (var6 - var5 > this.maxZoneSize) {
         this.splitZone(var3, var5, var6);
      }

   }

   void handleRemove(int var1, int var2) {
   }

   void splitZone(int var1, int var2, int var3) {
      Element var4 = this.getElement();
      Document var5 = var4.getDocument();
      Vector var6 = new Vector();
      int var7 = var2;

      do {
         var2 = var7;
         var7 = Math.min(this.getDesiredZoneEnd(var7), var3);
         var6.addElement(this.createZone(var2, var7));
      } while(var7 < var3);

      this.getView(var1);
      View[] var9 = new View[var6.size()];
      var6.copyInto(var9);
      this.replace(var1, 1, var9);
   }

   int getDesiredZoneEnd(int var1) {
      Element var2 = this.getElement();
      int var3 = var2.getElementIndex(var1 + this.maxZoneSize / 2);
      Element var4 = var2.getElement(var3);
      int var5 = var4.getStartOffset();
      int var6 = var4.getEndOffset();
      return var6 - var1 > this.maxZoneSize && var5 > var1 ? var5 : var6;
   }

   protected boolean updateChildren(DocumentEvent.ElementChange var1, DocumentEvent var2, ViewFactory var3) {
      return false;
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.handleInsert(var1.getOffset(), var1.getLength());
      super.insertUpdate(var1, var2, var3);
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.handleRemove(var1.getOffset(), var1.getLength());
      super.removeUpdate(var1, var2, var3);
   }

   class Zone extends AsyncBoxView {
      private Position start;
      private Position end;

      public Zone(Element var2, Position var3, Position var4) {
         super(var2, ZoneView.this.getAxis());
         this.start = var3;
         this.end = var4;
      }

      public void load() {
         if (!this.isLoaded()) {
            this.setEstimatedMajorSpan(true);
            Element var1 = this.getElement();
            ViewFactory var2 = this.getViewFactory();
            int var3 = var1.getElementIndex(this.getStartOffset());
            int var4 = var1.getElementIndex(this.getEndOffset());
            View[] var5 = new View[var4 - var3 + 1];

            for(int var6 = var3; var6 <= var4; ++var6) {
               var5[var6 - var3] = var2.create(var1.getElement(var6));
            }

            this.replace(0, 0, var5);
            ZoneView.this.zoneWasLoaded(this);
         }

      }

      public void unload() {
         this.setEstimatedMajorSpan(true);
         this.removeAll();
      }

      public boolean isLoaded() {
         return this.getViewCount() != 0;
      }

      protected void loadChildren(ViewFactory var1) {
         this.setEstimatedMajorSpan(true);
         Element var2 = this.getElement();
         int var3 = var2.getElementIndex(this.getStartOffset());
         int var4 = var2.getElementIndex(this.getEndOffset());
         int var5 = var4 - var3;
         View var6 = var1.create(var2.getElement(var3));
         var6.setParent(this);
         float var7 = var6.getPreferredSpan(0);
         float var8 = var6.getPreferredSpan(1);
         if (this.getMajorAxis() == 0) {
            var7 *= (float)var5;
         } else {
            var8 += (float)var5;
         }

         this.setSize(var7, var8);
      }

      protected void flushRequirementChanges() {
         if (this.isLoaded()) {
            super.flushRequirementChanges();
         }

      }

      public int getViewIndex(int var1, Position.Bias var2) {
         boolean var3 = var2 == Position.Bias.Backward;
         var1 = var3 ? Math.max(0, var1 - 1) : var1;
         Element var4 = this.getElement();
         int var5 = var4.getElementIndex(var1);
         int var6 = var4.getElementIndex(this.getStartOffset());
         return var5 - var6;
      }

      protected boolean updateChildren(DocumentEvent.ElementChange var1, DocumentEvent var2, ViewFactory var3) {
         Element[] var4 = var1.getChildrenRemoved();
         Element[] var5 = var1.getChildrenAdded();
         Element var6 = this.getElement();
         int var7 = var6.getElementIndex(this.getStartOffset());
         int var8 = var6.getElementIndex(this.getEndOffset() - 1);
         int var9 = var1.getIndex();
         if (var9 >= var7 && var9 <= var8) {
            int var10 = var9 - var7;
            int var11 = Math.min(var8 - var7 + 1, var5.length);
            int var12 = Math.min(var8 - var7 + 1, var4.length);
            View[] var13 = new View[var11];

            for(int var14 = 0; var14 < var11; ++var14) {
               var13[var14] = var3.create(var5[var14]);
            }

            this.replace(var10, var12, var13);
         }

         return true;
      }

      public AttributeSet getAttributes() {
         return ZoneView.this.getAttributes();
      }

      public void paint(Graphics var1, Shape var2) {
         this.load();
         super.paint(var1, var2);
      }

      public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
         this.load();
         return super.viewToModel(var1, var2, var3, var4);
      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         this.load();
         return super.modelToView(var1, var2, var3);
      }

      public int getStartOffset() {
         return this.start.getOffset();
      }

      public int getEndOffset() {
         return this.end.getOffset();
      }

      public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.isLoaded()) {
            super.insertUpdate(var1, var2, var3);
         }

      }

      public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.isLoaded()) {
            super.removeUpdate(var1, var2, var3);
         }

      }

      public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         if (this.isLoaded()) {
            super.changedUpdate(var1, var2, var3);
         }

      }
   }
}
