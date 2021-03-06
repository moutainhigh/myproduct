package zhumeng.com.uimei.kafka;


import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: KafkaProductServer.java
 * @Package zhumeng.com.uimei
 * @Description: TODO(用一句话描述该文件做什么)
 * @author z
 * @date 2018年7月2日
 * @version V1.0
 */
public class KafkaProductServer implements  Runnable{

	private static final Logger log = LoggerFactory.getLogger(KafkaProductServer.class);
	
	private String topic;
	private String key;
	private Object message;
	public KafkaProductServer(){}
	public KafkaProductServer(String topic,String key,Object message){
		this.topic=topic;
		this.key=key;
		this.message=message;
	}
	public static Properties props = null;
	static{
		 props = new Properties();
		 props.put("bootstrap.servers", ConfigUtil.get("bootstrap.servers"));
		 //生产者需要leader确认请求完成之前接收的应答数
		 props.put("acks", ConfigUtil.get("acks")!=null?ConfigUtil.get("acks"):"all");
		 props.put("retries", ConfigUtil.get("retries")!=null?Integer.valueOf(ConfigUtil.get("retries")):0);
		 props.put("batch.size", ConfigUtil.get("batch.size")!=null?Integer.valueOf(ConfigUtil.get("batch.size")):16384);
		 props.put("linger.ms", ConfigUtil.get("linger.ms")!=null?Integer.valueOf(ConfigUtil.get("linger.ms")):1);
		 props.put("buffer.memory", ConfigUtil.get("buffer.memory")!=null?Integer.valueOf(ConfigUtil.get("buffer.memory")):33554432);
		 
		   /* 设置序列化的类
	         * 可选：kafka.serializer.StringEncoder
	         * 默认：kafka.serializer.DefaultEncoder
	         */
		
		if(ConfigUtil.get("key.serializer")!=null){
			props.put("key.serializer", ConfigUtil.get("key.serializer"));
		}else{
			 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		}
		 if(ConfigUtil.get("producer.value.serializer")!=null){
			 props.put("value.serializer",ConfigUtil.get("producer.value.serializer"));
		 }else{
			 props.put("value.serializer", "zhumeng.com.uimei.interfaces.kafka.serialization.EncodeingKafka");
		 }
		
	}


	@Override
	public void run() {
		 Producer<String, Object> producer = createProducer();
//		 List<Object> asList = Arrays.asList(message);
		 producer.send(new ProducerRecord<String, Object>(topic, key, message), new Callback() {
				
				@Override
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if (metadata != null) {
						log.info(key+"发送成功："+"checksum: "+metadata.checksum()+" offset: "+metadata.offset()+" partition: "+metadata.partition()+" topic: "+metadata.topic());
					}
					if (exception != null) {
						log.info(key+"异常："+exception.getMessage());
					}
				}
			});
	}
	
	private  Producer<String, Object> createProducer(){
		return new KafkaProducer<String, Object>(props);
	}

}
