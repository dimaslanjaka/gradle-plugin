/**
 * @see "https://gist.github.com/iamnoah/9393655"
 * Usage: groovy webServer.groovy [-Pport=80] [-PwebRoot=/path/to/files]
 * usage: groovy -Dwebroot=./my-dir -Dport=8080 webServer.groovy
 * Or with gradle, place in src/main/groovy, and place assets in src/main/webapp
 * and use as the mainClassName.
 */
import com.sun.net.httpserver.*

// only supports basic web content types
final TYPES = [
        "css": "text/css",
        "gif": "image/gif",
        "html": "text/html",
        "jpg": "image/jpeg",
        "js": "application/javascript",
        "png": "image/png",
        "svg": "image/svg+xml",
]

def port = System.properties.port?.toInteger() ?: 8680
def root = new File(System.properties.webroot as String ?: "src/main/webapp")
def server = HttpServer.create(new InetSocketAddress(port), 0)
server.createContext("/", { HttpExchange exchange ->
    try {
        if (!"GET".equalsIgnoreCase(exchange.requestMethod)) {
            exchange.sendResponseHeaders(405, 0)
            exchange.responseBody.close()
            return
        }

        def path = exchange.requestURI.path
        println "GET $path"
        // path starts with /
        def file = new File(root, path.substring(1))
        if (file.isDirectory()) {
            file = new File(file, "index.html")
        }
        if (file.exists()) {
            exchange.responseHeaders.set("Content-Type",
                    TYPES[file.name.split(/\./)[-1]] ?: "text/plain")
            exchange.sendResponseHeaders(200, 0)
            file.withInputStream {
                exchange.responseBody << it
            }
            exchange.responseBody.close()
        } else {
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.close()
        }
    } catch(e) {
        e.printStackTrace()
    }
} as HttpHandler)
server.start()
println "started simple web server on port ${port}"