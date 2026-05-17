package com.pigbar.moviesfiles;

//esta es la que hay que correr
public class AppMoveFileToFtp {
    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        fileHandler.processDownloadedMovies(true);
    }
}
