package it.italiandudes.cah.client.connection;

import it.italiandudes.cah.exceptions.connection.InvalidAddressException;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.RawSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class ConnectionManager {

    // Attributes
    private static User user;

    // Methods
    public static boolean setServerConnection(@NotNull final String serverAddress, @NotNull final String username) throws InvalidAddressException, IOException {
        if (user != null) return false;

        String[] splitAddress = serverAddress.split(":");
        if (splitAddress.length != 2) throw new InvalidAddressException("The address is not valid! Must respect the format <address>:<port>.");
        String address = splitAddress[0];
        int port;
        try {
            port = Integer.parseInt(splitAddress[1]);
            if (port < 0 || port > 65535) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new InvalidAddressException("The port must be an integer number between 0 and 65535, inclusive.");
        }

        try {
            Socket connection = new Socket();
            connection.connect(new InetSocketAddress(address, port), Defs.Protocol.SOCKET_TIMEOUT_MILLIS);
            if (connection.isConnected() && !connection.isClosed() && connection.getOutputStream() != null && connection.getInputStream() != null) {
                RawSerializer.sendString(connection.getOutputStream(), Defs.Protocol.Login.LOGIN);
                RawSerializer.sendString(connection.getOutputStream(), username);
                boolean accepted = RawSerializer.receiveBoolean(connection.getInputStream());
                if (!accepted) {
                    try {
                        connection.close();
                    } catch (Exception ignored) {
                    }
                    return false;
                }
                user = new User(connection, username);
            } else {
                try {
                    connection.close();
                } catch (Exception ignored) {
                }
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new InvalidAddressException("The port must be an integer number between 0 and 65535, inclusive.");
        }
        return true;
    }
    public static void closeConnection() {
        try {
            if (user != null) user.getConnection().close();
        } catch (Exception ignored) {}
        user = null;
    }
    public static User getUser() {
        return user;
    }
    public static boolean isClosed() {
        return user == null || user.getConnection().isClosed();
    }
}
