package com.risServer.dataAccess;

import com.risServer.dataModels.Capture;
import com.risServer.dataModels.Image;
import com.risServer.dataModels.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class RisDataAccessService implements RisDao {

    private final JdbcTemplate jdbcTemplate;
    public RisDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Capture> selectAllCaptures() {
        String sql = "SELECT * FROM captures";
        return jdbcTemplate.query(sql, new CaptureRowMapper());
    }



    @Override
    @Transactional
    public int insertCapture(Capture capture) {

        String sqlCapture = "INSERT INTO captures(epoch_time, species, id_status, notes, moon_phase, temperature, humidity, longitude, latitude, geolocation) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,  ST_GeomFromText('POINT("+ capture.getLongitude() +" "+ capture.getLatitude() +")')  );";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int responseCodeCapture = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlCapture, new String[] {"id"});
            ps.setString(1,capture.getTime());
            ps.setString(2,capture.getSpecies());
            ps.setString(3,capture.getIdStatus());
            ps.setString(4,capture.getNotes());
            ps.setString(5,capture.getMoonPhase());
            ps.setFloat(6, capture.getTemperature());
            ps.setFloat(7, capture.getHumidity());
            ps.setDouble(8, capture.getLongitude());
            ps.setDouble(9, capture.getLatitude());
            return ps;
        }, keyHolder);

        if ( capture.getImages() == null ) return responseCodeCapture;
        else {

                String sqlImage =   "INSERT INTO images (capture_id, fileURL) " +
                                    "VALUES (?,?);";

                int imagesInsertedCount = 0;

                for (Image image : capture.getImages()) {
                    imagesInsertedCount += jdbcTemplate.update(sqlImage, keyHolder.getKey(), image.getFileURL());
                }

            if ((responseCodeCapture == 1) && (imagesInsertedCount == capture.getImages().size())) return 1;
            else return 0;

        }
        // int responseCodeCapture = jdbcTemplate.update(sql, capture.getTime(), capture.getSpecies(), capture.getIdStatus(), capture.getNotes(), capture.getMoonPhase(), capture.getLongitude(), capture.getLatitude() );

    }



    @Override
    public int deleteCapture(int id) {

        String sql =    "DELETE FROM captures " +
                        "WHERE id = ?;";

        return jdbcTemplate.update(sql, id);
    }



    @Override
    public Optional<Capture> selectCaptureByFields(Capture capture) {

        //TODO -better to compare the images ?

        String sql =    "SELECT * " +
                        "FROM captures " +
                        "WHERE epoch_time = ? AND longitude = ? AND latitude = ?";

        return jdbcTemplate.query(sql, new CaptureRowMapper(), capture.getTime(), capture.getLongitude(), capture.getLatitude()).stream().findFirst();

    }



    @Override
    public Optional<Capture> selectCaptureById(int id) {

        String sqlCaptures =    "SELECT * " +
                                "FROM captures " +
                                "WHERE id = ?;";

        //returns a list
        //List<Capture> captures = jdbcTemplate.query(sql, new CaptureRowMapper(), id).stream().findFirst();
        return jdbcTemplate.query(sqlCaptures, new CaptureRowMapper(), id).stream().findFirst();


    }



    @Override
    public List<Image> selectImagesByCaptureId( int captureId ) {

        String sqlImages =      "SELECT * " +
                                "FROM images " +
                                "WHERE capture_id = ?;";

        return jdbcTemplate.query(sqlImages, new ImageRowMapper(), captureId);

    }



    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int updateCapture(Capture capture) {

        String sql =    "UPDATE Captures " +
                        "SET species = ?, " +
                        "id_status = ?, " +
                        "notes = ? " +
                        "WHERE id = ?";

        int numberOfRowsAffected = jdbcTemplate.update(sql, capture.getSpecies(), capture.getIdStatus(), capture.getNotes(), capture.getId());
        return numberOfRowsAffected;

    }


    // ---------------------------- User and Roles ------------------------------------ //

    @Override
    public Optional<User> findByUserName(String username) {

        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_name = ? " +
                "LIMIT 1;";

        return jdbcTemplate.query(sql, new UserRowMapper(), username ).stream().findFirst();


    }

    @Override
    public List<User> getUsers() {

        String sql = "SELECT * " +
                    "FROM users;";

        return jdbcTemplate.query(sql, new UserRowMapper());

    }


    @Override
    public int insertUser(User user) {

        String sql = "INSERT INTO users( name, username, password, role) VALUES (?, ?, ?, ?);";

        return jdbcTemplate.update(sql, user.getName(), user.getUsername(), user.getPassword(), user.getRole());


    }



}























