package com.dimaslanjaka.gradle.server

import com.sun.net.httpserver.*

class BasicHttpServerExample {
    static def home = System.properties['user.home']
    static File root = new File(home as File, '.m2/repository')

    static {
        if (!root.exists()) root.mkdirs()
        def index = new File(root, "index.php")
        if (!index.exists()) {
            index.write("Index Repository")
        }
    }

    static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0)
        HttpContext context = server.createContext("/example")
        context.setHandler(BasicHttpServerExample.&handleRequest)
        server.start()
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI()
        printRequestInfo(exchange)
        String response = "This is the response at " + requestURI
        exchange.sendResponseHeaders(200, response.getBytes().length)
        OutputStream os = exchange.getResponseBody()
        os.write(response.getBytes())
        os.close()
    }

    private static void printRequestInfo(HttpExchange exchange) {
        System.out.println("-- headers --")
        Headers requestHeaders = exchange.getRequestHeaders()
        requestHeaders.entrySet().forEach(System.out.&println)

        System.out.println("-- principle --")
        HttpPrincipal principal = exchange.getPrincipal()
        System.out.println(principal)

        System.out.println("-- HTTP method --")
        String requestMethod = exchange.getRequestMethod()
        System.out.println(requestMethod)

        System.out.println("-- query --")
        URI requestURI = exchange.getRequestURI()
        String query = requestURI.getQuery()
        System.out.println(query)
    }
}