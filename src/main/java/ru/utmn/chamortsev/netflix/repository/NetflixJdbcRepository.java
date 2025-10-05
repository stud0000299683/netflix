package ru.utmn.chamortsev.netflix.repository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSourceExtensionsKt;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.utmn.chamortsev.netflix.model.Netflix;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

@Repository
public class NetflixJdbcRepository implements CommonRepository<Netflix> {

    private static final String SQL_INSERT = """
            insert into netflix
            (show_id, type, title, directors, cast_members, country, date_added,
             release_year, rating, duration, listed_in, description)
            values
            (:show_id, :type, :title, :directors, :cast, :country, :date_added,
             :release_year, :rating, :duration, :listed_in, :description)""";

    private static final String SQL_UPDATE = """
            update netflix set type=:type, title=:title, 
            directors=:directors, cast_members=:cast, country=:country, date_added=:date_added,
             release_year=:release_year, rating=:rating, duration=:duration, 
             listed_in=:listed_in, description=:description
            where show_id=:show_id""";
    private static final String SQL_EXIST = "select count(*) from netflix where show_id=:show_id";
    private static final String SQL_DELETE = "delete from netflix where show_id=:show_id";
    private static final String SQL_FIND_ALL = "select * from netflix";
    private static final String SQL_FIND_ONE = SQL_FIND_ALL +" where show_id=:show_id";

    private static final String SQL_COUNT = "select count(*) from netflix";

    private final NamedParameterJdbcTemplate template;

    public NetflixJdbcRepository(NamedParameterJdbcTemplate template){
        this.template = template;
    }

    @Override
    public Netflix save(Netflix domain) {
        if (exsists(domain.getShow_id()))
            return insertOrUpdate(SQL_UPDATE, domain);
        return insertOrUpdate(SQL_INSERT, domain);
    }

    private Netflix insertOrUpdate(final String sql, Netflix domain){
        Map<String, Object> namedParameter = new HashMap<>();
        namedParameter.put("show_id", domain.getShow_id());
        namedParameter.put("type", domain.getType());
        namedParameter.put("title", domain.getTitle());
        namedParameter.put("directors", domain.getDirectors());
        namedParameter.put("cast", domain.getCast());
        namedParameter.put("date_added", domain.getDate_added());
        namedParameter.put("country", domain.getCountry());
        namedParameter.put("release_year", domain.getRelease_year());
        namedParameter.put("rating", domain.getRating());
        namedParameter.put("duration", domain.getDuration());
        namedParameter.put("listed_in", domain.getListed_in());
        namedParameter.put("description", domain.getDescription());

        template.update(sql, namedParameter);
        return findById(domain.getShow_id());

    }

    @Override
    public Iterable<Netflix> save(Collection<Netflix> domains) {
        //domains.forEach(this::save);
        template.batchUpdate(SQL_INSERT, SqlParameterSourceUtils.createBatch(domains));
        return findAll();
    }

    @Override
    public void delete(String show_id) {
        Map<String, String> namedParameter = Collections.singletonMap("show_id", show_id);
        template.update(SQL_DELETE, namedParameter);
    }

    @Override
    public void delete(Netflix domain) {
        delete(domain.getShow_id());
    }

    @Override
    public Netflix findById(String show_id) {
        Map<String, String> namedParameter = Collections.singletonMap("show_id", show_id);
        return template.queryForObject(SQL_FIND_ONE, namedParameter, netflixRowMapper);
    }

    @Override
    public long count() {
        return template.queryForObject(SQL_COUNT, Collections.emptyMap(), long.class).longValue();
    }

    @Override
    public Iterable<Netflix> findAll() {
        return template.query(SQL_FIND_ALL, netflixRowMapper);
    }

    private RowMapper<Netflix> netflixRowMapper = (ResultSet rs, int rowNum) -> {
        Netflix netflix = new Netflix();
        netflix.setShow_id(rs.getString("show_id"));
        netflix.setType(rs.getString("type"));
        netflix.setTitle(rs.getString("title"));
        netflix.setDirectors(rs.getString("directors"));
        netflix.setCast(rs.getString("cast_members"));
        netflix.setCountry(rs.getString("country"));
        java.sql.Date date = rs.getDate("date_added");
        netflix.setDate_added(date != null ? date.toLocalDate() : null);
        netflix.setRelease_year(rs.getInt("release_year"));
        netflix.setRating(rs.getString("rating"));
        netflix.setDuration(rs.getString("duration"));
        netflix.setListed_in(rs.getString("listed_in"));
        netflix.setDescription(rs.getString("description"));
        return netflix;
    };


    @Override
    public boolean exsists(String show_id) {
        Map<String, String> namedParameter = Collections.singletonMap("show_id", show_id);
        return template.queryForObject(SQL_EXIST, namedParameter,Boolean.class).booleanValue();
    }



}
