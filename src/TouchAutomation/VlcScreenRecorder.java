package TouchAutomation;

import java.io.File;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

//http://www.experimentalqa.com/2017/11/record-selenium-test-video-in-mp4.html
public class VlcScreenRecorder {
	private final Logger logger =  LoggerFactory.getLogger(VlcScreenRecorder.class);
    private static final String[] OPTIONS = {
            "--quiet",
            "--quiet-synchro",
            "--intf",
            "dummy",
            "--video-filter",
            "rotate",
            "rotate-angle",
            "354"
    };

    private static final String MRL     = "screen://";
    private static final String SOUT    = ":sout=#transcode{vcodec=h264,vb=%d,scale=%f}:duplicate{dst=file{dst=%s}}";
    private static final String FPS     = ":screen-fps=%d";
    private static final String CACHING = ":screen-caching=%d";

    private static final int    fps     = 40;
    private static final int    caching = 1000;
    private static final int    bits    = 2048;
    private static final float  scale   = 1.0f;

    private final MediaPlayerFactory mediaPlayerFactory;
    private final MediaPlayer mediaPlayer;
    
    private String videoSubdirectory ="";

    public VlcScreenRecorder() {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir")+"\\lib");
        System.out.println(System.getProperty("user.dir")+"\\lib\\plugins");
        System.setProperty("VLC_PLUGIN_PATH",  System.getProperty("user.dir")+"\\lib\\plugins");
        mediaPlayerFactory = new MediaPlayerFactory(OPTIONS);
        mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
    }
    
    public void setVideoSubdirectory(String subdirectory) {
    	videoSubdirectory = subdirectory;
    }

    public void startRecording(String testName) {
        String mp4FileName = getFile(testName);
        logger.info("start recording, "+ mp4FileName);
        mediaPlayer.playMedia(MRL, getMediaOptions(mp4FileName));
    }

    public void stopRecording() {
        logger.info("stop recording ");
        mediaPlayer.stop();
    }
    
    public void releaseRecordingResources() {
        mediaPlayer.release();
        mediaPlayerFactory.release();
    }

    private String getFile(String testName) {
        File dir = new File(System.getProperty("user.dir"),videoSubdirectory);
        dir.mkdirs();
//        DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
//        return dir.getAbsolutePath() + "/" + testName + "-" + df.format(new Date()) + ".mp4";
        String filename = dir.getAbsolutePath() + "\\" + testName + ".mp4";
        System.out.println(filename);
        return filename;
    }
    
    private String[] getMediaOptions(String destination) {
        return new String[] {
                String.format(SOUT, bits, scale, destination),
                String.format(FPS, fps),
                String.format(CACHING, caching)
        };
    }
}
