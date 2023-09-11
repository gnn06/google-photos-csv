package gnn.com.photos;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CLI {

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        System.out.println("begin");
        Lister lister = new Lister();
        lister.list();
        System.out.println("end");
    }
}
