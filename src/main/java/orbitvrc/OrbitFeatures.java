package orbitvrc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCMessageListener;
import com.illposed.osc.OSCSerializeException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class OrbitFeatures {
    final static Logger log = Logger.getLogger(OrbitFeatures.class);

    // Note that VRC only send the right value at the moment the user change gogoLoco position, otherwise it sends 0
    public static Boolean lastGogoLocoActivation = false;
    public static Integer lastGogoLocoPos = -1;
    public static Float lastGogoLocoHeight = -1f;
    public static Boolean lastGogoLocoMirror = false;
    public static Float lastGogoLocoPosePlaySpace = -1f;
    public static Boolean lastGogoLocoPoseStationary = false;

    public static String avParamAddress = "/avatar/parameters";

    // send the OSC message to the remote with and without the gogoLoco prefix
    private static void sendOSCMessage(String param, List<Object> arguments) throws IOException, OSCSerializeException {
        OSCController.remote.send(new OSCMessage(avParamAddress + param, arguments));
        OSCController.remote.send(new OSCMessage(avParamAddress + "/Go" + param, arguments));
        if(param.equals("/VRCEmote")) {
            OSCController.remote.send(new OSCMessage(avParamAddress + "/SittingAnim", Collections.singletonList(lastGogoLocoPos)));
        }

        // log the used adress
        log.debug("address 1: " + avParamAddress + param);
        log.debug("address 2: " + avParamAddress + "/Go" + param);
        log.debug("address 3: " + avParamAddress + "/SittingAnim");
    }

    public static OSCMessageListener keepGogoLocoPosOnAviChange = (event) -> {
        // OSC address for gogoLoco position:
        String activation = "/Pose";
        String position = "/VRCEmote";
        String positionAlt = "/SittingAnim";
        String height = "/Float";
        String mirror = "/Mirror";
        String posePlaySpace = "/PosePlaySpace";
        String poseStationary = "/Stationary";

        // gogoLoco prefix:
        String go = "/Go";

        // OSC address on avi change:
        String aviChangeAddress = "/avatar/change";

        try {
            // Get the OSC message
            OSCMessage message = event.getMessage();
            String address = message.getAddress();
            List<Object> arguments = message.getArguments();


            if((address.contains(avParamAddress + position) || address.contains(positionAlt)) && arguments.size() > 0 && ((int) arguments.get(0) != 0)) {
                log.info("New gogoLoco position: " + arguments.get(0));
                lastGogoLocoPos = (int) arguments.get(0);
            } else if((address.contains(avParamAddress + activation) || address.contains(avParamAddress + go + activation)) && arguments.size() > 0) {
                log.info("New gogoLoco activation: " + arguments.get(0));
                lastGogoLocoActivation = (boolean) arguments.get(0);
            } else if((address.contains(avParamAddress + height) || address.contains(avParamAddress + go + height)) && arguments.size() > 0) {
                log.info("New gogoLoco height: " + arguments.get(0));
                lastGogoLocoHeight = (float) arguments.get(0);
            } else if((address.contains(avParamAddress + mirror) || address.contains(avParamAddress + go + mirror)) && arguments.size() > 0) {
                log.info("New gogoLoco mirror: " + arguments.get(0));
                lastGogoLocoMirror = (boolean) arguments.get(0);
            } else if((address.contains(avParamAddress + posePlaySpace) || address.contains(avParamAddress + go + posePlaySpace)) && arguments.size() > 0) {
                log.info("New gogoLoco pose play space: " + arguments.get(0));
                lastGogoLocoPosePlaySpace = (float) arguments.get(0);
            } else if((address.contains(avParamAddress + poseStationary) || address.contains(avParamAddress + go + poseStationary)) && arguments.size() > 0) {
                log.info("New gogoLoco pose stationary: " + arguments.get(0));
                lastGogoLocoPoseStationary = (boolean) arguments.get(0);
            } else if (address.contains(aviChangeAddress) && lastGogoLocoPos != -1) {
                log.info("New avatar" + arguments + ", sending last gogoLoco position: " + lastGogoLocoPos);
                new Thread(() -> {
                    try {
                        Thread.sleep(200);
                        sendOSCMessage(activation, Collections.singletonList(lastGogoLocoActivation));
                        sendOSCMessage(position, Collections.singletonList(lastGogoLocoPos));
                        sendOSCMessage(height, Collections.singletonList(lastGogoLocoHeight));
                        sendOSCMessage(mirror, Collections.singletonList(lastGogoLocoMirror));
                        sendOSCMessage(posePlaySpace, Collections.singletonList(lastGogoLocoPosePlaySpace));
                        sendOSCMessage(poseStationary, Collections.singletonList(lastGogoLocoPoseStationary));
                    } catch (Exception e) {
                        log.error("Error sending OSC message: " + e.getMessage());
                    }
                }).start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

}
