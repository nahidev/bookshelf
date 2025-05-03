package com.nahidev.bookshelf.service.exporter;

public class ExporterFactory {
    public static Exporter getExporter(String format) {
        return switch (format.toLowerCase()) {
            case "json" -> new JsonExporter();
            case "csv" -> new CsvExporter();
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }
}