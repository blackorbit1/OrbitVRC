package orbitvrc;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCMessageListener;

import java.util.Collections;
import java.util.List;

public class OrbitFeatures {

    // Note that VRC only send the right value at the moment the user change gogoLoco position, otherwise it sends 0
    public static int lastGogoLocoPos = -1;
    public static OSCMessageListener keepGogoLocoPosOnAviChange = (event) -> {
        // OSC address for gogoLoco position:
        String gogoLocoPosAddress = "/avatar/parameters/VRCEmote";
        // OSC address on avi change : /avatar/changeAvatar/
        try {
            // Get the OSC message
            OSCMessage message = event.getMessage();
            String address = message.getAddress();
            List<Object> arguments = message.getArguments();

            if(address.equals(gogoLocoPosAddress) && arguments.size() > 0 && ((int) arguments.get(0) != 0)) {
                lastGogoLocoPos = (int) arguments.get(0);
            } else if (address.equals("/avatar/changeAvatar/") && lastGogoLocoPos != -1) {
                // Send the last gogoLoco position
                OSCController.remote.send(new OSCMessage(gogoLocoPosAddress, Collections.singletonList(lastGogoLocoPos)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}
