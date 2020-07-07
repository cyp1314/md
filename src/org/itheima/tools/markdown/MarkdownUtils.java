package org.itheima.tools.markdown;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.itheima.tools.markdown.impl.PegdownImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkdownUtils
{
  private static Markdown markdown = (Markdown)new PegdownImpl();

  
  public static void toHtml(String markdownPath, String toHtmlPath) throws IOException { toHtml(new File(markdownPath), new File(toHtmlPath)); }

  
  public static void toHtml(File markdownFile, File toHtmlFile) throws IOException {
    String name = toHtmlFile.getName();
    toHtml(new FileInputStream(markdownFile), new FileOutputStream(toHtmlFile), name.replaceAll(".html", ""));
  }
  
  public static void toHtml(InputStream markdownStream, OutputStream toHtmlStream, String title) throws IOException {
    ByteArrayOutputStream baos = null;
    BufferedWriter writer = null;
    try {
      baos = new ByteArrayOutputStream();
      int len = -1;
      byte[] buffer = new byte[1024];
      while ((len = markdownStream.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      byte[] byteArray = baos.toByteArray();




      
      String bodyHtml = markdown.parse(new String(byteArray));

      
      String body = getBody(bodyHtml);

      
      String html = getTemplate(title, body);
      
      writer = new BufferedWriter(new OutputStreamWriter(toHtmlStream));
      writer.write(html);
    } finally {
      closeIO(markdownStream);
      closeIO(baos);
      closeIO(writer);
      closeIO(toHtmlStream);
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
  
  private static String getBody(String bodyHtml) {
    Document doc = Jsoup.parse(bodyHtml);
    Elements imgs = doc.select("img[src]");
    if (imgs != null) {
      for (Element element : imgs) {
        String src = element.attr("src");
        src = "D:\\documents\\changgou\\讲义\\day01\\讲义\\" +src;
        try {
          String image = base64Image(src);
          element.attr("src", "data:image/jpg;base64," + image);
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } 
    }
    
    return doc.toString();
  }

  
  private static String base64Image(String src) throws IOException {
    FileInputStream fis = null;
    ByteArrayOutputStream baos = null;
    try {
      String file = src;
      if (src.startsWith("file:")) {
        URI uri = URI.create(src);
        URL url = uri.toURL();
        file = url.getFile();
      } 
      
      baos = new ByteArrayOutputStream();
      fis = new FileInputStream(new File(file));
      int len = 0;
      byte[] buffer = new byte[1024];
      while ((len = fis.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      byte[] data = baos.toByteArray();
      return new String(Base64.encodeBase64(data));
    } finally {
      closeIO(fis);
      closeIO(baos);
    } 
  }


  
  private static String getTemplate(String title, String body) {
    VelocityEngine ve = new VelocityEngine();

    String path = App.class.getResource("vm").getPath();
    ve.setProperty(ve.FILE_RESOURCE_LOADER_PATH,path);

    
    ve.setProperty("resource.loader", "classpath");
    ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

    
    ve.setProperty("ISO-8859-1", "utf-8");
    ve.setProperty("input.encoding", "utf-8");
    ve.setProperty("output.encoding", "utf-8");
    
    ve.init();
    Template template = ve.getTemplate("html.vm");

    
    VelocityContext ctx = new VelocityContext();
    ctx.put("title", title);
    try {
      ctx.put("css", getJsOrcss("css/github-markdown.css"));
      ctx.put("treeCSS", getJsOrcss("css/zTreeStyle.css"));
      ctx.put("jqueryJS", getJsOrcss("js/jquery-1.4.4.min.js"));
      ctx.put("treeCodeJS", getJsOrcss("js/jquery.ztree.all-3.5.min.js"));
      ctx.put("treeTocJS", getJsOrcss("js/ztree_toc.min.js"));
      ctx.put("highlightCSS", getJsOrcss("css/highlight.css"));
      ctx.put("highlight", getJsOrcss("js/highlight.min.js"));
      ctx.put("highlightnumber", getJsOrcss("js/highlightjs-line-numbers.min.js"));
    } catch (IOException e) {
      e.printStackTrace();
    } 
    ctx.put("body", body);

    
    StringWriter sw = new StringWriter();
    template.merge((Context)ctx, sw);
    
    return sw.toString();
  }
  
  private static String getJsOrcss(String src) throws IOException {
    InputStream fis = null;
    ByteArrayOutputStream baos = null;
    try {
      baos = new ByteArrayOutputStream();
      
      fis = MarkdownUtils.class.getResourceAsStream(src);
      int len = 0;
      byte[] buffer = new byte[1024];
      while ((len = fis.read(buffer)) != -1) {
        baos.write(buffer, 0, len);
      }
      String text = baos.toString();
      text = text.replaceAll("(\r\n|\r|\n|\n\r)", "");
      return text;
    } finally {
      closeIO(fis);
      closeIO(baos);
    } 
  }

  
  private static String htmlConvert(String str) { return StringEscapeUtils.escapeHtml(str); }
}
