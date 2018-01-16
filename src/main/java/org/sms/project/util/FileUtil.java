package org.sms.project.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    public static String getText(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line + "\r\n");
            }
            String fromFile = sb.toString();
            return fromFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeText(String path, String data) {
    	FileOutputStream fos;
    	BufferedWriter rd;
		try {
			fos = new FileOutputStream(path);
			rd = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
	    	rd.write(data);
	    	rd.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
