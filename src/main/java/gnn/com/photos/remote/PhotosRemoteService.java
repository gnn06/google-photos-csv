package gnn.com.photos.remote;

import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.BatchGetMediaItemsResponse;
import com.google.photos.library.v1.proto.MediaItem;
import com.google.photos.library.v1.proto.MediaItemResult;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import gnn.com.photos.model.Photo;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PhotosRemoteService {

    private PhotosLibraryClient client;

    private static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                    "https://www.googleapis.com/auth/photoslibrary.readonly");

    public PhotosRemoteService() throws IOException, GeneralSecurityException {
        this.client = PhotosLibraryClientFactory.createClient("./code_secret_client.json", REQUIRED_SCOPES);
    }

    private String SEPARATOR = ",";

    public void getAllPhotos(PrintStream outputFile) {
        InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = client.listMediaItems();
        int count = 0;
        for (MediaItem item : response.iterateAll()) {
            String line = "";
            // Get some properties of a media item
            String id = item.getId();
            String description = item.getDescription();
            String mimeType = item.getMimeType();
            String productUrl = item.getProductUrl();
            String filename = item.getFilename();
            Timestamp creationTime = item.getMediaMetadata().getCreationTime();
            line += id + SEPARATOR + Timestamps.toString(creationTime);
            outputFile.println(line);
            System.out.print(".");
            if (count % 100 == 0) System.out.println();
            count++;
            if (count >= 123) break;
        }
        System.out.println();
        System.out.println("debug : count=" + count);
    }

    public ArrayList getRemotePhotos(String albumName) {

        InternalPhotosLibraryClient.ListAlbumsPagedResponse response = client.listAlbums();
        for (Album album : response.iterateAll()) {
            String title = album.getTitle();
            if (albumName.equals(title)) {
                String albumId = album.getId();
                //System.out.println("albumId=" + albumId);
                InternalPhotosLibraryClient.SearchMediaItemsPagedResponse responsePhotos = client.searchMediaItems(albumId);
                int count = 0;
                ArrayList result = new ArrayList();
                for (MediaItem item : responsePhotos.iterateAll()) {
                    String filename = item.getFilename();
                    result.add(new Photo(item.getBaseUrl(), item.getId()));
                    count++;
                }
                System.out.println("count=" + count);
                return result;
            }
        }
        System.err.println("album not found");
        return null;
    }

    public void getPhotosFromIds() {
        // baseUrl expire after 60 minutes
        ArrayList<String> ids = new ArrayList<>();
        ids.add("ADoMfeQCtFtIatW-zK6LyG99cV603z25Yha0n_EgY8A419Z5BFAQwd_-HJw4udqBxDyJisrT5WwINegmWOXgUsR5VvUdcjz6lA");
        ids.add("ADoMfeQhYF7flymP6FB5GzRodCKRRSimZEBgQq5imnoQwSTMNqQTcQbhagU1EPs4gtQqPEwCkc291wSXH-ebIgq5SjTEdH6EUg");
        ids.add("AZEZERZER");
        BatchGetMediaItemsResponse response = client.batchGetMediaItems(ids);
        for (MediaItemResult result :
                response.getMediaItemResultsList()) {
            // code == 3 if id does not exist
            System.out.println(result.getStatus().getCode());
            System.out.println(result.getMediaItem().getBaseUrl());
        }
    }

}
