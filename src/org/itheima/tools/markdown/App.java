package org.itheima.tools.markdown;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class App
{
  public static void main(String[] args) {

//    if (args == null || args.length <= 1) {
//      showUsage();
//
//      return;
//    }
    String option = args[0];
    if ("-h".equalsIgnoreCase(option) || "-html".equalsIgnoreCase(option)) {
      String mdPath = args[1];
      String htmlPath = null;
      if (args.length >= 3) {
        htmlPath = args[2];
      } else {
        htmlPath = mdPath.replace(".md", ".html");
      } 
      
      try {
        MarkdownUtils.toHtml(mdPath, htmlPath);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } else {
      showUsage();
      return;
    } 
  }
  
  private static void showUsage() {
    try {
      System.out.println(getText("/usage.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private static String getText(String src) throws IOException {
    InputStream fis = null;
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      fis = App.class.getResourceAsStream(src);
      int len = 0;
      byte[] buffer = new byte[1024];
      while ((len = fis.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      String text = baos.toString();
      return text;
    } finally {
      closeIO(fis);
      closeIO(baos);
    } 
  }
  
  private static void closeIO(Closeable io) {
    if (io != null) {
      try {
        io.close();
      } catch (IOException e) {
        e.printStackTrace();
      } 
      io = null;
    } 
  }
}
