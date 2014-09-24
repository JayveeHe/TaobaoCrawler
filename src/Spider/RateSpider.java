package Spider;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ITTC-Jayvee on 2014/9/15.
 */
public class RateSpider {
    /**
     * 根据指定商品页面地址爬取商品评价，在工程目录输出数据文件
     *
     * @param URL
     * @param maxPage 爬取的最大页数，如果为0则无上限
     * @throws IOException
     */
    public static JSONObject getRateByURL(String URL, int maxPage) throws IOException {
        //首先分析所给的URL，获取其中的商品ID
        String itemID = null;
        Matcher m = Pattern.compile("id=\\d{5,}").matcher(URL);
        if (m.find()) {
            itemID = m.group().replaceAll("id=", "");
        } else {
            System.out.println("所给的URL格式不正确，请重新检查！");
//            System.exit(-1);
            //TODO 注意退出条件
            return null;
        }
        //首先读取整个商品页面，获取其中的sellerID
        String sellerID = null;
        System.out.println("正在读取商品页面，获取相关信息……");
        Document itemDoc = Jsoup.connect(URL).timeout(5000).get();
        String itemHTML = itemDoc.html();
        m = Pattern.compile("sellerId:\"\\d{1,}").matcher(itemHTML);
        if (m.find()) {
            sellerID = m.group().replaceAll("sellerId:\"", "");
        } else {
            System.out.println("sellerId读取错误！");
//            System.exit(-1);
            //TODO 注意退出条件
            return null;
        }
        String itemName = itemDoc.select("title").text().replaceAll("(-淘宝网)|(\\s)|-tmall.com天猫|/|、", "");
        System.out.println(itemName);

        //进行正式的评价内容爬取
//        记录下进行任务时的时间
        Date date = new Date(System.currentTimeMillis());
        boolean hasNextPage = true;
        int pageNum = 1;//从第一页开始进行爬取
        JSONObject root = new JSONObject();
        try {//任务基本信息的填写
            root.put("taskTime", date.toString());
            root.put("itemID", itemID);
            root.put("sellerID", sellerID);
            root.put("itemName", itemName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray commentList = new JSONArray();
        do {
            Long time = System.currentTimeMillis();
            System.out.println("正在读取第" + pageNum + "页评价");
            JSONArray rateList = getRateList(itemID, sellerID, pageNum);
            time = System.currentTimeMillis() - time;
            System.out.println("用时：" + time + "ms\t休眠" + time / 2 + "ms");
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (null != rateList) {
                //进行JSONArray的合并
                for (int i = 0; i < rateList.length(); i++) {
                    try {
                        commentList.put(rateList.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("pageNum=" + pageNum + "\t调用了一个未存在的界面");
                hasNextPage = false;
            }
            if (maxPage != 0) {
                if (pageNum < maxPage) {
                    pageNum++;
                } else {
                    break;
                }
            } else {
                pageNum++;
            }
        } while (hasNextPage);
        try {
            root.put("rateList", commentList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        FileOutputStream fos = new FileOutputStream(new File(itemName + "-" + System.currentTimeMillis() + ".txt"));
//        fos.write(root.toString().getBytes("utf-8"));
//        System.out.println(commentList.toString());
        return root;
    }

    /**
     * 获取指定商品的指定页数的评价列表
     *
     * @param auctionNumId 商品id
     * @param userNumId    卖家id
     * @param pageNum      所指定的页数
     * @return 返回包含所有评价子项的JSONArray
     */
    public static JSONArray getRateList(String auctionNumId, String userNumId, int pageNum) {
        //开始进行商品评价的读取
        String rateURL = "http://rate.taobao.com/feedRateList.htm";
        Map<String, String> data = new HashMap<String, String>();
        data.put("auctionNumId", auctionNumId);
        data.put("currentPageNum", pageNum + "");
        data.put("ShowContent", "1");//只显示有内容的评价
//        data.put("order", "1");//排序规则，1为最近排序
        data.put("userNumId", userNumId);
        //读取原始html文本，并对超时进行异常处理
        Document doc = null;
        try {
            doc = Jsoup.connect(rateURL).header("User-Agent",
                    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                    .data(data).timeout(5000).get();
        } catch (SocketTimeoutException ste1) {
            System.out.println("连接超时，3秒后进行第一次重连");
            try {
                Thread.sleep(3000);
                doc = Jsoup.connect(rateURL).header("User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                        .data(data).timeout(5000).get();
            } catch (SocketTimeoutException ste2) {
                System.out.println("连接超时，3秒后进行第二次重连");
                try {
                    Thread.sleep(3000);
                    doc = Jsoup.connect(rateURL).header("User-Agent",
                            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                            .data(data).timeout(5000).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matcher m = Pattern.compile("\\{.*\\}").matcher(doc.text());
        String docSTR = null;
        if (m.find()) {
            docSTR = m.group();
        } else {
            System.out.println("匹配json文本失败！");
//            System.exit(-1);
            //TODO 注意退出条件
            return null;
        }

        JSONTokener jsonTokener = new JSONTokener(docSTR);
        JSONObject rootJSON = null;
        JSONArray commentList;
//        try {
        try {
            rootJSON = (JSONObject) jsonTokener.nextValue();
        } catch (JSONException e) {
            System.out.println("读取第" + pageNum + "页评价时发生JSON格式错误！");
            System.out.println(docSTR);
//            e.printStackTrace();
            //一般是因为content项中存在多余的“”
            //处理json文本中的多余引号
            Matcher content_matcher = Pattern.compile("(\"content\":.*?,\")").matcher(docSTR);
            StringBuffer strBuffer = new StringBuffer();
            while (content_matcher.find()) {
                String content = content_matcher.group();
                Matcher m1 = Pattern.compile("\"").matcher(content);
                int count = 0;
                while (m1.find()) {
                    count++;
                }
                if (count > 5) {
                    System.out.println(content);
                    StringBuffer sb = new StringBuffer();
                    Matcher q = Pattern.compile("\"").matcher(content);
                    for (int i = 0; i < count; i++) {
                        if (q.find() && i > 2 && i < (count - 2)) {
                            q.appendReplacement(sb, "'");
//                            System.out.println("替换后的字符串:" + sb);
                        }
                    }
                    q.appendTail(sb);
//                    System.out.println("最终字符串：" + sb);
                    content_matcher.appendReplacement(strBuffer, sb.toString());

                }
            }
            content_matcher.appendTail(strBuffer);
            docSTR = strBuffer.toString();
            jsonTokener = new JSONTokener(docSTR);
            try {
                rootJSON = (JSONObject) jsonTokener.nextValue();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        commentList = rootJSON.optJSONArray("comments");
        if (null == commentList) {
            System.out.println(doc.text());
            System.out.println(rateURL + "?auctionNumId=" + auctionNumId + "&userNumId=" + userNumId + "&currentPageNum=" + pageNum);
        }
        return commentList;
    }

}
