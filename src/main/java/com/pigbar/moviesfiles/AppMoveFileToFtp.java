package com.pigbar.moviesfiles;

public class AppMoveFileToFtp {
    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        fileHandler.processDownloadedMovies(true);
    }
}
