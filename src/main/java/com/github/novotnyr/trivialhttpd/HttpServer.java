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

        String baseDirectoryArg = args[0];

        int port = 8080;
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        File baseDirectory = new File(baseDirectoryArg);
        ResourceHandler resourceHandler = new ResourceHandler(new FileResourceManager(baseDirectory));
        FileErrorHandler rootHandler = new FileErrorHandler(baseDirectory, resourceHandler);

        Undertow server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(rootHandler)
                .build();

        server.start();
    }
}
