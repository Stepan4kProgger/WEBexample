import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    private static final int PORTTCP = 7999;
    private static final int PORTUDP = 8001;

    private static void send(String str, ObjectOutputStream obj) throws IOException {
        obj.writeObject(str);
    }

    private static String recv(ObjectInputStream obj) throws IOException, ClassNotFoundException {
        return (String) obj.readObject();
    }

    private static void useTCP(Scanner scanner) throws Exception {
        try (Socket socket = new Socket(HOST, PORTTCP)) {
            var output = new ObjectOutputStream(socket.getOutputStream());
            var input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                System.out.println("Доступные команды:\n" +
                        "/s - прекратить работу\n" +
                        "/input - ввести длину прыжка спортсмена (следуйте инструкциям после вызова)\n" +
                        "/winners - отобразить тройку лидеров\n");
                String msg = scanner.next();
                send(msg, output);
                if (msg.equals("/s"))
                    break;
                else if (msg.equals("/input")){
                    System.out.println("Введите " + recv(input) + ":");
                    send(scanner.next(), output);
                    msg = recv(input);
                    if (msg.equals("~e")) {
                        System.out.println("Возникла ошибка.");
                        continue;
                    }
                    System.out.println("Введите " + msg + ":");
                    send(scanner.next(), output);
                    msg = recv(input);
                    if (msg.equals("~e")) {
                        System.out.println("Возникла ошибка.");
                        continue;
                    }
                    System.out.println("Успешно");
                } else if (msg.equals("/winners")) {
                    msg = recv(input);
                    if (msg.isEmpty())
                        System.out.println("Команда не вернула ответ");
                    else
                        System.out.println("Место\tИмя\tДлина прыжка\n" + msg);
                }
            }
        }
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
                        DatagramPacket packet = new DatagramPacket(params, params.length, HOST, PORTUDP);
                        socket.send(packet);
                        byte[] buf = new byte[128];
                        DatagramPacket received = new DatagramPacket(buf, buf.length, HOST, PORTUDP);
                        socket.receive(received);
                        String res = new String(received.getData()).trim();
                        System.out.println("Ответ: " + res);
                        try (FileWriter file = new FileWriter("src\\Solutions.txt", true)) {
                            file.append("x=" + x + "; y=" + y + "; z=" + z + "; Result: " + res + "\n");
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

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - TCP part\n2 - UDP part\nEnter num of part");
        if (scanner.next().equals("1")) useTCP(scanner);
        else useUDP(scanner);
    }
}