//package com.media.share.flyway;
//
//
//import com.media.share.ShareMediaApplication;
//import org.flywaydb.core.Flyway;
//import org.flywaydb.core.api.MigrationInfo;
//import org.flywaydb.core.api.MigrationInfoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Lazy;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.utility.DockerImageName;
//
//@TestConfiguration(proxyBeanMethods = false)
//public class flywayTest implements CommandLineRunner {
//
//    @Bean
//    @ServiceConnection
//    MySQLContainer<?> mySQLContainer(){
//        return new MySQLContainer<>(DockerImageName.parse("mysql:9.0"))
//                .withDatabaseName("testdb")
//                .withUsername("testuser")
//                .withPassword("testpass");
//
//    }
//
//
//    public static void main(String[] args){
//        SpringApplication
//                .from(ShareMediaApplication::main)
//                .with(flywayTest.class)
//                .run(args);
//    }
//
//    @Autowired
//    private ApplicationContext context;
//
//    @Override
//    public void run(String... args){
//        Flyway flyway = context.getBean(Flyway.class);
//        flyway.migrate();
//        System.out.println("Flyway Info:");
//        // Retrieve MigrationInfoService
//        MigrationInfoService infoService = flyway.info();
//        MigrationInfo[] allMigrations = infoService.all();
//
//        // Use a for-each loop to iterate through MigrationInfo array
//        for (MigrationInfo info : allMigrations) {
//            System.out.println("Version: " + info.getVersion());
//            System.out.println("Description: " + info.getDescription());
//            System.out.println("Type: " + info.getType());
//            System.out.println("Installed On: " + info.getInstalledOn());
//            System.out.println("State: " + info.getState());
//            System.out.println("--------------------------");
//        }
//
//    }
//
//}
