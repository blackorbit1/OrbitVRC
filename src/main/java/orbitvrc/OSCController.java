package orbitvrc;

import com.illposed.osc.MessageSelector;
import com.illposed.osc.OSCMessageListener;
import com.illposed.osc.messageselector.JavaRegexAddressMessageSelector;
import com.illposed.osc.transport.udp.OSCPortIn;
import com.illposed.osc.transport.udp.OSCPortOut;

import java.io.IOException;
import java.net.InetSocketAddress;

public class OSCController {

    public static final int VRC_OSC_PORT_IN = 8000;
    public static final int VRC_OSC_PORT_OUT = 8001;
    public static final String VRC_OSC_IP = "localhost";
    private static final MessageSelector MESSAGE_SELECTOR = new JavaRegexAddressMessageSelector(".*");


    public static OSCPortIn listener;
    public static OSCPortOut remote;

    public static boolean init() {
        try {
            remote = new OSCPortOut(new InetSocketAddress(VRC_OSC_IP, VRC_OSC_PORT_OUT));
            remote.connect();

            listener = new OSCPortIn(VRC_OSC_PORT_IN);
            listener.setDaemonListener(false);
            listener.startListening();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static void addListener(OSCMessageListener oscListener) {
        listener.getDispatcher().addListener(MESSAGE_SELECTOR, oscListener);
    }

}
