package sun.security.tools.policytool;

class AWTPerm extends Perm {
   public AWTPerm() {
      super("AWTPermission", "java.awt.AWTPermission", new String[]{"accessClipboard", "accessEventQueue", "accessSystemTray", "createRobot", "fullScreenExclusive", "listenToAllAWTEvents", "readDisplayPixels", "replaceKeyboardFocusManager", "setAppletStub", "setWindowAlwaysOnTop", "showWindowWithoutWarningBanner", "toolkitModality", "watchMousePointer"}, (String[])null);
   }
}
