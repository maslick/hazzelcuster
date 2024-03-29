package io.maslick.hazzelcuster;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class HazelCastConfig {
	@Bean
	public Config config(@Value("${hazzelcuster.headless_dns}") final String HAZELCAST_SERVICE_NAME,
						 @Value("${hazzelcuster.mc_url}") final String MANCENTER_URL) {
		var config = new ClasspathXmlConfig("hazelcast.xml");
		JoinConfig joinConfig = config.getNetworkConfig().getJoin();
		joinConfig.getMulticastConfig().setEnabled(false);

		if (System.getProperty("k8s", "false").equalsIgnoreCase("true")) {
			joinConfig.getKubernetesConfig().setEnabled(true).setProperty("service-dns", HAZELCAST_SERVICE_NAME);
			config.getManagementCenterConfig().setEnabled(true);
			config.getManagementCenterConfig().setUrl(MANCENTER_URL);
		} else joinConfig.getTcpIpConfig().setEnabled(true).setMembers(Collections.singletonList("127.0.0.1:5701"));

		return config;
	}
}
