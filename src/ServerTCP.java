import partTCP.Jumper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/*
9.   Разработать приложение для определения призовых мест на соревнования по прыжкам в длину.
На сервере хранятся фамилии участников соревнований их идентификационные номера. На клиентской
части вводятся результаты прыжков по каждому идентификационному номеру, а сервер возвращает фамилии
спортсменов, занявших 1, 2 и 3 места.

Приложение должно располагать возможностью передачи и модифицирования получаемых (передаваемых) данных.
* */
public class ServerTCP {
    private static final InetAddress HOST;
    private static final int PORT = 8001;
    private static Jumper[] jumpers;

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

    private static String recv(ObjectInputStream obj) throws IOException, ClassNotFoundException {
        return (String) obj.readObject();
    }

    private static boolean idExists(int id){
        for (var val : jumpers)
            if (val.getId() == id)
                return true;
        return false;
    }

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(PORT)) {
            while (!socket.isClosed()) {
                Socket connection = socket.accept();
                var clientInput = new ObjectInputStream(connection.getInputStream());
                var clientOutput = new ObjectOutputStream(connection.getOutputStream());
                String inMsg = (String) clientInput.readObject();
                while (!inMsg.equals("/s")) {
                    switch (inMsg) {
                        case "/input":
                            clientOutput.writeObject("id");
                            inMsg = recv(clientInput);
                            int id;
                            try {
                                id = parseInt(inMsg);
                            } catch (NumberFormatException ex) {
                                clientOutput.writeObject("~e");
                                continue;
                            }
                            if (!idExists(id)) {
                                clientOutput.writeObject("~e");
                                continue;
                            }
                            clientOutput.writeObject("длину прыжка");
                            inMsg = recv(clientInput);
                            double jumpLength;
                            try {
                                jumpLength = parseDouble(inMsg);
                            } catch (NumberFormatException ex) {
                                clientOutput.writeObject("~e");
                                continue;
                            }
                            for (Jumper jumper : jumpers)
                                if (jumper.getId() == id)
                                    jumper.setJumpLength(jumpLength);
                            clientOutput.writeObject("0");
                            break;
                        case "/winners":
                            Comparator<Jumper> comparator = (l, r) -> l.getJumpLength() < r.getJumpLength() ? 0 : 1;
                            ArrayList<Jumper> arr = new ArrayList<>();
                            for (Jumper jumper : jumpers)
                                if (jumper.getJumpLength() != 0)
                                    arr.add(jumper);
                            arr.sort(comparator);
                            StringBuilder parsed = new StringBuilder();
                            for (int i = 0; i < arr.size() && i < 3; i++) {
                                parsed.append((i + 1)).append('\t').append(arr.get(i).toString()).append('\n');
                            }
                            clientOutput.writeObject(parsed.toString());
                            break;
                        default:
                            inMsg = (String) clientInput.readObject();
                            break;
                    }
                }
                connection.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
