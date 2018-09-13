package com.team360.hms;

import com.team360.hms.db.DBManager;
import com.team360.hms.db.DBManagerConfig;
import lombok.extern.slf4j.Slf4j;
import com.team360.hms.web.WebServerConfig;
import com.team360.hms.web.WebServerManager;

@Slf4j
public class Main {

    public static final String SQL_TYPE = "SQL_TYPE";
    public static final String SQL_URL = "SQL_URL";
    public static final String SQL_DRIVER = "SQL_DRIVER";
    public static final String SQL_USER = "SQL_USER";
    public static final String SQL_PASSWORD = "SQL_PASSWORD";

    public static final String SYSTEM_EMAIL_ADDRESS = "SYSTEM_EMAIL_ADDRESS";
    public static final String SYSTEM_EMAIL_PASS = "SYSTEM_EMAIL_PASS";
    public static final String SYSTEM_EMAIL_SERVER = "SYSTEM_EMAIL_SERVER";
    public static final String SYSTEM_EMAIL_PORT = "SYSTEM_EMAIL_PORT";

    public static final String SIGNATURE_KEY = "SIGNATURE_KEY";

    public static final String PROTOCOL = "PROTOCOL";
    public static final String PORT = "PORT";
    public static final String DOMAIN = "DOMAIN";
    public static final String CONTEXT = "CONTEXT";

    public static final boolean DEBUG_MODE = java.lang.management.ManagementFactory.getRuntimeMXBean().
            getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Close connections...");
            DBManager.stop();
        }, "disconnectDB"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping server...");
            WebServerManager.stop();
        }, "shutdownNow"));

        try {

            DBManagerConfig db = DBManagerConfig.builder()
                    .type(System.getenv(SQL_TYPE))
                    .url(System.getenv(SQL_URL))
                    .driver(System.getenv(SQL_DRIVER))
                    .user(System.getenv(SQL_USER))
                    .pass(System.getenv(SQL_PASSWORD))
                    .build();

            DBManager.start(db);

//            EmailUtils.test();

            WebServerConfig web = WebServerConfig.builder()
                    .protocol(System.getenv(PROTOCOL))
                    .domain(System.getenv(DOMAIN))
                    .port(System.getenv(PORT))
                    .context(System.getenv(CONTEXT))
                    .endpoints("modules/endpoints")
                    .secret(System.getenv(SIGNATURE_KEY))
                    .build();

            WebServerManager.start(web);

            System.out.println("Press Enter to exit..");
            System.in.read();

            log.info("Exiting...");
            System.exit(0);

        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
            System.exit(0);
        }
    }
}