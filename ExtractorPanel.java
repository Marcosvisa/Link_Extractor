import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtractorPanel extends JFrame {

    private JTextField urlField;
    private JRadioButton audioRadio;
    private JRadioButton videoRadio;
    private JTextField folderPathField;
    private JComboBox<String> qualityComboBox;
    private JButton downloadButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color ORANGE = new Color(255, 140, 0);
    private final Color TEXT_LIGHT = new Color(230, 230, 230);
    private final Color GREEN = new Color(0, 230, 0);

    public ExtractorPanel() {

        setTitle("Link Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 380);
        setLocationRelativeTo(null);

        Image iconImage = null;
        
        java.net.URL iconURL = ExtractorPanel.class.getResource("/imagens/logo.png");
        if (iconURL != null) {
            iconImage = new ImageIcon(iconURL).getImage();
        } else {
            File iconFile = new File("app/imagens/logo.png");
            if (iconFile.exists()) {
                iconImage = new ImageIcon(iconFile.getAbsolutePath()).getImage();
            }
        }
        
        if (iconImage != null) {
            setIconImage(iconImage);
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BG_DARK);

        JLabel logoLabel = new JLabel();
        java.net.URL logoURL = ExtractorPanel.class.getResource("/imagens/logo.png");

        if (logoURL != null) {
            Image img = new ImageIcon(logoURL).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } else {
            File logoFile = new File("app/imagens/logo.png");
            if (logoFile.exists()) {
                Image img = new ImageIcon(logoFile.getAbsolutePath()).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            }
        }

        JLabel titleLabel = new JLabel("Link Extractor");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(ORANGE);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setBackground(BG_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createLabel("URL do YouTube:"));
        urlField = createTextField();
        content.add(urlField);

        content.add(Box.createVerticalStrut(10));
        content.add(createLabel("Tipo de Extração:"));

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typePanel.setBackground(BG_DARK);
        typePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        audioRadio = createRadio("Áudio (MP3)", true);
        videoRadio = createRadio("Vídeo", false);

        ButtonGroup group = new ButtonGroup();
        group.add(audioRadio);
        group.add(videoRadio);

        typePanel.add(audioRadio);
        typePanel.add(Box.createHorizontalStrut(15));
        typePanel.add(videoRadio);
        content.add(typePanel);

        qualityComboBox = new JComboBox<>(new String[]{
                "Melhor qualidade", "1080p", "720p", "480p", "360p"
        });
        qualityComboBox.setVisible(false);
        qualityComboBox.setMaximumSize(new Dimension(600, 30));
        qualityComboBox.setBackground(new Color(30, 30, 30));
        qualityComboBox.setForeground(Color.WHITE);
        content.add(qualityComboBox);

        content.add(Box.createVerticalStrut(10));
        content.add(createLabel("Pasta de Destino:"));

        JPanel folderPanel = new JPanel(new BorderLayout(3, 0));
        folderPanel.setBackground(BG_DARK);
        folderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        folderPathField = createTextField();
        folderPathField.setText(System.getProperty("user.home") + "\\Downloads");

        JButton browse = new JButton("Procurar");
        browse.setBackground(ORANGE);
        browse.setFocusPainted(false);
        browse.setMargin(new Insets(2, 8, 2, 8));
        browse.addActionListener(e -> selectFolder());

        folderPanel.add(folderPathField, BorderLayout.CENTER);
        folderPanel.add(browse, BorderLayout.EAST);
        content.add(folderPanel);

        content.add(Box.createVerticalStrut(20));

        downloadButton = new JButton("INICIAR DOWNLOAD");
        downloadButton.setBackground(GREEN);
        downloadButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        downloadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        downloadButton.addActionListener(e -> startDownload());
        content.add(downloadButton);

        content.add(Box.createVerticalStrut(20));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        content.add(progressBar);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(TEXT_LIGHT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(statusLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);

        audioRadio.addActionListener(e -> qualityComboBox.setVisible(false));
        videoRadio.addActionListener(e -> qualityComboBox.setVisible(true));
    }

    private void startDownload() {
        String url = urlField.getText().trim();
        String folder = folderPathField.getText().trim();

        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite uma URL do YouTube!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }

        progressBar.setVisible(true);
        statusLabel.setText("Iniciando...");
        downloadButton.setEnabled(false);

        new Thread(() -> {
            try {
                // PROCURA O YT-DLP EM VÁRIOS LUGARES
                String ytDlpPath = findYtDlp();
                
                if (ytDlpPath == null) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("yt-dlp não encontrado!");
                        JOptionPane.showMessageDialog(ExtractorPanel.this,
                            "⚠️ yt-dlp.exe não encontrado!\n\n" +
                            "O programa procurou em:\n" +
                            "1. Pasta app/\n" +
                            "2. Pasta atual\n" +
                            "3. Pasta do JAR\n\n" +
                            "Verifique se o arquivo está na pasta 'app'.",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                    return;
                }
                
                System.out.println("yt-dlp encontrado em: " + ytDlpPath);
                boolean success = executeYtDlp(url, folder, ytDlpPath);
                
                if (success) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Download concluído! ✓");
                        statusLabel.setForeground(GREEN);
                        JOptionPane.showMessageDialog(ExtractorPanel.this,
                            "✅ DOWNLOAD CONCLUÍDO!\n\nArquivo salvo em:\n" + folder,
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Falha no download ❌");
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(ExtractorPanel.this,
                            "❌ FALHA NO DOWNLOAD!\n\nVerifique:\n1. A URL está correta\n2. Conexão com a internet",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    });
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Erro: " + ex.getMessage());
                    JOptionPane.showMessageDialog(ExtractorPanel.this,
                        "⚠️ ERRO:\n" + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    downloadButton.setEnabled(true);
                });
            }
        }).start();
    }

    // MÉTODO PARA ENCONTRAR O YT-DLP EM VÁRIOS LUGARES
    private String findYtDlp() {
        // 1. Procurar na pasta "app" (dentro do diretório atual)
        File appFile = new File("app" + File.separator + "yt-dlp.exe");
        if (appFile.exists()) {
            return appFile.getAbsolutePath();
        }
        
        // 2. Procurar no diretório atual
        File currentFile = new File("yt-dlp.exe");
        if (currentFile.exists()) {
            return currentFile.getAbsolutePath();
        }
        
        // 3. Procurar no diretório onde o JAR está rodando
        String jarPath = ExtractorPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            File jarDir = new File(new File(jarPath).getParent());
            File jarAppFile = new File(jarDir, "app" + File.separator + "yt-dlp.exe");
            if (jarAppFile.exists()) {
                return jarAppFile.getAbsolutePath();
            }
            
            // 4. Procurar no diretório pai do JAR
            File jarParentFile = new File(jarDir, "yt-dlp.exe");
            if (jarParentFile.exists()) {
                return jarParentFile.getAbsolutePath();
            }
        } catch (Exception e) {
            // Ignora erro
        }
        
        return null;
    }

    private boolean executeYtDlp(String url, String folder, String ytDlpPath) {
        try {
            List<String> command = new ArrayList<>();
            command.add(ytDlpPath);
            
            // Opções básicas
            command.add("--no-warnings");
            command.add("--no-playlist");
            
            if (audioRadio.isSelected()) {
                command.add("-x");
                command.add("--audio-format");
                command.add("mp3");
                command.add("--audio-quality");
                command.add("0");
            } else {
                String q = qualityComboBox.getSelectedItem().toString();
                if (!q.equals("Melhor qualidade")) {
                    String res = q.replace("p", "");
                    command.add("-f");
                    command.add("bestvideo[height<=" + res + "][ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]");
                } else {
                    command.add("-f");
                    command.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]");
                }
            }
            
            // Diretório de saída
            command.add("-P");
            command.add(folder);
            
            // Nome do arquivo
            if (audioRadio.isSelected()) {
                command.add("-o");
                command.add("%(title)s.%(ext)s");
            } else {
                command.add("-o");
                command.add("%(title)s [%(resolution)s].%(ext)s");
            }
            
            // URL
            command.add(url);
            
            // Executa o processo
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            
            // Define o diretório de trabalho como a pasta onde está o yt-dlp
            File ytFile = new File(ytDlpPath);
            pb.directory(ytFile.getParentFile());
            
            Process process = pb.start();
            
            // Lê a saída
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("[download]") || line.contains("[ExtractAudio]") || 
                    line.contains("Destination") || line.contains("Merging")) {
                    final String statusLine = line.trim();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText(statusLine);
                    });
                }
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(600, 30));
        tf.setBackground(new Color(30, 30, 30));
        tf.setForeground(Color.WHITE);
        tf.setBorder(BorderFactory.createLineBorder(ORANGE));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JRadioButton createRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setBackground(BG_DARK);
        rb.setForeground(TEXT_LIGHT);
        return rb;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            ExtractorPanel panel = new ExtractorPanel();
            panel.setVisible(true);
        });
    }
}