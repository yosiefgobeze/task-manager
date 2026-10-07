import { useState } from "react";
import "./App.css";
import TasksPage from "./pages/TasksPage";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import authService from "./services/authService";

type AuthPage = "login" | "register";

function App() {
  const [authenticated, setAuthenticated] =
    useState(authService.isAuthenticated());

  const [authPage, setAuthPage] =
    useState<AuthPage>("login");

  const handleLogin = () => {
    setAuthenticated(true);
  };

  const handleLogout = () => {
    authService.logout();
    setAuthenticated(false);
    setAuthPage("login");
  };

  const handleRegistered = () => {
    setAuthPage("login");
  };

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <div>
            <h1 className="app-title">
              Task Manager
            </h1>

            <p className="app-subtitle">
              Organize your work and stay productive
            </p>
          </div>

          {authenticated && (
            <div>
              <span>
                {authService.getUsername()}
              </span>

              <button
                className="button button-secondary"
                onClick={handleLogout}
                style={{ marginLeft: "12px" }}
              >
                Logout
              </button>
            </div>
          )}
        </div>
      </header>

      <main className="app-main">
        {!authenticated ? (
          authPage === "login" ? (
            <LoginForm
              onLogin={handleLogin}
              onShowRegister={() =>
                setAuthPage("register")
              }
            />
          ) : (
            <RegisterForm
              onRegistered={handleRegistered}
              onShowLogin={() =>
                setAuthPage("login")
              }
            />
          )
        ) : (
          <TasksPage />
        )}
      </main>
    </div>
  );
}

export default App;
