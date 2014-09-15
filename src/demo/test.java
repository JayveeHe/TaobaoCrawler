package demo;

import Spider.RateSpider;

import java.io.IOException;

/**
 * Created by ITTC-Jayvee on 2014/9/15.
 */
public class test {
    public static void main(String[] args) throws IOException {
        RateSpider.getRateByURL("http://detail.tmall.com/item.htm?spm=a230r.1.14.2.FCUKge&id=40347673269&ad_id=&am_id=&cm_id=140105335569ed55e27b&pm_id=&sku_properties=", 0);
    }
}
