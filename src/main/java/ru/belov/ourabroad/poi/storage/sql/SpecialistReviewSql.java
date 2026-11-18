package ru.belov.ourabroad.poi.storage.sql;

public final class SpecialistReviewSql {

    private SpecialistReviewSql() {
    }

    public static final String INSERT = """
            insert into specialist_reviews (
                id,
                booking_id,
                author_id,
                specialist_id,
                score,
                text,
                created_at
            ) values (
                :id,
                :bookingId,
                :authorId,
                :specialistId,
                :score,
                :text,
                :createdAt
            )
            """;

    public static final String EXISTS_BY_BOOKING_ID = """
            select exists(
                select 1
                from specialist_reviews
                where booking_id = :bookingId
            )
            """;

    public static final String FIND_BY_SPECIALIST_ID = """
            select id,
                   booking_id,
                   author_id,
                   specialist_id,
                   score,
                   text,
                   created_at
            from specialist_reviews
            where specialist_id = :specialistId
            order by created_at desc
            """;

    public static final String AVERAGE_SCORE_AND_COUNT = """
            select coalesce(avg(score), 0) as rating,
                   count(*) as reviews_count
            from specialist_reviews
            where specialist_id = :specialistId
            """;
}
