
//6
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleImageDownloaderApp extends JFrame {
    // private JTextField urlField = new JTextField(
    // "https://i.pinimg.com/736x/87/7e/a3/877ea3b78ad7c88e0718646657edfe72.jpg");
    // // if image url want to be fix
    private JTextField urlField = new JTextField(30); // if want to give the own url
    private JButton addButton = new JButton("Add Download");
    private DefaultListModel<DownloadModel> listModel = new DefaultListModel<>();
    private JList<DownloadModel> downloadList = new JList<>(listModel);
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(10); // 10 concurrent downloads

    public SimpleImageDownloaderApp() {
        super("Image Streamline Download");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        layoutComponents();
        setVisible(true);
    }

    private void layoutComponents() {
        // // Create a JLabel for the title
        // JLabel titleLabel = new JLabel("Image Streamline Download");
        // titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Set font and size
        // titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text

        // // Add the title label to the content pane
        // getContentPane().add(titleLabel, BorderLayout.NORTH);

        JPanel addPanel = new JPanel();
        addPanel.add(urlField);
        addPanel.add(addButton);
        JScrollPane scrollPane = new JScrollPane(downloadList);
        downloadList.setCellRenderer(new DownloadListCellRenderer());

        addButton.addActionListener(e -> addDownload(urlField.getText().trim()));

        setLayout(new BorderLayout());
        add(addPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton pauseButton = new JButton("Pause"); // Pause the downloading url
        JButton resumeButton = new JButton("Resume"); // Resume the downloading url
        JButton cancelButton = new JButton("Cancel"); // Cancel the downloading url
        JButton openFolderButton = new JButton("View Images"); // New button to open the download folder

        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(openFolderButton); // Add the open folder button to the button panel

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadModel selectedModel = downloadList.getSelectedValue();
                if (selectedModel != null) {
                    selectedModel.pause();
                }
            }
        });

        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadModel selectedModel = downloadList.getSelectedValue();
                if (selectedModel != null) {
                    selectedModel.resume();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadModel selectedModel = downloadList.getSelectedValue();
                if (selectedModel != null) {
                    selectedModel.cancel();
                    listModel.removeElement(selectedModel);
                }
            }
        });

        openFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File("downloads")); // Open the "downloads" folder
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @SuppressWarnings("deprecation")
    private void addDownload(String url) {
        try {
            new URL(url); // Validates URL format
            DownloadModel model = new DownloadModel(url);
            listModel.addElement(model);
            DownloadTask task = new DownloadTask(model, () -> SwingUtilities.invokeLater(this::repaint));
            model.setFuture(downloadExecutor.submit(task));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "Can you please check the URL: " + url, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleImageDownloaderApp::new);
    }
}

class DownloadModel {
    private final String url;
    private volatile String status = "Waiting...";
    private volatile long totalBytes = 0L;
    private volatile long downloadedBytes = 0L;
    private Future<?> future;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    public DownloadModel(String url) {
        this.url = url;
    }

    // Standard getters and setters
    public String getUrl() {
        return url;
    }

    public synchronized boolean isPaused() {
        return paused.get();
    }

    public synchronized void pause() {
        paused.set(true);
    }

    public synchronized void resume() {
        paused.set(false);
        notifyAll();
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void cancel() {
        if (future != null)
            future.cancel(true);
    }

    public synchronized void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public synchronized void addDownloadedBytes(long bytes) {
        this.downloadedBytes += bytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}

class DownloadTask implements Callable<Void> {
    private final DownloadModel model;
    private final Runnable updateUI;

    public DownloadTask(DownloadModel model, Runnable updateUI) {
        this.model = model;
        this.updateUI = updateUI;
    }

    @Override
    public Void call() throws Exception {
        model.setStatus("Downloading");
        @SuppressWarnings("deprecation")
        URL url = new URL(model.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = connection.getContentLengthLong();
        model.setTotalBytes(fileSize);

        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            Path targetPath = Paths.get("downloads", new File(url.getPath()).getName());
            Files.createDirectories(targetPath.getParent());
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (model) {
                        while (model.isPaused())
                            model.wait();
                    }
                    out.write(buffer, 0, bytesRead);
                    model.addDownloadedBytes(bytesRead);
                    updateUI.run();

                    Thread.sleep(200); // Adjust delay as needed
                }
                model.setStatus("Completed");
            }
        } catch (IOException | InterruptedException e) {
            model.setStatus("Error: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            updateUI.run();
        }
        return null;
    }
}

class DownloadListCellRenderer extends JPanel implements ListCellRenderer<DownloadModel> {
    @Override
    public Component getListCellRendererComponent(JList<? extends DownloadModel> list, DownloadModel value, int index,
            boolean isSelected, boolean cellHasFocus) {
        this.removeAll(); // Clear previous components
        setLayout(new BorderLayout());
        JLabel urlLabel = new JLabel(value.getUrl());
        JProgressBar progressBar = new JProgressBar(0, 100);
        if (value.getTotalBytes() > 0) {
            int progress = (int) ((value.getDownloadedBytes() * 100) / value.getTotalBytes());
            progressBar.setValue(progress);
        }
        JLabel statusLabel = new JLabel(value.getStatus());
        add(urlLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
