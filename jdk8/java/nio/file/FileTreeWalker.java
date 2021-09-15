package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import sun.nio.fs.BasicFileAttributesHolder;

class FileTreeWalker implements Closeable {
   private final boolean followLinks;
   private final LinkOption[] linkOptions;
   private final int maxDepth;
   private final ArrayDeque<FileTreeWalker.DirectoryNode> stack = new ArrayDeque();
   private boolean closed;

   FileTreeWalker(Collection<FileVisitOption> var1, int var2) {
      boolean var3 = false;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         FileVisitOption var5 = (FileVisitOption)var4.next();
         switch(var5) {
         case FOLLOW_LINKS:
            var3 = true;
            break;
         default:
            throw new AssertionError("Should not get here");
         }
      }

      if (var2 < 0) {
         throw new IllegalArgumentException("'maxDepth' is negative");
      } else {
         this.followLinks = var3;
         this.linkOptions = var3 ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
         this.maxDepth = var2;
      }
   }

   private BasicFileAttributes getAttributes(Path var1, boolean var2) throws IOException {
      BasicFileAttributes var3;
      if (var2 && var1 instanceof BasicFileAttributesHolder && System.getSecurityManager() == null) {
         var3 = ((BasicFileAttributesHolder)var1).get();
         if (var3 != null && (!this.followLinks || !var3.isSymbolicLink())) {
            return var3;
         }
      }

      try {
         var3 = Files.readAttributes(var1, BasicFileAttributes.class, this.linkOptions);
      } catch (IOException var5) {
         if (!this.followLinks) {
            throw var5;
         }

         var3 = Files.readAttributes(var1, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
      }

      return var3;
   }

   private boolean wouldLoop(Path var1, Object var2) {
      Iterator var3 = this.stack.iterator();

      Object var5;
      label30:
      do {
         while(var3.hasNext()) {
            FileTreeWalker.DirectoryNode var4 = (FileTreeWalker.DirectoryNode)var3.next();
            var5 = var4.key();
            if (var2 != null && var5 != null) {
               continue label30;
            }

            try {
               if (Files.isSameFile(var1, var4.directory())) {
                  return true;
               }
            } catch (SecurityException | IOException var7) {
            }
         }

         return false;
      } while(!var2.equals(var5));

      return true;
   }

   private FileTreeWalker.Event visit(Path var1, boolean var2, boolean var3) {
      BasicFileAttributes var4;
      try {
         var4 = this.getAttributes(var1, var3);
      } catch (IOException var10) {
         return new FileTreeWalker.Event(FileTreeWalker.EventType.ENTRY, var1, var10);
      } catch (SecurityException var11) {
         if (var2) {
            return null;
         }

         throw var11;
      }

      int var5 = this.stack.size();
      if (var5 < this.maxDepth && var4.isDirectory()) {
         if (this.followLinks && this.wouldLoop(var1, var4.fileKey())) {
            return new FileTreeWalker.Event(FileTreeWalker.EventType.ENTRY, var1, new FileSystemLoopException(var1.toString()));
         } else {
            DirectoryStream var6 = null;

            try {
               var6 = Files.newDirectoryStream(var1);
            } catch (IOException var8) {
               return new FileTreeWalker.Event(FileTreeWalker.EventType.ENTRY, var1, var8);
            } catch (SecurityException var9) {
               if (var2) {
                  return null;
               }

               throw var9;
            }

            this.stack.push(new FileTreeWalker.DirectoryNode(var1, var4.fileKey(), var6));
            return new FileTreeWalker.Event(FileTreeWalker.EventType.START_DIRECTORY, var1, var4);
         }
      } else {
         return new FileTreeWalker.Event(FileTreeWalker.EventType.ENTRY, var1, var4);
      }
   }

   FileTreeWalker.Event walk(Path var1) {
      if (this.closed) {
         throw new IllegalStateException("Closed");
      } else {
         FileTreeWalker.Event var2 = this.visit(var1, false, false);

         assert var2 != null;

         return var2;
      }
   }

   FileTreeWalker.Event next() {
      FileTreeWalker.DirectoryNode var1 = (FileTreeWalker.DirectoryNode)this.stack.peek();
      if (var1 == null) {
         return null;
      } else {
         FileTreeWalker.Event var2;
         do {
            Path var3 = null;
            IOException var4 = null;
            if (!var1.skipped()) {
               Iterator var5 = var1.iterator();

               try {
                  if (var5.hasNext()) {
                     var3 = (Path)var5.next();
                  }
               } catch (DirectoryIteratorException var7) {
                  var4 = var7.getCause();
               }
            }

            if (var3 == null) {
               try {
                  var1.stream().close();
               } catch (IOException var8) {
                  if (var4 != null) {
                     var4 = var8;
                  } else {
                     var4.addSuppressed(var8);
                  }
               }

               this.stack.pop();
               return new FileTreeWalker.Event(FileTreeWalker.EventType.END_DIRECTORY, var1.directory(), var4);
            }

            var2 = this.visit(var3, true, true);
         } while(var2 == null);

         return var2;
      }
   }

   void pop() {
      if (!this.stack.isEmpty()) {
         FileTreeWalker.DirectoryNode var1 = (FileTreeWalker.DirectoryNode)this.stack.pop();

         try {
            var1.stream().close();
         } catch (IOException var3) {
         }
      }

   }

   void skipRemainingSiblings() {
      if (!this.stack.isEmpty()) {
         ((FileTreeWalker.DirectoryNode)this.stack.peek()).skip();
      }

   }

   boolean isOpen() {
      return !this.closed;
   }

   public void close() {
      if (!this.closed) {
         while(true) {
            if (this.stack.isEmpty()) {
               this.closed = true;
               break;
            }

            this.pop();
         }
      }

   }

   static class Event {
      private final FileTreeWalker.EventType type;
      private final Path file;
      private final BasicFileAttributes attrs;
      private final IOException ioe;

      private Event(FileTreeWalker.EventType var1, Path var2, BasicFileAttributes var3, IOException var4) {
         this.type = var1;
         this.file = var2;
         this.attrs = var3;
         this.ioe = var4;
      }

      Event(FileTreeWalker.EventType var1, Path var2, BasicFileAttributes var3) {
         this(var1, var2, var3, (IOException)null);
      }

      Event(FileTreeWalker.EventType var1, Path var2, IOException var3) {
         this(var1, var2, (BasicFileAttributes)null, var3);
      }

      FileTreeWalker.EventType type() {
         return this.type;
      }

      Path file() {
         return this.file;
      }

      BasicFileAttributes attributes() {
         return this.attrs;
      }

      IOException ioeException() {
         return this.ioe;
      }
   }

   static enum EventType {
      START_DIRECTORY,
      END_DIRECTORY,
      ENTRY;
   }

   private static class DirectoryNode {
      private final Path dir;
      private final Object key;
      private final DirectoryStream<Path> stream;
      private final Iterator<Path> iterator;
      private boolean skipped;

      DirectoryNode(Path var1, Object var2, DirectoryStream<Path> var3) {
         this.dir = var1;
         this.key = var2;
         this.stream = var3;
         this.iterator = var3.iterator();
      }

      Path directory() {
         return this.dir;
      }

      Object key() {
         return this.key;
      }

      DirectoryStream<Path> stream() {
         return this.stream;
      }

      Iterator<Path> iterator() {
         return this.iterator;
      }

      void skip() {
         this.skipped = true;
      }

      boolean skipped() {
         return this.skipped;
      }
   }
}
