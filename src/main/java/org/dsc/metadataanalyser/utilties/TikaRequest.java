package org.dsc.metadataanalyser.utilties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;


public class TikaRequest {

    private final HttpClient client = HttpClient.newHttpClient();
    private String UploadFile;
    private String serviceUrl;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setUploadFile(String uploadFile) {
        UploadFile = uploadFile;
    }

    public InputStream makeTikaRequest() throws IOException, InterruptedException {
        System.out.println(UploadFile);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serviceUrl))
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofFile(Paths.get(UploadFile)))
                .build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        System.out.println(response.statusCode());
        return response.body();
    }

}
