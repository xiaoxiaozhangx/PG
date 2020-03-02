package com.zx.pg;
/* 创建时间：2020/3/1 11:57
*作者：Zhangxu
*修改时间：2020/3/1 11:57
*包名：com.zx.pg
*说明：
 */
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PhotoFetchr {
    private static final String TAG = "PhotoFetchr";
    private static  final String API_KEY="x-api-key";
    /*fan
    从指定url返回原始数据 并返回字节数组
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();//这里正真建立连接 get
            //如果是post请求，应该为 connection.getOutputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];//in -》inputstream不断输出字节流
            while ((bytesRead = in.read(buffer)) > 0) {//循环调用read读取数据直到读取完为止
                out.write(buffer, 0, bytesRead);
            }
            out.close();//关闭
            return out.toByteArray();
        } finally {
            connection.disconnect();//最终关闭连接
        }
    }
//转换成string
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public List<GalleryItem>  fetchItems() {
        List<GalleryItem> items = new ArrayList<>();
//https://cdn2.thecatapi.com/images/b5a.jpg
//        http://shibe.online/api/shibes?count=[1-100]&urls=[true/false]&httpsUrls=[true/false]
//  .appendQueryParameter("nojsoncallback", "1")
//                .appendQueryParameter("extras", "url_s")
//        .appendQueryParameter("size", "small")
//                .appendQueryParameter("mime_types", "true")
//                .appendQueryParameter("order", "ASC")
//                .appendQueryParameter("limit", "5")
//                .appendQueryParameter("page", "0")
//                .appendQueryParameter("category_ids", "true")

        try {


            String url = Uri.parse("https://api.thecatapi.com/v1/images/search")
                    .buildUpon()
                    .appendQueryParameter("methond", "GET")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("limit", "50")

                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
//            JSONObject jsonBody = new JSONObject(jsonString);
            JSONArray jsonArray=new JSONArray(jsonString);
            parseItems(items, jsonArray);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items,JSONArray  jsonArray)
            throws IOException, JSONException {
JSONObject jsonObject=jsonArray.getJSONObject(0);
//        JSONArray photosUrlJsonArray = jsonArray.getJSONArray(3);
//        JSONArray photoIdJsonArray = jsonArray.getJSONArray(2);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject photoJsonObject = jsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id")  );
//            item.setCaption(photoJsonObject.getString("breeds"));

//            if (!photoJsonObject.has("url_s")) {
//                continue;
//            }

            item.setUrl(photoJsonObject.getString("url"));
            items.add(item);
        }
    }

}
