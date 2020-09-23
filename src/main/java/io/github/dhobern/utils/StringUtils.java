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

    private static String toCsv(String ... values) {
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
            if (v.contains(",")) {
                sb.append("\""); 
                sb.append(v); 
                sb.append("\""); 
            } else {             
                sb.append(v);
            }
        }
        return sb.toString();
    }

}
