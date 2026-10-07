import api from "./api";

interface RegisterRequest {
  username: string;
  password: string;
}

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  token: string;
  username: string;
}

const authService = {

  register: async (
    data: RegisterRequest
  ): Promise<void> => {

    await api.post(
      "/auth/register",
      data
    );
  },

  login: async (
    data: LoginRequest
  ): Promise<LoginResponse> => {

    const response =
      await api.post<LoginResponse>(
        "/auth/login",
        data
      );

    localStorage.setItem(
      "token",
      response.data.token
    );

    localStorage.setItem(
      "username",
      response.data.username
    );

    return response.data;
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
  },

  isAuthenticated: (): boolean => {
    return Boolean(
      localStorage.getItem("token")
    );
  },

  getUsername: (): string | null => {
    return localStorage.getItem("username");
  },
};

export default authService;
