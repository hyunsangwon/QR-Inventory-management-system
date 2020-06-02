package pro.cntech.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.cntech.inventory.service.StatusService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class InventoryApplicationTests {

	@Test
	void contextLoads()
	{
		List<String> list = new ArrayList<>();
		list.add("hello");
		list.add("bye");



 	}

}
