package com.test.java;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 凉尘
 * @date 2021/3/23
 **/
public class PatternDemo {
    static Pattern compile = Pattern.compile("</?.*?/?>");

    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("<br>");
        strings.add("</br>");
        strings.add("</b>");

        System.out.println(regexSubstring("<a>ddasad<br></br>aasd<aaaaa><br></br><cccccc><br><br><br></a>", strings,""));
        System.out.println(regexSubstring("<a>ddasad<br></br>aasd<aaaaa><br></br><cccccc><br><br><br></a>", strings,""));
        System.out.println(regexSubstring("<a>ddasad<br></br>aasd<aaaaa><br></br><cccccc><br><br><br></a>", strings,""));
    }

    public static String regexSubstring(String arg, List<String> exclude,String replace) {
        Matcher matcher = compile.matcher(arg);
        while (matcher.find()) {
            String group = matcher.group();
            if (!exclude.contains(group)) {
                arg = arg.replaceAll(group, replace);
            } else {
                arg = arg.replaceAll(group, "\n");
            }
        }
        return arg;
    }

}
