package org.bendersdestiny.playertutorials.tutorial.task.tasks;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.tutorial.task.Task;

@Getter @Setter
public class CommandTask extends Task {
	private String requiredCommand;
	private String executedCommand;

	public CommandTask(int taskID, int areaID, int priority,String requiredCommand) {
		super(taskID, areaID,  "CommandTask", priority);
		this.requiredCommand = requiredCommand;
	}
}
