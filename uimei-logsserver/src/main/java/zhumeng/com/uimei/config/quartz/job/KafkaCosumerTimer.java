package zhumeng.com.uimei.config.quartz.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import zhumeng.com.uimei.kafka.ComsumerThreadPool;
import zhumeng.com.uimei.kafka.ConfigUtil;
import zhumeng.com.uimei.kafka.KafkaConsumerServer;

/**
 * @Title: StatiscSysInfo.java
 * @Package zhumeng.com.uimei.config.quartz.job
 * @Description: 定时上报系统信息
 * @author z
 * @date 2018年6月29日
 * @version V1.0
 */
@Component
@EnableScheduling
public class KafkaCosumerTimer{
	
	public final static Logger log = LoggerFactory.getLogger(KafkaCosumerTimer.class);

	

//	@Scheduled(cron = "0/10 * * * * ?")
//	@Scheduled(cron = "0 0/10 * * * ?")
	public void kafkaCosumerJoRun() throws Exception{
		log.error("开始消费日志信息======");
		new ComsumerThreadPool().getTaskExecutor().execute(
				new KafkaConsumerServer(ConfigUtil.get("topic")!=null?ConfigUtil.get("topic"):"visitLogs"));
	}


}
