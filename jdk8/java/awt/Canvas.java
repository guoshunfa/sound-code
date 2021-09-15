package java.awt;

import java.awt.image.BufferStrategy;
import java.awt.peer.CanvasPeer;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Canvas extends Component implements Accessible {
   private static final String base = "canvas";
   private static int nameCounter = 0;
   private static final long serialVersionUID = -2284879212465893870L;

   public Canvas() {
   }

   public Canvas(GraphicsConfiguration var1) {
      this();
      this.setGraphicsConfiguration(var1);
   }

   void setGraphicsConfiguration(GraphicsConfiguration var1) {
      synchronized(this.getTreeLock()) {
         CanvasPeer var3 = (CanvasPeer)this.getPeer();
         if (var3 != null) {
            var1 = var3.getAppropriateGraphicsConfiguration(var1);
         }

         super.setGraphicsConfiguration(var1);
      }
   }

   String constructComponentName() {
      Class var1 = Canvas.class;
      synchronized(Canvas.class) {
         return "canvas" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createCanvas(this);
         }

         super.addNotify();
      }
   }

   public void paint(Graphics var1) {
      var1.clearRect(0, 0, this.width, this.height);
   }

   public void update(Graphics var1) {
      var1.clearRect(0, 0, this.width, this.height);
      this.paint(var1);
   }

   boolean postsOldMouseEvents() {
      return true;
   }

   public void createBufferStrategy(int var1) {
      super.createBufferStrategy(var1);
   }

   public void createBufferStrategy(int var1, BufferCapabilities var2) throws AWTException {
      super.createBufferStrategy(var1, var2);
   }

   public BufferStrategy getBufferStrategy() {
      return super.getBufferStrategy();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Canvas.AccessibleAWTCanvas();
      }

      return this.accessibleContext;
   }

   protected class AccessibleAWTCanvas extends Component.AccessibleAWTComponent {
      private static final long serialVersionUID = -6325592262103146699L;

      protected AccessibleAWTCanvas() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.CANVAS;
      }
   }
}
