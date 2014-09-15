package Spider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    public static void getRateSTR(String URL, int maxPage) throws IOException {
        //首先分析所给的URL，获取其中的商品ID
        String itemID = null;
        Matcher m = Pattern.compile("id=\\d{5,}").matcher(URL);
        if (m.find()) {
            itemID = m.group().replaceAll("id=", "");
        } else {
            System.out.println("所给的URL格式不正确，请重新检查！");
            System.exit(-1);
        }
        //首先读取整个商品页面，获取其中的sellerID
        String sellerID = null;
        Document itemDoc = Jsoup.connect(URL).get();
        String itemHTML = itemDoc.html();
        m = Pattern.compile("sellerId:\"\\d{1,}").matcher(itemHTML);
        if (m.find()) {
            sellerID = m.group().replaceAll("sellerId:\"", "");
        } else {
            System.out.println("sellerId读取错误！");
            System.exit(-1);
        }
        String itemName = itemDoc.select("title").text().replaceAll("-淘宝网", "");
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
            JSONArray rateList = getRateList(itemID, sellerID, pageNum);
            time = System.currentTimeMillis() - time;
            System.out.println("用时：" + time + "ms\t休眠" + time / 2 + "ms");
            try {
                Thread.sleep(time / 2);
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
                System.out.println("调用了一个未存在的界面");
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
        FileOutputStream fos = new FileOutputStream(new File(itemName + "-" + System.currentTimeMillis() + ".txt"));
        fos.write(root.toString().getBytes("utf-8"));
//        System.out.println(commentList.toString());
    }

    /**
     * 获取指定商品的指定页数的评价列表
     *
     * @param auctionNumId 商品id
     * @param userNumId    卖家id
     * @param pageNum      所指定的页数
     * @return 返回包含所有评价子项的JSONArray
     */
    protected static JSONArray getRateList(String auctionNumId, String userNumId, int pageNum) {
        //开始进行商品评价的读取
        String rateURL = "http://rate.taobao.com/feedRateList.htm";
        Map<String, String> data = new HashMap<String, String>();
        data.put("auctionNumId", auctionNumId);
        data.put("currentPageNum", pageNum + "");
        data.put("ShowContent", "1");//只显示有内容的评价
        data.put("order", "1");//排序规则，1为最近排序
        data.put("userNumId", userNumId);
        //读取原始html文本，并对超时进行异常处理
        Document doc = null;
        try {
            doc = Jsoup.connect(rateURL).data(data).timeout(10000).get();
        } catch (SocketTimeoutException ste1) {
            System.out.println("连接超时，3秒后进行第一次重连");
            try {
                Thread.sleep(3000);
                doc = Jsoup.connect(rateURL).data(data).timeout(10000).get();
            } catch (SocketTimeoutException ste2) {
                System.out.println("连接超时，3秒后进行第二次重连");
                try {
                    Thread.sleep(3000);
                    doc = Jsoup.connect(rateURL).data(data).timeout(10000).get();
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
            System.exit(-1);
        }
        JSONTokener jsonTokener = new JSONTokener(docSTR);
        JSONObject rootJSON = null;
        JSONArray commentList = null;
//        try {
        try {
            rootJSON = (JSONObject) jsonTokener.nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commentList = rootJSON.optJSONArray("comments");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return commentList;
    }

//    class NullPageException extends Exception {
//        public NullPageException() {
//        }
//
//        public NullPageException(String msg) {
//            super(msg);
//        }
//
//    }
}
