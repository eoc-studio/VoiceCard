package eoc.studio.voicecard.recommend;


public class PicasaEntry {
    private String mTitle;
    private String mThumbnailUrl;


    public PicasaEntry(String title, String thumbnailUrl) {
        super();
        mTitle = title;
        mThumbnailUrl = thumbnailUrl;
    }


    public String getTitle() {
        return mTitle;
    }


    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }
}
