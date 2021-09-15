package sun.lwawt.macosx;

final class NSEvent {
   static final int SCROLL_PHASE_UNSUPPORTED = 1;
   static final int SCROLL_PHASE_BEGAN = 2;
   static final int SCROLL_PHASE_CONTINUED = 3;
   static final int SCROLL_PHASE_MOMENTUM_BEGAN = 4;
   static final int SCROLL_PHASE_ENDED = 5;
   private int type;
   private int modifierFlags;
   private int clickCount;
   private int buttonNumber;
   private int x;
   private int y;
   private double scrollDeltaY;
   private double scrollDeltaX;
   private int scrollPhase;
   private int absX;
   private int absY;
   private short keyCode;
   private String characters;
   private String charactersIgnoringModifiers;

   NSEvent(int var1, int var2, short var3, String var4, String var5) {
      this.type = var1;
      this.modifierFlags = var2;
      this.keyCode = var3;
      this.characters = var4;
      this.charactersIgnoringModifiers = var5;
   }

   NSEvent(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, double var9, double var11, int var13) {
      this.type = var1;
      this.modifierFlags = var2;
      this.clickCount = var3;
      this.buttonNumber = var4;
      this.x = var5;
      this.y = var6;
      this.absX = var7;
      this.absY = var8;
      this.scrollDeltaY = var9;
      this.scrollDeltaX = var11;
      this.scrollPhase = var13;
   }

   int getType() {
      return this.type;
   }

   int getModifierFlags() {
      return this.modifierFlags;
   }

   int getClickCount() {
      return this.clickCount;
   }

   int getButtonNumber() {
      return this.buttonNumber;
   }

   int getX() {
      return this.x;
   }

   int getY() {
      return this.y;
   }

   double getScrollDeltaY() {
      return this.scrollDeltaY;
   }

   double getScrollDeltaX() {
      return this.scrollDeltaX;
   }

   int getScrollPhase() {
      return this.scrollPhase;
   }

   int getAbsX() {
      return this.absX;
   }

   int getAbsY() {
      return this.absY;
   }

   short getKeyCode() {
      return this.keyCode;
   }

   String getCharactersIgnoringModifiers() {
      return this.charactersIgnoringModifiers;
   }

   String getCharacters() {
      return this.characters;
   }

   public String toString() {
      return "NSEvent[" + this.getType() + " ," + this.getModifierFlags() + " ," + this.getClickCount() + " ," + this.getButtonNumber() + " ," + this.getX() + " ," + this.getY() + " ," + this.getAbsX() + " ," + this.getAbsY() + " ," + this.getKeyCode() + " ," + this.getCharacters() + " ," + this.getCharactersIgnoringModifiers() + "]";
   }

   static int nsToJavaButton(int var0) {
      int var1 = var0 + 1;
      switch(var0) {
      case 0:
         var1 = 1;
         break;
      case 1:
         var1 = 3;
         break;
      case 2:
         var1 = 2;
      }

      return var1;
   }

   static int npToJavaEventType(int var0) {
      short var1 = 0;
      switch(var0) {
      case 2:
         var1 = 501;
         break;
      case 3:
         var1 = 502;
         break;
      case 4:
         var1 = 503;
         break;
      case 5:
         var1 = 504;
         break;
      case 6:
         var1 = 505;
         break;
      case 7:
         var1 = 506;
         break;
      case 8:
         var1 = 401;
         break;
      case 9:
         var1 = 402;
      }

      return var1;
   }

   static int nsToJavaEventType(int var0) {
      short var1 = 0;
      switch(var0) {
      case 1:
      case 3:
      case 25:
         var1 = 501;
         break;
      case 2:
      case 4:
      case 26:
         var1 = 502;
         break;
      case 5:
         var1 = 503;
         break;
      case 6:
      case 7:
      case 27:
         var1 = 506;
         break;
      case 8:
         var1 = 504;
         break;
      case 9:
         var1 = 505;
         break;
      case 10:
         var1 = 401;
         break;
      case 11:
         var1 = 402;
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 23:
      case 24:
      default:
         break;
      case 22:
         var1 = 507;
      }

      return var1;
   }

   static native int nsToJavaMouseModifiers(int var0, int var1);

   static native int nsToJavaKeyModifiers(int var0);

   static native boolean nsToJavaKeyInfo(int[] var0, int[] var1);

   static native void nsKeyModifiersToJavaKeyInfo(int[] var0, int[] var1);

   static native char nsToJavaChar(char var0, int var1);

   static boolean isPopupTrigger(int var0) {
      boolean var1 = (var0 & 4096) != 0;
      boolean var2 = (var0 & 1024) != 0;
      boolean var3 = (var0 & 128) != 0;
      return var1 || var3 && var2;
   }
}
