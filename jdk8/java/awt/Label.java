package java.awt;

import java.awt.peer.LabelPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Label extends Component implements Accessible {
   public static final int LEFT = 0;
   public static final int CENTER = 1;
   public static final int RIGHT = 2;
   String text;
   int alignment;
   private static final String base = "label";
   private static int nameCounter;
   private static final long serialVersionUID = 3094126758329070636L;

   public Label() throws HeadlessException {
      this("", 0);
   }

   public Label(String var1) throws HeadlessException {
      this(var1, 0);
   }

   public Label(String var1, int var2) throws HeadlessException {
      this.alignment = 0;
      GraphicsEnvironment.checkHeadless();
      this.text = var1;
      this.setAlignment(var2);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      GraphicsEnvironment.checkHeadless();
      var1.defaultReadObject();
   }

   String constructComponentName() {
      Class var1 = Label.class;
      synchronized(Label.class) {
         return "label" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = this.getToolkit().createLabel(this);
         }

         super.addNotify();
      }
   }

   public int getAlignment() {
      return this.alignment;
   }

   public synchronized void setAlignment(int var1) {
      switch(var1) {
      case 0:
      case 1:
      case 2:
         this.alignment = var1;
         LabelPeer var2 = (LabelPeer)this.peer;
         if (var2 != null) {
            var2.setAlignment(var1);
         }

         return;
      default:
         throw new IllegalArgumentException("improper alignment: " + var1);
      }
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      boolean var2 = false;
      synchronized(this) {
         if (var1 != this.text && (this.text == null || !this.text.equals(var1))) {
            this.text = var1;
            LabelPeer var4 = (LabelPeer)this.peer;
            if (var4 != null) {
               var4.setText(var1);
            }

            var2 = true;
         }
      }

      if (var2) {
         this.invalidateIfValid();
      }

   }

   protected String paramString() {
      String var1 = "";
      switch(this.alignment) {
      case 0:
         var1 = "left";
         break;
      case 1:
         var1 = "center";
         break;
      case 2:
         var1 = "right";
      }

      return super.paramString() + ",align=" + var1 + ",text=" + this.text;
   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Label.AccessibleAWTLabel();
      }

      return this.accessibleContext;
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      nameCounter = 0;
   }

   protected class AccessibleAWTLabel extends Component.AccessibleAWTComponent {
      private static final long serialVersionUID = -3568967560160480438L;

      public AccessibleAWTLabel() {
         super();
      }

      public String getAccessibleName() {
         if (this.accessibleName != null) {
            return this.accessibleName;
         } else {
            return Label.this.getText() == null ? super.getAccessibleName() : Label.this.getText();
         }
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.LABEL;
      }
   }
}
