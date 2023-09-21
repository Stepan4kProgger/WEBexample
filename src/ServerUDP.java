import java.io.IOException;
import java.net.*;

public class ServerUDP {
    private static final int PORT = 8001;

    private static double solve(double x, double y, double z){
        return Math.sqrt(8 + (x + y) * (x + y) + z)
                / (x*x + y*y + z*z)
                - Math.exp(Math.abs(x-y))
                * (Math.pow(Math.tan(z), 2) + Math.pow(Math.abs(z), (double) 1 /5));
    }
    public static void main(String[] args) throws SocketException {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Server is on!");
            while (!socket.isClosed()){
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String[] parsed = new String(packet.getData()).trim().split(" ");
                double answer = solve(Double.parseDouble(parsed[0]),
                        Double.parseDouble(parsed[1]),
                        Double.parseDouble(parsed[2]));
                buf = ("" + answer).getBytes();
                DatagramPacket toSend = new DatagramPacket(buf, buf.length,
                        packet.getAddress(), packet.getPort());
                socket.send(toSend);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
