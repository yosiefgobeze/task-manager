export interface Task {
  id: number;
  title: string;
  description: string;
  completed: boolean;
  createdAt?: string;
  updatedAt?: string;
}


export interface CreateTaskRequest {
    title: string;
    description: string;
}

export interface UpdateTaskRequest {
    title: string;
    description: string;
    completed?: boolean;
}
