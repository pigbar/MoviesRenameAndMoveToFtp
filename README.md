# MoviesRenameAndMoveToFtp
Project for renaming and moving "movies" folders to a FTP destination.
Small project for testing file uploading to a remote FTP target.
Renames source "movie" folders and files, in order to make it easier for Plex Media Server detecting de files on its own library.
For example, if we have a folder named:
best movie ever.1998 (ytx)

With a file inside named:
best movie ever.1998 (ytx).director.edition.mp4

It will be formatted as:
-- best.movie.ever.1998.(ytx).director.edition
------ best.movie.ever.1998.(ytx).director.edition.mp4

And will be uploaded to a FTP folder.