import React from "react";
import artsyLogo from "../assets/artsy_logo.svg"; 

const Footer: React.FC = () => {
  return (
    <footer className="bg-dark text-white text-center py-3 mt-auto">
      <a
        href="https://www.artsy.net"
        target="_blank"
        rel="noopener noreferrer"
        className="text-white text-decoration-none d-inline-flex align-items-center"
      >
        <img
          src={artsyLogo}
          alt="Artsy"
          height="20"
          className="me-2"
        />
        Powered by Artsy
      </a>
    </footer>
  );
};

export default Footer;
