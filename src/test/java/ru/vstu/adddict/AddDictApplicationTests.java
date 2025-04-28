package ru.vstu.adddict;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.vstu.adddict.config.TestContainersConfig;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.class)
class AddDictApplicationTests {

	@Test
	void contextLoads() {
	}

}
