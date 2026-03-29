package epaw.lab1.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitat per obrir el fitxer actual al navegador a localhost:8080.
 * Suporta fitxers HTML/JSP (a webapp) i Servlets Java (via @WebServlet).
 * 
 * Ús: java scripts/OpenBrowser.java <camí_absolut_fitxer>
 */
public class OpenBrowser {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Error: Falta el camí del fitxer.");
            System.exit(1);
        }

        String filePath = args[0];
        String url = resolveUrl(filePath);

        if (url != null) {
            System.out.println("🚀 Obrint navegador: " + url);
            openInBrowser(url);
        } else {
            System.err.println("❌ No s'ha pogut mapejar el fitxer a una URL de servidor.");
            System.exit(1);
        }

        // Afegim mig segon de marge de vida abans que la JVM mori. 
        // Això soluciona un problema conegut a Linux on les crides D-Bus (Desktop.browse)
        // a vegades es descarten silenciosament si el procés remitent es tanca instantàniament.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignorar
        }
    }

    private static String resolveUrl(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

        String normalizedPath = filePath.replace('\\', '/');

        // 1. Cas: Fitxer estàtic a src/main/webapp (HTML, JSP, CSS...)
        if (normalizedPath.contains("/src/main/webapp")) {
            String relative = normalizedPath.split("/src/main/webapp")[1];
            // Mantenim l'adreça explícita del fitxer (incloent index.html) perquè a vegades Jetty necessita el .html explícitament si no hi ha welcome-file
            return "http://localhost:8080" + relative;
        }

        // 2. Cas: Servlet de Java a src/main/java
        if (fileName.endsWith(".java")) {
            try {
                String content = Files.readString(path);
                // Busquem l'anotació @WebServlet("/ruta")
                Pattern pattern = Pattern.compile("@WebServlet\\s*\\(\\s*\"(.*?)\"\\s*\\)");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String servletPath = matcher.group(1);
                    if (!servletPath.startsWith("/"))
                        servletPath = "/" + servletPath;
                    return "http://localhost:8080" + servletPath;
                }
            } catch (IOException e) {
                System.err.println("Error llegint el fitxer: " + e.getMessage());
            }
        }

        return null;
    }

    private static void openInBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } else {
                    Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
                }
            } else if (os.contains("mac")) {
                if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } else {
                    Runtime.getRuntime().exec(new String[]{"open", url});
                }
            } else {
                // Tàctica Anti-VSCode:
                // VS Code mata (tree-kill) tots els processos "fills" quan la tasca s'acaba. 
                // Això destrueix el Google Chrome nou si no hi havia cap sessió prèvia oberta!
                // Usant 'setsid' desvinculem totalment el fill creant-li una nova sessió a l'escriptori.
                ProcessBuilder pb = new ProcessBuilder("setsid", "xdg-open", url);
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                pb.redirectError(ProcessBuilder.Redirect.DISCARD);
                pb.start();
            }
        } catch (Exception e) {
            System.err.println("No s'ha pogut obrir el navegador: " + e.getMessage());
        }
    }
}
