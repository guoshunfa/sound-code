package java.awt;

public class BufferCapabilities implements Cloneable {
   private ImageCapabilities frontCaps;
   private ImageCapabilities backCaps;
   private BufferCapabilities.FlipContents flipContents;

   public BufferCapabilities(ImageCapabilities var1, ImageCapabilities var2, BufferCapabilities.FlipContents var3) {
      if (var1 != null && var2 != null) {
         this.frontCaps = var1;
         this.backCaps = var2;
         this.flipContents = var3;
      } else {
         throw new IllegalArgumentException("Image capabilities specified cannot be null");
      }
   }

   public ImageCapabilities getFrontBufferCapabilities() {
      return this.frontCaps;
   }

   public ImageCapabilities getBackBufferCapabilities() {
      return this.backCaps;
   }

   public boolean isPageFlipping() {
      return this.getFlipContents() != null;
   }

   public BufferCapabilities.FlipContents getFlipContents() {
      return this.flipContents;
   }

   public boolean isFullScreenRequired() {
      return false;
   }

   public boolean isMultiBufferAvailable() {
      return false;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public static final class FlipContents extends AttributeValue {
      private static int I_UNDEFINED = 0;
      private static int I_BACKGROUND = 1;
      private static int I_PRIOR = 2;
      private static int I_COPIED = 3;
      private static final String[] NAMES = new String[]{"undefined", "background", "prior", "copied"};
      public static final BufferCapabilities.FlipContents UNDEFINED;
      public static final BufferCapabilities.FlipContents BACKGROUND;
      public static final BufferCapabilities.FlipContents PRIOR;
      public static final BufferCapabilities.FlipContents COPIED;

      private FlipContents(int var1) {
         super(var1, NAMES);
      }

      static {
         UNDEFINED = new BufferCapabilities.FlipContents(I_UNDEFINED);
         BACKGROUND = new BufferCapabilities.FlipContents(I_BACKGROUND);
         PRIOR = new BufferCapabilities.FlipContents(I_PRIOR);
         COPIED = new BufferCapabilities.FlipContents(I_COPIED);
      }
   }
}
