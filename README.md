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
| Key        | 类型          | 备注  |
| ------------- |:-------------:| -----:|
| rateList      | JSONArray | 详细的评价列表|
| itemName      | String|   商品名称 |
| itemID | String     |    商品ID |
| sellerID | String     |    卖家ID |
| taskTime | String     |    符合Date.toString格式的任务开始时间 |
2. 关于健壮性
还未考虑验证码、封IP、多线程等问题

--------
###**最后，有任何问题欢迎讨论**