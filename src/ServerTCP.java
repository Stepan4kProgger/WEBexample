import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
9.   Разработать приложение для определения призовых мест на соревнования по прыжкам в длину.
На сервере хранятся фамилии участников соревнований их идентификационные номера. На клиентской
части вводятся результаты прыжков по каждому идентификационному номеру, а сервер возвращает фамилии
спортсменов, занявших 1, 2 и 3 места.

Приложение должно располагать возможностью передачи и модифицирования получаемых (передаваемых) данных.
* */
public class ServerTCP {
    private static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int PORT = 8001;

    public static void main(String[] args) {


    }
}
