import React, { useState } from "react";
import { Card } from "react-bootstrap";
import { FaStar } from "react-icons/fa";
import { useAuth } from "../context/authContext";

type Props = {
  id: string;
  name: string;
  imageUrl: string;
  isFavorited: boolean;
  onClick: () => void;
  onToggleFavorite: () => void;
  selected: boolean;
};

const ArtistCard: React.FC<Props> = ({
  name,
  imageUrl,
  isFavorited,
  onClick,
  onToggleFavorite,
  selected,
}) => {
  const { isAuthenticated } = useAuth();
  const [isHovered, setIsHovered] = useState(false);

  const getNameBarColor = () => {
    if (selected || isHovered) return "#1D6FC7"; 
    return "#205375";
  };

  return (
    <Card
      style={{
        minWidth: "250px",
        cursor: "pointer",
        position: "relative",
        borderRadius: "0",
        overflow: "hidden",
        transition: "background-color 0.2s ease",
        padding: "0",
        border: "none",
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={onClick}
      className="mx-1 shadow-sm"
    >
      {isAuthenticated && (
        <div
          style={{
            position: "absolute",
            top: "12px",
            right: "12px",
            zIndex: 2,
            width: "32px",
            height: "32px",
            borderRadius: "50%",
            backgroundColor: "#205375", 
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            cursor: "pointer",
            transition: "background-color 0.2s ease",
          }}
          onClick={(e) => {
            e.stopPropagation();
            onToggleFavorite();
          }}
        >
          <FaStar
            color={isFavorited ? "gold" : "white"} 
            size={16}
          />
        </div>
      )}
      <div style={{ position: "relative" }}>
        <Card.Img
          src={imageUrl}
          alt={name}
          style={{
            height: "300px",
            objectFit: "cover",
            borderRadius: "0",
          }}
        />
        <div
          style={{
            position: "absolute",
            bottom: 0,
            width: "100%",
            backgroundColor: getNameBarColor(), 
            color: "white",
            padding: "6px 2px",
            textAlign: "center",
            fontWeight: "bold",
            fontSize: "14px",
            borderRadius: "0",
            transition: "background-color 0.2s ease",
          }}
        >
          {name}
        </div>
      </div>
    </Card>
  );
};

export default ArtistCard;