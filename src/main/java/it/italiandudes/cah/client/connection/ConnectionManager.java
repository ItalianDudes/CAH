package it.italiandudes.cah.client.connection;

import it.italiandudes.cah.exceptions.connection.InvalidAddressException;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.RawSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Socket;

public final class ConnectionManager {

    // Attributes
    private static User user;

    // Methods
    public static boolean setServerConnection(@NotNull final String address, final int mainPort, final int servicePort, @NotNull final String username) throws InvalidAddressException, IOException {
        if (user != null) return false;

        if (mainPort < 0 || mainPort > 65535) throw new InvalidAddressException("The main port must be an integer number between 0 and 65535, inclusive.");
        if (servicePort < 0 || servicePort > 65535) throw new InvalidAddressException("The service port must be an integer number between 0 and 65535, inclusive.");

        try {
            Socket mainConnection = new Socket();
            mainConnection.connect(new InetSocketAddress(address, mainPort), Defs.Protocol.SOCKET_TIMEOUT_MILLIS);
            if (mainConnection.isConnected() && !mainConnection.isClosed() && mainConnection.getOutputStream() != null && mainConnection.getInputStream() != null) {
                RawSerializer.sendString(mainConnection.getOutputStream(), Defs.Protocol.Login.MAIN_LOGIN);
                RawSerializer.sendString(mainConnection.getOutputStream(), username);
                int mainAccepted = RawSerializer.receiveInt(mainConnection.getInputStream());
                if (mainAccepted == 0) {
                    try {
                        mainConnection.close();
                    } catch (Exception ignored) {}
                    return false;
                } else if (mainAccepted < 0) {
                    try {
                        mainConnection.close();
                    } catch (Exception ignored) {}
                    throw new ProtocolException("Protocol not respected");
                }
                Socket serviceConnection = new Socket();
                serviceConnection.connect(new InetSocketAddress(address, servicePort), Defs.Protocol.SOCKET_TIMEOUT_MILLIS);
                if (serviceConnection.isConnected() && !serviceConnection.isClosed() && serviceConnection.getOutputStream() != null && serviceConnection.getInputStream() != null) {
                    RawSerializer.sendString(serviceConnection.getOutputStream(), Defs.Protocol.Login.SERVICE_LOGIN);
                    RawSerializer.sendString(serviceConnection.getOutputStream(), username);
                    int serviceAccepted = RawSerializer.receiveInt(serviceConnection.getInputStream());
                    if (serviceAccepted == 0) {
                        try {
                            mainConnection.close();
                        } catch (Exception ignored) {}
                        try {
                            serviceConnection.close();
                        } catch (Exception ignored) {}
                        return false;
                    } else if (serviceAccepted < 0) {
                        try {
                            mainConnection.close();
                        } catch (Exception ignored) {}
                        try {
                            serviceConnection.close();
                        } catch (Exception ignored) {}
                        throw new ProtocolException("Protocol not respected");
                    }
                    user = new User(mainConnection, serviceConnection, username);
                    return true;
                } else {
                    try {
                        mainConnection.close();
                    } catch (Exception ignored) {}
                    try {
                        serviceConnection.close();
                    } catch (Exception ignored) {}
                }
            } else {
                try {
                    mainConnection.close();
                } catch (Exception ignored) {}
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new InvalidAddressException("The port must be an integer number between 0 and 65535, inclusive.");
        }
        return false;
    }
    public static void closeConnection() {
        try {
            if (user != null) user.getMainConnection().close();
        } catch (Exception ignored) {}
        user = null;
    }
    public static User getUser() {
        return user;
    }
    public static boolean isClosed() {
        return user == null || user.getMainConnection().isClosed() || user.getServiceConnection().isClosed();
    }
}
