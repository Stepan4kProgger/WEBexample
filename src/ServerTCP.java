import partTCP.Jumper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.reverse;

/*
9.   Разработать приложение для определения призовых мест на соревнования по прыжкам в длину.
На сервере хранятся фамилии участников соревнований их идентификационные номера. На клиентской
части вводятся результаты прыжков по каждому идентификационному номеру, а сервер возвращает фамилии
спортсменов, занявших 1, 2 и 3 места.

Приложение должно располагать возможностью передачи и модифицирования получаемых (передаваемых) данных.
* */
public class ServerTCP {
    private static final InetAddress HOST;
    private static final int PORT = 7999;
    public static Jumper[] jumpers;

    static {
        try {
            HOST = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            jumpers = init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Jumper[] init() throws IOException {
        FileReader file = new FileReader("src\\partTCP\\ServerDat.txt");
        Scanner scanner = new Scanner(file);
        ArrayList<String> strings = new ArrayList<>();
        while (scanner.hasNextLine())
            strings.add(scanner.nextLine());

        Jumper[] jumpers = new Jumper[strings.size()];
        for (short i = 0; i < jumpers.length; i++)
            jumpers[i] = new Jumper(strings.get(i));
        return jumpers;
    }



    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(PORT)) {
            int i = 1;
            while (!socket.isClosed()) {
                Socket connection = socket.accept();
                Thread myThread = new Thread(new TCPThread(connection),"Thread " + i++);
                myThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
