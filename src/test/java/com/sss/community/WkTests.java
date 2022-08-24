package com.sss.community;

import java.io.IOException;


public class WkTests {
    public static void main(String[] args) throws IOException {
        String cmd =
                "D:/SoftWare/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://baidu.com D:/SoftWare/wkhtmltopdf/wk/img/1.png";
        Runtime.getRuntime().exec(cmd);
        System.out.println("ok");
    }
}
