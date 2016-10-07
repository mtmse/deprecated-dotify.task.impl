package org.daisy.dotify.impl.system.common;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskGroupSpecification;
import org.daisy.dotify.consumer.tasks.TaskGroupFactoryMaker;
import org.junit.Test;
public class DotifyTaskSystemTest {

	public DotifyTaskSystemTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void notAnActualTest() {
		Set<TaskGroupSpecification> specs = TaskGroupFactoryMaker.newInstance().listSupportedSpecifications();
		Map<String, List<TaskGroupSpecification>> byInput = DotifyTaskSystem.byInput(specs);
		//DotifyTaskSystem.getPathSpecifications("dtbook", "pef", "sv-SE", new HashMap<String, Object>(), specs);
		DotifyTaskSystem.listSpecs(System.out, byInput);
	}
	
	@Test
	public void testPath_01() {
		TaskGroupSpecification spec = new TaskGroupSpecification("dtbook", "pef", "sv-SE");
		List<TaskGroup> tasks = DotifyTaskSystem.getPath(TaskGroupFactoryMaker.newInstance(), spec, new HashMap<String, Object>());
		for (TaskGroup g : tasks) {
			System.out.println(g.getName());
		}
		assertEquals(2, tasks.size());
		assertEquals("XMLInputManager", tasks.get(0).getName());
		assertEquals("Layout Engine", tasks.get(1).getName());
	}
	
	@Test
	public void testPath_02() {
		TaskGroupSpecification spec = new TaskGroupSpecification("epub", "pef", "sv-SE");
		List<TaskGroup> tasks = DotifyTaskSystem.getPath(TaskGroupFactoryMaker.newInstance(), spec, new HashMap<String, Object>());
		for (TaskGroup g : tasks) {
			System.out.println(g.getName());
		}
		assertEquals(3, tasks.size());
		assertEquals("org.daisy.dotify.impl.input.epub.Epub3InputManager", tasks.get(0).getName());
		assertEquals("XMLInputManager", tasks.get(1).getName());
		assertEquals("Layout Engine", tasks.get(2).getName());
	}

}
