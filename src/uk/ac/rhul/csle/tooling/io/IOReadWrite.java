package uk.ac.rhul.csle.tooling.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This is a class containing helper functions for reading and writing files.
 * 
 * @author Robert Michael Walsh
 *
 */
public abstract class IOReadWrite {

  /**
   * This class is only meant to be used in a static context, so we don't want
   * to be able to instantiate it.
   */
  private IOReadWrite() {
  }

  /**
   * This function returns the contents of the given file as a single string.
   * 
   * @param path
   *          The path of the file to read
   * @return The contents of the file as a string
   * @throws IOException
   *           Occurs if the file is unable to be read.
   */
  public static String readFile(String path) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
  }

  /**
   * This function writes the given the string to the file given by the path
   * 
   * @param path
   *          The path of the file to write to
   * @param input
   *          The string to write to a file
   */
  public static void writeFile(String path, String input) {
    try (PrintStream print = new PrintStream(path)) {
      if (path.lastIndexOf('/') != -1) {
        File dir = new File(path.substring(0, path.lastIndexOf('/')));
        if (!dir.exists()) {
          dir.mkdirs();
        }
      }
      print.printf("%s", input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
