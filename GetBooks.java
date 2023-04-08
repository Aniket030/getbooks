import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class GetBooks {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the title of the book: ");
        String bookTitle = scanner.nextLine();
        String bookInfo = getBookInfo(bookTitle);
        System.out.println(bookInfo);
        Files.write(Paths.get("book_info.json"), bookInfo.getBytes());
    }

    public static String getBookInfo(String bookTitle) throws IOException {
        // Replace spaces in the book title with + sign for the URL
        bookTitle = bookTitle.replaceAll(" ", "+");
        URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + bookTitle);
        Scanner scanner = new Scanner(url.openStream());
        StringBuilder bookInfo = new StringBuilder();
        while (scanner.hasNextLine()) {
            bookInfo.append(scanner.nextLine());
        }
        scanner.close();
        // Extract relevant information from the API response
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(bookInfo.toString()).getAsJsonObject();
        JsonArray items = root.getAsJsonArray("items");
        if (items.size() == 0) {
            return "Sorry, no information found for the book '" + bookTitle + "'";
        } else {
            JsonObject volumeInfo = items.get(0).getAsJsonObject().getAsJsonObject("volumeInfo");
            JsonElement title = volumeInfo.get("title");
            JsonElement author = volumeInfo.get("authors");
            JsonElement publisher = volumeInfo.get("publisher");
            JsonElement publishedDate = volumeInfo.get("publishedDate");
            JsonElement description = volumeInfo.get("description");
            String titleStr = title != null ? title.getAsString() : "Title not found";
            String authorStr = author != null ? author.getAsJsonArray().get(0).getAsString() : "Author not found";
            String publisherStr = publisher != null ? publisher.getAsString() : "Publisher not found";
            String publishedYearStr = publishedDate != null ? publishedDate.getAsString().startsWith("0000") ? "Published Year not found" : publishedDate.getAsString().substring(0, 4) : "Published Year not found";
            String descriptionStr = description != null ? description.getAsString() : "Description not found";
            bookInfo = new StringBuilder("Title: " + titleStr + "\n"
                    + "Author: " + authorStr + "\n"
                    + "Publisher: " + publisherStr + "\n"
                    + "Published Year: " + publishedYearStr + "\n"
                    + "Description: " + descriptionStr);
        }
        return bookInfo.toString();
    }
}
