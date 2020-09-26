/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.utils;

import java.io.PrintWriter;

/**
 *
 * @author stang
 */
public class StringUtils {

    public static String buildCSV(String ... values) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String v: values) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            if (v == null || v.equalsIgnoreCase("NULL")) {
                v = "";
            }
            if (v.contains(",") || v. contains("\"")) {
                v.replaceAll("\"", "\\\"");
                sb.append("\""); 
                sb.append(v); 
                sb.append("\""); 
            } else {             
                sb.append(v);
            }
        }
        return sb.toString();
    }

    public static String safeString(Integer i) {
        return (i == null) ? "" : i.toString();
    }

    public static String linkURLs(String s) {
        String enabled = "";
        
        if (s != null) {
            int start = 0;
            int end = -1;
            int current = 0;
            start = s.indexOf("http://"); 
            if (start < 0) {
                start = s.indexOf("https://");
            }
            while (start >= 0) {
                end = s.indexOf(" ", start);
                if (end < 0) {
                    end = s.length();
                }
                String url = s.substring(start, end);
                String possibleComma = "";
                if (url.endsWith(",")) {
                    url = url.substring(0, url.length() - 1);
                    possibleComma = ",";
                }
                String prior = "";
                if (start > current) {
                    prior = s.substring(current, start);
                }
                enabled += prior + "<a href=\"" + url +"\" target=\"_blank\">" + url + "</a>" + possibleComma;
                current = end;
                int newStart = s.indexOf("http://", start + 1);
                if (newStart < 0) {
                    newStart = s.indexOf("https://", start + 1);
                }
                start = newStart;
            }
            if (current < s.length()) {
                enabled += s.substring(current);
            }
        }
        
        return enabled;
    }

    public static String wrapStrong(String s) {
        return "<strong>" + s + "</strong>";
    }

    public static String wrapEmphasis(String s) {
        return "<em>" + s + "</em>";
    }

    
    public static String upperFirst(String s) {
        if (s != null && s.length() > 1) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        return s;
    }

    public static String safeTrim(String s) {
        if (s != null) {
            s = s.trim();
            if (s.length() == 0) {
                s = null;
            } 
        }
        return s;
    }

    public static String wrapDiv(String divClass, String s) {
        s = "<div class=\"" + divClass + "\">" + s + "</div>";
        return s;
    }
}
