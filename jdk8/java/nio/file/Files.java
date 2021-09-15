package java.nio.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.spi.FileSystemProvider;
import java.nio.file.spi.FileTypeDetector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.nio.fs.DefaultFileTypeDetector;

public final class Files {
   private static final int BUFFER_SIZE = 8192;
   private static final int MAX_BUFFER_SIZE = 2147483639;

   private Files() {
   }

   private static FileSystemProvider provider(Path var0) {
      return var0.getFileSystem().provider();
   }

   private static Runnable asUncheckedRunnable(Closeable var0) {
      return () -> {
         try {
            var0.close();
         } catch (IOException var2) {
            throw new UncheckedIOException(var2);
         }
      };
   }

   public static InputStream newInputStream(Path var0, OpenOption... var1) throws IOException {
      return provider(var0).newInputStream(var0, var1);
   }

   public static OutputStream newOutputStream(Path var0, OpenOption... var1) throws IOException {
      return provider(var0).newOutputStream(var0, var1);
   }

   public static SeekableByteChannel newByteChannel(Path var0, Set<? extends OpenOption> var1, FileAttribute<?>... var2) throws IOException {
      return provider(var0).newByteChannel(var0, var1, var2);
   }

   public static SeekableByteChannel newByteChannel(Path var0, OpenOption... var1) throws IOException {
      HashSet var2 = new HashSet(var1.length);
      Collections.addAll(var2, var1);
      return newByteChannel(var0, var2);
   }

   public static DirectoryStream<Path> newDirectoryStream(Path var0) throws IOException {
      return provider(var0).newDirectoryStream(var0, Files.AcceptAllFilter.FILTER);
   }

   public static DirectoryStream<Path> newDirectoryStream(Path var0, String var1) throws IOException {
      if (var1.equals("*")) {
         return newDirectoryStream(var0);
      } else {
         FileSystem var2 = var0.getFileSystem();
         final PathMatcher var3 = var2.getPathMatcher("glob:" + var1);
         DirectoryStream.Filter var4 = new DirectoryStream.Filter<Path>() {
            public boolean accept(Path var1) {
               return var3.matches(var1.getFileName());
            }
         };
         return var2.provider().newDirectoryStream(var0, var4);
      }
   }

   public static DirectoryStream<Path> newDirectoryStream(Path var0, DirectoryStream.Filter<? super Path> var1) throws IOException {
      return provider(var0).newDirectoryStream(var0, var1);
   }

   public static Path createFile(Path var0, FileAttribute<?>... var1) throws IOException {
      EnumSet var2 = EnumSet.of(StandardOpenOption.CREATE_NEW, (Enum)StandardOpenOption.WRITE);
      newByteChannel(var0, var2, var1).close();
      return var0;
   }

   public static Path createDirectory(Path var0, FileAttribute<?>... var1) throws IOException {
      provider(var0).createDirectory(var0, var1);
      return var0;
   }

   public static Path createDirectories(Path var0, FileAttribute<?>... var1) throws IOException {
      try {
         createAndCheckIsDirectory(var0, var1);
         return var0;
      } catch (FileAlreadyExistsException var9) {
         throw var9;
      } catch (IOException var10) {
         SecurityException var2 = null;

         try {
            var0 = var0.toAbsolutePath();
         } catch (SecurityException var7) {
            var2 = var7;
         }

         Path var3 = var0.getParent();

         while(var3 != null) {
            try {
               provider(var3).checkAccess(var3);
               break;
            } catch (NoSuchFileException var8) {
               var3 = var3.getParent();
            }
         }

         if (var3 == null) {
            if (var2 == null) {
               throw new FileSystemException(var0.toString(), (String)null, "Unable to determine if root directory exists");
            } else {
               throw var2;
            }
         } else {
            Path var4 = var3;
            Iterator var5 = var3.relativize(var0).iterator();

            while(var5.hasNext()) {
               Path var6 = (Path)var5.next();
               var4 = var4.resolve(var6);
               createAndCheckIsDirectory(var4, var1);
            }

            return var0;
         }
      }
   }

   private static void createAndCheckIsDirectory(Path var0, FileAttribute<?>... var1) throws IOException {
      try {
         createDirectory(var0, var1);
      } catch (FileAlreadyExistsException var3) {
         if (!isDirectory(var0, LinkOption.NOFOLLOW_LINKS)) {
            throw var3;
         }
      }

   }

   public static Path createTempFile(Path var0, String var1, String var2, FileAttribute<?>... var3) throws IOException {
      return TempFileHelper.createTempFile((Path)Objects.requireNonNull(var0), var1, var2, var3);
   }

   public static Path createTempFile(String var0, String var1, FileAttribute<?>... var2) throws IOException {
      return TempFileHelper.createTempFile((Path)null, var0, var1, var2);
   }

   public static Path createTempDirectory(Path var0, String var1, FileAttribute<?>... var2) throws IOException {
      return TempFileHelper.createTempDirectory((Path)Objects.requireNonNull(var0), var1, var2);
   }

   public static Path createTempDirectory(String var0, FileAttribute<?>... var1) throws IOException {
      return TempFileHelper.createTempDirectory((Path)null, var0, var1);
   }

   public static Path createSymbolicLink(Path var0, Path var1, FileAttribute<?>... var2) throws IOException {
      provider(var0).createSymbolicLink(var0, var1, var2);
      return var0;
   }

   public static Path createLink(Path var0, Path var1) throws IOException {
      provider(var0).createLink(var0, var1);
      return var0;
   }

   public static void delete(Path var0) throws IOException {
      provider(var0).delete(var0);
   }

   public static boolean deleteIfExists(Path var0) throws IOException {
      return provider(var0).deleteIfExists(var0);
   }

   public static Path copy(Path var0, Path var1, CopyOption... var2) throws IOException {
      FileSystemProvider var3 = provider(var0);
      if (provider(var1) == var3) {
         var3.copy(var0, var1, var2);
      } else {
         CopyMoveHelper.copyToForeignTarget(var0, var1, var2);
      }

      return var1;
   }

   public static Path move(Path var0, Path var1, CopyOption... var2) throws IOException {
      FileSystemProvider var3 = provider(var0);
      if (provider(var1) == var3) {
         var3.move(var0, var1, var2);
      } else {
         CopyMoveHelper.moveToForeignTarget(var0, var1, var2);
      }

      return var1;
   }

   public static Path readSymbolicLink(Path var0) throws IOException {
      return provider(var0).readSymbolicLink(var0);
   }

   public static FileStore getFileStore(Path var0) throws IOException {
      return provider(var0).getFileStore(var0);
   }

   public static boolean isSameFile(Path var0, Path var1) throws IOException {
      return provider(var0).isSameFile(var0, var1);
   }

   public static boolean isHidden(Path var0) throws IOException {
      return provider(var0).isHidden(var0);
   }

   public static String probeContentType(Path var0) throws IOException {
      Iterator var1 = Files.FileTypeDetectors.installeDetectors.iterator();

      String var3;
      do {
         if (!var1.hasNext()) {
            return Files.FileTypeDetectors.defaultFileTypeDetector.probeContentType(var0);
         }

         FileTypeDetector var2 = (FileTypeDetector)var1.next();
         var3 = var2.probeContentType(var0);
      } while(var3 == null);

      return var3;
   }

   public static <V extends FileAttributeView> V getFileAttributeView(Path var0, Class<V> var1, LinkOption... var2) {
      return provider(var0).getFileAttributeView(var0, var1, var2);
   }

   public static <A extends BasicFileAttributes> A readAttributes(Path var0, Class<A> var1, LinkOption... var2) throws IOException {
      return provider(var0).readAttributes(var0, var1, var2);
   }

   public static Path setAttribute(Path var0, String var1, Object var2, LinkOption... var3) throws IOException {
      provider(var0).setAttribute(var0, var1, var2, var3);
      return var0;
   }

   public static Object getAttribute(Path var0, String var1, LinkOption... var2) throws IOException {
      if (var1.indexOf(42) < 0 && var1.indexOf(44) < 0) {
         Map var3 = readAttributes(var0, var1, var2);

         assert var3.size() == 1;

         int var5 = var1.indexOf(58);
         String var4;
         if (var5 == -1) {
            var4 = var1;
         } else {
            var4 = var5 == var1.length() ? "" : var1.substring(var5 + 1);
         }

         return var3.get(var4);
      } else {
         throw new IllegalArgumentException(var1);
      }
   }

   public static Map<String, Object> readAttributes(Path var0, String var1, LinkOption... var2) throws IOException {
      return provider(var0).readAttributes(var0, var1, var2);
   }

   public static Set<PosixFilePermission> getPosixFilePermissions(Path var0, LinkOption... var1) throws IOException {
      return ((PosixFileAttributes)readAttributes(var0, PosixFileAttributes.class, var1)).permissions();
   }

   public static Path setPosixFilePermissions(Path var0, Set<PosixFilePermission> var1) throws IOException {
      PosixFileAttributeView var2 = (PosixFileAttributeView)getFileAttributeView(var0, PosixFileAttributeView.class);
      if (var2 == null) {
         throw new UnsupportedOperationException();
      } else {
         var2.setPermissions(var1);
         return var0;
      }
   }

   public static UserPrincipal getOwner(Path var0, LinkOption... var1) throws IOException {
      FileOwnerAttributeView var2 = (FileOwnerAttributeView)getFileAttributeView(var0, FileOwnerAttributeView.class, var1);
      if (var2 == null) {
         throw new UnsupportedOperationException();
      } else {
         return var2.getOwner();
      }
   }

   public static Path setOwner(Path var0, UserPrincipal var1) throws IOException {
      FileOwnerAttributeView var2 = (FileOwnerAttributeView)getFileAttributeView(var0, FileOwnerAttributeView.class);
      if (var2 == null) {
         throw new UnsupportedOperationException();
      } else {
         var2.setOwner(var1);
         return var0;
      }
   }

   public static boolean isSymbolicLink(Path var0) {
      try {
         return readAttributes(var0, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS).isSymbolicLink();
      } catch (IOException var2) {
         return false;
      }
   }

   public static boolean isDirectory(Path var0, LinkOption... var1) {
      try {
         return readAttributes(var0, BasicFileAttributes.class, var1).isDirectory();
      } catch (IOException var3) {
         return false;
      }
   }

   public static boolean isRegularFile(Path var0, LinkOption... var1) {
      try {
         return readAttributes(var0, BasicFileAttributes.class, var1).isRegularFile();
      } catch (IOException var3) {
         return false;
      }
   }

   public static FileTime getLastModifiedTime(Path var0, LinkOption... var1) throws IOException {
      return readAttributes(var0, BasicFileAttributes.class, var1).lastModifiedTime();
   }

   public static Path setLastModifiedTime(Path var0, FileTime var1) throws IOException {
      ((BasicFileAttributeView)getFileAttributeView(var0, BasicFileAttributeView.class)).setTimes(var1, (FileTime)null, (FileTime)null);
      return var0;
   }

   public static long size(Path var0) throws IOException {
      return readAttributes(var0, BasicFileAttributes.class).size();
   }

   private static boolean followLinks(LinkOption... var0) {
      boolean var1 = true;
      LinkOption[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LinkOption var5 = var2[var4];
         if (var5 != LinkOption.NOFOLLOW_LINKS) {
            if (var5 == null) {
               throw new NullPointerException();
            }

            throw new AssertionError("Should not get here");
         }

         var1 = false;
      }

      return var1;
   }

   public static boolean exists(Path var0, LinkOption... var1) {
      try {
         if (followLinks(var1)) {
            provider(var0).checkAccess(var0);
         } else {
            readAttributes(var0, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
         }

         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public static boolean notExists(Path var0, LinkOption... var1) {
      try {
         if (followLinks(var1)) {
            provider(var0).checkAccess(var0);
         } else {
            readAttributes(var0, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
         }

         return false;
      } catch (NoSuchFileException var3) {
         return true;
      } catch (IOException var4) {
         return false;
      }
   }

   private static boolean isAccessible(Path var0, AccessMode... var1) {
      try {
         provider(var0).checkAccess(var0, var1);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public static boolean isReadable(Path var0) {
      return isAccessible(var0, AccessMode.READ);
   }

   public static boolean isWritable(Path var0) {
      return isAccessible(var0, AccessMode.WRITE);
   }

   public static boolean isExecutable(Path var0) {
      return isAccessible(var0, AccessMode.EXECUTE);
   }

   public static Path walkFileTree(Path var0, Set<FileVisitOption> var1, int var2, FileVisitor<? super Path> var3) throws IOException {
      FileTreeWalker var4 = new FileTreeWalker(var1, var2);
      Throwable var5 = null;

      try {
         FileTreeWalker.Event var6 = var4.walk(var0);

         do {
            FileVisitResult var7;
            switch(var6.type()) {
            case ENTRY:
               IOException var8 = var6.ioeException();
               if (var8 == null) {
                  assert var6.attributes() != null;

                  var7 = var3.visitFile(var6.file(), var6.attributes());
               } else {
                  var7 = var3.visitFileFailed(var6.file(), var8);
               }
               break;
            case START_DIRECTORY:
               var7 = var3.preVisitDirectory(var6.file(), var6.attributes());
               if (var7 == FileVisitResult.SKIP_SUBTREE || var7 == FileVisitResult.SKIP_SIBLINGS) {
                  var4.pop();
               }
               break;
            case END_DIRECTORY:
               var7 = var3.postVisitDirectory(var6.file(), var6.ioeException());
               if (var7 == FileVisitResult.SKIP_SIBLINGS) {
                  var7 = FileVisitResult.CONTINUE;
               }
               break;
            default:
               throw new AssertionError("Should not get here");
            }

            if (Objects.requireNonNull(var7) != FileVisitResult.CONTINUE) {
               if (var7 == FileVisitResult.TERMINATE) {
                  break;
               }

               if (var7 == FileVisitResult.SKIP_SIBLINGS) {
                  var4.skipRemainingSiblings();
               }
            }

            var6 = var4.next();
         } while(var6 != null);
      } catch (Throwable var16) {
         var5 = var16;
         throw var16;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var15) {
                  var5.addSuppressed(var15);
               }
            } else {
               var4.close();
            }
         }

      }

      return var0;
   }

   public static Path walkFileTree(Path var0, FileVisitor<? super Path> var1) throws IOException {
      return walkFileTree(var0, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, var1);
   }

   public static BufferedReader newBufferedReader(Path var0, Charset var1) throws IOException {
      CharsetDecoder var2 = var1.newDecoder();
      InputStreamReader var3 = new InputStreamReader(newInputStream(var0), var2);
      return new BufferedReader(var3);
   }

   public static BufferedReader newBufferedReader(Path var0) throws IOException {
      return newBufferedReader(var0, StandardCharsets.UTF_8);
   }

   public static BufferedWriter newBufferedWriter(Path var0, Charset var1, OpenOption... var2) throws IOException {
      CharsetEncoder var3 = var1.newEncoder();
      OutputStreamWriter var4 = new OutputStreamWriter(newOutputStream(var0, var2), var3);
      return new BufferedWriter(var4);
   }

   public static BufferedWriter newBufferedWriter(Path var0, OpenOption... var1) throws IOException {
      return newBufferedWriter(var0, StandardCharsets.UTF_8, var1);
   }

   private static long copy(InputStream var0, OutputStream var1) throws IOException {
      long var2 = 0L;

      int var5;
      for(byte[] var4 = new byte[8192]; (var5 = var0.read(var4)) > 0; var2 += (long)var5) {
         var1.write(var4, 0, var5);
      }

      return var2;
   }

   public static long copy(InputStream var0, Path var1, CopyOption... var2) throws IOException {
      Objects.requireNonNull(var0);
      boolean var3 = false;
      CopyOption[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         CopyOption var7 = var4[var6];
         if (var7 != StandardCopyOption.REPLACE_EXISTING) {
            if (var7 == null) {
               throw new NullPointerException("options contains 'null'");
            }

            throw new UnsupportedOperationException(var7 + " not supported");
         }

         var3 = true;
      }

      SecurityException var24 = null;
      if (var3) {
         try {
            deleteIfExists(var1);
         } catch (SecurityException var21) {
            var24 = var21;
         }
      }

      OutputStream var25;
      try {
         var25 = newOutputStream(var1, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
      } catch (FileAlreadyExistsException var22) {
         if (var24 != null) {
            throw var24;
         }

         throw var22;
      }

      OutputStream var26 = var25;
      Throwable var27 = null;

      long var8;
      try {
         var8 = copy(var0, var26);
      } catch (Throwable var20) {
         var27 = var20;
         throw var20;
      } finally {
         if (var25 != null) {
            if (var27 != null) {
               try {
                  var26.close();
               } catch (Throwable var19) {
                  var27.addSuppressed(var19);
               }
            } else {
               var25.close();
            }
         }

      }

      return var8;
   }

   public static long copy(Path var0, OutputStream var1) throws IOException {
      Objects.requireNonNull(var1);
      InputStream var2 = newInputStream(var0);
      Throwable var3 = null;

      long var4;
      try {
         var4 = copy(var2, var1);
      } catch (Throwable var14) {
         var3 = var14;
         throw var14;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var13) {
                  var3.addSuppressed(var13);
               }
            } else {
               var2.close();
            }
         }

      }

      return var4;
   }

   private static byte[] read(InputStream var0, int var1) throws IOException {
      int var2 = var1;
      byte[] var3 = new byte[var1];
      int var4 = 0;

      while(true) {
         int var5;
         while((var5 = var0.read(var3, var4, var2 - var4)) > 0) {
            var4 += var5;
         }

         if (var5 < 0 || (var5 = var0.read()) < 0) {
            return var2 == var4 ? var3 : Arrays.copyOf(var3, var4);
         }

         if (var2 <= 2147483639 - var2) {
            var2 = Math.max(var2 << 1, 8192);
         } else {
            if (var2 == 2147483639) {
               throw new OutOfMemoryError("Required array size too large");
            }

            var2 = 2147483639;
         }

         var3 = Arrays.copyOf(var3, var2);
         var3[var4++] = (byte)var5;
      }
   }

   public static byte[] readAllBytes(Path var0) throws IOException {
      SeekableByteChannel var1 = newByteChannel(var0);
      Throwable var2 = null;

      byte[] var7;
      try {
         InputStream var3 = Channels.newInputStream((ReadableByteChannel)var1);
         Throwable var4 = null;

         try {
            long var5 = var1.size();
            if (var5 > 2147483639L) {
               throw new OutOfMemoryError("Required array size too large");
            }

            var7 = read(var3, (int)var5);
         } catch (Throwable var30) {
            var4 = var30;
            throw var30;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var29) {
                     var4.addSuppressed(var29);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (Throwable var32) {
         var2 = var32;
         throw var32;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var28) {
                  var2.addSuppressed(var28);
               }
            } else {
               var1.close();
            }
         }

      }

      return var7;
   }

   public static List<String> readAllLines(Path var0, Charset var1) throws IOException {
      BufferedReader var2 = newBufferedReader(var0, var1);
      Throwable var3 = null;

      try {
         ArrayList var4 = new ArrayList();

         while(true) {
            String var5 = var2.readLine();
            if (var5 == null) {
               ArrayList var16 = var4;
               return var16;
            }

            var4.add(var5);
         }
      } catch (Throwable var14) {
         var3 = var14;
         throw var14;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var13) {
                  var3.addSuppressed(var13);
               }
            } else {
               var2.close();
            }
         }

      }
   }

   public static List<String> readAllLines(Path var0) throws IOException {
      return readAllLines(var0, StandardCharsets.UTF_8);
   }

   public static Path write(Path var0, byte[] var1, OpenOption... var2) throws IOException {
      Objects.requireNonNull(var1);
      OutputStream var3 = newOutputStream(var0, var2);
      Throwable var4 = null;

      try {
         int var5 = var1.length;

         int var7;
         for(int var6 = var5; var6 > 0; var6 -= var7) {
            var7 = Math.min(var6, 8192);
            var3.write(var1, var5 - var6, var7);
         }
      } catch (Throwable var15) {
         var4 = var15;
         throw var15;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var14) {
                  var4.addSuppressed(var14);
               }
            } else {
               var3.close();
            }
         }

      }

      return var0;
   }

   public static Path write(Path var0, Iterable<? extends CharSequence> var1, Charset var2, OpenOption... var3) throws IOException {
      Objects.requireNonNull(var1);
      CharsetEncoder var4 = var2.newEncoder();
      OutputStream var5 = newOutputStream(var0, var3);
      BufferedWriter var6 = new BufferedWriter(new OutputStreamWriter(var5, var4));
      Throwable var7 = null;

      try {
         Iterator var8 = var1.iterator();

         while(var8.hasNext()) {
            CharSequence var9 = (CharSequence)var8.next();
            var6.append(var9);
            var6.newLine();
         }
      } catch (Throwable var17) {
         var7 = var17;
         throw var17;
      } finally {
         if (var6 != null) {
            if (var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var16) {
                  var7.addSuppressed(var16);
               }
            } else {
               var6.close();
            }
         }

      }

      return var0;
   }

   public static Path write(Path var0, Iterable<? extends CharSequence> var1, OpenOption... var2) throws IOException {
      return write(var0, var1, StandardCharsets.UTF_8, var2);
   }

   public static Stream<Path> list(Path var0) throws IOException {
      DirectoryStream var1 = newDirectoryStream(var0);

      try {
         final Iterator var8 = var1.iterator();
         Iterator var9 = new Iterator<Path>() {
            public boolean hasNext() {
               try {
                  return var8.hasNext();
               } catch (DirectoryIteratorException var2) {
                  throw new UncheckedIOException(var2.getCause());
               }
            }

            public Path next() {
               try {
                  return (Path)var8.next();
               } catch (DirectoryIteratorException var2) {
                  throw new UncheckedIOException(var2.getCause());
               }
            }
         };
         return (Stream)StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)var9, 1), false).onClose(asUncheckedRunnable(var1));
      } catch (RuntimeException | Error var7) {
         Error var2 = var7;

         try {
            var1.close();
         } catch (IOException var6) {
            IOException var3 = var6;

            try {
               var2.addSuppressed(var3);
            } catch (Throwable var5) {
            }
         }

         throw var7;
      }
   }

   public static Stream<Path> walk(Path var0, int var1, FileVisitOption... var2) throws IOException {
      FileTreeIterator var3 = new FileTreeIterator(var0, var1, var2);

      try {
         Stream var10000 = StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)var3, 1), false);
         var3.getClass();
         return ((Stream)var10000.onClose(var3::close)).map((var0x) -> {
            return var0x.file();
         });
      } catch (RuntimeException | Error var5) {
         var3.close();
         throw var5;
      }
   }

   public static Stream<Path> walk(Path var0, FileVisitOption... var1) throws IOException {
      return walk(var0, Integer.MAX_VALUE, var1);
   }

   public static Stream<Path> find(Path var0, int var1, BiPredicate<Path, BasicFileAttributes> var2, FileVisitOption... var3) throws IOException {
      FileTreeIterator var4 = new FileTreeIterator(var0, var1, var3);

      try {
         Stream var10000 = StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator)var4, 1), false);
         var4.getClass();
         return ((Stream)var10000.onClose(var4::close)).filter((var1x) -> {
            return var2.test(var1x.file(), var1x.attributes());
         }).map((var0x) -> {
            return var0x.file();
         });
      } catch (RuntimeException | Error var6) {
         var4.close();
         throw var6;
      }
   }

   public static Stream<String> lines(Path var0, Charset var1) throws IOException {
      BufferedReader var2 = newBufferedReader(var0, var1);

      try {
         return (Stream)var2.lines().onClose(asUncheckedRunnable(var2));
      } catch (RuntimeException | Error var8) {
         Error var3 = var8;

         try {
            var2.close();
         } catch (IOException var7) {
            IOException var4 = var7;

            try {
               var3.addSuppressed(var4);
            } catch (Throwable var6) {
            }
         }

         throw var8;
      }
   }

   public static Stream<String> lines(Path var0) throws IOException {
      return lines(var0, StandardCharsets.UTF_8);
   }

   private static class FileTypeDetectors {
      static final FileTypeDetector defaultFileTypeDetector = createDefaultFileTypeDetector();
      static final List<FileTypeDetector> installeDetectors = loadInstalledDetectors();

      private static FileTypeDetector createDefaultFileTypeDetector() {
         return (FileTypeDetector)AccessController.doPrivileged(new PrivilegedAction<FileTypeDetector>() {
            public FileTypeDetector run() {
               return DefaultFileTypeDetector.create();
            }
         });
      }

      private static List<FileTypeDetector> loadInstalledDetectors() {
         return (List)AccessController.doPrivileged(new PrivilegedAction<List<FileTypeDetector>>() {
            public List<FileTypeDetector> run() {
               ArrayList var1 = new ArrayList();
               ServiceLoader var2 = ServiceLoader.load(FileTypeDetector.class, ClassLoader.getSystemClassLoader());
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  FileTypeDetector var4 = (FileTypeDetector)var3.next();
                  var1.add(var4);
               }

               return var1;
            }
         });
      }
   }

   private static class AcceptAllFilter implements DirectoryStream.Filter<Path> {
      static final Files.AcceptAllFilter FILTER = new Files.AcceptAllFilter();

      public boolean accept(Path var1) {
         return true;
      }
   }
}
