package ru.belov.ourabroad.poi.storage.sql;

public final class BookingSql {

    private BookingSql() {
    }

    public static final String FIND_BY_ID = """
            select id,
                   user_id,
                   specialist_id,
                   service_id,
                   start_time,
                   status,
                   created_at
            from bookings
            where id = :id
            """;

    public static final String FIND_BY_USER_ID = """
            select id,
                   user_id,
                   specialist_id,
                   service_id,
                   start_time,
                   status,
                   created_at
            from bookings
            where user_id = :userId
            order by start_time desc
            """;

    public static final String FIND_BY_SPECIALIST_ID = """
            select id,
                   user_id,
                   specialist_id,
                   service_id,
                   start_time,
                   status,
                   created_at
            from bookings
            where specialist_id = :specialistId
            order by start_time desc
            """;

    public static final String UPSERT = """
            insert into bookings (
                id,
                user_id,
                specialist_id,
                service_id,
                start_time,
                status,
                created_at
            ) values (
                :id,
                :userId,
                :specialistId,
                :serviceId,
                :startTime,
                :status,
                :createdAt
            )
            on conflict (id) do update set
                user_id = excluded.user_id,
                specialist_id = excluded.specialist_id,
                service_id = excluded.service_id,
                start_time = excluded.start_time,
                status = excluded.status,
                created_at = bookings.created_at
            """;

    public static final String UPDATE_STATUS = """
            update bookings
            set status = :status
            where id = :id
            """;
}
