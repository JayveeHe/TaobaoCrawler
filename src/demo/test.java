package demo;

import Spider.RateSpider;

import java.io.IOException;

/**
 * Created by ITTC-Jayvee on 2014/9/15.
 */
public class test {
    public static void main(String[] args) throws IOException {
        RateSpider.getRateSTR("http://item.taobao.com/item.htm?id=19945802022");
    }
}
