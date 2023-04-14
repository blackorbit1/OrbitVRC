package orbitvrc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class OrbitVRC {
    final static Logger log = Logger.getLogger(OrbitVRC.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();
        log.info("Running OrbitVRC \\o/");

        if(OSCController.init()){
            OSCController.addListener(OrbitFeatures.keepGogoLocoPosOnAviChange);
        }
    }
}