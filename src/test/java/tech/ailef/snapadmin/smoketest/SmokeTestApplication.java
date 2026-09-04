package tech.ailef.snapadmin.smoketest;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tech.ailef.snapadmin.external.SnapAdminAutoConfiguration;

@SpringBootApplication
@ImportAutoConfiguration(SnapAdminAutoConfiguration.class)
public class SmokeTestApplication {
}
