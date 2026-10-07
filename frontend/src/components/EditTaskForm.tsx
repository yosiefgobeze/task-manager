import { FormEvent, useState } from "react";
import taskService from "../services/taskService";
import type { Task } from "../types/task";

interface EditTaskFormProps {
  task: Task;
  onTaskUpdated: (updatedTask: Task) => void;
  onCancel: () => void;
}

function EditTaskForm({
  task,
  onTaskUpdated,
  onCancel,
}: EditTaskFormProps) {
  const [title, setTitle] = useState(task.title);
  const [description, setDescription] = useState(
    task.description
  );
  const [completed, setCompleted] = useState(
    task.completed
  );
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(
    null
  );

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    if (!title.trim()) {
      setError("Title is required.");
      return;
    }

    try {
      setSaving(true);
      setError(null);

      const updatedTask =
        await taskService.update(task.id, {
          title: title.trim(),
          description: description.trim(),
          completed,
        });

      onTaskUpdated(updatedTask);
    } catch (error) {
      console.error(error);
      setError("Failed to update task.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="edit-form">
      <h3 className="form-title">
        Edit Task
      </h3>

      {error && (
        <p className="form-error">
          {error}
        </p>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label
            className="form-label"
            htmlFor={`edit-title-${task.id}`}
          >
            Title
          </label>

          <input
            id={`edit-title-${task.id}`}
            className="form-input"
            type="text"
            value={title}
            onChange={(event) =>
              setTitle(event.target.value)
            }
            maxLength={200}
            disabled={saving}
          />
        </div>

        <div className="form-group">
          <label
            className="form-label"
            htmlFor={`edit-description-${task.id}`}
          >
            Description
          </label>

          <textarea
            id={`edit-description-${task.id}`}
            className="form-textarea"
            value={description}
            onChange={(event) =>
              setDescription(event.target.value)
            }
            maxLength={2000}
            disabled={saving}
          />
        </div>

        <div className="form-group">
          <label className="form-label">
            <input
              type="checkbox"
              checked={completed}
              onChange={(event) =>
                setCompleted(
                  event.target.checked
                )
              }
              disabled={saving}
            />

            {" "}Completed
          </label>
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={saving}
          >
            {saving
              ? "Saving..."
              : "Save Changes"}
          </button>

          <button
            className="button button-secondary"
            type="button"
            onClick={onCancel}
            disabled={saving}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}

export default EditTaskForm;
