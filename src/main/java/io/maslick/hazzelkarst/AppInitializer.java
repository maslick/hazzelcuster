package io.maslick.hazzelkarst;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppInitializer implements CommandLineRunner {

	private static final String[][] GREETINGS = new String[][] {
			{ "russian", "privet" },
			{ "english", "hello" },
			{ "slovenian", "zdravo" }
	};

	private final HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) {
		var hello = hazelcastInstance.<String, String>getMap("hello");
		if(!hello.isEmpty()) log.info("Skip loading '{}', not empty", hello.getName());
		else {
			Arrays.stream(GREETINGS).forEach(pair -> hello.set(pair[0], pair[1]));
			log.info("Loaded {} into '{}'", GREETINGS.length, hello.getName());
		}
	}
}
