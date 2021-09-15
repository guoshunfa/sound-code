package javax.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;

public class DefaultHighlighter extends LayeredHighlighter {
   private static final Highlighter.Highlight[] noHighlights = new Highlighter.Highlight[0];
   private Vector<DefaultHighlighter.HighlightInfo> highlights = new Vector();
   private JTextComponent component;
   private boolean drawsLayeredHighlights = true;
   private DefaultHighlighter.SafeDamager safeDamager = new DefaultHighlighter.SafeDamager();
   public static final LayeredHighlighter.LayerPainter DefaultPainter = new DefaultHighlighter.DefaultHighlightPainter((Color)null);

   public void paint(Graphics var1) {
      int var2 = this.highlights.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         DefaultHighlighter.HighlightInfo var4 = (DefaultHighlighter.HighlightInfo)this.highlights.elementAt(var3);
         if (!(var4 instanceof DefaultHighlighter.LayeredHighlightInfo)) {
            Rectangle var5 = this.component.getBounds();
            Insets var6 = this.component.getInsets();
            var5.x = var6.left;
            var5.y = var6.top;
            var5.width -= var6.left + var6.right;

            for(var5.height -= var6.top + var6.bottom; var3 < var2; ++var3) {
               var4 = (DefaultHighlighter.HighlightInfo)this.highlights.elementAt(var3);
               if (!(var4 instanceof DefaultHighlighter.LayeredHighlightInfo)) {
                  Highlighter.HighlightPainter var7 = var4.getPainter();
                  var7.paint(var1, var4.getStartOffset(), var4.getEndOffset(), var5, this.component);
               }
            }
         }
      }

   }

   public void install(JTextComponent var1) {
      this.component = var1;
      this.removeAllHighlights();
   }

   public void deinstall(JTextComponent var1) {
      this.component = null;
   }

   public Object addHighlight(int var1, int var2, Highlighter.HighlightPainter var3) throws BadLocationException {
      if (var1 < 0) {
         throw new BadLocationException("Invalid start offset", var1);
      } else if (var2 < var1) {
         throw new BadLocationException("Invalid end offset", var2);
      } else {
         Document var4 = this.component.getDocument();
         Object var5 = this.getDrawsLayeredHighlights() && var3 instanceof LayeredHighlighter.LayerPainter ? new DefaultHighlighter.LayeredHighlightInfo() : new DefaultHighlighter.HighlightInfo();
         ((DefaultHighlighter.HighlightInfo)var5).painter = var3;
         ((DefaultHighlighter.HighlightInfo)var5).p0 = var4.createPosition(var1);
         ((DefaultHighlighter.HighlightInfo)var5).p1 = var4.createPosition(var2);
         this.highlights.addElement(var5);
         this.safeDamageRange(var1, var2);
         return var5;
      }
   }

   public void removeHighlight(Object var1) {
      if (var1 instanceof DefaultHighlighter.LayeredHighlightInfo) {
         DefaultHighlighter.LayeredHighlightInfo var2 = (DefaultHighlighter.LayeredHighlightInfo)var1;
         if (var2.width > 0 && var2.height > 0) {
            this.component.repaint(var2.x, var2.y, var2.width, var2.height);
         }
      } else {
         DefaultHighlighter.HighlightInfo var3 = (DefaultHighlighter.HighlightInfo)var1;
         this.safeDamageRange(var3.p0, var3.p1);
      }

      this.highlights.removeElement(var1);
   }

   public void removeAllHighlights() {
      TextUI var1 = this.component.getUI();
      int var2;
      int var3;
      int var4;
      int var5;
      if (this.getDrawsLayeredHighlights()) {
         var2 = this.highlights.size();
         if (var2 != 0) {
            var3 = 0;
            var4 = 0;
            var5 = 0;
            int var6 = 0;
            int var7 = -1;
            int var8 = -1;

            for(int var9 = 0; var9 < var2; ++var9) {
               DefaultHighlighter.HighlightInfo var10 = (DefaultHighlighter.HighlightInfo)this.highlights.elementAt(var9);
               if (var10 instanceof DefaultHighlighter.LayeredHighlightInfo) {
                  DefaultHighlighter.LayeredHighlightInfo var11 = (DefaultHighlighter.LayeredHighlightInfo)var10;
                  var3 = Math.min(var3, var11.x);
                  var4 = Math.min(var4, var11.y);
                  var5 = Math.max(var5, var11.x + var11.width);
                  var6 = Math.max(var6, var11.y + var11.height);
               } else if (var7 == -1) {
                  var7 = var10.p0.getOffset();
                  var8 = var10.p1.getOffset();
               } else {
                  var7 = Math.min(var7, var10.p0.getOffset());
                  var8 = Math.max(var8, var10.p1.getOffset());
               }
            }

            if (var3 != var5 && var4 != var6) {
               this.component.repaint(var3, var4, var5 - var3, var6 - var4);
            }

            if (var7 != -1) {
               try {
                  this.safeDamageRange(var7, var8);
               } catch (BadLocationException var13) {
               }
            }

            this.highlights.removeAllElements();
         }
      } else if (var1 != null) {
         var2 = this.highlights.size();
         if (var2 != 0) {
            var3 = Integer.MAX_VALUE;
            var4 = 0;

            for(var5 = 0; var5 < var2; ++var5) {
               DefaultHighlighter.HighlightInfo var14 = (DefaultHighlighter.HighlightInfo)this.highlights.elementAt(var5);
               var3 = Math.min(var3, var14.p0.getOffset());
               var4 = Math.max(var4, var14.p1.getOffset());
            }

            try {
               this.safeDamageRange(var3, var4);
            } catch (BadLocationException var12) {
            }

            this.highlights.removeAllElements();
         }
      }

   }

   public void changeHighlight(Object var1, int var2, int var3) throws BadLocationException {
      if (var2 < 0) {
         throw new BadLocationException("Invalid beginning of the range", var2);
      } else if (var3 < var2) {
         throw new BadLocationException("Invalid end of the range", var3);
      } else {
         Document var4 = this.component.getDocument();
         if (var1 instanceof DefaultHighlighter.LayeredHighlightInfo) {
            DefaultHighlighter.LayeredHighlightInfo var5 = (DefaultHighlighter.LayeredHighlightInfo)var1;
            if (var5.width > 0 && var5.height > 0) {
               this.component.repaint(var5.x, var5.y, var5.width, var5.height);
            }

            var5.width = var5.height = 0;
            var5.p0 = var4.createPosition(var2);
            var5.p1 = var4.createPosition(var3);
            this.safeDamageRange(Math.min(var2, var3), Math.max(var2, var3));
         } else {
            DefaultHighlighter.HighlightInfo var8 = (DefaultHighlighter.HighlightInfo)var1;
            int var6 = var8.p0.getOffset();
            int var7 = var8.p1.getOffset();
            if (var2 == var6) {
               this.safeDamageRange(Math.min(var7, var3), Math.max(var7, var3));
            } else if (var3 == var7) {
               this.safeDamageRange(Math.min(var2, var6), Math.max(var2, var6));
            } else {
               this.safeDamageRange(var6, var7);
               this.safeDamageRange(var2, var3);
            }

            var8.p0 = var4.createPosition(var2);
            var8.p1 = var4.createPosition(var3);
         }

      }
   }

   public Highlighter.Highlight[] getHighlights() {
      int var1 = this.highlights.size();
      if (var1 == 0) {
         return noHighlights;
      } else {
         Highlighter.Highlight[] var2 = new Highlighter.Highlight[var1];
         this.highlights.copyInto(var2);
         return var2;
      }
   }

   public void paintLayeredHighlights(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
      for(int var7 = this.highlights.size() - 1; var7 >= 0; --var7) {
         DefaultHighlighter.HighlightInfo var8 = (DefaultHighlighter.HighlightInfo)this.highlights.elementAt(var7);
         if (var8 instanceof DefaultHighlighter.LayeredHighlightInfo) {
            DefaultHighlighter.LayeredHighlightInfo var9 = (DefaultHighlighter.LayeredHighlightInfo)var8;
            int var10 = var9.getStartOffset();
            int var11 = var9.getEndOffset();
            if (var2 < var10 && var3 > var10 || var2 >= var10 && var2 < var11) {
               var9.paintLayeredHighlights(var1, var2, var3, var4, var5, var6);
            }
         }
      }

   }

   private void safeDamageRange(Position var1, Position var2) {
      this.safeDamager.damageRange(var1, var2);
   }

   private void safeDamageRange(int var1, int var2) throws BadLocationException {
      Document var3 = this.component.getDocument();
      this.safeDamageRange(var3.createPosition(var1), var3.createPosition(var2));
   }

   public void setDrawsLayeredHighlights(boolean var1) {
      this.drawsLayeredHighlights = var1;
   }

   public boolean getDrawsLayeredHighlights() {
      return this.drawsLayeredHighlights;
   }

   class SafeDamager implements Runnable {
      private Vector<Position> p0 = new Vector(10);
      private Vector<Position> p1 = new Vector(10);
      private Document lastDoc = null;

      public synchronized void run() {
         if (DefaultHighlighter.this.component != null) {
            TextUI var1 = DefaultHighlighter.this.component.getUI();
            if (var1 != null && this.lastDoc == DefaultHighlighter.this.component.getDocument()) {
               int var2 = this.p0.size();

               for(int var3 = 0; var3 < var2; ++var3) {
                  var1.damageRange(DefaultHighlighter.this.component, ((Position)this.p0.get(var3)).getOffset(), ((Position)this.p1.get(var3)).getOffset());
               }
            }
         }

         this.p0.clear();
         this.p1.clear();
         this.lastDoc = null;
      }

      public synchronized void damageRange(Position var1, Position var2) {
         if (DefaultHighlighter.this.component == null) {
            this.p0.clear();
            this.lastDoc = null;
         } else {
            boolean var3 = this.p0.isEmpty();
            Document var4 = DefaultHighlighter.this.component.getDocument();
            if (var4 != this.lastDoc) {
               if (!this.p0.isEmpty()) {
                  this.p0.clear();
                  this.p1.clear();
               }

               this.lastDoc = var4;
            }

            this.p0.add(var1);
            this.p1.add(var2);
            if (var3) {
               SwingUtilities.invokeLater(this);
            }

         }
      }
   }

   class LayeredHighlightInfo extends DefaultHighlighter.HighlightInfo {
      int x;
      int y;
      int width;
      int height;

      LayeredHighlightInfo() {
         super();
      }

      void union(Shape var1) {
         if (var1 != null) {
            Rectangle var2;
            if (var1 instanceof Rectangle) {
               var2 = (Rectangle)var1;
            } else {
               var2 = var1.getBounds();
            }

            if (this.width != 0 && this.height != 0) {
               this.width = Math.max(this.x + this.width, var2.x + var2.width);
               this.height = Math.max(this.y + this.height, var2.y + var2.height);
               this.x = Math.min(this.x, var2.x);
               this.width -= this.x;
               this.y = Math.min(this.y, var2.y);
               this.height -= this.y;
            } else {
               this.x = var2.x;
               this.y = var2.y;
               this.width = var2.width;
               this.height = var2.height;
            }

         }
      }

      void paintLayeredHighlights(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
         int var7 = this.getStartOffset();
         int var8 = this.getEndOffset();
         var2 = Math.max(var7, var2);
         var3 = Math.min(var8, var3);
         this.union(((LayeredHighlighter.LayerPainter)this.painter).paintLayer(var1, var2, var3, var4, var5, var6));
      }
   }

   class HighlightInfo implements Highlighter.Highlight {
      Position p0;
      Position p1;
      Highlighter.HighlightPainter painter;

      public int getStartOffset() {
         return this.p0.getOffset();
      }

      public int getEndOffset() {
         return this.p1.getOffset();
      }

      public Highlighter.HighlightPainter getPainter() {
         return this.painter;
      }
   }

   public static class DefaultHighlightPainter extends LayeredHighlighter.LayerPainter {
      private Color color;

      public DefaultHighlightPainter(Color var1) {
         this.color = var1;
      }

      public Color getColor() {
         return this.color;
      }

      public void paint(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5) {
         Rectangle var6 = var4.getBounds();

         try {
            TextUI var7 = var5.getUI();
            Rectangle var8 = var7.modelToView(var5, var2);
            Rectangle var9 = var7.modelToView(var5, var3);
            Color var10 = this.getColor();
            if (var10 == null) {
               var1.setColor(var5.getSelectionColor());
            } else {
               var1.setColor(var10);
            }

            if (var8.y == var9.y) {
               Rectangle var11 = var8.union(var9);
               var1.fillRect(var11.x, var11.y, var11.width, var11.height);
            } else {
               int var13 = var6.x + var6.width - var8.x;
               var1.fillRect(var8.x, var8.y, var13, var8.height);
               if (var8.y + var8.height != var9.y) {
                  var1.fillRect(var6.x, var8.y + var8.height, var6.width, var9.y - (var8.y + var8.height));
               }

               var1.fillRect(var6.x, var9.y, var9.x - var6.x, var9.height);
            }
         } catch (BadLocationException var12) {
         }

      }

      public Shape paintLayer(Graphics var1, int var2, int var3, Shape var4, JTextComponent var5, View var6) {
         Color var7 = this.getColor();
         if (var7 == null) {
            var1.setColor(var5.getSelectionColor());
         } else {
            var1.setColor(var7);
         }

         Rectangle var8;
         if (var2 == var6.getStartOffset() && var3 == var6.getEndOffset()) {
            if (var4 instanceof Rectangle) {
               var8 = (Rectangle)var4;
            } else {
               var8 = var4.getBounds();
            }
         } else {
            try {
               Shape var9 = var6.modelToView(var2, Position.Bias.Forward, var3, Position.Bias.Backward, var4);
               var8 = var9 instanceof Rectangle ? (Rectangle)var9 : var9.getBounds();
            } catch (BadLocationException var10) {
               var8 = null;
            }
         }

         if (var8 != null) {
            var8.width = Math.max(var8.width, 1);
            var1.fillRect(var8.x, var8.y, var8.width, var8.height);
         }

         return var8;
      }
   }
}
