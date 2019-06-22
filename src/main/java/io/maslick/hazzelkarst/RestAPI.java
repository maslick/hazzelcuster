package io.maslick.hazzelkarst;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RestAPI {

	private final HazelcastInstance hazelcastInstance;

	@GetMapping("/healthz")
	public String health() {
		log.info("health probe");
		var clusterState = hazelcastInstance.getCluster().getClusterState();
		if (clusterState == ClusterState.ACTIVE) return "UP";
		else throw new RuntimeException("Cluster state: " + clusterState);
	}
}
