package com.example.SocialNetwork.Domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    // private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Friendship(String date, String id1, String id2){
        Long idl1 = Long.valueOf(id1);
        Long idl2 = Long.valueOf(id2);

        this.setId(new Tuple<>(idl1,idl2));
        this.date = LocalDateTime.parse(date);
    };
    public Friendship(Tuple<Long, Long> tuple) {
        this.date = LocalDateTime.now();
        this.setId(tuple);
    }

    public Friendship(Tuple<Long, Long> tuple, Timestamp time) {
        this.date = time.toLocalDateTime();
        this.setId(tuple);
    }

    @Override
    public String toString(){
        return "IdUser1: " + id.getLeft() + "\n" +
                "IdUser2: " + id.getRight() + "\n" +
                date.toString() + "\n";
    }

    /**
     * seddew
     *
     * @return the date when the friendship was created
     */
    public Timestamp getDate() {
        return Timestamp.valueOf(date);
    }
}
