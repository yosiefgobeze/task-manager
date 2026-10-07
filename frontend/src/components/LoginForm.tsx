import { FormEvent, useState } from "react";
import authService from "../services/authService";

interface LoginFormProps {
  onLogin: () => void;
  onShowRegister: () => void;
}

function LoginForm({
  onLogin,
  onShowRegister,
}: LoginFormProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    if (!username.trim() || !password) {
      setError("Username and password are required.");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await authService.login({
        username: username.trim(),
        password,
      });

      onLogin();
    } catch (error) {
      console.error(error);
      setError("Invalid username or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="form-container">
      <h2 className="form-title">Login</h2>

      {error && (
        <p className="form-error">
          {error}
        </p>
      )}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label
            className="form-label"
            htmlFor="login-username"
          >
            Username
          </label>

          <input
            id="login-username"
            className="form-input"
            type="text"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
            disabled={loading}
            autoComplete="username"
          />
        </div>

        <div className="form-group">
          <label
            className="form-label"
            htmlFor="login-password"
          >
            Password
          </label>

          <input
            id="login-password"
            className="form-input"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            disabled={loading}
            autoComplete="current-password"
          />
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>

          <button
            className="button button-secondary"
            type="button"
            onClick={onShowRegister}
            disabled={loading}
          >
            Register
          </button>
        </div>
      </form>
    </section>
  );
}

export default LoginForm;
