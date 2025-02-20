package org.example;

public class HttpResponse {
    private final int statusCode;
    private final String statusMessage;
    private final String body;

    public HttpResponse(int statusCode, String statusMessage, String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.body = body;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
    }
}
