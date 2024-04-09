package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.PlayDate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcPlayDateDao implements PlayDateDao{
    private final JdbcTemplate jdbc;
    public JdbcPlayDateDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public PlayDate getPlayDateById(int playDateId) {
        PlayDate playDate = null;
        String sql = "SELECT play_date_id, title, description, host_id, date_time, location_setting, ispublic\n" +
                "\tFROM play_dates\n" +
                "\tWHERE play_date_id = ?;";
        try {
            SqlRowSet results = jdbc.queryForRowSet(sql, playDateId);
            if (results.next()) {
                playDate = mapRowToPlayDate(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server.", e);
        }
        return playDate;
    }

    @Override
    public PlayDate createPlayDate(PlayDate newPlayDate) {
        PlayDate playDate = null;
        String sql = "INSERT INTO play_dates(\n" +
                "\ttitle, description, host_id, date_time, location_setting, ispublic)\n" +
                "\tVALUES (?, ?, ?, ?, ?, ?) RETURNING play_date_id;";
        try {
            int playDateId = jdbc.queryForObject(sql, int.class,
                    newPlayDate.getTitle(),
                    newPlayDate.getDescription(),
                    newPlayDate.getHostId(),
                    newPlayDate.getDateTime(),
                    newPlayDate.getLocation(),
                    newPlayDate.isPublicDate());
            playDate = getPlayDateById(playDateId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation.", e);
        }
        return playDate;
    }

    // write tests for this once we have the pet model information - COME BACK TO THIS!!!!!!!!!
    @Override
    public void insertPetPlayDate(int petId, int playDateId) {
        String sql = "INSERT INTO pet_play_dates(play_date_id, pet_id)\n" +
                "VALUES (?, ?);";
        try {
            jdbc.update(sql, playDateId, petId);
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }
    }

    private PlayDate mapRowToPlayDate(SqlRowSet results) {
        PlayDate playDate = new PlayDate();
        playDate.setPlayDateId(results.getInt("play_date_id"));
        playDate.setDescription(results.getString("description"));
        playDate.setHostId(results.getInt("host_id"));
        playDate.setLocation(results.getString("location_setting"));
        playDate.setTitle(results.getString("title"));
        playDate.setPublicDate(results.getBoolean("ispublic"));
        playDate.setDateTime(results.getTimestamp("date_time").toLocalDateTime());

        return playDate;
    }
}