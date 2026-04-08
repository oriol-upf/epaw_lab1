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
        // Això soluciona un problema conegut a Linux on les crides D-Bus
        // (Desktop.browse)
        // a vegades es descarten silenciosament si el procés remitent es tanca
        // instantàniament.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignorar
        }
    }

    private static String resolveUrl(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        String normalizedPath = path.toString().replace('\\', '/');
        String lowerPath = normalizedPath.toLowerCase();

        // 1. Case: Static file in src/main/webapp (HTML, JSP, CSS...)
        int webappIdx = lowerPath.lastIndexOf("/src/main/webapp");
        if (webappIdx != -1) {
            String relative = normalizedPath.substring(webappIdx + "/src/main/webapp".length());
            return "http://localhost:8080" + relative;
        }

        // 2. Case: Static file in EXTERNAL_RESOURCES
        int externalIdx = lowerPath.lastIndexOf("/external_resources");
        if (externalIdx != -1) {
            String relative = normalizedPath.substring(externalIdx + "/external_resources".length());
            return "http://localhost:8080" + relative;
        }

        // Special case: if relative path provided without leading slash
        if (lowerPath.startsWith("src/main/webapp")) {
            return "http://localhost:8080/" + normalizedPath.substring("src/main/webapp/".length());
        }
        if (lowerPath.startsWith("external_resources")) {
            return "http://localhost:8080/" + normalizedPath.substring("external_resources/".length());
        }

        // 3. Case: Java Servlet in src/main/java
        if (normalizedPath.toLowerCase().endsWith(".java")) {
            try {
                // Forcem lectura en UTF-8 per evitar problemes d'encoding que "amaguin" el text
                String content = Files.readString(path, java.nio.charset.StandardCharsets.UTF_8);

                // Regex flexible que suporta diversos formats d'@WebServlet
                Pattern pattern = Pattern.compile(
                        "@WebServlet\\s*\\(\\s*(?:(?:value|urlPatterns)\\s*=\\s*)?[\"'](.*?)[\"']",
                        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                Matcher matcher = pattern.matcher(content);

                if (matcher.find()) {
                    String servletPath = matcher.group(1);
                    if (!servletPath.startsWith("/")) {
                        servletPath = "/" + servletPath;
                    }
                    return "http://localhost:8080" + servletPath;
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

        return null;
    }

    private static void openInBrowser(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                if (java.awt.Desktop.isDesktopSupported()
                        && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } else {
                    Runtime.getRuntime().exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
                }
            } else if (os.contains("mac")) {
                if (java.awt.Desktop.isDesktopSupported()
                        && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } else {
                    Runtime.getRuntime().exec(new String[] { "open", url });
                }
            } else {
                // Tàctica Anti-VSCode:
                // VS Code mata (tree-kill) tots els processos "fills" quan la tasca s'acaba.
                // Això destrueix el Google Chrome nou si no hi havia cap sessió prèvia oberta!
                // Usant 'setsid' desvinculem totalment el fill creant-li una nova sessió a
                // l'escriptori.
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
