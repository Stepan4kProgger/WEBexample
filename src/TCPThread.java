import partTCP.Jumper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class TCPThread implements Runnable {

    private Socket connection;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;

    public TCPThread(Socket connection) throws IOException {
        this.connection = connection;
        clientInput = new ObjectInputStream(connection.getInputStream());
        clientOutput = new ObjectOutputStream(connection.getOutputStream());
    }

    private static String recv(ObjectInputStream obj) throws IOException, ClassNotFoundException {
        return (String) obj.readObject();
    }

    private static boolean idExists(int id) {
        for (var val : ServerTCP.jumpers)
            if (val.getId() == id)
                return true;
        return false;
    }

    @Override
    public void run() {
        System.out.println("Запущен поток " + Thread.currentThread().getName());
        try {
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
                        for (Jumper jumper : ServerTCP.jumpers)
                            if (jumper.getId() == id)
                                jumper.setJumpLength(jumpLength);
                        clientOutput.writeObject("0");
                        break;
                    case "/winners":
                        Comparator<Jumper> comparator = Comparator.comparing(Jumper::getJumpLength).reversed();
                        ArrayList<Jumper> arr = new ArrayList<>();
                        for (Jumper jumper : ServerTCP.jumpers)
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
                        inMsg = recv(clientInput);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            clientOutput.close();
            clientInput.close();
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Поток " + Thread.currentThread().getName() + " завершен");
    }
}
