package application;

import javafx.scene.paint.Color;

public class ThemeManager {
    public enum Theme {
        DARK("Dark Theme"),
        LIGHT("Light Theme"), 
        BLUE("Blue Theme"),
        GREEN("Green Theme"),
        PURPLE("Purple Theme");
        
        private final String displayName;
        
        Theme(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    private static Theme currentTheme = Theme.DARK;
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }
    
    public static String getThemeCSS() {
        switch (currentTheme) {
            case DARK:
                return getDarkTheme();
            case LIGHT:
                return getLightTheme();
            case BLUE:
                return getBlueTheme();
            case GREEN:
                return getGreenTheme();
            case PURPLE:
                return getPurpleTheme();
            default:
                return getDarkTheme();
        }
    }
    
    private static String getDarkTheme() {
        return """
            .root {
                -fx-background-color: #2b2b2b;
                -fx-text-fill: #ffffff;
            }
            .menu-bar {
                -fx-background-color: #1e1e1e;
            }
            .menu-bar .label {
                -fx-text-fill: #ffffff;
            }
            .menu-item {
                -fx-background-color: #2b2b2b;
            }
            .menu-item .label {
                -fx-text-fill: #ffffff;
            }
            .text-area {
                -fx-background-color: #1e1e1e;
                -fx-text-fill: #ffffff;
                -fx-border-color: #404040;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-font-family: 'Consolas', 'Monaco', 'Monospace';
                -fx-font-size: 14px;
            }
            .text-area .content {
                -fx-background-color: #1e1e1e;
            }
            .button {
                -fx-background-color: #404040;
                -fx-text-fill: #ffffff;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-font-weight: bold;
            }
            .button:hover {
                -fx-background-color: #505050;
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
            }
            .tool-bar {
                -fx-background-color: #1e1e1e;
                -fx-border-color: #404040;
                -fx-border-width: 0 0 1 0;
            }
            .split-pane {
                -fx-background-color: transparent;
            }
            .split-pane-divider {
                -fx-background-color: #404040;
            }
            .label {
                -fx-text-fill: #ffffff;
            }
            .tab-pane {
                -fx-background-color: #2b2b2b;
            }
            .tab {
                -fx-background-color: #404040;
                -fx-background-radius: 5 5 0 0;
            }
            .tab:selected {
                -fx-background-color: #6200ee;
            }
            .tab .tab-label {
                -fx-text-fill: #ffffff;
            }
            .context-menu {
                -fx-background-color: #2b2b2b;
                -fx-border-color: #404040;
            }
            """;
    }
    
    private static String getLightTheme() {
        return """
            .root {
                -fx-background-color: #f5f5f5;
                -fx-text-fill: #333333;
            }
            .menu-bar {
                -fx-background-color: #ffffff;
                -fx-border-color: #e0e0e0;
                -fx-border-width: 0 0 1 0;
            }
            .menu-bar .label {
                -fx-text-fill: #333333;
            }
            .text-area {
                -fx-background-color: #ffffff;
                -fx-text-fill: #333333;
                -fx-border-color: #e0e0e0;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-font-family: 'Consolas', 'Monaco', 'Monospace';
                -fx-font-size: 14px;
            }
            .text-area .content {
                -fx-background-color: #ffffff;
            }
            .button {
                -fx-background-color: #6200ee;
                -fx-text-fill: #ffffff;
                -fx-border-radius: 4;
                -fx-background-radius: 4;
                -fx-font-weight: bold;
            }
            .button:hover {
                -fx-background-color: #7c4dff;
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
            }
            .tool-bar {
                -fx-background-color: #ffffff;
                -fx-border-color: #e0e0e0;
                -fx-border-width: 0 0 1 0;
            }
            .split-pane-divider {
                -fx-background-color: #e0e0e0;
            }
            .label {
                -fx-text-fill: #333333;
            }
            .tab {
                -fx-background-color: #f0f0f0;
            }
            .tab:selected {
                -fx-background-color: #6200ee;
            }
            .tab .tab-label {
                -fx-text-fill: #333333;
            }
            .tab:selected .tab-label {
                -fx-text-fill: #ffffff;
            }
            """;
    }
    
    private static String getBlueTheme() {
        return getDarkTheme().replace("#6200ee", "#2196f3");
    }
    
    private static String getGreenTheme() {
        return getDarkTheme().replace("#6200ee", "#4caf50");
    }
    
    private static String getPurpleTheme() {
        return getDarkTheme().replace("#6200ee", "#9c27b0");
    }
}