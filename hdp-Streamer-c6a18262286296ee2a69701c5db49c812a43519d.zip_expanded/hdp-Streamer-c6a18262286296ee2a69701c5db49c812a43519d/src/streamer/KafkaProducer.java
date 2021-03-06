package streamer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;



public class KafkaProducer {

	public static void main(String[] args) throws InterruptedException {
		
		Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty("bootstrap.servers", "127.0.0.1:9092"); //kafka brokers Ips
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("buffer.memory", "33554432");
        properties.setProperty("acks", "1");// no losses
        properties.setProperty("retries", "3");
        properties.setProperty("linger.ms", "1"); 

		String csvFile = "dataSet/kddcup.data_10_percent_corrected";
        BufferedReader br = null;
        Producer<String, String> producer=null;
        String line = "";

        try {
     
              
           	producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(properties);
            br = new BufferedReader(new FileReader(csvFile));
            
            int i=0;
            while (true) { //Streaming the 100 first records of kddCup
            	line = br.readLine();
            	if (line == null){
                    br = new BufferedReader(new FileReader(csvFile));
            	}
            	ProducerRecord<String, String> producerRecord =
                        new ProducerRecord<String, String>("kddData", Integer.toString(i),line);
            	Thread.sleep(new Random().nextInt(200) + 5); // pauses 5 to 200ms 
		                producer.send(producerRecord);
		            	i++;		        
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	producer.flush();
            producer.close();
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
