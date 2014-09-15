package Spider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ITTC-Jayvee on 2014/9/15.
 */
public class RateSpider {
    public static void getRateSTR(String URL) throws IOException {
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
        String itemHTML = Jsoup.connect(URL).get().html();
        m = Pattern.compile("sellerId:\"\\d{1,}").matcher(itemHTML);
        if (m.find()) {
            sellerID = m.group().replaceAll("sellerId:\"", "");
        } else {
            System.out.println("sellerId读取错误！");
            System.exit(-1);
        }
        boolean hasNextPage = true;
        JSONArray commentList= new JSONArray();
        do {
            Long time = System.currentTimeMillis();
            JSONArray rateList = getRateList(itemID, sellerID, 11);
            System.out.println("用时：" + (System.currentTimeMillis() - time) + "ms");
            if (null != rateList) {
                //进行JSONArray的合并
            } else {
                System.out.println("调用了一个未存在的界面");
                hasNextPage = false;
            }

        } while (hasNextPage);

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
//        data.put("callback", "test");//回调函数的名称，任意指定
        data.put("auctionNumId", auctionNumId);
        data.put("currentPageNum", pageNum + "");
        data.put("ShowContent", "1");//只显示有内容的评价
        data.put("order", "1");//排序规则，1为最近排序
        data.put("userNumId", userNumId);
//        data.put()
        Document doc = null;
        try {
            doc = Jsoup.connect(rateURL).data(data).timeout(5000).get();
            System.out.println(doc.text());
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

    class NullPageException extends Exception {
        public NullPageException() {
        }

        public NullPageException(String msg) {
            super(msg);
        }

    }
}
