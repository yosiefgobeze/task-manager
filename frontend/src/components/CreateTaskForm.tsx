import { FormEvent, useState } from "react";
import taskService from "../services/taskService";
import type { Task } from "../types/task";

interface CreateTaskFormProps {
  onTaskCreated: (createdTask: Task) => void;
}

function CreateTaskForm({
  onTaskCreated,
}: CreateTaskFormProps) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    if (!title.trim()) {
      setError("Title is required.");
      return;
    }

    try {
      setSaving(true);
      setError(null);

      const createdTask = await taskService.create({
        title: title.trim(),
        description: description.trim(),
      });

      onTaskCreated(createdTask);

      setTitle("");
      setDescription("");
    } catch (error) {
      console.error(error);
      setError("Failed to create task.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="form-container">
      <h2 className="form-title">
        Create a New Task
      </h2>

      {error && (
        <p className="form-error">
          {error}
        </p>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label
            className="form-label"
            htmlFor="task-title"
          >
            Title
          </label>

          <input
            id="task-title"
            className="form-input"
            type="text"
            value={title}
            onChange={(event) =>
              setTitle(event.target.value)
            }
            placeholder="Enter task title"
            maxLength={200}
            disabled={saving}
          />
        </div>

        <div className="form-group">
          <label
            className="form-label"
            htmlFor="task-description"
          >
            Description
          </label>

          <textarea
            id="task-description"
            className="form-textarea"
            value={description}
            onChange={(event) =>
              setDescription(event.target.value)
            }
            placeholder="Enter task description"
            maxLength={2000}
            disabled={saving}
          />
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={saving}
          >
            {saving
              ? "Creating..."
              : "Create Task"}
          </button>
        </div>
      </form>
    </section>
  );
}

export default CreateTaskForm;
