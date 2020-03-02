package com.zx.pg;

public class GalleryItem {
//    Received JSON: [{"breeds":[],"id":"MjAzNjA0Nw",
//            "url":"https://cdn2.thecatapi.com/images/MjAzNjA0Nw.jpg","width":450,"height":600}]

//   [
//    {
//        "breeds": [],
//        "height": 310,
//            "id": "b5a",
//            "url": "https://cdn2.thecatapi.com/images/b5a.jpg",
//            "width": 500
//    }

//]

//    [{"breeds":[],"categories":[{"id":1,"name":"hats"}],"id":"37g","url":"https:\/\/cdn2.thecatapi.com\/images\/37g.jpg","width":605,"height":720}]
    private String mCaption;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    private String mId;
    private String mUrl;
    @Override
    public String toString() {
        return mId;
    }

}
