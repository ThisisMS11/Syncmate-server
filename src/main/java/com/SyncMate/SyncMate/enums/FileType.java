package com.SyncMate.SyncMate.enums;

public enum FileType {
    IMAGE, VIDEO, DOCUMENT, UNKNOWN;

    public static FileType detectFromContentType(String contentType) {
        if (contentType == null) return UNKNOWN;
        if (contentType.startsWith("image/")) return IMAGE;
        if (contentType.startsWith("video/")) return VIDEO;
        if (contentType.contains("pdf") || contentType.contains("msword") || contentType.contains("officedocument"))
            return DOCUMENT;
        return UNKNOWN;
    }
}
