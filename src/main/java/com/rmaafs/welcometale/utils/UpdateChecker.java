package com.rmaafs.welcometale.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    public final String REPO_URL = "https://github.com/rmaafs/WelcomeTale";
    private final String API_URL = REPO_URL.replace("github.com", "api.github.com/repos") + "/releases/latest";

    private final String currentVersion;
    private String latestVersion;
    private boolean usingLatest;

    public UpdateChecker(String currentVersion) {
        this.currentVersion = currentVersion;
        this.latestVersion = currentVersion;
        this.usingLatest = true;
        fetchLatestVersion();
    }

    private void fetchLatestVersion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Matcher matcher = Pattern.compile("\"tag_name\"\\s*:\\s*\"v?([^\"]+)\"").matcher(response.body());
                if (matcher.find()) {
                    this.latestVersion = matcher.group(1);
                    this.usingLatest = !isNewer(currentVersion, latestVersion);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUsingLatest() {
        return usingLatest;
    }

    private boolean isNewer(String current, String latest) {
        try {
            int[] c = parseVersion(current);
            int[] l = parseVersion(latest);
            for (int i = 0; i < 3; i++) {
                if (l[i] != c[i])
                    return l[i] > c[i];
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private int[] parseVersion(String version) {
        Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(version);
        if (m.find())
            return new int[] { Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3)) };
        throw new IllegalArgumentException("Invalid version: " + version);
    }
}
