package objects;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Rachamim on 3/13/17.
 */
public class RootPathObject {
    public String getRootPath(){
        try {
            return (new File(RootPathObject.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
