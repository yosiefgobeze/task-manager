import { useState } from "react";
import type { FormEvent } from "react";
import authService from "../services/authService";

interface RegisterFormProps {
  onRegistered: () => void;
  onShowLogin: () => void;
}

function RegisterForm({
  onRegistered,
  onShowLogin,
}: RegisterFormProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] =
    useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] =
    useState<string | null>(null);

  const handleSubmit = async (
    event: FormEvent
  ) => {
    event.preventDefault();

    if (!username.trim() || !password) {
      setError(
        "Username and password are required."
      );
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await authService.register({
        username: username.trim(),
        password,
      });

      onRegistered();
    } catch (error) {
      console.error(error);
      setError(
        "Registration failed. The username may already exist."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="form-container">
      <h2 className="form-title">
        Create Account
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
            htmlFor="register-username"
          >
            Username
          </label>

          <input
            id="register-username"
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
            htmlFor="register-password"
          >
            Password
          </label>

          <input
            id="register-password"
            className="form-input"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            disabled={loading}
            autoComplete="new-password"
          />
        </div>

        <div className="form-group">
          <label
            className="form-label"
            htmlFor="register-confirm-password"
          >
            Confirm Password
          </label>

          <input
            id="register-confirm-password"
            className="form-input"
            type="password"
            value={confirmPassword}
            onChange={(event) =>
              setConfirmPassword(event.target.value)
            }
            disabled={loading}
            autoComplete="new-password"
          />
        </div>

        <div className="form-actions">
          <button
            className="button button-primary"
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Creating..."
              : "Create Account"}
          </button>

          <button
            className="button button-secondary"
            type="button"
            onClick={onShowLogin}
            disabled={loading}
          >
            Back to Login
          </button>
        </div>
      </form>
    </section>
  );
}

export default RegisterForm;
