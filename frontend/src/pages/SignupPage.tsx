import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { AxiosError } from "axios";//axios, 
import API from "../services/api.tsx"
import { useAuth } from "../context/authContext";

const SignupPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  
  useEffect(() => {
    if (isAuthenticated) {
      navigate("/");
    }
  }, [isAuthenticated, navigate]);
  
  const [fullname, setFullname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [touched, setTouched] = useState<{ [key: string]: boolean }>({});
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  
  const isFormValid = fullname && email && password && Object.keys(errors).length === 0;
  
  const validateField = (field: string, value: string) => {
    const newErrors = { ...errors };
    
    switch (field) {
      case 'fullname':
        if (!value.trim()) {
          newErrors.fullname = "Fullname is required.";
        } else {
          delete newErrors.fullname;
        }
        break;
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
    return !newErrors[field]; 
  };
  
  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setTouched({ ...touched, [id]: true });
    validateField(id, value);
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    setTouched({
      fullname: true,
      email: true,
      password: true
    });
    
    const isFullnameValid = validateField('fullname', fullname);
    const isEmailValid = validateField('email', email);
    const isPasswordValid = validateField('password', password);
    
    if (isFullnameValid && isEmailValid && isPasswordValid) {
      try {
        await API.post("/api/auth/register", { fullname, email, password }, { withCredentials: true });
        await login(email, password); 
        setTimeout(() => {
          window.location.reload();
          navigate("/");
        }, 100);
      } catch (err: unknown) {
        const error = err as AxiosError<{ errors?: Record<string, string>; message?: string }>;
        const responseErrors = error.response?.data?.errors || {};
        
        const newErrors = { ...errors };
        
        if (responseErrors.email) {
          newErrors.email = responseErrors.email;
        }
        if (responseErrors.fullname) {
          newErrors.fullname = responseErrors.fullname;
        }
        if (responseErrors.password) {
          newErrors.password = responseErrors.password;
        }
        
        if (error.response?.data?.message && Object.keys(responseErrors).length === 0) {
          const message = error.response.data.message.toLowerCase();
          if (message.includes('email') || message.includes('already exists')) {
            newErrors.email = error.response.data.message;
          } else {
            console.error(error.response.data.message);
          }
        }
        
        setErrors(newErrors);
      }
    }
  };
  
  return (
    <div className="container mt-5 d-flex justify-content-center align-items-center">
      <div className="card p-4" style={{ maxWidth: "500px", width: "100%", borderRadius: "8px", boxShadow: "0 2px 10px rgba(0,0,0,0.1)" }}>
        <h2 className="text-center mb-4">Register</h2>
        <form onSubmit={handleSubmit} noValidate>
          <div className="mb-3">
            <label htmlFor="fullname" className="form-label">Full Name</label>
            <input
              type="text"
              className={`form-control ${(touched.fullname || errors.fullname) && errors.fullname ? "is-invalid" : ""}`}
              id="fullname"
              value={fullname}
              onChange={(e) => setFullname(e.target.value)}
              onBlur={handleBlur}
              required
              placeholder="Enter full name"
            />
            {(touched.fullname || errors.fullname) && errors.fullname && 
              <div className="invalid-feedback">{errors.fullname}</div>
            }
          </div>
          <div className="mb-3">
            <label htmlFor="email" className="form-label">Email address</label>
            <input
              type="email"
              className={`form-control ${(touched.email || errors.email) && errors.email ? "is-invalid" : ""}`}
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onBlur={handleBlur}
              required
              placeholder="Enter email"
            />
            {(touched.email || errors.email) && errors.email && 
              <div className="invalid-feedback">{errors.email}</div>
            }
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="form-label">Password</label>
            <input
              type="password"
              className={`form-control ${(touched.password || errors.password) && errors.password ? "is-invalid" : ""}`}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onBlur={handleBlur}
              required
              placeholder="Password"
            />
            {(touched.password || errors.password) && errors.password && 
              <div className="invalid-feedback">{errors.password}</div>
            }
          </div>
          <button
            type="submit"
            className="btn w-100 py-2"
            disabled={!isFormValid}
            style={{ backgroundColor: "#205375", borderColor: "#205375", color: "white" }}
          >
            Register
          </button>
        </form>
        <div className="text-center mt-3">
          <span>Already have an account? </span>
          <Link to="/login" style={{ color: "#6C8CFF" }}>Log In</Link>
        </div>
      </div>
    </div>
  );
};

export default SignupPage;