package orbitvrc;

import com.illposed.osc.MessageSelector;
import com.illposed.osc.OSCMessageListener;
import com.illposed.osc.messageselector.JavaRegexAddressMessageSelector;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OSCController {
    final static Logger log = Logger.getLogger(OSCController.class);

    public static final int VRC_OSC_PORT_IN = 8001;
    public static final int VRC_OSC_PORT_OUT = 8000;
    public static final String VRC_OSC_IP = "127.0.0.1";
    private static final MessageSelector MESSAGE_SELECTOR = new JavaRegexAddressMessageSelector(".*");


    public static OSCPortIn listener;
    public static OSCPortOut remote;

    public static boolean init() {
        try {
            remote = new OSCPortOut(new InetSocketAddress(VRC_OSC_IP, VRC_OSC_PORT_OUT));
            remote.connect();
            log.info("OSC sender connected to " + VRC_OSC_IP + ":" + VRC_OSC_PORT_OUT);

            listener = new OSCPortIn(VRC_OSC_PORT_IN);
            listener.setDaemonListener(false);
            listener.startListening();
            log.info("OSC listener started on port " + VRC_OSC_PORT_IN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static void addListener(OSCMessageListener oscListener) {
        log.debug("Adding OSC listener: " + oscListener);
        listener.getDispatcher().addListener(MESSAGE_SELECTOR, oscListener);
    }

}
