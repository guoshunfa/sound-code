package com.apple.eawt;

import java.awt.Window;
import java.io.File;
import java.net.URI;
import java.util.EventObject;
import java.util.List;

public abstract class AppEvent extends EventObject {
   AppEvent() {
      super(Application.getApplication());
   }

   public static class FullScreenEvent extends AppEvent {
      final Window window;

      FullScreenEvent(Window var1) {
         this.window = var1;
      }

      public Window getWindow() {
         return this.window;
      }
   }

   public static class SystemSleepEvent extends AppEvent {
      SystemSleepEvent() {
      }
   }

   public static class ScreenSleepEvent extends AppEvent {
      ScreenSleepEvent() {
      }
   }

   public static class UserSessionEvent extends AppEvent {
      UserSessionEvent() {
      }
   }

   public static class AppHiddenEvent extends AppEvent {
      AppHiddenEvent() {
      }
   }

   public static class AppForegroundEvent extends AppEvent {
      AppForegroundEvent() {
      }
   }

   public static class AppReOpenedEvent extends AppEvent {
      AppReOpenedEvent() {
      }
   }

   public static class QuitEvent extends AppEvent {
      QuitEvent() {
      }
   }

   public static class PreferencesEvent extends AppEvent {
      PreferencesEvent() {
      }
   }

   public static class AboutEvent extends AppEvent {
      AboutEvent() {
      }
   }

   public static class OpenURIEvent extends AppEvent {
      final URI uri;

      OpenURIEvent(URI var1) {
         this.uri = var1;
      }

      public URI getURI() {
         return this.uri;
      }
   }

   public static class PrintFilesEvent extends AppEvent.FilesEvent {
      PrintFilesEvent(List<File> var1) {
         super(var1);
      }
   }

   public static class OpenFilesEvent extends AppEvent.FilesEvent {
      final String searchTerm;

      OpenFilesEvent(List<File> var1, String var2) {
         super(var1);
         this.searchTerm = var2;
      }

      public String getSearchTerm() {
         return this.searchTerm;
      }
   }

   public abstract static class FilesEvent extends AppEvent {
      final List<File> files;

      FilesEvent(List<File> var1) {
         this.files = var1;
      }

      public List<File> getFiles() {
         return this.files;
      }
   }
}
