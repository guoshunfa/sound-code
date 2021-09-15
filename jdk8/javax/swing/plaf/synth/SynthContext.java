package javax.swing.plaf.synth;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;

public class SynthContext {
   private static final Queue<SynthContext> queue = new ConcurrentLinkedQueue();
   private JComponent component;
   private Region region;
   private SynthStyle style;
   private int state;

   static SynthContext getContext(JComponent var0, SynthStyle var1, int var2) {
      return getContext(var0, SynthLookAndFeel.getRegion(var0), var1, var2);
   }

   static SynthContext getContext(JComponent var0, Region var1, SynthStyle var2, int var3) {
      SynthContext var4 = (SynthContext)queue.poll();
      if (var4 == null) {
         var4 = new SynthContext();
      }

      var4.reset(var0, var1, var2, var3);
      return var4;
   }

   static void releaseContext(SynthContext var0) {
      queue.offer(var0);
   }

   SynthContext() {
   }

   public SynthContext(JComponent var1, Region var2, SynthStyle var3, int var4) {
      if (var1 != null && var2 != null && var3 != null) {
         this.reset(var1, var2, var3, var4);
      } else {
         throw new NullPointerException("You must supply a non-null component, region and style");
      }
   }

   public JComponent getComponent() {
      return this.component;
   }

   public Region getRegion() {
      return this.region;
   }

   boolean isSubregion() {
      return this.getRegion().isSubregion();
   }

   void setStyle(SynthStyle var1) {
      this.style = var1;
   }

   public SynthStyle getStyle() {
      return this.style;
   }

   void setComponentState(int var1) {
      this.state = var1;
   }

   public int getComponentState() {
      return this.state;
   }

   void reset(JComponent var1, Region var2, SynthStyle var3, int var4) {
      this.component = var1;
      this.region = var2;
      this.style = var3;
      this.state = var4;
   }

   void dispose() {
      this.component = null;
      this.style = null;
      releaseContext(this);
   }

   SynthPainter getPainter() {
      SynthPainter var1 = this.getStyle().getPainter(this);
      return var1 != null ? var1 : SynthPainter.NULL_PAINTER;
   }
}
