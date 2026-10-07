import { useEffect, useState } from "react";
import CreateTaskForm from "../components/CreateTaskForm";
import EditTaskForm from "../components/EditTaskForm";
import taskService from "../services/taskService";
import type { Task } from "../types/task";

function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingId, setDeletingId] =
    useState<number | null>(null);
  const [updatingId, setUpdatingId] =
    useState<number | null>(null);
  const [editingId, setEditingId] =
    useState<number | null>(null);

  useEffect(() => {
    const loadTasks = async () => {
      try {
        setLoading(true);
        setError(null);

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

  const handleTaskCreated = (
    createdTask: Task
  ) => {
    setTasks((currentTasks) => [
      ...currentTasks,
      createdTask,
    ]);
  };

  const handleToggleCompleted = async (
    task: Task
  ) => {
    try {
      setUpdatingId(task.id);
      setError(null);

      const updatedTask =
        await taskService.update(task.id, {
          title: task.title,
          description: task.description,
          completed: !task.completed,
        });

      setTasks((currentTasks) =>
        currentTasks.map((currentTask) =>
          currentTask.id === updatedTask.id
            ? updatedTask
            : currentTask
        )
      );
    } catch (error) {
      console.error(error);
      setError("Failed to update task.");
    } finally {
      setUpdatingId(null);
    }
  };

  const handleTaskUpdated = (
    updatedTask: Task
  ) => {
    setTasks((currentTasks) =>
      currentTasks.map((currentTask) =>
        currentTask.id === updatedTask.id
          ? updatedTask
          : currentTask
      )
    );

    setEditingId(null);
  };

  const handleDelete = async (id: number) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this task?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(id);
      setError(null);

      await taskService.delete(id);

      setTasks((currentTasks) =>
        currentTasks.filter(
          (task) => task.id !== id
        )
      );
    } catch (error) {
      console.error(error);
      setError("Failed to delete task.");
    } finally {
      setDeletingId(null);
    }
  };

  if (loading) {
    return (
      <div className="loading-state">
        Loading tasks...
      </div>
    );
  }

  return (
    <>
      <CreateTaskForm
        onTaskCreated={handleTaskCreated}
      />

      <section>
        <h2 className="section-title">
          Your Tasks
        </h2>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <p className="task-count">
          {tasks.length === 1
            ? "1 task"
            : `${tasks.length} tasks`}
        </p>

        {tasks.length === 0 ? (
          <div className="empty-state">
            <p>
              You don't have any tasks yet.
            </p>

            <p>
              Create your first task above.
            </p>
          </div>
        ) : (
          <div className="task-list">
            {tasks.map((task) => (
              <div
                key={task.id}
                className={`task-card ${
                  task.completed
                    ? "completed"
                    : ""
                }`}
              >
                {editingId === task.id ? (
                  <EditTaskForm
                    task={task}
                    onTaskUpdated={
                      handleTaskUpdated
                    }
                    onCancel={() =>
                      setEditingId(null)
                    }
                  />
                ) : (
                  <>
                    <div className="task-header">
                      <h3 className="task-title">
                        {task.title}
                      </h3>

                      <span
                        className={`status-badge ${
                          task.completed
                            ? "status-completed"
                            : "status-incomplete"
                        }`}
                      >
                        {task.completed
                          ? "Completed"
                          : "Incomplete"}
                      </span>
                    </div>

                    <p className="task-description">
                      {task.description ||
                        "No description"}
                    </p>

                    <div className="task-actions">
                      <button
                        className={`button ${
                          task.completed
                            ? "button-secondary"
                            : "button-success"
                        }`}
                        onClick={() =>
                          handleToggleCompleted(
                            task
                          )
                        }
                        disabled={
                          updatingId === task.id
                        }
                      >
                        {updatingId === task.id
                          ? "Updating..."
                          : task.completed
                            ? "Mark incomplete"
                            : "Mark completed"}
                      </button>

                      <button
                        className="button button-secondary"
                        onClick={() =>
                          setEditingId(task.id)
                        }
                      >
                        Edit
                      </button>

                      <button
                        className="button button-danger"
                        onClick={() =>
                          handleDelete(task.id)
                        }
                        disabled={
                          deletingId === task.id
                        }
                      >
                        {deletingId === task.id
                          ? "Deleting..."
                          : "Delete"}
                      </button>
                    </div>
                  </>
                )}
              </div>
            ))}
          </div>
        )}
      </section>
    </>
  );
}

export default TasksPage;
