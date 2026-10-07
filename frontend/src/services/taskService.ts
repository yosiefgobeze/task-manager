import api from "./api";
import type { Task } from "../types/task";

const taskService = {

  getAll: async (): Promise<Task[]> => {
    const response =
      await api.get<Task[]>("/tasks");

    return response.data;
  },

  create: async (task: {
    title: string;
    description: string;
  }): Promise<Task> => {

    const response =
      await api.post<Task>("/tasks", task);

    return response.data;
  },

  update: async (
    id: number,
    task: {
      title: string;
      description: string;
      completed: boolean;
    }
  ): Promise<Task> => {

    const response =
      await api.put<Task>(
        `/tasks/${id}`,
        task
      );

    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/tasks/${id}`);
  },
};

export default taskService;
