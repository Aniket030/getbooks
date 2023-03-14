import java.sql.*;
import java.util.*;

public class BookBot {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/books";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter a query: ");
            String query = scanner.nextLine();
            String response = handleQuery(query);
            System.out.println("Response: " + response);
        }
    }

    private static String handleQuery(String query) {
        if (query.startsWith("book info ")) {
            return getBookInfo(query.substring(10));
        } else if (query.startsWith("books by ")) {
            return getBooksByAuthor(query.substring(9));
        } else {
            return "Invalid query.";
        }
    }

    private static String getBookInfo(String title) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM books WHERE title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String author = resultSet.getString("author");
                String publicationDate = resultSet.getString("publication_date");
                String description = resultSet.getString("description");
                return title + " by " + author + ", published on " + publicationDate + "\n" + description;
            } else {
                return "Book not found.";
            }
        } catch (SQLException e) {
            return "Error retrieving book information.";
        }
    }

    private static String getBooksByAuthor(String author) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT title FROM books WHERE author = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, author);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Books by " + author + ":\n");
                do {
                    sb.append("- " + resultSet.getString("title") + "\n");
                } while (resultSet.next());
                return sb.toString();
            } else {
                return "Author not found.";
            }
        } catch (SQLException e) {
            return "Error retrieving books by author.";
        }
    }
}