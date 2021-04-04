import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private static int PORT = 8081;
    private static InetAddress INETADDRESS;

    static {
        try {
            INETADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        if (args.length == 2) {
            INETADDRESS = InetAddress.getByName(args[0]);
            PORT = Integer.parseInt(args[1]);
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT,0, INETADDRESS)) {
            while (true) {
                Session session = new Session(serverSocket.accept());
                session.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int fibonacci(int n) {
        int a = 0;
        int b = 1;
        for (int i = 2; i <= n; i++) {
            int next = a + b;
            a = b;
            b = next;
        }
        return b;
    }
}

class Session extends Thread {
    private final Socket socket;

    public Session(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream())
        ) {
            out.flush();
            while (true) {
                try {
                    int n = Integer.parseInt(reader.readLine());
                    if (n > 46) {
                        out.println("Error! The maximum possible number is 46");
                    } else {
                        out.println(Server.fibonacci(n));
                    }
                    out.flush();
                } catch (NumberFormatException e) {
                    out.println("Error");
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
