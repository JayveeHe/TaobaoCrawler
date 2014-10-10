TaobaoCrawler
=============

淘宝商品评价的爬虫

----------
####**使用方式**
依赖该工程或者依赖TaobaoCrawler.jar后，调用RateSpider的静态方法即可。
```Java
     /**
     * 根据指定商品页面地址爬取商品评价，在工程目录输出数据文件
     *
     * @param URL
     * @param maxPage 爬取的最大页数，如果为0则无上限
     * @throws IOException
     */
    public static JSONObject getRateByURL(String URL, int maxPage)
```

####**注意事项**
1. 返回的JSON格式
![](http://ww3.sinaimg.cn/large/64e1f62cjw1el6hb2414qj20hu06faa6.jpg)
2. 关于健壮性
还未考虑验证码、封IP、多线程等问题

--------
###**最后，有任何问题欢迎讨论**
