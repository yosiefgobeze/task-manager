import api from "./api";
import type {
    Task,
    CreateTaskRequest,
    UpdateTaskRequest,
} from "../types/task";

const taskService = {
    getAll: async (): Promise<Task[]> => {
        const response = await api.get<Task[]>("/tasks");
        return response.data;
    },

    getById: async (id: number): Promise<Task> => {
        const response = await api.get<Task>(`/tasks/${id}`);
        return response.data;
    },

    create: async (
        request: CreateTaskRequest
    ): Promise<Task> => {
        const response = await api.post<Task>("/tasks", request);
        return response.data;
    },

    update: async (
        id: number,
        request: UpdateTaskRequest
    ): Promise<Task> => {
        const response = await api.put<Task>(
            `/tasks/${id}`,
            request
        );

        return response.data;
    },

    delete: async (id: number): Promise<void> => {
        await api.delete(`/tasks/${id}`);
    },
};

export default taskService;
