package se.xfunserver.xfunbackend.services.clients;

import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.mc.auth.service.MojangAuthenticationService;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.ProxyInfo;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;

import java.net.Proxy;

public class ProtocolClient {

    // https://github.com/Steveice10/MCProtocolLib

    private final static int clients = 1;
    private final static int limitPerJoin = 1;
    private final static int threadLimit = 1000;

    private static final String HOST = "51.91.29.131";
    private static final String register = "";
    private static final int PORT = 25565;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static final ProxyInfo PROXY = null;

    private static final String USERNAME = null;
    private static final String PASSWORD = null;

    private static void login(String username) {
        MinecraftProtocol protocol = null;
        if (PASSWORD != null) {
            AuthenticationService authService = new MojangAuthenticationService();
            authService.setUsername(USERNAME);
            authService.setPassword(PASSWORD);
            authService.setProxy(AUTH_PROXY);

            protocol = new MinecraftProtocol(String.valueOf(authService));
            System.out.println("Successfully authenticated user.");
        } else {
            username = username.replaceAll("-", "_");
            protocol = new MinecraftProtocol(username);
        }

        SessionService sessionService = new SessionService();
        sessionService.setProxy(AUTH_PROXY);

        Session client = new TcpClientSession(HOST, PORT, protocol, PROXY);
        client.addListener(new SessionAdapter() {
            @Override
            public void disconnected(DisconnectedEvent event) {
                System.out.println("Disconnected: " + event.getReason());
                if (event.getCause() != null) {
                    event.getCause().printStackTrace();
                }
            }
        });

        client.connect();
    }
}
