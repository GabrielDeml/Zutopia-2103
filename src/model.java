import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class model {
    private String pathToSounds = "D:\\OneDrive - Worcester Polytechnic Institute (wpi.edu)\\B term F\\CS 2103\\project4\\project4-intelj\\res\\";
    private List<String> listOfSounds = new ArrayList<>(Arrays.asList("bleat", "boing", "chachiing", "quack", "Shatter", "whinny"));
    private HashMap<String, AudioClip> stringToAudioClip = new HashMap<>();

    public Path p1 = Paths.get("res/bleat.wav");
//    public String p2 = URI.create("res/bleat.wav");
//    final public AudioClip sound = new AudioClip(getClass().getClassLoader()
//            .getResource("res/bleat.wav").toString());
//    final public AudioClip sound =  new AudioClip(this.getClass().getResource("D:\\OneDrive - Worcester Polytechnic Institute (wpi.edu)\\B term F\\CS 2103\\project4\\project4-intelj\\res\\bleat.wav").toExternalForm());

    public void loadAudio() {
        System.out.println(System.getProperty("user.dir"));
        for (String n : listOfSounds) {
            // D:\OneDrive - Worcester Polytechnic Institute (wpi.edu)\B term F\CS 2103\project4\project4-intelj\res\bleat.wav
            System.out.println(System.getProperty("user.dir") + pathToSounds + n + ".wav");
            stringToAudioClip.put(n, new AudioClip(getClass().getClassLoader().getResource(pathToSounds + n + ".wav").toString()));

        }
    }

    public void playAudio(String n) {
        stringToAudioClip.get(n).play();
    }
}
