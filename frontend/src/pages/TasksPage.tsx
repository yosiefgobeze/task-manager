import { useEffect, useState } from "react";
import taskService  from "../services/taskService";
import type { Task } from "../types/task";

function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadTasks = async () => {
      try {
        setLoading(true);

        const data = await taskService.getAll();

        setTasks(data);
      } catch (error) {
        console.error(error);
        setError("Failed to load tasks.");
      } finally {
        setLoading(false);
      }
    };

    loadTasks();
  }, []);

  if (loading) {
    return <p>Loading tasks...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <main>
      <h1>Task Manager</h1>

      <p>Total tasks: {tasks.length}</p>

      {tasks.map((task) => (
        <div key={task.id}>
          <h2>{task.title}</h2>

          <p>{task.description}</p>

          <p>
            Status: {task.completed ? "Completed" : "Incomplete"}
          </p>
        </div>
      ))}
    </main>
  );
}

export default TasksPage;
