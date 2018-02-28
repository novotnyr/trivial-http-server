package com.github.novotnyr.trivialhttpd;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;

import java.io.File;

public class HttpServer {
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: HttpServer <baseDirectory> [port]");
            System.exit(1);
        }

        String baseDirectory = args[0];

        int port = 8080;
        if (args.length >= 2) {
            port = Integer.parseInt(args[2]);
        }

        ResourceHandler resourceHandler = new ResourceHandler(new FileResourceManager(new File(baseDirectory)));

        Undertow server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(resourceHandler)
                .build();

        server.start();
    }
}
