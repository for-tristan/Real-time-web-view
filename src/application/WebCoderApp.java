package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;
import javafx.util.Pair;

public class WebCoderApp extends Application {
    private TextArea htmlEditor;
    private TextArea cssEditor;
    private TextArea jsEditor;
    private WebView webView;
    private Stage primaryStage;
    private MenuBar menuBar;
    private Label statusBar;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("WebCoderFX - Modern Web Development IDE");
        
        createEditors();
        createWebPreview();
        createMenuBar();
        createStatusBar();
        
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(statusBar);
        
        Scene scene = new Scene(root, 1600, 900);
        applyTheme(scene);
        
        primaryStage.setScene(scene);
        setupKeyboardShortcuts(scene);
        primaryStage.show();
        
        updatePreview();
    }
    
    private void createEditors() {
        htmlEditor = createEditor(getDefaultHTML(), "html");
        cssEditor = createEditor(getDefaultCSS(), "css");
        jsEditor = createEditor(getDefaultJavaScript(), "js");
        
        setupEditorContextMenu(htmlEditor, "html");
        setupEditorContextMenu(cssEditor, "css");
        setupEditorContextMenu(jsEditor, "js");
    }
    
    private TextArea createEditor(String content, String type) {
        TextArea editor = new TextArea(content);
        editor.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 14px;");
        editor.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePreview();
            updateStatus("Editing " + type.toUpperCase() + "...");
        });
        return editor;
    }
    
    private void setupEditorContextMenu(TextArea editor, String type) {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem formatCode = new MenuItem("Format Code");
        MenuItem commentSelection = new MenuItem("Comment Selection");
        MenuItem duplicateLine = new MenuItem("Duplicate Line");
        MenuItem gotoLine = new MenuItem("Go to Line...");
        
        formatCode.setOnAction(e -> formatEditorCode(editor, type));
        commentSelection.setOnAction(e -> commentSelection(editor, type));
        duplicateLine.setOnAction(e -> duplicateCurrentLine(editor));
        gotoLine.setOnAction(e -> showGotoLineDialog(editor));
        
        if (type.equals("html")) {
            Menu htmlMenu = new Menu("HTML Snippets");
            MenuItem divSnippet = new MenuItem("Insert Div");
            MenuItem buttonSnippet = new MenuItem("Insert Button");
            MenuItem formSnippet = new MenuItem("Insert Form");
            
            divSnippet.setOnAction(e -> insertHtmlSnippet(editor, "<div class=\"container\">\n    \n</div>"));
            buttonSnippet.setOnAction(e -> insertHtmlSnippet(editor, "<button class=\"btn\" onclick=\"handleClick()\">Click me</button>"));
            formSnippet.setOnAction(e -> insertHtmlSnippet(editor, "<form>\n    <input type=\"text\" placeholder=\"Enter text\">\n    <button type=\"submit\">Submit</button>\n</form>"));
            
            htmlMenu.getItems().addAll(divSnippet, buttonSnippet, formSnippet);
            contextMenu.getItems().add(htmlMenu);
        } else if (type.equals("js")) {
            Menu jsMenu = new Menu("JavaScript Snippets");
            MenuItem functionSnippet = new MenuItem("Insert Function");
            MenuItem eventListenerSnippet = new MenuItem("Insert Event Listener");
            MenuItem consoleLogSnippet = new MenuItem("Insert console.log");
            
            functionSnippet.setOnAction(e -> insertJsSnippet(editor, "function functionName() {\n    // Your code here\n}"));
            eventListenerSnippet.setOnAction(e -> insertJsSnippet(editor, "document.addEventListener('DOMContentLoaded', function() {\n    // Your code here\n});"));
            consoleLogSnippet.setOnAction(e -> insertJsSnippet(editor, "console.log('Hello World');"));
            
            jsMenu.getItems().addAll(functionSnippet, eventListenerSnippet, consoleLogSnippet);
            contextMenu.getItems().add(jsMenu);
        }
        
        contextMenu.getItems().addAll(new SeparatorMenuItem(), formatCode, commentSelection, duplicateLine, new SeparatorMenuItem(), gotoLine);
        editor.setContextMenu(contextMenu);
    }
    
    private void createWebPreview() {
        webView = new WebView();
        webView.setZoom(0.9);
        
        webView.getEngine().setOnAlert(event -> {
            updateStatus("JS Alert: " + event.getData());
        });
        
        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
                
                webView.getEngine().executeScript(
                    "console.log = function(message) { " +
                    "   javaConnector.logToJava(message); " +
                    "}; " +
                    "console.error = function(message) { " +
                    "   javaConnector.errorToJava(message); " +
                    "};"
                );
            }
        });
    }
    
    public class JavaConnector {
        public void logToJava(String message) {
            updateStatus("JS Console: " + message);
        }
        
        public void errorToJava(String message) {
            updateStatus("JS Error: " + message);
        }
    }
    
    private void createMenuBar() {
        menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open...");
        MenuItem saveFile = new MenuItem("Save As...");
        MenuItem exportHTML = new MenuItem("Export HTML");
        MenuItem exitApp = new MenuItem("Exit");
        
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exitApp.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        
        newFile.setOnAction(e -> newFile());
        openFile.setOnAction(e -> openFile());
        saveFile.setOnAction(e -> saveFile());
        exportHTML.setOnAction(e -> exportHTML());
        exitApp.setOnAction(e -> primaryStage.close());
        
        fileMenu.getItems().addAll(newFile, openFile, saveFile, exportHTML, new SeparatorMenuItem(), exitApp);
        
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem findReplace = new MenuItem("Find and Replace...");
        MenuItem selectAll = new MenuItem("Select All");
        MenuItem runJS = new MenuItem("Run JavaScript");
        
        findReplace.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        selectAll.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
        runJS.setAccelerator(new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN));
        
        findReplace.setOnAction(e -> showFindReplaceDialog());
        selectAll.setOnAction(e -> selectAllText());
        runJS.setOnAction(e -> runJavaScript());
        
        editMenu.getItems().addAll(findReplace, selectAll, new SeparatorMenuItem(), runJS);
        
        // View Menu
        Menu viewMenu = new Menu("View");
        Menu themeMenu = new Menu("Themes");
        
        for (ThemeManager.Theme theme : ThemeManager.Theme.values()) {
            MenuItem themeItem = new MenuItem(theme.toString());
            themeItem.setOnAction(e -> switchTheme(theme));
            themeMenu.getItems().add(themeItem);
        }
        
        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem resetZoom = new MenuItem("Reset Zoom");
        
        zoomIn.setAccelerator(new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN));
        zoomOut.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));
        resetZoom.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN));
        
        zoomIn.setOnAction(e -> zoomWebView(0.1));
        zoomOut.setOnAction(e -> zoomWebView(-0.1));
        resetZoom.setOnAction(e -> webView.setZoom(0.9));
        
        viewMenu.getItems().addAll(themeMenu, new SeparatorMenuItem(), zoomIn, zoomOut, resetZoom);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        MenuItem shortcuts = new MenuItem("Keyboard Shortcuts");
        
        about.setOnAction(e -> showAboutDialog());
        shortcuts.setOnAction(e -> showShortcutsDialog());
        
        helpMenu.getItems().addAll(about, shortcuts);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
    }
    
    private void createStatusBar() {
        statusBar = new Label("Ready - WebCoderFX with JavaScript Support");
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #cccccc;");
    }
    
    private VBox createHeader() {
        ToolBar toolBar = new ToolBar();
        
        Button newBtn = createToolbarButton("New", "📄");
        Button openBtn = createToolbarButton("Open", "📂");
        Button saveBtn = createToolbarButton("Save", "💾");
        Button refreshBtn = createToolbarButton("Refresh", "🔄");
        Button runJSBtn = createToolbarButton("Run JS", "⚡");
        Button exportBtn = createToolbarButton("Export", "📤");
        
        newBtn.setOnAction(e -> newFile());
        openBtn.setOnAction(e -> openFile());
        saveBtn.setOnAction(e -> saveFile());
        refreshBtn.setOnAction(e -> updatePreview());
        runJSBtn.setOnAction(e -> runJavaScript());
        exportBtn.setOnAction(e -> exportHTML());
        
        toolBar.getItems().addAll(newBtn, openBtn, saveBtn, new Separator(), refreshBtn, runJSBtn, exportBtn);
        
        VBox header = new VBox();
        header.getChildren().addAll(menuBar, toolBar);
        return header;
    }
    
    private Button createToolbarButton(String tooltip, String emoji) {
        Button button = new Button(emoji);
        button.setTooltip(new Tooltip(tooltip));
        button.setStyle("-fx-font-size: 16px; -fx-padding: 8 12 8 12;");
        return button;
    }
    
    private SplitPane createMainContent() {
        TabPane editorTabs = new TabPane();
        Tab htmlTab = new Tab("HTML", htmlEditor);
        Tab cssTab = new Tab("CSS", cssEditor);
        Tab jsTab = new Tab("JavaScript", jsEditor);
        htmlTab.setClosable(false);
        cssTab.setClosable(false);
        jsTab.setClosable(false);
        editorTabs.getTabs().addAll(htmlTab, cssTab, jsTab);
        
        VBox previewPanel = new VBox(5);
        previewPanel.setPadding(new Insets(10));
        Label previewLabel = new Label("Live Preview");
        previewLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        previewPanel.getChildren().addAll(previewLabel, webView);
        VBox.setVgrow(webView, Priority.ALWAYS);
        
        SplitPane mainSplit = new SplitPane();
        mainSplit.getItems().addAll(editorTabs, previewPanel);
        mainSplit.setDividerPositions(0.5);
        
        return mainSplit;
    }
    
    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add("data:text/css," + ThemeManager.getThemeCSS());
    }
    
    private void setupKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S -> saveFile();
                    case R -> updatePreview();
                    case L -> showGotoLineDialog(getFocusedEditor());
                    case J -> runJavaScript();
                }
            }
        });
    }
    
    // Menu Actions
    private void newFile() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("New File");
        alert.setHeaderText("Create new file?");
        alert.setContentText("Unsaved changes will be lost. Continue?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                htmlEditor.setText(getDefaultHTML());
                cssEditor.setText(getDefaultCSS());
                jsEditor.setText(getDefaultJavaScript());
                updatePreview();
                updateStatus("New file created");
            }
        });
    }
    
    private void openFile() {
        String[] content = FileManager.loadFromFile(primaryStage);
        if (content != null && content.length >= 3) {
            htmlEditor.setText(content[0]);
            cssEditor.setText(content[1]);
            jsEditor.setText(content[2]);
            updatePreview();
            updateStatus("File loaded successfully");
        }
    }
    
    private void saveFile() {
        FileManager.saveProject(primaryStage, htmlEditor.getText(), cssEditor.getText(), jsEditor.getText());
    }
    
    private void exportHTML() {
        FileManager.exportHTML(primaryStage, htmlEditor.getText(), cssEditor.getText(), jsEditor.getText());
    }
    
    private void showFindReplaceDialog() {
        TextInputDialog findDialog = new TextInputDialog();
        findDialog.setTitle("Find");
        findDialog.setHeaderText("Enter text to find:");
        findDialog.showAndWait().ifPresent(findText -> {
            TextArea focusedEditor = getFocusedEditor();
            findInEditor(focusedEditor, findText);
        });
    }
    
    private void findInEditor(TextArea editor, String findText) {
        String content = editor.getText();
        int index = content.indexOf(findText, editor.getCaretPosition());
        
        if (index == -1) {
            index = content.indexOf(findText);
        }
        
        if (index != -1) {
            editor.selectRange(index, index + findText.length());
            editor.requestFocus();
            updateStatus("Found: " + findText);
        } else {
            updateStatus("Text not found: " + findText);
        }
    }
    
    private void selectAllText() {
        TextArea focusedEditor = getFocusedEditor();
        focusedEditor.selectAll();
        focusedEditor.requestFocus();
    }
    
    private void runJavaScript() {
        try {
            String jsCode = jsEditor.getText();
            Object result = webView.getEngine().executeScript(jsCode);
            if (result != null) {
                updateStatus("JavaScript executed. Result: " + result.toString());
            } else {
                updateStatus("JavaScript executed successfully.");
            }
        } catch (Exception e) {
            updateStatus("JavaScript Error: " + e.getMessage());
        }
    }
    
    private void switchTheme(ThemeManager.Theme theme) {
        ThemeManager.setTheme(theme);
        applyTheme(primaryStage.getScene());
        updateStatus("Theme switched to: " + theme);
    }
    
    private void zoomWebView(double delta) {
        double newZoom = webView.getZoom() + delta;
        if (newZoom >= 0.5 && newZoom <= 2.0) {
            webView.setZoom(newZoom);
            updateStatus("Zoom: " + String.format("%.0f%%", newZoom * 100));
        }
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About WebCoderFX");
        alert.setHeaderText("WebCoderFX - Modern Web Development IDE");
        alert.setContentText("""
            Version 2.0
            
            A complete web development environment with:
            • Real-time HTML/CSS/JavaScript preview
            • Multiple themes
            • Code formatting and snippets
            • JavaScript console integration
            • File management and export
            
            Built with JavaFX
            """);
        alert.showAndWait();
    }
    
    private void showShortcutsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keyboard Shortcuts");
        alert.setHeaderText("Keyboard Shortcuts");
        alert.setContentText("""
            Ctrl+N - New File
            Ctrl+O - Open File
            Ctrl+S - Save File
            Ctrl+Q - Exit
            
            Ctrl+F - Find and Replace
            Ctrl+A - Select All
            Ctrl+L - Go to Line
            Ctrl+R - Refresh Preview
            Ctrl+J - Run JavaScript
            
            Ctrl+= - Zoom In
            Ctrl+- - Zoom Out
            Ctrl+0 - Reset Zoom
            """);
        alert.showAndWait();
    }
    
    private void showGotoLineDialog(TextArea editor) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Go to Line");
        dialog.setHeaderText("Enter line number:");
        dialog.setContentText("Line:");
        
        dialog.showAndWait().ifPresent(lineStr -> {
            try {
                int lineNumber = Integer.parseInt(lineStr) - 1;
                if (lineNumber < 0) lineNumber = 0;
                
                String text = editor.getText();
                String[] lines = text.split("\n", -1);
                
                if (lineNumber < lines.length) {
                    int position = 0;
                    for (int i = 0; i < lineNumber; i++) {
                        position += lines[i].length() + 1;
                    }
                    editor.positionCaret(position);
                    editor.requestFocus();
                    updateStatus("Jumped to line " + (lineNumber + 1));
                } else {
                    updateStatus("Line " + (lineNumber + 1) + " does not exist");
                }
            } catch (NumberFormatException e) {
                updateStatus("Invalid line number");
            }
        });
    }
    
    // Editor Features
    private void formatEditorCode(TextArea editor, String type) {
        String text = editor.getText();
        switch (type) {
            case "html":
                text = text.replace("\n", "\n    ");
                break;
            case "css":
                text = text.replace("}", "\n}\n");
                text = text.replace("{", " {\n    ");
                break;
            case "js":
                text = text.replace("}", "\n}\n");
                text = text.replace("{", " {\n    ");
                text = text.replace(";", ";\n");
                break;
        }
        editor.setText(text);
        updateStatus(type.toUpperCase() + " code formatted");
    }
    
    private void commentSelection(TextArea editor, String type) {
        String selectedText = editor.getSelectedText();
        if (!selectedText.isEmpty()) {
            switch (type) {
                case "html":
                    editor.replaceSelection("<!-- " + selectedText + " -->");
                    break;
                case "css":
                    editor.replaceSelection("/* " + selectedText + " */");
                    break;
                case "js":
                    if (selectedText.contains("\n")) {
                        editor.replaceSelection("/*\n" + selectedText + "\n*/");
                    } else {
                        editor.replaceSelection("// " + selectedText);
                    }
                    break;
            }
        }
    }
    
    private void duplicateCurrentLine(TextArea editor) {
        int caretPosition = editor.getCaretPosition();
        String text = editor.getText();
        
        int lineStart = text.lastIndexOf("\n", caretPosition - 1) + 1;
        if (lineStart < 0) lineStart = 0;
        
        int lineEnd = text.indexOf("\n", caretPosition);
        if (lineEnd == -1) lineEnd = text.length();
        
        String currentLine = text.substring(lineStart, lineEnd);
        String newText = text.substring(0, lineEnd) + "\n" + currentLine + text.substring(lineEnd);
        editor.setText(newText);
        
        int newCaretPosition = lineEnd + currentLine.length() + 1;
        editor.positionCaret(newCaretPosition);
        
        updateStatus("Line duplicated");
    }
    
    private void insertHtmlSnippet(TextArea editor, String snippet) {
        int caretPos = editor.getCaretPosition();
        editor.insertText(caretPos, snippet);
        updateStatus("HTML snippet inserted");
    }
    
    private void insertJsSnippet(TextArea editor, String snippet) {
        int caretPos = editor.getCaretPosition();
        editor.insertText(caretPos, snippet);
        updateStatus("JavaScript snippet inserted");
    }
    
    private TextArea getFocusedEditor() {
        if (htmlEditor.isFocused()) return htmlEditor;
        if (cssEditor.isFocused()) return cssEditor;
        if (jsEditor.isFocused()) return jsEditor;
        return htmlEditor;
    }
    
    private void updatePreview() {
        String html = htmlEditor.getText();
        String css = cssEditor.getText();
        String js = jsEditor.getText();
        
        String fullHTML = "<!DOCTYPE html>\n" +
                         "<html>\n" +
                         "<head>\n" +
                         "    <meta charset=\"UTF-8\">\n" +
                         "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                         "    <title>WebCoderFX Preview</title>\n" +
                         "    <style>\n" + css + "\n    </style>\n" +
                         "</head>\n" +
                         "<body>\n" + html + "\n" +
                         "    <script>\n" + js + "\n    </script>\n" +
                         "</body>\n</html>";
        
        webView.getEngine().loadContent(fullHTML);
        updateStatus("Preview updated");
    }
    
    private void updateStatus(String message) {
        statusBar.setText(message);
    }
    
    // Default Content
    private String getDefaultHTML() {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <title>My Interactive Website</title>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"hero\">\n" +
               "        <div class=\"container\">\n" +
               "            <h1 class=\"title\">Welcome to WebCoderFX!</h1>\n" +
               "            <p class=\"subtitle\">Now with JavaScript support</p>\n" +
               "            <button class=\"cta-button\" onclick=\"handleClick()\">Click Me</button>\n" +
               "            <div id=\"output\" class=\"output\"></div>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }
    
    private String getDefaultCSS() {
        return "/* Reset and base styles */\n" +
               "* {\n" +
               "    margin: 0;\n" +
               "    padding: 0;\n" +
               "    box-sizing: border-box;\n" +
               "}\n" +
               "\n" +
               "body {\n" +
               "    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
               "    line-height: 1.6;\n" +
               "    color: #333;\n" +
               "}\n" +
               "\n" +
               ".container {\n" +
               "    max-width: 1200px;\n" +
               "    margin: 0 auto;\n" +
               "    padding: 0 20px;\n" +
               "}\n" +
               "\n" +
               "/* Hero section */\n" +
               ".hero {\n" +
               "    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
               "    color: white;\n" +
               "    padding: 120px 0;\n" +
               "    text-align: center;\n" +
               "    min-height: 100vh;\n" +
               "    display: flex;\n" +
               "    align-items: center;\n" +
               "    justify-content: center;\n" +
               "}\n" +
               "\n" +
               ".title {\n" +
               "    font-size: 4rem;\n" +
               "    margin-bottom: 1rem;\n" +
               "    text-shadow: 2px 2px 4px rgba(0,0,0,0.3);\n" +
               "}\n" +
               "\n" +
               ".subtitle {\n" +
               "    font-size: 1.5rem;\n" +
               "    margin-bottom: 2rem;\n" +
               "    opacity: 0.9;\n" +
               "}\n" +
               "\n" +
               ".cta-button {\n" +
               "    background: linear-gradient(45deg, #ff6b6b, #ffa36b);\n" +
               "    color: white;\n" +
               "    padding: 15px 40px;\n" +
               "    border: none;\n" +
               "    border-radius: 50px;\n" +
               "    font-size: 1.1rem;\n" +
               "    font-weight: 600;\n" +
               "    cursor: pointer;\n" +
               "    transition: all 0.3s ease;\n" +
               "    box-shadow: 0 10px 30px rgba(255,107,107,0.3);\n" +
               "}\n" +
               "\n" +
               ".cta-button:hover {\n" +
               "    transform: translateY(-3px) scale(1.05);\n" +
               "    box-shadow: 0 15px 40px rgba(255,107,107,0.5);\n" +
               "}\n" +
               "\n" +
               ".output {\n" +
               "    margin-top: 2rem;\n" +
               "    padding: 1rem;\n" +
               "    background: rgba(255,255,255,0.1);\n" +
               "    border-radius: 10px;\n" +
               "    min-height: 50px;\n" +
               "}";
    }
    
    private String getDefaultJavaScript() {
        return "// JavaScript Interactive Examples\n" +
               "console.log('WebCoderFX JavaScript loaded!');\n" +
               "\n" +
               "// Handle button click\n" +
               "function handleClick() {\n" +
               "    const output = document.getElementById('output');\n" +
               "    const messages = [\n" +
               "        'Hello from JavaScript!',\n" +
               "        'You clicked the button!',\n" +
               "        'JavaScript is working!',\n" +
               "        'WebCoderFX rocks!',\n" +
               "        'Try editing this code!'\n" +
               "    ];\n" +
               "    \n" +
               "    const randomMessage = messages[Math.floor(Math.random() * messages.length)];\n" +
               "    output.innerHTML = `<p style=\"color: #ffeb3b; font-weight: bold;\">${randomMessage}</p>`;\n" +
               "    \n" +
               "    console.log('Button clicked: ' + randomMessage);\n" +
               "}\n" +
               "\n" +
               "// Add some interactive features\n" +
               "document.addEventListener('DOMContentLoaded', function() {\n" +
               "    console.log('DOM fully loaded and parsed');\n" +
               "    \n" +
               "    // Change title color on mouse move\n" +
               "    document.addEventListener('mousemove', function(e) {\n" +
               "        const title = document.querySelector('.title');\n" +
               "        const x = e.clientX / window.innerWidth;\n" +
               "        const y = e.clientY / window.innerHeight;\n" +
               "        title.style.textShadow = `${x * 10}px ${y * 10}px 20px rgba(255,255,255,0.5)`;\n" +
               "    });\n" +
               "});\n" +
               "\n" +
               "// Utility function to demonstrate JavaScript capabilities\n" +
               "function showTime() {\n" +
               "    const now = new Date();\n" +
               "    return now.toLocaleTimeString();\n" +
               "}\n" +
               "\n" +
               "// Test the function\n" +
               "console.log('Current time:', showTime());";
    }
}