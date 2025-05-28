import React, { useState, useRef, useEffect } from "react";
import { Link, NavLink, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/authContext";
import { toast } from "react-toastify";

const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout, deleteAccount } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [navbarOpen, setNavbarOpen] = useState(false);
  const dropdownRef = useRef<HTMLLIElement>(null);
  const [profileImage, setProfileImage] = useState<string>("");

  const buttonStyle: React.CSSProperties = {
    backgroundColor: "#205375",
    borderColor: "#205375",
    color: "white",
  };

  useEffect(() => {
    setNavbarOpen(false);
  }, [location]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (user?.profileImageUrl) {
      setProfileImage(user.profileImageUrl);
    }
  }, [user]);

  const handleLogout = async () => {
    await logout();
    toast.success("Logged out");
    navigate("/");
  };

  const handleDeleteAccount = async () => {
    try {
      await deleteAccount();
      toast.success("Your account has been deleted successfully");
      navigate("/");
    } catch (error) {
      toast.error("Failed to delete account. Please try again.");
      console.error("[Navbar] Delete account error:", error);
    }
  };

  const toggleNavbar = () => {
    setNavbarOpen(!navbarOpen);
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light px-4">
      <Link to="/" className="navbar-brand">
        Artist Search
      </Link>
      
      {/* Add hamburger button for mobile */}
      <button 
        className="navbar-toggler" 
        type="button" 
        onClick={toggleNavbar}
        aria-controls="navbarNav" 
        aria-expanded={navbarOpen ? "true" : "false"} 
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className={`collapse navbar-collapse ${navbarOpen ? "show" : ""}`} id="navbarNav">
        <ul className="navbar-nav ms-auto align-items-center gap-3">
          {!isAuthenticated ? (
            <>
              <li className="nav-item">
                <NavLink
                  to="/"
                  end
                  className={({ isActive }) =>
                    isActive ? "btn px-3 py-1 mx-3" : "nav-link text-dark mx-3"
                  }
                  style={({ isActive }) => (isActive ? buttonStyle : undefined)}
                >
                  Search
                </NavLink>
              </li>
              <li className="nav-item">
                <NavLink
                  to="/login"
                  className={({ isActive }) =>
                    isActive ? "btn px-3 py-1 mx-3" : "nav-link text-dark mx-3"
                  }
                  style={({ isActive }) => (isActive ? buttonStyle : undefined)}
                >
                  Log In
                </NavLink>
              </li>
              <li className="nav-item">
                <NavLink
                  to="/register"
                  className={({ isActive }) =>
                    isActive ? "btn px-3 py-1 mx-3" : "nav-link text-dark mx-3"
                  }
                  style={({ isActive }) => (isActive ? buttonStyle : undefined)}
                >
                  Register
                </NavLink>
              </li>
            </>
          ) : (
            <>
              <li className="nav-item">
                <NavLink
                  to="/"
                  end
                  className={({ isActive }) =>
                    isActive ? "btn px-3 py-1 mx-3" : "nav-link text-dark mx-3"
                  }
                  style={({ isActive }) => (isActive ? buttonStyle : undefined)}
                >
                  Search
                </NavLink>
              </li>
              <li className="nav-item">
                <NavLink
                  to="/favorites"
                  className={({ isActive }) =>
                    isActive ? "btn px-3 py-1 mx-3" : "nav-link text-dark mx-3"
                  }
                  style={({ isActive }) => (isActive ? buttonStyle : undefined)}
                >
                  Favorites
                </NavLink>
              </li>
              <li
                className="nav-item dropdown ms-3"
                ref={dropdownRef}
                onClick={() => setDropdownOpen((prev) => !prev)}
                style={{ cursor: "pointer" }}
              >
                <span className="nav-link d-flex align-items-center">
  <img
    src={profileImage || "/default-avatar.png"}
    alt="avatar"
    className="rounded-circle me-2"
    width="30"
    height="30"
  />
  <span>{user?.fullname}</span>
  <span className="ms-1">&#x25BC;</span>
</span>

{dropdownOpen && (
  <div 
    className="dropdown-menu dropdown-menu-end show" 
    style={{ 
      right: "0", 
      left: "auto", 
      maxWidth: "calc(100vw - 30px)",
      position: "absolute"
    }}
  >
    <button className="dropdown-item" onClick={handleLogout}>
      Log out
    </button>
    <button
      className="dropdown-item text-danger"
      onClick={handleDeleteAccount}
    >
      Delete Account
    </button>
  </div>
)}
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
