package com.github.novotnyr.trivialhttpd;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.xnio.channels.StreamSinkChannel;

import java.io.File;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves any URL that ends with HTTP error with a file from the filesystem.
 *
 * For example, when the URL ends with {@code /500}, it resolves to the file {@code 500} (no extension)
 * from the base directory.
 */
public class FileErrorHandler implements HttpHandler {
    private static final Pattern ERROR_URL_REGEX = Pattern.compile(".*/(\\d{3})");

    private final File baseDirectory;

    private final HttpHandler next;

    public FileErrorHandler(File baseDirectory, HttpHandler next) {
        this.baseDirectory = baseDirectory;
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        int errorCode = getErrorCode(httpServerExchange);
        if (errorCode != -1) {
            httpServerExchange.setStatusCode(errorCode);
            File file = new File(this.baseDirectory, String.valueOf(errorCode));
            if (file.exists()) {
                StreamSinkChannel responseChannel = httpServerExchange.getResponseChannel();
                FileChannel fileChannel = FileChannel.open(file.toPath());
                responseChannel.transferFrom(fileChannel, 0, file.length());
                return;
            }
        }
        this.next.handleRequest(httpServerExchange);
    }

    private int getErrorCode(HttpServerExchange httpServerExchange) {
        String requestPath = httpServerExchange.getRequestPath();
        Matcher matcher = ERROR_URL_REGEX.matcher(requestPath);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
}
