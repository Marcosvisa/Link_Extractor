

import java.io.File;

public abstract class Extractor {
    protected String outputPath;
    protected String url;
    protected boolean isAudioOnly;
    protected String quality;
    
    public Extractor(String url, String outputPath, boolean isAudioOnly, String quality) {
        this.url = url;
        this.outputPath = outputPath;
        this.isAudioOnly = isAudioOnly;
        this.quality = quality;
    }
    
    public abstract void download() throws Exception;
    
    protected String getOutputTemplate() {
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        if (isAudioOnly) {
            return outputPath + File.separator + "%(title)s.%(ext)s";
        } else {
            return outputPath + File.separator + "%(title)s [%(resolution)s].%(ext)s";
        }
    }
    //getters
    public String getQuality() {
        return quality;
    }
    
    public boolean isAudioOnly() {
        return isAudioOnly;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
}