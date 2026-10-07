import { useState } from "react";
import taskService from "../services/taskService";
import type { Task } from "../types/task";

interface TaskItemProps {
  task: Task;
  onTaskUpdated: (task: Task) => void;
}

function TaskItem({
  task,
  onTaskUpdated,
}: TaskItemProps) {
  const [loading, setLoading] = useState(false);

  const handleToggleCompleted = async () => {
    try {
      setLoading(true);

      const updatedTask = await taskService.update(
        task.id,
        {
          title: task.title,
          description: task.description,
          completed: !task.completed,
        }
      );

      onTaskUpdated(updatedTask);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>{task.title}</h2>

      <p>{task.description}</p>

      <p>
        Status:{" "}
        {task.completed ? "Completed" : "Incomplete"}
      </p>

      <button
        onClick={handleToggleCompleted}
        disabled={loading}
      >
        {loading
          ? "Updating..."
          : task.completed
            ? "Mark Incomplete"
            : "Mark Complete"}
      </button>
    </div>
  );
}

export default TaskItem;
