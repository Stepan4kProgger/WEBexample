import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import partTCP.Jumper;

public class Main {
    private static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int PORT = 8001;

    private static void useTCP(Scanner scanner) {
        //тело клиента на TCP
    }

    private static void useUDP(Scanner scanner) throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                System.out.println("Выберите действие:\n" +
                        "1 - Отправить параметры для уравнения\n" +
                        "0 - Завершить работу");
                switch (scanner.next()) {
                    case "1": {
                        double x, y, z;
                        System.out.println("Вводите переменные без ошибок!\nВведите х:");
                        if (scanner.hasNextDouble())
                            x = scanner.nextDouble();
                        else break;
                        System.out.println("Введите y:");
                        if (scanner.hasNextDouble())
                            y = scanner.nextDouble();
                        else break;
                        System.out.println("Введите z:");
                        if (scanner.hasNextDouble())
                            z = scanner.nextDouble();
                        else break;
                        String line = "" + x + ' ' + y + ' ' + z;
                        byte[] params = line.getBytes(StandardCharsets.UTF_8);
                        DatagramPacket packet = new DatagramPacket(params, params.length, HOST, PORT);
                        socket.send(packet);
                        byte[] buf = new byte[128];
                        DatagramPacket received = new DatagramPacket(buf, buf.length, HOST, PORT);
                        socket.receive(received);
                        String res = new String(received.getData()).trim();
                        System.out.println("Ответ: " + res);
                        try (FileWriter file = new FileWriter("src\\Solutions.txt", true)) {
                            file.append("x=" + x+ "; y=" + y + "; z=" + z + "; Result: " + res + "\n");
                        }
                        break;
                    }
                    case "0":
                        return;
                }
                scanner.reset();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - TCP part\n2 - UDP part\nEnter num of part");
        if (scanner.next().equals("1")) useTCP(scanner);
        else useUDP(scanner);
    }
}
