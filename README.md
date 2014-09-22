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
![](https://d1zjcuqflbd5k.cloudfront.net/files/acc_294905/hJRm?response-content-disposition=inline;%20filename=QQ20140922103924.png&Expires=1411353807&Signature=WZ0iFOmJSrK4Iew3f30JPF0QszBhiIW6tyHn9uqae2K4SmK2rhz4VZSA~Vta3Cmq5pN5kVCVdQA7INgG7xYOOdlCm2wuTW7fQzJOzN55uXm2rEmACgRhLOsN8TzuddValmXi~GpyxmnqQ6w7naWm9-qWf-F05a~sMYVRP2Te5Q8_&Key-Pair-Id=APKAJTEIOJM3LSMN33SA)
2. 关于健壮性
还未考虑验证码、封IP、多线程等问题

--------
###**最后，有任何问题欢迎讨论**
