package zhumeng.com.uimei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class}) //排除DataSourceConfiguratrion
@MapperScan("zhumeng.com.uimei.dao.*")/** 扫描mybatis mapper接口 */			
@PropertySource({"classpath:config.properties","classpath:jdbc.properties"})
@EnableTransactionManagement   //开启事物管理功能
public class AppLogsServer extends SpringBootServletInitializer
{
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AppLogsServer.class);
    }
	public static void main(String[] args) {
		SpringApplication.run(AppLogsServer.class, args);
	}
}