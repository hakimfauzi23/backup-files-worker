package com.hakimfauzi23.backupworker.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FilePathDTO {

    private String filePath;

    public FilePathDTO() {
    }

    public FilePathDTO(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @JsonIgnore
    public String getFileDirectoryOnly() {
        String[] arrOfFilepath = filePath.split("/");
        StringBuilder fileDir = new StringBuilder("/");
        for (int i = 0; i < arrOfFilepath.length - 1; i++) {
            fileDir.append(arrOfFilepath[i]);
            if (i < arrOfFilepath.length - 2) {
                fileDir.append("/");
            }
        }
        return String.valueOf(fileDir);
    }

    @Override
    public String toString() {
        return "FilePathDTO{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
