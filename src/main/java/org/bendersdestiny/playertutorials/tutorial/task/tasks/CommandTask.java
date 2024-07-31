package org.bendersdestiny.playertutorials.tutorial.task.tasks;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bukkit.entity.Player;

@Getter @Setter
public class CommandTask extends Task {
	private String requiredCommand;
	private String executedCommand;

	public CommandTask(int id, int priority, Player player, String requiredCommand, String executedCommand) {
		super(id, player, priority);
		this.requiredCommand = requiredCommand;
		this.executedCommand = executedCommand;
	}

	public boolean validateCommand() {
		if (requiredCommand != null && !requiredCommand.isEmpty() && executedCommand != null && !executedCommand.isEmpty()) {
			String formattedRequiredCommand = requiredCommand.toLowerCase();
			String formattedExecutedCommand = executedCommand.toLowerCase();

			return formattedRequiredCommand.equals(formattedExecutedCommand);
		}
		return false;
	}
}
