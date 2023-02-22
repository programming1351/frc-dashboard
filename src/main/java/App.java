import app.DashboardManager;

import javax.swing.*;
import java.io.IOException;

public class App {
    public static void main(String... args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            try {
                new DashboardManager();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
