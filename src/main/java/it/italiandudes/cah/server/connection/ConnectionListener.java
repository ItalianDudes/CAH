package it.italiandudes.cah.server.connection;

import it.italiandudes.cah.client.connection.User;
import it.italiandudes.cah.utils.Defs;
import it.italiandudes.idl.common.Logger;
import it.italiandudes.idl.common.RawSerializer;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;

public final class ConnectionListener extends Thread {

    // Attributes
    private boolean interrupted = false;
    private final ServerSocket mainServerSocket;
    private final ServerSocket serviceServerSocket;

    // Constructors
    public ConnectionListener(final int mainPort, final int servicePort) throws IOException {
        mainServerSocket = new ServerSocket(mainPort);
        try {
            serviceServerSocket = new ServerSocket(servicePort);
        } catch (IOException e) {
            try {
                mainServerSocket.close();
            } catch (Exception ignored) {}
            throw e;
        }
        this.setDaemon(true);
        this.setName("ConnectionListener");
    }

    // Methods
    public void stopListeners() {
        interrupted = true;
        try {
            mainServerSocket.close();
        } catch (Exception ignored) {}
        try {
            serviceServerSocket.close();
        } catch (Exception ignored) {}
    }

    // Runnable
    @Override
    public void run() {
        Socket mainConnection;
        Socket serviceConnection;
        String mainUsername;
        String serviceUsername;
        while (!interrupted) {
            mainConnection = null;
            serviceConnection = null;
            try {
                mainConnection = mainServerSocket.accept();
                if (RawSerializer.receiveString(mainConnection.getInputStream()).equals(Defs.Protocol.Login.MAIN_LOGIN)) {
                    mainUsername = RawSerializer.receiveString(mainConnection.getInputStream());
                    if (mainUsername == null) {
                        RawSerializer.sendInt(mainConnection.getOutputStream(), Defs.Protocol.Login.NOT_OK);
                        throw new ProtocolException("Protocol not respected: username is null");
                    } else if (UserManager.containsUserByUsername(mainUsername)) {
                        RawSerializer.sendInt(mainConnection.getOutputStream(), Defs.Protocol.Login.NOT_OK);
                        throw new ProtocolException("Protocol not respected: already exist a user registered with name \"" + mainUsername + "\"");
                    } else {
                        RawSerializer.sendInt(mainConnection.getOutputStream(), Defs.Protocol.Login.OK);
                    }
                } else throw new ProtocolException("Protocol not respected: first message is not the main login");
                serviceConnection = serviceServerSocket.accept();
                if (RawSerializer.receiveString(serviceConnection.getInputStream()).equals(Defs.Protocol.Login.SERVICE_LOGIN)) {
                    serviceUsername = RawSerializer.receiveString(serviceConnection.getInputStream());
                    if (!mainUsername.equals(serviceUsername)) {
                        RawSerializer.sendInt(mainConnection.getOutputStream(), Defs.Protocol.Login.NOT_OK);
                        throw new ProtocolException("Protocol not respected: received two different usernames");
                    } else {
                        RawSerializer.sendInt(serviceConnection.getOutputStream(), Defs.Protocol.Login.OK);
                    }
                } else throw new ProtocolException("Protocol not respected: second message is not the service login");
                if (!UserManager.insertNewUser(new User(mainConnection, serviceConnection, mainUsername))) throw new IOException("Can't add user to server even if the user can be added");
            } catch (ProtocolException e) {
                if (!interrupted) Logger.log(e);
                try {
                    if (mainConnection != null) mainConnection.close();
                } catch (Exception ignored) {}
                try {
                    if (serviceConnection != null) serviceConnection.close();
                } catch (Exception ignored) {}
            } catch (Exception e) {
                if (!interrupted) Logger.log(e);
                try {
                    if (mainConnection != null) RawSerializer.sendInt(mainConnection.getOutputStream(), Defs.Protocol.Login.ERROR);
                } catch (Exception ignored) {}
                try {
                    if (serviceConnection != null) RawSerializer.sendInt(serviceConnection.getOutputStream(), Defs.Protocol.Login.ERROR);
                } catch (Exception ignored) {}
                try {
                    if (mainConnection != null) mainConnection.close();
                } catch (Exception ignored) {}
                try {
                    if (serviceConnection != null) serviceConnection.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
