package org.sms;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.sms.project.helper.mail.McMailService;

public class TestHtml {

    public static String getHtmlTemplate() {
        final String file = "/Users/sunny/work/face/soaer/src/main/resources/config/template/blog.html";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
            }
            String fromFile = sb.toString();
            return fromFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMdHtml() {
        final String file = "/Users/sunny/work/github/SSO-OpenSAML/client/src/main/resources/config/template/md.html";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line + "\r\n");
            }
            String fromFile = sb.toString();
            return fromFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String insertScript(Map<String, ?> map) {
        String scriptStart = "<script type=\"text/javascript\">";
        String scriptEnd = "</script>";
        StringBuffer buffer = new StringBuffer();
        buffer.append(scriptStart);
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            buffer.append("var " + entry.getKey() + "=\"" + entry.getValue() + "\" ");
        }
        buffer.append(scriptEnd);
        return buffer.toString();
    }

    public static void add(String[] str) {
        str[0] = "ll";
    }

    public static void main(String[] args) {
        McMailService mcMailService = new McMailService();
        mcMailService.setMailFrom("soaer_service@163.com");
        mcMailService.setMailSmtpHost("smtp.163.com");
        mcMailService.setSendMailPassword("liuzhenxing1");
        mcMailService.setSendMailUser("soaer_service");
        mcMailService.send("liuzhenx@hotmail.com", "验证码", "asdfasdfasdf", null, null);
        // String a = getHtmlTemplate();
        // Document doc = Jsoup.parse(a);
        // Element c = doc.getElementsByAttributeValue("id", "page-title").first();
        // Elements ddd = c.getElementsByTag("h1");
        // ddd.append("ddddddddd");
        //
        // System.out.println(ddd.toString());
        // Date date = new Date();
        // Calendar cal = Calendar.getInstance();
        // cal.setTime(new Date());
        // System.out.println(cal.get(Calendar.YEAR));
        // System.out.println(cal.get(Calendar.MONTH));
        // System.out.println(cal.get(Calendar.DAY_OF_MONTH));
        // doc.title("网页标题");
        // Element element = doc.head();
        //
        // Map<String, String> scriptMap = new HashMap<>();
        // scriptMap.put("username", "sunny");
        // scriptMap.put("blogId", "24234223423423");
        // element.append(insertScript(scriptMap));
        // System.out.println(element.toString());
        // System.out.println(doc.title());
        // System.out.println(doc.toString());
    }

    // public static void main(String[] args) {
    // String a = getHtmlTemplate();
    // String mdHtmlStr = getMdHtml();
    //
    // Document doc = Jsoup.parse(a);
    // Document mdDoc = Jsoup.parse(mdHtmlStr);
    //// System.out.println(mdDoc.toString());
    // Elements l = mdDoc.getElementsByTag("body");
    // String m = l.html().toString();
    // Element singerListDiv = doc.getElementsByAttributeValue("class",
    // "noteAd").first();
    // singerListDiv.after(m);
    // System.out.println(doc.toString());
    // }
}
