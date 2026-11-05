package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "postgres";
    public static final String SELECT = "SELECT player, game, rating, ratedOn FROM rating WHERE game = ? ORDER BY rating DESC LIMIT 10";
    public static final String DELETE = "DELETE FROM rating";
    public static final String INSERT = "INSERT INTO rating (player, game, rating, ratedOn) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE rating SET rating = ?, ratedOn = ? WHERE player = ? AND game = ?";

    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD)) {

            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM rating WHERE player = ? AND game = ?")) {

                check.setString(1, rating.getPlayer());
                check.setString(2, rating.getGame());

                var rs = check.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement ps = connection.prepareStatement(UPDATE)) {
                        ps.setInt(1, rating.getRating());
                        ps.setTimestamp(2, new java.sql.Timestamp(rating.getRatedOn().getTime()));
                        ps.setString(3, rating.getPlayer());
                        ps.setString(4, rating.getGame());
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement(INSERT)) {
                        ps.setString(1, rating.getPlayer());
                        ps.setString(2, rating.getGame());
                        ps.setInt(3, rating.getRating());
                        ps.setTimestamp(4, new java.sql.Timestamp(rating.getRatedOn().getTime()));
                        ps.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try (Connection connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT AVG(rating) FROM rating WHERE game = ?")) {

            ps.setString(1, game);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (Exception e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT rating FROM rating WHERE game = ? AND player = ?")) {

            ps.setString(1, game);
            ps.setString(2, player);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("rating");
            }

            return 0;

        } catch (Exception e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = connection.prepareStatement(DELETE)) {

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RatingException("Problem resetting rating", e);
        }
    }
}
