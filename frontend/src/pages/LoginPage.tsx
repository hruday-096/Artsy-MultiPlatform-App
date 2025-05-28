import React, { useState, useEffect } from "react";
import { useAuth } from "../context/authContext";
import { useNavigate, Link } from "react-router-dom";

const LoginPage: React.FC = () => {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  
  useEffect(() => {
    if (isAuthenticated) {
      navigate("/");
    }
  }, [isAuthenticated, navigate]);
  
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [touched, setTouched] = useState<{ [key: string]: boolean }>({});
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [isButtonDisabled, setIsButtonDisabled] = useState(false);
  
  const validateField = (field: string, value: string) => {
    const newErrors = { ...errors };
    
    switch (field) {
      case 'email':
        if (!value.trim()) {
          newErrors.email = "Email is required.";
        } else if (!/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(value)) {
          newErrors.email = "Email must be valid.";
        } else {
          delete newErrors.email;
        }
        break;
      case 'password':
        if (!value.trim()) {
          newErrors.password = "Password is required.";
        } else {
          delete newErrors.password;
        }
        break;
      default:
        break;
    }
    
    setErrors(newErrors);
  };
  
  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setTouched({ ...touched, [id]: true });
    validateField(id, value);
  };
  
  const handleFocus = () => {
    setIsButtonDisabled(false);
    if (errors.auth) {
      const newErrors = { ...errors };
      delete newErrors.auth;
      setErrors(newErrors);
    }
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setTouched({
      email: true,
      password: true
    });
    
    validateField('email', email);
    validateField('password', password);
    
    if (email.trim() !== "" && password.trim() !== "") {
      try {
        await login(email, password);
        setTimeout(() => {
          window.location.reload();
          navigate("/");
        }, 100);
      } catch (_error) {
        console.log(_error);
        setErrors(prev => ({ ...prev, auth: "Email or password incorrect" }));
        setIsButtonDisabled(true);
      }
    }
  };
  
  const isFormValid = email.trim() !== "" && password.trim() !== "";
  
  return (
    <div className="container mt-5 d-flex justify-content-center align-items-center">
      <div className="card p-4" style={{ maxWidth: "500px", width: "100%", borderRadius: "8px", boxShadow: "0 2px 10px rgba(0,0,0,0.1)" }}>
        <h3 className="text-center mb-4">Login</h3>
        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">
              Email address
            </label>
            <input
              type="email"
              required
              className={`form-control ${touched.email && errors.email ? "is-invalid" : ""}`}
              id="email"
              placeholder="Enter email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onBlur={handleBlur}
              onFocus={handleFocus}
            />
            {touched.email && errors.email &&
              <div className="invalid-feedback">{errors.email}</div>
            }
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="form-label">
              Password
            </label>
            <input
              type="password"
              required
              className={`form-control ${(touched.password && errors.password) || errors.auth ? "is-invalid" : ""}`}
              id="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onBlur={handleBlur}
              onFocus={handleFocus}
            />
            {touched.password && errors.password &&
              <div className="invalid-feedback">{errors.password}</div>
            }
            {errors.auth &&
              <div className="invalid-feedback">{errors.auth}</div>
            }
          </div>
          <button
            type="submit"
            className="btn w-100 py-2"
            disabled={!isFormValid || isButtonDisabled}
            style={{ backgroundColor: "#205375", borderColor: "#205375", color: "white" }}
          >
            Log in
          </button>
        </form>
        <div className="text-center mt-3">
          Don't have an account yet?{" "}
          <Link to="/register" style={{ color: "#6C8CFF" }}>
            Register
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;