package uff.ic.swlab.kws.deprecated;

import java.util.regex.Pattern;

public class NewClass {

    public static void main(String[] args) {
        Pattern PATTERN = Pattern.compile(".*\\p{C}.*");
        char c = 0x8;
        String s = "DD" + c;

        System.out.println(PATTERN.matcher(s).matches());
        System.out.println(s);

    }
}
