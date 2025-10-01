package application;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.*;
import java.nio.file.Files;

public class FileManager {
    
    public static void saveProject(Stage stage, String html, String css, String js) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("WebCoderFX Project", "*.wcfx"),
            new FileChooser.ExtensionFilter("HTML Files", "*.html")
        );
        fileChooser.setInitialFileName("myproject.wcfx");
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                String projectData = "HTML:\n" + html + "\n\nCSS:\n" + css + "\n\nJavaScript:\n" + js;
                Files.writeString(file.toPath(), projectData);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save Successful");
                alert.setHeaderText("Project Saved");
                alert.setContentText("Project saved successfully to: " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Failed");
                alert.setHeaderText("Error Saving File");
                alert.setContentText("Could not save file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    public static void exportHTML(Stage stage, String html, String css, String js) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export HTML File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("HTML Files", "*.html")
        );
        fileChooser.setInitialFileName("mywebsite.html");
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                String fullHTML = "<!DOCTYPE html>\n" +
                                 "<html>\n" +
                                 "<head>\n" +
                                 "    <meta charset=\"UTF-8\">\n" +
                                 "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                                 "    <title>My Website</title>\n" +
                                 "    <style>\n" + css + "\n    </style>\n" +
                                 "</head>\n" +
                                 "<body>\n" + html + "\n" +
                                 "    <script>\n" + js + "\n    </script>\n" +
                                 "</body>\n</html>";
                
                Files.writeString(file.toPath(), fullHTML);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText("HTML Exported");
                alert.setContentText("HTML file exported successfully to: " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Failed");
                alert.setHeaderText("Error Exporting File");
                alert.setContentText("Could not export file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    public static String[] loadFromFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("WebCoderFX Project", "*.wcfx"),
            new FileChooser.ExtensionFilter("HTML Files", "*.html"),
            new FileChooser.ExtensionFilter("CSS Files", "*.css"),
            new FileChooser.ExtensionFilter("JavaScript Files", "*.js"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                
                if (file.getName().endsWith(".wcfx")) {
                    return parseProjectFile(content);
                } else if (file.getName().endsWith(".css")) {
                    return new String[]{"", content, ""};
                } else if (file.getName().endsWith(".js")) {
                    return new String[]{"", "", content};
                } else {
                    String html = extractHTML(content);
                    String css = extractCSS(content);
                    String js = extractJavaScript(content);
                    return new String[]{html, css, js};
                }
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Load Failed");
                alert.setHeaderText("Error Loading File");
                alert.setContentText("Could not load file: " + e.getMessage());
                alert.showAndWait();
            }
        }
        return new String[]{"", "", ""};
    }
    
    private static String[] parseProjectFile(String content) {
        String html = "", css = "", js = "";
        String[] parts = content.split("\n\n");
        
        for (String part : parts) {
            if (part.startsWith("HTML:")) {
                html = part.substring(5).trim();
            } else if (part.startsWith("CSS:")) {
                css = part.substring(4).trim();
            } else if (part.startsWith("JavaScript:")) {
                js = part.substring(11).trim();
            }
        }
        
        return new String[]{html, css, js};
    }
    
    private static String extractHTML(String content) {
        int bodyStart = content.indexOf("<body>");
        int bodyEnd = content.indexOf("</body>");
        if (bodyStart != -1 && bodyEnd != -1) {
            return content.substring(bodyStart + 6, bodyEnd).trim();
        }
        return content;
    }
    
    private static String extractCSS(String content) {
        int styleStart = content.indexOf("<style>");
        int styleEnd = content.indexOf("</style>");
        if (styleStart != -1 && styleEnd != -1) {
            return content.substring(styleStart + 7, styleEnd).trim();
        }
        return "";
    }
    
    private static String extractJavaScript(String content) {
        int scriptStart = content.indexOf("<script>");
        int scriptEnd = content.indexOf("</script>");
        if (scriptStart != -1 && scriptEnd != -1) {
            return content.substring(scriptStart + 8, scriptEnd).trim();
        }
        return "";
    }
}