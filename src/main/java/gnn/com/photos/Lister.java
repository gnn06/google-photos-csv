package gnn.com.photos;

import gnn.com.photos.remote.PhotosRemoteService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class Lister {
    void list() throws GeneralSecurityException, IOException {
        PhotosRemoteService prs = new PhotosRemoteService();
        PrintStream output = null;
        ArrayList remote;
        try {
            output = new PrintStream(new FileOutputStream("output.csv"));
            prs.getAllPhotos(output);
        } finally {
            output.close();
        }
    }
}
