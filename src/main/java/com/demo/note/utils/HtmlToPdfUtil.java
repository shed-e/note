package com.demo.note.utils;

import com.itextpdf.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HtmlToPdfUtil {
    //先把获取到的网页内容重新生成一个html文件
    public static File createFile(String fileContent) throws Exception {
        File htmlFile = new File("C:/Users/31359/IdeaProjects/note/src/main/resources/static/document/temp.html");
        File pdfFile = new File("C:/Users/31359/IdeaProjects/note/src/main/resources/static/document/temp.pdf");
        FileOutputStream fos = null;
        String head = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
                + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
                + "<meta charset=\"UTF-8\" />\r\n"
                + "<meta name=\"viewport\" content=\"initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width\"/>"
                + "<style>\r\n" + "table {border-collapse: collapse;table-layout: fixed;word-break:break-all;}\r\n"
                + "th,td {word-break:break-all;word-wrap : break-word;}\r\n"
                + "pre {font-family: SimSun;white-space:pre-wrap;}\r\n"
                + "@page {size:210mm 297mm;margin: 0.25in;-fs-flow-bottom: \"footer\";-fs-flow-left: \"left\";-fs-flow-right: \"right\";padding: 1em;}\r\n"
                + "</style>\r\n" + "</head>\r\n" + "<body style = \"font-family: SimSun;\">\r\n"
                + "<div>";
        String foot = "\r\n</div>\r\n</body>\r\n</html>";

        if (!htmlFile.exists()) {
            htmlFile.createNewFile();
        }
        if (!pdfFile.exists()) {
            pdfFile.createNewFile();
        }
        fos = new FileOutputStream(htmlFile);
        String content = head + fileContent + foot;
        fos.write(content.getBytes("utf-8"));

        fos.flush();
        fos.close();

        html2pdf(htmlFile, pdfFile);

        return pdfFile;
    }

    //将html文件转为pdf
    public static void html2pdf(File htmlFile, File pdfFile) throws Exception {
        String url;
        OutputStream os;
        url = htmlFile.toURI().toURL().toString();
        os = new FileOutputStream(pdfFile);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        ITextFontResolver fontResolver = renderer.getFontResolver();

        fontResolver.addFont("C:/Windows/Fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.layout();
        renderer.createPDF(os);
        os.close();
        htmlFile.delete();
    }
}