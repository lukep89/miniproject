package ibf2022.batch1.project.server.repository;

public class Queries {

        public static final String SQL_GET_USER_BY_EMAIL = """
                        select * from user
                        where email = ?;
                        """;

        public static final String SQL_INSERT_TO_USER_TABLE = """
                        insert into user
                                (name, contact_number, email, password, status, role)
                                value
                                (?,?, ?,?,?,?);
                        """;

        public static final String SQL_GET_ALL_WHERE_ROLE_USER = """
                        select * from user
                        where role = 'user';
                        """;

        public static final String SQL_UPDATE_USER_STATUS_BY_EMAIL = """
                        update user set status = ?
                        where email = ?;
                        """;

        public static final String SQL_GET_USER_BY_ID = """
                        select * from user
                        where id = ?;
                        """;

        public static final String SQL_UPDATE_USER_STATUS_BY_ID = """
                        update user set status = ?
                        where id = ?;
                        """;

        public static final String SQL_GET_ALL_ADMIN = """
                        select * from user
                        where role = 'admin';
                        """;

        public static final String SQL_UPDATE_USER_PASSWORD = """
                        update user set password = ?, date_updated = now()
                        where email = ?;
                        """;
}
